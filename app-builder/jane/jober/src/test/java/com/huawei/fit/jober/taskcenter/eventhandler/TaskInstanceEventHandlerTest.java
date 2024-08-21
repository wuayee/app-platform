/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.InstanceChangedService;
import com.huawei.fit.jober.taskcenter.domain.InstanceEvent;
import com.huawei.fit.jober.taskcenter.domain.InstanceEventType;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceCreatedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceDeletedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceModifiedEvent;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.InstanceConverter;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.TaskConverter;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * {@link TaskInstanceEventHandler}对应测试类。
 *
 * @author 姚江
 * @since 2023-10-30 10:32
 */
@ExtendWith(MockitoExtension.class)
@Disabled
public class TaskInstanceEventHandlerTest {
    @Mock
    private TaskConverter taskConverter;

    @Mock
    private InstanceConverter instanceConverter;

    @Mock
    private OperationContext context;

    BrokerClient brokerClient;

    Router router;

    Invoker invoker;

    private InstanceEventType eventType;

    @BeforeEach
    void before() {
        when(context.operator()).thenReturn("Mockito.test");
    }

    private BrokerClient mockBrokerClient() {
        brokerClient = Mockito.mock(BrokerClient.class);
        router = Mockito.mock(Router.class);
        when(brokerClient.getRouter(any(), anyString())).thenReturn(router);
        return brokerClient;
    }

    private void mockInvoker(boolean ifThrowException) throws Throwable {

        invoker = Mockito.mock(Invoker.class);
        when(invoker.timeout(eq(500000000L), any())).thenReturn(invoker);

        if (ifThrowException) {
            when(invoker.invoke(any())).thenThrow(new Exception("测试抛出异常情形。"));
        } else {
            when(invoker.invoke(any())).thenReturn("测试成功返回结果。");
        }

        when(router.route(any())).thenReturn(invoker);
    }

