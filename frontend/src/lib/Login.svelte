<script>
    export let onClose = () => {};
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

<div class="overlay" on:click={onClose}></div>
<div class="login-modal">
    <button class="close-btn" on:click={onClose} aria-label="Fechar">
        <span class="close-x">X</span>
    </button>
    <h2>Bem-vindo de volta!</h2>
    <p class="subtitle">Que bom te ver por aqui :)</p>
    <form on:submit|preventDefault={handleLogin} autocomplete="off">
        <label for="username">Usuário</label>
        <input
            id="username"
            placeholder="Usuário"
            bind:value={username}
            required
            autocomplete="username"
        />
        <label for="password">Senha</label>
        <input
            id="password"
            type="password"
            placeholder="Senha"
            bind:value={password}
            required
            autocomplete="current-password"
        />
        <div class="register-link">Cadastre-se <a href="/criar-conta">aqui</a></div>
        <div class="captcha-placeholder">
            <img src="https://www.google.com/recaptcha/api2/anchor?ar=1&k=6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI&co=aHR0cHM6Ly9nb29nbGUuY29tOjQ0Mw..&hl=pt-BR&v=PRMR6KkA2zDg1w2Qh6Qn61Qw&size=normal&cb=1" alt="captcha" style="width:100%;max-width:300px;" />
        </div>
        {#if error}
            <p class="error">{error}</p>
        {/if}
        {#if success}
            <p class="success">{success}</p>
        {/if}
        <button type="submit" class="login-btn">Entrar</button>
    </form>
    <div class="bee-corner">
        <img src="/logo.png" alt="logo" />
    </div>
</div>

<style>
    .overlay {
        position: fixed;
        top: 0; left: 0; right: 0; bottom: 0;
        background: rgba(0,0,0,0.4);
        z-index: 1000;
    }

    .login-modal {
        position: fixed;
        top: 50%; left: 50%;
        transform: translate(-50%, -50%);
        background: #DDDDDD;
        border-radius: 20px;
        box-shadow: 0 4px 32px rgba(0,0,0,0.25);
        width: 400px;
        height: 550px;
        z-index: 1001;
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        font-family: 'Inter', sans-serif;
        box-sizing: border-box;
        padding: 2.5rem 2.5rem 2rem 2.5rem;
    }

    .close-btn {
        position: absolute;
        top: 18px;
        right: 18px;
        background: none;
        border: none;
        cursor: pointer;
        z-index: 10;
        padding: 0;
    }
    .close-x {
        display: flex;
        align-items: center;
        justify-content: center;
        background: #ff00a8;
        color: #fff;
        border-radius: 50%;
        width: 32px;
        height: 32px;
        font-size: 1.3rem;
        font-weight: bold;
        box-shadow: 2px 2px 8px #2222;
        transition: background 0.2s;
    }
    .close-btn:hover .close-x {
        background: #c5283d;
    }
    .login-modal h2 {
        margin: 0 0 0.2em 0;
        font-size: 1.8rem;
        color: #1C1C1C;
        font-family: 'Familjen Grotesk', serif;
        font-weight: semibold;
    }
    .subtitle {
        color: #888;
        margin-bottom: 1.5em;
        font-family: 'Familjen Grotesk', serif;
        font-weight: medium;
        margin-top: -0.5em;
    }
    .login-modal form {
        width: 100%;
        display: flex;
        flex-direction: column;
        gap: 0.7em;
    }
    .login-modal label {
        font-weight: bold;
        margin-top: 0.5em;
        margin-bottom: 0.2em;
    }
    .login-modal input {
        padding: 0.7em;
        border-radius: 8px;
        border: 1px solid #bbb;
        font-size: 1em;
        background: #e6e6e6;
        margin-bottom: 0.5em;
    }
    .register-link {
        color: #888;
        font-size: 0.95em;
        margin: 0.5em 0 0.5em 0;
    }
    .register-link a {
        color: #888;
        text-decoration: underline;
    }
    .captcha-placeholder {
        margin: 1em 0 1em 0;
        display: flex;
        justify-content: center;
    }
    .login-btn {
        background: #22506b;
        color: #fff;
        border: none;
        border-radius: 10px;
        font-size: 1.2em;
        padding: 0.5em 2.5em;
        margin: 1.2em auto 0 auto;
        display: block;
        box-shadow: 2px 2px 8px #2222;
        cursor: pointer;
        transition: background 0.2s;
    }

    .login-btn:hover {
        background: #1a3a4d;
    }

    .bee-corner {
        position: absolute;
        left: -40px;
        bottom: -40px;
        width: 110px;
        height: 110px;
        overflow: visible;
    }

    .bee-corner img {
        width: 100%;
        height: auto;
    }

    .error {
        color: #c5283d;
        margin: 0.5em 0 0 0;
        font-size: 1em;
    }

    .success {
        color: #2e7d32;
        margin: 0.5em 0 0 0;
        font-size: 1em;
    }

    @media (max-width: 500px) {
        .login-modal {
            width: 90vw;
            height: auto;
            min-width: unset;
            min-height: unset;
            padding: 1.2rem 0.5rem 1.2rem 0.5rem;
        }
        .bee-corner {
            left: -20px;
            bottom: -20px;
            width: 60px;
            height: 60px;
        }
    }
</style>