<script>
    import { Music, Search, ChevronLeft, ChevronRight, Star, TrendingUp, Award, ArrowRight, Flame } from 'lucide-svelte'
    import { push } from 'svelte-spa-router'
    import { onMount, afterUpdate } from 'svelte'
    import Login from '../lib/Login.svelte'
    let searchQuery = ''
    let currentCarouselIndex = 0
    let showLoginModal = false
    function openLoginModal() { showLoginModal = true }
    function closeLoginModal() { showLoginModal = false }
    
    let hotAlbums = []
    let topRatedAlbums = []
    let loadingHot = true
    let loadingTop = true
    let errorHot = ''
    let errorTop = ''
    
    let searchResults = []
    let searching = false
    let searchError = ''
    let showResults = false

    async function handleSearch(event) {
        event.preventDefault()
        searching = true
        searchError = ''
        showResults = false
        searchResults = []

        try {
            const [albumsRes, usersRes, artistsRes] = await Promise.all([
                fetch(`http://localhost:8080/search/albums?query=${encodeURIComponent(searchQuery)}`),
                fetch(`http://localhost:8080/search/users?query=${encodeURIComponent(searchQuery)}`),
                fetch(`http://localhost:8080/search/artists?query=${encodeURIComponent(searchQuery)}`)
            ])

            if (!albumsRes.ok || !usersRes.ok || !artistsRes.ok) {
                throw new Error('Erro ao buscar resultados')
            }

            const [albums, users, artists] = await Promise.all([
                albumsRes.json(),
                usersRes.json(),
                artistsRes.json()
            ])

            // Marque cada tipo para renderização
            const albumResults = albums.map(a => ({ ...a, _type: 'album' }))
            const userResults = users.map(u => ({ ...u, _type: 'user' }))
            const artistResults = artists.map(ar => ({ ...ar, _type: 'artist' }))

            searchResults = [...albumResults, ...artistResults, ...userResults]
            showResults = true
        } catch (e) {
            searchError = e.message
        } finally {
            searching = false
        }
    }
    
    function nextCarousel() {
        currentCarouselIndex = (currentCarouselIndex + 1) % Math.max(1, hotAlbums.length - 4)
    }
    
    function prevCarousel() {
        currentCarouselIndex = currentCarouselIndex === 0 ? Math.max(0, hotAlbums.length - 5) : currentCarouselIndex - 1
    }
    
    function handleAlbumClick(album) {
        console.log('Album clicked:', album.title)
        // Navigate to album detail page
    }
    
    function handleViewAllClick() {
        console.log('View all top albums clicked')
        // Navigate to top 100 albums page
    }
    
    function renderStars(rating) {
        const fullStars = Math.floor(rating)
        const hasHalfStar = rating % 1 !== 0
        const emptyStars = 5 - Math.ceil(rating)
        
        return {
            fullStars: Array(fullStars).fill(0),
            hasHalfStar,
            emptyStars: Array(emptyStars).fill(0)
        }
    }

    // Função para ajustar dinamicamente o tamanho da fonte dos títulos
    function adjustTitleClasses() {
        const titleElements = document.querySelectorAll('.album-title');
        
        titleElements.forEach((element) => {
            const title = element;
            
            // Remove classes existentes e reset styles
            title.classList.remove('single-line', 'multi-line');
            title.removeAttribute('style');
            
            const containerWidth = title.clientWidth;
            const text = title.textContent;
            
            // Se não há texto ou largura, pula
            if (!text || containerWidth <= 0) return;
            
            // Configurações
            const maxFontSize = 1.05; // Fonte máxima
            const minFontSize = 0.78; // Fonte mínima
            const step = 0.02; // Passo para ajuste fino
            let bestFontSize = maxFontSize;
            let canFitInOneLine = false;
            
            // Cria elemento de teste uma vez
            const tempElement = document.createElement('div');
            tempElement.style.position = 'absolute';
            tempElement.style.visibility = 'hidden';
            tempElement.style.whiteSpace = 'nowrap';
            tempElement.style.fontFamily = getComputedStyle(title).fontFamily;
            tempElement.style.padding = '0';
            tempElement.style.margin = '0';
            tempElement.style.border = 'none';
            tempElement.textContent = text;
            
            document.body.appendChild(tempElement);
            
            // Busca binária para encontrar o melhor tamanho de fonte
            let low = minFontSize;
            let high = maxFontSize;
            
            while (high - low > step) {
                const mid = (low + high) / 2;
                tempElement.style.fontSize = mid + 'rem';
                tempElement.style.fontWeight = '700';
                tempElement.style.lineHeight = '1.2';
                
                const textWidth = tempElement.scrollWidth;
                
                if (textWidth <= containerWidth * 0.98) { // Margem de segurança de 2%
                    low = mid;
                    bestFontSize = mid;
                    canFitInOneLine = true;
                } else {
                    high = mid;
                }
            }
            
            document.body.removeChild(tempElement);
            
            if (canFitInOneLine && bestFontSize >= 0.85) {
                // Aplica a maior fonte que cabe em uma linha
                title.classList.add('single-line');
                title.setAttribute('style', `font-size: ${bestFontSize}rem; font-weight: 700; line-height: 1.2;`);
            } else {
                // Não cabe em uma linha adequadamente, usa duas linhas
                title.classList.add('multi-line');
                title.setAttribute('style', 'font-size: 0.78rem; font-weight: 600; line-height: 1.1;');
            }
        });
    }

    onMount(() => {
        // Async logic separated from cleanup logic
        (async () => {
            // 1. Buscar álbuns em alta
            try {
                const resHot = await fetch('http://localhost:8080/albums/hot')
                if (!resHot.ok) throw new Error('Erro ao buscar álbuns em alta')
                hotAlbums = await resHot.json()
            } catch (e) {
                errorHot = e.message
            } finally {
                loadingHot = false
            }

            // 2. Buscar top-rated
            try {
                const resTop = await fetch('http://localhost:8080/albums/top-rated')
                if (!resTop.ok) throw new Error('Erro ao buscar top álbuns')
                topRatedAlbums = await resTop.json()
            } catch (e) {
                errorTop = e.message
            } finally {
                loadingTop = false
            }

            // 3. Pequeno delay para garantir que DOM está pronto
            setTimeout(adjustTitleClasses, 100);
        })();

        // 4. Reajustar quando a janela for redimensionada
        const handleResize = () => {
            setTimeout(adjustTitleClasses, 50);
        };
        window.addEventListener('resize', handleResize);

        // 5. Cleanup
        return () => {
            window.removeEventListener('resize', handleResize);
        };
    });

    afterUpdate(() => {
        // Adjust classes after any updates
        setTimeout(adjustTitleClasses, 50);
    });
