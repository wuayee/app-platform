/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

const path = require("path");
module.exports = {
    mode: "production",
    entry: {
        "presentation": "./plugins/presentation/src/index.js"
    },
    output: {
        filename: "[name].js",
        path: path.resolve(__dirname, "build"),
        library: {
            type: "module"
        }
    },
    experiments: {
        outputModule: true,
    },
    module: {
        rules: [
            {
                test: /\.(js)$/,
                exclude: [/node_modules/],
                loader: "babel-loader",
            },
        ],
    },
    // todo@zhangyue 在将elsa-core放入npm仓库之后，需要将elsa-core排除掉.
    // externals: {
    //     "../../build/elsa-core.js" : "commonjs ../../build/elsa-core.js"
    // },
    resolve: {
        extensions: [".js", ".jsx"]
    },
    optimization: {
        minimize: false
    }
};
