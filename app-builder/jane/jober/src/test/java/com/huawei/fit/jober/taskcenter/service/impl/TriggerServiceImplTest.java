/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.dao.TriggerMapper;
import com.huawei.fit.jober.taskcenter.dao.po.TriggerObject;
import com.huawei.fit.jober.taskcenter.declaration.SourceTriggersDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TriggerDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TriggerEntity;
import com.huawei.fit.jober.taskcenter.filter.TriggerFilter;
import com.huawei.fit.jober.taskcenter.validation.TriggerValidator;
import com.huawei.fit.jober.taskcenter.validation.impl.TriggerValidatorImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class TriggerServiceImplTest {
    @Mock
    private TriggerMapper mockTriggerMapper;

    private final TriggerValidator triggerValidator = new TriggerValidatorImpl(64);

    private TriggerServiceImpl triggerServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        triggerServiceImplUnderTest = new TriggerServiceImpl(mockTriggerMapper, triggerValidator);
    }

    @Test
    void testCreate() {
        // Setup
        MockedStatic create = Mockito.mockStatic(Entities.class);
        final TriggerDeclaration declaration = new TriggerDeclaration();
        declaration.setPropertyName(UndefinableValue.defined("propertyName"));
        declaration.setFitableId(UndefinableValue.defined("fitableId"));

        final OperationContext context = OperationContext.empty();
        final TriggerEntity expectedResult = new TriggerEntity();
        expectedResult.setId("id");
        expectedResult.setPropertyId("taskPropertyId");
        expectedResult.setFitableId("fitableId");

        // Configure TriggerMapper.selectTriggerByTaskId(...).
        final TriggerObject triggerObject = new TriggerObject("id", "taskSourceId", "taskPropertyId", "fitableId");
        when(mockTriggerMapper.selectTriggerByTaskId("a3a7df7e45f44451970a6a8e138382c2", "sourceId",
                "propertyName")).thenReturn(triggerObject);
        create.when(() -> Entities.generateId()).thenReturn("id");
        create.when(() -> Entities.validateId(eq("a3a7df7e45f44451970a6a8e138382c2"), any()))
                .thenReturn("a3a7df7e45f44451970a6a8e138382c2");

        // Run the test
        final TriggerEntity result = triggerServiceImplUnderTest.create("a3a7df7e45f44451970a6a8e138382c2", "sourceId",
                declaration, context);

        // Verify the results
        assertEquals(expectedResult, result);
        verify(mockTriggerMapper).create(new TriggerObject("id", "taskSourceId", "taskPropertyId", "fitableId"));
        create.close();
    }

    @Test
    void testPatch() {
        // Setup
        final TriggerDeclaration declaration = new TriggerDeclaration();
        declaration.setPropertyName(UndefinableValue.defined("value"));
        declaration.setFitableId(UndefinableValue.defined("value"));

        final OperationContext context = OperationContext.empty();

        // Run the test
        triggerServiceImplUnderTest.patch("taskId", "sourceId", "triggerId", declaration, context);

        // Verify the results
    }

    @Test
    void testDelete() {
        // Setup
        final TriggerFilter filter = new TriggerFilter();
        filter.setIds(UndefinableValue.defined(Arrays.asList("id")));
        filter.setSourceIds(UndefinableValue.defined(Arrays.asList("taskSourceId")));
        filter.setPropertyIds(UndefinableValue.defined(Arrays.asList("taskPropertyId")));
        filter.setFitableIds(UndefinableValue.defined(Arrays.asList("fitableId")));

        final OperationContext context = OperationContext.empty();

        // Run the test
        triggerServiceImplUnderTest.delete("taskId", filter, context);

        // Verify the results
        verify(mockTriggerMapper).delete(Arrays.asList("id"), Arrays.asList("taskSourceId"),
                Arrays.asList("taskPropertyId"), Arrays.asList("fitableId"));
    }

    @Test
    void testRetrieve() {
        // Setup
        final OperationContext context = OperationContext.empty();
        final TriggerEntity expectedResult = new TriggerEntity();
        expectedResult.setId("a3a7df7e45f44451970a6a8e138382c2");
        expectedResult.setPropertyId("taskPropertyId");
        expectedResult.setFitableId("fitableId");
        when(mockTriggerMapper.retrieve("a3a7df7e45f44451970a6a8e138382c2")).thenReturn(expectedResult);

        // Run the test
        final TriggerEntity result = triggerServiceImplUnderTest.retrieve("taskId", "sourceId",
                "a3a7df7e45f44451970a6a8e138382c2", context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testList() {
        // Setup
        final TriggerFilter filter = new TriggerFilter();
        filter.setIds(UndefinableValue.defined(Arrays.asList("id")));
        filter.setSourceIds(UndefinableValue.defined(Arrays.asList("taskSourceId")));
        filter.setPropertyIds(UndefinableValue.defined(Arrays.asList("taskPropertyId")));
        filter.setFitableIds(UndefinableValue.defined(Arrays.asList("fitableId")));

        final OperationContext context = OperationContext.empty();
        final Map<String, List<TriggerEntity>> expectedResult = new HashMap<>();
        final TriggerEntity triggerEntity = new TriggerEntity();
        triggerEntity.setId("id");
        triggerEntity.setPropertyId("taskPropertyId");
        triggerEntity.setFitableId("fitableId");

        // Configure TriggerMapper.list(...).
        final List<TriggerObject> triggerObjects = Arrays.asList(
                new TriggerObject("id", "taskSourceId", "taskPropertyId", "fitableId"));
        expectedResult.put("taskSourceId", Arrays.asList(triggerEntity));
        when(mockTriggerMapper.list(Arrays.asList("id"), Arrays.asList("taskSourceId"), Arrays.asList("taskPropertyId"),
                Arrays.asList("fitableId"))).thenReturn(triggerObjects);

        // Run the test
        final Map<String, List<TriggerEntity>> result = triggerServiceImplUnderTest.list(filter, context);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    void testBatchSave() {
        // Setup
        MockedStatic batchSave = Mockito.mockStatic(Entities.class);
        final String taskId = "a3a7df7e45f44451970a6a8e138382c2";
        final String taskPropertyId = "taskPropertyId";
        final SourceTriggersDeclaration sourceTriggersDeclaration = new SourceTriggersDeclaration();
        sourceTriggersDeclaration.setSourceId("taskSourceId");
        final TriggerDeclaration triggerDeclaration = new TriggerDeclaration();
        final List<TriggerObject> triggerObjects = Arrays.asList(
                new TriggerObject("id", "taskSourceId", "taskPropertyId", "fitableId"));
        triggerDeclaration.setPropertyName(UndefinableValue.defined("propertyName"));
        triggerDeclaration.setFitableId(UndefinableValue.defined("fitableId"));
        sourceTriggersDeclaration.setTriggers(Arrays.asList(triggerDeclaration));
        final List<SourceTriggersDeclaration> declarations = Arrays.asList(sourceTriggersDeclaration);
        final OperationContext context = OperationContext.empty();
        batchSave.when(() -> Entities.generateId()).thenReturn("id");
        batchSave.when(() -> Entities.validateId(eq("a3a7df7e45f44451970a6a8e138382c2"), any()))
                .thenReturn("a3a7df7e45f44451970a6a8e138382c2");
        when(mockTriggerMapper.selectTaskPropertyIdByTaskIdAndName(taskId, "propertyName")).thenReturn(taskPropertyId);
        // Run the test
        triggerServiceImplUnderTest.batchSave(taskId, declarations, context);

        // Verify the results
        verify(mockTriggerMapper).batchSave(triggerObjects);
        batchSave.close();
    }
}
