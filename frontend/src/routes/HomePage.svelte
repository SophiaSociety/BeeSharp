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
        action: "avaliou",
        album: { title: "The Tortured Poets Department", artist: "Taylor Swift", image: "/placeholder.svg?height=60&width=60" },
        rating: 4,
        comment: "O trabalho mais introspectivo da Swift até agora. A narrativa é incrível.",
        time: "2 horas atrás"
        },
        {
        id: 2,
        user: { name: "Emma Wilson", username: "@emmawilson", avatar: "/placeholder.svg?height=40&width=40" },
        action: "avaliou",
        album: { title: "Genesis", artist: "RAYE", image: "/placeholder.svg?height=60&width=60" },
        rating: 5,
        comment: "Estreia absolutamente deslumbrante. A voz da RAYE é sobrenatural.",
        time: "8 horas atrás"
        },
        {
        id: 3,
        user: { name: "Carlos Silva", username: "@carlosmusic", avatar: "/placeholder.svg?height=40&width=40" },
        action: "avaliou",
        album: { title: "GUTS", artist: "Olivia Rodrigo", image: "/placeholder.svg?height=60&width=60" },
        rating: 4.5,
        comment: null,
        time: "12 horas atrás"
        },
        {
        id: 4,
        user: { name: "Marina Santos", username: "@marinasants", avatar: "/placeholder.svg?height=40&width=40" },
        action: "avaliou",
        album: { title: "Midnights", artist: "Taylor Swift", image: "/placeholder.svg?height=60&width=60" },
        rating: 3.5,
        comment: "Bom, mas não o melhor trabalho dela. Algumas faixas são muito repetitivas.",
        time: "1 dia atrás"
        },
        {
        id: 5,
        user: { name: "João Pedro", username: "@joaopmusic", avatar: "/placeholder.svg?height=40&width=40" },
        action: "avaliou",
        album: { title: "SOS", artist: "SZA", image: "/placeholder.svg?height=60&width=60" },
        rating: 4.5,
        comment: "SZA continua sendo uma das melhores vozes do R&B contemporâneo.",
        time: "1 dia atrás"
        },
        {
        id: 6,
        user: { name: "Ana Costa", username: "@anacosta", avatar: "/placeholder.svg?height=40&width=40" },
        action: "avaliou",
        album: { title: "Folklore", artist: "Taylor Swift", image: "/placeholder.svg?height=60&width=60" },
        rating: 4.8,
        comment: null,
        time: "2 dias atrás"
        },
        {
        id: 7,
        user: { name: "Felipe Oliveira", username: "@felipeoliveira", avatar: "/placeholder.svg?height=40&width=40" },
        action: "avaliou",
        album: { title: "Un Verano Sin Ti", artist: "Bad Bunny", image: "/placeholder.svg?height=60&width=60" },
        rating: 4,
        comment: "Bad Bunny não decepciona. Cada faixa é um hit potencial.",
        time: "2 dias atrás"
        },
        {
        id: 8,
        user: { name: "Beatriz Lima", username: "@bialima", avatar: "/placeholder.svg?height=40&width=40" },
        action: "avaliou",
        album: { title: "Harry's House", artist: "Harry Styles", image: "/placeholder.svg?height=60&width=60" },
        rating: 4.2,
        comment: "Harry mostra sua maturidade artística neste álbum. Muito bem produzido.",
        time: "3 dias atrás"
        },
        {
        id: 9,
        user: { name: "Rafael Mendes", username: "@rafaelmendes", avatar: "/placeholder.svg?height=40&width=40" },
        action: "avaliou",
        album: { title: "Renaissance", artist: "Beyoncé", image: "/placeholder.svg?height=60&width=60" },
        rating: 5,
        comment: null,
        time: "3 dias atrás"
        },
        {
        id: 10,
        user: { name: "Larissa Fernandes", username: "@larifernandes", avatar: "/placeholder.svg?height=40&width=40" },
        action: "avaliou",
        album: { title: "Dawn FM", artist: "The Weeknd", image: "/placeholder.svg?height=60&width=60" },
        rating: 3.8,
        comment: "Conceito interessante, mas algumas faixas não me conquistaram.",
        time: "4 dias atrás"
        },
        {
        id: 11,
        user: { name: "Thiago Rocha", username: "@thiagorocha", avatar: "/placeholder.svg?height=40&width=40" },
        action: "avaliou",
        album: { title: "Donda", artist: "Kanye West", image: "/placeholder.svg?height=60&width=60" },
        rating: 3,
        comment: "Kanye sempre polêmico, mas este álbum tem momentos brilhantes.",
        time: "4 dias atrás"
        },
        {
        id: 12,
        user: { name: "Camila Rodrigues", username: "@camilarodrigues", avatar: "/placeholder.svg?height=40&width=40" },
        action: "avaliou",
        album: { title: "Sour", artist: "Olivia Rodrigo", image: "/placeholder.svg?height=60&width=60" },
        rating: 4.3,
        comment: "Debut impressionante! Olivia tem muito potencial.",
        time: "5 dias atrás"
        }
    ]

    // Estado para controlar a visualização expandida da seção de atividades
    let showExpandedActivity = $state(false)
    let currentActivityPage = $state(1)
    const ACTIVITIES_PER_PAGE = 6
    const INITIAL_ACTIVITIES_SHOWN = 3

    function toggleActivityExpansion() {
        showExpandedActivity = !showExpandedActivity
        if (!showExpandedActivity) {
            currentActivityPage = 1
        }
    }

    function getActivitiesForPage(page) {
        if (!showExpandedActivity) {
            return friendsActivity.slice(0, INITIAL_ACTIVITIES_SHOWN)
        }
        const startIndex = (page - 1) * ACTIVITIES_PER_PAGE
        const endIndex = startIndex + ACTIVITIES_PER_PAGE
        return friendsActivity.slice(startIndex, endIndex)
    }

    function getTotalPages() {
        return Math.ceil(friendsActivity.length / ACTIVITIES_PER_PAGE)
    }

    function goToActivityPage(page) {
        currentActivityPage = page
    }

    function handleUserClick(username) {
        console.log('User clicked:', username)
        push('/perfil')
        // Scroll para o topo da página
        window.scrollTo({ top: 0, behavior: 'smooth' })
    }

    function handleActivityAlbumClick(album) {
        console.log('Activity album clicked:', album.title)
        push('/album')
        // Scroll para o topo da página
        window.scrollTo({ top: 0, behavior: 'smooth' })
    }
    
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
        push(`/album/${album.id}`)
        // Scroll para o topo da página
        window.scrollTo({ top: 0, behavior: 'smooth' })
    }
    
    function handleArtistClick(artist) {
        console.log('Artist clicked:', artist.name)
        // Navigate to artist page
        // window.scrollTo({ top: 0, behavior: 'smooth' })
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
                <button class="logo-button" onclick={() => { push('/'); window.scrollTo({ top: 0, behavior: 'smooth' }); }} aria-label="MusicBoxd">
                    <img src="/logocomtexto.png" alt="MusicBoxd Logo" />
                </button>
            </div>
                <div class="nav-links">
                <a href="/albuns" onclick={(e) => { e.preventDefault(); push('/top100'); window.scrollTo({ top: 0, behavior: 'smooth' }); }}>TOP ÁLBUNS</a>
                <div class="user-menu">
                    <button class="user-avatar" aria-label="User Profile" onclick={() => { push('/perfil'); window.scrollTo({ top: 0, behavior: 'smooth' }); }}>
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
            <div class="hero-title-container">
                <h1 class="hero-title" data-text="Descubra seus favoritos!">Descubra seus favoritos!</h1>
                <img src="/logo.png" alt="BeeSharp Logo" class="hero-logo">
            </div>
            <p class="hero-subtitle">
                Explore, avalie e resenhe álbuns de todos os gêneros e épocas. Siga pessoas da comunidade de amantes da música.
            </p>
            
            <form class="search-form" onsubmit={handleSearch}>
            <div class="search-container">
                <Search size={20} />
                <input
                type="text"
                class="search-input"
                placeholder="Busque por álbuns, artistas ou usuários :)"
                bind:value={searchQuery}
                />
                <button type="submit" class="search-button">
                Buscar
                </button>
            </div>
            </form>
        </div>
        </div>
    </section>

    <main class="main-content">
        <div class="container">
        <!-- Recommended Albums -->
        <section class="section">
            <div class="section-header">
            <div class="section-title recommendations-title">
                <Award size={24} />
                <h2>Recomendações</h2>
            </div>
            <div class="carousel-controls">
                <button class="carousel-btn" onclick={() => prevCarousel('recommended')} aria-label="Álbuns anteriores">
                ‹
                </button>
                <button class="carousel-btn" onclick={() => nextCarousel('recommended')} aria-label="Próximos álbuns">
                ›
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
                            <Headphones size={24} />
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
                            {#each renderStars(album.rating).fullStars as _}
                                <Star size={12} class="star-filled" />
                            {/each}
                            {#if renderStars(album.rating).hasHalfStar}
                                <div class="star-half">
                                    <Star size={12} class="star-empty" />
                                    <div class="star-half-fill">
                                        <Star size={12} class="star-filled" />
                                    </div>
                                </div>
                            {/if}
                            {#each renderStars(album.rating).emptyStars as _}
                                <Star size={12} class="star-empty" />
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
            <div class="section-title hot-albums-title">
                <TrendingUp size={24} />
                <h2>Em Alta</h2>
            </div>
            <div class="carousel-controls">
                <button class="carousel-btn" onclick={() => prevCarousel('hot')} aria-label="Álbuns anteriores">
                ‹
                </button>
                <button class="carousel-btn" onclick={() => nextCarousel('hot')} aria-label="Próximos álbuns">
                ›
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
                            <Headphones size={24} />
                        </div>
                        </div>
                        <div class="trending-badge">
                        <TrendingUp size={14} />
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
                            {#each renderStars(album.rating).fullStars as _}
                                <Star size={12} class="star-filled" />
                            {/each}
                            {#if renderStars(album.rating).hasHalfStar}
                                <div class="star-half">
                                    <Star size={12} class="star-empty" />
                                    <div class="star-half-fill">
                                        <Star size={12} class="star-filled" />
                                    </div>
                                </div>
                            {/if}
                            {#each renderStars(album.rating).emptyStars as _}
                                <Star size={12} class="star-empty" />
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

        <!-- Activity from Friends -->
        <section class="section activity-friends-section">
            <div class="section-header">
                <h2 class="section-title">
                <Users size={24} />
                Atividade dos Amigos
                </h2>
                <button class="view-all-link" onclick={toggleActivityExpansion}>
                    {showExpandedActivity ? 'Ver Menos' : 'Ver Tudo'}
                </button>
            </div>

            <div class="activity-container">
                <div class="activity-list" class:grid-layout={showExpandedActivity}>
                    {#each getActivitiesForPage(currentActivityPage) as activity (activity.id)}
                    <div class="activity-item">
                        <button class="activity-avatar-btn" onclick={() => handleUserClick(activity.user.username)}>
                            <img src={activity.user.avatar || "/placeholder.svg"} alt={activity.user.name} />
                        </button>
                        <div class="activity-content">
                        <div class="activity-header">
                            <button class="user-name-btn" onclick={() => handleUserClick(activity.user.username)}>
                                {activity.user.name}
                            </button>
                            <span class="activity-action">{activity.action} com</span>
                            <div class="activity-rating">
                            <div class="stars-container">
                                {#each renderStars(activity.rating).fullStars as _}
                                    <Star size={12} class="star-filled" />
                                {/each}
                                {#if renderStars(activity.rating).hasHalfStar}
                                    <div class="star-half">
                                        <Star size={12} class="star-empty" />
                                        <div class="star-half-fill">
                                            <Star size={12} class="star-filled" />
                                        </div>
                                    </div>
                                {/if}
                                {#each renderStars(activity.rating).emptyStars as _}
                                    <Star size={12} class="star-empty" />
                                {/each}
                            </div>
                            </div>
                        </div>
                        
                        <button class="activity-album" onclick={() => handleActivityAlbumClick(activity.album)}>
                            <img src={activity.album.image || "/placeholder.svg"} alt={activity.album.title} class="album-thumbnail" />
                            <div class="album-details">
                            <h4 class="album-title">{activity.album.title}</h4>
                            <p class="album-artist">{activity.album.artist}</p>
                            </div>
                        </button>

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

                {#if showExpandedActivity && getTotalPages() > 1}
                    <div class="pagination">
                        {#each Array(getTotalPages()) as _, i}
                            <button 
                                class="page-btn" 
                                class:active={currentActivityPage === i + 1}
                                onclick={() => goToActivityPage(i + 1)}
                            >
                                {i + 1}
                            </button>
                        {/each}
                    </div>
                {/if}
            </div>
        </section>

        <!-- Genre Spotlight -->
        <section class="section">
            <div class="section-header">
            <div class="section-title genre-spotlight-title">
                <Disc size={24} />
                <h2>Destaque: Rock Alternativo</h2>
            </div>
            <div class="carousel-controls">
                <button class="carousel-btn" onclick={() => prevCarousel('genre')} aria-label="Álbuns anteriores">
                ‹
                </button>
                <button class="carousel-btn" onclick={() => nextCarousel('genre')} aria-label="Próximos álbuns">
                ›
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
                            <Headphones size={24} />
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
                            {#each renderStars(album.rating).fullStars as _}
                                <Star size={12} class="star-filled" />
                            {/each}
                            {#if renderStars(album.rating).hasHalfStar}
                                <div class="star-half">
                                    <Star size={12} class="star-empty" />
                                    <div class="star-half-fill">
                                        <Star size={12} class="star-filled" />
                                    </div>
                                </div>
                            {/if}
                            {#each renderStars(album.rating).emptyStars as _}
                                <Star size={12} class="star-empty" />
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
        background: #14181c;
        height: 80px;
        min-height: 80px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
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
        width: 40px;
        height: 40px;
        min-width: 40px;
        min-height: 40px;
        max-width: 40px;
        max-height: 40px;
        border-radius: 50%;
        border: 2px solid #255F85;
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
        font-family: 'Familjen Grotesk', sans-serif;
    }

    .section-title :global(svg) {
        color: #ff6b35;
    }

    .section-title h2 {
        margin: 0;
        font-size: 1.75rem;
        font-weight: 700;
        color: white;
    }

    /* Ícone específico para Recomendações - medalha amarela preenchida */
    .recommendations-title :global(svg) {
        color: #FFC857;
        fill: #FFC857;
    }

    /* Ícone específico para Em Alta - vermelho igual aos botões de carrossel */
    .hot-albums-title :global(svg) {
        color: #C5283D;
        fill: #C5283D;
    }

    /* Ícone específico para Destaque de Gênero - azul não preenchido */
    .genre-spotlight-title :global(svg) {
        color: #255F85;
        fill: none;
        stroke: #255F85;
        stroke-width: 2;
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
        background: rgba(197, 40, 61, 0.9);
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
        gap: 2px;
    }

    .rating-text {
        font-size: 0.75rem;
        color: #9ca3af;
        font-weight: 500;
    }

    /* Activity Section - Sempre com fundo cinza */
    .activity-friends-section {
        margin-bottom: 4rem;
    }

    .activity-container {
        background: #1d232a;
        border-radius: 12px;
        padding: 2rem;
        transition: all 0.3s ease;
    }

    .activity-list {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .activity-list.grid-layout {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 2rem;
        position: relative;
    }

    .activity-list.grid-layout::before {
        content: '';
        position: absolute;
        left: 50%;
        top: 0;
        bottom: 0;
        width: 1px;
        background: #374151;
        transform: translateX(-50%);
    }

    .view-all-link {
        background: none;
        border: none;
        color: #FFC857;
        cursor: pointer;
        font-size: 0.875rem;
        text-decoration: underline;
        transition: color 0.2s ease;
    }

    .view-all-link:hover {
        color: #d6a639;
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

    .activity-avatar-btn {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        overflow: hidden;
        flex-shrink: 0;
        border: 2px solid #374151;
        background: none;
        cursor: pointer;
        transition: all 0.2s ease;
        padding: 0;
    }

    .activity-avatar-btn:hover {
        border-color: #FFC857;
        transform: scale(1.05);
    }

    .activity-avatar-btn img {
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
        display: flex;
        align-items: center;
        gap: 0.5rem;
        flex-wrap: wrap;
    }

    .user-name-btn {
        background: none;
        border: none;
        font-weight: 600;
        color: white;
        cursor: pointer;
        transition: color 0.2s ease;
        padding: 0;
        font-size: inherit;
        font-family: inherit;
    }

    .user-name-btn:hover {
        color: #FFC857;
    }

    .activity-action {
        color: #9ca3af;
    }

    .activity-rating {
        display: flex;
        align-items: center;
    }

    .activity-album {
        display: flex;
        gap: 0.75rem;
        align-items: center;
        margin-bottom: 0.5rem;
        background: none;
        border: none;
        cursor: pointer;
        text-align: left;
        width: 100%;
        padding: 0.5rem;
        border-radius: 8px;
        transition: all 0.2s ease;
    }

    .activity-album:hover {
        background: rgba(255, 255, 255, 0.05);
        transform: translateX(4px);
    }

    .album-thumbnail {
        width: 48px;
        height: 48px;
        border-radius: 4px;
        object-fit: cover;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
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

    .activity-comment {
        color: #d1d5db;
        font-weight: 400;
        margin: 0.75rem 0 0.5rem 0;
        font-size: 0.875rem;
        line-height: 1.4;
        font-style: italic;
        background: rgba(255, 255, 255, 0.03);
        padding: 0.75rem;
        border-radius: 6px;
        border-left: 3px solid #FFC857;
    }

    .activity-time {
        display: flex;
        align-items: center;
        gap: 0.25rem;
        font-size: 0.75rem;
        color: #6b7280;
    }

    /* Paginação */
    .pagination {
        display: flex;
        justify-content: center;
        gap: 0.5rem;
        margin-top: 2rem;
        padding-top: 2rem;
        border-top: 1px solid #374151;
    }

    .page-btn {
        width: 32px;
        height: 32px;
        border: 1px solid #374151;
        background: transparent;
        color: #9ca3af;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.2s ease;
        font-size: 0.875rem;
        font-weight: 500;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .page-btn:hover {
        background: #374151;
        color: white;
    }

    .page-btn.active {
        background: #255F85;
        border-color: #255F85;
        color: white;
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

        .activity-list.grid-layout {
        grid-template-columns: 1fr;
        }

        .activity-list.grid-layout::before {
        display: none;
        }
    }

    @media (max-width: 480px) {
        .carousel-item {
        flex: 0 0 calc(100% - 0rem);
        }

        .activity-list.grid-layout {
        grid-template-columns: 1fr;
        }

        .activity-list.grid-layout::before {
        display: none;
        }

        .hero-title {
            font-size: 2rem;
        }

        .hero-logo {
            height: 2.5rem;
        }
    }
</style>
