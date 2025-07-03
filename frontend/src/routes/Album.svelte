<script>
  import { Music, Star, Heart, MessageSquare, Plus, ThumbsUp, ThumbsDown, Calendar, User, TrendingUp, Search } from 'lucide-svelte'
  import { push } from 'svelte-spa-router'
  import Login from '../lib/Login.svelte'
  import ReviewSubmissionModal from './ReviewSubmissionModal.svelte'
  import { onMount } from 'svelte'
  import { getAuthToken, isTokenExpired, getUsername, logout } from '../lib/auth'

  // Props to receive album ID from route
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
  onMount(() => {
    console.log('Album component mounted with params:', params);

    // Check authentication first
    checkAuthStatus();
    if (!isLoggedIn) {
      showLoginModal = true;
    }

    // Async logic separated from cleanup
    (async () => {
      if (params?.id) {
        console.log('Loading album with ID:', params.id);
        loading = true
        error = ''
        try {
          const response = await fetch(`http://localhost:8080/albums/${params.id}`)
          if (!response.ok) throw new Error('Erro ao buscar álbum')
          album = await response.json()
          
          // Garantir que o artistId esteja definido
          if (!album.artistId && album.artist) {
            // Se não tiver artistId, tentar carregar o ID do artista pelo nome
            try {
              const artistResponse = await fetch(`http://localhost:8080/artists?name=${encodeURIComponent(album.artist)}`)
              if (artistResponse.ok) {
                const artists = await artistResponse.json()
                if (artists && artists.length > 0) {
                  album.artistId = artists[0].id
                }
              }
            } catch (artistError) {
              console.warn('Não foi possível obter o ID do artista:', artistError)
            }
          }
          
          console.log('Album loaded from API:', album);
        } catch (e) {
          error = e.message
          console.error('Error fetching album:', e)
          // Mock data for demonstration quando a API falha
          album = {
            id: params.id,
            title: "Álbum de Exemplo",
            artist: "Artista Exemplo",
            artistId: params.id, // Adicionando artistId baseado no ID do álbum
            image: "/imagemm.jpg",
            averageRating: 4.2,
            totalRatings: 8547,
            year: 2020,
            genre: "Rock Alternativo",
            description: "Um álbum que explora diversos temas musicais e emocionais através de composições elaboradas e produção cuidadosa."
          }
          console.log('Using mock album data:', album);
        } finally {
          loading = false
        }
      } else {
        console.log('No album ID provided in params');
        loading = false;
        error = 'ID do álbum não fornecido';
      }
    })();

    // Close user menu when clicking outside
    const handleClickOutside = (event) => {
      if (showUserMenu && !event.target.closest('.user-menu')) {
        showUserMenu = false;
      }
    };
    document.addEventListener('click', handleClickOutside);

    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  })
  
  // Album data (inicial vazio)
  let album = null
  let loading = true
  let error = ''
  let isFavorited = false

  function handleFavorite() {
    isFavorited = !isFavorited
    // TODO: Adicionar chamada de API para persistir o estado de favorito
  }

  // Rating distribution data (mock, pode ser atualizado depois)
  let ratingDistribution = [
    { stars: 0.5, count: 64, percentage: 0.5 },
    { stars: 1.0, count: 129, percentage: 1 },
    { stars: 1.5, count: 193, percentage: 1.5 },
    { stars: 2.0, count: 257, percentage: 2 },
    { stars: 2.5, count: 385, percentage: 3 },
    { stars: 3.0, count: 643, percentage: 5 },
    { stars: 3.5, count: 1284, percentage: 10 },
    { stars: 4.0, count: 2569, percentage: 20 },
    { stars: 4.5, count: 3212, percentage: 25 },
    { stars: 5.0, count: 3211, percentage: 25 }
  ]

  // Friends' reviews e topReviews permanecem mockados por enquanto
  let friendsReviews = [
    // Array vazio inicialmente para demonstrar o card "sem reviews de amigos"
    // {
    //   id: 1,
    //   user: { name: "Alex Chen", username: "@alexmusic", avatar: "/imagemm.jpg" },
    //   rating: 5,
    //   comment: "Uma obra-prima absoluta que definiu uma geração. Cada faixa é perfeitamente elaborada e o álbum flui como uma experiência completa e envolvente. O artista no seu auge criativo.",
    //   date: "há 2 dias",
    //   likes: 12,
    //   dislikes: 0,
    //   userLiked: true,
    //   userDisliked: false
    // }
  ]
  let topReviews = [
    {
      id: 4,
      user: { name: "Music Critic Pro", username: "@criticpro", avatar: "/imagemm.jpg" },
      rating: 5,
      comment: "Este álbum se destaca como uma das obras mais influentes já gravadas. A visão artística expressa através das composições se tornou ainda mais relevante com o tempo. A paleta sonora se expandiu dramaticamente aqui, incorporando elementos diversos que influenciariam inúmeros artistas. Desde a faixa de abertura até o encerramento, cada momento serve ao tema geral do álbum. Esta não é apenas uma obra-prima do artista—é um dos álbuns definidores da década.",
      date: "há 1 mês",
      likes: 234,
      dislikes: 12,
      userLiked: false,
      userDisliked: false,
      verified: true
    },
    {
      id: 5,
      user: { name: "Vinyl Collector", username: "@vinylhead", avatar: "/imagemm.jpg" },
      rating: 5,
      comment: "Anos depois, e este álbum ainda soa fresco e inovador. A produção é imaculada, a composição é incomparável, e os temas são atemporais. Cada faixa aqui é essencial e contribui para a experiência completa. Um álbum perfeito em todos os sentidos que merece estar em qualquer coleção.",
      date: "há 3 semanas",
      likes: 189,
      dislikes: 8,
      userLiked: true,
      userDisliked: false,
      verified: false
    }
  ]

  let showReviewModal = false

  function renderStars(rating) {
    const stars = []
    for (let i = 1; i <= 5; i++) {
      if (i <= rating) {
        stars.push({ type: 'full', key: i })
      } else if (i - 0.5 <= rating) {
        stars.push({ type: 'half', key: i })
      } else {
        stars.push({ type: 'empty', key: i })
      }
    }
    return stars
  }

  function handleLike(reviewId, isFromFriends = true) {
    const reviews = isFromFriends ? friendsReviews : topReviews
    const review = reviews.find(r => r.id === reviewId)
    if (review) {
      console.log('Before like:', { userLiked: review.userLiked, userDisliked: review.userDisliked, likes: review.likes, dislikes: review.dislikes })
      
      if (review.userLiked) {
        review.likes--
        review.userLiked = false
      } else {
        if (review.userDisliked) {
          review.dislikes--
          review.userDisliked = false
        }
        review.likes++
        review.userLiked = true
      }
      
      console.log('After like:', { userLiked: review.userLiked, userDisliked: review.userDisliked, likes: review.likes, dislikes: review.dislikes })
      
      // Force reactivity update
      if (isFromFriends) {
        friendsReviews = [...friendsReviews]
      } else {
        topReviews = [...topReviews]
      }
    }
  }
  
  function handleDislike(reviewId, isFromFriends = true) {
    const reviews = isFromFriends ? friendsReviews : topReviews
    const review = reviews.find(r => r.id === reviewId)
    if (review) {
      console.log('Before dislike:', { userLiked: review.userLiked, userDisliked: review.userDisliked, likes: review.likes, dislikes: review.dislikes })
      
      if (review.userDisliked) {
        review.dislikes--
        review.userDisliked = false
      } else {
        if (review.userLiked) {
          review.likes--
          review.userLiked = false
        }
        review.dislikes++
        review.userDisliked = true
      }
      
      console.log('After dislike:', { userLiked: review.userLiked, userDisliked: review.userDisliked, likes: review.likes, dislikes: review.dislikes })
      
      // Force reactivity update
      if (isFromFriends) {
        friendsReviews = [...friendsReviews]
      } else {
        topReviews = [...topReviews]
      }
    }
  }
  
  function openReviewModal() {
    showReviewModal = true
  }
  
  function closeReviewModal() {
    showReviewModal = false
  }
  
  function handleReviewSubmit(reviewData) {
    console.log('Nova review enviada:', reviewData)
    // Adiciona a nova review às reviews dos amigos (assumindo que é do usuário atual)
    const newReview = {
      id: Date.now(),
      user: { name: "Você", username: "@voce", avatar: "/imagemm.jpg" },
      rating: reviewData.rating,
      comment: reviewData.comment || "",
      date: "agora mesmo",
      likes: 0,
      dislikes: 0,
      userLiked: false,
      userDisliked: false
    }
    friendsReviews = [newReview, ...friendsReviews]
    closeReviewModal()
  }

  function goToProfile(username) {
    // Remove the @ symbol if present
    const cleanUsername = username.replace('@', '')
    push(`/profile/${cleanUsername}`)
  }

  function handleArtistClick(artist) {
    console.log('Artist clicked:', artist.name, 'ID:', artist.id)
    if (artist && artist.id) {
      push(`/artist/${artist.id}`)
    } else {
      console.error('Erro: ID do artista não encontrado')
      // Fallback para uma pesquisa pelo nome do artista se necessário
    }
  }
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
            <button onclick={(e) => { e.preventDefault(); const username = getUsername(); if(username) push(`/profile/${username}`); showUserMenu = false; }}>
              <User size={16} />
              Meu Perfil
            </button>
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

