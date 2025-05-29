<script>
    let username = '';
    let email = '';
    let password = '';
    let repeatPassword = '';
    let error = '';
    let success = '';

    async function handleSignup() {
        error = '';
        success = '';
        const res = await fetch('/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Accept': 'application/json'
            },
            body: new URLSearchParams({
                username,
                email,
                password,
                'repeat-password': repeatPassword
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
            error = data.error || 'Erro ao criar conta';
        }
    }
</script>

<h2>Criar Conta</h2>
<form on:submit|preventDefault={handleSignup}>
    <input
        placeholder="Usuário"
        bind:value={username}
        required
        autocomplete="username"
    />
    <input
        type="email"
        placeholder="E-mail"
        bind:value={email}
        required
        autocomplete="email"
    />
    <input
        type="password"
        placeholder="Senha"
        bind:value={password}
        required
        autocomplete="new-password"
    />
    <input
        type="password"
        placeholder="Repita a senha"
        bind:value={repeatPassword}
        required
        autocomplete="new-password"
    />
    <button type="submit">Criar Conta</button>
    {#if error}
        <p style="color:red">{error}</p>
    {/if}
    {#if success}
        <p style="color:green">{success}</p>
    {/if}
</form>