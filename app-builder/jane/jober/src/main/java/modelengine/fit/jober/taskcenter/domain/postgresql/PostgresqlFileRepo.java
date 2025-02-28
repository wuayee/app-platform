/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.postgresql;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import lombok.RequiredArgsConstructor;
import modelengine.fit.jane.task.domain.File;
import modelengine.fit.jane.task.util.Dates;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.FileService;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.common.util.ParamUtils;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.util.sql.InsertSql;
import modelengine.fit.jober.taskcenter.util.sql.SqlBuilder;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 为 {@link File.Repo} 提供基于 Postgresql 的实现。
 *
 * @author 陈镕希
 * @since 2023-10-10
 */
@Component
@RequiredArgsConstructor
public class PostgresqlFileRepo implements File.Repo {
    private static final String TABLE_NAME = "file";

    private static final Logger log = Logger.get(PostgresqlFileRepo.class);

    private final DynamicSqlExecutor executor;

    private final FileService fileService;

    private static LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        return Dates.fromUtc((ObjectUtils.<Timestamp>cast(value)).toLocalDateTime());
    }

    private static String toString(Object value) {
        return ObjectUtils.cast(value);
    }

    private static byte[] toBytes(Object value) {
        return (byte[]) value;
    }

    @Override
    @Transactional
    public File upload(File.Declaration declaration, OperationContext context) {
        notNull(declaration, "The declaration of file to upload cannot be null.");
        notNull(context, "The operation context to upload file cannot be null.");

        log.debug("UploadFile. [declaration={}, operator={}]", declaration, context.operator());

        String operator = context.operator();
        LocalDateTime operationTime = Dates.toUtc(LocalDateTime.now());

        File file = File.custom()
                .id(Entities.generateId())
                .name(declaration.name().required(() -> new BadRequestException(ErrorCodes.FILE_NAME_REQUIRED)))
                .content(
                        declaration.content().required(() -> new BadRequestException(ErrorCodes.FILE_CONTENT_REQUIRED)))
                .creator(operator)
                .creationTime(operationTime)
                .lastModifier(operator)
                .lastModificationTime(operationTime)
                .build();

        fileService.upload(file.id(), ParamUtils.convertDeclaration(declaration),
                ParamUtils.convertOperationContext(context));

        InsertSql sql = InsertSql.custom().into(TABLE_NAME);
        sql.value("id", file.id());
        sql.value("name", file.name());
        sql.value("created_by", file.creator());
        sql.value("created_at", file.creationTime());
        sql.value("type", "S3");
        sql.conflict("id");
        sql.execute(this.executor);
        return file;
    }

    @Override
    @Transactional
    public File download(String fileId, OperationContext context) {
        notBlank(fileId, "The file id to download cannot be null.");
        notNull(context, "The operation context to upload file cannot be null.");

        log.debug("DownloadFile. [fileId={}, operator={}]", fileId, context.operator());

        String actualFileId = Entities.validateId(fileId, () -> new BadRequestException(ErrorCodes.FILE_ID_INVALID));

        SqlBuilder sql = SqlBuilder.custom();
        buildSelectFromWhere(sql).appendIdentifier("id").append(" = ?");
        List<Object> args = Collections.singletonList(actualFileId);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        String fileName = rows.get(0).get("name").toString();
        String objectKey = fileId + "/" + fileName;

        return ParamUtils.convertFile(fileService.download(objectKey, ParamUtils.convertOperationContext(context)));
    }

    @Override
    public Map<String, String> fileInfo(List<String> fileIds, OperationContext context) {
        if (CollectionUtils.isEmpty(fileIds)) {
            return new HashMap<>();
        }
        List<String> actualFileIds = fileIds.stream()
                .map(fileId -> Entities.validateId(fileId, () -> new BadRequestException(ErrorCodes.FILE_ID_INVALID)))
                .collect(Collectors.toList());
        SqlBuilder sql = SqlBuilder.custom();
        buildSelectFromWhere(sql).appendIdentifier("id")
                .append(" IN (?")
                .appendRepeatedly(", ?", actualFileIds.size() - 1)
                .append(")");
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), actualFileIds);
        return rows.stream()
                .collect(Collectors.toMap(row -> row.get("id").toString(), row -> row.get("name").toString()));
    }

    private SqlBuilder buildSelectFromWhere(SqlBuilder sql) {
        return sql.append("SELECT ")
                .appendIdentifier("id")
                .append(", ")
                .appendIdentifier("name")
                .append(" FROM ")
                .appendIdentifier(TABLE_NAME)
                .append(" WHERE ");
    }

    private static class FileRow {
        private final Map<String, Object> row;

        FileRow(Map<String, Object> row) {
            this.row = row;
        }

        String id() {
            return PostgresqlFileRepo.toString(this.row.get("id"));
        }

        String name() {
            return PostgresqlFileRepo.toString(this.row.get("name"));
        }

        byte[] content() {
            return PostgresqlFileRepo.toBytes(this.row.get("content"));
        }

        String creator() {
            return PostgresqlFileRepo.toString(this.row.get("created_by"));
        }

        LocalDateTime creationTime() {
            return PostgresqlFileRepo.toLocalDateTime(this.row.get("created_at"));
        }

        File toFile() {
            return File.custom()
                    .id(this.id())
                    .name(this.name())
                    .content(this.content())
                    .creator(this.creator())
                    .creationTime(Dates.fromUtc(this.creationTime()))
                    .build();
        }
    }
}
