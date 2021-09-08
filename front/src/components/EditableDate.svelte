<script lang="ts">
    import type {Moment} from "moment";
    import Datepicker from "./Datepicker.svelte";

    export let date: Moment;
    export let editing: Boolean = false;

    let editDate: Moment = date;

    function startEditing() {
        editing = true;
    }

    function clickOk() {
        date = editDate;
        editing = false;
    }

    function clickCancel() {
        editDate = date;
        editing = false;
    }
</script>

{#if !editing}
    <span on:click={startEditing} style="cursor: pointer">{date.format('LL')}</span>
{:else}
    <div class="ui action input">
        <Datepicker bind:date={editDate}/>
        <button class="ui icon button" on:click={clickCancel}>
            <i class="times icon"></i>
        </button>
        <button class="ui primary icon button" on:click={clickOk}>
            <i class="check icon"></i>
        </button>
    </div>
{/if}