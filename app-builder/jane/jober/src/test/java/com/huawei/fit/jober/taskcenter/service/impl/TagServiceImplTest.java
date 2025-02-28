/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.impl;

import modelengine.fit.jober.DatabaseBaseTest;
import modelengine.fit.jober.taskcenter.service.TagService;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.validation.TagValidator;
import modelengine.fit.jober.taskcenter.validation.impl.TagValidatorImpl;

import modelengine.fitframework.transaction.TransactionManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link TagServiceImpl} 对应测试类
 *
 * @author 员文豪
 * @since 2023-11-09
 */
public class TagServiceImplTest extends DatabaseBaseTest {
    private TagService tagService;

    @Mock
    private DynamicSqlExecutor dynamicSqlExecutor;

    private final TagValidator tagValidator = new TagValidatorImpl(64, 512, 16);

    private static final String SQL_PATH = "handler/flowDefinition/saveGraphAndTagData.sql";

    @Mock
    private TransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        tagService = new TagServiceImpl(dynamicSqlExecutor, tagValidator, transactionManager);
    }

    @Test
    @DisplayName("测试根据标签查询流程定义ID")
    public void findFlowsIdWithTagSuccess() {
        executeSqlInFile(SQL_PATH);
        List<String> tags = new ArrayList<>();
        tags.add("aTag");
        tags.add("twoTag");
        List<String> ids = tagService.list("FLOW GRAPH", tags);
        Assertions.assertEquals(0, ids.size());
    }
}
