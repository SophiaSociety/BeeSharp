<script>
  import { Music, Star, Heart, MessageSquare, Plus, Calendar, User, TrendingUp, Disc, Search } from 'lucide-svelte'
  import { push } from 'svelte-spa-router'
  import Login from '../lib/Login.svelte'
  import { onMount } from 'svelte'
  import { getAuthToken, isTokenExpired, getUsername, logout } from '../lib/auth'

  // Props to receive artist ID from route
  export let params = {}
  
  // User authentication state
  let isLoggedIn = false
  let currentUser = ''
  let showUserMenu = false
  
  // Função para verificar se o usuário está logado
  function checkAuthStatus() {
    const token = getAuthToken();
    isLoggedIn = token && !isTokenExpired(token);
    if (isLoggedIn) {
      const username = getUsername();
      if (username) {
        currentUser = username;
      }
    }
  }
  
  // Função para fazer logout
  function handleLogout() {
    logout();
    isLoggedIn = false;
    currentUser = '';
    push('/');
  }
  
  // Função para toggle do menu do usuário
  function toggleUserMenu() {
    showUserMenu = !showUserMenu;
  }

  let showLoginModal = false
  function openLoginModal() { showLoginModal = true }
  function closeLoginModal() { showLoginModal = false }
  
  // Check login status when component mounts
  onMount(async () => {
    console.log('Artist component mounted with params:', params);

    // Check authentication first
    checkAuthStatus();
    if (!isLoggedIn) {
      showLoginModal = true;
    }
    
    // Continue with component initialization...
  })
  
  // Searchbar removida para manter consistência com Album.svelte

  // Artist data
  let artist = null
  let artistAlbums = []
  let loading = true
  let loadingAlbums = true
  let error = ''
  let albumsError = ''
  let sortedAlbums = []
  let paginatedAlbums = []
  
  // Paginação
  let currentPage = 1
  let albumsPerPage = 12
  let totalPages = 1
  
  $: {
    // Recalcula o número total de páginas sempre que a lista de álbuns mudar
    if (artistAlbums) {
      totalPages = Math.ceil(artistAlbums.length / albumsPerPage);
      if (currentPage > totalPages) currentPage = totalPages;
      if (currentPage < 1) currentPage = 1;
    }
  }
  
  // Ordenação dos álbuns por ano (mais novo para o mais antigo)
  $: sortedAlbums = [...artistAlbums].sort((a, b) => {
    // Garante que anos não numéricos ou undefined não causem erros na ordenação
    const yearA = parseInt(a.year) || 0;
    const yearB = parseInt(b.year) || 0;
    return yearB - yearA;
  });
  
  // Álbuns na página atual
  $: paginatedAlbums = sortedAlbums.slice(
    (currentPage - 1) * albumsPerPage,
    currentPage * albumsPerPage
  );
  
  // Referência para a seção de álbuns
  let albumsSection;
  
  function goToPage(page) {
    if (page >= 1 && page <= totalPages) {
      currentPage = page;
      // Usa a referência direta ao elemento em vez de querySelector
      if (albumsSection) {
        // Aguarda a próxima atualização do DOM antes de rolar
        setTimeout(() => {
          window.scrollTo({ 
            top: albumsSection.offsetTop - 100, 
            behavior: 'smooth' 
          });
        }, 10);
      }
    }
  }
  
  // Novo renderStars igual ao TopPage.svelte
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

  function handleAlbumClick(album) {
    push(`/album/${album.id}`)
  }

  function calculateAverageRating() {
    if (artistAlbums.length === 0) return 0
    const totalRating = artistAlbums.reduce((sum, album) => sum + album.averageRating, 0)
    return (totalRating / artistAlbums.length).toFixed(1)
  }

  function getTotalReviews() {
    return artistAlbums.reduce((sum, album) => sum + album.reviewsCount, 0)
  }
  
  // Função de navegação removida

  // Dados mockados para visualização no frontend
  const mockArtists = {
    1: {
      id: 1,
      name: "Taylor Swift",
      image: "/imagemm.jpg",
      bio: "Taylor Swift é uma cantora e compositora americana que se tornou uma das artistas mais influentes da sua geração. Começando com country e evoluindo para pop, seus álbuns incluem sucessos como 'Fearless', '1989', 'Lover', 'folklore' e 'Midnights'.",
      followers: 128000000,
      monthlyListeners: 85000000,
      popular: true
    },
    2: {
      id: 2,
      name: "Kendrick Lamar",
      image: "/imagemm.jpg",
      bio: "Kendrick Lamar é um rapper e compositor americano considerado um dos artistas de hip-hop mais influentes e bem-sucedidos de sua geração. Conhecido por seus álbuns conceituais e letras profundas que abordam temas como desigualdade racial, identidade e espiritualidade.",
      followers: 45000000,
      monthlyListeners: 38000000,
      popular: true
    },
    3: {
      id: 3,
      name: "Radiohead",
      image: "/imagemm.jpg",
      bio: "Radiohead é uma banda britânica de rock alternativo que revolucionou a música nos anos 90 e 2000, conhecida por sua evolução constante de estilo, experimentação sonora e álbuns conceitualmente densos como 'OK Computer' e 'Kid A'.",
      followers: 22000000,
      monthlyListeners: 18000000,
      popular: true
    }
  };
  
  // Use o artista 1 como padrão
  const mockArtist = mockArtists[1];

  const mockAlbums = [
    {
      id: 1,
      title: "folklore",
      image: "/imagemm.jpg",
      year: "2020",
      genre: "Indie Folk",
      averageRating: 4.8,
      reviewsCount: 12500
    },
    {
      id: 2,
      title: "Midnights",
      image: "/imagemm.jpg",
      year: "2022",
      genre: "Pop",
      averageRating: 4.6,
      reviewsCount: 15000
    },
    {
      id: 3,
      title: "1989",
      image: "/imagemm.jpg",
      year: "2014",
      genre: "Pop",
      averageRating: 4.7,
      reviewsCount: 18000
    },
    {
      id: 4,
      title: "Red (Taylor's Version)",
      image: "/imagemm.jpg",
      year: "2021",
      genre: "Pop/Country",
      averageRating: 4.9,
      reviewsCount: 14000
    },
    {
      id: 5,
      title: "evermore",
      image: "/imagemm.jpg",
      year: "2020",
      genre: "Indie Folk",
      averageRating: 4.5,
      reviewsCount: 11000
    },
    {
      id: 6,
      title: "Lover",
      image: "/imagemm.jpg",
      year: "2019",
      genre: "Pop",
      averageRating: 4.4,
      reviewsCount: 13000
    },
    {
      id: 7,
      title: "reputation",
      image: "/imagemm.jpg",
      year: "2017",
      genre: "Pop/Electronic",
      averageRating: 4.3,
      reviewsCount: 12000
    },
    {
      id: 8,
      title: "Speak Now",
      image: "/imagemm.jpg",
      year: "2010",
      genre: "Country Pop",
      averageRating: 4.7,
      reviewsCount: 9000
    },
    {
      id: 9,
      title: "Fearless (Taylor's Version)",
      image: "/imagemm.jpg",
      year: "2021",
      genre: "Country Pop",
      averageRating: 4.8,
      reviewsCount: 11500
    },
    {
      id: 10,
      title: "Taylor Swift",
      image: "/imagemm.jpg",
      year: "2006",
      genre: "Country",
      averageRating: 4.2,
      reviewsCount: 7500
    },
    {
      id: 11,
      title: "The Tortured Poets Department",
      image: "/imagemm.jpg",
      year: "2024",
      genre: "Pop",
      averageRating: 4.7,
      reviewsCount: 18500
    },
    {
      id: 12,
      title: "Speak Now (Taylor's Version)",
      image: "/imagemm.jpg",
      year: "2023",
      genre: "Country Pop",
      averageRating: 4.9,
      reviewsCount: 14500
    },
    {
      id: 13,
      title: "TPRD: The Anthology",
      image: "/imagemm.jpg",
      year: "2024",
      genre: "Pop",
      averageRating: 4.6,
      reviewsCount: 10000
    },
    {
      id: 14,
      title: "1989 (Taylor's Version)",
      image: "/imagemm.jpg",
      year: "2023",
      genre: "Pop",
      averageRating: 4.8,
      reviewsCount: 16000
    },
    {
      id: 15,
      title: "evermore: deluxe edition",
      image: "/imagemm.jpg",
      year: "2020",
      genre: "Indie Folk",
      averageRating: 4.6,
      reviewsCount: 9500
    }
  ];

  onMount(async () => {
    console.log("Artist.svelte onMount - params:", params);
    
    // Tenta carregar os dados reais primeiro, se falhar, usa os dados mockados
    if (params?.id) {
      console.log("Artist.svelte - Carregando artista com ID:", params.id);
      loading = true
      error = ''
      try {
        const response = await fetch(`http://localhost:8080/artists/${params.id}`)
        if (!response.ok) throw new Error('Artista não encontrado')
        artist = await response.json()
        console.log("Artist.svelte - Dados do artista carregados da API:", artist);
      } catch (e) {
        console.log('Usando dados mockados para o artista devido ao erro:', e.message)
        error = '' // Limpamos o erro para mostrar os dados mockados
        // Usar o artista mockado correspondente ao ID, ou o artista 1 como fallback
        artist = mockArtists[params.id] || mockArtists[1]
        console.log("Artist.svelte - Usando dados mockados do artista:", artist);
      } finally {
        loading = false
      }

      // Load artist albums
      loadingAlbums = true
      albumsError = ''
      try {
        const albumsResponse = await fetch(`http://localhost:8080/artists/${params.id}/albums`)
        if (!albumsResponse.ok) throw new Error('Erro ao carregar álbuns')
        artistAlbums = await albumsResponse.json()
      } catch (e) {
        console.log('Usando dados mockados para os álbuns devido ao erro:', e.message)
        albumsError = '' // Limpamos o erro para mostrar os dados mockados
        artistAlbums = mockAlbums
      } finally {
        loadingAlbums = false
      }
    } else {
      // Se não houver ID na URL, mostrar dados mockados diretamente
      loading = false
      loadingAlbums = false
      error = ''
      albumsError = ''
      artist = mockArtist
      artistAlbums = mockAlbums
    }
  })
