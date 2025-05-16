/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import style from './style.module.scss';

const Introduction = () => {
    return (
        <div className={style['introduction']}>
            <div className={style['title']}>端到端的AI开发流程</div>
            <div className={style['sub-title']}>从数据处理到行业应用落地的全流程解决方案</div>
            <div className={style['primary-intro-container']}>
                <div className={style['primary-intro-img']}></div>
                <div className={style['text-container']}>
                    <div className={style['text']}>ModelEngine提供从数据处理、知识生成，到模型微调和部署，以及RAG（Retrieval Augmented
                        Generation）应用开发的AI训推全流程工具链，用于缩短从数据到模型、数据到AI应用的落地周期。</div>
                    <div className={style['sub-text']}> ModelEngine提供低代码编排、灵活的执行调度、高性能数据总线等技术，结合内置的数据处理算子、RAG框架以及广泛的生态能力，为数据开发工程师、模型开发工程师、应用开发工程师提供高效易用、开放灵活、开箱即用、轻量的全流程AI开发体验。</div>
                </div>
            </div>
        </div>
    );
};

export default Introduction;