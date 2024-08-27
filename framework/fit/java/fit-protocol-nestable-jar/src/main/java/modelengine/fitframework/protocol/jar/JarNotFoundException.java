/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.protocol.jar;

import java.io.FileNotFoundException;

/**
 * 当 JAR 不存在时引发的异常。
 *
 * @author 梁济时
 * @since 2023-02-17
 */
public class JarNotFoundException extends FileNotFoundException {
    /**
     * 使用异常信息初始化 {@link JarNotFoundException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public JarNotFoundException(String message) {
        super(message);
    }
}
