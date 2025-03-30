/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * 测试日志记录，方便自动化统计概率失败的用例
 *
 * @author 杨祥宇
 * @since 2024/9/27
 */
public class MethodNameLoggerExtension implements BeforeTestExecutionCallback {
    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        String methodName = extensionContext.getRequiredTestMethod().getName();
        System.out.println("***block test name***: " + methodName);
    }
}
