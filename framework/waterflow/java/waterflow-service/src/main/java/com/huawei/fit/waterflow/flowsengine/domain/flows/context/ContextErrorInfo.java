/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * flow context错误信息类
 *
 * @author 杨祥宇
 * @since 2023/12/19
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContextErrorInfo {
    /**
     * 原报错详细信息message
     */
    private String originMessage;

    /**
     * 错误码
     */
    private Integer errorCode;

    /**
     * 错误详细信息说明
     */
    private String errorMessage;

    /**
     * 调用出错的fitable实现
     */
    private String fitableId;

    /**
     * 节点名称
     */
    private String nodeName;
}
