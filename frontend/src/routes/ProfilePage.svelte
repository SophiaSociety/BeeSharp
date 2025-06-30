<script>
  import { Star, Heart, Calendar, Music, Users, MessageSquare, ListMusic, ThumbsUp, ThumbsDown } from 'lucide-svelte'
  import { push } from 'svelte-spa-router'
  import Login from '../lib/Login.svelte'
  
  let showLoginModal = $state(false)
  function openLoginModal() { showLoginModal = true }
  function closeLoginModal() { showLoginModal = false }
  
    let listenedAlbums = $state([
        { id: 1, title: "DAMN.", artist: "Kendrick Lamar", year: "2017", rating: 4.5 },
        { id: 2, title: "Currents", artist: "Tame Impala", year: "2015", rating: 4.0 },
        { id: 3, title: "In Rainbows", artist: "Radiohead", year: "2007", rating: 5.0 },
        { id: 4, title: "MBDTF", artist: "Kanye West", year: "2010", rating: 4.5 },
        { id: 5, title: "Funeral", artist: "Arcade Fire", year: "2004", rating: 3.5 },
        { id: 6, title: "The Suburbs", artist: "Arcade Fire", year: "2010", rating: 4.0 },
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

    function handleFollow() {
        console.log('Follow clicked')
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
            <button class="follow-btn" onclick={handleFollow}>
              Seguir
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
          <button class="view-all">Ver todos</button>
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
          <button class="view-all">Ver todos</button>
        </div>

        <div class="albums-grid">
          {#each listenedAlbums as album (album.id)}
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
      </section>

      <div class="separator"></div>

      <!-- All Reviews -->
      <section class="section">
        <div class="section-header">
          <div class="section-title">
            <MessageSquare size={20} />
            <h2>Reviews</h2>
          </div>
          <button class="view-all">Ver todas</button>
        </div>

        <div class="reviews-list">
          {#each recentReviews as review (review.id)}
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

  .app {
    min-height: 100vh;
    background-color: #14181c;
    margin-top: 80px;
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

    .follow-btn {
        background-color: #C5283D;
        color: white;
        border: none;
        padding: 0.5rem 1rem;
        border-radius: 0.375rem;
        font-weight: 500;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .follow-btn:hover {
        background-color: #8A1A2A;
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

  .view-all {
    font-size: 0.875rem;
    color: #FFC857;
    background: none;
    border: none;
    cursor: pointer;
    text-decoration: underline;
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
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
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
    gap: 1px;
  }

  .star-half-small {
    position: relative;
    display: inline-flex;
    align-items: center;
  }

  .star-half-fill-small {
    position: absolute;
    top: 0;
    left: 0;
    width: 50%;
    overflow: hidden;
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

  /* Star Rating Styles */
  :global(.star-filled) {
    fill: #FFC857;
    color: #FFC857;
  }

  :global(.star-empty) {
    fill: none;
    color: #374151;
  }

  /* Responsive Design */
  @media (min-width: 768px) {
    .profile-info {
      flex-direction: row;
    }

    .user-header {
      flex-direction: row;
      align-items: center;
    }

    .follow-btn {
      margin-left: auto;
    }

    .review-header {
      flex-direction: row;
      align-items: flex-start;
    }

    .stats-bar {
      grid-template-columns: repeat(4, 1fr);
    }

    .albums-grid {
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    }
  }

  @media (max-width: 767px) {
    .albums-grid {
      grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    }

    .review-layout {
      flex-direction: column;
      gap: 1rem;
    }

    .album-info-review {
      width: 150px;
      height: auto;
      margin: 0 auto;
    }

    .album-avatar {
      aspect-ratio: 1;
      width: 150px;
      height: 150px;
    }

    .album-image-button {
      width: 150px;
      height: 150px;
      flex: none;
    }

    .review-header {
      flex-direction: column;
      gap: 0.5rem;
    }

    .review-rating {
      align-items: flex-start;
    }
  }

  @media (max-width: 480px) {
    .review-card {
      padding: 1rem;
    }

    .album-info-review {
      width: 120px;
    }

    .album-avatar {
      width: 120px;
      height: 120px;
    }

    .album-image-button {
      width: 120px;
      height: 120px;
    }
  }
</style>
