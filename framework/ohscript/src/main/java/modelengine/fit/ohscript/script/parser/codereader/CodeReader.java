/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.codereader;

/**
 * 代码源
 *
 * @since 1.0
 */
public interface CodeReader {
    /**
     * 读取一行代码
     *
     * @return 返回读取的一行代码
     */
    String readLine();

    /**
     * 关闭代码源
     */
    void close();
}
