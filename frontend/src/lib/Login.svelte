<script lang="ts">
    import { Eye, EyeOff, User, Lock, X, Music } from 'lucide-svelte';
    import { getAuthToken } from './auth.js';
    import { push } from 'svelte-spa-router';
    
    interface LoginResponse {
        token?: string;
        error?: string;
    }
    
    let { onClose = null, isModal = false } = $props();
    let username = $state('');
    let password = $state('');
    let error = $state('');
    let success = $state('');
    let showPassword = $state(false);
    let isLoading = $state(false);

    // Check if user is already logged in
    $effect(() => {
        const token = getAuthToken();
        if (token && !isTokenExpired(token)) {
            if (isModal && onClose) {
                onClose();
            }
            push('/albuns');
        } else if (token && isTokenExpired(token)) {
            localStorage.removeItem('authToken');
            localStorage.removeItem('username');
        }
    });

    async function handleLogin(event: Event) {
        event.preventDefault();
        error = '';
        success = '';

        // Basic validation
        if (!username.trim()) {
            error = 'Username is required';
            return;
        }
        if (!password.trim()) {
            error = 'Password is required';
            return;
        }
        if (password.length < 3) {
            error = 'Password must be at least 3 characters';
            return;
        }

        isLoading = true;

        try {
            const res = await fetch('http://localhost:8080/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Accept': 'application/json'
                },
                body: new URLSearchParams({
                    username: username.trim(),
                    password: password
                })
            });

            const data: LoginResponse = await res.json();

            if (res.ok && data.token) {
                // Store the JWT token
                localStorage.setItem('authToken', data.token);
                localStorage.setItem('username', username.trim());
                success = 'Login successful! Redirecting...';
                
                // Close modal and stay on the same page (or redirect if not modal)
                setTimeout(() => {
                    if (isModal && onClose) {
                        onClose(); // Just close the modal and stay on the same page
                    } else {
                        push('/albuns'); // Only redirect if not in modal mode
                    }
                }, 1500);
            } else {
                error = data.error || 'Login failed';
            }
        } catch (e) {
            console.error('Login error:', e);
            error = 'Network error. Please check if the server is running.';
        } finally {
            isLoading = false;
        }
    }

    function togglePasswordVisibility() {
        showPassword = !showPassword;
    }

    function handleOverlayClick(event: MouseEvent) {
        if (isModal && onClose) {
            onClose();
        }
    }

    function handleCloseClick(event: MouseEvent) {
        if (isModal && onClose) {
            onClose();
        }
    }

    function handleKeydown(event: KeyboardEvent) {
        if (event.key === 'Enter' && isModal && onClose) {
            onClose();
        }
    }

    function handleSignUpClick() {
        // Check current location to decide action
        const currentPath = window.location.pathname;
        
        if (currentPath === '/criar-conta') {
            // If we're already on the signup page, just close the modal
            if (isModal && onClose) {
                onClose();
            }
        } else {
            // If we're on landing page or anywhere else, redirect to signup
            if (isModal && onClose) {
                onClose(); // Close modal first
            }
            push('/criar-conta'); // Navigate to signup page
        }
    }

    function isTokenExpired(token) {
        if (!token) return true;
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            // exp está em segundos desde epoch
            return (payload.exp * 1000) < Date.now();
        } catch (e) {
            return true;
        }
    }
</script>

