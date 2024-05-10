package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.service.SourceService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.validation.RelationshipValidator;
import com.huawei.fit.jober.taskcenter.validation.TaskTypeValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class PostgresqlTaskTypeRepoTest {
    @Mock
    private DynamicSqlExecutor mockExecutor;

    @Mock
    private TaskTypeValidator mockValidator;

    @Mock
    private SourceService mockSourceService;

    @Mock
    private RelationshipValidator mockRelationshipValidator;

    private PostgresqlTaskTypeRepo postgresqlTaskTypeRepoUnderTest;

    @BeforeEach
    void setUp() {
        postgresqlTaskTypeRepoUnderTest = new PostgresqlTaskTypeRepo(mockExecutor, mockValidator, mockSourceService,
                mockRelationshipValidator);
    }

    @Test
    void testPatch() {
        // Setup
        TaskType.Declaration declaration = TaskType.Declaration.custom()
                .name("name")
                .sourceIds(Collections.singletonList("11111"))
                .build();
        final OperationContext context = OperationContext.empty();
        when(mockValidator.name("name")).thenReturn("result");
        when(mockExecutor.executeUpdate(any(), any())).thenReturn(2);
        // Run the test
        postgresqlTaskTypeRepoUnderTest.patch("a472852614e24002bfe2ca6de0fda610", "a472852614e24002bfe2ca6de0fda610",
                declaration, context);
        // Verify the results
        verify(mockRelationshipValidator).validateTaskExistInTenant("a472852614e24002bfe2ca6de0fda610",
                "00000000000000000000000000000000");
    }
}
