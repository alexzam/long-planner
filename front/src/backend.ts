import type {Plan, ShortPlan, Var} from "../generated/model";
import model from "./model";
import type {Entity} from "@alexzam/entityvc";

const backHost = "";

function parseEntity<T extends Entity>(resp: Response, entityName: string): Promise<T> {
    return resp.json().then(record => model.fromBackendEntity(record, entityName)) as Promise<T>;
}

function parseEntities<T extends Entity>(resp: Response, entityName: string): Promise<Array<T>> {
    return resp.json().then(records => records.map(record => model.fromBackendEntity(record, entityName)));
}

const plans = {
    createPlan(): Promise<Plan> {
        return fetch(backHost + "/api/plans", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(resp => parseEntity(resp, "Plan"));
    },

    update(plan: Plan): Promise<Plan> {
        return fetch(backHost + "/api/plans/" + plan._id, {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(model.toBackendEntity(plan))
        })
            .then(resp => parseEntity(resp, "Plan"));
    },

    getPlans(): Promise<Array<ShortPlan>> {
        return fetch(backHost + "/api/plans")
            .then(resp => parseEntities(resp, "ShortPlan"));
    },
    getPlan(id: number): Promise<Plan> {
        return fetch(backHost + "/api/plans/" + id)
            .then(resp => parseEntity(resp, "Plan"));
    },
    addVariable(planId: number): Promise<Var> {
        return fetch(backHost + "api/plans/" + planId + "/vars", {method: "POST"})
            .then(resp => parseEntity(resp, "Var"));
    },
    editVariable(planId: number, vvar: Var): Promise<Plan> {
        return fetch(backHost + "/api/plans/" + planId + "/vars/" + vvar.id, {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(vvar)
        })
            .then(resp => parseEntity(resp, "Plan"));
    }
}

const api = {
    plans
}

export default api;