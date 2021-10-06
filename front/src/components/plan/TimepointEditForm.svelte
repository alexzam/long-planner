<script lang="ts">
    import type {TimePoint, TimePointShort, Var} from "../../../generated/model";
    import backend from "../../backend";
    import TimepointValueEditor from "./TimepointValueEditor.svelte";

    export let point: TimePointShort;
    export let vars: Array<Var>;

    let pointFull: TimePoint;
    let prevTimepoint: TimePoint;

    type ValPair = { key: number, value: string };
    let valPairs: Array<ValPair> = [];
    $: fetchTimepoint(point?.id);

    function fetchTimepoint(id: number) {
        if (id == null) return;

        backend.timepoints.getWithPrev(id).then(ret => {
            pointFull = ret.cur;
            prevTimepoint = ret.prev;
            valPairs = [];
            for (let key in ret.cur.presetValues) {
                // noinspection TypeScriptValidateTypes
                valPairs.push({key: key, value: ret.cur.presetValues[key]});
            }
        });
    }

    function addValue() {
        valPairs.push({key: null, value: "0"});
        valPairs = valPairs;
    }
</script>

{#if pointFull != null}
    <div class="ui raised segments">
        {#each valPairs as valueRec}
            <TimepointValueEditor
                    {vars}
                    bind:varId={valueRec.key}
                    bind:value={valueRec.value}
                    timepointId={point?.id}
                    thisTimepoint={pointFull}
                    {prevTimepoint}
            />
        {:else}
            <div class="ui segment">
                <em>No values</em>
            </div>
        {/each}
    </div>

    <button class="ui small button secondary" on:click={addValue}>
        <i class="plus icon"></i>
        Add value
    </button>
{/if}
