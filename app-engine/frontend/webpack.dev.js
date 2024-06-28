const path = require('path');
const { merge } = require('webpack-merge');
const webpack = require('webpack');
const common = require('./webpack.common.js');
const alphaConfig = require('./src/config/alpha-config.json');

module.exports = merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',
  output: {
    filename: '[name].bundle.js',
    publicPath: '/',
    path: path.resolve(__dirname, 'build'),
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('development'),
    }),
    new webpack.DefinePlugin({
      __APP_CONFIG__: JSON.stringify({
        ...alphaConfig,
      }),
    }),
  ],
  module: {
    rules: [
      {
        test: /\.(sc|sa)ss$/,
        include: [path.resolve(__dirname, 'src')],
        exclude: /node_modules/,
        use: [
          {
            loader: 'style-loader',
          },
          {
            loader: 'css-loader',
            options: {
              sourceMap: true,
              importLoaders: 2,
            },
          },
          {
            loader: 'scoped-css-loader',
          },
          'postcss-loader',
          {
            loader: 'sass-loader',
          },
        ],
      },
    ],
  },
  devServer: {
    historyApiFallback: true,
    hot: true,
    open: true,
    inline: true,
    https: false,
    proxy: {
      '/api': {
        target: 'http://10.62.115.236:8080',
        secure: false,
        changeOrigin: true,
        pathRewrite: { '^/api/jober': '' },
      },
      '/aiApi': {
        target: 'http://10.62.115.236:8080',
        pathRewrite: {
          '^/aiApi': '',
        },
        secure: false,
        changeOrigin: true,
      },
      '/aippApi': {
        target: 'http://10.62.115.236:8080',
        // pathRewrite: { '^/aippApi': '/api/jober/v1/api' },
        pathRewrite: { '^/aippApi': '/v1/api' },
        secure: false,
        changeOrigin: true,
      },
      '/modelApi': {
        target: 'http://10.62.115.236:8080',
        pathRewrite: {
          '^/modelApi': '/api',
        },
        secure: false,
        changeOrigin: true,
      },
      '/knowledge': {
        target: 'http://10.62.115.236:8080',
        pathRewrite: {
          '^/knowledge': '',
        },
        secure: false,
        changeOrigin: true,
      },
      '/app': {
        target: 'http://10.62.115.236:8080',
        pathRewrite: { '^/app': '' },
        secure: false,
        changeOrigin: true,
      },
      '/v1': {
        target: 'http://10.62.115.236:8080',
        pathRewrite: { '^/v1': '/v1' }, //不能替换V1
        secure: false,
        changeOrigin: true,
      },
      '/elsaApi': {
        target: 'http://10.62.115.236:8080',
        pathRewrite: {
          '^/elsaApi': '',
        },
        secure: false,
        changeOrigin: true,
      },
      '/modelbase': {
        target: 'http://10.62.115.236:8080',
        pathRewrite: {
          '^/modelbase': '/api/model_manage', // 后端环境即为此路径
        },
        secure: false,
        changeOrigin: true,
      },
      '/modeltrain': {
        target: 'http://10.62.115.236:8080',
        pathRewrite: {
          '^/modeltrain': '/api/model_finetune', // 后端环境即为此路径
        },
        secure: false,
        changeOrigin: true,
      },
    },
  },
});
