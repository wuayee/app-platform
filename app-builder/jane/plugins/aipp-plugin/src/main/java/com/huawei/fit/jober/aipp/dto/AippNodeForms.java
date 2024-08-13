/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto;

import com.huawei.fit.dynamicform.entity.FormMetaInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Aipp节点表单
 *
 * @author 刘信宏
 * @since 2023-12-25
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippNodeForms {
    /**
     * 节点类型
     * 查看 {@link com.huawei.fit.jober.entity.consts.NodeTypes}
     */
    private String type;

    /**
     * metaInfo
     */
    private List<FormMetaInfo> metaInfo;
}
