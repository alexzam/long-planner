import type {Plan} from "../generated/model";

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
    }
}

const api = {
    plans
}

export default api;