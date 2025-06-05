<script>
    import Router from 'svelte-spa-router';
    import LandingPage from './lib/LandingPage.svelte';
    import Login from './routes/Login.svelte';
    import Signup from './routes/Signup.svelte';
    import UserProfile from './routes/UserProfile.svelte';

    const routes = {
        '/': LandingPage,
        '/login': Login,
        '/criar-conta': Signup,
        '/:username': UserProfile
    };
</script>

<Router {routes} />