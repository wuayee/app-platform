/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.dynamicform;

import com.huawei.fit.dynamicform.entity.FormMetaInfo;
import com.huawei.fit.dynamicform.entity.FormMetaQueryParameter;
import com.huawei.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 表单的FIT服务
 *
 * @author x00576283
 * @since 2023/12/12
 */
public interface DynamicFormMetaService {
    /**
     * 查询表单的元数据信息
     *
     * @param parameter 查询参数，可以批量查询
     * @return 表单中的元数据字段列表，批量返回
     */
    @Genericable(id = "250869a27a044d4dadd09681a1e35b4b")
    List<FormMetaInfo> query(List<FormMetaQueryParameter> parameter);
}
