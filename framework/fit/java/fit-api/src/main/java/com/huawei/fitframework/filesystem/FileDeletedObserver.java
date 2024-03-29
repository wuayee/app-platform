/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * 表示文件删除的观察者。
 *
 * @author 季聿阶 j00559309
 * @since 2023-07-26
 */
@FunctionalInterface
public interface FileDeletedObserver {
    /**
     * 获取空的观察者。
     *
     * @return 表示空的观察者的 {@link FileDeletedObserver}。
     */
    static FileDeletedObserver empty() {
        return file -> {};
    }

    /**
     * 表示当文件删除时，触发的事件。
     *
     * @param file 表示删除的文件的 {@link File}。
     * @throws IOException 当处理 {@code file} 删除事件过程中发生 IO 异常时。
     */
    void onFileDeleted(File file) throws IOException;
}
