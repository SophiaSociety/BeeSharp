<script>
  import { Star, Heart, Calendar, Music, Users, MessageSquare, ListMusic, ThumbsUp, ThumbsDown, Search } from 'lucide-svelte'
  import { push } from 'svelte-spa-router'
  import Login from '../lib/Login.svelte'
  import { isAuthenticated } from '../lib/auth'
  import { onMount } from 'svelte'
  
  // Simulação de usuário logado
  let loggedUser = {
    name: "Alex Chen",
    username: "@alexmusic",
    avatar: "/placeholder.svg?height=32&width=32"
  }
  let showLoginModal = $state(false)
  function openLoginModal() { showLoginModal = true }
  function closeLoginModal() { showLoginModal = false }

  // Check login status when component mounts
  onMount(() => {
    if (!isAuthenticated()) {
      showLoginModal = true;
    }
  })
  
  // Searchbar
  let searchQuery = $state('')
  function handleSearch(event) {
    event.preventDefault()
    // Implementar busca
  }

  // Controle de seguir
  let isFollowing = $state(false)
  function handleFollow() {
    // Simula sucesso
    isFollowing = !isFollowing
  }
  
  // Pagination variables and functions
  let showAllAlbums = $state(false);
  let showAllReviews = $state(false);
  let currentAlbumsPage = $state(1);
  let currentReviewsPage = $state(1);
  const ITEMS_PER_PAGE = 15;
  
  function toggleAlbumsView() {
    showAllAlbums = !showAllAlbums;
    currentAlbumsPage = 1;
  }
  
  function toggleReviewsView() {
    showAllReviews = !showAllReviews;
    currentReviewsPage = 1;
  }
  
  function goToAlbumsPage(page) {
    currentAlbumsPage = page;
  }
  
  function goToReviewsPage(page) {
    currentReviewsPage = page;
  }
  
  function getTotalAlbumsPages() {
    return Math.ceil(listenedAlbums.length / ITEMS_PER_PAGE);
  }
  
  function getTotalReviewsPages() {
    return Math.ceil(recentReviews.length / ITEMS_PER_PAGE);
  }
  
  function getCurrentAlbumsPage() {
    const start = (currentAlbumsPage - 1) * ITEMS_PER_PAGE;
    const end = start + ITEMS_PER_PAGE;
    return listenedAlbums.slice(start, end);
  }
  
  function getCurrentReviewsPage() {
    const start = (currentReviewsPage - 1) * ITEMS_PER_PAGE;
    const end = start + ITEMS_PER_PAGE;
    return recentReviews.slice(start, end);
  }

  let listenedAlbums = $state([
    { id: 1, title: "DAMN.", artist: "Kendrick Lamar", year: "2017", rating: 4.5 },
    { id: 2, title: "Currents", artist: "Tame Impala", year: "2015", rating: 4.0 },
    { id: 3, title: "In Rainbows", artist: "Radiohead", year: "2007", rating: 5.0 },
    { id: 4, title: "MBDTF", artist: "Kanye West", year: "2010", rating: 4.5 },
    { id: 5, title: "Funeral", artist: "Arcade Fire", year: "2004", rating: 3.5 },
    { id: 6, title: "The Suburbs", artist: "Arcade Fire", year: "2010", rating: 4.0 },
    { id: 7, title: "OK Computer", artist: "Radiohead", year: "1997", rating: 4.5 },
    { id: 8, title: "To Pimp a Butterfly", artist: "Kendrick Lamar", year: "2015", rating: 5.0 },
    { id: 9, title: "The Dark Side of the Moon", artist: "Pink Floyd", year: "1973", rating: 4.5 },
    { id: 10, title: "Blonde", artist: "Frank Ocean", year: "2016", rating: 4.0 },
    { id: 11, title: "Abbey Road", artist: "The Beatles", year: "1969", rating: 4.8 },
    { id: 12, title: "Rumours", artist: "Fleetwood Mac", year: "1977", rating: 4.7 },
    { id: 13, title: "Nevermind", artist: "Nirvana", year: "1991", rating: 4.6 },
    { id: 14, title: "Is This It", artist: "The Strokes", year: "2001", rating: 4.3 },
    { id: 15, title: "Kid A", artist: "Radiohead", year: "2000", rating: 4.9 },
    { id: 16, title: "Good Kid, M.A.A.D City", artist: "Kendrick Lamar", year: "2012", rating: 4.8 },
    { id: 17, title: "Channel Orange", artist: "Frank Ocean", year: "2012", rating: 4.5 },
    { id: 18, title: "The Queen Is Dead", artist: "The Smiths", year: "1986", rating: 4.4 },
    { id: 19, title: "Loveless", artist: "My Bloody Valentine", year: "1991", rating: 4.7 },
    { id: 20, title: "Blue", artist: "Joni Mitchell", year: "1971", rating: 4.9 },
  ])

  let favoriteAlbums = $state([
    { id: 1, title: "OK Computer", artist: "Radiohead", year: "1997" },
    { id: 2, title: "To Pimp a Butterfly", artist: "Kendrick Lamar", year: "2015" },
    { id: 3, title: "The Dark Side of the Moon", artist: "Pink Floyd", year: "1973" },
    { id: 4, title: "Blonde", artist: "Frank Ocean", year: "2016" },
  ])

  let recentReviews = $state([
    {
      id: 1,
      title: "IGOR",
      artist: "Tyler, The Creator",
      year: "2019",
      rating: 4.5,
      review: "A bold artistic statement that showcases Tyler's growth as both a producer and songwriter. The cohesive narrative and lush production make this a standout album.",
      date: "2 days ago",
      liked: true,
      likes: 15,
      dislikes: 2,
      userLiked: false,
      userDisliked: false
    },
    {
      id: 2,
      title: "Folklore",
      artist: "Taylor Swift",
      year: "2020",
      rating: 4,
      review: "Swift's indie folk pivot is surprisingly successful, with introspective lyrics and stripped-down production that feels both intimate and cinematic.",
      date: "1 week ago",
      liked: false,
      likes: 8,
      dislikes: 1,
      userLiked: false,
      userDisliked: false
    },
    {
      id: 3,
      title: "Swimming",
      artist: "Mac Miller",
      year: "2018",
      rating: 5,
      review: "A deeply personal and vulnerable album that showcases Mac's incredible talent. Every track feels essential and the production is absolutely gorgeous.",
      date: "2 weeks ago",
      liked: true,
      likes: 23,
      dislikes: 0,
      userLiked: true,
      userDisliked: false
    },
    {
      id: 4,
      title: "Random Access Memories",
      artist: "Daft Punk",
      year: "2013",
      rating: 4.5,
      review: "A masterful blend of electronic and live music that takes the listener on a journey. Each track is meticulously crafted and the collaborations are top-notch.",
      date: "1 month ago",
      liked: false,
      likes: 10,
      dislikes: 0,
      userLiked: false,
      userDisliked: false
    },
    {
      id: 5,
      title: "25",
      artist: "Adele",
      year: "2015",
      rating: 4.8,
      review: "A powerful and emotional album that showcases Adele's incredible vocal range and songwriting skills. A modern classic.",
      date: "2 months ago",
      liked: true,
      likes: 30,
      dislikes: 5,
      userLiked: true,
      userDisliked: false
    },
    {
      id: 6,
      title: "Future Nostalgia",
      artist: "Dua Lipa",
      year: "2020",
      rating: 4.2,
      review: "A fun and energetic album that brings together elements of disco, pop, and funk. Dua Lipa's vocals are captivating and the production is stellar.",
      date: "3 months ago",
      liked: false,
      likes: 12,
      dislikes: 3,
      userLiked: false,
      userDisliked: false
    },
    {
      id: 7,
      title: "After Hours",
      artist: "The Weeknd",
      year: "2020",
      rating: 4.7,
      review: "A dark and moody album that perfectly showcases The Weeknd's unique sound and artistic vision. The production is top-notch and the features are well-placed.",
      date: "4 months ago",
      liked: true,
      likes: 25,
      dislikes: 4,
      userLiked: true,
      userDisliked: false
    },
    {
      id: 8,
      title: "Positions",
      artist: "Ariana Grande",
      year: "2020",
      rating: 4.3,
      review: "An album that continues to solidify Ariana's place in pop music. The production is sleek, and her vocals are as impressive as ever.",
      date: "5 months ago",
      liked: false,
      likes: 18,
      dislikes: 2,
      userLiked: false,
      userDisliked: false
    },
    {
      id: 9,
      title: "Justice",
      artist: "Justin Bieber",
      year: "2021",
      rating: 3.8,
      review: "A solid album with some standout tracks. Justin's growth as an artist is evident, though some songs feel less impactful.",
      date: "6 months ago",
      liked: true,
      likes: 20,
      dislikes: 10,
      userLiked: true,
      userDisliked: false
    },
  ])

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

  function handleLike(reviewId) {
    const review = recentReviews.find(r => r.id === reviewId)
    if (review) {
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
      recentReviews = [...recentReviews]
    }
  }

  function handleDislike(reviewId) {
    const review = recentReviews.find(r => r.id === reviewId)
    if (review) {
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
      recentReviews = [...recentReviews]
    }
  }

  function handleAlbumClick(album) {
    console.log('Album clicked:', album.title)
    // Navegar para a página do álbum
    push('/album')
  }
