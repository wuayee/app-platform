/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import style from './style.module.scss';
import modelLite from '@assets/png/modelLite.png';
import edatemate from '@assets/png/edatamate.png';
import appengine from '@assets/png/appengine.png';

const Features = () => {

    return (
        <div className={style['features-container']}>
            <div className={style['feature-card']} style={{ paddingRight: '0' }}>
                <div className={style['feature-text']} style={{ marginRight: '32px' }}>
                    <div className={style['title']}>数据使能</div>
                    <div className={style['content']}>提供数据清洗和知识生成的一站式工具链，提升数据处理效率 </div>
                    <div className={style['sub-title']}>丰富的数据处理算子及自定义扩展功能</div>
                    <div className={style['content']}>内置50+数据处理算子，覆盖文本、图像等多模态数据，同时支持自定义算子插件扩展机制，满足用户差异化场景。</div>
                    <div className={style['sub-title']}>基于大模型的QA对自动生成</div>
                    <div className={style['content']}>基于清洗后的文本数据与外置大模型服务，自动生成大模型微调QA对，留用率60%；具备QA对自动评估/留用审核能力，大幅提升QA对审核效率。</div>
                    <div className={style['sub-title']}>丰富的数据质量评估模式</div>
                    <div className={style['content']}>内置数据质量评估能力，对文本质量进行人工/自动化评估，对数据清洗效果产生反馈，辅助优化数据清洗流程。</div>
                </div>
                <img src={edatemate} className={style['feature-img']} alt="" />
            </div>
            <div className={style['feature-card']} style={{ paddingLeft: '0' }}>
                <img src={modelLite} className={style['feature-img']} alt="" />
                <div className={style['feature-text']} style={{ marginLeft: '32px' }}>
                    <div className={style['title']}>模型使能</div>
                    <div className={style['content']}>模型管理与评估，训练和推理服务部署任务一键式下发和管理；</div>
                    <div className={style['sub-title']}>管理</div>
                    <div className={style['content']}>支持模型权重的上传和版本管理；支持模型量化格式转换；支持模型权重回收站；</div>
                    <div className={style['sub-title']}>模型训练</div>
                    <div className={style['content']}>支持全参和LoRA微调训练；</div>
                    <div className={style['content']}>支持TP，PP，DP分布式训练策略组合和训练超参数配置；</div>
                    <div className={style['content']}>支持训练任务监控和 Checkpoints 归档保存；</div>
                    <div className={style['sub-title']}>模型服务</div>
                    <div className={style['content']}>支持分布式推理策略配置，支持推理服务超参配置，支持量化模型推理部署；</div>
                    <div className={style['content']}>支持统一北向接口网管，支持SK认证和OpenAI API风格调用，支持API访问统计；</div>
                    <div className={style['sub-title']}>模型评测</div>
                    <div className={style['content']}>支持模型评测任务的下发和管理；</div>
                    <div className={style['content']}>支持模型评测结果的可视化分析和导出；</div>
                </div>
            </div>
            <div className={style['feature-card']} style={{ paddingRight: '0' }}>
                <div className={style['feature-text']} style={{ marginRight: '32px' }}>
                    <div className={style['title']}>应用使能</div>
                    <div className={style['content']}>一站式可视化应用编排，应用分钟级级发布</div>
                    <div className={style['sub-title']}>可视化应用编排</div>
                    <div className={style['content']}>通过可视化编排，大模型流式开发关键特点，实现低代码应用开发，降低开发门槛。</div>
                    <div className={style['sub-title']}>一站式开发平台</div>
                    <div className={style['content']}>面向普通用户提供零代码拖拽式编排；企业级场景可编排复用，支持多知识库和多模型配置，模型流式输出内置支持；提供北向API被上层集成</div>
                    <div className={style['sub-title']}>声明式开发框架</div>
                    <div className={style['content']}>自研声明式框架，提供多种调用方式，缺省支持模型流式输出；内置10+大模型原语；解耦模型服务与知识库服务，换模型和知识库不改应用，整体开发与维护效率可提高30%+</div>
                    <div className={style['sub-title']}>多语言插件</div>
                    <div className={style['content']}>提供插件开发SDK，面向Java与Python开发者提供Java与Python两种语言的插件开发能力。</div>
                </div>
                <img src={appengine} className={style['feature-img']} alt="" />
            </div>
        </div>
    );
};

export default Features;
