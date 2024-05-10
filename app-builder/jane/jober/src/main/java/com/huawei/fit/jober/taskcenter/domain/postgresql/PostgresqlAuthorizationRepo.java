/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.Authorization;
import com.huawei.fit.jane.task.util.Dates;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Sqls;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.DeleteSql;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fit.jober.taskcenter.util.sql.UpdateSql;
import com.huawei.fit.jober.taskcenter.validation.AuthorizationValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.StringUtils;

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
 * 为 {@link Authorization.Repo} 提供基于 {@code Postgresql} 数据库的实现。
 *
 * @author 梁济时 l00815032
 * @since 2023-11-27
 */
@Component
public class PostgresqlAuthorizationRepo implements Authorization.Repo {
    private static final Logger log = Logger.get(PostgresqlAuthorizationRepo.class);

    private final DynamicSqlExecutor executor;

    private final AuthorizationValidator validator;

    public PostgresqlAuthorizationRepo(DynamicSqlExecutor executor, AuthorizationValidator validator) {
        this.executor = executor;
        this.validator = validator;
    }

    @Override
    @Transactional
    public Authorization create(Authorization.Declaration declaration, OperationContext context) {
        Row row = new Row();
        row.id(Entities.generateId());
        row.system(this.validator.system(UndefinableValue.require(declaration.system(),
                () -> new BadRequestException(ErrorCodes.AUTHORIZATION_SYSTEM_REQUIRED))));
        row.user(this.validator.user(UndefinableValue.require(declaration.user(),
                () -> new BadRequestException(ErrorCodes.AUTHORIZATION_USER_REQUIRED))));
        row.token(this.validator.token(UndefinableValue.require(declaration.token(),
                () -> new BadRequestException(ErrorCodes.AUTHORIZATION_TOKEN_REQUIRED))));
        row.expiration(this.validator.expiration(UndefinableValue.withDefault(declaration.expiration(), 0L)));
        row.creator(context.operator());
        row.creationTime(LocalDateTime.now());
        row.lastModifier(row.creator());
        row.lastModificationTime(row.creationTime());
        row.insert(this.executor);
        return convert(row, Function.identity());
    }

