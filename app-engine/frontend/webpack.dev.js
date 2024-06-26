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
        target: 'http://80.11.128.86:30010',
        secure: false,
        changeOrigin: true,
        pathRewrite: {
          '^/api/jober': '/api/jober',
          '^/api': '',
        },
      },
      '/aiApi': {
        target: 'http://80.11.128.86:30010',
        pathRewrite: {
          '^/aiApi': '/tzaip/api/hisp', // 后端环境即为次路径
        },
        secure: false,
        changeOrigin: true,
      },
      '/aippApi': {
        target: 'http://80.11.128.86:30010',
        pathRewrite: { '^/aippApi': '/api/jober/v1/api' },
        // pathRewrite: { '^/aippApi': '/v1/api' },
        secure: false,
        changeOrigin: true,
      },
      '/modelApi': {
        target: 'http://80.11.128.86:30030',
        pathRewrite: {
          '^/modelApi': '/api',
        },
        secure: false,
        changeOrigin: true,
      },
      '/knowledge': {
        target: 'http://80.11.128.86:30030',
        pathRewrite: {
          '^/knowledge': '',
        },
        secure: false,
        changeOrigin: true,
      },
      '/app': {
        target: 'http://80.11.128.86:30030',
        pathRewrite: { '^/app': '' },
        secure: false,
        changeOrigin: true,
      },
      '/v1': {
        target: 'http://80.11.128.86:30030',
        pathRewrite: { '^/v1': '/v1' }, //不能替换V1
        secure: false,
        changeOrigin: true,
      },
      '/elsaApi': {
        target: 'http://10.91.144.226:8080',
        pathRewrite: {
          '^/elsaApi': '',
        },
        secure: false,
        changeOrigin: true,
      },
    },
  },
});
