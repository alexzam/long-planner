<!--suppress TypeScriptUnresolvedFunction -->
<script lang="ts">
    import jQuery from "jquery";
    import {onMount} from "svelte";
    import "fomantic-ui/dist/components/transition";
    import "fomantic-ui/dist/components/popup";
    import "fomantic-ui/dist/components/calendar";
    import type {Moment} from "moment";
    import moment from "moment";

    export let date: Moment;

    let el;

    onMount(() => {
        jQuery(el).calendar({
            type: 'date',
            firstDayOfWeek: 1,
            onChange: (newDate) => {
                date = moment(newDate);
                jQuery(el).calendar('blur');
            }
        })
            .calendar('set date', date.toDate(), true, false);
    });

    export function get() {
        let v = jQuery(el).calendar('get date');
    }
</script>

<div bind:this={el} class="ui calendar">
    <div class="ui input left icon">
        <i class="calendar icon"></i>
        <input placeholder="Start date" type="text">
    </div>
</div>