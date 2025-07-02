<script>
    import { Award, ArrowLeft, Star, Music } from 'lucide-svelte'
    import { push } from 'svelte-spa-router'
    import { onMount, afterUpdate } from 'svelte'
    import Login from '../lib/Login.svelte'
    
    let showLoginModal = false
    function openLoginModal() { showLoginModal = true }
    function closeLoginModal() { showLoginModal = false }
    
    let topRatedAlbums = []
    let loading = true
    let error = ''
    
    function handleAlbumClick(album) {
        console.log('Album clicked:', album.title)
        push(`/album/${album.id}`)
    }
    
    // Add artist click function
    function handleArtistClick(artist) {
        console.log('Artist clicked:', artist.name)
        push(`/artist/${artist.id}`)
    }

    function renderStars(rating) {
        let safeRating = Number(rating)
        if (isNaN(safeRating) || safeRating < 0) safeRating = 0
        if (safeRating > 5) safeRating = 5

        const fullStars = Math.floor(safeRating)
        const hasHalfStar = safeRating % 1 !== 0
        const emptyStars = 5 - Math.ceil(safeRating)

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
        // Fetch top 100 albums
        (async () => {
            try {
                const response = await fetch('http://localhost:8080/albums/top-rated?limit=100')
                if (!response.ok) throw new Error('Erro ao buscar top álbuns')
                topRatedAlbums = await response.json()
            } catch (e) {
                error = e.message
                console.error('Error fetching top albums:', e)
                // Mock data for demonstration
                topRatedAlbums = Array.from({ length: 100 }, (_, i) => ({
                    id: i + 1,
                    title: `Album ${i + 1}`,
                    artist: `Artista ${i + 1}`,
                    image: '/imagemm.jpg',
                    averageRating: (Math.random() * 2 + 3).toFixed(1),
                    year: 2000 + Math.floor(Math.random() * 24)
                }))
            } finally {
                loading = false
                setTimeout(adjustTitleClasses, 100);
            }
        })();

        const handleResize = () => {
            setTimeout(adjustTitleClasses, 50);
        };
        window.addEventListener('resize', handleResize);

        return () => {
            window.removeEventListener('resize', handleResize);
        };
    });

    afterUpdate(() => {
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

<div class="top-page">
    <main class="main-content">
        <div class="container">
            <!-- Page Header -->
            <section class="page-header">
                <div class="section-title top-albums-title">
                    <Award size={32} />
                    <h1>Top 100 Álbuns Mais Bem Avaliados</h1>
                </div>
                <p class="page-subtitle">
                    Descubra os álbuns com as melhores avaliações da nossa comunidade
                </p>
            </section>

            <!-- Top Albums Grid -->
            <section class="albums-section">
                {#if loading}
                    <div class="loading-state">
                        <p>Carregando top 100 álbuns...</p>
                    </div>
                {:else if error && topRatedAlbums.length === 0}
                    <div class="error-state">
                        <p style="color:red">{error}</p>
                    </div>
                {:else}
                    <div class="albums-grid">
                        {#each topRatedAlbums as album, index (album.id)}
                            <button class="album-card top-rated" onclick={() => handleAlbumClick(album)}>
                                <div class="album-cover">
                                    <img src={album.image || '/imagemm.jpg'} alt={`Capa do álbum ${album.title}`} />
                                    <div class="album-overlay">
                                        <div class="play-button">
                                            <Music size={24} />
                                        </div>
                                    </div>
                                    <div class="ranking-badge">
                                        #{index + 1}
                                    </div>
                                </div>
                                <div class="album-info">
                                    <h3 class="album-title">{album.title}</h3>
                                    <p class="album-artist">
                                      <span class="artist-link" onclick={(e) => {e.stopPropagation(); handleArtistClick({id: album.id, name: album.artist})}}>
                                        {album.artist}
                                      </span>
                                    </p>
                                    <div class="album-rating">
                                        <div class="stars-container">
                                            {#each renderStars(Number(album.averageRating)).fullStars as _}
                                                <Star size={14} class="star-filled" />
                                            {/each}
                                            {#if renderStars(Number(album.averageRating)).hasHalfStar}
                                                <div class="star-half">
                                                    <Star size={14} class="star-empty" />
                                                    <div class="star-half-fill">
                                                        <Star size={14} class="star-filled" />
                                                    </div>
                                                </div>
                                            {/if}
                                            {#each renderStars(Number(album.averageRating)).emptyStars as _}
                                                <Star size={14} class="star-empty" />
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

    .top-page {
        min-height: 100vh;
        background-color: #14181c;
        padding-top: 80px;
    }

    .container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 1rem;
    }

    /* Navbar - identical to AlbumsOverview */
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
        background: #255F85;
        height: 80px;
        min-height: 80px;
        box-shadow: 0 4px 15px rgba(37, 95, 133, 0.3);
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

    /* Main Content */
    .main-content {
        padding: 2rem 0 3rem 0;
    }

    /* Page Header */
    .page-header {
        margin-bottom: 3rem;
        text-align: center;
    }

    .section-title {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 1rem;
        margin-bottom: 1rem;
    }

    .top-albums-title :global(svg) {
        color: #FFC857;
        fill: #FFC857;
    }

    .section-title h1 {
        margin: 0;
        font-size: 2.5rem;
        font-weight: 700;
        color: white;
        font-family: 'Familjen Grotesk', sans-serif;
    }

    .page-subtitle {
        font-size: 1.125rem;
        color: #9ca3af;
        margin: 0;
        font-weight: 400;
    }

    /* Albums Section */
    .albums-section {
        margin-bottom: 4rem;
    }

    .loading-state, .error-state {
        text-align: center;
        padding: 4rem 0;
        font-size: 1.125rem;
    }

    /* Albums Grid - identical to AlbumsOverview */
    .albums-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
        gap: 2rem;
    }

    /* Album Cards - identical to AlbumsOverview */
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

    .ranking-badge {
        position: absolute;
        top: 0.5rem;
        left: 0.5rem;
        background: #C5283D;
        color: white;
        padding: 0.25rem 0.5rem;
        border-radius: 12px;
        font-size: 0.75rem;
        font-weight: 700;
        min-width: 24px;
        text-align: center;
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

    /* Star Rating Styles - identical to AlbumsOverview */
    :global(.star-filled) {
        fill: #FFC857;
        color: #FFC857;
    }

    :global(.star-empty) {
        fill: none;
        color: #FFC857;
        stroke: #FFC857;
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

    /* Footer - identical to AlbumsOverview */
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

    /* Responsive Design */
    @media (max-width: 768px) {
        .section-title h1 {
            font-size: 2rem;
        }
        
        .albums-grid {
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 1.5rem;
        }
    }

    @media (max-width: 480px) {
        .section-title {
            flex-direction: column;
            gap: 0.5rem;
        }
        
        .section-title h1 {
            font-size: 1.75rem;
            text-align: center;
        }
        
        .albums-grid {
            grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
        }
    }
    
    /* Artist Link Styles */
    .artist-link {
        background: none;
        border: none;
        color: #9ca3af;
        cursor: pointer;
        font-size: inherit;
        font-family: inherit;
        padding: 0;
        text-decoration: none;
        transition: color 0.2s ease;
    }

    .artist-link:hover {
        color: #FFC857;
        text-decoration: underline;
            gap: 1rem;
    }
</style>