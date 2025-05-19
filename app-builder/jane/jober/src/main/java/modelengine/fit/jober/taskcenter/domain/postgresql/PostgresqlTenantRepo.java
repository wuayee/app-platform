/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.postgresql;

import static modelengine.fit.jober.taskcenter.util.Sqls.longValue;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import lombok.RequiredArgsConstructor;
import modelengine.fit.jane.task.domain.Tenant;
import modelengine.fit.jane.task.domain.TenantAccessLevel;
import modelengine.fit.jane.task.domain.TenantMember;
import modelengine.fit.jane.task.gateway.EmployeeDetailVO;
import modelengine.fit.jane.task.util.Dates;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.ServerInternalException;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.common.exceptions.ConflictException;
import modelengine.fit.jober.common.exceptions.NotFoundException;
import modelengine.fit.jober.common.utils.UserUtil;
import modelengine.fit.jober.taskcenter.service.TagService;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.util.Enums;
import modelengine.fit.jober.taskcenter.util.Sqls;
import modelengine.fit.jober.taskcenter.util.sql.SqlBuilder;
import modelengine.fit.jober.taskcenter.validation.TenantValidator;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link Tenant.Repo} 提供基于 Postgresql 的实现。
 *
 * @author 陈镕希
 * @since 2023-10-11
 */
@Component
@RequiredArgsConstructor
public class PostgresqlTenantRepo implements Tenant.Repo {
    private static final String TABLE_NAME = "tenant";

    private static final String MEMBER_TABLE_NAME = "tenant_member";

    private static final Logger log = Logger.get(PostgresqlTenantRepo.class);

    private static final String TAG_TYPE = "TENANT";

    private final DynamicSqlExecutor executor;

    private final TenantValidator validator;

    private final UserUtil userUtil;

    private final TagService tagService;

