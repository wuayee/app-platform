const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const {
  CleanWebpackPlugin
} = require("clean-webpack-plugin");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const HappyPack = require("happypack");
const PUBLIC_PATH = "/appbuilder/";
const webpack = require("webpack");

module.exports = {
  entry: {
    index: "./src/index.js",
  },
  output: {
    filename: "[name].bundle[hash].js",
    publicPath: PUBLIC_PATH,
    path: path.resolve(__dirname, "build"),
  },
  module: {
    exprContextCritical: false,
    rules: [{
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        include: [path.resolve(__dirname, "src")],
        use: {
          loader: "happypack/loader?id=babel",
        },
      },
      {
        test: /\.svg$/,
        use: ['@svgr/webpack']
      },
      {
        test: /\.(png|jpg|gif)$/,
        type: 'javascript/auto',
        include: [path.resolve(__dirname, "src")],
        exclude: /node_modules/,
        use: [{
          loader: "url-loader",
          options: {
            esModule: false
          }
        }, ],
      },
      {
        test: /\.css$/,
        use: ['style-loader', 'css-loader']
      },
      {
        test: /\.(woff|woff2|eot|otf|ttf)$/,
        loader: "file-loader",
        options: {
          name: "[name]-[hash:5].min.[ext]"
        }
      },
      {
        test: /\.(ts|tsx)$/,
        exclude: /node_modules/,
        use: [{
          loader: 'babel-loader',
          options: {
            presets: [
              '@babel/preset-env',
              '@babel/preset-react',
              '@babel/preset-typescript',
            ],
          },
        }, ],
      },
    ],
  },
  resolve: {
    alias: {
      // todo@zhangxiaohua 稳定后删除注释
      // __utils: path.join(__dirname, "src/utils"),
      __styles: path.join(__dirname, "src/styles"),
      __plugins: path.join(__dirname, "src/plugins"),
      // __enum: path.join(__dirname, "src/enum"),
      __pages: path.join(__dirname, "src/pages"),
      __service: path.join(__dirname, "src/service"),
      __constants: path.join(__dirname, "src/constants"),
      __support: path.join(__dirname, "src/support"),
      __components: path.join(__dirname, "src/components"),
      __shared: path.join(__dirname, "src/shared"),
      '@assets': path.join(__dirname, "src/assets"),
      __store: path.join(__dirname, "src/store"),
      '@': path.resolve(__dirname, 'src')
    },
    extensions: ['.ts', '.tsx', '.js', '.jsx'],
  },
  plugins: [
    new HappyPack({
      id: "babel",
      loaders: ["babel-loader?cacheDirectory"]
    }),
    new MiniCssExtractPlugin({
      filename: "[name].[hash:8].css",
      chunkFilename: "[name].[hash:8].css",
    }),
    new HtmlWebpackPlugin({
      title: "elsa",
      template: path.join(process.cwd(), './src/index.html'),
      filename: "index.html",
    }),
    new CleanWebpackPlugin(),
    new webpack.DefinePlugin({
      'process.env.SSO_URL': JSON.stringify(process.env.SSO_URL)
    })
  ],
  optimization: {
    splitChunks: {
      chunks: 'all',
    }
  },
};
