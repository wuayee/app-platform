const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const webpack = require("webpack");
const { merge } = require("webpack-merge");
const common = require("./webpack.common.js");
const prodConfig = require("./src/config/product-config.json");
const PUBLICPATH = "/elsa/";

module.exports = merge(common, {
  output: {
    // publicPath: PUBLICPATH,
  },
  plugins: [
    new webpack.DefinePlugin({
      "process.env.NODE_ENV": JSON.stringify("production"),
    }),
      new webpack.DefinePlugin({
          __APP_CONFIG__: JSON.stringify({...prodConfig})
      })
  ],
  module: {
    rules: [
      {
        test: /\.(sa|sc)ss$/,
        include: [path.resolve(__dirname, "src")],
        exclude: /node_modules/,
        use: [
          MiniCssExtractPlugin.loader,
          "css-loader",
          "sass-loader",
        ],
      },
    ],
  },
});
