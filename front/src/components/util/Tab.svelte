<script lang="ts">
    import {getContext} from "svelte";

    export let title: string;

    let register = getContext("tabsReg");
    let id = register(title);
</script>

<div class="ui tab" data-tab={id}>
    <slot></slot>
</div>
