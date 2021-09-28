<script lang="ts">
    import backend from "../../backend";

    export let planId: number;

    $: timepoints = backend.plans.getTimepointsStats(planId);
</script>

<div class="ui segments">
    {#await timepoints then timepointList}
        {#each timepointList as point}
            <div>{point.num}</div>
        {/each}
    {/await}
</div>