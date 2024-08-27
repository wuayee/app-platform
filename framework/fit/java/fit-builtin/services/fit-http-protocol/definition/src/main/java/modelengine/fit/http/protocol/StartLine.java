/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fit.http.protocol;

/**
 * 表示 Http 报文的起始行。
 *
 * @author 季聿阶
 * @since 2022-07-11
 */
public interface StartLine {
    /**
     * 获取 Http 协议的版本。
     *
     * @return 表示 Http 协议版本的 {@link HttpVersion}。
     */
    HttpVersion httpVersion();
}
