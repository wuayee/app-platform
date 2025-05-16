/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util;

import modelengine.fit.jober.taskcenter.domain.util.support.DefaultAsynchronousRunner;

/**
 * 为任务实例的相关通知提供异步执行器。
 *
 * @author 梁济时
 * @since 2023-09-18
 */
public interface AsynchronousRunner {
    /**
     * 提交待异步执行的逻辑。
     *
     * @param actions 表示待异步执行的逻辑的 {@link Runnable}{@code []}。
     */
    void run(Runnable... actions);

    /**
     * 返回异步执行器的唯一实例。
     *
     * @return 表示异步执行器的唯一实例的 {@link AsynchronousRunner}。
     */
    static AsynchronousRunner instance() {
        return DefaultAsynchronousRunner.INSTANCE;
    }
}

