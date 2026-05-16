import vue from '@vitejs/plugin-vue'
import path from 'path'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@ull-tfg/ull-tfg-vue': path.resolve(__dirname, './src/adapter/vuejs/shims/ull-tfg-vue.ts'),
      d3: path.resolve(__dirname, '../../ull-tfg-vue/ull-tfg-vue/node_modules/d3'),
    },
  },
})
