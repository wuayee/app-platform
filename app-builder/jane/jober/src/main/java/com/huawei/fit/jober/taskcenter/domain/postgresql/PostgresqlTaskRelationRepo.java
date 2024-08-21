/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static com.huawei.fit.jober.common.ErrorCodes.FILTER_IS_EMPTY;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.TaskRelation;
import com.huawei.fit.jane.task.util.Dates;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.aop.ObjectTypeEnum;
import com.huawei.fit.jober.common.aop.OperateEnum;
import com.huawei.fit.jober.common.aop.OperationRecord;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.util.Sqls;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.DeleteSql;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fit.jober.taskcenter.validation.TaskRelationValidator;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link TaskRelation.Repo} 提供基于 {@code Postgresql} 数据库的实现。
 *
 * @author 罗书强
 * @since 2024-01-02
 */
@Component
public class PostgresqlTaskRelationRepo implements TaskRelation.Repo {
    private static final Logger log = Logger.get(PostgresqlTaskRelationRepo.class);

    private final DynamicSqlExecutor executor;

    private final TaskRelationValidator validator;

    public PostgresqlTaskRelationRepo(DynamicSqlExecutor executor, TaskRelationValidator validator) {
        this.executor = executor;
        this.validator = validator;
    }

    @Override
    @Transactional
    @OperationRecord(objectId = -1, objectIdGetMethodName = "objectId1", objectType = ObjectTypeEnum.INSTANCE,
            operate = OperateEnum.RELADD, declaration = 0)
    public TaskRelation create(TaskRelation.Declaration declaration, OperationContext context) {
        Row row = new Row();
        row.id(this.validator.id(UndefinableValue.require(declaration.id(),
                () -> new BadRequestException(ErrorCodes.TASK_RELATION_RELATION_ID_INVALID))));
        row.objectId1(this.validator.objectId1(UndefinableValue.require(declaration.objectId1(),
                () -> new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_ID1_REQUIRED))));
        row.objectType1(this.validator.objectType1(UndefinableValue.require(declaration.objectType1(),
                () -> new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_TYPE1_REQUIRED))));
        row.objectId2(this.validator.objectId2(UndefinableValue.require(declaration.objectId2(),
                () -> new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_ID2_REQUIRED))));
        row.objectType2(this.validator.objectType2(UndefinableValue.require(declaration.objectType2(),
                () -> new BadRequestException(ErrorCodes.TASK_RELATION_OBJECT_TYPE2_REQUIRED))));
        row.relationType(Enums.toString(this.validator.relationType(UndefinableValue.require(declaration.relationType(),
                () -> new BadRequestException(ErrorCodes.TASK_RELATION_RELATION_TYPE_REQUIRED)))));
        row.creator(context.operator());
        row.creationTime(LocalDateTime.now());
        // 判断两个任务是否关联
        row.relationISExist(this.executor, row);
        row.insert(this.executor);
        return convert(row, Function.identity());
    }

    @Override
    @Transactional
    @OperationRecord(objectId = -2, objectType = ObjectTypeEnum.INSTANCE, operate = OperateEnum.RELDEL)
    public void delete(String relationId, OperationContext context) {
        String actualId = this.validator.id(relationId);
        DeleteSql sql = DeleteSql.custom()
                .from(PostgresqlTaskRelationRepo.Row.TABLE)
                .where(Condition.expectEqual(PostgresqlTaskRelationRepo.Row.COLUMN_ID, actualId));
        int affectedRows = sql.execute(this.executor);
        if (affectedRows < 1) {
            log.error("The task relation to delete does not exist. [id={}]", actualId);
            throw new NotFoundException(ErrorCodes.TASK_RELATION_RELATION_NOT_FOUND);
        }
    }

    @Override
    public TaskRelation retrieve(String relationId, OperationContext context) {
        String actualId = this.validator.id(relationId);
        Row row = Row.select(this.executor, actualId);
        if (row == null) {
            log.error("The task relation to retrieve does not exist. [id={}]", actualId);
            throw new NotFoundException(ErrorCodes.TASK_RELATION_RELATION_NOT_FOUND);
        }
        return convert(row, Dates::fromUtc);
    }

    @Override
    public RangedResultSet<TaskRelation> list(TaskRelation.Filter filter, long offset, int limit,
            OperationContext context) {
        List<String> ids = values(filter.ids());
        List<String> objectId1s = values(filter.objectId1s());
        List<String> objectId2s = values(filter.objectId2s());
        if (ids.isEmpty() && objectId1s.isEmpty() && objectId2s.isEmpty()) {
            throw new BadRequestException(FILTER_IS_EMPTY);
        }
        StringBuilder where = new StringBuilder();
        List<Object> args = new LinkedList<>();
        if (!ids.isEmpty()) {
            Sqls.andIn(where, Sqls.identifier(Row.COLUMN_ID), ids.size());
            args.addAll(ids);
        }
        if (!objectId1s.isEmpty()) {
            Sqls.andLikeAny(where, Sqls.identifier(Row.COLUMN_OBJECT_ID1), objectId1s.size());
            args.addAll(objectId1s.stream().map(Sqls::escapeLikeValue).collect(Collectors.toList()));
        }
        if (!objectId2s.isEmpty()) {
            Sqls.andLikeAny(where, Sqls.identifier(Row.COLUMN_OBJECT_ID2), objectId2s.size());
            args.addAll(objectId2s.stream().map(Sqls::escapeLikeValue).collect(Collectors.toList()));
        }
        SqlBuilder countSql = SqlBuilder.custom().append("SELECT COUNT(1) FROM ").appendIdentifier(Row.TABLE);
        appendWhereTask(countSql, where);
        SqlBuilder sql = Row.fillSelectPrefixTask(SqlBuilder.custom());
        appendWhereTask(sql, where);
        sql.append(" OFFSET ? LIMIT ? ");
        List<Object> whereArgsList = new ArrayList<>();
        whereArgsList.addAll(args);
        whereArgsList.addAll(Arrays.asList(offset, limit));
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), whereArgsList);
        if (rows.isEmpty()) {
            log.error("The task relation does not exist.");
            throw new NotFoundException(ErrorCodes.TASK_RELATION_RELATION_NOT_FOUND);
        }
        List<TaskRelation> taskRelations = rows.stream()
                .map(values -> convert(new Row(values), Dates::fromUtc))
                .collect(Collectors.toList());
        long total = (ObjectUtils.<Number>cast(this.executor.executeScalar(countSql.toString(), args))).longValue();
        return RangedResultSet.create(taskRelations, (int) offset, limit, (int) total);
    }

    private static List<String> values(List<String> values) {
        return Optional.ofNullable(values)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

    private static void appendWhereTask(SqlBuilder sql, StringBuilder whereTask) {
        if (whereTask.length() > 0) {
            sql.append(" WHERE ").append(whereTask.substring(5));
        }
    }

    private static final class Row {
        private static final String TABLE = "jane_relation";

        private static final String COLUMN_ID = "id";

        private static final String COLUMN_OBJECT_ID1 = "object_id1";

        private static final String COLUMN_OBJECT_TYPE1 = "object_type1";

        private static final String COLUMN_OBJECT_ID2 = "object_id2";

        private static final String COLUMN_OBJECT_TYPE2 = "object_type2";

        private static final String COLUMN_RELATION_TYPE = "relation_type";

        private static final String COLUMN_CREATION_BY = "created_by";

        private static final String COLUMN_CREATION_TIME = "created_at";

        private final Map<String, Object> values;

        Row() {
            this(null);
        }

        Row(Map<String, Object> values) {
            this.values = nullIf(values, new HashMap<>());
        }

        String id() {
            return cast(this.values.get(COLUMN_ID));
        }

        void id(String id) {
            this.values.put(COLUMN_ID, id);
        }

        String objectId1() {
            return cast(this.values.get(COLUMN_OBJECT_ID1));
        }

        void objectId1(String objectId1) {
            this.values.put(COLUMN_OBJECT_ID1, objectId1);
        }

        String objectType1() {
            return cast(this.values.get(COLUMN_OBJECT_TYPE1));
        }

        void objectType1(String objectType1) {
            this.values.put(COLUMN_OBJECT_TYPE1, objectType1);
        }

        String objectId2() {
            return cast(this.values.get(COLUMN_OBJECT_ID2));
        }

        void objectId2(String objectId2) {
            this.values.put(COLUMN_OBJECT_ID2, objectId2);
        }

        String objectType2() {
            return cast(this.values.get(COLUMN_OBJECT_TYPE2));
        }

        void objectType2(String objectType2) {
            this.values.put(COLUMN_OBJECT_TYPE2, objectType2);
        }

        String relationType() {
            return cast(this.values.get(COLUMN_RELATION_TYPE));
        }

        void relationType(String relationType) {
            this.values.put(COLUMN_RELATION_TYPE, relationType);
        }

        String creator() {
            return cast(this.values.get(COLUMN_CREATION_BY));
        }

        void creator(String creator) {
            this.values.put(COLUMN_CREATION_BY, creator);
        }

        LocalDateTime creationTime() {
            Object value = this.values.get(COLUMN_CREATION_TIME);
            if (value instanceof Timestamp) {
                value = ((Timestamp) value).toLocalDateTime();
                this.values.put(COLUMN_CREATION_TIME, value);
            }
            return cast(value);
        }

        void creationTime(LocalDateTime creationTime) {
            this.values.put(COLUMN_CREATION_TIME, creationTime);
        }

        void insert(DynamicSqlExecutor executor) {
            int affectedRows = InsertSql.custom()
                    .into(TABLE)
                    .value(COLUMN_ID, this.id())
                    .value(COLUMN_OBJECT_ID1, this.objectId1())
                    .value(COLUMN_OBJECT_TYPE1, this.objectType1())
                    .value(COLUMN_OBJECT_ID2, this.objectId2())
                    .value(COLUMN_OBJECT_TYPE2, this.objectType2())
                    .value(COLUMN_RELATION_TYPE, this.relationType())
                    .value(COLUMN_CREATION_BY, this.creator())
                    .value(COLUMN_CREATION_TIME, Dates.toUtc(this.creationTime()))
                    .execute(executor);
            if (affectedRows < 1) {
                throw new ServerInternalException("Failed to insert task relation into database.");
            }
        }

        void relationISExist(DynamicSqlExecutor executor, Row row) {
            SqlBuilder sql = fillSelectPrefixTask(SqlBuilder.custom());
            sql.append(" WHERE ").appendIdentifier(COLUMN_OBJECT_ID1).append(" = ?");
            sql.append(" AND ").appendIdentifier(COLUMN_OBJECT_ID2).append(" = ?");
            List<Object> args = new ArrayList<>();
            args.add(row.objectId1());
            args.add(row.objectId2());
            List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
            if (!rows.isEmpty()) {
                log.error("Two task relations already exist.");
                throw new BadRequestException(ErrorCodes.TASK_RELATION_EXIST_RELATION);
            }
        }

        private static SqlBuilder fillSelectPrefixTask(SqlBuilder sql) {
            sql.append("SELECT ");
            sql.appendIdentifier(COLUMN_ID).append(", ");
            sql.appendIdentifier(COLUMN_OBJECT_ID1).append(", ");
            sql.appendIdentifier(COLUMN_OBJECT_TYPE1).append(", ");
            sql.appendIdentifier(COLUMN_OBJECT_ID2).append(", ");
            sql.appendIdentifier(COLUMN_OBJECT_TYPE2).append(", ");
            sql.appendIdentifier(COLUMN_RELATION_TYPE).append(", ");
            sql.appendIdentifier(COLUMN_CREATION_BY).append(", ");
            sql.appendIdentifier(COLUMN_CREATION_TIME);
            sql.append(" FROM ").appendIdentifier(TABLE);
            return sql;
        }

        static PostgresqlTaskRelationRepo.Row select(DynamicSqlExecutor executor, String id) {
            SqlBuilder sql = fillSelectPrefixTask(SqlBuilder.custom());
            sql.append(" WHERE ").appendIdentifier(COLUMN_ID).append(" = ?");
            List<Object> args = Collections.singletonList(id);
            List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
            if (rows.isEmpty()) {
                return null;
            } else {
                return new PostgresqlTaskRelationRepo.Row(rows.get(0));
            }
        }
    }

    private static TaskRelation convert(PostgresqlTaskRelationRepo.Row row,
            Function<LocalDateTime, LocalDateTime> datetimeMapper) {
        return TaskRelation.custom()
                .id(row.id())
                .objectId1(row.objectId1())
                .objectType1(row.objectType1())
                .objectId2(row.objectId2())
                .objectType2(row.objectType2())
                .relationType(row.relationType())
                .createdBy(row.creator())
                .createdAt(datetimeMapper.apply(row.creationTime()))
                .build();
    }
}