{#if isModal}
<div class="overlay" onclick={handleOverlayClick} role="button" tabindex="0" onkeydown={handleKeydown}></div>
{/if}

<div class="login-modal" class:standalone={!isModal}>
    <!-- Close Button -->
    {#if isModal && onClose}
    <button class="close-btn" onclick={handleCloseClick} aria-label="Fechar">
        <X size={18} />
    </button>
    {/if}

    <!-- Header -->
    <div class="login-header">
        <!-- Removido o bloco brand-icon com a logo -->
        <h2 class="login-title">Bem-vindo de volta!</h2>
        <p class="login-subtitle">
        Que bom te ver por aqui :)
        </p>
    </div>

    <!-- Form -->
    <form class="login-form" onsubmit={handleLogin} autocomplete="off">
        <!-- Username -->
        <div class="form-group">
        <label for="username" class="form-label"><User size={16} class="input-icon" />Usuário</label>
        <div class="input-wrapper">
            <input
            id="username"
            class="form-input"
            placeholder="Digite seu nome de usuário"
            bind:value={username}
            required
            autocomplete="username"
            />
        </div>
        </div>

        <!-- Password -->
        <div class="form-group">
        <label for="password" class="form-label"><Lock size={16} class="input-icon" />Senha</label>
        <div class="input-wrapper">
            <input
            id="password"
            type={showPassword ? 'text' : 'password'}
            class="form-input password-input"
            placeholder="Digite sua senha"
            bind:value={password}
            required
            autocomplete="current-password"
            />
            <button
            type="button"
            class="password-toggle"
            onclick={togglePasswordVisibility}
            aria-label={showPassword ? 'Ocultar senha' : 'Mostrar senha'}
            >
            {#if showPassword}
                <EyeOff size={16} />
            {:else}
                <Eye size={16} />
            {/if}
            </button>
        </div>
        </div>

        <!-- Register Link -->
        <div class="register-link">
        Não tem uma conta? 
        <button type="button" class="register-btn" onclick={handleSignUpClick}>
            Cadastre-se
        </button>
        </div>

        <!-- Error/Success Messages -->
        {#if error}
        <div class="message error-message" role="alert">
            {error}
        </div>
        {/if}
        {#if success}
        <div class="message success-message" role="alert">
            {success}
        </div>
        {/if}

        <!-- Submit Button -->
        <button type="submit" class="login-button" disabled={isLoading}>
            {#if isLoading}
                Entrando...
            {:else}
                Entrar
            {/if}
        </button>
    </form>
    <div class="corner-logo-container">
        <div class="semicircle-bg"></div>
        <img src="/logo.png" alt="Logo BeeSharp" class="corner-logo" />
    </div>
</div>

<style>
    /* CSS Custom Properties for Color Palette */
    :root {
        /* Primary Palette */
        --color-primary: #FFC857;
        --color-secondary: #E9724C;
        --color-tertiary: #C5283D;
        --color-dark-red: #481D24;
        --color-blue: #255F85;
        /* Neutral Palette */
        --color-black: #0a0a0a;
        --color-dark-gray: #1a1a1a;
        --color-medium-gray: #2a2a2a;
        --color-light-gray: #3a3a3a;
        --color-text-gray: #9ca3af;
        --color-white: #ffffff;
        --color-off-white: #f8f9fa;
        /* Semantic Colors */
        --color-background: var(--color-black);
        --color-surface: #23272f;
        --color-surface-elevated: #2c313a;
        --color-border: #353a45;
        --color-text-primary: #f8f9fa;
        --color-text-secondary: #b0b4bb;
        --color-accent: var(--color-primary);
        --color-accent-hover: #e6b84f;
        --color-success: var(--color-blue);
        --color-warning: var(--color-secondary);
        --color-error: var(--color-tertiary);
    }

    .overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.8);
        backdrop-filter: blur(4px);
        z-index: 1000;
        animation: fadeIn 0.2s ease-out;
    }

    @keyframes fadeIn {
        from { opacity: 0; }
        to { opacity: 1; }
    }

    .login-modal {
        background: var(--color-surface);
        border-radius: 1.5rem;
        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
        border: 1px solid var(--color-border);
        width: 400px;
        max-width: 90vw;
        max-height: 90vh;
        padding: 2rem;
        box-sizing: border-box;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        color: var(--color-text-primary);
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        z-index: 1001;
        animation: modalSlideIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        overflow: hidden;
    }

    .login-modal.standalone {
        border-radius: 1.5rem;
        position: static;
        transform: none;
        margin: 2rem auto;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.10);
        animation: none;
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        justify-content: center;
        background: var(--color-surface);
        z-index: auto;
    }

    .login-header {
        text-align: left;
        margin-bottom: 2rem;
    }

    .login-title {
        font-size: 1.75rem;
        font-weight: 600;
        margin: 0 0 0.5rem 0;
        color: var(--color-text-primary);
        letter-spacing: -0.025em;
        text-align: left;
        font-family: 'Familjen Grotesk', sans-serif;
    }

    .login-subtitle {
        font-size: 0.95rem;
        color: var(--color-text-secondary);
        margin: 0;
        line-height: 1.5;
        text-align: left;
        font-family: 'Familjen Grotesk', sans-serif;
    }

    .login-form {
        display: flex;
        flex-direction: column;
        gap: 1.25rem;
    }

    .form-group {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .form-label {
        font-size: 0.95rem;
        font-weight: 500;
        color: var(--color-text-primary);
        margin-bottom: 0.25rem;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        text-align: left;
    }

    .input-wrapper {
        position: relative;
        display: flex;
        align-items: center;
    }

    .form-input {
        width: 100%;
        padding: 0.75rem 0.875rem 0.75rem 1.5rem; 
        background: var(--color-surface-elevated);
        border: 1px solid var(--color-border);
        border-radius: 6px;
        color: var(--color-text-primary);
        font-size: 0.95rem;
        transition: all 0.2s ease;
        box-sizing: border-box;
        text-align: left;
    }

    .form-input:focus {
        outline: none;
        border-color: var(--color-accent);
        background: #23272f;
    }

    .form-input:hover {
        border-color: var(--color-accent);
    }

    .form-input::placeholder {
        color: #aaa;
    }

    .password-input {
        padding-right: 2.5rem;
    }

    .password-toggle {
        position: absolute;
        right: 0.875rem;
        background: none;
        border: none;
        color: #b0b4bb;
        cursor: pointer;
        padding: 0.25rem;
        border-radius: 4px;
        transition: all 0.2s ease;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .password-toggle:hover {
        color: var(--color-accent);
        background: #23272f;
    }

    .message {
        padding: 0.75rem;
        border-radius: 6px;
        font-size: 0.95rem;
        margin: 0;
        animation: slideIn 0.2s ease-out;
    }

    .error-message {
        background: #3a2323;
        border: 1px solid #e57373;
        color: #ffb3b3;
    }

    .success-message {
        background: #233a2b;
        border: 1px solid #34a853;
        color: #b6f5c6;
    }

    .login-button {
        background: var(--color-blue);
        color: var(--color-white);
        border: none;
        padding: 0.75rem 1.5rem;
        border-radius: 10px;
        font-size: 1rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s ease;
        min-width: 120px;
        margin-top: 1rem;
        align-self: center;
    }

    .login-button:hover:not(:disabled) {
        background: #1e4e6d;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(37, 95, 133, 0.3);
    }

    .login-button:active:not(:disabled) {
        transform: translateY(0);
    }

    .login-button:disabled {
        background: #353a45;
        color: #888;
        cursor: not-allowed;
        opacity: 0.6;
    }

    .register-link {
        font-size: 0.875rem;
        color: var(--color-text-secondary);
        text-align: left;
        margin-top: 0.75rem;
        margin-bottom: 0;
    }

    .register-btn {
        background: none;
        border: none;
        color: var(--color-accent);
        cursor: pointer;
        font-size: inherit;
        font-weight: 500;
        text-decoration: underline;
        text-underline-offset: 2px;
        transition: color 0.2s ease;
    }

    .register-btn:hover {
        color: var(--color-accent-hover);
    }

    .close-btn {
        position: absolute;
        top: 1rem;
        right: 1rem;
        background: transparent;
        border: none;
        color: var(--color-text-secondary);
        cursor: pointer;
        padding: 0.5rem;
        border-radius: 50%;
        transition: background 0.2s, color 0.2s;
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 10;
    }

    .close-btn:hover {
        color: var(--color-text-primary);
        background: #2c313a;
    }

    .corner-logo-container {
        position: absolute;
        left: 0;
        bottom: 0;
        width: 120px;
        height: 120px;
        pointer-events: none;
        z-index: 2;
    }
    .semicircle-bg {
        position: absolute;
        left: 0;
        bottom: 0;
        width: 100px;
        height: 100px;
        background: var(--color-blue);
        border-top-right-radius: 120px;
        border-bottom-left-radius: 0;
        border-top-left-radius: 0;
        border-bottom-right-radius: 0;
        clip-path: ellipse(100% 100% at 0% 100%);
        opacity: 0.95;
    }
    .corner-logo {
        position: absolute;
        left: 10px;
        bottom: 10px;
        width: 70px;
        height: 70px;
        border-radius: 50%;
        object-fit: cover;
        background: transparent;
        z-index: 3;
        pointer-events: auto;
        transition: all 0.3s ease;
        cursor: pointer;
    }

    .corner-logo:hover {
        transform: scale(1.1) rotate(5deg);
    }

    @keyframes modalSlideIn {
        from {
            opacity: 0;
            transform: translate(-50%, -50%) scale(0.95) translateY(10px);
        }
        to {
            opacity: 1;
            transform: translate(-50%, -50%) scale(1) translateY(0);
        }
    }

    @keyframes slideIn {
        from { opacity: 0; transform: translateY(-8px); }
        to { opacity: 1; transform: translateY(0); }
    }

    @media (max-width: 480px) {
        .login-modal {
            width: 90vw;
            padding: 1.5rem;
            margin: 1rem;
        }
        .login-title {
            font-size: 1.5rem;
        }
        .close-btn {
            top: 0.75rem;
            right: 0.75rem;
        }
        .corner-logo-container, .semicircle-bg {
            width: 80px;
            height: 80px;
        }
        .corner-logo {
            width: 40px;
            height: 40px;
            left: 10px;
            bottom: 10px;
        }
    }
</style>
