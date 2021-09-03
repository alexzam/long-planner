<script lang="ts">
    import type {Var} from "../../generated/model";

    export let expression: string;
    export let vars: Array<Var>;

    type Part = {
        type: "T" | "V"
        text: string
        isPrev?: boolean
    }

    let parts: Array<Part>;
    $: parts = process(expression);

    function process(exp: string): Array<Part> {
        let ret: Array<Part> = [];
        let currentPart: Part = null;

        for (let i in exp) {
            const c = exp[i];

            if (currentPart == null) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    currentPart = {type: "V", text: c, isPrev: false}
                } else if (c == '#') {
                    currentPart = {type: "V", text: c, isPrev: true}
                } else if (c != ' ') {
                    currentPart = {type: "T", text: c}
                }
            } else {
                if (c != ' ') {
                    currentPart.text += c;
                } else {
                    ret.push(finalizePart(currentPart));
                    currentPart = null;
                }
            }
        }
        if (currentPart != null) ret.push(finalizePart(currentPart));

        return ret;
    }

    function finalizePart(part: Part): Part {
        if (part.isPrev === true && part.text.startsWith("#prev.")) part.text = part.text.substr(6);
        if (part.type === "V") {
            let id = part.text.substr(2);
            let vvar = vars.find((v) => v.id == id);
            if (vvar === undefined) {
                part.type = "T";
            } else {
                part.text = vvar.name;
            }
        }

        return part;
    }
</script>

<style>
    .expression {
        font-family: "Fira Code", monospace;
    }
</style>

{#each parts as part}
    {#if part.type === "T"}
        <span class="expression">{part.text}</span>
    {:else}
        <div class="ui tiny horizontal label">
            {#if part.isPrev}
                <i class="icon caret square left outline"></i>
            {/if}
            {part.text}
        </div>
    {/if}
{/each}
