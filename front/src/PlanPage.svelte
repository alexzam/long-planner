<script lang="ts">
    import type {Plan, Var} from "../generated/model";
    import EditableText from "./components/EditableText.svelte";
    import backend from "./backend";
    import VarEditForm from "./components/VarEditForm.svelte";

    export let planId: number;

    let plan: Plan = null;
    let editingVar: number = null;

    $: loadPlan(planId);
    $: planUpdateName(plan?.name)

    function loadPlan(id: number) {
        if (id == 0) {
            backend.plans.createPlan()
                .then(p => {
                    plan = p;
                    planId = p._id;
                });
        } else if (plan == null || plan._id != id) {
            backend.plans.getPlan(id)
                .then(p => plan = p);
        }
    }

    function planUpdateName(name: string | null) {
        if (name == null) return
        backend.plans.updateName(planId, plan.name)
    }

    function goOut() {
        planId = null;
    }

    function addVariable() {
        backend.plans.addVariable(planId)
            .then(v => {
                plan.vars.push(v);
                plan = plan;
            });
    }

    function openVar(id: number) {
        editingVar = id;
    }

    function editVar(event: CustomEvent<Var>) {
        editingVar = null;
        backend.plans.editVariable(planId, event.detail)
            .then(p => plan = p);
    }
</script>

{#if plan != null}
    <div class="ui breadcrumb">
        <a class="section" on:click|preventDefault={goOut}>Home</a>
        <div class="divider">/</div>
        <div class="active section">{plan.name}</div>
    </div>

    <h1>
        <EditableText bind:text={plan.name}/>
    </h1>
{/if}

<p>Plan {planId} selected</p>

{#if plan != null}
    <div class="ui segments">
        {#each plan.vars as vvar}
            <div class="ui segment clickable" class:clickable={vvar.id !== editingVar}
                 on:click={() => openVar(vvar.id)}>
                {#if vvar.id !== editingVar}
                    <div class="ui tiny label">{vvar.id}</div>
                    {vvar.name}
                {:else}
                    <VarEditForm {vvar} on:done={editVar}/>
                {/if}
            </div>
        {/each}
    </div>
    <button class="ui primary button" on:click={addVariable}>
        <i class="plus icon"></i> Add variable
    </button>
{:else}
    Loading...
{/if}