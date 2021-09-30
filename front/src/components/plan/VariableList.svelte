<script lang="ts">
    import type {Var} from "../../../generated/model";
    import RenderedExpression from "../util/RenderedExpression.svelte";
    import VarEditForm from "./VarEditForm.svelte";
    import backend from "../../backend";

    export let planId: number;
    export let vars: Array<Var>;

    let editingVar: number = null;

    function openVar(id: number) {
        editingVar = id;
    }

    function editVar(event: CustomEvent<Var>) {
        editingVar = null;
        backend.plans.editVariable(planId, event.detail)
            .then(p => vars = p.vars);
    }

    function addVariable() {
        backend.plans.addVariable(planId)
            .then(v => {
                vars.push(v);
                vars = vars;
            });
    }
</script>

<div class="ui segments">
    {#each vars as vvar}
        <div class="ui segment" class:clickable={vvar.id !== editingVar}
             on:click={() => openVar(vvar.id)}>
            {#if vvar.id !== editingVar}
                <div class="ui tiny label">{vvar.id}</div>
                {vvar.name}
                <RenderedExpression expression={vvar.expression} {vars}/>
            {:else}
                <VarEditForm {vvar} {vars} on:done={editVar}/>
            {/if}
        </div>
    {/each}
</div>

<button class="ui primary button" on:click={addVariable}>
    <i class="plus icon"></i> Add variable
</button>