</script>

<nav class="navbar-albums">
  <div class="navbar-container">
    <div class="logo-component">
      <button class="logo-button" onclick={() => push('/')} aria-label="Ir para página inicial">
        <img src="/logocomtexto.png" alt="BeeSharp Logo" />
      </button>
    </div>
    <div class="search-and-user">
      <form class="search-form" onsubmit={handleSearch}>
        <div class="search-container">
          <Search size={18} />
          <input
            type="text"
            class="search-input"
            bind:value={searchQuery}
          />
          <button type="submit" class="search-button">Buscar</button>
        </div>
      </form>
      <div class="user-menu">
        <button class="user-avatar" aria-label="Perfil do usuário" onclick={() => push('/perfil')}>
          <img src={loggedUser.avatar} alt={loggedUser.name} />
        </button>
      </div>
    </div>
  </div>
</nav>

{#if showLoginModal}
    <div class="login-popup-overlay">
        <Login onClose={closeLoginModal} isModal={true} />
    </div>
{/if}

<div class="app">
  <!-- Header -->
  <header class="header">
    <div class="container">
      <div class="profile-info">
        <div class="avatar">
          <img src="/placeholder.svg?height=96&width=96" alt="Alex Chen" />
        </div>

        <div class="user-details">
          <div class="user-header">
            <div class="user-name">
              <h1>Alex Chen</h1>
              <p class="username">@alexmusic</p>
            </div>
            <button class="follow-btn {isFollowing ? 'following' : ''}" onclick={handleFollow}>
              {isFollowing ? 'Seguindo' : 'Seguir'}
            </button>
          </div>

          <p class="bio">
            Music enthusiast exploring sounds across genres. Always looking for the next album that will change my
            perspective. Currently obsessed with experimental hip-hop and indie folk.
          </p>

          <div class="stats">
            <div class="stat">
              <Music size={16} />
              <span class="stat-number">1,247</span> álbuns
            </div>
            <div class="stat">
              <MessageSquare size={16} />
              <span class="stat-number">892</span> reviews
            </div>
            <div class="stat">
              <Users size={16} />
              <span class="stat-number">156</span> seguindo
            </div>
            <div class="stat">
              <Users size={16} />
              <span class="stat-number">324</span> seguidores
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>

  <main class="main">
    <div class="container">
      <!-- Stats Bar -->
      <section class="stats-bar">
        <div class="stat-card">
          <div class="stat-value">4.2</div>
          <div class="stat-label">Avaliação Média</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">127</div>
          <div class="stat-label">Esse Ano</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">1,247</div>
          <div class="stat-label">Total</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">24</div>
          <div class="stat-label">Favoritos</div>
        </div>

      </section>

      <!-- Favorite Albums -->
      <section class="section">
        <div class="section-header">
          <div class="section-title">
            <Heart size={20} />
            <h2>Favoritos</h2>
          </div>
        </div>

        <div class="albums-grid">
          {#each favoriteAlbums as album (album.id)}
            <button class="album-card" onclick={() => handleAlbumClick(album)}>
              <div class="album-cover">
                <img src="/placeholder.svg?height=200&width=200" alt="{album.title} by {album.artist}" />
                <div class="album-overlay">
                  <div class="play-button">
                    <Heart size={24} />
                  </div>
                </div>
              </div>
              <div class="album-info">
                <p class="album-title">{album.title}</p>
                <p class="album-artist">{album.artist} • {album.year}</p>
              </div>
            </button>
          {/each}
        </div>
      </section>

      <!-- Listened Albums -->
      <section class="section">
        <div class="section-header">
          <div class="section-title">
            <Music size={20} />
            <h2>Álbuns Escutados</h2>
          </div>
        </div>

        <div class="albums-grid">
          {#each getCurrentAlbumsPage() as album (album.id)}
            <button class="album-card" onclick={() => handleAlbumClick(album)}>
              <div class="album-cover">
                <img src="/placeholder.svg?height=200&width=200" alt="{album.title} by {album.artist}" />
                <div class="album-overlay">
                  <div class="play-button">
                    <Music size={24} />
                  </div>
                </div>
              </div>
              <div class="album-info">
                <p class="album-title">{album.title}</p>
                <p class="album-artist">{album.artist} • {album.year}</p>
                <div class="album-rating">
                  <div class="stars-small">
                    {#each renderStars(album.rating).fullStars as _}
                      <Star size={14} class="star-filled" />
                    {/each}
                    {#if renderStars(album.rating).hasHalfStar}
                      <div class="star-half-small">
                        <Star size={14} class="star-empty" />
                        <div class="star-half-fill-small">
                          <Star size={14} class="star-filled" />
                        </div>
                      </div>
                    {/if}
                    {#each renderStars(album.rating).emptyStars as _}
                      <Star size={14} class="star-empty" />
                    {/each}
                  </div>
                </div>
              </div>
            </button>
          {/each}
        </div>

        {#if listenedAlbums.length > ITEMS_PER_PAGE}
          <div class="pagination">
            {#if currentAlbumsPage > 1}
              <button class="pagination-button" onclick={() => goToAlbumsPage(currentAlbumsPage - 1)}>
                ‹ Anterior
              </button>
            {/if}

            {#each Array(getTotalAlbumsPages()) as _, i (i)}
              <button class="pagination-button {currentAlbumsPage === i + 1 ? 'active' : ''}" onclick={() => goToAlbumsPage(i + 1)}>
                {i + 1}
              </button>
            {/each}

            {#if currentAlbumsPage < getTotalAlbumsPages()}
              <button class="pagination-button" onclick={() => goToAlbumsPage(currentAlbumsPage + 1)}>
                Próxima ›
              </button>
            {/if}
          </div>
        {/if}
      </section>

      <div class="separator"></div>

      <!-- All Reviews -->
      <section class="section">
        <div class="section-header">
          <div class="section-title">
            <MessageSquare size={20} />
            <h2>Reviews</h2>
          </div>
        </div>

        <div class="reviews-list">
          {#each getCurrentReviewsPage() as review (review.id)}
            <div class="review-card">
              <div class="review-layout">
                <div class="album-info-review">
                  <button class="album-image-button" onclick={() => handleAlbumClick(review)} aria-label="Ver álbum {review.title}">
                    <img src="/placeholder.svg?height=120&width=120" alt="{review.title} by {review.artist}" class="album-avatar" />
                  </button>
                </div>
                
                <div class="review-content-wrapper">
                  <div class="review-header">
                    <div class="album-details">
                      <button class="album-name-button" onclick={() => handleAlbumClick(review)} aria-label="Ver álbum {review.title}">
                        <h4 class="album-name">{review.title}</h4>
                      </button>
                      <p class="album-artist-year">{review.artist} • {review.year}</p>
                    </div>
                    <div class="review-rating">
                      <div class="stars-container">
                        {#each renderStars(review.rating).fullStars as _}
                          <Star size={16} class="star-filled" />
                        {/each}
                        {#if renderStars(review.rating).hasHalfStar}
                          <div class="star-half-container">
                            <Star size={16} class="star-empty" />
                            <div class="star-half-overlay">
                              <Star size={16} class="star-filled" />
                            </div>
                          </div>
                        {/if}
                        {#each renderStars(review.rating).emptyStars as _}
                          <Star size={16} class="star-empty" />
                        {/each}
                      </div>
                      <span class="review-date">{review.date}</span>
                    </div>
                  </div>
                  
                  {#if review.review}
                    <div class="review-content">
                      <p>{review.review}</p>
                    </div>
                  {/if}
                  
                  <div class="review-actions">
                    <button 
                      class="action-btn like-btn {review.userLiked ? 'active' : ''}"
                      onclick={() => handleLike(review.id)}
                      aria-label="Curtir review"
                    >
                      <ThumbsUp size={16} />
                      <span>{review.likes}</span>
                    </button>
                    <button 
                      class="action-btn dislike-btn {review.userDisliked ? 'active' : ''}"
                      onclick={() => handleDislike(review.id)}
                      aria-label="Não curtir review"
                    >
                      <ThumbsDown size={16} />
                      <span>{review.dislikes}</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          {/each}
        </div>

        {#if recentReviews.length > ITEMS_PER_PAGE}
          <div class="pagination">
            {#if currentReviewsPage > 1}
              <button class="pagination-button" onclick={() => goToReviewsPage(currentReviewsPage - 1)}>
                ‹ Anterior
              </button>
            {/if}

            {#each Array(getTotalReviewsPages()) as _, i (i)}
              <button class="pagination-button {currentReviewsPage === i + 1 ? 'active' : ''}" onclick={() => goToReviewsPage(i + 1)}>
                {i + 1}
              </button>
            {/each}

            {#if currentReviewsPage < getTotalReviewsPages()}
              <button class="pagination-button" onclick={() => goToReviewsPage(currentReviewsPage + 1)}>
                Próxima ›
              </button>
            {/if}
          </div>
        {/if}
      </section>
    </div>
  </main>
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

  /* Navbar - identical to Album.svelte */
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
  
  .search-and-user {
    display: flex;
    align-items: center;
    gap: 1.5rem;
  }
  
  /* Search Styles */
  .search-form {
    margin-bottom: 0;
  }

  .search-container {
    position: relative;
    max-width: 400px;
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 20px;
    border: 2.5px solid rgba(255, 255, 255, 0.3);
    transition: all 0.3s ease;
    padding-left: 1rem;
    backdrop-filter: blur(20px);
    box-shadow: 
        0 8px 32px rgba(0, 0, 0, 0.3),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
    height: 40px;
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
    padding: 0.5rem;
    background: transparent;
    border: none;
    color: white;
    font-size: 0.9rem;
    outline: none;
  }

  .search-input::placeholder {
    color: rgba(255, 255, 255, 0.6);
    font-family: 'Roboto', sans-serif;
    font-weight: 500;
  }

  .search-button {
    padding: 0.5rem 1.5rem;
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
    height: 100%;
  }

  .search-button:hover {
    background: #1e4c6b;
  }

  .search-button:focus {
    outline: none;
    background: #1e4c6b;
  }

  /* User Avatar Styles */
  .user-menu {
    position: relative;
  }

  .user-avatar {
    width: 40px;
    height: 40px;
    min-width: 40px;
    min-height: 40px;
    max-width: 40px;
    max-height: 40px;
    border-radius: 50%;
    border: 2px solid;
    overflow: hidden;
    background: none;
    cursor: pointer;
    transition: border-color 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0;
    box-sizing: border-box;
    flex-shrink: 0;
    aspect-ratio: 1 / 1;
  }

  .user-avatar:hover {
    border-color: #1e4c6b;
  }

  .user-avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 50%;
  }
  
  /* Botão seguir/seguindo */
  .follow-btn {
    background-color: #255F85;
    color: #fff;
    border: none;
    border-radius: 20px;
    padding: 0.5rem 1.2rem;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.3s ease;
    outline: none;
    margin-left: 1rem;
    min-width: 100px; /* Fixed width to maintain consistent button size */
    text-align: center;
  }
  
  .follow-btn:hover {
    background-color: #1e4c6b;
  }
  
  .follow-btn.following {
    background-color: #8A1A2A;
  }
  
  .follow-btn.following:hover {
    background-color: #72152b;
  }
  
  .login-popup-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0,0,0,0.7);
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .app {
    min-height: 100vh;
    background-color: #14181c;
    margin-top: 60px;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 1rem;
  }

  /* Header Styles */
  .header {
    background-color: #232a33;
    border-bottom: 1px solid #374151;
    padding: 2rem 1.5rem;
  }

    .profile-info {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .avatar {
        width: 96px;
        height: 96px;
        border-radius: 50%;
        border: 2px solid #FFC857;
        overflow: hidden;
        background-color: #2c3440;
    }

    .avatar img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .user-details {
        flex: 1;
    }

    .user-header {
        display: flex;
        flex-direction: column;
        gap: 1rem;
        margin-bottom: 1rem;
    }

    .user-name h1 {
        margin: 0;
        font-size: 1.5rem;
        font-weight: bold;
        font-family: 'Familjen Grotesk', sans-serif;
    }

    .username {
        margin: 0;
        color: #9ca3af;
    }

    .bio {
        color: #d1d5db;
        margin-bottom: 1rem;
        max-width: 32rem;
        line-height: 1.5;
        text-align: justify
    }

    /* Stats Styles */
    .stats {
    display: flex;
    flex-wrap: wrap;
    gap: 1.5rem;
    font-size: 0.875rem;
    color: #9ca3af;
  }

  .stat {
    display: flex;
    align-items: center;
    gap: 0.25rem;
  }

  .stat-number {
    font-weight: 500;
    color: white;
  }

  /* Main Content Styles */
  .main {
    padding: 2rem 0;
  }

  .section {
    margin-bottom: 3rem;
  }

  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 1.5rem;
  }

  .section-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #C5283D;
  }

  .section-title h2 {
    margin: 0;
    font-size: 1.25rem;
    font-weight: bold;
    color: white;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  /* Stats Bar */
  .stats-bar {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 1rem;
    margin-bottom: 2rem;
  }

  .stat-card {
    background-color: #2c3440;
    border-radius: 0.375rem;
    padding: 1rem;
    text-align: center;
  }

  .stat-value {
    font-size: 1.5rem;
    font-weight: bold;
    color: white;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  .stat-label {
    font-size: 0.75rem;
    color: #9ca3af;
  }

  /* Albums Grid - styled like AlbumsOverview.svelte */
  .albums-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1.5rem;
  }

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

  .album-info {
    padding: 0.5rem 0;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
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
  }

  .album-artist {
    color: #9ca3af;
    margin: 0;
    font-size: 0.8125rem;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .album-rating {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .stars-small {
    display: flex;
    align-items: center;
    gap: 2px;
    color: #facc15; /* Amarelo para estrelas preenchidas */
  }

  .star-half-small {
    position: relative;
    display: inline-block;
  }

  .star-half-fill-small {
    position: absolute;
    top: 0;
    left: 0;
    width: 50%;
    overflow: hidden;
    color: #facc15;
  }

  /* Separator */
  .separator {
    height: 1px;
    background-color: #374151;
    margin: 2rem 0;
  }

  /* Reviews */
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
    display: flex;
    flex-direction: column;
    min-height: 200px;
  }

  .review-card:hover {
    border-color: #4b5563;
  }

  .review-layout {
    display: flex;
    gap: 1.5rem;
    height: 100%;
    align-items: stretch;
    min-height: 180px;
  }

  .album-info-review {
    flex-shrink: 0;
    width: 150px;
    display: flex;
    flex-direction: column;
    align-self: stretch;
  }

  .album-image-button {
    background: none;
    border: none;
    padding: 0;
    cursor: pointer;
    height: 150px;
    width: 150px;
    display: flex;
    border-radius: 8px;
    overflow: hidden;
    transition: transform 0.2s ease;
  }

  .album-image-button:hover {
    transform: scale(1.02);
  }

  .album-avatar {
    width: 150px;
    height: 150px;
    border-radius: 8px;
    object-fit: cover;
    border: 2px solid #374151;
    display: block;
    aspect-ratio: 1;
  }

  .review-content-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .review-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .album-details {
    display: flex;
    flex-direction: column;
  }

  .album-name-button {
    background: none;
    border: none;
    padding: 0;
    cursor: pointer;
    text-align: left;
    transition: color 0.2s ease;
  }

  .album-name-button:hover .album-name {
    color: #FFC857;
  }

  .album-name {
    font-size: 1rem;
    font-weight: 600;
    margin: 0;
    color: white;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  .album-artist-year {
    font-size: 0.875rem;
    color: #9ca3af;
    margin: 0;
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

  .review-date {
    font-size: 0.75rem;
    color: #6b7280;
  }

  .review-content {
    margin-bottom: 0;
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
    margin-top: auto;
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
    font-family: 'Familjen Grotesk', sans-serif;
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

  /* Searchbar menor para perfil */
  .profile-search {
    margin-bottom: 0;
    margin-left: 2rem;
    margin-right: 2rem;
    flex: 1;
    max-width: 350px;
  }

  /* Pagination Styles */
  .pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 2rem;
    gap: 0.5rem;
  }

  .pagination-button {
    background-color: #374151; /* Cinza escuro */
    color: white;
    border: 1px solid #4b5563; /* Cinza um pouco mais claro */
    padding: 0.5rem 1rem;
    border-radius: 0.375rem; /* 6px */
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .pagination-button:hover {
    background-color: #4b5563;
  }

  .pagination-button.active {
    background-color: #2563eb; /* Azul */
    border-color: #2563eb;
  }

  .pagination-button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  /* Login Popup */
  .login-popup-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0,0,0,0.7);
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
  }
</style>
