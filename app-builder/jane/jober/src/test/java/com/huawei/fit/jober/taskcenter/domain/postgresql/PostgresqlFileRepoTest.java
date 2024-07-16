/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.FileService;
import com.huawei.fit.jober.entity.File;
import com.huawei.fit.jober.entity.FileDeclaration;
import com.huawei.fit.jober.taskcenter.fitable.util.ParamUtils;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link PostgresqlFileRepo}对应测试类。
 *
 * @author lwx1301876
 * @since 2024-01-09
 */
@ExtendWith(MockitoExtension.class)
public class PostgresqlFileRepoTest {
    private static final String fileId = "6b11955739924ad5a27af3191b1a4dac";

    @Mock
    private DynamicSqlExecutor executor;

    @Mock
    private FileService fileService;

    private PostgresqlFileRepo repo;

    OperationContext context = OperationContext.custom().operator("zhangsan").build();

    File file = new File();

    @BeforeEach
    void before() {
        repo = new PostgresqlFileRepo(executor, fileService);
    }

    @Test
    @DisplayName("上传文件")
    void uploadTest() {
        file.setName("fileName");
        FileDeclaration declaration = new FileDeclaration();
        declaration.setContent(new byte[0]);
        doReturn(file).when(fileService).upload(anyString(), any(), any());
        InsertSql sql = InsertSql.custom().into("file");
        sql.value("type", "S3");
        com.huawei.fit.jane.task.domain.File uploadedFile = repo.upload(ParamUtils.convertDeclaration(declaration), context);
        Assertions.assertNotNull(uploadedFile);
    }

    @Test
    @DisplayName("下载文件")
    void downloadTest() {
        Map<String, Object> row = new HashMap<>();
        row.put("name", "fileName");
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row);
        when(executor.executeQuery(anyString(), anyList())).thenReturn(rows);
        when(fileService.download(anyString(), any())).thenReturn(file);
        com.huawei.fit.jane.task.domain.File downloadedFile = repo.download(fileId, context);
        Assertions.assertNotNull(downloadedFile);
    }

    @Test
    @DisplayName("查询文件信息")
    void fileInfoTest() {
        Map<String, Object> row = new HashMap<>();
        row.put("name", "fileName");
        row.put("id", "12345678123456781234567812345678");
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row);
        when(executor.executeQuery(anyString(), anyList())).thenReturn(rows);
        Map<String, String> map = repo.fileInfo(Collections.singletonList("12345678123456781234567812345678"), context);
        Assertions.assertNotNull(map);
    }
}
