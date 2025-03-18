/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import { Menu } from 'antd';

const Navigate = ({items, onMenuClick, defaultSelectedKeys, defaultOpenKeys}) => (
    <Menu
        items={items}
        mode='inline'
        onClick={onMenuClick}
        defaultSelectedKeys={defaultSelectedKeys}
        inlineIndent={12}
        defaultOpenKeys={defaultOpenKeys}
    />
);

export default Navigate;