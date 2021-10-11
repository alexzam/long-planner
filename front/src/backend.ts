import type {
    Plan,
    ShortPlan,
    TimePoint,
    TimePointShort,
    TimepointStatItem,
    TimepointWithPrev,
    Var
} from "../generated/model";
import model from "./model";
import type {Entity} from "@alexzam/entityvc";
import type {Moment} from "moment";

const backHost = "";

function parseEntity<T extends Entity>(resp: Response, entityName: string): Promise<T> {
    return resp.json().then(record => model.fromBackendEntity(record, entityName)) as Promise<T>;
}

function parseEntities<T extends Entity>(resp: Response, entityName: string): Promise<Array<T>> {
    return resp.json().then(records => records.map(record => model.fromBackendEntity(record, entityName)));
}

const url = {
    plans: backHost + "/api/plans",
    plan(id: number): string {
        return url.plans + "/" + id
    }
}

const plans = {
    createPlan(): Promise<Plan> {
        return fetch(url.plans, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(resp => parseEntity(resp, "Plan"));
    },

    update(plan: Plan): Promise<Plan> {
        return fetch(url.plan(plan._id), {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(model.toBackendEntity(plan))
        })
            .then(resp => parseEntity(resp, "Plan"));
    },

    getPlans(): Promise<Array<ShortPlan>> {
        return fetch(url.plans)
            .then(resp => parseEntities(resp, "ShortPlan"));
    },
    getPlan(id: number): Promise<Plan> {
        return fetch(url.plan(id))
            .then(resp => parseEntity(resp, "Plan"));
    },
    addVariable(planId: number): Promise<Var> {
        return fetch(url.plan(planId) + "/vars", {method: "POST"})
            .then(resp => parseEntity(resp, "Var"));
    },
    editVariable(planId: number, vvar: Var): Promise<Plan> {
        return fetch(url.plan(planId) + "/vars/" + vvar.id, {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(vvar)
        })
            .then(resp => parseEntity(resp, "Plan"));
    },
    getTimepointsStats(planId: number): Promise<Array<TimepointStatItem>> {
        return fetch(url.plan(planId) + "/timepoints")
            .then(resp => parseEntities(resp, "TimepointStatItem"));
    },
    addTimepoint(planId: number, date: Moment): Promise<TimePointShort> {
        return fetch(url.plan(planId) + "/timepoints?date=" + date.format("YYYY-MM-DD"),
            {method: "POST"})
            .then(resp => parseEntity(resp, "TimePointShort"));
    },
    calculate(planId: number): Promise<Plan> {
        return fetch(url.plan(planId) + "/_calculate", {method: 'POST'})
            .then(resp => parseEntity(resp, "Plan"));
    }
}

const timepoints = {
    get(id: number): Promise<TimePoint> {
        return fetch(backHost + "/api/timepoints/" + id)
            .then(resp => parseEntity(resp, "TimePoint"));
    },

    getWithPrev(id: number): Promise<TimepointWithPrev> {
        return fetch(backHost + "/api/timepoints/" + id + "?withPrev=true")
            .then(resp => parseEntity(resp, "TimepointWithPrev"));
    },

    savePresetValue(timepointId: number, varId: number, value: string): Promise<any> {
        return fetch(backHost + "/api/timepoints/" + timepointId + "/values/" + varId + "?value=" + value,
            {method: 'POST'});
    }
}

const api = {
    plans,
    timepoints
}

export default api;