<script>
    import { Music, Search, ChevronLeft, ChevronRight, Star, TrendingUp, Award, ArrowRight, Play, Heart, MessageSquare, Calendar, User, Headphones, Disc, Users } from 'lucide-svelte'
    import { push } from 'svelte-spa-router'
    import { onMount } from 'svelte'
    
    let searchQuery = $state('')
    let recommendedCarouselIndex = $state(0)
    let hotCarouselIndex = $state(0)
    let genreCarouselIndex = $state(0)
  
    // Mock data for recommended albums
    let recommendedAlbums = [
        { id: 1, title: "The Tortured Poets Department", artist: "Taylor Swift", year: "2024", rating: 4.2, image: "/placeholder.svg?height=300&width=300", genre: "Pop" },
        { id: 2, title: "Hit Me Hard and Soft", artist: "Billie Eilish", year: "2024", rating: 4.5, image: "/placeholder.svg?height=300&width=300", genre: "Alternative" },
        { id: 3, title: "Brat", artist: "Charli XCX", year: "2024", rating: 4.3, image: "/placeholder.svg?height=300&width=300", genre: "Pop" },
        { id: 4, title: "Short n' Sweet", artist: "Sabrina Carpenter", year: "2024", rating: 4.1, image: "/placeholder.svg?height=300&width=300", genre: "Pop" },
        { id: 5, title: "Eternal Sunshine", artist: "Ariana Grande", year: "2024", rating: 4.0, image: "/placeholder.svg?height=300&width=300", genre: "R&B" },
        { id: 6, title: "The Rise and Fall of a Midwest Princess", artist: "Chappell Roan", year: "2023", rating: 4.4, image: "/placeholder.svg?height=300&width=300", genre: "Pop" }
    ]
    
    // Mock data for hot albums
    let hotAlbums = [
        { id: 7, title: "GUTS", artist: "Olivia Rodrigo", year: "2023", rating: 4.2, image: "/placeholder.svg?height=300&width=300", trending: true },
        { id: 8, title: "Did you know that there's a tunnel under Ocean Blvd", artist: "Lana Del Rey", year: "2023", rating: 4.1, image: "/placeholder.svg?height=300&width=300", trending: true },
        { id: 9, title: "The Record", artist: "boygenius", year: "2023", rating: 4.6, image: "/placeholder.svg?height=300&width=300", trending: true },
        { id: 10, title: "Genesis", artist: "RAYE", year: "2023", rating: 4.3, image: "/placeholder.svg?height=300&width=300", trending: true },
        { id: 11, title: "Heaven Knows", artist: "PinkPantheress", year: "2023", rating: 4.0, image: "/placeholder.svg?height=300&width=300", trending: true },
        { id: 12, title: "Scaring the Hoes", artist: "JPEGMAFIA & Danny Brown", year: "2023", rating: 4.2, image: "/placeholder.svg?height=300&width=300", trending: true }
    ]
    
    // Mock data for genre spotlight
    let genreAlbums = [
        { id: 13, title: "OK Computer", artist: "Radiohead", year: "1997", rating: 4.8, image: "/placeholder.svg?height=300&width=300", genre: "Alternative Rock" },
        { id: 14, title: "The Bends", artist: "Radiohead", year: "1995", rating: 4.5, image: "/placeholder.svg?height=300&width=300", genre: "Alternative Rock" },
        { id: 15, title: "In Rainbows", artist: "Radiohead", year: "2007", rating: 4.7, image: "/placeholder.svg?height=300&width=300", genre: "Alternative Rock" },
        { id: 16, title: "Kid A", artist: "Radiohead", year: "2000", rating: 4.6, image: "/placeholder.svg?height=300&width=300", genre: "Alternative Rock" },
        { id: 17, title: "Hail to the Thief", artist: "Radiohead", year: "2003", rating: 4.3, image: "/placeholder.svg?height=300&width=300", genre: "Alternative Rock" },
        { id: 18, title: "Amnesiac", artist: "Radiohead", year: "2001", rating: 4.4, image: "/placeholder.svg?height=300&width=300", genre: "Alternative Rock" }
    ]
    
    // Mock data for friends' activity
    let friendsActivity = [
        {
        id: 1,
        user: { name: "Alex Chen", username: "@alexmusic", avatar: "/placeholder.svg?height=40&width=40" },
        action: "reviewed",
        album: { title: "The Tortured Poets Department", artist: "Taylor Swift", image: "/placeholder.svg?height=60&width=60" },
        rating: 4,
        comment: "Swift's most introspective work yet. The storytelling is incredible.",
        time: "2 hours ago"
        },
        {
        id: 2,
        user: { name: "Sarah Johnson", username: "@sarahbeats", avatar: "/placeholder.svg?height=40&width=40" },
        action: "liked",
        album: { title: "Brat", artist: "Charli XCX", image: "/placeholder.svg?height=60&width=60" },
        time: "4 hours ago"
        },
        {
        id: 3,
        user: { name: "Mike Rodriguez", username: "@mikevibes", avatar: "/placeholder.svg?height=40&width=40" },
        action: "added to list",
        album: { title: "Hit Me Hard and Soft", artist: "Billie Eilish", image: "/placeholder.svg?height=60&width=60" },
        listName: "Albums of 2024",
        time: "6 hours ago"
        },
        {
        id: 4,
        user: { name: "Emma Wilson", username: "@emmawilson", avatar: "/placeholder.svg?height=40&width=40" },
        action: "reviewed",
        album: { title: "Genesis", artist: "RAYE", image: "/placeholder.svg?height=60&width=60" },
        rating: 5,
        comment: "Absolutely stunning debut. RAYE's vocals are otherworldly.",
        time: "8 hours ago"
        }
    ]
    
    // Mock data for featured artists
    let featuredArtists = [
        { id: 1, name: "Taylor Swift", image: "/placeholder.svg?height=150&width=150", albumCount: 12, avgRating: 4.3 },
        { id: 2, name: "Kendrick Lamar", image: "/placeholder.svg?height=150&width=150", albumCount: 8, avgRating: 4.7 },
        { id: 3, name: "Radiohead", image: "/placeholder.svg?height=150&width=150", albumCount: 15, avgRating: 4.5 },
        { id: 4, name: "Frank Ocean", image: "/placeholder.svg?height=150&width=150", albumCount: 4, avgRating: 4.6 }
    ]
    
    function handleSearch(event) {
        event.preventDefault()
        console.log('Searching for:', searchQuery)
        // Implement search functionality
    }
    
    function nextCarousel(type) {
        const albums = type === 'recommended' ? recommendedAlbums : type === 'hot' ? hotAlbums : genreAlbums
        const maxIndex = Math.max(0, albums.length - 4)
        
        if (type === 'recommended') {
        recommendedCarouselIndex = (recommendedCarouselIndex + 1) % (maxIndex + 1)
        } else if (type === 'hot') {
        hotCarouselIndex = (hotCarouselIndex + 1) % (maxIndex + 1)
        } else {
        genreCarouselIndex = (genreCarouselIndex + 1) % (maxIndex + 1)
        }
    }
    
    function prevCarousel(type) {
        const albums = type === 'recommended' ? recommendedAlbums : type === 'hot' ? hotAlbums : genreAlbums
        const maxIndex = Math.max(0, albums.length - 4)
        
        if (type === 'recommended') {
        recommendedCarouselIndex = recommendedCarouselIndex === 0 ? maxIndex : recommendedCarouselIndex - 1
        } else if (type === 'hot') {
        hotCarouselIndex = hotCarouselIndex === 0 ? maxIndex : hotCarouselIndex - 1
        } else {
        genreCarouselIndex = genreCarouselIndex === 0 ? maxIndex : genreCarouselIndex - 1
        }
    }
    
    function handleAlbumClick(album) {
        console.log('Album clicked:', album.title)
        // Navigate to album detail page
    }
    
    function handleArtistClick(artist) {
        console.log('Artist clicked:', artist.name)
        // Navigate to artist page
    }
    
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
    
    // Estado reativo para manter o tamanho da janela e rastrear quais títulos devem ter uma ou duas linhas
    let windowWidth = $state(typeof window !== 'undefined' ? window.innerWidth : 0);
    let titleSizes = $state({});
    
    // Função para calcular o tamanho ideal dos títulos sem manipulação direta do DOM
    function calculateTitleSize(title, containerWidth) {
        // Estimar largura do texto baseado no número de caracteres e no tamanho médio de caractere em pixels
        // Esta é uma estimativa aproximada que funciona bem para a maioria dos casos
        const avgCharWidth = 7; // Largura média estimada de um caractere em pixels com fonte de 1rem
        const textLength = title.length;
        const estimatedWidth = textLength * avgCharWidth;
        
        // Verificar se o título provavelmente caberá em uma linha
        const willFitInOneLine = estimatedWidth <= containerWidth * 0.85;
        
        return {
            isSingleLine: willFitInOneLine,
            fontSize: willFitInOneLine ? '0.875rem' : '0.78rem',
            fontWeight: willFitInOneLine ? '700' : '600',
            lineHeight: willFitInOneLine ? '1.2' : '1.1'
        };
    }
    
    // Função para calcular e atualizar os tamanhos dos títulos
    function updateTitleSizes() {
        // Estimar a largura disponível para cada título baseado no layout atual
        const estimatedContainerWidth = windowWidth > 1200 
            ? 250  // Em telas grandes, cada card ocupa aproximadamente 250px
            : windowWidth > 768 
                ? 180  // Em telas médias
                : 150; // Em telas pequenas
        
        // Atualizar o tamanho de cada título
        const newTitleSizes = {};
        
        // Calcular para álbuns recomendados
        recommendedAlbums.forEach(album => {
            newTitleSizes[`album-${album.id}`] = calculateTitleSize(album.title, estimatedContainerWidth);
        });
        
        // Calcular para álbuns em alta
        hotAlbums.forEach(album => {
            newTitleSizes[`album-${album.id}`] = calculateTitleSize(album.title, estimatedContainerWidth);
        });
        
        // Calcular para álbuns por gênero
        genreAlbums.forEach(album => {
            newTitleSizes[`album-${album.id}`] = calculateTitleSize(album.title, estimatedContainerWidth);
        });
        
        // Atualizar o estado
        titleSizes = newTitleSizes;
    }
    
    onMount(() => {
        // Inicializar os tamanhos dos títulos
        updateTitleSizes();
        
        // Reajustar quando a janela for redimensionada
        const handleResize = () => {
            windowWidth = window.innerWidth;
            updateTitleSizes();
        };
        window.addEventListener('resize', handleResize);
        
        // Cleanup
        return () => {
            window.removeEventListener('resize', handleResize);
        };
    });
    
    // Removed afterUpdate (deprecated in runes mode)
    // If you need to react to changes, use reactive statements ($:) or onMount as needed.