<div class="album-detail-page">
  <main class="main-content">
    <div class="container">
      {#if loading}
        <div class="loading-state">
          <p>Carregando álbum...</p>
        </div>
      {:else if error}
        <div class="error-state">
          <p style="color:red">{error}</p>
        </div>
      {:else if album}
        <!-- Album Hero Section -->
        <section class="album-hero">
          <div class="album-cover-container">
            <img src={album.image || "/placeholder.svg"} alt="{album.title} by {album.artist}" class="album-cover" />
          </div>
          
          <div class="album-info">
            <div class="album-header">
              <h1 class="album-title">{album.title}</h1>
              <div class="album-meta">
                <span class="album-artist">
                  <span 
                    class="artist-link" 
                    onclick={() => handleArtistClick({id: album.artistId, name: album.artist})}
                    role="button"
                    tabindex="0"
                    onkeydown={(e) => e.key === 'Enter' && handleArtistClick({id: album.artistId, name: album.artist})}
                  >
                    {album.artist}
                  </span>
                </span>
                <span class="album-year">{album.year}</span>
                <span class="album-genre">{album.genre}</span>
                <span class="album-duration">{album.duration}</span>
              </div>
            </div>
            
            <div class="album-rating-section">
              <!-- Rating and Actions Column + Distribution Column -->
              <div class="rating-layout">
                <div class="rating-and-action-column">
                  <div class="average-rating">
                    <div class="rating-display">
                      <div class="stars-large">
                        {#each renderStars(album.averageRating) as star (star.key)}
                          {#if star.type === 'full'}
                            <Star size={24} class="star-filled" />
                          {:else if star.type === 'half'}
                            <div class="star-half-container">
                              <Star size={24} class="star-empty" />
                              <div class="star-half-overlay">
                                <Star size={24} class="star-filled" />
                              </div>
                            </div>
                          {:else}
                            <Star size={24} class="star-empty" />
                          {/if}
                        {/each}
                      </div>
                      <span class="rating-number">{album.averageRating}</span>
                    </div>
                    <p class="rating-count">{album.totalRatings?.toLocaleString() || 0} avaliações</p>
                  </div>
                  
                  <!-- Review Button -->
                  <div class="album-actions">
                    <button class="review-btn" onclick={openReviewModal} aria-label="Escrever uma review">
                      <Plus size={20} />
                      Avaliar
                    </button>
                    <button 
                      class="favorite-btn {isFavorited ? 'favorited' : ''}" 
                      onclick={handleFavorite} 
                      aria-label="Favoritar álbum"
                    >
                      <Heart size={20} />
                    </button>
                  </div>
                </div>
                
                <!-- Rating Distribution -->
                <div class="rating-distribution-compact">
                  <div class="rating-bars-vertical">
                    {#each ratingDistribution as rating (rating.stars)}
                      <div class="rating-bar-vertical" title="{rating.count.toLocaleString()} avaliações com {rating.stars} estrelas">
                        <div class="rating-bar-fill-vertical" style="height: {rating.percentage * 6}px;"></div>
                        <div class="rating-label-vertical">{rating.stars}</div>
                      </div>
                    {/each}
                  </div>
                </div>
              </div>
            </div>

          </div>
        </section>

        <!-- Friends' Reviews -->
        <section class="reviews-section">
          <div class="section-header">
            <h2 class="section-title">
              <User size={24} />
              Reviews dos Amigos
            </h2>
          </div>
          
          <div class="reviews-list">
            {#if friendsReviews.length === 0}
              <div class="no-reviews-card">
                <div class="no-reviews-content">
                  <User size={48} color="#6b7280" />
                  <h3>Sem reviews de amigos</h3>
                  <p>Você ainda não segue ninguém que avaliou este álbum.</p>
                </div>
              </div>
            {:else}
              {#each friendsReviews as review (review.id)}
                <div class="review-card">
                  <div class="review-header">
                    <div class="user-info" onclick={() => goToProfile(review.user.username)} title="Ver perfil de {review.user.name}">
                      <img src={review.user.avatar || "/placeholder.svg"} alt={review.user.name} class="user-avatar clickable" />
                      <div class="user-details">
                        <h4 class="user-name clickable">{review.user.name}</h4>
                        <p class="user-username clickable">{review.user.username}</p>
                      </div>
                    </div>
                    <div class="review-rating">
                      <div class="stars-container">
                        {#each renderStars(review.rating) as star (star.key)}
                          {#if star.type === 'full'}
                            <Star size={16} class="star-filled" />
                          {:else if star.type === 'half'}
                            <div class="star-half-container">
                              <Star size={16} class="star-empty" />
                              <div class="star-half-overlay">
                                <Star size={16} class="star-filled" />
                              </div>
                            </div>
                          {:else}
                            <Star size={16} class="star-empty" />
                          {/if}
                        {/each}
                      </div>
                      <span class="review-date">{review.date}</span>
                    </div>
                  </div>
                  
                  {#if review.comment}
                    <div class="review-content">
                      <p>{review.comment}</p>
                    </div>
                  {/if}
                  
                  <div class="review-actions">
                    <button 
                      class="action-btn like-btn {review.userLiked ? 'active' : ''}"
                      onclick={() => handleLike(review.id, true)}
                      aria-label="Curtir review"
                    >
                      <ThumbsUp size={16} />
                      <span>{review.likes}</span>
                    </button>
                    <button 
                      class="action-btn dislike-btn {review.userDisliked ? 'active' : ''}"
                      onclick={() => handleDislike(review.id, true)}
                      aria-label="Não curtir review"
                    >
                      <ThumbsDown size={16} />
                      <span>{review.dislikes}</span>
                    </button>
                  </div>
                </div>
              {/each}
            {/if}
          </div>
        </section>

        <!-- Top Reviews -->
        <section class="reviews-section top-reviews">
          <div class="section-header">
            <h2 class="section-title">
              <TrendingUp size={24} />
              Reviews Mais Bem Avaliadas
            </h2>
          </div>
          
          <div class="reviews-list">
            {#each topReviews as review (review.id)}
              <div class="review-card">
                <div class="review-header">
                  <div class="user-info" onclick={() => goToProfile(review.user.username)} title="Ver perfil de {review.user.name}">
                    <img src={review.user.avatar || "/placeholder.svg"} alt={review.user.name} class="user-avatar clickable" />
                    <div class="user-details">
                      <h4 class="user-name clickable">
                        {review.user.name}
                        {#if review.verified}
                          <span class="verified-badge">✓</span>
                        {/if}
                      </h4>
                      <p class="user-username clickable">{review.user.username}</p>
                    </div>
                  </div>
                  <div class="review-rating">
                    <div class="stars-container">
                      {#each renderStars(review.rating) as star (star.key)}
                        {#if star.type === 'full'}
                          <Star size={16} class="star-filled" />
                        {:else if star.type === 'half'}
                          <div class="star-half-container">
                            <Star size={16} class="star-empty" />
                            <div class="star-half-overlay">
                              <Star size={16} class="star-filled" />
                            </div>
                          </div>
                        {:else}
                          <Star size={16} class="star-empty" />
                        {/if}
                      {/each}
                    </div>
                    <span class="review-date">{review.date}</span>
                  </div>
                </div>
                
                <div class="review-content">
                  <p>{review.comment}</p>
                </div>
                
                <div class="review-actions">
                  <button 
                    class="action-btn like-btn {review.userLiked ? 'active' : ''}"
                    onclick={() => handleLike(review.id, false)}
                    aria-label="Curtir review"
                  >
                    <ThumbsUp size={16} />
                    <span>{review.likes}</span>
                  </button>
                  <button 
                    class="action-btn dislike-btn {review.userDisliked ? 'active' : ''}"
                    onclick={() => handleDislike(review.id, false)}
                    aria-label="Não curtir review"
                  >
                    <ThumbsDown size={16} />
                    <span>{review.dislikes}</span>
                  </button>
                </div>
              </div>
            {/each}
          </div>
        </section>
      {/if}
    </div>
  </main>

  <!-- Footer -->
  <footer class="footer">
    <p>© BeeSharp Limited. Feito por estudantes do IME USP apaixonados por música. 
        Dados da API do Spotify.</p>
  </footer>
</div>

<!-- Review Modal -->
{#if showReviewModal}
  <ReviewSubmissionModal 
    album={album}
    onClose={closeReviewModal}
    onSubmit={handleReviewSubmit}
  />
{/if}

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

  .album-detail-page {
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
    padding: 2rem 0;
  }

  /* Loading and Error States */
  .loading-state, .error-state {
    text-align: center;
    padding: 4rem 0;
    font-size: 1.125rem;
  }

  .loading-state p {
    color: #9ca3af;
  }

  .error-state p {
    color: #ef4444;
  }

  /* Album Hero Section */
  .album-hero {
    display: grid;
    grid-template-columns: 350px 1fr;
    gap: 2rem;
    margin-bottom: 1.5rem;
    padding: 2rem 0;
  }

  .album-cover-container {
    position: relative;
  }

  .album-cover {
    width: 100%;
    aspect-ratio: 1;
    border-radius: 8px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
    object-fit: cover;
  }

  .album-actions {
    display: flex;
    justify-content: flex-start;
    gap: 0.75rem; /* Adiciona espaço entre os botões */
    margin-top: 0.7rem;
  }

  .favorite-btn {
    background: none;
    border: 2px solid #6b7280; /* Borda cinza */
    color: white;
    padding: 0.75rem;
    border-radius: 8px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
  }

  .favorite-btn:hover {
    border-color: #C5283D; /* Vermelho no hover */
    color: #C5283D;
  }

  .favorite-btn.favorited {
    background-color: #C5283D; /* Vermelho quando favoritado */
    border-color: #C5283D;
    color: white;
  }

  .favorite-btn :global(svg) {
    fill: none; /* Coração sempre vazio */
    stroke: currentColor;
  }

  .review-btn {
    background: #255F85; /* Azul da navbar */
    color: white;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    justify-content: center;
    transition: all 0.2s ease;
    font-size: 0.875rem;
    font-family: 'Familjen Grotesk', sans-serif;
    min-width: 120px;
  }

  .review-btn:hover {
    background: #1e4c6b; /* Azul mais escuro no hover */
    transform: translateY(-1px);
  }

  .album-info {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .album-header {
    border-bottom: 1px solid #374151;
    padding-bottom: 1.5rem;
  }

  .album-title {
    font-size: 2.5rem;
    font-weight: 700;
    margin: 0 0 0.5rem 0;
    color: white;
    line-height: 1.2;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  .album-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 1rem;
    font-size: 1rem;
    color: #9ca3af;
  }

  .album-artist {
    color: #FFC857;
    font-weight: 600;
  }

  .album-rating-section {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .rating-layout {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    gap: 3rem;
    width: 100%;
  }

  .rating-and-action-column {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    flex: 1;
    gap: 3rem;
  }

  .average-rating {
    text-align: left;
    margin-bottom: 3rem;
  }

  .rating-display {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .stars-large {
    display: flex;
    align-items: center;
    gap: 2px;
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

  /* Rating Distribution Compact */
  .rating-distribution-compact {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;
    justify-content: flex-end;
  }

  .rating-bars-vertical {
    display: flex;
    align-items: flex-end;
    gap: 4px;
    height: 180px;
    padding: 0;
    width: fit-content;
    margin-top: 2rem;
  }

  .rating-bar-vertical {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 20px;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .rating-bar-vertical:hover {
    transform: translateY(-2px);
  }

  .rating-bar-fill-vertical {
    width: 100%;
    background: #FFC857;
    border-radius: 2px 2px 0 0;
    transition: all 0.3s ease;
    min-height: 2px;
  }

  .rating-label-vertical {
    font-size: 0.75rem;
    color: #9ca3af;
    margin-top: 0.25rem;
    text-align: center;
    font-weight: 500;
  }

  /* Reviews Section */
  .reviews-section {
    margin-bottom: 3rem;
  }

  .section-header {
    margin-bottom: 2rem;
  }

  .reviews-list {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .review-card {
    background: #1d232a;
    border-radius: 12px;
    padding: 1.5rem;
    border: 1px solid #374151;
    transition: all 0.2s ease;
  }

  .review-card:hover {
    border-color: #4b5563;
  }

  .no-reviews-card {
    background: #1d232a;
    border-radius: 12px;
    padding: 3rem 1.5rem;
    border: 1px solid #374151;
    text-align: center;
  }

  .no-reviews-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }

  .no-reviews-content h3 {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
    color: #9ca3af;
  }

  .no-reviews-content p {
    margin: 0;
    color: #6b7280;
    max-width: 400px;
    line-height: 1.5;
  }



  .review-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 1rem;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    cursor: pointer;
    border-radius: 8px;
    padding: 0.25rem;
    transition: all 0.2s ease;
  }

  .user-info:hover {
    transform: translateY(-1px);
  }

  .user-info .user-avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #374151;
    transition: all 0.2s ease;
  }

  .user-info:hover .user-avatar {
    border-color: #4A90E2;
    transform: scale(1.05);
  }

  .clickable {
    transition: all 0.2s ease;
  }

  .user-info:hover .clickable {
    color: #4A90E2;
  }

  .user-details {
    display: flex;
    flex-direction: column;
  }

  .user-name {
    font-size: 1rem;
    font-weight: 600;
    margin: 0;
    color: white;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    transition: color 0.2s ease;
  }

  .verified-badge {
    background: #FFC857;
    color: #14181c;
    font-size: 0.75rem;
    padding: 0.125rem 0.25rem;
    border-radius: 50%;
    font-weight: bold;
  }

  .user-username {
    font-size: 0.875rem;
    color: #9ca3af;
    margin: 0;
    transition: color 0.2s ease;
  }

  .review-rating {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 0.25rem;
  }

  .stars-container {
    display: flex;
    align-items: center;
    gap: 2px;
  }

  .review-date {
    font-size: 0.75rem;
    color: #6b7280;
  }

  .review-content {
    margin-bottom: 1rem;
  }

  .review-content p {
    color: #d1d5db;
    line-height: 1.6;
    margin: 0;
  }

  .review-actions {
    display: flex;
    gap: 1rem;
    padding-top: 1rem;
    border-top: 1px solid #374151;
  }

  .action-btn {
    background: none;
    border: 1px solid #374151;
    color: #9ca3af;
    padding: 0.5rem 1rem;
    border-radius: 6px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.875rem;
    transition: all 0.2s ease;
  }

  .action-btn:hover {
    border-color: #4b5563;
    color: white;
  }

  .action-btn.active.like-btn {
    border-color: #FFC857;
    color: #FFC857;
    background: rgba(255, 200, 87, 0.1);
  }

  .action-btn.active.dislike-btn {
    border-color: #ef4444;
    color: #ef4444;
    background: rgba(239, 68, 68, 0.1);
  }

  /* Star Rating Styles */
  :global(.star-filled) {
    fill: #FFC857;
    color: #FFC857;
  }

  :global(.star-empty) {
    fill: none;
    color: #FFC857;
    stroke: #FFC857;
  }

  .star-half-container {
    position: relative;
    display: inline-flex;
    align-items: center;
  }

  .star-half-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 50%;
    overflow: hidden;
  }

  .star-half-overlay :global(.star-filled) {
    fill: #FFC857;
    color: #FFC857;
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
  @media (max-width: 767px) {
    .album-hero {
      grid-template-columns: 1fr;
      gap: 1.5rem;
      text-align: center;
    }

    .album-cover-container {
      max-width: 280px;
      margin: 0 auto;
    }

    .album-title {
      font-size: 2rem;
    }

    .album-rating-section {
      gap: 1rem;
    }

    .rating-layout {
      flex-direction: column;
      gap: 2rem;
      align-items: center;
    }

    .rating-and-action-column {
      align-items: center;
      text-align: center;
    }

    .average-rating {
      text-align: center;
    }

    .rating-display {
      justify-content: center;
    }

    .album-actions {
      justify-content: center;
    }

    .rating-bars-vertical {
      height: 140px;
      gap: 2px;
    }

    .rating-label-vertical {
      font-size: 0.6875rem;
    }

    .review-header {
      flex-direction: column;
      gap: 1rem;
    }

    .review-rating {
      align-items: flex-start;
    }
  }

  @media (max-width: 480px) {
    .album-actions {
      margin-top: 1rem;
    }

    .review-card {
      padding: 1rem;
    }

    .user-info .user-avatar {
      width: 40px;
      height: 40px;
    }

    .action-btn {
      padding: 0.375rem 0.75rem;
      font-size: 0.8125rem;
    }
    
    /* Artist Link Styles */
    .artist-link {
      background: none;
      border: none;
      color: #FFC857;
      cursor: pointer;
      font-size: inherit;
      font-family: inherit;
      padding: 0;
      text-decoration: none;
      transition: color 0.2s ease, text-decoration-color 0.2s ease;
      font-weight: 600;
    }

    .artist-link:hover {
      text-decoration: underline !important;
      text-decoration-color: #FFC857 !important;
    }
  }
</style>
