/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static com.huawei.fit.jober.taskcenter.util.Sqls.longValue;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.util.Dates;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.common.model.TextStringValue;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.taskcenter.declaration.NodeDeclaration;
import com.huawei.fit.jober.taskcenter.domain.NodeEntity;
import com.huawei.fit.jober.taskcenter.filter.NodeFilter;
import com.huawei.fit.jober.taskcenter.service.NodeService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.ExecutableSql;
import com.huawei.fit.jober.taskcenter.util.Sqls;
import com.huawei.fit.jober.taskcenter.validation.NodeValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 为 {@link NodeService} 提供实现。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-17
 */
@Component
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {
    private final DynamicSqlExecutor executor;

    private final NodeValidator validator;

    private static NodeEntity toEntity(Map<String, Object> row) {
        NodeEntity entity = new NodeEntity();
        entity.setId(ObjectUtils.cast(row.get("id")));
        entity.setChildCount(0);
        entity.setParentId(Entities.ignoreEmpty(ObjectUtils.cast(row.get("parent_id"))));
        entity.setName(ObjectUtils.cast(row.get("name")));
        Entities.fillTraceInfo(entity, row);
        return entity;
    }

    @Override
    @Transactional
    public NodeEntity create(String treeId, NodeDeclaration declaration, OperationContext context) {
        String actualTreeId = Entities.validateId(treeId,
                () -> new BadRequestException(ErrorCodes.TREE_ID_INVALID, ParamUtils.convertOperationContext(context)));
        String name = this.validator.name(
                declaration.getName().required(() -> new BadRequestException(ErrorCodes.TYPE_NAME_REQUIRED)),
                this.nodeNames(treeId), context);
        String parentId = this.validator.parentId(declaration.getParentId().get(), context);
        List<String> sourceIds = declaration.getSourceIds().withDefault(Collections.emptyList());
        sourceIds = sourceIds.stream()
                .map(sourceId -> Entities.validateId(sourceId,
                        () -> new BadRequestException(ErrorCodes.SOURCE_SOURCE_INVALID,
                                ParamUtils.convertOperationContext(context))))
                .collect(Collectors.toList());
        String operator = context.operator();
        LocalDateTime operationTime = LocalDateTime.now();
        LocalDateTime operationTimeUtc = Dates.toUtc(operationTime);
        String nodeId = Entities.generateId();
        String sql = "INSERT INTO task_type(id, tree_id, parent_id, task_id, name, created_by, created_at, updated_by, "
                + "updated_at) VALUES(?, (SELECT id FROM task_tree_v2 WHERE id = ?), ?, "
                + "(SELECT task_id FROM task_tree_task WHERE tree_id = ?), ?, ?, ?, ?, ?)";
        List<Object> args = Arrays.asList(nodeId, actualTreeId, parentId, actualTreeId, name, operator,
                operationTimeUtc, operator, operationTimeUtc);
        if (this.executor.executeUpdate(sql, args) < 1) {
            throw new ServerInternalException("Failed to save task node into database.");
        }
        if (!sourceIds.isEmpty()) {
            StringBuilder sourceSql = new StringBuilder();
            sourceSql.append("INSERT INTO task_node_source(id, node_id, source_id, created_by, created_at) VALUES");
            List<Object> sourceArgs = new ArrayList<>(sourceIds.size() * 5);
            for (String sourceId : sourceIds) {
                sourceSql.append("(?, ?, SELECT id FROM task_source WHERE id = ?, ?, ?), ");
                sourceArgs.addAll(Arrays.asList(Entities.generateId(), nodeId, sourceId, operator, operationTimeUtc));
            }
            sourceSql.setLength(sourceSql.length() - 2);
            this.executor.executeUpdate(sourceSql.toString(), sourceArgs);
        }
        NodeEntity entity = new NodeEntity();
        entity.setId(nodeId);
        entity.setParentId(Entities.ignoreEmpty(parentId));
        entity.setName(name);
        entity.setSourceIds(sourceIds);
        entity.setCreator(operator);
        entity.setCreationTime(operationTime);
        entity.setLastModifier(entity.getCreator());
        entity.setLastModificationTime(entity.getCreationTime());
        return entity;
    }

    private Set<String> nodeNames(String treeId) {
        if (StringUtils.isEmpty(treeId)) {
            return Collections.EMPTY_SET;
        }
        String sql = "SELECT id, tree_id, parent_id, name, created_by, created_at, updated_by, updated_at "
                + "FROM task_type WHERE tree_id = ?";
        List<Map<String, Object>> rows = this.executor.executeQuery(sql, Collections.singletonList(treeId));
        if (CollectionUtils.isEmpty(rows)) {
            return Collections.EMPTY_SET;
        }
        return rows.stream().map(obj -> ObjectUtils.<String>cast(obj.get("name"))).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void patch(String treeId, String nodeId, NodeDeclaration declaration, OperationContext context) {
        String id = Entities.validateId(nodeId, () -> new BadRequestException(ErrorCodes.NODE_ID_INVALID));
        String operator = context.operator();
        LocalDateTime operationTimeUtc = Dates.toUtc(LocalDateTime.now());
        StringBuilder sql = new StringBuilder();
        List<Object> args = new ArrayList<>(5);
        sql.append("UPDATE task_type SET updated_by = ?, updated_at = ?");
        args.addAll(Arrays.asList(operator, operationTimeUtc));
        if (declaration.getName().defined()) {
            String name = this.validator.name(declaration.getName().get(), this.nodeNames(treeId), context);
            sql.append(", name = ?");
            args.add(name);
        }
        if (declaration.getParentId().defined()) {
            String parentId = this.validator.parentId(declaration.getParentId().get(), context);
            sql.append(", parent_id = ?");
            args.add(parentId);
        }
        sql.append(" WHERE id = ?");
        args.add(id);
        if (this.executor.executeUpdate(sql.toString(), args) < 1) {
            throw new NotFoundException(ErrorCodes.NODE_NOT_FOUND);
        }
        if (declaration.getSourceIds().defined()) {
            this.acceptSources(id, declaration.getSourceIds().get(), operator, operationTimeUtc);
        }
    }

    private void acceptSources(String nodeId, List<String> sourceIds, String operator, LocalDateTime operationTimeUtc) {
        if (CollectionUtils.isEmpty(sourceIds)) {
            this.executor.executeUpdate("DELETE FROM task_node_source WHERE node_id = ?",
                    Collections.singletonList(nodeId));
            return;
        }
        StringBuilder sql = new StringBuilder();
        List<Object> args = new LinkedList<>();
        sql.append("INSERT INTO task_node_source(id, node_id, source_id, created_by, created_at) VALUES");
        for (String sourceId : sourceIds) {
            String actualSourceId = Entities.validateId(sourceId,
                    () -> new BadRequestException(ErrorCodes.SOURCE_SOURCE_INVALID));
            sql.append("(?, ?, (SELECT id FROM task_source WHERE id = ?), ?, ?), ");
            args.addAll(Arrays.asList(Entities.generateId(), nodeId, actualSourceId, operator, operationTimeUtc));
        }
        sql.setLength(sql.length() - 2);
        sql.append(" ON CONFLICT (node_id, source_id) DO UPDATE SET node_id = EXCLUDED.node_id RETURNING id");
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        List<String> ids = rows.stream().map(row ->
                ObjectUtils.<String>cast(row.get("id"))).collect(Collectors.toList());

        StringBuilder deleteSql = new StringBuilder();
        deleteSql.append("DELETE FROM task_node_source WHERE node_id = ? AND id NOT IN (?");
        for (int i = 1; i < ids.size(); i++) {
            deleteSql.append(", ?");
        }
        deleteSql.append(')');
        List<Object> deleteArgs = new ArrayList<>(ids.size() + 1);
        deleteArgs.add(nodeId);
        deleteArgs.addAll(ids);
        this.executor.executeUpdate(deleteSql.toString(), deleteArgs);
    }

    @Override
    @Transactional
    public void delete(String treeId, String nodeId, OperationContext context) {
        String id = Entities.validateId(nodeId, () -> new BadRequestException(ErrorCodes.NODE_ID_INVALID));
        if (this.countSources(nodeId) > 0) {
            throw new ConflictException(ErrorCodes.NODE_DELETING_HAS_SOURCES);
        }
        if (this.countChildren(nodeId) > 0) {
            throw new ConflictException(ErrorCodes.NODE_DELETING_HAS_NODES);
        }
        String sql = "DELETE FROM task_type WHERE id = ?";
        if (this.executor.executeUpdate(sql, Collections.singletonList(id)) < 1) {
            throw new NotFoundException(ErrorCodes.NODE_NOT_FOUND);
        }
    }

    private long countSources(String nodeId) {
        String sql = "SELECT COUNT(1) FROM task_node_source WHERE node_id = ?";
        List<Object> args = Collections.singletonList(nodeId);
        return longValue(this.executor.executeScalar(sql, args));
    }

    private long countChildren(String nodeId) {
        String sql = "SELECT COUNT(1) FROM task_type WHERE parent_id = ?";
        List<Object> args = Collections.singletonList(nodeId);
        return longValue(this.executor.executeScalar(sql, args));
    }

    @Override
    @Transactional
    public void delete(String treeId, NodeFilter filter, OperationContext context) {
        String actualTreeId = Entities.validateId(treeId, () -> new BadRequestException(ErrorCodes.TREE_ID_INVALID));
        StringBuilder whereSql = new StringBuilder();
        List<Object> whereArgs = new LinkedList<>();
        if (filter.getIds().defined()) {
            List<String> ids = nullIf(filter.getIds().get(), Collections.emptyList());
            ids = ids.stream().filter(Entities::isId).collect(Collectors.toList());
            if (ids.isEmpty()) {
                return;
            }
            Sqls.andIn(whereSql, "id", ids.size());
            whereArgs.addAll(ids);
        }
        if (filter.getSourceIds().defined()) {
            List<String> sourceIds = nullIf(filter.getSourceIds().get(), Collections.emptyList());
            sourceIds = sourceIds.stream().filter(Entities::isId).collect(Collectors.toList());
            if (sourceIds.isEmpty()) {
                return;
            }
            Sqls.andIn(whereSql, "source_id", sourceIds.size());
            whereArgs.addAll(sourceIds);
        }
        if (filter.getParentIds().defined()) {
            List<String> parentIds = nullIf(filter.getParentIds().get(), Collections.emptyList());
            parentIds = parentIds.stream().filter(Entities::isId).collect(Collectors.toList());
            if (parentIds.isEmpty()) {
                return;
            }
            Sqls.andIn(whereSql, "parent_id", parentIds.size());
            whereArgs.addAll(parentIds);
        }
        if (filter.getNames().defined()) {
            List<String> names = nullIf(filter.getNames().get(), Collections.emptyList());
            if (names.isEmpty()) {
                return;
            }
            Sqls.andLikeAny(whereSql, "name", names.size());
            whereArgs.addAll(names);
        }
        sqlSplicingExecution(whereSql, actualTreeId, whereArgs);
    }

    private void sqlSplicingExecution(StringBuilder whereSql, String actualTreeId, List<Object> whereArgs) {
        if (whereSql.length() < 1) {
            String sql = "SELECT COUNT(1) FROM task_node_source AS ns INNER JOIN task_type n ON n.id = ns.node_id "
                    + "INNER JOIN task_tree_v2 t ON t.id = n.tree_id WHERE t.id = ?";
            List<Object> args = Collections.singletonList(actualTreeId);
            long count = longValue(this.executor.executeScalar(sql, args));
            if (count > 0) {
                throw new ConflictException(ErrorCodes.NODE_DELETING_HAS_SOURCES);
            }
            this.executor.executeUpdate("DELETE FROM task_type WHERE tree_id = ?", args);
        } else {
            String sql = "SELECT COUNT(1) FROM task_node_source AS ns, (SELECT id FROM task_type WHERE "
                    + whereSql.substring(5) + ") AS nid WHERE ns.node_id = nid.id";
            long count = longValue(this.executor.executeScalar(sql, whereArgs));
            if (count > 0) {
                throw new ConflictException(ErrorCodes.NODE_DELETING_HAS_SOURCES);
            }
            String deleteSql = "DELETE FROM task_type WHERE tree_id = ?" + whereSql;
            List<Object> deleteArgs = new ArrayList<>(whereArgs.size() + 1);
            deleteArgs.add(actualTreeId);
            deleteArgs.addAll(whereArgs);
            this.executor.executeUpdate(deleteSql, deleteArgs);
        }
    }

    @Override
    @Transactional
    public NodeEntity retrieve(String treeId, String nodeId, OperationContext context) {
        String id = Entities.validateId(nodeId, () -> new BadRequestException(ErrorCodes.NODE_ID_INVALID,
                ParamUtils.convertOperationContext(context)));
        treeId = Entities.validateId(treeId, () -> new BadRequestException(ErrorCodes.TREE_ID_INVALID,
                ParamUtils.convertOperationContext(context)));
        String sql = "SELECT id, tree_id, parent_id, name, created_by, created_at, updated_by, updated_at "
                + "FROM task_type WHERE id = ? AND tree_id = ?";
        List<Map<String, Object>> rows = this.executor.executeQuery(sql, Arrays.asList(id, treeId));
        if (rows.isEmpty()) {
            throw new NotFoundException(ErrorCodes.NODE_NOT_FOUND, ParamUtils.convertOperationContext(context));
        } else {
            NodeEntity entity = toEntity(rows.get(0));
            Map<String, List<String>> sources = this.listSourcesOfNodes(Collections.singletonList(entity.getId()));
            entity.setSourceIds(nullIf(sources.get(entity.getId()), Collections.emptyList()));
            return entity;
        }
    }

    @Override
    @Transactional
    public RangedResultSet<NodeEntity> list(String treeId, NodeFilter filter, long offset, int limit,
            OperationContext context) {
        treeId = Entities.validateId(treeId, () -> new BadRequestException(ErrorCodes.TREE_ID_INVALID,
                ParamUtils.convertOperationContext(context)));
        StringBuilder whereSql = new StringBuilder();
        List<Object> whereArgs = new LinkedList<>();
        whereSql.append(" WHERE 1 = 1");
        RangedResultSet<NodeEntity> result = this.fillWhereFromFilter(filter, offset, limit, whereSql, whereArgs);
        if (result != null) {
            return result;
        }

        String countSql = "SELECT COUNT(1) FROM task_type" + whereSql + " AND tree_id = ?";
        whereArgs.add(treeId);
        long count = longValue(this.executor.executeScalar(countSql, whereArgs));

        String sql = "SELECT id, tree_id, parent_id, name, created_by, created_at, updated_by, updated_at "
                + "FROM task_type" + whereSql + " AND tree_id = ? ORDER BY created_at OFFSET ? LIMIT ?";
        List<Object> args = new ArrayList<>(whereArgs.size() + 3);
        args.addAll(whereArgs);
        args.addAll(Arrays.asList(offset, limit));
        List<Map<String, Object>> rows = this.executor.executeQuery(sql, args);
        List<NodeEntity> entities = rows.stream().map(NodeServiceImpl::toEntity).collect(Collectors.toList());

        List<String> nodeIds = entities.stream().map(NodeEntity::getId).collect(Collectors.toList());
        Map<String, List<String>> sources = this.listSourcesOfNodes(nodeIds);
        entities.forEach(entity -> entity.setSourceIds(nullIf(sources.get(entity.getId()), Collections.emptyList())));
        if (nodeIds.size() == 0) {
            return RangedResultSet.create(entities, (int) offset, limit, (int) count);
        }
        List<Map<String, Object>> childCountRows = ExecutableSql.resolve(
                "SELECT parent_id, count(1) AS num FROM task_type WHERE parent_id IN (${nodeIds}) GROUP BY parent_id",
                Collections.singletonMap("nodeIds", nodeIds)).executeQuery(executor);
        for (NodeEntity entity : entities) {
            for (Map<String, Object> row : childCountRows) {
                if (row.get("parent_id").toString().equals(entity.getId())) {
                    entity.setChildCount(ObjectUtils.cast(row.get("num")));
                    break;
                }
            }
        }

        return RangedResultSet.create(entities, (int) offset, limit, (int) count);
    }

    private RangedResultSet<NodeEntity> fillWhereFromFilter(NodeFilter filter, long offset, int limit,
            StringBuilder whereSql, List<Object> whereArgs) {
        if (filter.getIds().defined()) {
            List<String> ids = nullIf(filter.getIds().get(), Collections.emptyList());
            if (ids.isEmpty()) {
                return Entities.emptyRangedResultSet(offset, limit);
            }
            Sqls.andIn(whereSql, "id", ids.size());
            whereArgs.addAll(ids);
        }
        if (filter.getSourceIds().defined()) {
            List<String> sourceIds = nullIf(filter.getSourceIds().get(), Collections.emptyList());
            if (sourceIds.isEmpty()) {
                return Entities.emptyRangedResultSet(offset, limit);
            }
            Sqls.andIn(whereSql, "source_id", sourceIds.size());
            whereArgs.addAll(sourceIds);
        }
        if (filter.getParentIds().defined()) {
            List<String> parentIds = nullIf(filter.getParentIds().get(), Collections.emptyList());
            if (parentIds.isEmpty()) {
                return Entities.emptyRangedResultSet(offset, limit);
            }
            Sqls.andIn(whereSql, "parent_id", parentIds.size());
            whereArgs.addAll(parentIds);
        }
        if (filter.getNames().defined()) {
            List<String> names = nullIf(filter.getNames().get(), Collections.emptyList());
            if (!names.isEmpty()) {
                Sqls.andLikeAny(whereSql, "name", names.size());
                whereArgs.addAll(names.stream().map(Sqls::escapeLikeValue).collect(Collectors.toList()));
            }
        }
        return null;
    }

    @Override
    public Map<String, List<TextStringValue>> findChild(List<String> nodeIds) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return Collections.emptyMap();
        }
        StringBuilder sqlSb = new StringBuilder();
        List<Object> args = new ArrayList<>(nodeIds);
        sqlSb.append("SELECT id, parent_id, name FROM task_type WHERE parent_id IN (?");
        for (int i = 1; i < nodeIds.size(); i++) {
            sqlSb.append(", ?");
        }
        sqlSb.append(")");
        List<Map<String, Object>> result = executor.executeQuery(sqlSb.toString(), args);
        return result.stream()
                .collect(Collectors.groupingBy(map -> map.get("parent_id").toString(),
                        Collectors.mapping(this::convert, Collectors.toList())));
    }

    private TextStringValue convert(Map<String, Object> childInfoMap) {
        return TextStringValue.builder()
                .text(childInfoMap.get("name").toString())
                .value(childInfoMap.get("id").toString())
                .build();
    }

    private Map<String, List<String>> listSourcesOfNodes(List<String> nodeIds) {
        if (nodeIds.isEmpty()) {
            return Collections.emptyMap();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT node_id, source_id FROM task_node_source WHERE 1 = 1");
        Sqls.andIn(sql, "node_id", nodeIds.size());
        List<Object> args = new ArrayList<>(nodeIds.size());
        args.addAll(nodeIds);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        return rows.stream()
                .collect(Collectors.groupingBy(row -> ObjectUtils.cast(row.get("node_id")),
                        Collectors.mapping(row -> ObjectUtils.cast(row.get("source_id")), Collectors.toList())));
    }
}