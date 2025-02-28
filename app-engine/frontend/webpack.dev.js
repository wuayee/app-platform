/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

const path = require('path');
const { merge } = require('webpack-merge');
const webpack = require('webpack');
const common = require('./webpack.common.js');
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
  ],
  devServer: {
    historyApiFallback: true,
    hot: true,
    open: true,
    inline: true,
    https: false,
    proxy: {
      '/api': {
        target: 'http://10.243.226.192:30010',
        secure: false,
        changeOrigin: true,
        pathRewrite: {
          '^/api/jober': '/api/jober',
          '^/api': '',
        },
      },

      '/aiApi': {
        target: 'http://10.243.226.192:30010',
        pathRewrite: {
          '^/aiApi': '/tzaip/api/hisp',
        },
        secure: false,
        changeOrigin: true,
      },
      '/aippApi': {
        target: 'http://10.243.226.192:30010',
        pathRewrite: { '^/aippApi': '/api/jober/v1/api' },
        secure: false,
        changeOrigin: true,
      },
      '/modelApi': {
        target: 'http://80.11.128.86:30040',
        pathRewrite: {
          '^/modelApi': '/api',
        },
        secure: false,
        changeOrigin: true,
      },
      '/knowledge': {
        target: 'http://80.11.128.86:30040',
        pathRewrite: {
          '^/knowledge': '',
        },
        secure: false,
        changeOrigin: true,
      },
      '/app': {
        target: 'http://10.91.144.186:8080',
        pathRewrite: { '^/app': '' },
        secure: false,
        changeOrigin: true,
      },
      '/v1': {
        target: 'http://80.11.128.86:30040',
        pathRewrite: { '^/v1': '/v1' },
        secure: false,
        changeOrigin: true,
      },
      '/elsaApi': {
        target: 'http://80.11.128.86:30040',
        pathRewrite: {
          '^/elsaApi': '',
        },
        secure: false,
        changeOrigin: true,
      },
      '/llmApi': {
        target: 'http://80.11.128.86:30040',
        pathRewrite: {
          '^/llmApi': '',
        },
        secure: false,
        changeOrigin: true,
      },
      '/modelbase': {
        target: 'http://80.11.128.86:30040',
        pathRewrite: {
          '^/modelbase': '/api/model_manage',
        },
        secure: false,
        changeOrigin: true,
      },
      '/modeltrain': {
        target: 'http://80.11.128.86:30040',
        pathRewrite: {
          '^/modeltrain': '/api/model_finetune',
        },
        secure: false,
        changeOrigin: true,
      },
      '/ttApi': {
        target: 'http://10.169.57.250:8080',
        pathRewrite: { '^/ttApi': '' },
        secure: false,
        changeOrigin: true,
      },
      '/evaluate': {
        target: 'http://10.245.112.252:8080',
        pathRewrite: { '^/evaluate': '' },
        secure: false,
        changeOrigin: true,
      },
    },
  },
});
