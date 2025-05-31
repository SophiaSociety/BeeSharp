<nav class="navbar-homepage">
    <div class="navbar-container">
        <div class="logo-component">
            <img src="/logocomtexto.png" alt="BeeSharp Logo" />
        </div>
        <div class="nav-links">
            <a href="/perfil">MEU PERFIL</a>
            <a href="/albuns">MEUS ÁLBUNS</a>
            <a href="/amigos">AMIGOS</a>
        </div>
    </div>
</nav>

<h2 class="carousel-title">Recomendações para você</h2>

<script>
    let albuns = [
        { titulo: "Álbum 1", artista: "Artista 1", capa: "/capa1.jpg" },
        { titulo: "Álbum 2", artista: "Artista 2", capa: "/capa2.jpg" },
        { titulo: "Álbum 3", artista: "Artista 3", capa: "/capa3.jpg" },
        { titulo: "Álbum 4", artista: "Artista 4", capa: "/capa4.jpg" },
        { titulo: "Álbum 5", artista: "Artista 5", capa: "/capa5.jpg" },
        { titulo: "Álbum 6", artista: "Artista 6", capa: "/capa6.jpg" },
        // ...adicione mais se quiser
    ];
    let current = 0;
    const visible = 4;

    function next() {
        if (current + visible < albuns.length) current++;
    }
    function prev() {
        if (current > 0) current--;
    }
</script>

<div class="carousel">
    <button on:click={prev} disabled={current === 0}>&lt;</button>
    <div class="carousel-track">
        {#each albuns.slice(current, current + visible) as album}
            <div class="album">
                <img src={album.capa} alt="Capa do álbum" />
                <h3>{album.titulo}</h3>
                <p>{album.artista}</p>
            </div>
        {/each}
    </div>
    <button on:click={next} disabled={current + visible >= albuns.length}>&gt;</button>
</div>

<style>
    :global(html, body) {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
        overflow: auto;
        background-color: #1C1C1C;
        background-size: 100% 100%, 100% 100%, min(100vw, 2000px);
        background-image: none !important;
        background-repeat: no-repeat;
        background-attachment: scroll;
        background-position: center center, center center, center -400px;
    }

    .navbar-homepage {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        z-index: 100;
        display: flex;
        justify-content: space-between;
        align-items:center;
        padding: 0 1.5rem;
        background: #481D24;
    }

    .navbar-container {
        width: 1500px;
        margin: 0 auto;
        display: flex;
        justify-content:space-between;
        align-items: center;
    }

    .logo-component img {
        height: 72px;
        cursor: pointer;
    }

    .nav-links a {
        margin-left: 1.5rem;
        margin-right: 3.5rem;
        color: white;
        font-family: 'Familjen Grotesk', sans-serif;
        font-weight: bold;
        font-size: 16px;
        letter-spacing: 0.1em;
        text-decoration: none;
        white-space: nowrap;
        overflow-x: auto;
    }
    .carousel {
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 6rem 0 2rem 0;
        gap: 2rem;
    }
    .carousel-track {
        display: flex;
        gap: 2rem;
    }
    .album {
        background: #fff;
        border-radius: 16px;
        padding: 1rem 2rem;
        box-shadow: 0 2px 12px #0002;
        text-align: center;
        min-width: 220px;
        max-width: 220px;
    }
    .album img {
        width: 180px;
        height: 180px;
        object-fit: cover;
        border-radius: 12px;
        margin-bottom: 1rem;
    }
    .carousel button {
        background: #481D24;
        color: #fff;
        border: none;
        border-radius: 50%;
        width: 40px;
        height: 40px;
        font-size: 1.5rem;
        cursor: pointer;
        transition: background 0.2s;
    }
    .carousel button:disabled {
        opacity: 0.4;
        cursor: not-allowed;
    }
    .carousel button:hover:enabled {
        background: #C5283D;
    }
    .carousel-title {
        color: #fff;
        font-family: 'Familjen Grotesk', sans-serif;
        font-size: 2rem;
        font-weight: bold;
        margin-top: 7rem;
        margin-bottom: 2rem;
        text-align: center;
        letter-spacing: 0.05em;
    }
</style>

