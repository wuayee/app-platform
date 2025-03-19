import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import markdown from 'vite-plugin-md';
import path from "path";
import postCssPxToRem from 'postcss-pxtorem'

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
    },
    // css: {
    //     postcss: {
    //         plugins: [
    //             postCssPxToRem({
    //                 // 设计稿宽度的1/10，通常是370的1/10
    //                 rootValue: 192,
    //                 // 需要转换的属性，除 border 外所有px 转 rem
    //                 propList: ['*', "!border"],
    //                 // 要忽略的选择器
    //                 selectorBlackList: ['van'],
    //                 replace: true, // 直接更换成rem
    //                 mediaQuery: false, // 是否要在媒体查询中转换px
    //                 minPixelValue: 2 // 设置要转换的最小像素值
    //             })
    //         ]
    //     }
    // }
})
