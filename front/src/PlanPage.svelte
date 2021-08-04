<script lang="ts">
    import {Plan} from "../generated/model";
    import EditableText from "./components/EditableText.svelte";
    import {plans as plansApi} from "./backend";

    export let planId: number;

    let plan: Plan = null;
    $: loadPlan(planId);

    function loadPlan(id: number) {
        plansApi.createPlan()
            .then(p => {
                plan = p;
            });
    }
</script>

{#if plan != null}
    <h1>
        <EditableText bind:text={plan.name}/>
    </h1>
{/if}
Plan {planId} selected