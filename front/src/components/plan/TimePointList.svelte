<script lang="ts">
    import backend from "../../backend";
    import type {TimePointShort, TimepointStatItem} from "../../../generated/model";
    import Datepicker from "../util/Datepicker.svelte";
    import type {Moment} from "moment";
    import moment from "moment";

    export let planId: number;

    type ListItem = TimepointStatItem | TimePointShort;
    let timepoints: Array<ListItem> = [];
    let newPointDate: Moment = moment();

    $: backend.plans.getTimepointsStats(planId).then((items) => timepoints = items);

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
</script>

<div class="ui segments">
    {#each timepoints as point}
        <div>
            {#if point._entityType === 'TimePointShort'}
                {point.date.format()}
            {:else}
                {point.num}
            {/if}
        </div>
    {/each}
</div>

<Datepicker bind:date={newPointDate}/>
<button class="ui primary button" on:click={addTimepoint}>
    <i class="plus icon"></i> Add preset point
</button>