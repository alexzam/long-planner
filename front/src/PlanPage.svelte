<script lang="ts">
    import {tick} from 'svelte';
    import {fly} from 'svelte/transition';
    import type {Plan, Var} from "../generated/model";
    import EditableText from "./components/EditableText.svelte";
    import backend from "./backend";
    import VarEditForm from "./components/VarEditForm.svelte";
    import RenderedExpression from "./components/RenderedExpression.svelte";
    import EditableDate from "./components/EditableDate.svelte";
    import {assign} from "svelte/internal";
    import Toggle from "./components/Toggle.svelte";
    import type {Moment} from "moment";
    import moment from "moment";

    export let planId: number;

    type PlanInfo = {
        name: string;
        start: Moment;
        end?: Moment;
    };

    let plan: Plan = null;
    let planInfo: PlanInfo = {name: "", start: moment(), end: null};
    let editingVar: number = null;
    let freezeUpdates: boolean = true;

    let planHasNoEnd: Boolean = true;

    $: loadPlan(planId);
    $: planUpdate(planInfo)
    $: updateNoEnd(planHasNoEnd)

    function loadPlan(id: number) {
        if (id == 0) {
            setPlan(backend.plans.createPlan());
        } else if (plan == null || plan._id != id) {
            setPlan(backend.plans.getPlan(id));
        }
    }

    function planUpdate(planInfo: PlanInfo) {
        if (freezeUpdates) return

        let planToSend = assign(assign({}, plan), planInfo);
        planToSend.vars = [];

        setPlan(backend.plans.update(planToSend));
    }

    function updateNoEnd(noEnd: boolean) {
        if (freezeUpdates) return

        if (noEnd) {
            planInfo.end = null;
            planUpdate(planInfo);
        } else planInfo.end = planInfo.start;
    }

    function setPlan(newPlan: Promise<Plan>) {
        newPlan.then((p) => {
            freezeUpdates = true;
            planId = p._id;
            plan = p;
            planHasNoEnd = (plan.end == null);

            planInfo.name = plan.name;
            planInfo.start = plan.start;
            planInfo.end = plan.end;

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
        <div class="active section">{planInfo.name}</div>
    </div>

    <h1>
        <EditableText bind:text={planInfo.name}/>
    </h1>

    <strong>Start:</strong>
    <EditableDate bind:date={planInfo.start}/>

    <div>
        <Toggle bind:value={planHasNoEnd} label="Use last timepoint as end date"/>
    </div>

    {#if !planHasNoEnd}
        <div transition:fly|local="{{y: -10}}">
            <strong>End:</strong>
            <EditableDate bind:date={planInfo.end}/>
        </div>
    {/if}

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