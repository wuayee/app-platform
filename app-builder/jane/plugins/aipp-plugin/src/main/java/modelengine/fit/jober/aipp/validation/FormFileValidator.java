/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.validation;

import java.io.File;
import java.util.Map;

/**
 * 表单文件校验器
 *
 * @author 陈潇文
 * @since 2024/11/18
 */
public interface FormFileValidator {
    /**
     * 校验表单schema是否有问题
     *
     * @param config 表单schema
     */
    void validateSchema(Map<String, Object> config);

    /**
     * 校验表单预览图是否有问题
     *
     * @param file 表单预览图
     */
    void validateImg(File file);

    /**
     * 校验表单组件文件是否有问题
     *
     * @param directory 表单组件文件
     */
    void validateComponent(File directory);
}
