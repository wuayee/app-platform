/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package modelengine.fitframework.schedule;

/**
 * 表示执行过程中发生的异常。
 *
 * @author 季聿阶
 * @since 2022-12-26
 */
public class ExecuteException extends RuntimeException {
    public ExecuteException(Throwable cause) {
        super(cause);
    }
}
