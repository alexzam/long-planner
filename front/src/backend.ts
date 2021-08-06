import type {Plan} from "../generated/model";

const backHost = "";

const plans = {
    createPlan: function (): Promise<Plan> {
        return fetch(backHost + "/api/plans", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(resp => resp.json());
    }
}

const api = {
    plans
}

export default api;