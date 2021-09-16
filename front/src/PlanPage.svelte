<script lang="ts">
    import {tick} from 'svelte';
    import type {Plan, Var} from "../generated/model";
    import EditableText from "./components/EditableText.svelte";
    import backend from "./backend";
    import VarEditForm from "./components/VarEditForm.svelte";
    import RenderedExpression from "./components/RenderedExpression.svelte";
    import EditableDate from "./components/EditableDate.svelte";
    import {assign} from "svelte/internal";

    export let planId: number;

    let plan: Plan = null;
    let editingVar: number = null;
    let freezeUpdates: boolean = true;

    $: loadPlan(planId);
    $: planUpdateName(plan?.name)
    $: planUpdate(plan?.start, plan?.end)

    function loadPlan(id: number) {
        if (id == 0) {
            backend.plans.createPlan()
                .then(p => {
                    plan = p;
                    planId = p._id;
                    return tick();
                })
                .then(() => {
                    freezeUpdates = false;
                })
            ;
        } else if (plan == null || plan._id != id) {
            backend.plans.getPlan(id)
                .then(p => {
                    plan = p;
                    return tick();
                })
                .then(() => {
                    freezeUpdates = false;
                });
        }
    }

    function planUpdateName(name: string | null) {
        if (freezeUpdates || name == null) return
        backend.plans.updateName(planId, plan.name)
    }

    function planUpdate(..._) {
        if (freezeUpdates) return

        let planToSend = assign({}, plan);
        planToSend.vars = [];

        backend.plans.update(planToSend)
            .then((p) => {
                freezeUpdates = true;
                plan = p;
                return tick();
            })
            .then(() => {
                freezeUpdates = false;
            })
    }

    function goOut() {
        planId = null;
    }

    function addVariable() {
        backend.plans.addVariable(planId)
            .then(v => {
                plan.vars.push(v);
                plan = plan;
            });
    }

    function openVar(id: number) {
        editingVar = id;
    }

    function editVar(event: CustomEvent<Var>) {
        editingVar = null;
        backend.plans.editVariable(planId, event.detail)
            .then(p => plan = p);
    }
</script>

{#if plan != null}
    <div class="ui breadcrumb">
        <a class="section" on:click|preventDefault={goOut}>Home</a>
        <div class="divider">/</div>
        <div class="active section">{plan.name}</div>
    </div>

    <h1>
        <EditableText bind:text={plan.name}/>
    </h1>

    <strong>Start:</strong>
    <EditableDate bind:date={plan.start}/>

    <div class="ui segments">
        {#each plan.vars as vvar}
            <div class="ui segment clickable" class:clickable={vvar.id !== editingVar}
                 on:click={() => openVar(vvar.id)}>
                {#if vvar.id !== editingVar}
                    <div class="ui tiny label">{vvar.id}</div>
                    {vvar.name}
                    <RenderedExpression expression={vvar.expression} vars={plan.vars}/>
                {:else}
                    <VarEditForm {vvar} vars={plan.vars} on:done={editVar}/>
                {/if}
            </div>
        {/each}
    </div>
    <button class="ui primary button" on:click={addVariable}>
        <i class="plus icon"></i> Add variable
    </button>
{:else}
    Loading...
{/if}