import { defineConfig } from 'vite'
import { svelte } from '@sveltejs/vite-plugin-svelte'

// https://vite.dev/config/
export default defineConfig({
  plugins: [svelte()],
    server: {
    proxy: {
      '/login': 'http://localhost:8080',
      '/signup': 'http://localhost:8080'

    },
    clearScreen: false
  }
})