    @Override
    @Transactional
    public void patch(String id, Authorization.Declaration declaration, OperationContext context) {
        String actualId = this.validator.id(id);
        UpdateSql sql = UpdateSql.custom().table(Row.TABLE);
        declaration.system().ifDefined(value -> sql.set(Row.COLUMN_SYSTEM, this.validator.system(value)));
        declaration.user().ifDefined(value -> sql.set(Row.COLUMN_USER, this.validator.user(value)));
        declaration.token().ifDefined(value -> sql.set(Row.COLUMN_TOKEN, this.validator.token(value)));
        declaration.expiration().ifDefined(value -> sql.set(Row.COLUMN_EXPIRATION, this.validator.expiration(value)));
        sql.set(Row.COLUMN_LAST_MODIFIER, context.operator());
        sql.set(Row.COLUMN_LAST_MODIFICATION_TIME, Dates.toUtc(LocalDateTime.now()));
        sql.where(Condition.expectEqual(Row.COLUMN_ID, actualId));
        if (sql.execute(this.executor) < 1) {
            log.error("The authorization to patch does not exist. [id={}]", actualId);
            throw new NotFoundException(ErrorCodes.AUTHORIZATION_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void delete(String id, OperationContext context) {
        String actualId = this.validator.id(id);
        DeleteSql sql = DeleteSql.custom().from(Row.TABLE).where(Condition.expectEqual(Row.COLUMN_ID, actualId));
        int affectedRows = sql.execute(this.executor);
        if (affectedRows < 1) {
            log.error("The authorization to delete does not exist. [id={}]", actualId);
            throw new NotFoundException(ErrorCodes.AUTHORIZATION_NOT_FOUND);
        }
    }

    @Override
    public Authorization retrieve(String id, OperationContext context) {
        String actualId = this.validator.id(id);
        Row row = Row.select(this.executor, actualId);
        if (row == null) {
            log.error("The authorization to retrieve does not exist. [id={}]", actualId);
            throw new NotFoundException(ErrorCodes.AUTHORIZATION_NOT_FOUND);
        }
        return convert(row, Dates::fromUtc);
    }

    @Override
    public Authorization retrieveSystemUser(String system, String user, OperationContext context) {
        Row row = Row.selectSystemUser(this.executor, system, user);
        if (row == null) {
            return null;
        }
        return convert(row, Dates::fromUtc);
    }

    @Override
    public RangedResultSet<Authorization> list(Authorization.Filter filter, long offset, int limit,
            OperationContext context) {
        List<String> ids = values(filter.ids());
        List<String> systems = values(filter.systems());
        List<String> users = values(filter.users());
        StringBuilder where = new StringBuilder();
        List<Object> args = new LinkedList<>();
        if (!ids.isEmpty()) {
            Sqls.andIn(where, Sqls.identifier(Row.COLUMN_ID), ids.size());
            args.addAll(ids);
        }
        if (!users.isEmpty()) {
            Sqls.andIn(where, Sqls.identifier(Row.COLUMN_USER), users.size());
            args.addAll(users);
        }
        if (!systems.isEmpty()) {
            Sqls.andLikeAny(where, Sqls.identifier(Row.COLUMN_SYSTEM), systems.size());
            args.addAll(systems.stream().map(Sqls::escapeLikeValue).collect(Collectors.toList()));
        }

        SqlBuilder countSql = SqlBuilder.custom().append("SELECT COUNT(1) FROM ").appendIdentifier(Row.TABLE);
        appendWhere(countSql, where);
        long total = ((Number) this.executor.executeScalar(countSql.toString(), args)).longValue();

        SqlBuilder whereSql = Row.fillSelectPrefix(SqlBuilder.custom());
        appendWhere(whereSql, where);
        whereSql.append(" OFFSET ? LIMIT ?");
        List<Object> whereArgs = new ArrayList<>(args.size() + 2);
        whereArgs.addAll(args);
        whereArgs.addAll(Arrays.asList(offset, limit));
        List<Map<String, Object>> rows = this.executor.executeQuery(whereSql.toString(), whereArgs);
        List<Authorization> authorizations = rows.stream()
                .map(values -> convert(new Row(values), Dates::fromUtc))
                .collect(Collectors.toList());

        return RangedResultSet.create(authorizations, (int) offset, limit, (int) total);
    }

    private static List<String> values(List<String> values) {
        return Optional.ofNullable(values).map(Collection::stream).orElseGet(Stream::empty)
                .map(StringUtils::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
    }

    private static void appendWhere(SqlBuilder sql, StringBuilder where) {
        if (where.length() > 0) {
            sql.append(" WHERE ").append(where.substring(5));
        }
    }

    private static final class Row {
        private static final String TABLE = "authorization";

        private static final String COLUMN_ID = "id";

        private static final String COLUMN_SYSTEM = "system";

        private static final String COLUMN_USER = "user";

        private static final String COLUMN_TOKEN = "token";

        private static final String COLUMN_EXPIRATION = "expiration";

        private static final String COLUMN_CREATOR = "created_by";

        private static final String COLUMN_CREATION_TIME = "created_at";

        private static final String COLUMN_LAST_MODIFIER = "updated_by";

        private static final String COLUMN_LAST_MODIFICATION_TIME = "updated_at";

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

        String system() {
            return cast(this.values.get(COLUMN_SYSTEM));
        }

        void system(String system) {
            this.values.put(COLUMN_SYSTEM, system);
        }

        String user() {
            return cast(this.values.get(COLUMN_USER));
        }

        void user(String user) {
            this.values.put(COLUMN_USER, user);
        }

        String token() {
            return cast(this.values.get(COLUMN_TOKEN));
        }

        void token(String token) {
            this.values.put(COLUMN_TOKEN, token);
        }

        Long expiration() {
            Object expiration = this.values.computeIfAbsent(COLUMN_EXPIRATION, k -> 0L);
            if (!(expiration instanceof Long)) {
                expiration = ((Number) expiration).longValue();
                this.values.put(COLUMN_EXPIRATION, expiration);
            }
            return cast(expiration);
        }

        void expiration(Long expiration) {
            this.values.put(COLUMN_EXPIRATION, expiration);
        }

        String creator() {
            return cast(this.values.get(COLUMN_CREATOR));
        }

        void creator(String creator) {
            this.values.put(COLUMN_CREATOR, creator);
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

        String lastModifier() {
            return cast(this.values.get(COLUMN_LAST_MODIFIER));
        }

        void lastModifier(String lastModifier) {
            this.values.put(COLUMN_LAST_MODIFIER, lastModifier);
        }

        LocalDateTime lastModificationTime() {
            Object value = this.values.get(COLUMN_LAST_MODIFICATION_TIME);
            if (value instanceof Timestamp) {
                value = ((Timestamp) value).toLocalDateTime();
                this.values.put(COLUMN_LAST_MODIFICATION_TIME, value);
            }
            return cast(value);
        }

        void lastModificationTime(LocalDateTime lastModificationTime) {
            this.values.put(COLUMN_LAST_MODIFICATION_TIME, lastModificationTime);
        }

        void insert(DynamicSqlExecutor executor) {
            int affectedRows = InsertSql.custom().into(TABLE)
                    .value(COLUMN_ID, this.id())
                    .value(COLUMN_SYSTEM, this.system())
                    .value(COLUMN_USER, this.user())
                    .value(COLUMN_TOKEN, this.token())
                    .value(COLUMN_EXPIRATION, this.expiration())
                    .value(COLUMN_CREATOR, this.creator())
                    .value(COLUMN_CREATION_TIME, Dates.toUtc(this.creationTime()))
                    .value(COLUMN_LAST_MODIFIER, this.lastModifier())
                    .value(COLUMN_LAST_MODIFICATION_TIME, Dates.toUtc(this.lastModificationTime()))
                    .execute(executor);
            if (affectedRows < 1) {
                throw new ServerInternalException("Failed to insert authorization into database.");
            }
        }

        private static SqlBuilder fillSelectPrefix(SqlBuilder sql) {
            sql.append("SELECT ");
            sql.appendIdentifier(COLUMN_ID).append(", ");
            sql.appendIdentifier(COLUMN_SYSTEM).append(", ");
            sql.appendIdentifier(COLUMN_USER).append(", ");
            sql.appendIdentifier(COLUMN_TOKEN).append(", ");
            sql.appendIdentifier(COLUMN_EXPIRATION).append(", ");
            sql.appendIdentifier(COLUMN_CREATOR).append(", ");
            sql.appendIdentifier(COLUMN_CREATION_TIME).append(", ");
            sql.appendIdentifier(COLUMN_LAST_MODIFIER).append(", ");
            sql.appendIdentifier(COLUMN_LAST_MODIFICATION_TIME);
            sql.append(" FROM ").appendIdentifier(TABLE);
            return sql;
        }

        static Row select(DynamicSqlExecutor executor, String id) {
            SqlBuilder sql = fillSelectPrefix(SqlBuilder.custom());
            sql.append(" WHERE ").appendIdentifier(COLUMN_ID).append(" = ?");
            List<Object> args = Collections.singletonList(id);
            List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
            if (rows.isEmpty()) {
                return null;
            } else {
                return new Row(rows.get(0));
            }
        }

        private static Row selectSystemUser(DynamicSqlExecutor executor, String system, String user) {
            SqlBuilder sql = fillSelectPrefix(SqlBuilder.custom());
            sql.append(" WHERE ").appendIdentifier(COLUMN_SYSTEM).append(" = ?");
            sql.append(" AND ").appendIdentifier(COLUMN_USER).append(" = ?");
            List<Object> args = new ArrayList<>();
            args.add(system);
            args.add(user);
            List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
            if (rows.isEmpty()) {
                return null;
            } else {
                return new Row(rows.get(0));
            }
        }
    }

    private static Authorization convert(Row row, Function<LocalDateTime, LocalDateTime> datetimeMapper) {
        return Authorization.custom().id(row.id()).system(row.system()).user(row.user()).token(row.token())
                .expiration(row.expiration()).creator(row.creator()).lastModifier(row.lastModifier())
                .creationTime(datetimeMapper.apply(row.creationTime()))
                .lastModificationTime(datetimeMapper.apply(row.lastModificationTime()))
                .build();
    }
}
