/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import Home from '../pages/Home';
import Docs from '../pages/Docs';
import CodeExample from '../pages/CodeExample';

const routes = [
    {
        path: '/home',
        element: Home
    },
    {
        path: '/docs',
        element: Docs
    },
    {
        path: 'codeExamples',
        element: CodeExample
    }
]

export default routes;