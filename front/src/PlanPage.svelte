<script lang="ts">
    import {tick} from 'svelte';
    import {fly} from 'svelte/transition';
    import type {Plan} from "../generated/model";
    import EditableText from "./components/util/EditableText.svelte";
    import backend from "./backend";
    import EditableDate from "./components/util/EditableDate.svelte";
    import {assign} from "svelte/internal";
    import Toggle from "./components/util/Toggle.svelte";
    import type {Moment} from "moment";
    import moment from "moment";
    import VariableList from "./components/plan/VariableList.svelte";
    import TimePointList from "./components/plan/TimePointList.svelte";

    export let planId: number;

    type PlanInfo = {
        name: string;
        start: Moment;
        end?: Moment;
    };

    let plan: Plan = null;
    let planInfo: PlanInfo = {name: "", start: moment(), end: null};
    let freezeUpdates: boolean = true;

    let planHasNoEnd: Boolean = true;
    let pointList: TimePointList;

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

            pointList.refresh();

            return tick();
        })
            .then(() => {
                freezeUpdates = false;
            })
    }

    function goOut() {
        planId = null;
    }

    function calculate() {
        setPlan(backend.plans.calculate(planId));
    }
</script>

<style>
    .ui.grid {
        margin-top: 0;
    }
</style>

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
        <div transition:fly|local={{y: -10}}>
            <strong>End:</strong>
            <EditableDate bind:date={planInfo.end}/>
        </div>
    {/if}

    <div class="ui two column stackable grid">
        <div class="column">
            <h2>Variables</h2>

            <VariableList {planId} vars={plan.vars}/>
        </div>

        <div class="column">
            <h2>Time points</h2>

            <TimePointList bind:this={pointList} {planId} vars={plan.vars}/>
        </div>
    </div>

    <div class="ui centered grid">
        <div class="ui ten wide column">
            <button class="ui fluid primary huge button" on:click={calculate}>
                Calculate
            </button>
        </div>
    </div>
{:else}
    Loading...
{/if}