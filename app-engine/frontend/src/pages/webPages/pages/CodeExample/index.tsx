/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import {config} from './config/config.ts';
import MarkdownExplorer from '../../components/MarkdownExplorer';

const Docs = () => {
    const markdownFiles = import.meta.glob(`./markdown/**/*.md`, { as: 'raw' });
    return (
        <>
            <MarkdownExplorer
                config={config}
                defaultCurrentMd={'01. 模型.md'}
                defaultCurrentGroup={'fel'}
                defaultOpenKeys={['fel']}
                defaultSelectedKeys={['01. 模型.md']}
                markdownFiles={markdownFiles}
            />
        </>
    );
}
export default Docs;