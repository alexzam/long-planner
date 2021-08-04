<script lang="ts">
    export let text: string;
    export let editing: Boolean = false;

    let editText: string = text;

    function startEditing() {
        editing = true;
    }

    function clickOk() {
        text = editText;
        editing = false;
    }

    function clickCancel() {
        editText = text;
        editing = false;
    }
</script>

{#if !editing}
    <span on:click={startEditing} style="cursor: pointer">{text}</span>
{:else}
    <div class="ui action input">
        <input type="text" bind:value={editText}/>
        <button class="ui icon button" on:click={clickCancel}>
            <i class="cross icon"></i>
        </button>
        <button class="ui primary icon button" on:click={clickOk}>
            <i class="check icon"></i>
        </button>
    </div>
{/if}