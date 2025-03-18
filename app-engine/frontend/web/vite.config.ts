import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import markdown from 'vite-plugin-md';
import path from "path";

export default defineConfig({
    plugins: [
      react(),
      markdown()
    ],
    assetsInclude: ["**/*.md", '**/*.png'],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src'),
            '@assets': path.join(__dirname, "src/assets"),
        }
    },
    base: '',
    build: {
        outDir: 'build',
        assetsDir: 'assets',
        emptyOutDir: true,
        rollupOptions: {
            output: {
                entryFileNames: `[name].bundle[hash].js`,
                chunkFileNames: `[name].bundle[hash].js`,
                assetFileNames: `assets/[name].[hash].[ext]`,
            },
        },
        sourcemap: true,
    }
})