    private TaskEntity mockTaskEntity(String idNum) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTypes(Collections.singletonList(mockTaskType(idNum)));
        return taskEntity;
    }

    private TaskType mockTaskType(String idNum) {
        return TaskType.custom()
                .name("测试Type")
                .id("test_task_type_id_" + idNum)
                .children(new LinkedList<>())
                .sources(Collections.singletonList(mockSourceEntity(idNum)))
                .build();
    }

    private SourceEntity mockSourceEntity(String idNum) {
        SourceEntity source = new SourceEntity();
        source.setEvents(Collections.singletonList(InstanceEvent.custom()
                .type(eventType)
                .fitableId("test_fitable_id_" + idNum)
                .build()));
        source.setId("test_source_id_" + idNum);
        return source;
    }

    private TaskInstance mockInstance(String idNum, InstanceEventType instanceEventType) {
        SourceEntity source = new SourceEntity();
        source.setId("test_source_id_" + idNum);
        InstanceEvent instanceEvent =
                InstanceEvent.custom().type(instanceEventType).fitableId("test_fitable_id_003").build();
        source.setEvents(Collections.singletonList(instanceEvent));
        TaskInstance instance = mock(TaskInstance.class);
        when(instance.source()).thenReturn(source);
        return instance;
    }

    @Nested
    @DisplayName("子类Created测试")
    class CreatedTest {
        TaskInstanceEventHandler.Created createdHandler;

        @BeforeEach
        void before() {
            createdHandler = new TaskInstanceEventHandler.Created(taskConverter, instanceConverter, mockBrokerClient());
            eventType = InstanceEventType.CREATED;
        }

        @Test
        @DisplayName("Created不抛出异常的测试")
        void testNoExceptionCreated() throws Throwable {
            // given
            TaskInstanceCreatedEvent event = mockTaskInstanceCreatedEvent("001");
            mockInvoker(false);

            // when
            createdHandler.handleEvent(event);

            // then
            verify(brokerClient).getRouter(eq(InstanceChangedService.class), eq("e1c7adbb69f148c3b81d0067ad02799f"));
            verify(router).route(any());
            verify(invoker).timeout(anyLong(), eq(TimeUnit.MILLISECONDS));
            verify(invoker).invoke(any());
            Assertions.assertEquals("测试成功返回结果。", invoker.invoke(any()));
        }

        @Test
        @DisplayName("Created抛出异常的测试")
        @Disabled
        void testExceptionCreated() throws Throwable {
            // given
            TaskInstanceCreatedEvent event = mockTaskInstanceCreatedEvent("002");
            mockInvoker(true);

            // when
            createdHandler.handleEvent(event);

            // then
            verify(brokerClient).getRouter(eq(InstanceChangedService.class), eq("e1c7adbb69f148c3b81d0067ad02799f"));
            verify(router).route(any());
            verify(invoker).timeout(anyLong(), eq(TimeUnit.MILLISECONDS));
            Exception exception = Assertions.assertThrows(Exception.class, () -> invoker.invoke(any()));
            Assertions.assertEquals("测试抛出异常情形。", exception.getMessage());
        }

        private TaskInstanceCreatedEvent mockTaskInstanceCreatedEvent(String testTimes) {
            TaskInstanceCreatedEvent event = Mockito.mock(TaskInstanceCreatedEvent.class);
            TaskEntity taskEntity = mockTaskEntity(testTimes);
            TaskInstance instanceEntity = mockInstance(testTimes, InstanceEventType.CREATED);

            when(event.task()).thenReturn(taskEntity);
            when(event.instance()).thenReturn(instanceEntity);
            when(event.context()).thenReturn(context);
            return event;
        }
    }

    @Nested
    @DisplayName("子类Modified测试")
    class ModifiedTest {
        TaskInstanceEventHandler.Modified modifiedHandler;

        @BeforeEach
        void before() {
            modifiedHandler =
                    new TaskInstanceEventHandler.Modified(taskConverter, instanceConverter, mockBrokerClient());
            eventType = InstanceEventType.MODIFIED;
        }

        @Test
        @DisplayName("Modified不抛出异常的测试")
        void testNoExceptionModified() throws Throwable {
            // given
            TaskInstanceModifiedEvent event = mockTaskInstanceModifiedEvent("003");
            mockInvoker(false);

            // when
            modifiedHandler.handleEvent(event);

            // then
            verify(brokerClient).getRouter(eq(InstanceChangedService.class), eq("59e2aeaeffad4242bf8c446be11d20e6"));
            verify(router).route(any());
            verify(invoker).timeout(anyLong(), eq(TimeUnit.MILLISECONDS));
            verify(invoker).invoke(any());
            Assertions.assertEquals("测试成功返回结果。", invoker.invoke(any()));
        }

        @Test
        @DisplayName("Modified抛出异常的测试")
        @Disabled
        void testExceptionModified() throws Throwable {
            // given
            TaskInstanceModifiedEvent event = mockTaskInstanceModifiedEvent("004");
            mockInvoker(true);

            // when
            modifiedHandler.handleEvent(event);

            // then
            verify(brokerClient).getRouter(eq(InstanceChangedService.class), eq("59e2aeaeffad4242bf8c446be11d20e6"));
            verify(router).route(any());
            verify(invoker).timeout(anyLong(), eq(TimeUnit.MILLISECONDS));
            Exception exception = Assertions.assertThrows(Exception.class, () -> invoker.invoke(any()));
            Assertions.assertEquals("测试抛出异常情形。", exception.getMessage());
        }

        private TaskInstanceModifiedEvent mockTaskInstanceModifiedEvent(String testTimes) {
            TaskInstanceModifiedEvent event = Mockito.mock(TaskInstanceModifiedEvent.class);
            TaskEntity taskEntity = mockTaskEntity(testTimes);
            TaskInstance instanceEntity = mockInstance(testTimes, InstanceEventType.MODIFIED);

            when(event.task()).thenReturn(taskEntity);
            when(event.instance()).thenReturn(instanceEntity);
            when(event.context()).thenReturn(context);
            return event;
        }
    }

    @Nested
    @DisplayName("子类Deleted测试")
    class DeletedTest {
        TaskInstanceEventHandler.Deleted deletedHandler;

        @BeforeEach
        void before() {
            deletedHandler = new TaskInstanceEventHandler.Deleted(taskConverter, instanceConverter, mockBrokerClient());
            eventType = InstanceEventType.DELETED;
        }

        @Test
        @DisplayName("Deleted不抛出异常的测试")
        void testNoExceptionDeleted() throws Throwable {
            // given
            TaskInstanceDeletedEvent event = mockTaskInstancedeletedEvent("003");
            mockInvoker(false);

            // when
            deletedHandler.handleEvent(event);

            // then
            verify(brokerClient).getRouter(eq(InstanceChangedService.class), eq("47227b3e78924058b428f9a125938d59"));
            verify(router).route(any());
            verify(invoker).timeout(anyLong(), eq(TimeUnit.MILLISECONDS));
            verify(invoker).invoke(any());
            Assertions.assertEquals("测试成功返回结果。", invoker.invoke(any()));
        }

        @Test
        @DisplayName("Deleted抛出异常的测试")
        @Disabled
        void testExceptionDeleted() throws Throwable {
            // given
            TaskInstanceDeletedEvent event = mockTaskInstancedeletedEvent("004");
            mockInvoker(true);

            // when
            deletedHandler.handleEvent(event);

            // then
            verify(brokerClient).getRouter(eq(InstanceChangedService.class), eq("47227b3e78924058b428f9a125938d59"));
            verify(router).route(any());
            verify(invoker).timeout(anyLong(), eq(TimeUnit.MILLISECONDS));
            Exception exception = Assertions.assertThrows(Exception.class, () -> invoker.invoke(any()));
            Assertions.assertEquals("测试抛出异常情形。", exception.getMessage());
        }

        private TaskInstanceDeletedEvent mockTaskInstancedeletedEvent(String testTimes) {
            TaskInstanceDeletedEvent event = Mockito.mock(TaskInstanceDeletedEvent.class);
            TaskEntity taskEntity = mockTaskEntity(testTimes);
            TaskInstance instanceEntity = mockInstance(testTimes, InstanceEventType.DELETED);

            when(event.task()).thenReturn(taskEntity);
            when(event.instance()).thenReturn(instanceEntity);
            when(event.context()).thenReturn(context);
            return event;
        }
    }
}
