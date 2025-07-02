<script>
    import { Music, Eye, EyeOff, Mail, User, Lock, UserCircle } from 'lucide-svelte'
    import { push } from 'svelte-spa-router'
    import Login from '../lib/Login.svelte'
    
    // Reactive state using $state rune - properly initialized
    let formData = $state({
        nickname: '',
        email: '',
        password: '',
        displayName: '',
        confirmPassword: ''
    })
    
    let showPassword = $state(false)
    let showConfirmPassword = $state(false)
    let isLoading = $state(false)
    let showLoginModal = $state(false)
    
    // Properly reactive objects using $state - these will trigger UI updates
    let errors = $state({})
    let touched = $state({})
    
    // Derived state for form validity - automatically updates when errors change
    const isFormValid = $derived(Object.keys(errors).length === 0 && Object.values(formData).every(value => value.trim() !== ''))
    
    // Derived state for checking if any field has been touched
    const hasAnyTouched = $derived(Object.keys(touched).length > 0)
    
    function validateField(field, value) {
        switch (field) {
        case 'nickname':
            if (!value.trim()) return 'Nome de usuário é obrigatório'
            if (value.length < 3) return 'Nome de usuário deve ter pelo menos 3 caracteres'
            if (value.length > 20) return 'Nome de usuário deve ter menos de 20 caracteres'
            if (!/^[a-zA-Z0-9_-]+$/.test(value)) return 'Nome de usuário pode conter apenas letras, números, hífens e sublinhados'
            return null
            
        case 'email':
            if (!value.trim()) return 'Email é obrigatório'
            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) return 'Por favor, digite um endereço de email válido'
            return null
            
        case 'password':
            if (!value) return 'Senha é obrigatória'
            if (value.length < 8) return 'Senha deve ter pelo menos 8 caracteres'
            if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) return 'Senha deve conter pelo menos uma letra maiúscula, uma minúscula e um número'
            return null
            
        case 'confirmPassword':
            if (!value) return 'Por favor, confirme sua senha'
            if (value !== formData.password) return 'Senhas não coincidem'
            return null
            
        case 'displayName':
            if (!value.trim()) return 'Nome de exibição é obrigatório'
            if (value.length < 2) return 'Nome de exibição deve ter pelo menos 2 caracteres'
            if (value.length > 50) return 'Nome de exibição deve ter menos de 50 caracteres'
            return null
            
        default:
            return null
        }
    }
    
    function updateTouchedState(field) {
        // Create new object to trigger reactivity
        touched[field] = true
    }
    
    function updateErrorState(field, error) {
        if (error) {
        // Add error - create new object to trigger reactivity
        errors[field] = error
        } else {
        // Remove error - create new object without this field
        delete errors[field]
        }
    }
    
    function handleBlur(field) {
        // Mark field as touched
        updateTouchedState(field)
        
        // Validate and update error state
        const error = validateField(field, formData[field])
        updateErrorState(field, error)
    }
    
    function handleInput(field, value) {
        // Update form data - this automatically triggers reactivity
        formData[field] = value
        
        // If field has been touched, validate immediately
        if (touched[field]) {
        const error = validateField(field, value)
        updateErrorState(field, error)
        }
        
        // Special case: if password changes, revalidate confirm password if it's been touched
        if (field === 'password' && touched['confirmPassword']) {
        const confirmError = validateField('confirmPassword', formData.confirmPassword)
        updateErrorState('confirmPassword', confirmError)
        }
    }
    
    function togglePasswordVisibility() {
        showPassword = !showPassword
    }
    
    function toggleConfirmPasswordVisibility() {
        showConfirmPassword = !showConfirmPassword
    }
    
    function validateAllFields() {
        const fieldErrors = {}
        
        // Validate each field
        Object.keys(formData).forEach(field => {
        const error = validateField(field, formData[field])
        if (error) {
            fieldErrors[field] = error
        }
        })
        
        return fieldErrors
    }
    
    function markAllFieldsAsTouched() {
        // Mark all fields as touched
        touched['nickname'] = true
        touched['email'] = true
        touched['password'] = true
        touched['displayName'] = true
        touched['confirmPassword'] = true
    }
    
    function resetForm() {
        // Reset all reactive state
        formData.nickname = ''
        formData.email = ''
        formData.password = ''
        formData.displayName = ''
        formData.confirmPassword = ''
        
        // Clear touched and errors
        Object.keys(touched).forEach(key => delete touched[key])
        Object.keys(errors).forEach(key => delete errors[key])
    }
    
    async function handleSubmit(event) {
        event.preventDefault()
        
        // Mark all fields as touched for validation display
        markAllFieldsAsTouched()
        
        // Validate all fields
        const fieldErrors = validateAllFields()
        
        // Update errors state - this will trigger UI updates
        Object.keys(errors).forEach(key => delete errors[key])
        Object.keys(fieldErrors).forEach(key => {
            errors[key] = fieldErrors[key]
        })
        
        // If there are errors, don't submit
        if (Object.keys(fieldErrors).length > 0) {
        return
        }
        
        isLoading = true
        
        try {
            // Make actual API call to backend
            const response = await fetch('/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    username: formData.nickname, // Map nickname to username for backend
                    email: formData.email,
                    password: formData.password,
                    'repeat-password': formData.confirmPassword
                })
            })
            
            if (response.ok) {
                const data = await response.json()
                alert('Cadastro realizado com sucesso! Faça login para continuar.')
                resetForm()
                window.location.href = '/#/login'
            } else {
                let errorData = {}
                try {
                    errorData = await response.json()
                } catch {
                    errorData = { error: 'Erro inesperado no servidor.' }
                }
                errors['submit'] = errorData.error || 'Falha no cadastro. Tente novamente.'
            }
            
        } catch (error) {
        console.error('Signup failed:', error)
        // Add submit error to errors state
        errors['submit'] = 'Erro de rede. Tente novamente.'
        } finally {
        isLoading = false
        }
    }
    
    // Helper functions for template - these are reactive and will update UI
    function hasError(field) {
        return !!(errors[field] && touched[field])
    }
    
    function getError(field) {
        return errors[field] || ''
    }
    
    function isTouched(field) {
        return !!touched[field]
    }
    
    function getFieldValidationClass(field) {
        if (!touched[field]) return ''
        return errors[field] ? 'error' : 'valid'
    }
    
    function openLoginModal() {
        showLoginModal = true
    }
    
    function closeLoginModal() {
        showLoginModal = false
    }
    
    // Effect to log state changes for debugging (optional)
    $effect(() => console.log('Errors updated:', errors))
    
    $effect(() => console.log('Touched updated:', touched))
