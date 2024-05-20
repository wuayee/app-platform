const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const webpack = require("webpack");
const {merge} = require("webpack-merge");
const common = require("./webpack.common.js");
const betaConfig = require("./src/config/beta-config.json")
module.exports = merge(common, {
    plugins: [
      new webpack.DefinePlugin({
        "process.env.NODE_ENV": JSON.stringify("beta"),
      }),
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
