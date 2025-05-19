/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import style from './style.module.scss';
import { Button, Dropdown } from 'antd';
import { useNavigate } from 'react-router-dom';
import logo from '@assets/png/logo.png';
import menu from '@assets/png/menu.png';
import close from '@assets/png/close.png';
import gitcode from '@assets/png/gitcode.png';
import gitee from '@assets/png/gitee.png';
import github from '@assets/png/github.png';
import { useState } from 'react';

const Header = () => {
    const navigate = useNavigate();
    const [menuVisible, setMenuVisible] = useState(false);

    const onNavigateClick = (path) => {
        setMenuVisible(false);
        navigate(path);
    }

    const onOpenLink = (path) => {
        setMenuVisible(false);
        window.open(path, '_blank', 'noopener,noreferrer');
    }

    const onToggleMemu = () => {
        setMenuVisible(!menuVisible);
    }

    return (
        <div className={style['header']}>
            <img src={logo} className={style['logo']}></img>
            <div className={style['menu-icon-wrap']}>
                <img src={menuVisible ? close: menu} className={style['menu-icon']} onClick={onToggleMemu}></img>
            </div>
            <div className={style['header-button-group']}>
                <Button type="text" className={style['text']} onClick={() => onNavigateClick('/home')}>首页</Button>
                <CodeDropDown />
                <Button type="text" className={style['text']} onClick={() => onNavigateClick('/codeExamples')}>代码示例</Button>
                <Button type="text" className={style['text']} onClick={() => onNavigateClick('/docs')}>官方文档</Button>
            </div>
            <ul className={menuVisible ? style['header-button-group-sm'] + ' ' + style['show'] : style['header-button-group-sm']}>
                <li onClick={() => onNavigateClick('/home')}>首页</li>
                <li>开源代码访问</li>
                <li className={style['sub-menu']} onClick={() => onOpenLink('https://gitcode.com/ModelEngine')}>
                    <img src={gitcode} alt="" />
                    <span>Gitcode</span>
                </li>
                <li className={style['sub-menu']} onClick={() => onOpenLink('https://gitee.com/openeuler/modelengine')}>
                    <img src={gitee} alt="" />
                    <span>Gitee</span>
                </li>
                <li className={style['sub-menu']} onClick={() => onOpenLink('https://github.com/ModelEngine-Group/fit-framework')}>
                    <img src={github} alt="" />
                    <span>Github</span>
                </li>
                <li onClick={() => onNavigateClick('/codeExamples')}>代码示例</li>
                <li onClick={() => onNavigateClick('/docs')}>官方文档</li>
            </ul>
        </div>
    );
};

const CodeDropDown = () => {
    const items = [
        {
            key: 'gitcode',
            label: (
                <a target="_blank" className={style['code-link']} rel="noopener noreferrer" href="https://gitcode.com/ModelEngine">
                    <img src={gitcode} alt="" />
                    <span>Gitcode</span>
                </a>
            ),
        },
        {
            key: 'gitee',
            label: (
                <a target="_blank" className={style['code-link']} rel="noopener noreferrer" href="https://gitee.com/openeuler/modelengine">
                    <img src={gitee} alt="" />
                    <span>Gitee</span>
                </a>
            ),
        },
        {
            key: 'github',
            label: (
                <a target="_blank" className={style['code-link']} rel="noopener noreferrer" href="https://github.com/ModelEngine-Group/fit-framework">
                    <img src={github} alt="" />
                    <span>Github</span>
                </a>
            ),
        }
    ];

    return (
        <Dropdown menu={{ items }} arrow>
            <Button type='text' className={style['text']}>开源代码访问</Button>
        </Dropdown>
    );
};

export default Header;
