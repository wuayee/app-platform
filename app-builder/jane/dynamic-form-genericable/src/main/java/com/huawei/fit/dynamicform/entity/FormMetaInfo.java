/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.dynamicform.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 表单元数据信息
 *
 * @author x00576283
 * @since 2023/12/13
 */
@Data
public class FormMetaInfo {
    /**
     * 表单id
     */
    private String formId;

    /**
     * 表单版本
     */
    private String version;

    /**
     * 表单的元信息列表
     */
    private List<FormMetaItem> formMetaItems;

    public FormMetaInfo(String formId, String version) {
        this.formId = formId;
        this.version = version;
        this.formMetaItems = new ArrayList<>();
    }
}
