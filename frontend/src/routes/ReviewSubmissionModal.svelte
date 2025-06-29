<script>
  import { Star, X, Music } from 'lucide-svelte'
  
  let { album, onClose, onSubmit } = $props()
  
  let rating = $state(0)
  let comment = $state('')
  let isSubmitting = $state(false)
  let hoveredRating = $state(0)
  
  function handleStarClick(starIndex, isHalf = false) {
    rating = isHalf ? starIndex - 0.5 : starIndex
  }
  
  function handleStarHover(starIndex, isHalf = false) {
    hoveredRating = isHalf ? starIndex - 0.5 : starIndex
  }
  
  function handleStarLeave() {
    hoveredRating = 0
  }
  
  function handleStarMouseMove(event, starIndex) {
    const rect = event.currentTarget.getBoundingClientRect()
    const x = event.clientX - rect.left
    const isHalf = x < rect.width / 2
    hoveredRating = isHalf ? starIndex - 0.5 : starIndex
  }
  
  function handleStarClickFromMove(event, starIndex) {
    const rect = event.currentTarget.getBoundingClientRect()
    const x = event.clientX - rect.left
    const isHalf = x < rect.width / 2
    rating = isHalf ? starIndex - 0.5 : starIndex
  }
  
  function handleOverlayClick(event) {
    if (event.target === event.currentTarget) {
      onClose()
    }
  }
  
  async function handleSubmit(event) {
    event.preventDefault()
    
    if (rating === 0) {
      alert('Por favor, selecione uma avaliação')
      return
    }
    
    isSubmitting = true
    
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      onSubmit({
        rating,
        comment: comment.trim()
      })
    } catch (error) {
      console.error('Falha ao enviar review:', error)
      alert('Falha ao enviar review. Tente novamente.')
    } finally {
      isSubmitting = false
    }
  }
  
  function getStarDisplay(starIndex) {
    const currentRating = hoveredRating || rating
    if (starIndex <= currentRating) {
      return 'full'
    } else if (starIndex - 0.5 <= currentRating) {
      return 'half'
    } else {
      return 'empty'
    }
  }
  
  function getRatingText(rating) {
    if (rating === 0) return ''
    if (rating === 0.5) return '0.5 estrela'
    if (rating === 1) return '1 estrela'
    return `${rating} estrelas`
  }
</script>

