<script>
  import { Star, Heart, Calendar, Music, Users, MessageSquare, ListMusic } from 'lucide-svelte'
  
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

    function handleFollow() {
        console.log('Follow clicked')
    }

    function handleAlbumClick(album) {
        console.log('Album clicked:', album.title)
    }
</script>

<nav class="navbar-homepage">
    <div class="navbar-container">
        <div class="logo-component">
            <img src="/logocomtexto.png" alt="BeeSharp Logo" />
        </div>
        <div class="nav-links">
            <a href="/albuns">MEUS ÁLBUNS</a>
            <a href="/amigos">AMIGOS</a>
        </div>
    </div>
</nav>

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

  <!-- Navigation -->
  <nav class="navigation">
    <div class="container">
      <div class="nav-tabs">
        <button class="nav-tab active">Perfil</button>
        <button class="nav-tab">Álbuns</button>
        <button class="nav-tab">Reviews</button>
        <button class="nav-tab">Likes</button>
      </div>
    </div>
  </nav>

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
                  <Heart size={32} />
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

      <div class="separator"></div>

      <!-- Recent Reviews -->
      <section class="section">
        <div class="section-header">
          <div class="section-title">
            <MessageSquare size={20} />
            <h2>Reviews Recentes</h2>
          </div>
          <button class="view-all">Ver todos</button>
        </div>

        <div class="reviews-list">
          {#each recentReviews as review (review.id)}
            <div class="review-card">
              <button class="review-album">
                <img src="/placeholder.svg?height=64&width=64" alt="{review.title} by {review.artist}" />
              </button>

              <div class="review-content">
                <div class="review-header">
                  <div class="review-info">
                    <button class="review-title">{review.title}</button>
                    <p class="review-artist">{review.artist} • {review.year}</p>
                  </div>

                  <div class="review-rating">
                    <div class="stars">
                      {#each renderStars(review.rating).fullStars as _}
                        <Star size={16} class="star-filled" />
                      {/each}
                      {#if renderStars(review.rating).hasHalfStar}
                        <div class="star-half">
                          <Star size={16} class="star-empty" />
                          <div class="star-half-fill">
                            <Star size={16} class="star-filled" />
                          </div>
                        </div>
                      {/if}
                      {#each renderStars(review.rating).emptyStars as _}
                        <Star size={16} class="star-empty" />
                      {/each}
                    </div>
                    {#if review.liked}
                      <Heart size={16} class="heart-liked" />
                    {/if}
                  </div>
                </div>

                <p class="review-text">{review.review}</p>

                <div class="review-date">
                  <Calendar size={16} />
                  <span>{review.date}</span>
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
        background: #131313;
        height: 60px; 
        min-height: 60px;
    }

    .navbar-container {
        width: 1500px;
        margin: 0 auto;
        display: flex;
        justify-content:space-between;
        align-items: center;
    }

    .logo-component img {
        height: 60px;
        cursor: pointer;
    }

    .nav-links a {
        margin-left: 1.5rem;
        margin-right: 3.5rem;
        color: white;
        font-family: 'Familjen Grotesk', sans-serif;
        font-weight: bold;
        font-size: 14px;
        letter-spacing: 0.1em;
        text-decoration: none;
        white-space: nowrap;
        overflow-x: auto;
    }

    :global(body) {
        margin: 0;
        padding: 0;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        background-color: #181b20;
        color: white;
    }

    .app {
        min-height: 100vh;
        background-color: #14181c;
        margin-top: 72px;
        border-radius: 1.5rem;
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
        border-radius: 1.5rem 1.5rem 0 0;
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

    /* Navigation Styles */
    .navigation {
        background-color: #1d232a;
        border-bottom: 1px solid #374151;
        position: sticky;
        top: 0;
        z-index: 10;
    }

    .nav-tabs {
        display: flex;
        overflow-x: auto;
    }

    .nav-tab {
        padding: 0.75rem 1rem;
        font-size: 0.875rem;
        font-weight: 500;
        color: #9ca3af;
        background: none;
        border: none;
        cursor: pointer;
        transition: color 0.2s;
        border-bottom: 2px solid transparent;
    }

    .nav-tab:hover {
        color: white;
    }

    .nav-tab.active {
        color: #FFC857;
        border-bottom-color: #FFC857;
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
    }

    .stat-label {
        font-size: 0.75rem;
        color: #9ca3af;
    }

    /* Albums Grid */
    .albums-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
        gap: 1rem;
    }

    .album-card {
        background: none;
        border: none;
        cursor: pointer;
        text-align: left;
    }

    .album-cover {
        position: relative;
        aspect-ratio: 1;
        margin-bottom: 0.5rem;
        overflow: hidden;
        border-radius: 0.375rem;
        background-color: #374151;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    }

    .album-cover img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.2s;
    }

    .album-card:hover .album-cover img {
        transform: scale(1.05);
    }

    .album-overlay {
        position: absolute;
        inset: 0;
        background-color: rgba(0, 0, 0, 0);
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s;
        color: white;
    }

    .album-card:hover .album-overlay {
        background-color: rgba(0, 0, 0, 0.4);
    }

    .album-info {
        font-size: 0.875rem;
    }

    .album-title {
        font-weight: 500;
        color: white;
        margin: 0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .album-artist {
        color: #9ca3af;
        margin: 0;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
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
        background-color: #2c3440;
        border-radius: 0.5rem;
        padding: 1.5rem;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
        display: flex;
        gap: 1rem;
    }

    .review-album {
        flex-shrink: 0;
        background: none;
        border: none;
        cursor: pointer;
    }

    .review-album img {
        width: 135px;
        height: 135px;
        aspect-ratio: 1 / 1;
        border-radius: 0.375rem;
        background-color: #374151;
        object-fit: cover;
        transition: transform 0.2s;
        display: block;
    }

    .review-album:hover img {
        transform: scale(1.05);
    }

    .review-content {
        flex: 1;
        min-width: 0;
    }

    .review-header {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
        margin-bottom: 0.75rem;
    }

    .review-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: flex-start;
    }

    .review-title {
        font-size: 1.125rem;
        font-weight: 600;
        color: white;
        background: none;
        border: none;
        cursor: pointer;
        text-align: left;
        padding: 0;
        transition: color 0.2s;
        outline: none;
        width: 100%;
        text-align: left;
    }

    .review-title:focus,
    .review-title:active {
        color: #FFC857;
        background: rgba(255, 200, 87, 0.12); /* leve destaque amarelo translúcido */
    }

    .review-title:hover {
        color: #FFC857;
    }

    .review-artist {
        color: #9ca3af;
        margin: 0;
    }

    .review-rating {
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .stars {
        display: flex;
        align-items: center;
        gap: 0.125rem;
    }

    .star-half {
        position: relative;
        display: inline-flex;
        align-items: center;
        height: 18px;
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

    :global(.star-filled) {
        fill: #FFC587;
        color: #FFC587;
    }

    :global(.star-empty) {
    fill: none;
    color: #6b7280;
    }

    :global(.heart-liked) {
        fill: #C5283D;
        color: #C5283D;
    }

    .review-text {
        color: #d1d5db;
        line-height: 1.6;
        margin-bottom: 0.75rem;
        text-align: justify;
    }

    .review-date {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.875rem;
        color: #9ca3af;
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
        grid-template-columns: repeat(4, 1fr);
        }
    }
</style>
