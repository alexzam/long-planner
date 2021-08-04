<script lang="ts">
    export let currentPlan: number | null;

    function startAddPlan() {
        currentPlan = 0
    }
</script>

<h2>Plans</h2>
<button class="ui primary button" on:click={startAddPlan}>
    Add
</button>