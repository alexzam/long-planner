<script lang="ts">
    import type {ShortPlan} from "../generated/model";
    import {onMount} from "svelte";
    import backend from "./backend";

    export let currentPlan: number | null;

    let plans: Array<ShortPlan> = []

    onMount(() => {
        backend.plans.getPlans()
            .then(pls => plans = pls);
    });

    function startAddPlan() {
        currentPlan = 0
    }

    function gotoPlan(id: number) {
        currentPlan = id
    }
</script>

<h2>Plans</h2>
<button class="ui primary button" on:click={startAddPlan}>
    Add
</button>
<div class="ui segments">
    {#each plans as plan}
        <div class="ui segment plan" on:click={() => gotoPlan(plan._id)}>
            {plan.name}
        </div>
    {/each}
</div>