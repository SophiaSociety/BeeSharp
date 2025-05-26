<style>
    :global(html, body) {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
        overflow: auto;
        background-color: #1C1C1C;

        background-image: linear-gradient(180deg,rgba(0, 0, 0, 0) 0%, rgba(28, 28, 28, 1) 60%),  
        linear-gradient(rgba(28,28,28,0.05), rgba(28,28,28,0.5)), url('/imagemm.jpg');

        background-size: 100% 100%, 100% 100%, min(100vw, 2000px);    /* altura = 100vh, largura proporcional */
        background-repeat: no-repeat;
        background-attachment: scroll;   /* rola junto com o conteúdo */
        background-position: center center, center center, center -400px;
    }

    .landing-page {
        position: relative;
        z-index: 0;
        min-height: 120vh;
        display: flex;
        flex-direction: column;
    }

    .navbar-landing {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        z-index: 100;
        display: flex;
        justify-content: space-between;
        align-items:center;
        padding: 0 1.5rem;
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

    .hero {
        position: relative;
        margin-top: 430px;
        left: 0;
        width: 100%;
        text-align: center;
    }

    .footer {
        padding: 1rem;
        text-align: center;
        background-color: #1C1C1C;
        margin-top: auto;
    }

    .btn-primary {
        background-color: #C5283D;
        margin-top: 20px;
        width: 200px;
        height: 40px;
        font-family: 'Familjen Grotesk';
        font-size: 22px;
        text-align: center;
        border: none;
        cursor: pointer;
        padding: 0;
        line-height: 40px;
    }

    .subheading {
        margin-top: 25px;
        font-family: 'Roboto';
        font-weight: 500;
        font-size: 20px;
        color: #797676;
        letter-spacing: -0.05em;
    }

    h1.heading {
        font-family: 'Averia Serif Libre', serif;
        font-weight: bold;
        font-size: 34px;
        letter-spacing: -0.05em;
    }

    p {
        font-family:'Inter';
        color: #797676;
        font-size: 13px;
    }
 
</style>


<div class="landing-page">
    <nav class="navbar-landing">
        <div class="navbar-container">
            <div class="logo-component">
                <img src="/logocomtexto.png" alt="BeeSharp Logo" />

            </div>
            <div class="nav-links">
                <a href="/albuns">ÁLBUNS</a>
                <a href="/criar-conta">CRIAR CONTA</a>
                <a href="/login">LOGIN</a>
            </div>
        </div>
    </nav>

    <main class="hero">
        <div class="hero-content">
            <h1 class="heading">
                Guarde os álbuns que você já ouviu.<br />
                Descubra novos sons que combinam com você.<br />
                Compartilhe seus favoritos com seus amigos.<br />
            </h1>

            <button class="btn-primary">Entre agora :)</button>
            <p class="subheading">Para todos que amam música.</p>
        </div>
    </main>

    <footer class="footer">
        <p>© BeeSharp Limited. Feito por estudantes do IME USP apaixonados por música. 
            Dados da API do Spotify.</p>
    </footer>
</div>