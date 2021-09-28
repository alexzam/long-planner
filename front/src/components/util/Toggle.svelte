<script lang="ts">
    import jQuery from "jquery";
    import "fomantic-ui/dist/components/checkbox";
    import {onMount} from 'svelte';

    export let label: string = null;
    export let value: boolean;
    let el;

    onMount(() => {
        jQuery(el).checkbox();
    });
</script>

<div bind:this={el} class="ui toggle checkbox">
    <input bind:checked={value} class="hidden" tabindex="0" type="checkbox">
    {#if label != null}
        <label>{label}</label>
    {/if}
</div>
