/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fit.jober.entity.InstanceCategoryChanged;
import com.huawei.fitframework.annotation.Genericable;

import java.util.List;

/**
 * InstancesCategory修改Genericable。
 *
 * @author 陈镕希
 * @since 2023-08-30
 */
public interface OnInstancesCategoryChanged {
    /**
     * process
     *
     * @param messages messages
     */
    @Genericable(id = "e504e51720c242ab8edf5d0ccf97f5cc")
    void process(List<InstanceCategoryChanged> messages);
}
