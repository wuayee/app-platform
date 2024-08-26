/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.biz.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 流程节点VO类
 *
 * @author 陈镕希
 * @since 2024-02-27
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowNodeVO {
    /**
     * 流程节点metaId，与前端保持一致
     */
    private String metaId;

    /**
     * 流程节点名称
     */
    private String name;

    /**
     * 流程节点类型
     */
    private String type;

    /**
     * 流程节点触发类型
     */
    private String triggerMode;

    /**
     * 流程节点属性Map，key为属性的键值，value为属性具体的值
     */
    private Map<String, Object> properties;

    /**
     * 流程节点事件列表
     */
    private List<FlowEventVO> events;

    /**
     * 流程节点自动任务
     */
    private FlowJoberVO jober;

    /**
     * 流程节点自动任务数据过滤器
     */
    private FlowFilterVO joberFilter;

    /**
     * 流程节点手动任务
     */
    private FlowTaskVO task;

    /**
     * 流程节点手动任务数据过滤器
     */
    private FlowFilterVO taskFilter;

    /**
     * 流程节点回调函数
     */
    private FlowCallbackVO callback;
}
