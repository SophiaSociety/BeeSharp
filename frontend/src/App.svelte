<script>
    import Router from 'svelte-spa-router';
    import LandingPage from './lib/LandingPage.svelte';
    import Login from './lib/Login.svelte';
    import Signup from './routes/Signup.svelte';
    import HomePage from './routes/HomePage.svelte';
    import ProfilePage from './routes/ProfilePage.svelte';

    const routes = {
        '/': LandingPage,
        '/login': Login,
        '/criar-conta': Signup,
        '/albuns': HomePage,
        '/perfil': ProfilePage
    };
</script>

<Router {routes} />