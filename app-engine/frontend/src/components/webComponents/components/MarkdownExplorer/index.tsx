/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import Navigate from '../../components/Navigate';
import style from './style.module.scss';
import { Layout } from 'antd';
import MarkdownViewer from '../../components/MarkdownViewer';
import { useEffect, useState } from 'react';
import code from '@assets/png/code.png';
import docs from '@assets/png/docs.png';

const { Sider } = Layout;
const MarkdownExplorer = ({
    config,
    defaultCurrentMd,
    defaultCurrentGroup,
    defaultOpenKeys,
    defaultSelectedKeys,
    markdownFiles
}) => {

    const [currentMd, setCurrentMd] = useState(defaultCurrentMd);
    const [currentGroup, setCurrentGroup] = useState(defaultCurrentGroup);
    const [content, setContent] = useState('');
    const loadMarkdownFile = async () => {
        try {
            const key = `./markdown/${currentGroup}/${currentMd}`;
            const content = await markdownFiles[key]();
            setContent(content);
        } catch (error) {
            console.error('Failed to load Markdown file:', error);
        }
    };

    const onMenuClick = (e) => {
        setCurrentGroup(e.keyPath[e.keyPath.length - 1]);
        setCurrentMd(e.domEvent.target.textContent);
    }

    useEffect(() => {
        loadMarkdownFile()
    }, [currentMd]);

    return (
        <>
            <div className={style['docs-container']}>
                <div className={style['docs-container-sider']}>
                    <div className={style['docs-container-title']}>
                        { config.type === 'code' ? <img src={code} ></img> : <img src={docs} ></img> }
                        <span>{config.name}</span>
                    </div>
                    <div className={style['docs-container-sider-content']}>
                        <Sider width={256} style={{ height: '100%' }} theme={'light'}>
                            <Navigate items={config.items}
                                onMenuClick={onMenuClick}
                                defaultOpenKeys={defaultOpenKeys}
                                defaultSelectedKeys={defaultSelectedKeys}
                            />
                        </Sider>
                    </div>
                </div>
                <div className={style['docs-container-content']}>
                    <MarkdownViewer content={content} />
                </div>
            </div>
        </>
    );
}
export default MarkdownExplorer;