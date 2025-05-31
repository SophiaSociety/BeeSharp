<script>
    import Router from 'svelte-spa-router';
    import LandingPage from './lib/LandingPage.svelte';
    import Login from './lib/Login.svelte';
    import Signup from './routes/Signup.svelte';
    import HomePage from './routes/HomePage.svelte';

    const routes = {
        '/': LandingPage,
        '/login': Login,
        '/criar-conta': Signup,
        '/albuns': HomePage
    };
</script>

<Router {routes} />