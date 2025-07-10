<script>
  import { Star, Heart, Calendar, Music, Users, MessageSquare, ListMusic, ThumbsUp, ThumbsDown, Search, Edit3, Camera, User } from 'lucide-svelte'
  import { push } from 'svelte-spa-router'
  import Login from '../lib/Login.svelte'
  import { isAuthenticated, getUsername, logout } from '../lib/auth'
  import { onMount } from 'svelte'
  
  // Props to receive route parameters
  let props = $props()
  
  // Extract params from props, providing fallback
  const params = props.params || {}
  
  // Simulação de usuário logado
  let loggedUser = $state({
    name: "Alex Chen",
    username: "@alexmusic",
    avatar: "/placeholder.svg?height=32&width=32"
  })
  
  // Estado para controlar se o perfil sendo visualizado é do próprio usuário
  let isOwnProfile = $state(false)
  let showLoginModal = $state(false)
  function openLoginModal() { showLoginModal = true }
  function closeLoginModal() { showLoginModal = false }

  // Check login status when component mounts and load profile
  onMount(async () => {
    if (!isAuthenticated()) {
      showLoginModal = true;
      return;
    }
    
    console.log('ProfilePage mounted with params:', params);
    
    // Se temos um username nos parâmetros, usamos ele
    // Caso contrário, usamos o username do usuário autenticado
    const profileUsername = params.username || getUsername();
    console.log('Loading profile for username:', profileUsername);
    
    // Verifica se o perfil sendo visualizado é do próprio usuário
    const loggedInUsername = getUsername();
    isOwnProfile = loggedInUsername === profileUsername;
    console.log('Is own profile:', isOwnProfile);
    
    // Buscar os dados do usuário da API
    try {
      // Buscar dados básicos do usuário
      const userResponse = await fetch(`http://localhost:8080/${profileUsername}`);
      if (userResponse.ok) {
        const userData = await userResponse.json();
        loggedUser = {
          name: userData.username, // Usando username como nome por enquanto
          username: `@${userData.username}`,
          avatar: userData.profileImage || "/placeholder.svg?height=32&width=32"
        };
        userId = userData.id;
        
        // Buscar descrição do usuário
        const descResponse = await fetch(`http://localhost:8080/users/${userData.id}/description`);
        if (descResponse.ok) {
          console.log('Descrição encontrada');
          const descJson = await descResponse.json();
          console.log('descrição: ', descJson.description);
          userBio = descJson.description || '';
        } else {
          console.log('Falha ao buscar descrição');
        }

        
        // Carregar todos os dados do usuário
        await loadUserData(userData.id);
        
        // Verificar se o usuário logado está seguindo este perfil (apenas se não for o próprio perfil)
        if (!isOwnProfile && loggedInUsername) {
          try {
            // TODO: Implementar endpoint real quando estiver disponível
            /*
            const followResponse = await fetch(`/users/${loggedInUsername}/following/${profileUsername}`);
            if (followResponse.ok) {
              const followData = await followResponse.json();
              isFollowing = followData.isFollowing || false;
            }
            */
            
            // Por enquanto, simular que não está seguindo (estado inicial)
            isFollowing = false;
          } catch (error) {
            console.error('Erro ao verificar status de seguir:', error);
          }
        }
        
      } else {
        console.error('Falha ao obter dados do usuário');
        // Fallback com dados vazios
        loggedUser = {
          name: profileUsername,
          username: `@${profileUsername}`,
          avatar: "/placeholder.svg?height=32&width=32"
        };
      }
    } catch (error) {
      console.error('Erro ao carregar perfil:', error);
      // Fallback com dados vazios
      loggedUser = {
        name: profileUsername,
        username: `@${profileUsername}`,
        avatar: "/placeholder.svg?height=32&width=32"
      };
    } finally {
      loadingProfile = false;
    }
  })
  
  // Estado removido da barra de busca

  // Controle de seguir
  let isFollowing = $state(false)
  let followLoading = $state(false)
  
  async function handleFollow() {
    if (followLoading) return;
    
    followLoading = true;
    
    try {
      const profileUsername = params.username || getUsername();
      const loggedInUsername = getUsername();
      
      if (!loggedInUsername || !profileUsername) {
        alert('Erro: usuário não autenticado');
        return;
      }
      
      // Por enquanto, simular o comportamento até que os endpoints sejam implementados
      // TODO: Implementar chamadas reais para a API quando os endpoints estiverem disponíveis
      /*
      const action = isFollowing ? 'unfollow' : 'follow';
      
      const response = await fetch(`/users/${loggedInUsername}/${action}/${profileUsername}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) {
        throw new Error(`Falha ao ${action === 'follow' ? 'seguir' : 'deixar de seguir'} usuário`);
      }
      */
      
      // Simulação por enquanto - remover quando os endpoints estiverem prontos
      await new Promise(resolve => setTimeout(resolve, 500)); // Simular delay da rede
      
      // Atualizar estado local
      const wasFollowing = isFollowing;
      isFollowing = !isFollowing;
      
      // Atualizar contadores localmente
      if (!wasFollowing) {
        // Começou a seguir
        userStats.followers += 1;
        console.log(`${loggedInUsername} começou a seguir ${profileUsername}`);
        
        // TODO: Mostrar notificação de sucesso
        // showNotification('Agora você está seguindo este usuário!', 'success');
      } else {
        // Parou de seguir  
        userStats.followers -= 1;
        console.log(`${loggedInUsername} parou de seguir ${profileUsername}`);
        
        // TODO: Mostrar notificação
        // showNotification('Você deixou de seguir este usuário.', 'info');
      }
      
      // TODO: Quando os endpoints estiverem prontos, recarregar os dados para ter certeza
      // await loadUserData(userId);
      
    } catch (error) {
      console.error('Erro ao seguir/deixar de seguir:', error);
      alert('Ocorreu um erro. Por favor, tente novamente.');
    } finally {
      followLoading = false;
    }
  }
  
  // Estados para gerenciar a edição de perfil
  let editingBio = $state(false);
  let newBio = $state('');
  let fileInput;
  let isLoading = $state(false);
  let userId = $state(null); // Inicializado como null
  let userBio = $state(''); // Bio atual do usuário
  
  // Estados para dados reais do usuário
  let userStats = $state({
    totalAlbums: 0,
    totalReviews: 0,
    following: 0,
    followers: 0,
    averageRating: 0,
    albumsThisYear: 0,
    totalFavorites: 0
  });
  
  let listenedAlbums = $state([]);
  let favoriteAlbums = $state([]);
  let recentReviews = $state([]);
  
  // Estados de carregamento
  let loadingProfile = $state(true);
  let loadingAlbums = $state(true);
  let loadingReviews = $state(true);
  
  // Função para editar avatar
  function handleEditAvatar() {
    fileInput.click();
  }
  
  // Função para lidar com a seleção do arquivo
  async function handleFileSelect(event) {
    const file = event.target.files[0];
    if (!file) return;
    
    isLoading = true;
    
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await fetch(`/upload/user-photo/${userId}`, {
        method: 'POST',
        body: formData
      });
      
      if (!response.ok) {
        throw new Error('Falha ao fazer upload da imagem');
      }
      
      // Recarregar a página para mostrar a nova foto
      location.reload();
    } catch (error) {
      console.error('Erro ao enviar a imagem:', error);
      alert('Ocorreu um erro ao fazer upload da imagem. Por favor, tente novamente.');
    } finally {
      isLoading = false;
    }
  }
  
  // Função para iniciar a edição da bio
  function handleEditBio() {
    newBio = userBio;
    editingBio = true;
  }
  
  // Função para salvar a bio atualizada
  async function saveBio() {
    isLoading = true;
    
    try {
      const response = await fetch(`/users/${userId}/description`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          description: newBio
        })
      });
      
      if (!response.ok) {
        throw new Error('Falha ao atualizar a biografia');
      }
      
      // Atualizar a bio localmente
      userBio = newBio;
      editingBio = false;
    } catch (error) {
      console.error('Erro ao atualizar a biografia:', error);
      alert('Ocorreu um erro ao atualizar sua biografia. Por favor, tente novamente.');
    } finally {
      isLoading = false;
    }
  }
  
  // Função para cancelar a edição da bio
  function cancelEditBio() {
    editingBio = false;
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

  // Função para carregar dados do usuário
  async function loadUserData(profileUserId) {
    try {
      // Carregar estatísticas do usuário
      const statsResponse = await fetch(`http://localhost:8080/users/${profileUserId}/stats`);
      if (statsResponse.ok) {
        userStats = await statsResponse.json();
      }
      
      // Carregar álbuns favoritos
      const favoritesResponse = await fetch(`http://localhost:8080/users/${profileUserId}/favorites`);
      if (favoritesResponse.ok) {
        favoriteAlbums = await favoritesResponse.json();
      }
      loadingAlbums = false;
      
      // Carregar álbuns escutados (com avaliações) e reviews
      const [listenedResponse, reviewsResponse] = await Promise.all([
        fetch(`http://localhost:8080/users/${profileUserId}/listened`),
        fetch(`http://localhost:8080/users/${profileUserId}/reviews`)
      ]);
      
      let listenedAlbumsData = [];
      let reviewsData = [];
      
      if (listenedResponse.ok) {
        listenedAlbumsData = await listenedResponse.json();
      }
      
      if (reviewsResponse.ok) {
        reviewsData = await reviewsResponse.json();
        recentReviews = reviewsData;
      }
      
      // Combinar álbuns escutados com álbuns que têm reviews
      // Criar um mapa para evitar duplicatas baseado no título e artista (já que pode não ter ID consistente)
      const albumsMap = new Map();
      
      // Adicionar álbuns escutados (com rating direto)
      listenedAlbumsData.forEach(album => {
        const key = `${album.title}-${album.artist}`.toLowerCase();
        albumsMap.set(key, {
          ...album,
          rating: album.averageRating || 0, // Usar averageRating se existir, senão 0
          source: 'listened'
        });
      });
      
      // Adicionar álbuns que têm reviews (caso não estejam já nos escutados)
      reviewsData.forEach(review => {
        const key = `${review.title || review.albumTitle}-${review.artist || review.albumArtist}`.toLowerCase();
        
        if (!albumsMap.has(key)) {
          albumsMap.set(key, {
            id: review.id ?? key, // corrigido!
            title: review.title || review.albumTitle,
            artist: review.artist || review.albumArtist,
            year: review.year || review.albumYear,
            image: review.coverUrl || review.albumCover,
            rating: review.rating || review.userRating || 0,
            source: 'review'
          });
        } else {
          // Se já existe, verificar qual tem rating mais alto e manter esse
          const existing = albumsMap.get(key);
          const reviewRating = review.rating || review.userRating || 0;
          if (reviewRating > existing.rating) {
            albumsMap.set(key, {
              ...existing,
              rating: reviewRating,
              source: 'both'
            });
          }
        }
      });
      
      // Converter o mapa de volta para array e ordenar por rating (maior primeiro)
      listenedAlbums = Array.from(albumsMap.values()).sort((a, b) => (b.rating || 0) - (a.rating || 0));

      console.log('Álbuns escutados carregados:', listenedAlbums);
      
      loadingReviews = false;
      
    } catch (error) {
      console.error('Erro ao carregar dados do usuário:', error);
      loadingAlbums = false;
      loadingReviews = false;
    }
  }

function renderStars(rating) {
  // Converte de 0-10 para 0-5
  const fiveStarRating = Number.isFinite(rating) ? rating / 2 : 0;
  const fullStars = Math.floor(fiveStarRating);
  const hasHalfStar = fiveStarRating % 1 >= 0.5;
  const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
  return {
    fullStars: Array(Math.max(0, fullStars)).fill(0),
    hasHalfStar,
    emptyStars: Array(Math.max(0, emptyStars)).fill(0)
  };
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

  function handleAlbumClick(album) {
    console.log('Album clicked:', album.title)
    // Navegar para a página do álbum com o ID
    if (album.id) {
      push(`/album/${album.id}`)
    } else {
      push('/album')
    }
  }
  
  // Função para alternar a exibição do menu do usuário
  let showUserMenu = $state(false);
  function toggleUserMenu() {
    showUserMenu = !showUserMenu;
  }
  
  // Função para fazer logout
  function handleLogout() {
    logout();
    push('/');
  }
</script>

<nav class="navbar-albums">
  <div class="navbar-container">
    <div class="logo-component">
      <button class="logo-button" onclick={() => push('/home')} aria-label="Ir para página inicial">
        <img src="/logocomtexto.png" alt="BeeSharp Logo" />
      </button>
    </div>
    
    {#if isAuthenticated()}
      <!-- Navbar para usuário logado -->
      <div class="user-menu">
        <button class="user-avatar" aria-label="User Profile" onclick={toggleUserMenu}>
          {#if getUsername()}
            <div class="user-avatar-text">
              {getUsername().charAt(0).toUpperCase()}
            </div>
          {:else}
            <User size={20} />
          {/if}
        </button>
        {#if getUsername()}
          <span class="user-name">{getUsername()}</span>
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

<div class="app">
  <!-- Header -->
  <header class="header">
    <div class="container">
      <div class="profile-info">          <div class="profile-layout">
            <div class="avatar-container">
              <img src={userId ? `/user-photo/${userId}` : "/placeholder.svg?height=140&width=140"} 
                   alt={loggedUser.name} />
              {#if isOwnProfile}
                <div class="edit-avatar-overlay">
                  <button class="edit-avatar-btn" onclick={handleEditAvatar} aria-label="Editar foto de perfil">
                    <Camera size={24} />
                  </button>
                </div>
              {/if}
            </div>

            <div class="user-details">
              <div class="user-header">
                <div class="user-name-follow">
                  <div class="user-name">
                    <div style="display: flex; align-items: center;">
                      <h1>{loggedUser.name}</h1>
                      {#if isOwnProfile}
                        <button class="edit-bio-btn" onclick={handleEditBio} aria-label="Editar biografia">
                          <Edit3 size={20} />
                        </button>
                      {/if}
                    </div>
                    <p class="username">{loggedUser.username}</p>
                  </div>
                  {#if !isOwnProfile}
                    <button class="follow-btn {isFollowing ? 'following' : ''}" onclick={handleFollow} disabled={followLoading}>
                      {#if followLoading}
                        Carregando...
                      {:else}
                        {isFollowing ? 'Seguindo' : 'Seguir'}
                      {/if}
                    </button>
                  {/if}
                </div>
              
                <div class="bio-container">
                  {#if editingBio}
                    <div class="edit-bio-form">
                      <textarea
                        bind:value={newBio}
                        class="bio-textarea"
                        placeholder="Escreva algo sobre você..."
                        maxlength="1000"
                      ></textarea>
                      <div class="bio-actions">
                        <button class="bio-btn cancel" onclick={cancelEditBio} disabled={isLoading}>
                          Cancelar
                        </button>
                        <button class="bio-btn save" onclick={saveBio} disabled={isLoading}>
                          {isLoading ? 'Salvando...' : 'Salvar'}
                        </button>
                      </div>
                    </div>
                  {:else}
                    {#if userBio}
                      <p class="bio">{userBio}</p>
                    {:else}
                      <p class="bio empty-bio">
                        {#if isOwnProfile}
                          Clique no botão ao lado para adicionar uma biografia.
                        {:else}
                          Este usuário ainda não adicionou uma biografia.
                        {/if}
                      </p>
                    {/if}
                  {/if}
                </div>
                
                <!-- Input de arquivo oculto para upload de foto -->
                <input 
                  type="file" 
                  accept="image/*" 
                  style="display: none;" 
                  bind:this={fileInput}
                  onchange={handleFileSelect}
                />
              </div>

          <div class="stats">
            <div class="stat">
              <Music size={16} />
              <span class="stat-number">{userStats.totalAlbums}</span> álbuns
            </div>
            <div class="stat">
              <MessageSquare size={16} />
              <span class="stat-number">{userStats.totalReviews}</span> reviews
            </div>
            <div class="stat">
              <Users size={16} />
              <span class="stat-number">{userStats.following}</span> seguindo
            </div>
            <div class="stat">
              <Users size={16} />
              <span class="stat-number">{userStats.followers}</span> seguidores
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
          <div class="stat-value">{userStats.averageRating ? userStats.averageRating.toFixed(1) : '-'}</div>
          <div class="stat-label">Avaliação Média</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{userStats.albumsThisYear}</div>
          <div class="stat-label">Esse Ano</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{userStats.totalAlbums}</div>
          <div class="stat-label">Total</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{userStats.totalFavorites}</div>
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

        {#if loadingAlbums}
          <div class="loading-state">
            <p>Carregando álbuns favoritos...</p>
          </div>
        {:else if favoriteAlbums.length === 0}
          <div class="empty-state">
            <Heart size={48} class="empty-icon" />
            <h3>Não há álbuns favoritos</h3>
            <p>
              {#if isOwnProfile}
                Você ainda não marcou nenhum álbum como favorito. Explore novos álbuns e adicione seus favoritos!
              {:else}
                Este usuário ainda não marcou nenhum álbum como favorito.
              {/if}
            </p>
          </div>
        {:else}
          <div class="albums-grid">
            {#each favoriteAlbums as album, i (album.id ?? i)}
              <button class="album-card" onclick={() => handleAlbumClick(album)}>
                <div class="album-cover">
                  <img src={album.image || "/placeholder.svg?height=200&width=200"} alt="{album.title} by {album.artist}" />
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
        {/if}
      </section>

      <!-- Listened Albums -->
      <section class="section listened-albums-section">
        <div class="section-header">
          <div class="section-title">
            <Music size={20} />
            <h2>Álbuns Escutados</h2>
          </div>
        </div>

        {#if loadingAlbums}
          <div class="loading-state">
            <p>Carregando álbuns escutados...</p>
          </div>
        {:else if listenedAlbums.length === 0}
          <div class="empty-state">
            <Music size={48} class="empty-icon" />
            <h3>Não há álbuns escutados</h3>
            <p>
              {#if isOwnProfile}
                Você ainda não avaliou nenhum álbum. Comece explorando e deixe sua opinião sobre os álbuns que você escuta!
              {:else}
                Este usuário ainda não avaliou nenhum álbum.
              {/if}
            </p>
          </div>
        {:else}
          <div class="albums-grid">
            {#each getCurrentAlbumsPage() as album, i (album.id ?? i)}
              <button class="album-card" onclick={() => handleAlbumClick(album)}>
                <div class="album-cover">
                  <img src={album.image || "/placeholder.svg?height=200&width=200"} alt="{album.title} by {album.artist}" />
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

        {#if loadingReviews}
          <div class="loading-state">
            <p>Carregando reviews...</p>
          </div>
        {:else if recentReviews.length === 0}
          <div class="empty-state">
            <MessageSquare size={48} class="empty-icon" />
            <h3>Não há reviews</h3>
            <p>
              {#if isOwnProfile}
                Você ainda não escreveu nenhum review. Comece explorando álbuns e compartilhe suas opiniões!
              {:else}
                Este usuário ainda não escreveu nenhum review.
              {/if}
            </p>
          </div>
        {:else}
          <div class="reviews-list">
            {#each getCurrentReviewsPage() as review (review.id)}
              <div class="review-card">
                <div class="review-layout">
                  <div class="album-info-review">
                    <button class="album-image-button" onclick={() => handleAlbumClick(review)} aria-label="Ver álbum {review.title}">
                      <img src={review.coverUrl || "/placeholder.svg?height=120&width=120"} alt="{review.title} by {review.artist}" class="album-avatar" />
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
                    
                    {#if review.content}
                      <div class="review-content">
                        <p>{review.content}</p>
                      </div>
                    {/if}
                    
                    <div class="review-actions">
                      <button 
                        class="action-btn like-btn {review.userLiked ? 'active' : ''}"
                        onclick={() => handleLike(review.id)}
                        aria-label="Curtir review"
                      >
                        <ThumbsUp size={16} />
                        <span>{review.likesCount}</span>
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
  
  /* Estilo removido pois não está sendo utilizado */
  
  /* Estilos de busca removidos */

  /* User Avatar Styles */
  .user-menu {
    position: relative;
    display: flex;
    align-items: center;
  }

  .user-avatar {
    width: 40px;
    height: 40px;
    min-width: 40px;
    min-height: 40px;
    max-width: 40px;
    max-height: 40px;
    border-radius: 50%;
    border: 2px solid rgba(255, 255, 255, 0.3);
    overflow: hidden;
    background-color: rgba(30, 76, 107, 0.8);
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
    border-color: #FFC857;
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
  
  .follow-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
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
        padding: 1rem 0;
    }
    
    .profile-layout {
        display: flex;
        gap: 2rem;
        align-items: flex-start;
    }

    .avatar-container {
        position: relative;
        width: 140px;
        height: 140px;
        border-radius: 50%;
        overflow: hidden;
        border: 3px solid #FFC857;
        background-color: #2c3440;
        flex-shrink: 0;
    }

    .avatar-container img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .user-details {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
    }

    .user-header {
        display: flex;
        flex-direction: column;
        margin-bottom: 1rem;
    }
    
    .user-name-follow {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 0.5rem;
    }

    .user-name h1 {
        margin: 0;
        font-size: 1.8rem;
        font-weight: bold;
        font-family: 'Familjen Grotesk', sans-serif;
    }

    .username {
        margin: 0;
        color: #9ca3af;
        font-size: 1.1rem;
    }

    .bio {
        color: #d1d5db;
        margin-bottom: 1rem;
        max-width: 100%;
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

  /* Albums Grid para Favoritos */
  .albums-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 1rem;
  }
  
  /* Albums Grid para Álbuns Escutados - 5 por linha */
  /* Use a specific class for the listened albums section instead of :has() and :contains() */
  .listened-albums-section .albums-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 0.8rem;
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
    margin-bottom: 0.5rem;
    overflow: hidden;
    border-radius: 6px;
    background-color: #374151;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  }
  
  /* Tamanho reduzido para Álbuns Escutados */
  /* Use a specific class for the listened albums section instead of :has() and :contains() */
  .listened-albums-section .album-cover {
    width: 100%;
    aspect-ratio: 1;
  }
  
  .listened-albums-section .album-info {
    font-size: 0.9rem;
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
    width: 36px;
    height: 36px;
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

  /* Avatar com edição */
  
  .edit-avatar-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    opacity: 0;
    transition: opacity 0.2s ease-in-out;
    border-radius: 50%;
  }
  
  .avatar-container:hover .edit-avatar-overlay {
    opacity: 1;
  }
  
  .edit-avatar-btn {
    background: none;
    border: none;
    color: white;
    cursor: pointer;
    padding: 0;
  }
  
  /* Bio com edição */
  .bio-container {
    width: 100%;
    margin-top: 0.75rem;
    display: flex;
    align-items: center;
    padding-left: 7px;
  }
  
  .bio {
    flex: 1;
    margin: 0;
    line-height: 1.5;
    color: #e2e8f0;
  }
  
  .edit-bio-btn {
    background-color: rgba(255, 200, 87, 0.1);
    border: 1px solid rgba(255, 200, 87, 0.3);
    color: #FFC857;
    cursor: pointer;
    padding: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    margin-left: 12px;
    transition: transform 0.2s ease, background-color 0.2s ease;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    position: relative;
    top: 2px;
  }
  
  .edit-bio-btn:hover {
    transform: scale(1.1);
    background-color: rgba(255, 200, 87, 0.25);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  }
  
  /* Estilos para a edição da bio */
  .edit-bio-form {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .bio-textarea {
    width: 100%;
    min-height: 120px;
    padding: 0.75rem;
    border-radius: 8px;
    border: 1px solid #374151;
    background-color: #1e2732;
    color: #e2e8f0;
    font-size: 0.95rem;
    line-height: 1.5;
    resize: vertical;
    font-family: inherit;
    transition: border-color 0.2s ease;
  }
  
  .bio-textarea:focus {
    border-color: #FFC857;
    outline: none;
    box-shadow: 0 0 0 2px rgba(255, 200, 87, 0.2);
  }
  
  .bio-actions {
    display: flex;
    justify-content: flex-end;
    gap: 0.75rem;
  }
  
  .bio-btn {
    padding: 0.5rem 1rem;
    border-radius: 6px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  
  .bio-btn.cancel {
    background-color: transparent;
    border: 1px solid #4b5563;
    color: #e2e8f0;
  }
  
  .bio-btn.cancel:hover {
    background-color: #2c3440;
    border-color: #6b7280;
  }
  
  .bio-btn.save {
    background-color: #255F85;
    border: 1px solid #255F85;
    color: white;
  }
  
  .bio-btn.save:hover {
    background-color: #1e4c6b;
  }
  
  .bio-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  
  /* Estados vazios e de carregamento */
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 3rem 1rem;
    text-align: center;
    color: #9ca3af;
  }
  
  .empty-state :global(.empty-icon) {
    margin-bottom: 1rem;
    opacity: 0.5;
  }
  
  .empty-state h3 {
    margin: 0 0 0.5rem 0;
    font-size: 1.25rem;
    color: #d1d5db;
  }
  
  .empty-state p {
    margin: 0;
    line-height: 1.5;
    max-width: 400px;
  }
  
  .loading-state {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 2rem;
    color: #9ca3af;
  }
  
  .empty-bio {
    color: #9ca3af;
    font-style: italic;
  }

  /* User menu dropdown */
  .user-avatar-text {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    font-size: 1rem;
    color: white;
  }
  
  .user-dropdown {
    position: absolute;
    top: 100%;
    right: 0;
    width: 180px;
    background: #1e4c6b;
    border-radius: 8px;
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
    overflow: hidden;
    margin-top: 0.5rem;
    z-index: 100;
    border: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .user-dropdown button {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    font-size: 0.9rem;
    text-decoration: none;
    color: white;
    transition: background 0.2s;
    width: 100%;
    border: none;
    background: transparent;
    text-align: left;
    cursor: pointer;
  }
  
  .user-dropdown button:hover {
    background: rgba(255, 255, 255, 0.1);
  }
  
  /* Navbar links para usuário não logado */
  .nav-links {
    display: flex;
    align-items: center;
    gap: 1.5rem;
  }
  
  .nav-links a {
    color: white;
    text-decoration: none;
    font-weight: 600;
    font-size: 0.9rem;
    transition: opacity 0.2s;
  }
  
  .nav-links a:hover {
    opacity: 0.8;
  }
  
  .nav-login-btn {
    padding: 0.5rem 1.2rem;
    background: rgba(255, 255, 255, 0.15);
    color: white;
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 20px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
  }
  
  .nav-login-btn:hover {
    background: rgba(255, 255, 255, 0.25);
  }
  
  .user-name {
    font-size: 0.9rem;
    color: white;
    margin-left: 0.5rem;
  }
</style>
