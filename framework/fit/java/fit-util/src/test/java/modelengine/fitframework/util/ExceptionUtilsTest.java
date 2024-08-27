/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fitframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import modelengine.fitframework.exception.MethodInvocationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 为 {@link ExceptionUtils} 提供单元测试。
 *
 * @author 季聿阶
 * @since 2021-05-18
 */
public class ExceptionUtilsTest {
    @Nested
    @DisplayName("test getReason(Throwable throwable)")
    class WhenGetReason {
        @Test
        @DisplayName("when throwable is null, then reason is 'No exception'")
        void givenNoThrowableThenGetDefaultReason() {
            String reason = ExceptionUtils.getReason(null);
            assertThat(reason).isEqualTo("No exception");
        }

        @Test
        @DisplayName("when throwable is a common exception, then reason is its class name and message")
        void givenOneThrowableThenGetItsClassNameAndMessage() {
            Throwable throwable = new Exception("this is a exception");
            String reason = ExceptionUtils.getReason(throwable);
            assertThat(reason).isEqualTo("Exception: this is a exception");
        }
    }

    @Nested
    @DisplayName("test getActualCause(MethodInvocationException exception)")
    class WhenGetActualCause {
        @Test
        @DisplayName("when throwable is null, then actual cause is null")
        void givenNoThrowableThenNoCause() {
            Throwable cause = ExceptionUtils.getActualCause(null);
            assertThat(cause).isNull();
        }

        @SuppressWarnings("ThrowableNotThrown")
        @Test
        @DisplayName("when throwable's cause's cause is itself, then throw exception")
        void givenCyclicThrowableThenThrowException() {
            MethodInvocationException e1 = new MethodInvocationException("e1");
            MethodInvocationException e2 = new MethodInvocationException("e2");
            e1.initCause(e2);
            e2.initCause(e1);
            IllegalStateException exception = catchThrowableOfType(() -> ExceptionUtils.getActualCause(e1),
                    IllegalStateException.class);
            assertThat(exception).isNotNull().hasMessage("Cyclic throwable cause.");
        }

        @SuppressWarnings("ThrowableNotThrown")
        @Test
        @DisplayName("when too many MethodInvocationException, then throw exception")
        void givenTooManyMethodInvocationExceptionThenThrowException() {
            MethodInvocationException e1 = new MethodInvocationException("e1");
            MethodInvocationException e2 = new MethodInvocationException(e1);
            MethodInvocationException e3 = new MethodInvocationException(e2);
            MethodInvocationException e4 = new MethodInvocationException(e3);
            MethodInvocationException e5 = new MethodInvocationException(e4);
            MethodInvocationException e6 = new MethodInvocationException(e5);
            MethodInvocationException e7 = new MethodInvocationException(e6);
            MethodInvocationException e8 = new MethodInvocationException(e7);
            MethodInvocationException e9 = new MethodInvocationException(e8);
            MethodInvocationException e10 = new MethodInvocationException(e9);
            IllegalStateException exception = catchThrowableOfType(() -> ExceptionUtils.getActualCause(e10),
                    IllegalStateException.class);
            assertThat(exception).isNotNull().hasMessage("Too many duplicated throwable.");
        }

        @Test
        @DisplayName("when MethodInvocationException has another type cause, then get it")
        void givenExceptionHasAnotherTypeCauseThenGetItsCause() {
            Exception cause = new Exception("error");
            MethodInvocationException e1 = new MethodInvocationException(cause);
            Throwable actualCause = ExceptionUtils.getActualCause(e1);
            assertThat(actualCause).isNotNull().hasMessage("error");
        }

        @Test
        @DisplayName("when MethodInvocationException's cause has another type cause, then get its cause's cause")
        void givenExceptionCauseHasAnotherTypeCauseThenGetItsDescendantsCause() {
            Exception cause = new Exception("error");
            MethodInvocationException e1 = new MethodInvocationException(cause);
            MethodInvocationException e2 = new MethodInvocationException(e1);
            Throwable actualCause = ExceptionUtils.getActualCause(e2);
            assertThat(actualCause).isNotNull().hasMessage("error");
        }
    }
}