</script>

<nav class="navbar-albums">
  <div class="navbar-container">
    <div class="logo-component">
      <button class="logo-button" onclick={() => push('/home')} aria-label="Ir para página inicial">
        <img src="/logocomtexto.png" alt="BeeSharp Logo" />
      </button>
    </div>
    
    {#if isLoggedIn}
      <!-- Navbar para usuário logado -->
      <div class="user-menu">
        <button class="user-avatar" aria-label="User Profile" onclick={toggleUserMenu}>
          {#if currentUser}
            <div class="user-avatar-text">
              {currentUser.charAt(0).toUpperCase()}
            </div>
          {:else}
            <User size={20} />
          {/if}
        </button>
        {#if currentUser}
          <span class="user-name">{currentUser}</span>
        {/if}
        
        {#if showUserMenu}
          <div class="user-dropdown">
            <a href="/profile" onclick={(e) => { 
              e.preventDefault(); 
              const username = getUsername();
              if (username) {
                push(`/profile/${username}`);
              }
              showUserMenu = false; 
            }}>
              <User size={16} />
              Meu Perfil
            </a>
            <button onclick={handleLogout}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                <polyline points="16,17 21,12 16,7"/>
                <line x1="21" y1="12" x2="9" y2="12"/>
              </svg>
              Sair
            </button>
          </div>
        {/if}
      </div>
    {:else}
      <!-- Navbar para usuário não logado -->
      <div class="nav-links">
        <a href="/criar-conta" onclick={(e) => { e.preventDefault(); push('/criar-conta') }}>CRIAR CONTA</a>
        <button class="nav-login-btn" onclick={(e) => { e.preventDefault(); openLoginModal() }}>LOGIN</button>
      </div>
    {/if}
  </div>
</nav>

{#if showLoginModal}
    <div class="login-popup-overlay">
        <Login onClose={closeLoginModal} isModal={true} />
    </div>
{/if}

<div class="artist-detail-page">
  <main class="main-content">
    <div class="container">
      {#if loading}
        <div class="loading-state">
          <p>Carregando artista...</p>
        </div>
      {:else if error}
        <div class="error-state">
          <p style="color:red">{error}</p>
        </div>
      {:else if artist}
        <!-- Artist Hero Section -->
        <section class="artist-hero">
          <div class="artist-photo-container">
            <img src={artist.image || "/placeholder.svg"} alt="{artist.name}" class="artist-photo" />
          </div>
          
          <div class="artist-info">
            <div class="artist-header">
              <h1 class="artist-name">{artist.name}</h1>
              <div class="artist-stats">
                <span class="albums-count">{artistAlbums.length} álbuns</span>
              </div>
            </div>
            
            <div class="artist-rating-section">
              <div class="rating-layout">
                <div class="rating-display">
                  <div class="average-rating">
                    <div class="stars-large" style="font-size: 24px; line-height: 1;">
                      {#each renderStars(calculateAverageRating()).fullStars as _}
                        <Star size={24} class="star-filled" />
                      {/each}
                      {#if renderStars(calculateAverageRating()).hasHalfStar}
                        <span class="star-half">
                          <Star size={24} class="star-empty" />
                          <span class="star-half-fill">
                            <Star size={24} class="star-filled" />
                          </span>
                        </span>
                      {/if}
                      {#each renderStars(calculateAverageRating()).emptyStars as _}
                        <Star size={24} class="star-empty" />
                      {/each}
                    </div>
                    <span class="rating-number">{calculateAverageRating()}</span>
                  </div>
                  <p class="rating-count">Média dos álbuns</p>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Albums Section -->
        <section class="albums-section" bind:this={albumsSection}>
          <div class="section-header">
            <h2>
              <Disc size={28} />
              Álbuns
            </h2>
          </div>

          {#if loadingAlbums}
            <div class="loading-state">
              <p>Carregando álbuns...</p>
            </div>
          {:else if albumsError}
            <div class="error-state">
              <p style="color:red">{albumsError}</p>
            </div>
          {:else if artistAlbums.length === 0}
            <div class="no-albums">
              <p>Nenhum álbum encontrado para este artista.</p>
            </div>
          {:else}
            <div class="albums-grid">
              {#each paginatedAlbums as album (album.id)}
                <div class="album-card" onclick={() => handleAlbumClick(album)}>
                  <div class="album-cover-container">
                    <img src={album.image || "/placeholder.svg"} alt="{album.title}" class="album-cover" />
                    <div class="album-overlay">
                      <Music size={32} />
                    </div>
                  </div>
                  <div class="album-details">
                    <h3 class="album-title">{album.title}</h3>
                    <div class="album-meta">
                      <span class="album-year">{album.year || 'N/A'}</span>
                      <span class="album-genre">{album.genre || ''}</span>
                    </div>
                    <div class="album-rating">
                      <div class="stars">
                        {#each renderStars(album.averageRating).fullStars as _}
                          <Star size={16} class="star-filled" />
                        {/each}
                        {#if renderStars(album.averageRating).hasHalfStar}
                          <div class="star-half">
                            <Star size={16} class="star-empty" />
                            <div class="star-half-fill">
                              <Star size={16} class="star-filled" />
                            </div>
                          </div>
                        {/if}
                        {#each renderStars(album.averageRating).emptyStars as _}
                          <Star size={16} class="star-empty" />
                        {/each}
                      </div>
                      <span class="rating-text">{album.averageRating}</span>
                    </div>
                  </div>
                </div>
              {/each}
            </div>
            
            {#if totalPages > 1}
              <div class="pagination">
                <div class="pagination-container">
                  {#if currentPage > 1}
                    <button 
                      class="page-button" 
                      aria-label="Página anterior" 
                      onclick={() => goToPage(currentPage - 1)}
                    >
                      &lt;
                    </button>
                  {/if}
                  
                  {#each Array(totalPages) as _, i}
                    {@const pageNum = i + 1}
                    {#if pageNum === currentPage || pageNum === 1 || pageNum === totalPages || (pageNum >= currentPage - 1 && pageNum <= currentPage + 1)}
                      <button 
                        class="page-button {pageNum === currentPage ? 'active' : ''}" 
                        onclick={() => goToPage(pageNum)}
                        aria-label="Página {pageNum}"
                        aria-current={pageNum === currentPage ? 'page' : undefined}
                      >
                        {pageNum}
                      </button>
                    {:else if pageNum === currentPage - 2 || pageNum === currentPage + 2}
                      <span class="page-ellipsis">...</span>
                    {/if}
                  {/each}
                  
                  {#if currentPage < totalPages}
                    <button 
                      class="page-button" 
                      aria-label="Próxima página" 
                      onclick={() => goToPage(currentPage + 1)}
                    >
                      &gt;
                    </button>
                  {/if}
                </div>
              </div>
            {/if}
          {/if}
        </section>
      {/if}
    </div>
  </main>

  <!-- Navegação de teste removida -->

  <!-- Footer -->
  <footer class="footer">
    <div class="container">
      <p>&copy; 2024 BeeSharp. Todos os direitos reservados.</p>
    </div>
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

  .artist-detail-page {
    min-height: 100vh;
    background-color: #14181c;
    padding-top: 80px;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 1rem;
  }

  /* Navbar - identical to ProfilePage.svelte */
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
  
  /* Removida a seção de search styles para manter consistência com Album.svelte */

  /* User Avatar Styles */
  .user-menu {
    position: relative;
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .user-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    border: 2px solid rgba(255, 255, 255, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s ease;
    color: white;
  }

  .user-avatar:hover {
    background: rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.5);
    transform: translateY(-1px);
  }

  .user-avatar-text {
    font-weight: 600;
    font-size: 1rem;
    color: white;
  }

  .user-name {
    color: white;
    font-weight: 600;
    font-size: 0.9rem;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  .user-dropdown {
    position: absolute;
    top: 100%;
    right: 0;
    background: white;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    padding: 0.5rem 0;
    min-width: 180px;
    z-index: 200;
    margin-top: 0.5rem;
  }

  .user-dropdown a,
  .user-dropdown button {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    width: 100%;
    padding: 0.75rem 1rem;
    background: none;
    border: none;
    text-decoration: none;
    color: #1f2937;
    font-size: 0.9rem;
    cursor: pointer;
    transition: background-color 0.2s ease;
    text-align: left;
    font-family: inherit;
  }

  .user-dropdown a:hover,
  .user-dropdown button:hover {
    background: #f3f4f6;
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
    flex: 1;
    padding: 2rem 0;
  }

  /* Artist Hero Section */
  .artist-hero {
    display: grid;
    grid-template-columns: 350px 1fr;
    gap: 2rem;
    margin-bottom: 1.5rem;
    padding: 2rem 0;
  }

  .artist-photo-container {
    position: relative;
  }

  .artist-photo {
    width: 100%;
    aspect-ratio: 1;
    border-radius: 50%;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
    object-fit: cover;
  }

  .artist-info {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .artist-header {
    border-bottom: 1px solid #374151;
    padding-bottom: 1.5rem;
  }

  .artist-name {
    font-size: 2.5rem;
    font-weight: 700;
    margin: 0 0 0.5rem 0;
    color: white;
    line-height: 1.2;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  .artist-stats {
    display: flex;
    flex-wrap: wrap;
    gap: 1rem;
    font-size: 1rem;
    color: #9ca3af;
  }

  .artist-rating-section {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .rating-layout {
    display: flex;
    align-items: center;
    gap: 2rem;
  }

  .rating-display {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .average-rating {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .stars-large {
    display: flex;
    gap: 4px;
  }

  .rating-number {
    font-size: 2rem;
    font-weight: 700;
    color: white;
  }

  .rating-count {
    font-size: 0.875rem;
    color: #9ca3af;
    margin: 0;
  }

  /* Albums Section */
  .albums-section {
    background: #23272f;
    border-radius: 20px;
    padding: 2rem;
    backdrop-filter: blur(10px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  }

  .section-header {
    margin-bottom: 2rem;
  }

  .section-header h2 {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 2rem;
    font-weight: 700;
    margin: 0;
    color: white;
  }

  .albums-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 2rem;
  }

  .album-card {
    background: #2d3440;
    border-radius: 15px;
    padding: 1.5rem;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
    transition: all 0.3s ease;
    cursor: pointer;
    border: 2px solid transparent;
  }

  .album-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
    border-color: #FFC857;
  }

  .album-cover-container {
    position: relative;
    margin-bottom: 1rem;
    border-radius: 10px;
    overflow: hidden;
  }

  .album-cover {
    width: 100%;
    height: 200px;
    object-fit: cover;
    transition: transform 0.3s ease;
  }

  .album-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 200, 87, 0.8);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s ease;
    color: white;
  }

  .album-card:hover .album-overlay {
    opacity: 1;
  }

  .album-card:hover .album-cover {
    transform: scale(1.1);
  }

  .album-details {
    text-align: center;
  }

  .album-title {
    font-size: 1.2rem;
    font-weight: 700;
    margin: 0 0 0.5rem 0;
    color: white;
    line-height: 1.3;
  }

  .album-meta {
    display: flex;
    justify-content: center;
    gap: 1rem;
    margin-bottom: 1rem;
    font-size: 0.9rem;
    color: #9ca3af;
  }

  .album-year, .album-genre {
    font-weight: 500;
  }

  .album-rating {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
  }

  .stars {
    display: inline-flex; /* Alterado para inline-flex para garantir alinhamento */
    gap: 2px;
    align-items: center;
    vertical-align: middle;
  }

  .rating-text {
    font-size: 0.9rem;
    color: white; /* Alterado para branco para melhor contraste */
    font-weight: 600;
  }

  /* Star Rating Styles */
  :global(.star-filled) {
    color: #FFC857;
    fill: #FFC857;
  }

  :global(.star-empty) {
    color: #FFC857;
    fill: none;
    stroke: #FFC857;
  }

  /* Estilo das half stars igual ao TopPage.svelte */
  .star-half {
    position: relative;
    display: inline-block;
    width: 1em;
    height: 1em;
    vertical-align: middle;
  }
  .star-half :global(.star-empty) {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }
  .star-half-fill {
    position: absolute;
    top: 0;
    left: 0;
    width: 50%;
    height: 100%;
    overflow: hidden;
    pointer-events: none;
    display: block;
  }
  .star-half-fill :global(.star-filled) {
    position: absolute;
    top: 0;
    left: 0;
    width: 200%;
    height: 100%;
  }

  /* As propriedades já foram definidas anteriormente, então removemos esta duplicação */

  /* Loading and Error States */
  .loading-state, .error-state, .no-albums {
    text-align: center;
    padding: 2rem;
    font-size: 1.1rem;
  }

  .loading-state {
    color: #9ca3af;
  }

  .error-state {
    color: #e74c3c;
  }

  .no-albums {
    color: #9ca3af;
    font-style: italic;
  }
  
  /* Pagination Styles */
  .pagination {
    display: flex;
    justify-content: center;
    margin-top: 3rem;
  }
  
  .pagination-container {
    display: flex;
    gap: 0.5rem;
    align-items: center;
    background: #2d3440;
    padding: 0.75rem 1rem;
    border-radius: 50px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .page-button {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: transparent;
    border: none;
    color: white;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  
  .page-button:hover {
    background: #3c4655;
  }
  
  .page-button.active {
    background: #FFC857;
    color: #14181c;
    font-weight: 700;
  }
  
  .page-ellipsis {
    color: #9ca3af;
    padding: 0 5px;
  }

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

  /* Responsive Design */
  @media (max-width: 768px) {
    .container {
      padding: 0 1rem;
    }

    .artist-hero {
      grid-template-columns: 1fr;
      text-align: center;
      gap: 2rem;
    }

    .artist-photo {
      width: 200px;
      height: 200px;
    }

    .artist-name {
      font-size: 2rem;
    }

    .albums-grid {
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 1.5rem;
    }

    .rating-layout {
      flex-direction: column;
      gap: 1rem;
    }

    .artist-stats {
      flex-direction: column;
      gap: 0.5rem;
    }
  }

  @media (max-width: 480px) {
    .navbar-container {
      padding: 1rem;
    }

    .main-content {
      padding: 1rem 0;
    }

    .artist-hero, .albums-section {
      padding: 1.5rem;
    }

    .albums-grid {
      grid-template-columns: 1fr;
    }

    .artist-name {
      font-size: 1.8rem;
    }
  }
</style>
