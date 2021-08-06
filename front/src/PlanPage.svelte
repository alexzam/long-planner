<script lang="ts">
    import {Plan} from "../generated/model";
    import EditableText from "./components/EditableText.svelte";
    import backend from "./backend";

    export let planId: number;

    let plan: Plan = null;
    $: loadPlan(planId);

    function loadPlan(id: number) {
        if (id == 0) {
            backend.plans.createPlan()
                .then(p => {
                    plan = p;
                    planId = p._id;
                });
        } else if (plan == null || plan._id != planId) {
            // load plan
        }
    }
</script>

{#if plan != null}
    <h1>
        <EditableText bind:text={plan.name}/>
    </h1>
{/if}
Plan {planId} selected