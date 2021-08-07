import type {Plan, ShortPlan} from "../generated/model";

const backHost = "";

const plans = {
    createPlan(): Promise<Plan> {
        return fetch(backHost + "/api/plans", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(resp => resp.json());
    },

    updateName(planId: number, name: string) {
        fetch(backHost + "/api/plans/" + planId + "/_updateName?name=" + name, {method: "POST"})
    },

    getPlans(): Promise<Array<ShortPlan>> {
        return fetch(backHost + "/api/plans")
            .then(resp => resp.json());
    },
    getPlan(id: number): Promise<Plan> {
        return fetch(backHost + "/api/plans/" + id)
            .then(resp => resp.json());
    }
}

const api = {
    plans
}

export default api;