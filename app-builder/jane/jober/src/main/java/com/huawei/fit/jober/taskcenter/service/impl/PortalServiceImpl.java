/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static com.huawei.fit.jane.task.util.UndefinableValue.withDefault;
import static com.huawei.fit.jober.common.ErrorCodes.FAILED_TO_GET_THREAD_RESULT;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.taskcenter.declaration.NodeDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TreeDeclaration;
import com.huawei.fit.jober.taskcenter.domain.CategoryEntity;
import com.huawei.fit.jober.taskcenter.domain.Index;
import com.huawei.fit.jober.taskcenter.domain.NodeEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceType;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.domain.TreeEntity;
import com.huawei.fit.jober.taskcenter.domain.ViewMode;
import com.huawei.fit.jober.taskcenter.domain.portal.TaskNode;
import com.huawei.fit.jober.taskcenter.domain.portal.TaskNodeType;
import com.huawei.fit.jober.taskcenter.filter.TaskFilter;
import com.huawei.fit.jober.taskcenter.filter.TaskTemplateFilter;
import com.huawei.fit.jober.taskcenter.service.CategoryService;
import com.huawei.fit.jober.taskcenter.service.NodeService;
import com.huawei.fit.jober.taskcenter.service.PortalService;
import com.huawei.fit.jober.taskcenter.service.SourceService;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.service.TreeService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link PortalService} 的默认实现类。
 *
 * @author 陈镕希
 * @since 2023-08-17
 */
@Component
@RequiredArgsConstructor
public class PortalServiceImpl implements PortalService {
    private static final Logger log = Logger.get(PortalServiceImpl.class);

    private static final ThreadPoolExecutor EXECUTOR =
            new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    private static final String TASK_TEMPLATE_NAME = "普通任务";

    private static final String INDEX_NAME = "portal-default-index";

    private final TreeService treeService;

    private final NodeService nodeService;

    private final TaskService taskService;

    private final TaskType.Repo taskTypeRepo;

    private final SourceService sourceService;

    private final DynamicSqlExecutor executor;

    private final TaskProperty.Repo taskPropertyRepo;

    private final TaskInstance.Repo taskInstanceRepo;

    private final CategoryService categoryService;

    private final TaskTemplate.Repo taskTemplateRepo;

    private final Index.Repo indexRepo;

