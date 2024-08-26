/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.biz.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * 流程定义节点手动任务VO类
 *
 * @author 陈镕希
 * @since 2024-02-28
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowTaskVO {
    /**
     * 手动操作任务ID标识
     */
    private String taskId;

    /**
     * 手动操作任务类型
     */
    private String taskType;

    /**
     * 节点任务异常处理fitables集合
     */
    private Set<String> exceptionFitables;

    /**
     * 手动操作任务自定义属性
     * key为属性的键值，value为属性具体的值
     */
    private Map<String, String> properties;
}
