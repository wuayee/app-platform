/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, {Suspense} from 'react';
import {createRoot} from 'react-dom/client';
import app from './app';

const render = Component => {
    const container = document.getElementById('root');
    const root = createRoot(container);
    return root.render(
        <Suspense fallback={<div id='Loading'>
            <div className='loader-inner ball-beat'>
                <div/>
                <div/>
                <div/>
                <div/>
                <div/>
            </div>
        </div>}>
            <Component/>
        </Suspense>
    );
}

render(app);
