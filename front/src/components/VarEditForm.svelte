<script lang="ts">
    import type {Var} from "../../generated/model";
    import {createEventDispatcher} from 'svelte';

    export let vvar: Var = null;
    let editEntity: Var = Object.assign({}, vvar);

    let dispatch = createEventDispatcher();

    function onOk() {
        dispatch("done", editEntity);
    }

    function onClear() {
        editEntity = Object.assign({}, vvar);
    }
</script>

{#if editEntity}
    <form class="ui form">
        <div class="field">
            <label>Name</label>
            <input type="text" bind:value={editEntity.name}/>
        </div>
        <div class="field">
            <label>Initial value</label>
            <input type="text" bind:value={editEntity.initialValue}/>
        </div>
        <div class="field">
            <label>Expression</label>
            <input type="text" bind:value={editEntity.expression}/>
        </div>
        <button class="ui small button" on:click|preventDefault={onClear}>Reset</button>
        <button class="ui small secondary button" on:click|preventDefault|stopPropagation={onOk}>OK</button>
    </form>
{/if}