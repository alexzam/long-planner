<!--suppress TypeScriptUnresolvedFunction -->
<script lang="ts">
    import type {Var} from "../../../generated/model";
    import {onMount} from 'svelte';
    import "fomantic-ui/dist/components/dropdown";
    import "fomantic-ui/dist/components/dropdown.css";
    import jQuery from "jquery";

    export let vars: Array<Var> = [];
    export let value: Var = null;
    export let valueId: number = 0;

    let el;

    onMount(() => {
        jQuery(el).dropdown({
            onChange: (newId) => {
                if (newId == null) {
                    value = null;
                    valueId = 0;
                } else {
                    const found = vars.find((varr) => varr.id == newId);
                    if (found == undefined) {
                        value = null;
                        valueId = 0;
                    } else {
                        value = found;
                        valueId = newId;
                    }
                }
            }
        });

        if (value != null) {
            jQuery(el).dropdown('set selected', value.id, null, true);
        } else if (valueId != 0) {
            jQuery(el).dropdown('set selected', valueId, null, true);
        }
    });
</script>

<div bind:this={el} class="ui small selection dropdown">
    <div class="text">Text?</div>
    <i class="dropdown icon"></i>
    <div class="menu">
        {#each vars as varr}
            <div class="item" data-value={varr.id}>{varr.name}</div>
        {/each}
    </div>
</div>
