<script>
    let username = '';
    let password = '';
    let error = '';
    let success = '';

    async function handleLogin() {
        error = '';
        success = '';
        const res = await fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Accept': 'application/json'
            },
            body: new URLSearchParams({
                username,
                password
            })
        });
        let data = {};
        try {
            data = await res.json();
        } catch (e) {
            error = 'Erro inesperado no servidor.';
            return;
        }
        if (res.ok) {
            success = data.message;
        } else {
            error = data.error || 'Erro ao fazer login';
        }
    }
</script>

<h2>Entrar</h2>
<form on:submit|preventDefault={handleLogin}>
    <input
        placeholder="Usuário"
        bind:value={username}
        required
        autocomplete="username"
    />
    <input
        type="password"
        placeholder="Senha"
        bind:value={password}
        required
        autocomplete="current-password"
    />
    <button type="submit">Entrar</button>
    {#if error}
        <p style="color:red">{error}</p>
    {/if}
    {#if success}
        <p style="color:green">{success}</p>
    {/if}
</form>