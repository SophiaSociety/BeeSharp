<script>
    import { onMount } from 'svelte';
    import { wrap } from 'svelte-spa-router/wrap';

    export let params = {}; // svelte-spa-router injeta params

    let userData = null;
    let error = '';
    let loading = true;

    onMount(async () => {
        loading = true;
        error = '';
        userData = null;
        try {
            const res = await fetch(`/${params.username}`);
            if (!res.ok) {
                const data = await res.json();
                error = data.error || 'Erro ao buscar usuário';
            } else {
                userData = await res.json();
            }
        } catch (e) {
            error = 'Erro de conexão com o servidor';
        }
        loading = false;
    });
</script>

{#if loading}
    <p>Carregando perfil...</p>
{:else if error}
    <p style="color:red">{error}</p>
{:else if userData}
    <h2>Perfil de {userData.username}</h2>
    <p><b>Email:</b> {userData.email}</p>
    <h3>Álbuns favoritos:</h3>
    <ul>
        {#each userData.albums as albumId}
            <li>ID do álbum: {albumId}</li>
        {/each}
    </ul>
    <h3>Seguindo:</h3>
    <ul>
        {#each userData.following as follow}
            <li>{follow}</li>
        {/each}
    </ul>
{/if}