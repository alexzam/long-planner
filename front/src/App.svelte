<script lang="ts">
    import PlansPage from "./PlansPage.svelte";
    import PlanPage from "./PlanPage.svelte";

    let currentPlan: number | null = null;

</script>

<div class="ui container">
    {#if currentPlan === null}
        <PlansPage bind:currentPlan/>
    {:else }
        <PlanPage bind:planId={currentPlan}/>
    {/if}
</div>