    private static LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        return ObjectUtils.<Timestamp>cast(value).toLocalDateTime();
    }

    private static String toString(Object value) {
        return ObjectUtils.cast(value);
    }

    private static TenantAccessLevel toTenantAccessLevel(Object value) {
        if (value == null) {
            return null;
        }
        return Enums.parse(TenantAccessLevel.class, String.valueOf(value));
    }

    private static void fillWithTagsSql(SqlBuilder sql, int tagSize) {
        sql.append("WITH ").appendIdentifier("tag_owner").append(" AS SELECT ").appendIdentifier("tu");
        sql.append('.').appendIdentifier("object_id").append(" AS ").appendIdentifier("id").append(" FROM ");
        sql.appendIdentifier("tag").append(" AS ").appendIdentifier("t").append(" INNER JOIN ");
        sql.appendIdentifier("tag_usage").append(" AS ").appendIdentifier("tu").append(" ON ");
        sql.appendIdentifier("tu").append('.').appendIdentifier("tag_id").append(" = ").appendIdentifier("t");
        sql.append('.').appendIdentifier("id").append(" WHERE ").appendIdentifier("tu").append('.');
        sql.appendIdentifier("object_type").append(" = ? AND ").appendIdentifier("t").append('.');
        sql.appendIdentifier("name").append(" IN (").appendRepeatedly("?, ", tagSize).backspace(2);
        sql.append(") ");
    }

    private static String columns() {
        SqlBuilder sql = SqlBuilder.custom();
        sql.appendIdentifier("t").append('.').appendIdentifier("id").append(", ");
        sql.appendIdentifier("t").append('.').appendIdentifier("name").append(", ");
        sql.appendIdentifier("t").append('.').appendIdentifier("description").append(", ");
        sql.appendIdentifier("t").append('.').appendIdentifier("avatar_id").append(", ");
        sql.appendIdentifier("t").append('.').appendIdentifier("access_level").append(", ");
        sql.appendIdentifier("t").append('.').appendIdentifier("created_by").append(", ");
        sql.appendIdentifier("t").append('.').appendIdentifier("created_at").append(", ");
        sql.appendIdentifier("t").append('.').appendIdentifier("updated_by").append(", ");
        sql.appendIdentifier("t").append('.').appendIdentifier("updated_at");
        return sql.toString();
    }

    private static String memberColumns() {
        SqlBuilder sql = SqlBuilder.custom();
        sql.appendIdentifier("id").append(", ");
        sql.appendIdentifier("tenant_id").append(", ");
        sql.appendIdentifier("user_id").append(", ");
        sql.appendIdentifier("created_by").append(", ");
        sql.appendIdentifier("created_at");
        return sql.toString();
    }

    private static boolean whereIds(SqlBuilder sql, List<Object> args, Tenant.Filter filter) {
        return whereArgs(filter.ids(), sql, "id", args);
    }

    private static boolean whereArgs(UndefinableValue<List<String>> filter, SqlBuilder sql, String argKey,
            List<Object> args) {
        if (filter.defined()) {
            List<String> argValues = filter.withDefault(Collections.emptyList());
            if (argValues.isEmpty()) {
                return false;
            }
            sql.append(" AND ")
                    .appendIdentifier(argKey)
                    .append(" IN (")
                    .appendRepeatedly("?, ", argValues.size())
                    .backspace(2)
                    .append(')');
            args.addAll(argValues);
        }
        return true;
    }

    private static boolean whereNames(SqlBuilder sql, List<Object> args, Tenant.Filter filter) {
        return whereArgs(filter.names(), sql, "name", args);
    }

    private static boolean whereAccessLevel(SqlBuilder sql, List<Object> args, Tenant.Filter filter) {
        UndefinableValue<List<TenantAccessLevel>> undefinableAccessLevels = filter.accessLevels();
        if (undefinableAccessLevels.defined()) {
            List<TenantAccessLevel> tenantAccessLevels = undefinableAccessLevels.withDefault(Collections.emptyList());
            if (tenantAccessLevels.isEmpty()) {
                return false;
            }
            sql.append(" AND ")
                    .appendIdentifier("access_level")
                    .append(" IN (")
                    .appendRepeatedly("?, ", tenantAccessLevels.size())
                    .backspace(2)
                    .append(')');
            args.addAll(tenantAccessLevels.stream().map(TenantAccessLevel::name).collect(Collectors.toList()));
        }
        return true;
    }

    private static boolean whereTenantId(SqlBuilder sql, List<Object> args, TenantMember.Filter filter) {
        if (!filter.tenantId().defined()) {
            return false;
        }
        String tenantId = filter.tenantId().withDefault(StringUtils.EMPTY);
        if (StringUtils.isBlank(tenantId)) {
            return false;
        }
        sql.append(" AND ").appendIdentifier("tenant_id").append(" = ?");
        args.add(tenantId);
        return true;
    }

    private static boolean whereIds(SqlBuilder sql, List<Object> args, TenantMember.Filter filter) {
        return whereArgs(filter.ids(), sql, "id", args);
    }

    @Override
    @Transactional
    public Tenant create(Tenant.Declaration declaration, OperationContext context) {
        String name = this.validator.name(
                declaration.name().required(() -> new BadRequestException(ErrorCodes.TENANT_NAME_REQUIRED)), context);
        this.verifyTheExistenceOfTenantName(context, null, name);
        String tenantId = Entities.generateId();
        String operator = this.getGlobalUserId(context);
        LocalDateTime operationTime = LocalDateTime.now();
        LocalDateTime operationTimeUtc = Dates.toUtc(operationTime);
        String description = declaration.description().withDefault(null);
        String avatarId = declaration.avatarId().withDefault(Entities.emptyId());
        TenantAccessLevel accessLevel = declaration.accessLevel().withDefault(TenantAccessLevel.PRIVATE);
        List<Object> args = Arrays.asList(tenantId, name, description, avatarId, operator, operationTimeUtc, operator,
                operationTimeUtc, Enums.toString(accessLevel));
        String sql = Sqls.script(TABLE_NAME, "insert-prefix") + Sqls.script(TABLE_NAME, "insert-values");
        if (this.executor.executeUpdate(sql, args) < 1) {
            throw new ServerInternalException("Failed to save tenant into database.");
        }
        List<String> members = new ArrayList<>(declaration.members().withDefault(new ArrayList<>()));
        members.add(context.operator());
        this.insertMembersWithoutAuth(tenantId, members, context);
        if (declaration.tags().defined()) {
            tagService.save(TAG_TYPE, Collections.singletonMap(tenantId, declaration.tags().get()), context);
        }
        return Tenant.custom()
                .id(tenantId)
                .name(name)
                .description(description)
                .avatarId(Entities.ignoreEmpty(avatarId))
                .tags(declaration.tags().withDefault(Collections.emptyList()))
                .creator(operator)
                .creationTime(operationTime)
                .lastModifier(operator)
                .lastModificationTime(operationTime)
                .accessLevel(accessLevel)
                .build();
    }

    private void insertMembersWithoutAuth(String tenantId, List<String> members, OperationContext context) {
        String operator = this.getGlobalUserId(context);
        List<String> memberGlobalUserIdList = members.stream()
                .map(this::getGlobalUserIdFromUserName)
                .collect(Collectors.toList());
        List<String> allMemberGlobalUserIdList = memberGlobalUserIdList.stream()
                .distinct()
                .collect(Collectors.toList());
        LocalDateTime operationTimeUtc = Dates.toUtc(LocalDateTime.now());
        List<Object> memberArgs = allMemberGlobalUserIdList.stream()
                .flatMap(globalUserId -> Stream.of(Entities.generateId(), tenantId, globalUserId, operator,
                        operationTimeUtc))
                .collect(Collectors.toList());
        StringBuilder memberStringBuilder = new StringBuilder();
        memberStringBuilder.append(Sqls.script(TABLE_NAME, "insert-member-prefix"));
        memberStringBuilder.append(Sqls.script(TABLE_NAME, "insert-member-values"));
        for (int i = 1; i < allMemberGlobalUserIdList.size(); i++) {
            memberStringBuilder.append(",");
            memberStringBuilder.append(Sqls.script(TABLE_NAME, "insert-member-values"));
        }
        memberStringBuilder.append(Sqls.script(TABLE_NAME, "insert-member-conflict"));
        memberStringBuilder.append(" DO NOTHING");
        String memberSql = memberStringBuilder.toString();
        if (this.executor.executeUpdate(memberSql, memberArgs) < 1) {
            throw new BadRequestException(ErrorCodes.MEMBER_IS_EXISTS);
        }
    }

    private void verifyTheExistenceOfTenantName(OperationContext context, String tenantId, String name) {
        RangedResultSet<Tenant> resultSet = this.list(
                Tenant.Filter.custom().names(Collections.singletonList(name)).build(), 0, 1, context);
        if (resultSet.getRange().getTotal() > 0 && !StringUtils.equals(resultSet.getResults().get(0).id(), tenantId)) {
            log.error("Tenant {} already exists.", name);
            throw new BadRequestException(ErrorCodes.TENANT_IS_EXISTS);
        }
    }

    @Override
    @Transactional
    public void patch(String tenantId, Tenant.Declaration declaration, OperationContext context) {
        StringBuilder sql = new StringBuilder();
        sql.append(Sqls.script(TABLE_NAME, "update-prefix"));
        List<Object> args = new LinkedList<>();
        args.add(this.getGlobalUserId(context));
        args.add(Dates.toUtc(LocalDateTime.now()));
        Tenant tenant = this.retrieve(tenantId, context);
        checkUserPermission(tenant, context);
        if (declaration.name().defined()) {
            String actualName = this.validator.name(declaration.name().get(), context);
            this.verifyTheExistenceOfTenantName(context, tenantId, actualName);
            args.add(actualName);
            sql.append(", \"name\" = ?");
        }
        if (declaration.description().defined()) {
            args.add(declaration.description().get());
            sql.append(", \"description\" = ?");
        }
        if (declaration.avatarId().defined()) {
            args.add(declaration.avatarId().get());
            sql.append(", \"avatar_id\" = ?");
        }
        if (declaration.accessLevel().defined()) {
            args.add(Enums.toString(declaration.accessLevel().get()));
            sql.append(", \"access_level\" = ?");
        }
        sql.append(" WHERE \"id\" = ?");
        String actualTenantId = Entities.validateId(tenantId, () -> new NotFoundException(ErrorCodes.TENANT_NOT_FOUND));
        args.add(actualTenantId);
        if (this.executor.executeUpdate(sql.toString(), args) < 1) {
            throw new NotFoundException(ErrorCodes.TENANT_NOT_FOUND);
        }
        if (declaration.members().defined()) {
            boolean canLoopFlag = true;
            Set<String> authorizedUserIdList = this.queryAuthorizedUserIdList(context, actualTenantId, canLoopFlag);
            HashSet<String> declarationMemberSet = new HashSet<>(declaration.members().get());
            Set<String> newMembers = CollectionUtils.difference(declarationMemberSet, authorizedUserIdList);
            Set<String> deleteMembers = CollectionUtils.difference(authorizedUserIdList, declarationMemberSet);
            if (!newMembers.isEmpty()) {
                this.insertMembers(actualTenantId, new ArrayList<>(newMembers), context);
            }
            if (!deleteMembers.isEmpty()) {
                this.deleteMembersByUserId(actualTenantId, new ArrayList<>(deleteMembers), context);
            }
        }
        if (declaration.tags().defined()) {
            tagService.save(TAG_TYPE, Collections.singletonMap(actualTenantId, declaration.tags().get()), context);
        }
    }

    private Set<String> queryAuthorizedUserIdList(OperationContext context, String actualTenantId, boolean canLoop) {
        Set<String> authorizedUserIdList = new HashSet<>();
        boolean canLoopFlag = canLoop;
        for (int i = 0; canLoopFlag; i++) {
            RangedResultSet<TenantMember> memberResult = this.listMember(
                    TenantMember.Filter.custom().tenantId(actualTenantId).build(), i * 100L, 100, context);
            authorizedUserIdList.addAll(
                    memberResult.getResults().stream().map(TenantMember::userId).collect(Collectors.toList()));
            if (memberResult.getRange().getTotal() <= i * 100L + 100) {
                canLoopFlag = false;
            }
        }
        return authorizedUserIdList;
    }

    @Override
    @Transactional
    public void delete(String tenantId, OperationContext context) {
        Tenant tenant = this.retrieve(tenantId, context);
        this.deleteMemberByTenant(tenant, context);
        List<Object> args = Collections.singletonList(tenantId);
        List<Object> selectTaskByTenantArgs = Collections.singletonList(tenantId);
        String sqlString = Sqls.script(TABLE_NAME, "select-task-count-by-tenant");
        long idCount = longValue(this.executor.executeScalar(sqlString, selectTaskByTenantArgs));
        long nameCount = longValue(this.executor.executeScalar(sqlString, Collections.singletonList(tenant.name())));
        if (idCount > 0 || nameCount > 0) {
            throw new ConflictException(ErrorCodes.TENANT_DELETING_HAS_TASKS);
        }
        sqlString = Sqls.script(TABLE_NAME, "delete-by-id");
        if (this.executor.executeUpdate(sqlString, args) < 1) {
            throw new NotFoundException(ErrorCodes.TENANT_NOT_FOUND);
        }
        tagService.save(TAG_TYPE, Collections.singletonMap(tenantId, Collections.emptyList()), context);
    }

    @Override
    @Transactional
    public Tenant retrieve(String tenantId, OperationContext context) {
        // 由于历史原因，TenantId传入有可能是租户id，也有可能是租户名称，这里需要兼容都查询
        List<Object> args = Collections.singletonList(tenantId);
        SqlBuilder sql = SqlBuilder.custom();
        buildSelectFromWhere(sql).appendIdentifier("id").append(" = ?");
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        List<TenantRow> tenantRows = rows.stream()
                .map(row -> new TenantRow(row, userUtil))
                .collect(Collectors.toList());
        if (tenantRows.isEmpty()) {
            sql = SqlBuilder.custom();
            buildSelectFromWhere(sql).appendIdentifier("name").append(" = ?");
            rows = this.executor.executeQuery(sql.toString(), args);
            tenantRows = rows.stream().map(row -> new TenantRow(row, userUtil)).collect(Collectors.toList());
            if (tenantRows.isEmpty()) {
                log.error("The tenant to retrieve does not exist. [tenantId={}]", tenantId);
                throw new NotFoundException(ErrorCodes.TENANT_NOT_FOUND);
            }
        }
        TenantRow row = tenantRows.get(0);
        Map<String, List<String>> tags = this.tagService.list(TAG_TYPE, Collections.singletonList(row.id()), context);
        row.tags(tags.get(row.id()));
        return tenantRows.get(0).toTenant();
    }

    @Override
    @Transactional
    public RangedResultSet<Tenant> list(Tenant.Filter filter, long offset, int limit, OperationContext context) {
        validator.validatePagination(offset, limit);
        SqlBuilder sql = SqlBuilder.custom();
        List<Object> args = new LinkedList<>();
        boolean shouldWithTags = false;
        if (filter.tags().defined()) {
            List<String> tags = filter.tags().withDefault(Collections.emptyList());
            if (tags.isEmpty()) {
                return Entities.emptyRangedResultSet(offset, limit);
            }
            shouldWithTags = true;
            fillWithTagsSql(sql, tags.size());
            args.add(TAG_TYPE);
            args.addAll(tags);
        }
        sql.append("SELECT {0} FROM ").appendIdentifier(TABLE_NAME).append(" AS ").appendIdentifier("t");
        if (shouldWithTags) {
            sql.append(" INNER JOIN ")
                    .appendIdentifier("tag_owner")
                    .append(" AS ")
                    .appendIdentifier("to")
                    .append(" ON ")
                    .appendIdentifier("t")
                    .append('.')
                    .appendIdentifier("id")
                    .append(" = ")
                    .appendIdentifier("to")
                    .append('.')
                    .appendIdentifier("id");
        }
        sql.append(" WHERE 1 = 1");
        if (!whereIds(sql, args, filter) || !whereNames(sql, args, filter) || !whereAccessLevel(sql, args, filter)) {
            return Entities.emptyRangedResultSet(offset, limit);
        }
        return this.getTenantRangedResultSet(offset, limit, context, sql, args);
    }

    @Override
    public RangedResultSet<Tenant> listMy(List<String> myTenantIds, long offset, int limit, OperationContext context) {
        validator.validatePagination(offset, limit);
        SqlBuilder sql = SqlBuilder.custom();
        List<Object> args = new LinkedList<>();
        sql.append("SELECT {0} FROM ").appendIdentifier(TABLE_NAME).append(" AS ").appendIdentifier("t");
        sql.append(" WHERE \"access_level\" = 'PUBLIC' OR ");
        sql.append("1 = 1");
        boolean hasTenantArg = whereArgs(UndefinableValue.defined(myTenantIds), sql, "id", args);
        if (!hasTenantArg) {
            sql.backspace(9);
        }
        return this.getTenantRangedResultSet(offset, limit, context, sql, args);
    }

    private RangedResultSet<Tenant> getTenantRangedResultSet(long offset, int limit, OperationContext context,
            SqlBuilder sql, List<Object> args) {
        String countSql = StringUtils.format(sql.toString(), "COUNT(1)");
        long count = longValue(this.executor.executeScalar(countSql, new ArrayList<>(args)));

        sql.append(" ORDER BY ").appendIdentifier("created_at").append(" OFFSET ? LIMIT ?");
        args.addAll(Arrays.asList(offset, limit));
        String selectSql = StringUtils.format(sql.toString(), columns());
        List<TenantRow> rows = TenantRow.select(this.executor, selectSql, args, userUtil);
        List<String> tenantIds = rows.stream().map(TenantRow::id).collect(Collectors.toList());
        Map<String, List<String>> tags = this.tagService.list(TAG_TYPE, tenantIds, context);
        rows.forEach(row -> row.tags(tags.get(row.id())));
        List<Tenant> tenants = rows.stream().map(TenantRow::toTenant).collect(Collectors.toList());

        return RangedResultSet.create(tenants, (int) offset, limit, (int) count);
    }

    @Override
    @Transactional
    public void insertMembers(String tenantId, List<String> members, OperationContext context) {
        Tenant tenant = this.retrieve(tenantId, context);
        checkUserPermission(tenant, context);
        this.insertMembersWithoutAuth(tenantId, members, context);
    }

    @Override
    @Transactional
    public void deleteMemberByTenant(Tenant tenant, OperationContext context) {
        checkUserPermission(tenant, context);
        String sqlString = Sqls.script(TABLE_NAME, "delete-members-by-tenant-id");
        this.executor.executeUpdate(sqlString, Collections.singletonList(tenant.id()));
    }

    @Override
    @Transactional
    public void deleteMembersById(String tenantId, List<String> memberIds, OperationContext context) {
        Tenant tenant = this.retrieve(tenantId, context);
        checkUserPermission(tenant, context);
        if (CollectionUtils.isEmpty(memberIds)) {
            return;
        }
        List<Object> args = new LinkedList<>();
        args.add(Entities.validateId(tenantId, () -> new BadRequestException(ErrorCodes.TENANT_ID_INVALID)));
        memberIds.stream()
                .map(memberId -> Entities.validateId(memberId,
                        () -> new BadRequestException(ErrorCodes.MEMBER_ID_INVALID)))
                .forEach(args::add);
        SqlBuilder sqlBuilder = SqlBuilder.custom().append(Sqls.script(TABLE_NAME, "delete-member-prefix") + "(");
        this.constructSqlBuilderAndExecute(memberIds, args, sqlBuilder);
    }

    private void checkUserPermission(Tenant tenant, OperationContext context) {
        if (tenant.isPermitted(this, context.operator(), context)) {
            return;
        }
        log.error("Operation {} does not have authority to operate tenant {}", context.operator(), tenant.id());
        throw new BadRequestException(ErrorCodes.NO_OPERATE_PERMISSION);
    }

    @Override
    public void deleteMembersByUserId(String tenantId, List<String> userIds, OperationContext context) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<String> memberGlobalUserIdList = userIds.stream()
                .map(this::getGlobalUserIdFromUserName)
                .collect(Collectors.toList());
        List<Object> args = new LinkedList<>();
        args.add(Entities.validateId(tenantId, () -> new NotFoundException(ErrorCodes.TENANT_NOT_FOUND)));
        args.addAll(memberGlobalUserIdList);
        SqlBuilder sqlBuilder = SqlBuilder.custom()
                .append(Sqls.script(TABLE_NAME, "delete-member-by-userid-prefix") + "(");
        this.constructSqlBuilderAndExecute(memberGlobalUserIdList, args, sqlBuilder);
    }

    @Override
    public List<String> listTenantIdsByUserId(String userId, OperationContext context) {
        String globalUserId = this.getGlobalUserIdFromUserName(userId);
        SqlBuilder sql = SqlBuilder.custom()
                .append("SELECT \"tenant_id\" FROM ")
                .appendIdentifier(MEMBER_TABLE_NAME)
                .append(" WHERE user_id = ?");
        List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), Collections.singletonList(globalUserId));
        return rows.stream().map(row -> ObjectUtils.<String>cast(row.get("tenant_id"))).collect(Collectors.toList());
    }

    private void constructSqlBuilderAndExecute(List<String> memberGlobalUserIdList, List<Object> args,
            SqlBuilder sqlBuilder) {
        for (int i = 0; i < memberGlobalUserIdList.size(); i++) {
            sqlBuilder.append("?,");
        }
        sqlBuilder.backspace(1);
        sqlBuilder.append(")");
        if (this.executor.executeUpdate(sqlBuilder.toString(), args) < 1) {
            throw new NotFoundException(ErrorCodes.MEMBER_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public RangedResultSet<TenantMember> listMember(TenantMember.Filter filter, long offset, int limit,
            OperationContext context) {
        validator.validatePagination(offset, limit);
        SqlBuilder sql = SqlBuilder.custom();
        List<Object> args = new LinkedList<>();
        sql.append("SELECT {0} FROM ").appendIdentifier(MEMBER_TABLE_NAME).append(" WHERE 1 = 1");
        if (!whereIds(sql, args, filter) || !whereTenantId(sql, args, filter) || !whereUserIds(filter, sql, args)) {
            return Entities.emptyRangedResultSet(offset, limit);
        }
        String countSql = StringUtils.format(sql.toString(), "COUNT(1)");
        long count = longValue(this.executor.executeScalar(countSql, new ArrayList<>(args)));
        sql.append(" ORDER BY ").appendIdentifier("created_at").append(" OFFSET ? LIMIT ?");
        args.addAll(Arrays.asList(offset, limit));
        String selectSql = StringUtils.format(sql.toString(), memberColumns());
        List<TenantMemberRow> rows = TenantMemberRow.select(this.executor, selectSql, args, userUtil);
        List<TenantMember> tenantMembers = rows.stream()
                .map(TenantMemberRow::toTenantMember)
                .collect(Collectors.toList());
        return RangedResultSet.create(tenantMembers, (int) offset, limit, (int) count);
    }

    private boolean whereUserIds(TenantMember.Filter filter, SqlBuilder sql, List<Object> args) {
        UndefinableValue<List<String>> undefinableUserIds = filter.userIds();
        if (undefinableUserIds.defined()) {
            List<String> argValues = undefinableUserIds.withDefault(Collections.emptyList());
            if (argValues.isEmpty()) {
                return false;
            }
            argValues = argValues.stream().map(this::getGlobalUserIdFromUserName).collect(Collectors.toList());
            sql.append(" AND ")
                    .appendIdentifier("user_id")
                    .append(" IN (")
                    .appendRepeatedly("?, ", argValues.size())
                    .backspace(2)
                    .append(')');
            args.addAll(argValues);
        }
        return true;
    }

    private String getGlobalUserId(OperationContext context) {
        String operator = context.operator();
        return this.getGlobalUserIdFromUserName(operator);
    }

    private String getGlobalUserIdFromUserName(String operator) {
        if (StringUtils.isBlank(operator)) {
            return StringUtils.EMPTY;
        }
        String[] operatorAfterSplit = operator.split(" ");
        EmployeeDetailVO employeeDetailVO = userUtil.getEmployeeDetail(
                operatorAfterSplit[operatorAfterSplit.length - 1], null, null, null);
        return employeeDetailVO.getGlobalUserId();
    }

    private SqlBuilder buildSelectFromWhere(SqlBuilder sql) {
        return sql.append("SELECT ")
                .appendIdentifier("id")
                .append(", ")
                .appendIdentifier("name")
                .append(", ")
                .appendIdentifier("description")
                .append(", ")
                .appendIdentifier("avatar_id")
                .append(", ")
                .appendIdentifier("access_level")
                .append(", ")
                .appendIdentifier("created_by")
                .append(", ")
                .appendIdentifier("created_at")
                .append(", ")
                .appendIdentifier("updated_by")
                .append(", ")
                .appendIdentifier("updated_at")
                .append(" FROM ")
                .appendIdentifier(TABLE_NAME)
                .append(" WHERE ");
    }

    private static class TenantRow {
        private final Map<String, Object> row;

        private final UserUtil userUtil;

        TenantRow(Map<String, Object> row, UserUtil userUtil) {
            this.row = row;
            this.userUtil = userUtil;
        }

        static List<TenantRow> select(DynamicSqlExecutor executor, String sql, List<Object> args, UserUtil userUtil) {
            return executor.executeQuery(sql, args)
                    .stream()
                    .map(row -> new TenantRow(row, userUtil))
                    .collect(Collectors.toList());
        }

        String id() {
            return PostgresqlTenantRepo.toString(this.row.get("id"));
        }

        String name() {
            return PostgresqlTenantRepo.toString(this.row.get("name"));
        }

        String description() {
            return PostgresqlTenantRepo.toString(this.row.get("description"));
        }

        String avatarId() {
            return PostgresqlTenantRepo.toString(this.row.get("avatar_id"));
        }

        TenantAccessLevel accessLevel() {
            return PostgresqlTenantRepo.toTenantAccessLevel(this.row.get("access_level"));
        }

        String creator() {
            return PostgresqlTenantRepo.toString(this.row.get("created_by"));
        }

        LocalDateTime creationTime() {
            return PostgresqlTenantRepo.toLocalDateTime(this.row.get("created_at"));
        }

        String lastModifier() {
            return PostgresqlTenantRepo.toString(this.row.get("updated_by"));
        }

        LocalDateTime lastModificationTime() {
            return PostgresqlTenantRepo.toLocalDateTime(this.row.get("updated_at"));
        }

        List<String> tags() {
            List<String> tags = cast(this.row.get("tags"));
            return nullIf(tags, Collections.emptyList());
        }

        void tags(List<String> tags) {
            List<String> actual = Optional.ofNullable(tags)
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .map(StringUtils::trim)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            if (actual.isEmpty()) {
                this.row.remove("tags");
            } else {
                this.row.put("tags", tags);
            }
        }

        Tenant toTenant() {
            return Tenant.custom()
                    .id(this.id())
                    .name(this.name())
                    .description(this.description())
                    .avatarId(Entities.ignoreEmpty(this.avatarId()))
                    .accessLevel(this.accessLevel())
                    .creator(userUtil.getUserName(null, null, null, this.creator()))
                    .creationTime(Dates.fromUtc(this.creationTime()))
                    .lastModifier(this.lastModifier())
                    .lastModificationTime(Dates.fromUtc(this.lastModificationTime()))
                    .tags(this.tags())
                    .build();
        }
    }

    private static class TenantMemberRow {
        private static final int LOAD_USER_ID = 0x01;

        private static final int LOAD_CREATOR_ID = 0x02;

        private final Map<String, Object> row;

        private final UserUtil userUtil;

        private int flags;

        TenantMemberRow(Map<String, Object> row, UserUtil userUtil) {
            this.row = row;
            this.userUtil = userUtil;
            this.flags = 0;
        }

        static List<TenantMemberRow> select(DynamicSqlExecutor executor, String sql, List<Object> args,
                UserUtil userUtil) {
            return executor.executeQuery(sql, args)
                    .stream()
                    .map(row -> new TenantMemberRow(row, userUtil))
                    .collect(Collectors.toList());
        }

        String id() {
            return PostgresqlTenantRepo.toString(this.row.get("id"));
        }

        String tenantId() {
            return PostgresqlTenantRepo.toString(this.row.get("tenant_id"));
        }

        String userId() {
            String value = PostgresqlTenantRepo.toString(this.row.get("user_id"));
            if (StringUtils.isNotBlank(value) && (this.flags & LOAD_USER_ID) != LOAD_USER_ID) {
                value = this.userUtil.getUserName(null, null, null, value);
                this.row.put("user_id", value);
                this.flags |= LOAD_USER_ID;
            }
            return value;
        }

        String creator() {
            String value = PostgresqlTenantRepo.toString(this.row.get("created_by"));
            if (StringUtils.isNotBlank(value) && (this.flags & LOAD_CREATOR_ID) != LOAD_CREATOR_ID) {
                value = this.userUtil.getUserName(null, null, null, value);
                this.row.put("created_by", value);
                this.flags |= LOAD_CREATOR_ID;
            }
            return value;
        }

        LocalDateTime creationTime() {
            return PostgresqlTenantRepo.toLocalDateTime(this.row.get("created_at"));
        }

        TenantMember toTenantMember() {
            return TenantMember.custom()
                    .id(this.id())
                    .tenantId(this.tenantId())
                    .userId(this.userId())
                    .creator(this.creator())
                    .creationTime(Dates.fromUtc(this.creationTime()))
                    .build();
        }
    }
}