</script>

<nav class="navbar-albums">
    <div class="navbar-container">
        <div class="logo-component">
            <button class="logo-button" onclick={() => push('/')} aria-label="Ir para página inicial">
                <img src="/logocomtexto.png" alt="BeeSharp Logo" />
            </button>
        </div>
        <div class="nav-links">
            <a href="/criar-conta" onclick={(e) => { e.preventDefault(); push('/criar-conta') }}>CRIAR CONTA</a>
            <button class="nav-login-btn" onclick={(e) => { e.preventDefault(); openLoginModal() }}>LOGIN</button>
        </div>
    </div>
</nav>

{#if showLoginModal}
    <div class="login-popup-overlay">
        <Login onClose={closeLoginModal} isModal={true} />
    </div>
{/if}

<div class="overview-page">
    <!-- Hero Section with Search -->
    <section class="hero-section">
        <div class="container">
        <div class="hero-content">
            <div class="hero-title-container">
                <h1 class="hero-title" data-text="Descubra seus favoritos!">Descubra seus favoritos!</h1>
                <img src="/logo.png" alt="BeeSharp Logo" class="hero-logo">
            </div>
            <p class="hero-subtitle">
            Explore, avalie e resenhe álbuns de todos os gêneros e épocas. Junte-se à comunidade de amantes da música.
            </p>
            
            <form class="search-form" onsubmit={handleSearch}>
            <div class="search-container">
                <Search size={20} />
                <input
                type="text"
                class="search-input"
                placeholder="Busque por álbuns ou artistas :)"
                bind:value={searchQuery}
                />
                <button type="submit" class="search-button">
                Buscar
                </button>
            </div>
            </form>
            {#if searching}
                <div class="search-results-box">
                    <p>Buscando...</p>
                </div>
            {:else if searchError}
                <div class="search-results-box">
                    <p style="color:red">{searchError}</p>
                </div>
            {:else if showResults}
                <div class="search-results-box">
                    {#if searchResults.length === 0}
                        <p>Nenhum resultado encontrado.</p>
                    {:else}
                        <ul class="search-results-list">
                            {#each searchResults as result (result._type + '-' + result.id)}
                                <li class="search-result-item">
                                    {#if result._type === 'album'}
                                        <a href={"/album/" + result.id} class="result-link">
                                            <img class="result-img" src={result.image || "/placeholder-album.svg"} alt="Capa do álbum" />
                                            <span class="result-title">{result.title}</span>
                                            <span class="result-type">Álbum</span>
                                        </a>
                                    {:else if result._type === 'artist'}
                                        <a href={"/artist/" + result.id} class="result-link">
                                            <img class="result-img" src={result.photo || "/placeholder-artist.svg"} alt="Foto do artista" />
                                            <span class="result-title">{result.name}</span>
                                            <span class="result-type">Artista</span>
                                        </a>
                                    {:else if result._type === 'user'}
                                        <a href={"/" + result.username} class="result-link">
                                            <img class="result-img" src={result.profileImageUrl || "/placeholder-user.svg"} alt="Foto do usuário" />
                                            <span class="result-title">{result.username}</span>
                                            <span class="result-type">Usuário</span>
                                        </a>
                                    {/if}
                                </li>
                            {/each}
                        </ul>
                    {/if}
                </div>
            {/if}
        </div>
        </div>
    </section>

    <main class="main-content">
        <div class="container">
            <!-- Hot Albums Carousel -->
            <section class="section">
                <div class="section-header">
                    <div class="section-title hot-albums-title">
                        <Flame size={24} />
                        <h2>Álbuns em Alta</h2>
                    </div>
                    <div class="carousel-controls">
                        <button class="carousel-btn" onclick={prevCarousel} aria-label="Álbuns anteriores">
                            ‹
                        </button>
                        <button class="carousel-btn" onclick={nextCarousel} aria-label="Próximos álbuns">
                            ›
                        </button>
                    </div>
                </div>

                <div class="carousel-container">
                    {#if loadingHot}
                        <p>Carregando álbuns em alta...</p>
                    {:else if errorHot}
                        <p style="color:red">{errorHot}</p>
                    {:else}
                        <div class="carousel-track" style="transform: translateX(-{currentCarouselIndex * 25}%)">
                            {#each hotAlbums as album (album.id)}
                                <div class="carousel-item">
                                    <button class="album-card hot-album" onclick={() => handleAlbumClick(album)}>
                                        <div class="album-cover">
                                            <img src={album.image || "/placeholder.svg"} alt="{album.title} by {album.artist}" />
                                            <div class="album-overlay">
                                                <div class="play-button">
                                                    <Music size={24} />
                                                </div>
                                            </div>
                                            <div class="trending-badge">
                                                <TrendingUp size={14} />
                                            </div>
                                        </div>
                                        <div class="album-info">
                                            <h3 class="album-title">{album.title}</h3>
                                            <p class="album-artist">{album.artist}</p>
                                            <div class="album-rating">
                                                <div class="stars-container">
                                                    {#each renderStars(album.averageRating).fullStars as _}
                                                        <Star size={12} class="star-filled" />
                                                    {/each}
                                                    {#if renderStars(album.averageRating).hasHalfStar}
                                                        <div class="star-half">
                                                            <Star size={12} class="star-empty" />
                                                            <div class="star-half-fill">
                                                                <Star size={12} class="star-filled" />
                                                            </div>
                                                        </div>
                                                    {/if}
                                                    {#each renderStars(album.averageRating).emptyStars as _}
                                                        <Star size={12} class="star-empty" />
                                                    {/each}
                                                </div>
                                                <span class="rating-text">{album.averageRating}</span>
                                            </div>
                                        </div>
                                    </button>
                                </div>
                            {/each}
                        </div>
                    {/if}
                </div>
            </section>

            <!-- Top Rated Albums -->
            <section class="section">
                <div class="section-header">
                    <div class="section-title top-albums-title">
                        <Award size={24} />
                        <h2>Top Álbuns</h2>
                    </div>
                    <button class="view-all-btn" onclick={handleViewAllClick}>
                        Ver Tudo
                        <ArrowRight size={16} />
                    </button>
                </div>

                {#if loadingTop}
                    <p>Carregando top álbuns...</p>
                {:else if errorTop}
                    <p style="color:red">{errorTop}</p>
                {:else}
                    <div class="albums-grid">
                        {#each topRatedAlbums as album (album.id)}
                            <button class="album-card top-rated" onclick={() => handleAlbumClick(album)}>
                                <div class="album-cover">
                                    <img src={album.image || "/placeholder.svg"} alt="{album.title} by {album.artist}" />
                                    <div class="album-overlay">
                                        <div class="rating-badge">
                                            <Star size={16} class="star-filled" />
                                            <span>{album.averageRating}</span>
                                        </div>
                                    </div>
                                </div>
                                <div class="album-info">
                                    <h3 class="album-title">{album.title}</h3>
                                    <p class="album-artist">{album.artist}</p>
                                    <div class="album-rating">
                                        <div class="stars-container">
                                            {#each renderStars(album.averageRating).fullStars as _}
                                                <Star size={12} class="star-filled" />
                                            {/each}
                                            {#if renderStars(album.averageRating).hasHalfStar}
                                                <div class="star-half">
                                                    <Star size={12} class="star-empty" />
                                                    <div class="star-half-fill">
                                                        <Star size={12} class="star-filled" />
                                                    </div>
                                                </div>
                                            {/if}
                                            {#each renderStars(album.averageRating).emptyStars as _}
                                                <Star size={12} class="star-empty" />
                                            {/each}
                                        </div>
                                        <span class="rating-text">{album.averageRating}</span>
                                    </div>
                                </div>
                            </button>
                        {/each}
                    </div>
                {/if}
            </section>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>© BeeSharp Limited. Feito por estudantes do IME USP apaixonados por música. 
            Dados da API do Spotify.</p>
    </footer>
</div>

<style>
    /* Global Styles */
    :global(body) {
        margin: 0;
        padding: 0;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        background-color: #14181c;
        color: white;
        display: block !important;
        place-items: unset !important;
    }

    :global(#app) {
        max-width: none !important;
        margin: 0 !important;
        padding: 0 !important;
        text-align: left !important;
    }

    .overview-page {
        min-height: 100vh;
        background-color: #14181c;
    }

    .container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 1rem;
    }

    /* Hero Section */
    .hero-section {
        background: #255F85;
        padding: 4rem 0;
        text-align: center;
        position: relative;
        overflow: hidden;
    }

    .hero-section::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: 
            linear-gradient(45deg, 
                transparent 0%, 
                rgba(255, 255, 255, 0.05) 25%, 
                rgba(255, 255, 255, 0.1) 50%, 
                rgba(255, 255, 255, 0.05) 75%, 
                transparent 100%),
            radial-gradient(circle at 25% 25%, rgba(255, 200, 87, 0.06) 0%, transparent 60%),
            radial-gradient(circle at 75% 75%, rgba(255, 255, 255, 0.04) 0%, transparent 60%);
        background-size: 200% 200%, 100% 100%, 100% 100%;
        animation: wave 5s ease-in-out infinite;
        z-index: 1;
    }

    .hero-content {
        max-width: 800px;
        margin: 0 auto;
        position: relative;
        z-index: 2;
    }

    .hero-title-container {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 1.5rem;
        margin-bottom: 0.5rem;
        flex-wrap: wrap;
    }

    .hero-logo {
        height: 5rem;
        width: auto;
        object-fit: contain;
        transition: transform 0.3s ease;
    }

    .hero-logo:hover {
        transform: scale(1.05);
    }

    .hero-title {
        font-size: 3.5rem;
        font-weight: 900;
        margin: 0;
        color: #ffffff;
        font-family: 'Familjen Grotesk', sans-serif;
        position: relative;
        text-align: center;
        letter-spacing: -0.025em;
        text-shadow: 0 2px 3px rgba(0, 0, 0, 0.4);
        line-height: 1.1;
        flex-shrink: 0;
    }

    .hero-subtitle {
        font-size: 1.3rem;
        color: rgba(255, 255, 255, 0.95);
        margin: 0 0 3rem 0;
        line-height: 1.6;
        font-weight: 400;
        position: relative;
        display: inline-block;
        padding: 0.5rem 0;
        font-family: 'Familjen Grotesk', sans-serif;
        max-width: 600px;
        margin-left: auto;
        margin-right: auto;
    }

    .search-form {
        margin-bottom: 2rem;
    }

    .search-container {
        position: relative;
        max-width: 600px;
        margin: 0 auto;
        display: flex;
        align-items: center;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 20px;
        border: 3px solid rgba(255, 255, 255, 0.3);
        transition: all 0.3s ease;
        padding-left: 1rem;
        backdrop-filter: blur(20px);
        box-shadow: 
            0 8px 32px rgba(0, 0, 0, 0.3),
            inset 0 1px 0 rgba(255, 255, 255, 0.2);
        
    }

    .search-container :global(svg) {
        color: rgba(255, 255, 255, 0.7);
        margin-right: 0.75rem;
        flex-shrink: 0;
    }

    .search-container:focus-within {
        border-color: rgba(255, 255, 255, 0.5);
        box-shadow: 
            0 8px 32px rgba(0, 0, 0, 0.4),
            0 0 20px rgba(255, 255, 255, 0.1),
            inset 0 1px 0 rgba(255, 255, 255, 0.3);
        background: rgba(255, 255, 255, 0.15);
    }

    .search-input {
        flex: 1;
        padding: 1rem;
        background: transparent;
        border: none;
        color: white;
        font-size: 1rem;
        outline: none;
    }

    .search-input::placeholder {
        color: rgba(255, 255, 255, 0.6);
        font-family: 'Roboto', sans-serif;
        font-weight: 500;
    }

    .search-button {
        padding: 1rem 2rem;
        background: #255F85;
        color: white;
        border: none;
        border-radius: 0 17px 17px 0;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s ease;
        font-family: 'Familjen Grotesk', sans-serif;
        letter-spacing: 0.025em;
        outline: none;
    }

    .search-button:hover {
        background: #1e4c6b;
    }

    .search-button:focus {
        outline: none;
        background: #1e4c6b;
    }

    .search-results-box {
        background: #23272f;
        border-radius: 12px;
        box-shadow: 0 4px 16px rgba(0,0,0,0.18);
        margin: 1rem auto 2rem auto;
        max-width: 600px;
        padding: 1.5rem;
        color: white;
        z-index: 10;
        position: relative;
    }

    .search-results-list {
        list-style: none;
        padding: 0;
        margin: 0;
    }

    .search-result-item {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 0.5rem 0;
        border-bottom: 1px solid #333;
    }

    .result-img {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        object-fit: cover;
        background: #444;
    }

    .result-title {
        font-weight: 600;
        color: #fff;
    }

    .result-type {
        margin-left: auto;
        font-size: 0.85rem;
        color: #FFC857;
        font-weight: 500;
    }

    .result-link {
        display: flex;
        align-items: center;
        gap: 1rem;
        text-decoration: none;
        color: inherit;
        width: 100%;
    }
    .result-link:hover .result-title {
        text-decoration: underline;
        color: #FFC857;
    }

    /* Main Content */
    .main-content {
        padding: 3rem 0;
    }

    .section {
        margin-bottom: 4rem;
    }

    .section-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 2rem;
    }

    .section-title {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        color: #FFC857;
    }

    .section-title :global(svg) {
        color: #ff6b35;
    }

    /* Ícone específico para Álbuns em Alta - foguinho vermelho preenchido */
    .hot-albums-title :global(svg) {
        color: #dc2626;
        fill: #dc2626;
    }

    /* Ícone específico para Top Álbuns - medalha amarela */
    .top-albums-title :global(svg) {
        color: #FFC857;
        fill: #FFC857;
    }

    .section-title h2 {
        margin: 0;
        font-size: 1.75rem;
        font-weight: 700;
        color: white;
    }

    /* Carousel */
    .carousel-controls {
        display: flex;
        gap: 0.5rem;
    }

    .carousel-btn {
        width: 40px;
        height: 40px;
        background: #C5283D;
        border: none;
        border-radius: 20px;
        color: white;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s ease;
        font-size: 18px;
        font-weight: bold;
        font-family: 'Familjen Grotesk', sans-serif;
    }

    .carousel-btn:hover {
        background: #a02030;
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(197, 40, 61, 0.3);
    }

    .carousel-container {
        overflow: hidden;
        border-radius: 8px;
    }

    .carousel-track {
        display: flex;
        transition: transform 0.3s ease;
        gap: 1rem;
    }

    .carousel-item {
        flex: 0 0 calc(25% - 0.75rem);
        min-width: 0;
    }

    /* Album Cards */
    .album-card {
        background: none;
        border: none;
        cursor: pointer;
        text-align: left;
        width: 100%;
        transition: transform 0.2s ease;
    }

    .album-card:hover {
        transform: translateY(-4px);
    }

    .album-cover {
        position: relative;
        aspect-ratio: 1;
        margin-bottom: 0.75rem;
        overflow: hidden;
        border-radius: 8px;
        background-color: #374151;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }

    .album-cover img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.2s ease;
    }

    .album-card:hover .album-cover img {
        transform: scale(1.05);
    }

    .album-overlay {
        position: absolute;
        inset: 0;
        background: rgba(0, 0, 0, 0);
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s ease;
        color: white;
    }

    .album-card:hover .album-overlay {
        background: rgba(0, 0, 0, 0.6);
    }

    .play-button {
        background: #255F85;
        border-radius: 50%;
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        transform: scale(0);
        transition: transform 0.2s ease;
    }

    .album-card:hover .play-button {
        transform: scale(1);
    }

    .trending-badge {
        position: absolute;
        top: 0.5rem;
        right: 0.5rem;
        background: #C5283D;
        color: white;
        padding: 0.25rem 0.5rem;
        border-radius: 12px;
        font-size: 0.75rem;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 0.25rem;
    }

    .rating-badge {
        background: #255F85;
        color: white;
        padding: 0.25rem 0.5rem;
        border-radius: 12px;
        font-size: 0.75rem;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 0.25rem;
        transform: scale(0);
        transition: transform 0.2s ease;
    }

    .album-card:hover .rating-badge {
        transform: scale(1);
    }

    .album-info {
        padding: 0.5rem 0;
        display: flex;
        flex-direction: column;
        height: 5rem;
    }

    .album-title {
        font-size: 0.875rem;
        font-weight: 600;
        color: white;
        margin: 0 0 0.25rem 0;
        line-height: 1.3;
        display: -webkit-box;
        line-clamp: 2;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        height: auto;
        min-height: 2.1rem;
        max-height: 2.5rem;
        flex-shrink: 0;
        transition: all 0.2s ease;
    }

    /* Título de uma linha - ajuste dinâmico aplicado via JS */
    :global(.album-title.single-line) {
        margin-bottom: 0.15rem;
        -webkit-line-clamp: 1;
        line-clamp: 1;
    }

    /* Título de duas linhas - menor e mais compacto */
    :global(.album-title.multi-line) {
        margin-bottom: 0.1rem;
        -webkit-line-clamp: 2;
        line-clamp: 2;
    }

    .album-artist {
        font-size: 0.75rem;
        color: #9ca3af;
        margin: 0 0 0.5rem 0;
        line-height: 1.3;
        flex-shrink: 0;
    }

    .album-rating {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        margin-top: auto;
    }

    .stars-container {
        display: flex;
        align-items: center;
        gap: 1px;
    }

    .rating-text {
        font-size: 0.75rem;
        color: #9ca3af;
        font-weight: 500;
    }

    /* Albums Grid */
    .albums-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
        gap: 2rem;
    }

    .view-all-btn {
        background: #C5283D;
        border: none;
        color: white;
        padding: 0.75rem 1.5rem;
        border-radius: 20px;
        font-weight: bold;
        font-family: 'Familjen Grotesk', sans-serif;
        font-size: 16px;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        transition: all 0.2s ease;
        text-align: center;
    }

    .view-all-btn:hover {
        background: #a02030;
        transform: translateY(-1px);
        box-shadow: 0 4px 8px rgba(197, 40, 61, 0.3);
    }

    /* Stats Section */
    .stats-section {
        background: #1d232a;
        border-radius: 16px;
        padding: 3rem 2rem;
        margin: 4rem 0;
    }

    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1.5rem;
        align-items: center;
    }

    .stat-card {
        display: flex;
        align-items: center;
        gap: 1rem;
        text-align: left;
    }

    .stat-logo {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 0;
        margin: 0;
    }

    .stats-logo {
        height: 80px;
        width: auto;
        object-fit: contain;
        transition: transform 0.3s ease;
    }

    .stats-logo:hover {
        transform: scale(1.1) rotate(5deg);
    }

    .stat-icon {
        color: #FFC857;
        background: rgba(255, 200, 87, 0.1);
        padding: 1rem;
        border-radius: 12px;
    }

    .stat-number {
        font-size: 2rem;
        font-weight: 700;
        color: white;
        line-height: 1;
    }

    .stat-label {
        font-size: 0.875rem;
        color: #9ca3af;
        margin-top: 0.25rem;
    }


    /* Star Rating Styles */
    :global(.star-filled) {
        fill: #FFC857;
        color: #FFC857;
    }

    :global(.star-empty) {
        fill: none;
        color: #374151;
    }

    .star-half {
        position: relative;
        display: inline-flex;
        align-items: center;
        height: 14px;
    }

    .star-half-fill {
        position: absolute;
        top: 0;
        left: 0;
        overflow: hidden;
        height: 100%;
        display: flex;
        align-items: center;
        clip-path: inset(0 50% 0 0);
    }

    @keyframes wave {
        0% { background-position: 0% 0%; }
        50% { background-position: 100% 100%; }
        100% { background-position: 0% 0%; }
    }

    .navbar-albums {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        z-index: 100;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 1.5rem;
        background: #14181c;
        height: 80px;
        min-height: 80px;
        transition: all 0.3s ease;
        overflow: visible;
    }
    .navbar-container {
        width: 1500px;
        max-width: 100vw;
        margin: 0 auto;
        display: flex;
        justify-content: space-between;
        align-items: center;
        position: relative;
        z-index: 1;
    }
    .logo-component img {
        height: 72px;
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
        transition: opacity 0.2s ease;
    }
    
    .logo-button:hover {
        opacity: 0.8;
    }

    .nav-links {
        display: flex;
        align-items: center;
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
        transition: color 0.2s;
        position: relative;
        z-index: 2;
    }
    
    .nav-login-btn {
        margin-left: 1.5rem;
        margin-right: 3.5rem;
        background: none;
        border: none;
        color: white;
        font-family: 'Familjen Grotesk', sans-serif;
        font-weight: bold;
        font-size: 16px;
        letter-spacing: 0.1em;
        white-space: nowrap;
        cursor: pointer;
        transition: color 0.2s;
        position: relative;
        z-index: 2;
    }

    .overview-page {
        padding-top: 80px; 
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

    /* Responsividade para o hero-section */
    @media (max-width: 768px) {
        .hero-title-container {
            flex-direction: column;
            gap: 1rem;
        }

        .hero-title {
            font-size: 2.5rem;
        }

        .hero-logo {
            height: 3rem;
        }
    }

    @media (max-width: 480px) {
        .hero-title {
            font-size: 2rem;
        }

        .hero-logo {
            height: 2.5rem;
        }
    }
    /* ...existing code... */

    /* Footer */
    .footer {
        padding: 1rem;
        text-align: center;
        margin-top: 3rem;
        font-size: 0.75rem;
        color: #6b7280;
        background-color: #14181c;
    }

    .footer p {
        margin: 0;
    }
</style>