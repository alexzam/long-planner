<!--suppress TypeScriptUnresolvedFunction -->
<script lang="ts">
    import {onMount, setContext} from "svelte";
    import jQuery from "jquery";
    import "fomantic-ui/dist/components/tab";

    // Usage: <Tabs><Tab title="A">A content</Tab></Tabs>

    let titles = [];
    let lastId = 0;
    let menuEl;

    setContext("tabsReg", register);

    function register(title: string): string {
        let id = "t" + lastId++;
        titles.push([id, title]);
        titles = titles;
        return id;
    }

    onMount(() => {
        jQuery(menuEl).children(".item").tab();
    });
</script>

<div bind:this={menuEl} class="ui tabular menu">
    {#each titles as pair}
        <div class="item" data-tab={pair[0]}>{pair[1]}</div>
    {/each}
</div>

<slot></slot>