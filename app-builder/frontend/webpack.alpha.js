const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const webpack = require("webpack");
const {merge} = require("webpack-merge");
const common = require("./webpack.common.js");
const alphaConfig = require("./src/config/alpha-config.json")
module.exports = merge(common, {
    plugins: [
        new webpack.DefinePlugin({
            "process.env.NODE_ENV": JSON.stringify("alpha"),
        }),
        new webpack.DefinePlugin({
            __APP_CONFIG__: JSON.stringify({...alphaConfig})
        }),
        // 此插件允许添加生成的文件顶部生成一段注释或者代码
        new webpack.BannerPlugin({
          banner: `console.log(${JSON.stringify({...alphaConfig})});`,
          raw: true,
          entryOnly: true,
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
