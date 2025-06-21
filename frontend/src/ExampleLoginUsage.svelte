<script>
    import Login from './lib/Login.svelte';
    
    let showLogin = false;
    
    function openLogin() {
        showLogin = true;
    }
    
    function closeLogin() {
        showLogin = false;
    }
</script>

<main>
    <h1>Welcome to BeeSharp</h1>
    <button onclick={openLogin}>Sign In</button>
    
    {#if showLogin}
        <Login onClose={closeLogin} isModal={true} />
    {/if}
</main>

<style>
    main {
        padding: 2rem;
        text-align: center;
    }
    
    button {
        background: #FFC857;
        color: #0a0a0a;
        border: none;
        padding: 0.75rem 1.5rem;
        border-radius: 6px;
        cursor: pointer;
        font-weight: 600;
    }
    
    button:hover {
        background: #e6b84f;
    }
</style>
