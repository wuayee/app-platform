const path = require("path");
const {
  merge
} = require("webpack-merge");
const webpack = require("webpack");
const common = require("./webpack.common.js");
const alphaConfig = require("./src/config/alpha-config.json");

module.exports = merge(common, {
  mode: "development",
  devtool: "inline-source-map",
  output: {
    filename: "[name].bundle.js",
    publicPath: '/',
    path: path.resolve(__dirname, "build"),
  },
  plugins: [
    new webpack.DefinePlugin({
      "process.env.NODE_ENV": JSON.stringify("development"),
    }),
    new webpack.DefinePlugin({
      __APP_CONFIG__: JSON.stringify({
        ...alphaConfig
      })
    })
  ],
  module: {
    rules: [{
      test: /\.(sc|sa)ss$/,
      include: [path.resolve(__dirname, "src")],
      exclude: /node_modules/,
      use: [{
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
          loader: 'scoped-css-loader'
        },
        'postcss-loader',
        {
          loader: 'sass-loader',
        },
      ],
    }, ],
  },
  devServer: {
    historyApiFallback: true,
    hot: true,
    open: true,
    inline: true,
    https: false,
    proxy: {
      "/api": {
        target: "https://jane-beta.huawei.com/api",
        // target: "http://10.62.115.236:8028/api",
        // target: "http://10.91.144.110:8080",
        secure: false,
        changeOrigin: true,
        pathRewrite: {
          "^/api": ""
        },
      },
      "/aiApi": {
        target: 'https://tzaip-beta.paas.huawei.com/tzaip/api',
        pathRewrite: {
          "^/aiApi": ""
        },
        secure: false,
        changeOrigin: true,
      },
      "/aippApi": {
        // target: 'http://10.91.144.92:8028/api/jober/v1/api',
        // target: 'http://10.169.58.7:8080/v1/api',
        target: 'http://80.11.128.66:31111/api/jober/v1/api',
        // target: 'http://10.91.144.79:8028/api/jober/v1/api',
        // target: 'https://jane-beta.huawei.com/api/jober/v1/api',
        // target: 'http://10.91.144.226:8028/api/jober/v1/api',
        pathRewrite: {"^/aippApi": ""},
        secure: false,
        changeOrigin: true,
      },
      "/modelApi": {
        target: 'https://tzaip-beta.paas.huawei.com/api',
        pathRewrite: {
          "^/modelApi": ""
        },
        secure: false,
        changeOrigin: true,
      },
      "/knowledge/repos": {
        target: 'http://10.85.112.74:8080',
        pathRewrite: {
          "^/modelApi": ""
        },
        secure: false,
        changeOrigin: true,
      },
      "/knowledge": {
        target: 'http://10.85.112.75:8080',
        pathRewrite: {
          "^/modelApi": ""
        },
        secure: false,
        changeOrigin: true,
      },
      "/app": {
        target: 'http://80.11.128.66:30216/v1/api',
        pathRewrite: {"^/app": ""},
        secure: false,
        changeOrigin: true,
      },
    },
  },
});