    private Optional<TaskTemplate> loadTaskTemplate(OperationContext context) {
        TaskTemplateFilter filter = new TaskTemplateFilter();
        filter.setIds(UndefinableValue.undefined());
        filter.setNames(UndefinableValue.defined(Collections.singletonList(TASK_TEMPLATE_NAME)));
        RangedResultSet<TaskTemplate> results = this.taskTemplateRepo.list(filter, 0, 10, context);
        for (TaskTemplate current : results.getResults()) {
            if (StringUtils.equals(current.name(), TASK_TEMPLATE_NAME)) {
                return Optional.ofNullable(current);
            }
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public TaskEntity createTask(String treeId, TaskDeclaration declaration, OperationContext context) {
        TaskEntity taskEntity = taskService.create(declaration, context);
        TreeDeclaration treeDeclaration = new TreeDeclaration();
        treeDeclaration.setTaskId(UndefinableValue.defined(taskEntity.getId()));
        treeDeclaration.setName(UndefinableValue.undefined());
        treeService.patch(treeId, treeDeclaration, context);
        return taskEntity;
    }

    @Override
    @Transactional
    public SourceEntity createSource(String treeId, String nodeId, SourceDeclaration declaration,
            OperationContext context) {
        TreeEntity treeEntity = treeService.retrieve(treeId, context);
        SourceEntity sourceEntity = sourceService.create(treeEntity.getTaskId(), nodeId, declaration, context);
        NodeEntity nodeEntity = nodeService.retrieve(treeId, nodeId, context);
        Set<String> sourceIds = new HashSet<>(nodeEntity.getSourceIds().size() + 1);
        sourceIds.addAll(nodeEntity.getSourceIds());
        sourceIds.add(sourceEntity.getId());
        NodeDeclaration nodeDeclaration = new NodeDeclaration();
        nodeDeclaration.setSourceIds(UndefinableValue.defined(new ArrayList<>(sourceIds)));
        nodeDeclaration.setName(UndefinableValue.undefined());
        nodeDeclaration.setParentId(UndefinableValue.undefined());
        nodeService.patch(treeId, nodeId, nodeDeclaration, context);
        return sourceEntity;
    }

    @Override
    public RangedResultSet<SourceEntity> listSource(String treeId, String nodeId, long offset, int limit,
            OperationContext context) {
        List<String> sourceIds = nodeService.retrieve(treeId, nodeId, context).getSourceIds();
        return sourceService.listBySourceIds(sourceIds, offset, limit, context);
    }

    @Override
    public List<TagCountEntity> count(List<String> owners, List<String> creators, List<String> tags,
            List<String> taskIds, OperationContext context) {
        List<Callable<Integer>> tasks = Arrays.asList(
                () -> {
                    List<String> categories = Collections.singletonList("未开始");
                    return this.count(owners, creators, tags, categories, taskIds, context);
                },
                () -> {
                    List<String> categories = Collections.singletonList("处理中");
                    return this.count(owners, creators, tags, categories, taskIds, context);
                },
                () -> {
                    List<String> categories = Arrays.asList("未开始", "处理中", "风险");
                    return this.count(owners, creators, tags, categories, taskIds, context);
                }
        );

        List<Future<Integer>> futures;
        AtomicLong nonStarted = new AtomicLong();
        AtomicLong processing = new AtomicLong();
        AtomicLong risk = new AtomicLong();
        try {
            futures = EXECUTOR.invokeAll(tasks);

            for (int i = 0; i < futures.size(); i++) {
                Future<Integer> future = futures.get(i);
                int count = future.get();
                if (i == 0) {
                    nonStarted.set(count);
                } else if (i == 1) {
                    processing.set(count);
                } else if (i == 2) {
                    risk.set(count);
                } else {
                    throw new IllegalStateException();
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            log.error("Failed to get thread result: {}", ex.getMessage());
            log.debug("details: ", ex);
            throw new JobberException(FAILED_TO_GET_THREAD_RESULT);
        }
        return Arrays.asList(new TagCountEntity("未开始", nonStarted.get()),
                new TagCountEntity("处理中", processing.get()),
                new TagCountEntity("风险", risk.get()));
    }

    private int count(List<String> owners, List<String> creators, List<String> tags, List<String> categories,
            List<String> taskIds, OperationContext context) {
        List<TaskGroup> groups = this.listTaskGroups(owners, creators, tags, categories, taskIds, context);
        return groups.stream().mapToInt(TaskGroup::getNumberOfTasks).sum();
    }

    @Override
    public List<TaskGroup> listTaskGroups(List<String> owners, List<String> creators, List<String> tags,
            List<String> categories, List<String> taskIds, OperationContext context) {
        List<String> actualOwners = canonicalize(owners);
        List<String> actualCreators = canonicalize(creators);
        if (actualOwners.isEmpty() && actualCreators.isEmpty()) {
            throw new IllegalArgumentException("The owner and creator of task cannot be both empty.");
        }
        TaskTemplate defaultTemplate = this.loadTaskTemplate(context).orElse(null);
        if (defaultTemplate == null) {
            throw new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_NOT_FOUND);
        }
        List<String> actualTags = canonicalize(tags);
        List<String> actualCategories = canonicalize(categories);
        SqlBuilder selectSql = SqlBuilder.custom();
        selectSql.append("SELECT t.id AS task_id, t.name AS task_name, COUNT(ins.id) AS task_count"
                + " FROM task_instance_wide AS ins INNER JOIN task AS t ON t.id = ins.task_id");
        SqlBuilder whereSql = SqlBuilder.custom().append(" WHERE t.template_id = ?");
        List<Object> args = new LinkedList<>();
        args.add(defaultTemplate.id());
        this.appendCategoryCondition(actualCategories, selectSql, whereSql, args);
        appendLikeAny(whereSql, args, defaultTemplate.property("owner").column(), actualOwners);
        appendLikeAny(whereSql, args, defaultTemplate.property("created_by").column(), actualCreators);
        if (!actualTags.isEmpty()) {
            whereSql.append(" AND ins.id IN (SELECT tu.object_id FROM tag_usage AS tu "
                    + "INNER JOIN tag AS t ON t.id = tu.tag_id WHERE t.name IN (");
            whereSql.appendRepeatedly("?, ", actualTags.size()).backspace(2);
            whereSql.append(") AND tu.object_type = 'INSTANCE')");
            args.addAll(actualTags);
        }
        SqlBuilder sql = SqlBuilder.custom();
        sql.append(selectSql.toString());
        sql.append(whereSql.toString());
        if (!taskIds.isEmpty()) {
            sql.append(" AND ins.task_id IN (");
            sql.appendRepeatedly("?, ", taskIds.size()).backspace(2);
            sql.append(") ");
            args.addAll(taskIds);
        }
        sql.append(" GROUP BY t.id, t.name order by t.created_at");
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        List<TaskGroup> groups = rows.stream().map(row -> {
            TaskGroup group = new TaskGroup();
            group.setTaskId(cast(row.get("task_id")));
            group.setTreeId(group.getTaskId());
            group.setTreeName(cast(row.get("task_name")));
            group.setNumberOfTasks((ObjectUtils.<Number>cast(row.get("task_count")).intValue()));
            return group;
        }).collect(Collectors.toCollection(ArrayList::new));
        groups.addAll(this.countRefreshInTime(owners, creators, tags, categories, taskIds, context));
        return groups;
    }

    private void appendCategoryCondition(List<String> categories, SqlBuilder selectSql, SqlBuilder whereSql,
            List<Object> args) {
        int categoryIndex = 0;
        List<CategoryEntity> categoryEntities = this.categoryService.listByNames(categories);
        Map<String, List<String>> groupedCategoryIds = categoryEntities.stream()
                .collect(Collectors.groupingBy(CategoryEntity::getGroup,
                        Collectors.mapping(CategoryEntity::getId, Collectors.toList())));
        for (List<String> categoryIds : groupedCategoryIds.values()) {
            categoryIndex++;
            selectSql.append(StringUtils.format(
                    " INNER JOIN category_usage AS cu{0} ON cu{0}.object_id = ins.id AND cu{0}.object_type = "
                            + "'INSTANCE'",
                    categoryIndex));
            whereSql.append(StringUtils.format(" AND cu{0}.category_id IN (", categoryIndex));
            whereSql.appendRepeatedly("?, ", categoryIds.size()).backspace(2).append(')');
            args.addAll(categoryIds);
        }
    }

    private static void appendLikeAny(SqlBuilder sql, List<Object> args, String column, List<String> values) {
        if (values.isEmpty()) {
            return;
        }
        if (column.contains("list_text")) {
            if (values.size() == 1) {
                sql.append(" AND ins.id IN (SELECT DISTINCT instance_id FROM list_text WHERE value LIKE ? ESCAPE "
                        + "'\\') ");
                args.add("%" + values.get(0).replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%");
                return;
            }
            sql.append(" AND ins.id IN (SELECT DISTINCT instance_id FROM list_text WHERE ");
            for (String value : values) {
                sql.append("value LIKE ? ESCAPE '\\' OR ");
                args.add("%" + value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%");
            }
            sql.backspace(4).append(')');
        } else {
            if (values.size() == 1) {
                sql.append(" AND ins.").append(column).append(" LIKE ? ESCAPE '\\'");
                args.add("%" + values.get(0).replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%");
                return;
            }
            sql.append(" AND (");
            for (String value : values) {
                sql.append("ins.").append(column).append(" LIKE ? ESCAPE '\\' OR ");
                args.add("%" + value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%");
            }
            sql.backspace(4).append(')');
        }
    }

    private List<TaskGroup> countRefreshInTime(List<String> owners, List<String> creators, List<String> tags,
            List<String> categories, List<String> taskIds, OperationContext context) {
        List<String> actualOwners = canonicalize(owners);
        List<String> actualCreators = canonicalize(creators);
        List<String> propertyNames = new ArrayList<>(2);
        Map<String, List<String>> infos = new HashMap<>();
        if (!actualOwners.isEmpty()) {
            propertyNames.add("owner");
            infos.put("owner", actualOwners);
        }
        if (!actualCreators.isEmpty()) {
            propertyNames.add("created_by");
            infos.put("created_by", actualCreators);
        }
        if (propertyNames.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> rows = this.lookupTasksByProperty(propertyNames);
        TaskInstance.Filter filter = buildFilterForInstance(infos, tags, categories);
        List<TaskGroup> groups = new ArrayList<>(rows.size());
        Map<String, TaskEntity> tasks = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String taskId = cast(row.get("task_id"));
            if (CollectionUtils.isNotEmpty(taskIds) && !taskIds.contains(taskId)) {
                continue;
            }
            String taskName = cast(row.get("task_name"));
            TaskEntity task = tasks.computeIfAbsent(taskId, key -> this.taskService.retrieve(taskId, context));
            long count;
            try {
                count = this.taskInstanceRepo.list(task,
                        filter,
                        com.huawei.fit.jane.task.util.Pagination.create(0, 1),
                        Collections.emptyList(),
                        ViewMode.LIST,
                        context).pagination().total();
            } catch (JobberException exception) {
                log.warn("Failed to count task instances. [taskId={}, taskName={}, errorMsg={}]",
                        taskId,
                        taskName,
                        exception.getMessage());
                count = 0L;
            }
            if (count != 0) {
                groups.add(new TaskGroup(taskId, taskName, taskId, (int) count));
            }
        }
        return groups;
    }

    private List<Map<String, Object>> lookupTasksByProperty(List<String> propertyNames) {
        SqlBuilder sql = SqlBuilder.custom();
        sql.append("SELECT t.id AS task_id, t.name AS task_name ");
        sql.append("FROM task AS t ");
        sql.append("INNER JOIN task_property AS tp ON tp.task_id = t.id ");
        sql.append("INNER JOIN task_source AS ts ON ts.task_id = t.id ");
        sql.append("WHERE tp.name IN (").appendRepeatedly("?, ", propertyNames.size()).backspace(2);
        sql.append(") AND ts.type = ?");
        List<Object> args = new ArrayList<>(propertyNames.size() + 1);
        args.addAll(propertyNames);
        args.add(Enums.toString(SourceType.REFRESH_IN_TIME));
        return this.executor.executeQuery(sql.toString(), args);
    }

    private static List<String> canonicalize(List<String> values) {
        return Optional.ofNullable(values)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

    private static UndefinableValue<List<String>> define(List<String> values) {
        List<String> actual = canonicalize(values);
        if (actual.isEmpty()) {
            return UndefinableValue.undefined();
        } else {
            return UndefinableValue.defined(actual);
        }
    }

    private static TaskInstance.Filter buildFilterForInstance(Map<String, List<String>> infos, List<String> tags,
            List<String> categories) {
        return TaskInstance.Filter.custom().infos(infos).tags(tags).categories(categories).build();
    }

    private static final class InstanceCount {
        private final String taskId;

        private final String taskName;

        private final long instanceCount;

        private InstanceCount(String taskId, String taskName, long instanceCount) {
            this.taskId = taskId;
            this.taskName = taskName;
            this.instanceCount = instanceCount;
        }

        String taskId() {
            return this.taskId;
        }

        String taskName() {
            return this.taskName;
        }

        long instanceCount() {
            return this.instanceCount;
        }
    }

    @Override
    public List<TaskNode> getTree(OperationContext context) {
        long offset = 0L;
        List<TaskNode> nodes = new LinkedList<>();
        RangedResultSet<TaskEntity> results;
        do {
            results = this.taskService.list(new TaskFilter(), offset, 20, context);
            nodes.addAll(results.getResults().stream().map(this::toTaskNode).collect(Collectors.toList()));
            offset += results.getResults().size();
        } while (results.getRange().getOffset() + results.getResults().size() < results.getRange().getTotal());
        return nodes;
    }

    private TaskNode toTaskNode(TaskEntity task) {
        return TaskNode.custom()
                .id(task.getId())
                .name(task.getName())
                .type(TaskNodeType.TASK)
                .children(task.getTypes().stream().map(this::toTaskNode).collect(Collectors.toList()))
                .build();
    }

    private TaskNode toTaskNode(TaskType type) {
        return TaskNode.custom()
                .id(type.id())
                .name(type.name())
                .type(TaskNodeType.TASK_TYPE)
                .children(type.children().stream().map(this::toTaskNode).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public TaskEntity createTask(TaskDeclaration declaration, OperationContext context) {
        List<TaskProperty.Declaration> properties = withDefault(declaration.getProperties(), Collections.emptyList());
        List<Index.Declaration> indexes = withDefault(declaration.getIndexes(), Collections.emptyList());
        if (!properties.isEmpty() && indexes.isEmpty()) {
            List<String> propertyNames = indexablePropertyNames(properties);
            if (!propertyNames.isEmpty()) {
                Index.Declaration indexDeclaration =
                        Index.Declaration.custom().name(INDEX_NAME).propertyNames(propertyNames).build();
                declaration.setIndexes(UndefinableValue.defined(Collections.singletonList(indexDeclaration)));
            }
        }
        TaskEntity task = this.taskService.create(declaration, context);
        TreeDeclaration tree = new TreeDeclaration();
        tree.setName(UndefinableValue.defined(task.getName()));
        tree.setTaskId(UndefinableValue.defined(task.getId()));
        this.treeService.create(tree, context);
        return task;
    }

    private static List<String> indexablePropertyNames(List<TaskProperty.Declaration> properties) {
        return properties.stream()
                .filter(PortalServiceImpl::indexable)
                .map(TaskProperty.Declaration::name)
                .filter(Objects::nonNull)
                .filter(property -> property.defined())
                .map(UndefinableValue::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static boolean indexable(TaskProperty.Declaration declaration) {
        if (declaration == null) {
            return false;
        }
        if (indexable(withDefault(declaration.appearance(), null))) {
            return false;
        }
        PropertyDataType dataType;
        try {
            dataType = Enums.parse(PropertyDataType.class,
                    withDefault(declaration.dataType(), StringUtils.EMPTY),
                    PropertyDataType.TEXT,
                    ErrorCodes.TASK_PROPERTY_DATA_TYPE_INVALID);
        } catch (BadRequestException ignored) {
            return false;
        }
        return dataType.indexable();
    }

    private static boolean indexable(TaskProperty property) {
        return indexable(property.appearance());
    }

    private static boolean indexable(Map<String, Object> appearance) {
        return appearance != null && (enabled(appearance, "filterable") || enabled(appearance, "sortable"));
    }

    private static boolean enabled(Map<String, Object> values, String key) {
        Object value = values.get(key);
        return value instanceof Boolean && (boolean) value;
    }

    @Override
    @Transactional
    public void patchTask(String taskId, TaskDeclaration declaration, OperationContext context) {
        this.taskService.patch(taskId, declaration, context);
        String treeId;
        if (declaration.getName() != null && declaration.getName().defined()
                && (treeId = this.obtainTreeId(taskId)) != null) {
            TreeDeclaration tree = new TreeDeclaration();
            tree.setName(declaration.getName());
            tree.setTaskId(UndefinableValue.undefined());
            this.treeService.patch(treeId, tree, context);
        }
    }

    @Override
    @Transactional
    public void deleteTask(String taskId, OperationContext context) {
        this.taskService.delete(taskId, context);
        String sql = "DELETE FROM task_tree_task WHERE task_id = ? RETURNING tree_id";
        List<Object> args = Collections.singletonList(taskId);
        String treeId = ObjectUtils.cast(this.executor.executeScalar(sql, args));
        if (treeId != null) {
            sql = "DELETE FROM task_tree_v2 WHERE id = ?";
            args = Collections.singletonList(treeId);
            this.executor.executeUpdate(sql, args);
        }
    }

    /**
     * obtainTreeId
     *
     * @param taskId taskId
     * @return treeId
     * @deprecated 废弃不在使用
     */
    @Deprecated
    private String obtainTreeId(String taskId) {
        String sql = "SELECT tree_id FROM task_tree_task WHERE task_id = ?";
        List<Object> args = Collections.singletonList(taskId);
        return ObjectUtils.cast(this.executor.executeScalar(sql, args));
    }

    @Override
    @Transactional
    public TaskEntity retrieveTask(String taskId, OperationContext context) {
        return this.taskService.retrieve(taskId, context);
    }

    @Override
    @Transactional
    public TaskProperty createTaskProperty(String taskId, TaskProperty.Declaration declaration,
            OperationContext context) {
        TaskProperty property = this.taskPropertyRepo.create(taskId, declaration, context);
        if (indexable(property)) {
            TaskEntity task = this.taskService.retrieve(taskId, context);
            Index index = lookupIndex(task);
            if (index == null) {
                Index.Declaration indexDeclaration = this.buildIndexDeclaration(property);
                this.indexRepo.create(task, indexDeclaration, context);
            } else {
                List<String> propertyNames = new ArrayList<>(index.properties().size() + 1);
                propertyNames.addAll(index.properties().stream().map(TaskProperty::name).collect(Collectors.toList()));
                propertyNames.add(property.name());
                Index.Declaration indexDeclaration = Index.Declaration.custom().propertyNames(propertyNames).build();
                this.indexRepo.patch(task, index.id(), indexDeclaration, context);
            }
        }
        return property;
    }

    private static Index lookupIndex(TaskEntity task) {
        return Optional.ofNullable(task.getIndexes())
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .filter(index -> StringUtils.equalsIgnoreCase(index.name(), INDEX_NAME))
                .findAny()
                .orElse(null);
    }

    @Override
    @Transactional
    public void patchTaskProperty(String taskId, String propertyId, TaskProperty.Declaration declaration,
            OperationContext context) {
        TaskEntity task = null;
        TaskProperty property = null;
        boolean isIndexed = false;
        boolean isIndexable = false;
        if (declaration.appearance() != null && declaration.appearance().defined()) {
            task = this.taskService.retrieve(taskId, context);
            property = task.getPropertyById(Entities.canonicalizeId(propertyId));
            if (property != null) {
                isIndexed = indexable(property.appearance());
                isIndexable = indexable(declaration.appearance().withDefault(null));
            }
        }
        this.taskPropertyRepo.patch(taskId, propertyId, declaration, context);
        if (isIndexed == isIndexable) {
            return;
        }
        Index index = lookupIndex(task);
        if (isIndexable) {
            if (index == null) {
                Index.Declaration indexDeclaration = this.buildIndexDeclaration(property);
                this.indexRepo.create(task, indexDeclaration, context);
                return;
            }
            Set<String> propertyNames = index.properties()
                    .stream()
                    .map(TaskProperty::name)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (propertyNames.add(property.name())) {
                Index.Declaration indexDeclaration =
                        Index.Declaration.custom().propertyNames(new ArrayList<>(propertyNames)).build();
                this.indexRepo.patch(task, index.id(), indexDeclaration, context);
            }
        } else {
            if (index == null) {
                return;
            }
            String exceptedPropertyName = property.name();
            List<String> propertyNames = index.properties()
                    .stream()
                    .map(TaskProperty::name)
                    .filter(propertyName -> !StringUtils.equalsIgnoreCase(propertyName, exceptedPropertyName))
                    .collect(Collectors.toList());
            if (propertyNames.isEmpty()) {
                this.indexRepo.delete(task, index.id(), context);
            } else {
                Index.Declaration indexDeclaration = Index.Declaration.custom().propertyNames(propertyNames).build();
                this.indexRepo.patch(task, index.id(), indexDeclaration, context);
            }
        }
    }

    private Index.Declaration buildIndexDeclaration(TaskProperty property) {
        return Index.Declaration.custom()
                .name(INDEX_NAME)
                .propertyNames(Collections.singletonList(property.name()))
                .build();
    }

    @Override
    @Transactional
    public void patchProperties(String taskId, Map<String, TaskProperty.Declaration> declarations,
            OperationContext context) {
        for (Map.Entry<String, TaskProperty.Declaration> entry : declarations.entrySet()) {
            this.patchTaskProperty(taskId, entry.getKey(), entry.getValue(), context);
        }
    }

    @Override
    @Transactional
    public void deleteTaskProperty(String taskId, String propertyId, OperationContext context) {
        this.taskPropertyRepo.delete(taskId, propertyId, context);
    }

    @Override
    @Transactional
    public TaskType createTaskType(String taskId, TaskType.Declaration declaration, OperationContext context) {
        return this.taskTypeRepo.create(taskId, declaration, context);
    }

    @Override
    @Transactional
    public void patchTaskType(String taskId, String typeId, TaskType.Declaration declaration,
            OperationContext context) {
        this.taskTypeRepo.patch(taskId, typeId, declaration, context);
    }

    @Override
    @Transactional
    public void deleteTaskType(String taskId, String typeId, OperationContext context) {
        this.taskTypeRepo.delete(taskId, typeId, context);
    }

    @Override
    @Transactional
    public SourceEntity createTaskSource(String taskId, String typeId, SourceDeclaration declaration,
            OperationContext context) {
        return this.sourceService.create(taskId, typeId, declaration, context);
    }

    @Override
    @Transactional
    public void patchTaskSource(String taskId, String typeId, String sourceId, SourceDeclaration declaration,
            OperationContext context) {
        this.sourceService.patch(taskId, typeId, sourceId, declaration, context);
    }

    @Override
    @Transactional
    public void deleteTaskSource(String taskId, String typeId, String sourceId, OperationContext context) {
        this.sourceService.delete(taskId, typeId, sourceId, context);
    }

    @Override
    @Transactional
    public List<SourceEntity> listTaskSources(String taskId, String typeId, OperationContext context) {
        return this.sourceService.list(taskId, typeId, context);
    }
}
