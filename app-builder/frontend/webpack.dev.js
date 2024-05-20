const path = require("path");
const {merge} = require("webpack-merge");
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
        __APP_CONFIG__: JSON.stringify({...alphaConfig})
      })
    ],
    module: {
      rules: [
        {
          test: /\.(sa|sc)ss$/,
          include: [path.resolve(__dirname, "src")],
          exclude: /node_modules/,
          use: [
            "style-loader",
            "css-loader",
            "sass-loader",
          ],
        },
      ],
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    },
    devServer: {
        historyApiFallback: true,
        hot: true,
        open: true,
        inline: true,
        https: true,
        proxy: {
          "/api": {
            target: "https://jane-beta.huawei.com/api",
            // target: "http://10.62.115.236:8028/api",
            // target: "http://10.91.144.110:8080",
            secure: false,
            changeOrigin: true,
            pathRewrite: {"^/api": ""},
          },
          "/aiApi": {
            target: 'https://tzaip-beta.paas.huawei.com/tzaip/api',
            pathRewrite: {"^/aiApi": ""},
            secure: false,
            changeOrigin: true,
          },
          "/aippApi": {
            // target: 'http://10.91.144.92:8028/api/jober/v1/api',
            // target: 'http://10.169.58.7:8028/api/jober/v1/api',
            // target: 'http://10.91.144.79:8028/api/jober/v1/api',
            target: 'https://jane-beta.huawei.com/api/jober/v1/api',
            // target: 'http://10.91.144.226:8028/api/jober/v1/api',
            pathRewrite: {"^/aippApi": ""},
            secure: false,
            changeOrigin: true,
          },
          "/modelApi": {
            target: 'https://tzaip-beta.paas.huawei.com/api',
            pathRewrite: {"^/modelApi": ""},
            secure: false,
            changeOrigin: true,
          },
      },
    },
});