<div class="modal-overlay" tabindex="0" onkeydown={(event) => event.key === 'Escape' && onClose()} role="dialog" aria-modal="true" aria-labelledby="modal-title" onclick={handleOverlayClick}>
  <div class="modal-container">
    <!-- Header -->
    <div class="modal-header">
      <div class="album-info">
        <img src={album.image || "/placeholder.svg"} alt="{album.title}" class="album-thumbnail" />
        <div class="album-details">
          <h3 id="modal-title" class="album-title">{album.title}</h3>
          <p class="album-artist">{album.artist} • {album.year}</p>
        </div>
      </div>
      <button class="close-btn" onclick={onClose} aria-label="Fechar">
        <X size={20} />
      </button>
    </div>

    <!-- Form -->
    <form class="review-form" onsubmit={handleSubmit}>
      <!-- Rating Section -->
      <div class="rating-section">
        <label class="rating-label" for="rating">Avalie este álbum *</label>
        <div class="star-rating">
          {#each Array(5) as _, i}
            <div class="star-container">
              <button
                type="button"
                class="star-btn"
                onclick={(event) => handleStarClickFromMove(event, i + 1)}
                onmousemove={(event) => handleStarMouseMove(event, i + 1)}
                onmouseleave={handleStarLeave}
                aria-label="Avaliar até {i + 1} estrelas"
              >
                {#if getStarDisplay(i + 1) === 'full'}
                  <Star size={32} class="star-filled" />
                {:else if getStarDisplay(i + 1) === 'half'}
                  <div class="star-half-wrapper">
                    <Star size={32} class="star-empty" />
                    <div class="star-half-overlay">
                      <Star size={32} class="star-filled" />
                    </div>
                  </div>
                {:else}
                  <Star size={32} class="star-empty" />
                {/if}
              </button>
            </div>
          {/each}
        </div>
        {#if rating > 0}
          <p class="rating-text">
            {getRatingText(rating)}
          </p>
        {/if}
      </div>

      <!-- Comment Section -->
      <div class="comment-section">
        <label for="comment" class="comment-label">Escreva uma review (opcional)</label>
        <textarea
          id="comment"
          class="comment-textarea"
          placeholder="Compartilhe seus pensamentos sobre este álbum..."
          bind:value={comment}
          rows="6"
          maxlength="1000"
          aria-labelledby="comment-label"
        ></textarea>
        <div class="character-count">
          {comment.length}/1000
        </div>
      </div>

      <!-- Actions -->
      <div class="modal-actions">
        <button type="button" class="cancel-btn" onclick={onClose}>
          Cancelar
        </button>
        <button type="submit" class="submit-btn" disabled={rating === 0 || isSubmitting}>
          {#if isSubmitting}
            Enviando...
          {:else}
            Enviar Review
          {/if}
        </button>
      </div>
    </form>
  </div>
</div>

<style>
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.8);
    backdrop-filter: blur(4px);
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 1rem;
    animation: fadeIn 0.2s ease-out;
  }

  @keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
  }

  .modal-container {
    background: #1d232a;
    border-radius: 12px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.5);
    border: 1px solid #374151;
    width: 100%;
    max-width: 500px;
    max-height: 90vh;
    overflow-y: auto;
    animation: modalSlideIn 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

  @keyframes modalSlideIn {
    from { 
      opacity: 0; 
      transform: scale(0.95) translateY(10px); 
    }
    to { 
      opacity: 1; 
      transform: scale(1) translateY(0); 
    }
  }

  .modal-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 1.5rem;
    border-bottom: 1px solid #374151;
  }

  .album-info {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .album-thumbnail {
    width: 60px;
    height: 60px;
    border-radius: 6px;
    object-fit: cover;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  }

  .album-details {
    display: flex;
    flex-direction: column;
  }

  .album-title {
    font-size: 1.125rem;
    font-weight: 600;
    margin: 0;
    color: white;
    line-height: 1.3;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  .album-artist {
    font-size: 0.875rem;
    color: #FFC857;
    margin: 0;
    font-weight: 500;
  }

  .close-btn {
    background: none;
    border: none;
    color: #9ca3af;
    cursor: pointer;
    padding: 0.5rem;
    border-radius: 6px;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .close-btn:hover {
    color: white;
    background: #374151;
  }

  .review-form {
    padding: 1.5rem;
    display: flex;
    flex-direction: column;
    gap: 2rem;
  }

  .rating-section {
    text-align: center;
  }

  .rating-label {
    display: block;
    font-size: 1rem;
    font-weight: 600;
    color: white;
    margin-bottom: 1rem;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  .star-rating {
    display: flex;
    justify-content: center;
    gap: 2px;
    margin-bottom: 0.5rem;
  }

  .star-container {
    position: relative;
    display: inline-block;
  }

  .star-btn {
    background: none;
    border: none;
    cursor: pointer;
    padding: 2px;
    border-radius: 4px;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
  }

  .star-btn:hover {
    transform: scale(1.1);
  }

  .star-half-wrapper {
    position: relative;
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }

  .star-half-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 50%;
    height: 100%;
    overflow: hidden;
  }

  :global(.star-filled) {
    fill: #FFC857;
    color: #FFC857;
  }

  :global(.star-empty) {
    fill: none;
    color: #374151;
    stroke: #374151;
    stroke-width: 1;
  }

  .rating-text {
    font-size: 0.875rem;
    color: #9ca3af;
    margin: 0;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  .comment-section {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .comment-label {
    font-size: 0.875rem;
    font-weight: 500;
    color: white;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  .comment-textarea {
    width: 100%;
    padding: 0.75rem;
    background: #2c3440;
    border: 1px solid #374151;
    border-radius: 8px;
    color: white;
    font-size: 0.875rem;
    font-family: 'Familjen Grotesk', sans-serif;
    resize: vertical;
    min-height: 120px;
    transition: all 0.2s ease;
    box-sizing: border-box;
  }

  .comment-textarea:focus {
    outline: none;
    border-color: #FFC857;
    background: #374151;
  }

  .comment-textarea::placeholder {
    color: #6b7280;
  }

  .character-count {
    font-size: 0.75rem;
    color: #6b7280;
    text-align: right;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  .modal-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
    padding-top: 1rem;
    border-top: 1px solid #374151;
  }

  .cancel-btn,
  .submit-btn {
    padding: 0.75rem 1.5rem;
    border-radius: 8px;
    font-size: 0.875rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
    font-family: 'Familjen Grotesk', sans-serif;
  }

  .cancel-btn {
    background: none;
    border: 1px solid #374151;
    color: #9ca3af;
  }

  .cancel-btn:hover {
    border-color: #4b5563;
    color: white;
  }

  .submit-btn {
    background: #C5283D;
    color: white;
    border: none;
  }

  .submit-btn:hover:not(:disabled) {
    background: #a02030;
    transform: translateY(-1px);
  }

  .submit-btn:disabled {
    background: #6b7280;
    cursor: not-allowed;
    opacity: 0.6;
  }

  /* Responsive Design */
  @media (max-width: 480px) {
    .modal-container {
      margin: 0.5rem;
      max-width: none;
    }

    .modal-header {
      padding: 1rem;
    }

    .review-form {
      padding: 1rem;
      gap: 1.5rem;
    }

    .star-rating {
      gap: 1px;
    }

    .star-btn :global(svg) {
      width: 28px;
      height: 28px;
    }

    .star-btn {
      width: 32px;
      height: 32px;
    }

    .modal-actions {
      flex-direction: column;
    }

    .cancel-btn,
    .submit-btn {
      width: 100%;
    }
  }
</style>
