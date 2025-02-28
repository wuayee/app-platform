/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.eventhandler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fit.jober.taskcenter.event.TaskInstanceModifyingEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TaskInstanceDeclaringEventHandlerTest {
    @Nested
    @DisplayName("测试修改事件")
    class ModifiedTest {
        private TaskInstanceDeclaringEventHandler.ModifyingInfoHandler handler;

        @BeforeEach
        void setup() {
            this.handler = new TaskInstanceDeclaringEventHandler.ModifyingInfoHandler();
        }

        @Test
        @DisplayName("任务实例的 info 未改变时不会抛出异常")
        void should_not_throw_when_info_not_supplied() {
            TaskEntity task = mock(TaskEntity.class);
            OperationContext context = mock(OperationContext.class);
            TaskInstance instance = mock(TaskInstance.class);
            TaskInstanceModifyingEvent event = new TaskInstanceModifyingEvent(null, task, context, instance);
            event.declaration(TaskInstance.Declaration.custom().build());
            assertDoesNotThrow(() -> this.handler.handleEvent(event));
        }
    }
}