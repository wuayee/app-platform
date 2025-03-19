/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import style from './style.module.scss';
import { LinkIcon } from '@assets/icons.tsx';

const Title = () => {

    const Detail = ({title, link}) => (
        <>
            <div className={style['fit-inner-text-container']}>
                <div className={style['fit-inner-text']}>{title}</div>
                <div className={style['fit-inner-text-detail-container']} onClick={() => {window.open(link)}}>
                    <div className={style['fit-inner-text-detail']}>查看详情</div>
                    <div className={style['fit-inner-text-detail-icon']}><LinkIcon/></div>
                </div>
            </div>
        </>
    );

    return (
        <div className={style['fit-title']}>
            <div className={style['fit-text']}>FIT: 重新定义 AI 工程化的三维坐标系</div>
            <div className={style['fit-desc']}>Java 企业级 AI 开发框架，提供多语言函数引擎（FIT）、流式编排引擎（WaterFlow）及
                Java 生态的 LangChain 替代方案（FEL）。原生 / Spring 双模运行，支持插件热插拔与智能聚散部署，无缝统一大模型与业务系统。
            </div>
            <div className={style['fit-inner']}>
                <div className={style['fit-inner-img']}></div>
                <div className={style['fit-inner-content']}>
                <Detail title={'FIT Core：语言无界，算力随需'} link={'https://gitcode.com/ModelEngine/fit-framework/tree/main/framework/fit/java'}/>
                <div className={style['fit-sub-text']}>多语言函数计算底座（Java/Python/C++）支持插件化热插拔，独创智能聚散部署——代码无需修改，单体应用与分布式服务一键切换，运行时自动路由本地调用或 RPC，让基础设施成为「隐形的伙伴」。</div>
                <Detail title={'WaterFlow Engine：流式智能，万物可编排'} link={'https://gitcode.com/ModelEngine/fit-framework/tree/main/framework/waterflow/java'}/>
                <div className={style['fit-sub-text']}>打破 BPM 与响应式编程的次元壁，图形化编排与声明式 API 双模驱动。业务逻辑可像乐高组合般动态拼接，从毫秒级微流程到跨系统长事务，皆以统一范式驾驭。</div>
                <Detail title={'FEL (FIT Expression for LLM)：Java 生态的 LangChain 革命'} link={'https://gitcode.com/ModelEngine/fit-framework/tree/main/framework/fel/java'}/>
                <div className={style['fit-sub-text']}>当 Python 阵营的 LangChain 重塑 AI 应用开发时，FEL 为 Java 开发者带来了更符合工程化实践的答案——基于标准化原语封装大模型、知识库与工具链，让 AI 能力真正融入 Java 技术栈的血脉。</div>
              </div>
            </div>
        </div>
    );
};

export default Title;