</script>

<div class="homepage">
    <!-- Top Navigation Bar -->
    <nav class="navbar-albums">
        <div class="navbar-container">
            <div class="logo-component">
                <button class="logo-button" onclick={() => push('/')} aria-label="MusicBoxd">
                    <img src="/logocomtexto.png" alt="MusicBoxd Logo" />
                </button>
            </div>
            <div class="nav-links">
                <a href="#/albuns">Álbuns</a>
                <div class="user-menu">
                    <button class="user-avatar" aria-label="User Profile">
                        <img src="/placeholder.svg?height=32&width=32" alt="User" />
                    </button>
                </div>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container">
        <div class="hero-content">
            <div class="hero-text">
            <h1 class="hero-title">Your Music Journey Starts Here</h1>
            <p class="hero-subtitle">
                Discover, rate, and share albums with a passionate community of music lovers. 
                Track your listening journey and find your next favorite album.
            </p>
            </div>
            
            <form class="search-form" onsubmit={handleSearch}>
            <div class="search-container">
                <Search size={20} class="search-icon" />
                <input
                type="text"
                class="search-input"
                placeholder="Search for albums, artists, or songs..."
                bind:value={searchQuery}
                />
                <button type="submit" class="search-button">
                Search
                </button>
            </div>
            </form>

            <div class="hero-stats">
            <div class="stat-item">
                <div class="stat-number">50K+</div>
                <div class="stat-label">Albums</div>
            </div>
            <div class="stat-item">
                <div class="stat-number">1M+</div>
                <div class="stat-label">Reviews</div>
            </div>
            <div class="stat-item">
                <div class="stat-number">25K+</div>
                <div class="stat-label">Users</div>
            </div>
            </div>
        </div>
        </div>
    </section>

    <main class="main-content">
        <div class="container">
        <!-- Recommended Albums -->
        <section class="section">
            <div class="section-header">
            <div class="section-title">
                <Award size={24} />
                <h2>Recommended for You</h2>
            </div>
            <div class="carousel-controls">
                <button class="carousel-btn" onclick={() => prevCarousel('recommended')} aria-label="Previous albums">
                <ChevronLeft size={20} />
                </button>
                <button class="carousel-btn" onclick={() => nextCarousel('recommended')} aria-label="Next albums">
                <ChevronRight size={20} />
                </button>
            </div>
            </div>

            <div class="carousel-container">
            <div class="carousel-track" style="transform: translateX(-{recommendedCarouselIndex * 25}%)">
                {#each recommendedAlbums as album (album.id)}
                <div class="carousel-item">
                    <button class="album-card" onclick={() => handleAlbumClick(album)}>
                    <div class="album-cover">
                        <img src={album.image || "/placeholder.svg"} alt="{album.title} by {album.artist}" />
                        <div class="album-overlay">
                        <div class="play-button">
                            <Play size={24} />
                        </div>
                        </div>
                        <div class="genre-badge">{album.genre}</div>
                    </div>
                    <div class="album-info">
                        <h3 class="album-title" style={titleSizes[`album-${album.id}`] 
                            ? `font-size: ${titleSizes[`album-${album.id}`].fontSize}; 
                               font-weight: ${titleSizes[`album-${album.id}`].fontWeight}; 
                               line-height: ${titleSizes[`album-${album.id}`].lineHeight};` 
                            : ''}>
                            {album.title}
                        </h3>
                        <p class="album-artist">{album.artist} • {album.year}</p>
                        <div class="album-rating">
                        <div class="stars-container">
                            {#each renderStars(album.rating) as star (star.key)}
                            {#if star.type === 'full'}
                                <Star size={12} class="star-filled" />
                            {:else if star.type === 'half'}
                                <div class="star-half-container">
                                <Star size={12} class="star-empty" />
                                <div class="star-half-overlay">
                                    <Star size={12} class="star-filled" />
                                </div>
                                </div>
                            {:else}
                                <Star size={12} class="star-empty" />
                            {/if}
                            {/each}
                        </div>
                        <span class="rating-text">{album.rating}</span>
                        </div>
                    </div>
                    </button>
                </div>
                {/each}
            </div>
            </div>
        </section>

        <!-- Hot Right Now -->
        <section class="section">
            <div class="section-header">
            <div class="section-title">
                <TrendingUp size={24} />
                <h2>Hot Right Now</h2>
            </div>
            <div class="carousel-controls">
                <button class="carousel-btn" onclick={() => prevCarousel('hot')} aria-label="Previous albums">
                <ChevronLeft size={20} />
                </button>
                <button class="carousel-btn" onclick={() => nextCarousel('hot')} aria-label="Next albums">
                <ChevronRight size={20} />
                </button>
            </div>
            </div>

            <div class="carousel-container">
            <div class="carousel-track" style="transform: translateX(-{hotCarouselIndex * 25}%)">
                {#each hotAlbums as album (album.id)}
                <div class="carousel-item">
                    <button class="album-card hot-album" onclick={() => handleAlbumClick(album)}>
                    <div class="album-cover">
                        <img src={album.image || "/placeholder.svg"} alt="{album.title} by {album.artist}" />
                        <div class="album-overlay">
                        <div class="play-button">
                            <Play size={24} />
                        </div>
                        </div>
                        <div class="trending-badge">
                        <TrendingUp size={14} />
                        Hot
                        </div>
                    </div>
                    <div class="album-info">
                        <h3 class="album-title" style={titleSizes[`album-${album.id}`] 
                            ? `font-size: ${titleSizes[`album-${album.id}`].fontSize}; 
                               font-weight: ${titleSizes[`album-${album.id}`].fontWeight}; 
                               line-height: ${titleSizes[`album-${album.id}`].lineHeight};` 
                            : ''}>
                            {album.title}
                        </h3>
                        <p class="album-artist">{album.artist} • {album.year}</p>
                        <div class="album-rating">
                        <div class="stars-container">
                            {#each renderStars(album.rating) as star (star.key)}
                            {#if star.type === 'full'}
                                <Star size={12} class="star-filled" />
                            {:else if star.type === 'half'}
                                <div class="star-half-container">
                                <Star size={12} class="star-empty" />
                                <div class="star-half-overlay">
                                    <Star size={12} class="star-filled" />
                                </div>
                                </div>
                            {:else}
                                <Star size={12} class="star-empty" />
                            {/if}
                            {/each}
                        </div>
                        <span class="rating-text">{album.rating}</span>
                        </div>
                    </div>
                    </button>
                </div>
                {/each}
            </div>
            </div>
        </section>

        <!-- Two Column Layout -->
        <div class="two-column-layout">
            <!-- Activity from Friends -->
            <section class="activity-section">
            <div class="section-header">
                <h2 class="section-title">
                <Users size={24} />
                Activity from Friends
                </h2>
                <button class="view-all-link">View All</button>
            </div>

            <div class="activity-list">
                {#each friendsActivity as activity (activity.id)}
                <div class="activity-item">
                    <div class="activity-avatar">
                    <img src={activity.user.avatar || "/placeholder.svg"} alt={activity.user.name} />
                    </div>
                    <div class="activity-content">
                    <div class="activity-header">
                        <span class="user-name">{activity.user.name}</span>
                        <span class="activity-action">{activity.action}</span>
                        {#if activity.listName}
                        <span class="list-name">"{activity.listName}"</span>
                        {/if}
                    </div>
                    
                    <div class="activity-album">
                        <img src={activity.album.image || "/placeholder.svg"} alt={activity.album.title} class="album-thumbnail" />
                        <div class="album-details">
                        <h4 class="album-title">{activity.album.title}</h4>
                        <p class="album-artist">{activity.album.artist}</p>
                        </div>
                    </div>

                    {#if activity.rating}
                        <div class="activity-rating">
                        <div class="stars-container">
                            {#each renderStars(activity.rating) as star (star.key)}
                            {#if star.type === 'full'}
                                <Star size={12} class="star-filled" />
                            {:else}
                                <Star size={12} class="star-empty" />
                            {/if}
                            {/each}
                        </div>
                        </div>
                    {/if}

                    {#if activity.comment}
                        <p class="activity-comment">"{activity.comment}"</p>
                    {/if}

                    <div class="activity-time">
                        <Calendar size={12} />
                        <span>{activity.time}</span>
                    </div>
                    </div>
                </div>
                {/each}
            </div>
            </section>

            <!-- Featured Artists -->
            <section class="featured-section">
            <div class="section-header">
                <h2 class="section-title">
                <User size={24} />
                Featured Artists
                </h2>
            </div>

            <div class="artists-grid">
                {#each featuredArtists as artist (artist.id)}
                <button class="artist-card" onclick={() => handleArtistClick(artist)}>
                    <div class="artist-image">
                    <img src={artist.image || "/placeholder.svg"} alt={artist.name} />
                    <div class="artist-overlay">
                        <Headphones size={24} />
                    </div>
                    </div>
                    <div class="artist-info">
                    <h3 class="artist-name">{artist.name}</h3>
                    <p class="artist-stats">{artist.albumCount} albums • {artist.avgRating} avg</p>
                    </div>
                </button>
                {/each}
            </div>
            </section>
        </div>

        <!-- Genre Spotlight -->
        <section class="section">
            <div class="section-header">
            <div class="section-title">
                <Disc size={24} />
                <h2>Genre Spotlight: Alternative Rock</h2>
            </div>
            <div class="carousel-controls">
                <button class="carousel-btn" onclick={() => prevCarousel('genre')} aria-label="Previous albums">
                <ChevronLeft size={20} />
                </button>
                <button class="carousel-btn" onclick={() => nextCarousel('genre')} aria-label="Next albums">
                <ChevronRight size={20} />
                </button>
            </div>
            </div>

            <div class="carousel-container">
            <div class="carousel-track" style="transform: translateX(-{genreCarouselIndex * 25}%)">
                {#each genreAlbums as album (album.id)}
                <div class="carousel-item">
                    <button class="album-card genre-album" onclick={() => handleAlbumClick(album)}>
                    <div class="album-cover">
                        <img src={album.image || "/placeholder.svg"} alt="{album.title} by {album.artist}" />
                        <div class="album-overlay">
                        <div class="play-button">
                            <Play size={24} />
                        </div>
                        </div>
                    </div>
                    <div class="album-info">
                        <h3 class="album-title" style={titleSizes[`album-${album.id}`] 
                            ? `font-size: ${titleSizes[`album-${album.id}`].fontSize}; 
                               font-weight: ${titleSizes[`album-${album.id}`].fontWeight}; 
                               line-height: ${titleSizes[`album-${album.id}`].lineHeight};` 
                            : ''}>
                            {album.title}
                        </h3>
                        <p class="album-artist">{album.artist} • {album.year}</p>
                        <div class="album-rating">
                        <div class="stars-container">
                            {#each renderStars(album.rating) as star (star.key)}
                            {#if star.type === 'full'}
                                <Star size={12} class="star-filled" />
                            {:else if star.type === 'half'}
                                <div class="star-half-container">
                                <Star size={12} class="star-empty" />
                                <div class="star-half-overlay">
                                    <Star size={12} class="star-filled" />
                                </div>
                                </div>
                            {:else}
                                <Star size={12} class="star-empty" />
                            {/if}
                            {/each}
                        </div>
                        <span class="rating-text">{album.rating}</span>
                        </div>
                    </div>
                    </button>
                </div>
                {/each}
            </div>
            </div>
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
    }

    .homepage {
        min-height: 100vh;
        background-color: #14181c;
        padding-top: 80px;
    }

    .container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 1rem;
    }

    /* Top Navigation Bar */
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

    .user-menu {
        position: relative;
    }

    .user-avatar {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        border: 2px solid #FFC857;
        overflow: hidden;
        background: none;
        cursor: pointer;
        transition: border-color 0.2s;
    }

    .user-avatar:hover {
        border-color: #d6a639;
    }

    .user-avatar img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    /* Hero Section */
    .hero-section {
        background: #255F85;
        padding: 4rem 0;
        text-align: center;
        position: relative;
        overflow: hidden;
    }

    .hero-content {
        max-width: 800px;
        margin: 0 auto;
    }

    .hero-text {
        margin-bottom: 3rem;
    }

    .hero-title {
        font-size: 3.5rem;
        font-weight: 700;
        margin: 0 0 1rem 0;
        background: linear-gradient(135deg, #00e054 0%, #00c048 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        line-height: 1.2;
    }

    .hero-subtitle {
        font-size: 1.25rem;
        color: #9ca3af;
        margin: 0;
        line-height: 1.6;
        max-width: 600px;
        margin: 0 auto;
    }

    .search-form {
        margin-bottom: 3rem;
    }

    .search-container {
        position: relative;
        max-width: 600px;
        margin: 0 auto;
        display: flex;
        align-items: center;
        background: #2c3440;
        border: 2px solid #374151;
        border-radius: 12px;
        transition: all 0.2s ease;
    }

    .search-container:focus-within {
        border-color: #00e054;
        box-shadow: 0 0 0 4px rgba(0, 224, 84, 0.1);
    }

    .search-input {
        flex: 1;
        padding: 1rem 1rem 1rem 3rem;
        background: transparent;
        border: none;
        color: white;
        font-size: 1rem;
        outline: none;
    }

    .search-input::placeholder {
        color: #6b7280;
    }

    .search-button {
        padding: 1rem 2rem;
        background: #00e054;
        color: #14181c;
        border: none;
        border-radius: 0 10px 10px 0;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .search-button:hover {
        background: #00c048;
    }

    .hero-stats {
        display: flex;
        justify-content: center;
        gap: 3rem;
    }

    .stat-item {
        text-align: center;
    }

    .stat-number {
        font-size: 2rem;
        font-weight: 700;
        color: #00e054;
        line-height: 1;
    }

    .stat-label {
        font-size: 0.875rem;
        color: #9ca3af;
        margin-top: 0.25rem;
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
        color: #00e054;
        font-size: 1.75rem;
        font-weight: 700;
        margin: 0;
    }

    .section-title h2 {
        margin: 0;
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

    /* Album Cards - consistente com as outras páginas */
    .album-card {
        background: none;
        border: none;
        cursor: pointer;
        text-align: left;
        width: 100%;
        transition: transform 0.2s ease, box-shadow 0.2s ease;
        position: relative;
        will-change: transform;
        border-radius: 10px;
    }

    .album-card:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 20px rgba(0, 0, 0, 0.3);
        z-index: 2;
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
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }

    .album-card:hover .play-button {
        transform: scale(1);
    }

    .trending-badge {
        position: absolute;
        top: 0.5rem;
        right: 0.5rem;
        background: #ff6b35;
        color: white;
        padding: 0.25rem 0.5rem;
        border-radius: 12px;
        font-size: 0.75rem;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 0.25rem;
    }

    .genre-badge {
        position: absolute;
        top: 0.5rem;
        left: 0.5rem;
        background: rgba(0, 224, 84, 0.9);
        color: white;
        padding: 0.25rem 0.5rem;
        border-radius: 12px;
        font-size: 0.75rem;
        font-weight: 600;
    }

    .album-info {
        padding: 0.5rem 0;
    }

    .album-title {
        font-size: 0.875rem;
        font-weight: 600;
        color: white;
        margin: 0 0 0.25rem 0;
        line-height: 1.3;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
    }

    .album-artist {
        font-size: 0.75rem;
        color: #9ca3af;
        margin: 0 0 0.5rem 0;
        line-height: 1.3;
    }

    .album-rating {
        display: flex;
        align-items: center;
        gap: 0.5rem;
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

    /* Two Column Layout */
    .two-column-layout {
        display: grid;
        grid-template-columns: 2fr 1fr;
        gap: 3rem;
        margin-bottom: 4rem;
    }

    /* Activity Section */
    .activity-section {
        background: #1d232a;
        border-radius: 12px;
        padding: 2rem;
    }

    .view-all-link {
        background: none;
        border: none;
        color: #00e054;
        cursor: pointer;
        font-size: 0.875rem;
        text-decoration: underline;
        transition: color 0.2s ease;
    }

    .view-all-link:hover {
        color: #00c048;
    }

    .activity-list {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .activity-item {
        display: flex;
        gap: 1rem;
        align-items: flex-start;
    }

    .activity-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        overflow: hidden;
        flex-shrink: 0;
        border: 2px solid #374151;
    }

    .activity-avatar img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .activity-content {
        flex: 1;
        min-width: 0;
    }

    .activity-header {
        margin-bottom: 0.75rem;
    }

    .user-name {
        font-weight: 600;
        color: white;
    }

    .activity-action {
        color: #9ca3af;
        margin: 0 0.25rem;
    }

    .list-name {
        color: #00e054;
        font-weight: 500;
    }

    .activity-album {
        display: flex;
        gap: 0.75rem;
        align-items: center;
        margin-bottom: 0.5rem;
    }

    .album-thumbnail {
        width: 40px;
        height: 40px;
        border-radius: 4px;
        object-fit: cover;
    }

    .album-details .album-title {
        font-size: 0.875rem;
        font-weight: 600;
        color: white;
        margin: 0;
    }

    .album-details .album-artist {
        font-size: 0.75rem;
        color: #9ca3af;
        margin: 0;
    }

    .activity-rating {
        margin-bottom: 0.5rem;
    }

    .activity-comment {
        color: #d1d5db;
        font-style: italic;
        margin: 0 0 0.5rem 0;
        font-size: 0.875rem;
    }

    .activity-time {
        display: flex;
        align-items: center;
        gap: 0.25rem;
        font-size: 0.75rem;
        color: #6b7280;
    }

    /* Featured Section */
    .featured-section {
        background: #1d232a;
        border-radius: 12px;
        padding: 2rem;
    }

    .artists-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1rem;
    }

    .artist-card {
        background: none;
        border: none;
        cursor: pointer;
        text-align: center;
        transition: transform 0.2s ease;
    }

    .artist-card:hover {
        transform: translateY(-2px);
    }

    .artist-image {
        position: relative;
        aspect-ratio: 1;
        margin-bottom: 0.5rem;
        overflow: hidden;
        border-radius: 50%;
        background-color: #374151;
    }

    .artist-image img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.2s ease;
    }

    .artist-card:hover .artist-image img {
        transform: scale(1.05);
    }

    .artist-overlay {
        position: absolute;
        inset: 0;
        background: rgba(0, 0, 0, 0);
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s ease;
        color: white;
    }

    .artist-card:hover .artist-overlay {
        background: rgba(0, 0, 0, 0.6);
    }

    .artist-name {
        font-size: 0.875rem;
        font-weight: 600;
        color: white;
        margin: 0 0 0.25rem 0;
    }

    .artist-stats {
        font-size: 0.75rem;
        color: #9ca3af;
        margin: 0;
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

    /* Star Rating Styles */
    :global(.star-filled) {
        fill: #FFC857;
        color: #FFC857;
    }

    :global(.star-empty) {
        fill: none;
        color: #374151;
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

    @media (max-width: 480px) {
        .carousel-item {
        flex: 0 0 calc(100% - 0rem);
        }

        .hero-stats {
        flex-direction: column;
        gap: 1rem;
        }

        .footer-links {
        grid-template-columns: 1fr;
        }

        .artists-grid {
        grid-template-columns: 1fr;
        }
    }

    /* Navbar - idêntica às outras páginas */
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

    .user-avatar {
        background: none;
        border: 2px solid rgba(255, 255, 255, 0.7);
        border-radius: 50%;
        width: 40px;
        height: 40px;
        padding: 1px;
        overflow: hidden;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .user-avatar:hover {
        border-color: white;
        transform: scale(1.05);
    }

    .user-avatar img {
        width: 100%;
        height: 100%;
        border-radius: 50%;
        object-fit: cover;
    }

    .homepage {
        padding-top: 80px;
    }
</style>
