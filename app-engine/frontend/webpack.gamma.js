const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const webpack = require("webpack");
const {
  merge
} = require("webpack-merge");
const common = require("./webpack.common.js");
const gammaConfig = require("./src/config/gamma-config.json")
module.exports = merge(common, {
  plugins: [
    new webpack.DefinePlugin({
      "process.env.NODE_ENV": JSON.stringify("gamma"),
    }),
    new webpack.DefinePlugin({
      __APP_CONFIG__: JSON.stringify({
        ...gammaConfig
      })
    }),
    // 此插件允许添加生成的文件顶部生成一段注释或者代码
    new webpack.BannerPlugin({
      banner: `console.log(${JSON.stringify({...gammaConfig})});`,
      raw: true,
      entryOnly: true,
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
