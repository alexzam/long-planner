<script lang="ts">
    import backend from "../../backend";
    import type {TimePointPage, TimePointShort, TimepointStatItem, Var} from "../../../generated/model";
    import Datepicker from "../util/Datepicker.svelte";
    import type {Moment} from "moment";
    import moment from "moment";
    import TimepointEditForm from "./TimepointEditForm.svelte";

    export let planId: number;
    export let vars: Array<Var>;

    type ListItem = TimepointStatItem | TimePointShort;
    let timepoints: Array<ListItem> = [];
    let newPointDate: Moment = moment();
    let editingPoint: number = null;

    $: refresh(planId);

    function sortPoints() {
        timepoints.sort((a, b) => {
            const dA = (a._entityType == 'TimePointShort') ? a.date : a.minDate;
            const dB = (b._entityType == 'TimePointShort') ? b.date : b.minDate;
            return dA.diff(dB, 'days');
        });
    }

    function addTimepoint() {
        backend.plans.addTimepoint(planId, newPointDate).then((ntp) => {
            const oldPoint = timepoints.find((tp) => (tp._entityType == 'TimePointShort' && tp.id == ntp.id));
            if (oldPoint === undefined) timepoints.push(ntp);
            sortPoints();
            timepoints = timepoints;
        });
    }

    export function refresh(id: number = 0) {
        backend.plans.getTimepointsStats(planId).then((items) => timepoints = items);
    }

    function loadPoints(pointPack: TimepointStatItem) {
        backend.timepoints.getPage(planId, pointPack.minDate, pointPack.maxDate, 10).then((points: TimePointPage) => {
            pointPack.num -= points.items.length;
            pointPack.minDate = points.next;
            if (pointPack.num <= 0) timepoints.splice(timepoints.indexOf(pointPack), 1)

            timepoints.push(...points.items);
            sortPoints();
            timepoints = timepoints;
        });
    }
</script>

<div class="ui segments">
    {#each timepoints as point}
        {#if point._entityType === 'TimePointShort'}
            {#if point.id === editingPoint}
                <div class="ui segment">
                    <TimepointEditForm {point} {vars}/>
                </div>
            {:else}
                <div class="ui segment clickable" on:click={() => editingPoint = point.id}>
                    <div class="ui tiny label">{point.date.format("DD MMM YYYY")}</div>
                </div>
            {/if}
        {:else}
            <div class="ui center aligned segment clickable" on:click={() => loadPoints(point)}>
                <strong>
                    {point.num} points from {point.minDate.format("DD MMM YYYY")}
                    to {point.maxDate.format("DD MMM YYYY")}
                </strong>
            </div>
        {/if}
    {:else}
        <div class="ui segment">
            <em>No timepoints</em>
        </div>
    {/each}
</div>

<Datepicker bind:date={newPointDate}/>
<button class="ui primary button" on:click={addTimepoint}>
    <i class="plus icon"></i> Add preset point
</button>