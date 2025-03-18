/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import style from './style.module.scss';
import { Popover } from 'antd';
import modelEngine from '@assets/png/model-engine.png';

const Title = () => {
    const content = (
        <div>
            <img src={modelEngine} alt=""/>
            <div style={{textAlign: 'center', fontWeight: '400'}}>扫描二维码申请体验</div>
        </div>
    );

    return (
        <div className={style['title']}>
            <div className={style['start-text']}>让天下没有难落地的企业AI</div>
            <div className={style['start-desc']}>从数据治理到应用部署的全链路AI开发平台</div>
            <Popover content={content}>
                <span className={style['start-btn']}>申请体验</span>
            </Popover>
        </div>
    );
};

export default Title;