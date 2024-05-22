import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';
import libCss from 'vite-plugin-libcss';
import {fileURLToPath, URL} from 'node:url'
import svgr from "vite-plugin-svgr";

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        react(),
        libCss(),
        svgr()
    ],
    build: {
        lib: {
            entry: 'src/index.js',
            name: 'elsa-react',
            filename: (format) => `elsa-react.${format}.js`
        },
        sourcemap: true,
        rollupOptions: {
            external: ['react', 'react-dom', '@fit-elsa/elsa-core', 'antd', 'axios'],
            output: {
                globals: {
                    react: 'react',
                    'react-dom': 'ReactDOM',
                    '@fit-elsa/elsa-core': '@fit-elsa/elsa-core'
                }
            }
        },
        outDir: "build"
    },
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
})
