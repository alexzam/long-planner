<script lang="ts">
    import type {TimePoint, TimePointShort, Var} from "../../../generated/model";
    import backend from "../../backend";
    import VarSelector from "../util/VarSelector.svelte";

    export let point: TimePointShort;
    export let vars: Array<Var>;

    let pointFull: TimePoint;

    type ValPair = { key: number, value: string };
    let valPairs: Array<ValPair> = [];
    $: fetchTimepoint(point?.id);

    function fetchTimepoint(id: number) {
        if (id == null) return;

        backend.timepoints.get(id).then(tp => {
            pointFull = tp;
            valPairs = [];
            for (let key in tp.presetValues) {
                // noinspection TypeScriptValidateTypes
                valPairs.push({key: key, value: tp.presetValues[key]});
            }
        });
    }
</script>

{#if pointFull != null}
    <div class="ui raised segments">
        {#each valPairs as valueRec}
            <div class="ui segment small form">
                <VarSelector {vars} bind:valueId={valueRec.key}/>
                <i class="arrow left icon"></i>
                <span class="inline field">
                    <input type="number" bind:value={valueRec.value}/>
                </span>
            </div>
        {:else}
            <div class="ui segment">
                <em>No values</em>
            </div>
        {/each}
    </div>

    <button class="ui small button secondary">
        <i class="plus icon"></i>
        Add value
    </button>
{/if}
