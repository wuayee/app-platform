/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.dto.form;

import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.dynamicform.entity.DynamicFormEntity;
import com.huawei.fitframework.annotation.Property;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Aipp表单信息
 *
 * @author 刘信宏
 * @since 2024-02-22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AippFormRsp extends DynamicFormDetailEntity {
    @Property(description = "aipp 最新版本号")
    @JSONField(name = "aipp_version")
    private String aippVersion;

    /**
     * 构造方法
     *
     * @param meta 表单元数据
     * @param data 表单数据
     * @param aippVersion aipp最新版本号
     */
    public AippFormRsp(DynamicFormEntity meta, String data, String aippVersion) {
        super(meta, data);
        this.aippVersion = aippVersion;
    }
}
