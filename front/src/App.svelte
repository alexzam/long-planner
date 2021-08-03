<script lang="ts">
    function startAddPlan() {
        console.info("Start add plan")
    }
</script>

<div class="ui container">
    <h2>Plans</h2>
    <button class="ui primary button" on:click={startAddPlan}>
        Add
    </button>
</div>