</script>

<nav class="navbar-signup">
    <div class="navbar-container">
        <div class="logo-component">
            <button class="logo-button" onclick={() => push('/')} aria-label="Voltar para página inicial">
                <img src="/logocomtexto.png" alt="BeeSharp Logo" />
            </button>
        </div>
    </div>
</nav>

<div class="signup-page">

    <!-- Main Content -->
    <main class="main">
        <div class="container">
        <div class="signup-container">
            <div class="signup-card">
            <!-- Header -->
            <div class="signup-header">
                <div class="header-icon">
                <img src="/logo.png" alt="Profile" class="header-profile-image" />
                </div>
                <h1 class="signup-title">Cadastre-se no BeeSharp</h1>
                <p class="signup-subtitle">
                Descubra, avalie e compartilhe seus álbuns favoritos com uma comunidade apaixonada de amantes da música.
                </p>
            </div>

            <!-- Form -->
            <form class="signup-form" onsubmit={handleSubmit} novalidate>
                <div class="form-grid">
                <!-- Nickname -->
                <div class="form-group">
                    <label for="nickname" class="form-label">
                    <User size={16} />
                    Nome de Usuário
                    </label>
                    <input
                    type="text"
                    id="nickname"
                    class="form-input {getFieldValidationClass('nickname')}"
                    placeholder="Digite seu nome de usuário"
                    bind:value={formData.nickname}
                    oninput={(e) => handleInput('nickname', e.currentTarget.value)}
                    onblur={() => handleBlur('nickname')}
                    aria-describedby={hasError('nickname') ? 'nickname-error' : undefined}
                    aria-invalid={hasError('nickname')}
                    />
                    {#if hasError('nickname')}
                    <p class="error-message" id="nickname-error" role="alert">
                        {getError('nickname')}
                    </p>
                    {/if}
                </div>

                <!-- Display Name -->
                <div class="form-group">
                    <label for="displayName" class="form-label">
                    <UserCircle size={16} />
                    Nome de Exibição
                    </label>
                    <input
                    type="text"
                    id="displayName"
                    class="form-input {getFieldValidationClass('displayName')}"
                    placeholder="Seu nome completo"
                    bind:value={formData.displayName}
                    oninput={(e) => handleInput('displayName', e.currentTarget.value)}
                    onblur={() => handleBlur('displayName')}
                    aria-describedby={hasError('displayName') ? 'displayName-error' : undefined}
                    aria-invalid={hasError('displayName')}
                    />
                    {#if hasError('displayName')}
                    <p class="error-message" id="displayName-error" role="alert">
                        {getError('displayName')}
                    </p>
                    {/if}
                </div>
                </div>

                <!-- Email -->
                <div class="form-group">
                <label for="email" class="form-label">
                    <Mail size={16} />
                    Endereço de Email
                </label>
                <input
                    type="email"
                    id="email"
                    class="form-input {getFieldValidationClass('email')}"
                    placeholder="Digite seu email"
                    bind:value={formData.email}
                    oninput={(e) => handleInput('email', e.currentTarget.value)}
                    onblur={() => handleBlur('email')}
                    aria-describedby={hasError('email') ? 'email-error' : undefined}
                    aria-invalid={hasError('email')}
                />
                {#if hasError('email')}
                    <p class="error-message" id="email-error" role="alert">
                    {getError('email')}
                    </p>
                {/if}
                </div>

                <div class="form-grid">
                <!-- Password -->
                <div class="form-group">
                    <label for="password" class="form-label">
                    <Lock size={16} />
                    Senha
                    </label>
                    <div class="password-input-wrapper">
                    <input
                        type={showPassword ? 'text' : 'password'}
                        id="password"
                        class="form-input password-input {getFieldValidationClass('password')}"
                        placeholder="Crie uma senha"
                        bind:value={formData.password}
                        oninput={(e) => handleInput('password', e.currentTarget.value)}
                        onblur={() => handleBlur('password')}
                        aria-describedby={hasError('password') ? 'password-error' : undefined}
                        aria-invalid={hasError('password')}
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
                    {#if hasError('password')}
                    <p class="error-message" id="password-error" role="alert">
                        {getError('password')}
                    </p>
                    {/if}
                </div>

                <!-- Confirm Password -->
                <div class="form-group">
                    <label for="confirmPassword" class="form-label">
                    <Lock size={16} />
                    Confirmar Senha
                    </label>
                    <div class="password-input-wrapper">
                    <input
                        type={showConfirmPassword ? 'text' : 'password'}
                        id="confirmPassword"
                        class="form-input password-input {getFieldValidationClass('confirmPassword')}"
                        placeholder="Confirme sua senha"
                        bind:value={formData.confirmPassword}
                        oninput={(e) => handleInput('confirmPassword', e.currentTarget.value)}
                        onblur={() => handleBlur('confirmPassword')}
                        aria-describedby={hasError('confirmPassword') ? 'confirmPassword-error' : undefined}
                        aria-invalid={hasError('confirmPassword')}
                    />
                    <button
                        type="button"
                        class="password-toggle"
                        onclick={toggleConfirmPasswordVisibility}
                        aria-label={showConfirmPassword ? 'Ocultar senha' : 'Mostrar senha'}
                    >
                        {#if showConfirmPassword}
                        <EyeOff size={16} />
                        {:else}
                        <Eye size={16} />
                        {/if}
                    </button>
                    </div>
                    {#if hasError('confirmPassword')}
                    <p class="error-message" id="confirmPassword-error" role="alert">
                        {getError('confirmPassword')}
                    </p>
                    {/if}
                </div>
                </div>

                <!-- Submit Button -->
                <button
                type="submit"
                class="submit-button"
                disabled={isLoading || !isFormValid}
                aria-label="Criar Conta"
                >
                {#if isLoading}
                    <div class="loading-spinner"></div>
                    Criando Conta...
                {:else}
                    Criar Conta
                {/if}
                </button>

                {#if errors['submit']}
                <p class="error-message submit-error" role="alert">
                    {errors['submit']}
                </p>
                {/if}
            </form>

            <!-- Footer -->
            <div class="signup-footer">
                <p class="footer-text">
                Já possui uma conta? 
                <button class="footer-link" aria-label="Entrar na sua conta" onclick={openLoginModal}>
                    Entrar
                </button>
                </p>
            </div>
            </div>

            <!-- Side Content -->
            <div class="side-content">
            <div class="feature-list">
                <div class="feature-item">
                <div class="feature-icon">
                    <Music size={24} />
                </div>
                <div class="feature-text">
                    <h3>Descubra Novas Músicas</h3>
                    <p>Encontre seu próximo álbum favorito através de recomendações personalizadas e avaliações da comunidade.</p>
                </div>
                </div>
                
                <div class="feature-item">
                <div class="feature-icon">
                    <UserCircle size={24} />
                </div>
                <div class="feature-text">
                    <h3>Construa Seu Perfil</h3>
                    <p>Demonstre seu gosto musical com listas selecionadas, avaliações e resenhas que refletem seu estilo único.</p>
                </div>
                </div>
                
                <div class="feature-item">
                <div class="feature-icon">
                    <User size={24} />
                </div>
                <div class="feature-text">
                    <h3>Conecte-se com Outros</h3>
                    <p>Siga amigos e entusiastas da música para descobrir novos álbuns e compartilhar suas descobertas favoritas.</p>
                </div>
                </div>
            </div>
            </div>
        </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>© BeeSharp Limited. Feito por estudantes do IME USP apaixonados por música. 
            Dados da API do Spotify.</p>
    </footer>
</div>

<!-- Login Modal -->
{#if showLoginModal}
    <div class="login-popup-overlay">
        <Login onClose={closeLoginModal} isModal={true} />
    </div>
{/if}

<style>
    .navbar-signup {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        z-index: 100;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 1.5rem;
        background: #255F85;
        height: 80px; 
        min-height: 80px;
        box-shadow: 0 4px 15px rgba(37, 95, 133, 0.3);
        transition: all 0.3s ease;
    }

    .navbar-signup::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(45deg, 
            transparent 0%, 
            rgba(255, 255, 255, 0.05) 25%, 
            rgba(255, 255, 255, 0.1) 50%, 
            rgba(255, 255, 255, 0.05) 75%, 
            transparent 100%);
        background-size: 200% 200%;
        animation: wave 5s ease-in-out infinite;
    }

    @keyframes wave {
        0% { 
            background-position: 0% 0%;
        }
        50% { 
            background-position: 100% 100%;
        }
        100% { 
            background-position: 0% 0%;
        }
    }

    .navbar-container {
        width: 1500px;
        margin: 0 auto;
        display: flex;
        justify-content: space-between;
        align-items: center;
        position: relative;
        z-index: 1;
    }

    .logo-component img {
        height: 70px;
        cursor: pointer;
        transition: all 0.3s ease;
        filter: drop-shadow(2px 2px 4px rgba(0, 0, 0, 0.1));
    }

    .logo-component img:hover {
        transform: scale(1.05);
        filter: drop-shadow(3px 3px 6px rgba(0, 0, 0, 0.2));
    }

    .logo-button {
        background: none;
        border: none;
        padding: 0;
        cursor: pointer;
        display: flex;
        align-items: center;
    }

    :global(*) {
        box-sizing: border-box;
    }

    :global(html) {
        background-color: #14181c !important;
        height: 100%;
    }

    :global(body) {
        margin: 0;
        padding: 0;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        background-color: #14181c !important;
        color: white;
        min-height: 100vh;
        height: 100%;
    }

    :global(#app) {
        background-color: #14181c !important;
        min-height: 100vh;
    }

    .signup-page {
        min-height: 100vh;
        background-color: #14181c !important;
        padding-top: 120px;
        width: 100%;
        position: relative;
    }

    .signup-page::before {
        content: '';
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: #14181c;
        z-index: -1;
    }

    .container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 1rem;
        background-color: #14181c;
    }

    .main {
        padding: 2rem 0;
        background-color: #14181c;
        width: 100%;
    }

    .signup-container {
        display: grid;
        grid-template-columns: 1fr;
        gap: 3rem;
        max-width: 1000px;
        margin: 0 auto;
    }

    .signup-card {
        background-color: #1d232a;
        border-radius: 0.75rem;
        padding: 2rem;
        box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.3);
        border: 1px solid #374151;
    }

    .signup-header {
        text-align: center;
        margin-bottom: 2rem;
    }

    .header-icon {
        color: #FFC857;
        margin-bottom: 1rem;
        display: flex;
        justify-content: center;
        position: relative;
        width: 105px;
        height: 105px;
        margin: 0 auto 1rem auto;
        background: #255F85;
        border-radius: 50%;
        box-shadow: 0 4px 13px rgba(37, 95, 133, 0.4);
        align-items: center;
    }

    .header-profile-image {
        width: 90px;
        height: 90px;
        border-radius: 50%;
        object-fit: cover;
        transition: all 0.3s ease;
        position: relative;
        z-index: 1;
    }

    .header-profile-image:hover {
        transform: scale(1.05);
    }

    .signup-title {
        font-size: 2rem;
        font-weight: bold;
        margin: 0 0 0.5rem 0;
        color: white;
        font-family: 'Familjen Grotesk'
    }

    .signup-subtitle {
        font-size: 1rem;
        color: #9ca3af;
        margin: 0;
        line-height: 1.5;
    }

    .signup-form {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .form-grid {
        display: grid;
        grid-template-columns: 1fr;
        gap: 1.5rem;
    }

    .form-group {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .form-label {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.875rem;
        font-weight: 500;
        color: #d1d5db;
    }

    .form-input {
        padding: 0.75rem 1rem;
        background-color: #2c3440;
        border: 1px solid #374151;
        border-radius: 0.375rem;
        color: white;
        font-size: 0.875rem;
        transition: all 0.2s;
    }

    .form-input:focus {
        outline: none;
        border-color: #FFC857;
        box-shadow: 0 0 0 2px rgba(255, 200, 87, 0.1);
    }

    .form-input:hover {
        border-color: #4b5563;
    }

    .form-input::placeholder {
        color: #6b7280;
    }

    .form-input.error {
        border-color: #ef4444;
    }

    .form-input.error:focus {
        border-color: #ef4444;
        box-shadow: 0 0 0 2px rgba(239, 68, 68, 0.1);
    }

    .form-input.valid {
        border-color: #10b981;
    }

    .form-input.valid:focus {
        border-color: #10b981;
        box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.1);
    }

    .password-input-wrapper {
        position: relative;
    }

    .password-input {
        padding-right: 3rem;
    }

    .password-toggle {
        position: absolute;
        right: 0.75rem;
        top: 50%;
        transform: translateY(-50%);
        background: none;
        border: none;
        color: #6b7280;
        cursor: pointer;
        padding: 0.25rem;
        border-radius: 0.25rem;
        transition: color 0.2s;
    }

    .password-toggle:hover {
        color: #9ca3af;
    }

    .error-message {
        font-size: 0.75rem;
        color: #ef4444;
        margin: 0;
        text-align: left;
    }

    .submit-button {
        background-color: #FFC857;
        color: #14181c;
        border: none;
        padding: 0.875rem 1.5rem;
        border-radius: 0.375rem;
        font-size: 0.875rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        margin-top: 0.5rem;
    }

    .submit-button:hover:not(:disabled) {
        background-color: #E9724C;
        transform: translateY(-1px);
    }

    .submit-button:disabled {
        background-color: #6b7280;
        cursor: not-allowed;
        transform: none;
    }

    .loading-spinner {
        width: 16px;
        height: 16px;
        border: 2px solid #14181c;
        border-top: 2px solid transparent;
        border-radius: 50%;
        animation: spin 1s linear infinite;
    }

    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }

    .submit-error {
        text-align: center;
        margin-top: 0.5rem;
    }

    .signup-footer {
        text-align: center;
        margin-top: 2rem;
        padding-top: 2rem;
        border-top: 1px solid #374151;
    }

    .footer-text {
        font-size: 0.875rem;
        color: #9ca3af;
        margin: 0;
    }

    .footer-link {
        color: #FFC857;
        background: none;
        border: none;
        cursor: pointer;
        text-decoration: underline;
        font-size: inherit;
        font-weight: 500;
        transition: color 0.2s;
    }

    .footer-link:hover {
        color: #D4A843;
    }

    .side-content {
        display: none;
    }

    .feature-list {
        display: flex;
        flex-direction: column;
        gap: 2rem;
    }

    .feature-item {
        display: flex;
        gap: 1rem;
        align-items: flex-start;
    }

    .feature-icon {
        flex-shrink: 0;
        padding: 0.75rem;
        border-radius: 50%;
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: transform 0.3s ease;
    }

    .feature-icon:hover {
        transform: scale(1.1);
    }

    .feature-item:nth-child(1) .feature-icon {
        background-color: rgba(255, 200, 87, 0.3);
        color: #FFC857;
    }

    .feature-item:nth-child(2) .feature-icon {
        background-color: rgba(233, 114, 76, 0.3);
        color: #E9724C;
    }

    .feature-item:nth-child(3) .feature-icon {
        background-color: rgba(197, 40, 61, 0.3);
        color: #C5283D;
    }

    .feature-text h3 {
        margin: 0 0 0.5rem 0;
        font-size: 1.125rem;
        font-weight: 600;
        color: white;
        font-family: 'Familjen Grotesk', sans-serif;
    }

    .feature-text p {
        margin: 0;
        font-size: 0.875rem;
        color: #9ca3af;
        line-height: 1.5;
    }

    @media (min-width: 640px) {
        .form-grid {
        grid-template-columns: 1fr 1fr;
        }
        
        .signup-card {
        padding: 2.5rem;
        }
    }

    @media (min-width: 1024px) {
        .signup-container {
        grid-template-columns: 1fr 1fr;
        gap: 4rem;
        align-items: start;
        }
        
        .side-content {
        display: block;
        padding-top: 2rem;
        }
        
        .signup-card {
        padding: 3rem;
        }
    }

    .footer {
        padding: 1rem;
        text-align: center;
        margin-top: 3rem;
        font-size: 0.75rem;
        color: #6b7280;
    }

    .footer p {
        margin: 0;
    }

    .login-popup-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100vw;
        height: 100vh;
        background: rgba(0,0,0,0.5);
        z-index: 1000;
        display: flex;
        align-items: center;
        justify-content: center;
    }
</style>
