/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import style from './style.module.scss';
import { MedicalIcon, FinanceIcon, EducationIcon } from '@/assets/icons.tsx';
import mailBox from '@assets/png/mailbox.png';
import modelEngine from '@assets/png/model-engine.png';
import fit from '@assets/png/fit.png';
import bilibili from '@assets/png/bilibili.png';

const Footer = () => {
    const industries = [
        {
            name: '医疗',
            icon: <MedicalIcon />
        },
        {
            name: '金融',
            icon: <FinanceIcon />
        },
        {
            name: '高校',
            icon: <EducationIcon />
        }
    ];

    const contacts = [
        {
            name: '摩擎',
            icon: modelEngine
        },
        {
            name: 'FIT',
            icon: fit
        },
        {
            name: 'bilibili',
            icon: bilibili
        }
    ];

    return (
        <footer className={style['footer']}>
            <div className={style['footer-item']}>
                <div className={style['title']}>关于我们</div>
                <div className={style['about-us']}>
                    整合三大核心能力，提供从数据处理到应用部署的全生命周期管理。支持主流AI框架，内置自动化运维工具，助力企业快速构建智能应用。
                </div>
            </div>
            <div className={style['footer-item']}>
                <div className={style['title']}>行业赋能</div>
                <div className={style['industry-container']}>
                    {
                        industries.map((industry, index) => (
                            <div key={index} className={style['industry']}>
                                <div className={style['icon']}>{industry.icon}</div>
                                <div className={style['name']}>{industry.name}</div>
                            </div>
                        ))
                    }
                </div>
            </div>
            <div className={style['footer-item']}>
                <div className={style['title']}>联系我们</div>
                <div className={style['contact-us']}>
                    <div className={style['contact-us-qr']}>
                        {
                            contacts.map((item, index) => (
                                <div key={index} className={style['qr-inner']}>
                                    <div className={style['icon']}>
                                        <img src={item.icon} alt="" />
                                    </div>
                                    <div className={style['name']}>{item.name}</div>
                                </div>
                            ))
                        }
                    </div>
                    <div className={style['contact']}>
                        <div className={style['contact-title']}>
                            <img src={mailBox} alt="" />
                            <span>邮箱 modelengine@163.com</span>
                        </div>
                    </div>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
