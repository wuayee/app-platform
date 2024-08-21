/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import com.huawei.fit.jane.task.util.Dates;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.service.TagService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.validation.TagValidator;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transaction;
import modelengine.fitframework.transaction.TransactionException;
import modelengine.fitframework.transaction.TransactionIsolationLevel;
import modelengine.fitframework.transaction.TransactionManager;
import modelengine.fitframework.transaction.TransactionMetadata;
import modelengine.fitframework.transaction.TransactionPropagationPolicy;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link TagService} 提供实现。
 *
 * @author 梁济时
 * @since 2023-08-16
 */
@Component
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final DynamicSqlExecutor executor;

    private final TagValidator validator;

    private final TransactionManager transactions;

    private final Map<String, String> tagIds = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public void add(String objectType, String objectId, String tag, OperationContext context) {
        String operator = context.operator();
        LocalDateTime operationTime = Dates.toUtc(LocalDateTime.now());
        String tagId = this.identifyTag(tag, operator, operationTime);
        String sql = "INSERT INTO tag_usage(id, tag_id, object_type, object_id, created_by, created_at) "
                + "VALUES(?, ?, ?, ?, ?, ?) ON CONFLICT (tag_id, object_id, object_type) DO NOTHING";
        List<Object> args = Arrays.asList(Entities.generateId(), tagId, this.validator.objectType(objectType),
                this.validator.objectId(objectId), operator, operationTime);
        this.executor.executeUpdate(sql, args);
    }

    @Override
    @Transactional
    public void save(String objectType, String objectId, List<String> tags, OperationContext context) {
        this.save(objectType, Collections.singletonMap(objectId, tags), context);
    }

    private static List<Object> fillDeleteArgs(String objectType, Map<String, List<String>> tags,
            List<String> usageIds) {
        List<Object> args = new ArrayList<>(tags.size() + usageIds.size() + 1);
        args.addAll(tags.keySet());
        args.addAll(usageIds);
        args.add(objectType);
        return args;
    }

    private static void fillDeleteSql(Map<String, List<String>> tags, StringBuilder sql, List<String> usageIds) {
        sql.setLength(0);
        sql.append("DELETE FROM tag_usage WHERE object_id IN (?");
        for (int i = 1; i < tags.size(); i++) {
            sql.append(", ?");
        }
        if (CollectionUtils.isNotEmpty(usageIds)) {
            sql.append(") AND id NOT IN (?");
        }
        for (int i = 1; i < usageIds.size(); i++) {
            sql.append(", ?");
        }
        sql.append(") AND object_type = ?");
    }

    @Override
    @Transactional
    public void save(String objectType, Map<String, List<String>> tags, OperationContext context) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        List<String> tagNames = tags.values()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        String operator = context.operator();
        LocalDateTime operationTime = Dates.toUtc(LocalDateTime.now());
        Map<String, String> tagIdMappings = this.identifyTags(tagNames, operator, operationTime);
        StringBuilder sql = new StringBuilder(128);
        sql.append("INSERT INTO tag_usage(id, tag_id, object_type, object_id, created_by, created_at) VALUES");
        List<List<Object>> groupedArgs = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : tags.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            for (String tag : entry.getValue()) {
                List<Object> values = Arrays.asList(Entities.generateId(), tagIdMappings.get(tag), objectType,
                        entry.getKey(), operator, operationTime);
                groupedArgs.add(values);
                sql.append("(?, ?, ?, ?, ?, ?), ");
            }
        }
        sql.setLength(sql.length() - 2);
        sql.append(" ON CONFLICT (tag_id, object_id, object_type) "
                + "DO UPDATE SET object_type = EXCLUDED.object_type RETURNING id");
        List<Object> args = groupedArgs.stream().flatMap(Collection::stream).collect(Collectors.toList());
        List<String> usageIds = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(args)) {
            List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
            usageIds = rows.stream().map(row -> ObjectUtils.<String>cast(row.get("id"))).collect(Collectors.toList());
        }
        fillDeleteSql(tags, sql, usageIds);
        args = fillDeleteArgs(objectType, tags, usageIds);
        this.executor.executeUpdate(sql.toString(), args);
    }

    private String identifyTag(String tag, String operator, LocalDateTime operationTime) {
        return this.identifyTags(Collections.singletonList(tag), operator, operationTime).get(tag);
    }

    private Map<String, String> identifyTags(List<String> tags, String operator, LocalDateTime operationTime) {
        List<String> actualTags = Optional.ofNullable(tags)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (actualTags.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<String> unknownTags = CollectionUtils.difference(actualTags, this.tagIds.keySet());
        if (!unknownTags.isEmpty()) {
            Map<String, String> tagIdsMap = this.identifyTags0(unknownTags, operator, operationTime);
            this.tagIds.putAll(tagIdsMap);
        }
        return actualTags.stream().collect(Collectors.toMap(Function.identity(), this.tagIds::get));
    }

    private Map<String, String> identifyTags0(Collection<String> tags, String operator, LocalDateTime operationTime) {
        Map<String, String> ids;
        Transaction transaction = this.transactions.begin(TransactionMetadata.custom()
                .name("identify-tags")
                .propagation(TransactionPropagationPolicy.REQUIRES_NEW)
                .isolation(Optional.ofNullable(this.transactions.active())
                        .map(Transaction::metadata)
                        .map(TransactionMetadata::isolation)
                        .orElse(TransactionIsolationLevel.READ_UNCOMMITTED))
                .build());
        try {
            InsertSql sql = InsertSql.custom().into("tag");
            for (String tag : tags) {
                sql.next()
                        .value("id", Entities.generateId())
                        .value("name", tag)
                        .value("description", "")
                        .value("created_by", operator)
                        .value("created_at", operationTime)
                        .value("updated_by", operator)
                        .value("updated_at", operationTime);
            }
            sql.conflict("name").update("updated_by", "updated_at");
            List<Map<String, Object>> rows = sql.executeAndReturn(this.executor, "id", "name");
            ids = rows.stream()
                    .collect(Collectors.toMap(row -> ObjectUtils.cast(row.get("name")),
                            row -> ObjectUtils.cast(row.get("id"))));
            transaction.commit();
        } catch (ClassCastException | IllegalArgumentException | TransactionException t) {
            transaction.rollback();
            throw t;
        }
        return ids;
    }

    @Override
    @Transactional
    public void remove(String objectType, String objectId, String tag, OperationContext context) {
        String sql = "DELETE FROM tag_usage AS tu USING tag AS t WHERE tu.tag_id = t.id "
                + "AND t.name = ? AND tu.object_id = ? AND tu.object_type = ?";
        List<Object> args = Arrays.asList(this.validator.tag(tag), this.validator.objectId(objectId),
                this.validator.objectType(objectType));
        this.executor.executeUpdate(sql, args);
    }

    @Override
    public List<String> list(String objectType, String objectId, OperationContext context) {
        String sql = "SELECT t.name FROM tag_usage AS tu INNER JOIN tag AS t ON t.id = tu.tag_id "
                + "WHERE tu.object_id = ? AND tu.object_type = ?";
        List<Object> args = Arrays.asList(this.validator.objectId(objectId), this.validator.objectType(objectType));
        List<Map<String, Object>> rows = this.executor.executeQuery(sql, args);
        return rows.stream().map(row -> ObjectUtils.<String>cast(row.get("name"))).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> list(String objectType, List<String> objectIds, OperationContext context) {
        if (objectIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Object> args = new ArrayList<>(objectIds.size() + 1);
        args.addAll(objectIds.stream().map(this.validator::objectId).collect(Collectors.toList()));
        args.add(this.validator.objectType(objectType));
        StringBuilder sql = new StringBuilder(128);
        sql.append("SELECT tu.object_id, t.name FROM tag_usage AS tu INNER JOIN tag AS t ON t.id = tu.tag_id "
                + "WHERE tu.object_id IN (?");
        for (int i = 1; i < objectIds.size(); i++) {
            sql.append(", ?");
        }
        sql.append(") AND tu.object_type = ?");
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        return rows.stream()
                .collect(Collectors.groupingBy(row -> ObjectUtils.cast(row.get("object_id")),
                        Collectors.mapping(row -> ObjectUtils.cast(row.get("name")), Collectors.toList())));
    }

    /**
     * 根据标签查询到对应的flowId
     *
     * @param objectType 表示对象的类型的 {@link String}。
     * @param tags 标签
     * @return 使用了标签的flowGraph对应的id
     */
    @Override
    public List<String> list(String objectType, List<String> tags) {
        this.validator.objectType(objectType);
        StringBuilder sql = new StringBuilder(128);
        sql.append("SELECT tu.object_id FROM tag_usage AS tu JOIN tag AS t ON tu.tag_id = t.id WHERE t.name IN (?");
        for (int i = 1; i < tags.size(); i++) {
            sql.append(", ?");
        }
        sql.append(")");
        List<Object> args = new ArrayList<>(tags);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        return rows.stream().map(row -> ObjectUtils.<String>cast(row.get("object_id"))).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String identify(String tag, OperationContext context) {
        Map<String, String> identities = this.identify(Collections.singletonList(tag), context);
        return identities.get(tag);
    }

    @Override
    @Transactional
    public Map<String, String> identify(List<String> tags, OperationContext context) {
        return this.identifyTags(tags, context.operator(), LocalDateTime.now());
    }
}
