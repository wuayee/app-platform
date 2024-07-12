const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const webpack = require("webpack");
const {
  merge
} = require("webpack-merge");
const common = require("./webpack.common.js");
module.exports = merge(common, {
  plugins: [
    new webpack.DefinePlugin({
      "process.env.NODE_ENV": JSON.stringify("gamma"),
    })
  ],
  module: {
    rules: [{
      test: /\.(sc|sa)ss$/,
      include: [path.resolve(__dirname, "src")],
      exclude: /node_modules/,
      use: [
        MiniCssExtractPlugin.loader,
        {
          loader: 'css-loader',
          options: {
            sourceMap: true,
            importLoaders: 2,
            modules: {
              auto: true,
              exportLocalsConvention: 'dashesOnly',
              localIdentName: '[local]__[hash:base64:5]',
            },
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
});
