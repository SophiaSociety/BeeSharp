<script>
    import Router from 'svelte-spa-router';
    import LandingPage from './lib/LandingPage.svelte';
    import Login from './routes/Login.svelte';
    import Signup from './routes/Signup.svelte';

    const routes = {
        '/': LandingPage,
        '/login': Login,
        '/criar-conta': Signup
    };
</script>

<Router {routes} />