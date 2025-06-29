<script>
    import Router from 'svelte-spa-router';
    import LandingPage from './lib/LandingPage.svelte';
    import Login from './lib/Login.svelte';
    import Signup from './routes/Signup.svelte';
    import HomePage from './routes/HomePage.svelte';
    import ProfilePage from './routes/ProfilePage.svelte';
    import AlbumsOverview from './routes/AlbumsOverview.svelte';
    import TopPage from './routes/TopPage.svelte';
    import Album from './routes/Album.svelte';

    const routes = {
        '/': LandingPage,
        '/login': Login,
        '/criar-conta': Signup,
        '/albuns':  AlbumsOverview,
        '/top100': TopPage,
        '/perfil': ProfilePage,
        '/profile/:username': ProfilePage,
        '/album/:id': Album
    };
</script>

<Router {routes} />