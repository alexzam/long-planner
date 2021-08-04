import type {Plan} from "../generated/model";

export const plans = {
    createPlan: function (): Promise<Plan> {
        return new Promise<Plan>((resolve) => {
            resolve({
                id: id,
                name: "aaa",
                _entityName: "aaa"
            })
        });
    }
}