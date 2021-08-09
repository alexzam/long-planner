<script lang="ts">
    import {Plan} from "../generated/model";
    import EditableText from "./components/EditableText.svelte";
    import backend from "./backend";

    export let planId: number;

    let plan: Plan = null;

    $: loadPlan(planId);
    $: planUpdateName(plan?.name)

    function loadPlan(id: number) {
        if (id == 0) {
            backend.plans.createPlan()
                .then(p => {
                    plan = p;
                    planId = p._id;
                });
        } else if (plan == null || plan._id != id) {
            backend.plans.getPlan(id)
                .then(p => plan = p);
        }
    }

    function planUpdateName(name: string | null) {
        if (name == null) return
        backend.plans.updateName(planId, plan.name)
    }

    function goOut() {
        planId = null;
    }
</script>

{#if plan != null}
    <div class="ui breadcrumb">
        <a class="section" on:click|preventDefault={goOut}>Home</a>
        <div class="divider"> /</div>
        <div class="active section">{plan.name}</div>
    </div>

    <h1>
        <EditableText bind:text={plan.name}/>
    </h1>
{/if}
Plan {planId} selected