<script lang="ts">
    import VarSelector from "../util/VarSelector.svelte";
    import type {TimePoint, Var} from "../../../generated/model";
    import backend from "../../backend";

    export let vars: Array<Var> = [];
    export let varId: number;
    export let value: string;
    export let timepointId: number;
    export let thisTimepoint: TimePoint;
    export let prevTimepoint: TimePoint;

    let status = 2;

    $: value = offerFor(varId);
    $: saveValue(timepointId, varId, value);

    function offerFor(varId: number): string {
        let v = thisTimepoint.presetValues[varId];
        if (v == null) v = thisTimepoint.values[varId];
        if (v == null) v = prevTimepoint?.values[varId];
        if (v == null) v = prevTimepoint?.presetValues[varId];
        if (v == null) v = vars.find((vr) => {
            vr.id == varId
        })?.initialValue;
        if (v == null) v = "0";
        return v;
    }

    function saveValue(timepointId: number, varId: number, value: string) {
        status = 2;
        if (varId == null) return;
        status = 1;

        backend.timepoints.savePresetValue(timepointId, varId, value)
            .then(() => status = 0);
    }
</script>

<div class="ui segment small form" class:red={status===2} class:yellow={status===1}>
    <VarSelector bind:valueId={varId} {vars}/>
    <i class="arrow left icon"></i>
    <span class="inline field">
        <input bind:value={value} type="number"/>
    </span>
</div>
