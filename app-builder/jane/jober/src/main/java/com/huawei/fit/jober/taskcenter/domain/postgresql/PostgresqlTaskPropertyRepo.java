/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static com.huawei.fit.jober.taskcenter.util.sql.Condition.expectEqual;
import static com.huawei.fit.jober.taskcenter.util.sql.SqlValue.json;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.PropertyCategoryDeclaration;
import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.PropertyScope;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyDeletingEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyModifiedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyModifyingEvent;
import com.huawei.fit.jober.taskcenter.service.CategoryService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.util.SequenceUtils;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.DeleteSql;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fit.jober.taskcenter.util.sql.SqlValue;
import com.huawei.fit.jober.taskcenter.util.sql.UpdateSql;
import com.huawei.fit.jober.taskcenter.validation.PropertyValidator;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.event.Event;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 为 {@link TaskProperty.Repo} 提供基于 Postgresql 的实现。
 *
 * @author 梁济时
 * @since 2023-10-25
 */
@Component
public class PostgresqlTaskPropertyRepo implements TaskProperty.Repo {
    private static final Logger log = Logger.get(PostgresqlTaskPropertyRepo.class);

    private final DynamicSqlExecutor executor;

    private final PropertyValidator validator;

    private final CategoryService categoryService;

    private final ObjectSerializer serializer;

    private final Plugin plugin;

    public PostgresqlTaskPropertyRepo(DynamicSqlExecutor executor, PropertyValidator validator,
            CategoryService categoryService, @Fit(alias = "json") ObjectSerializer serializer, Plugin plugin) {
        this.executor = executor;
        this.validator = validator;
        this.categoryService = categoryService;
        this.serializer = serializer;
        this.plugin = plugin;
    }

    @Override
    @Transactional
    public TaskProperty create(String taskId, TaskProperty.Declaration declaration, OperationContext context) {
        log.info("Start to create property. [taskId={}, declaration={}, context={}]",
                taskId, declaration, context);
        TaskProperty property = this.new Creator(taskId, declaration, context).create();
        log.info("Complete to create property. [propertyId={}]", property.id());
        return property;
    }

    @Override
    @Transactional
    public void patch(String taskId, String propertyId, TaskProperty.Declaration declaration,
            OperationContext context) {
        log.info("Start to patch property. [taskId={}, propertyId={}, declaration={}, context={}]",
                taskId, propertyId, declaration, context);
        this.new Patcher(taskId, propertyId, declaration, context).patch();
        log.info("Complete to patch property.");
    }

    @Override
    @Transactional
    public void delete(String taskId, String propertyId, OperationContext context) {
        log.info("Start to delete property. [taskId={}, propertyId={}, context={}]",
                taskId, propertyId, context);
        String actualTaskId = this.validator.validateTaskId(taskId, context);
        TaskProperty property = this.retrieve(actualTaskId, propertyId, context);
        if (this.hasInstances(actualTaskId)) {
            log.error("Cannot delete property of a task which has instances. [taskId={}, propertyId={}]",
                    actualTaskId, property.id());
            throw new ConflictException(ErrorCodes.PROPERTY_CANNOT_BE_DELETED_WITH_INSTANCES);
        }
        this.plugin.publisherOfEvents().publishEvent(new TaskPropertyDeletingEvent(this, property));
        DeleteSql sql = DeleteSql.custom().from(Row.TABLE_NAME).where(expectEqual(Row.COLUMN_ID, property.id()));
        if (sql.execute(PostgresqlTaskPropertyRepo.this.executor) < 1) {
            log.error("The property to delete does not exist. [taskId={}, propertyId={}]",
                    actualTaskId, property.id());
            throw new NotFoundException(ErrorCodes.TASK_PROPERTY_NOT_FOUND);
        }
        this.saveCategories(property.id(), Collections.emptyList());
        log.info("Complete to delete property.");
    }

    @Override
    @Transactional
    public void deleteByTask(String taskId, OperationContext context) {
        log.info("Start to delete properties of task. [taskId={}, context={}]", taskId, context);
        String actualTaskId = this.validator.validateTaskId(taskId, context);
        List<Row> rows = Row.selectByTask(this.executor, actualTaskId);
        this.fillCategories(rows);
        rows.forEach(row -> {
            TaskProperty property = toDomainObject(row);
            TaskPropertyDeletingEvent event = new TaskPropertyDeletingEvent(this, property);
            plugin.publisherOfEvents().publishEvent(event);
        });
        DeleteSql sql = DeleteSql.custom().from(Row.TABLE_NAME).where(expectEqual(Row.COLUMN_TASK_ID, actualTaskId));
        sql.execute(this.executor);
        log.info("Complete to delete properties of task.");
    }

    @Override
    @Transactional
    public TaskProperty retrieve(String taskId, String propertyId, OperationContext context) {
        log.info("Start to retrieve property. [taskId={}, propertyId={}, context={}]",
                taskId, propertyId, context);
        String actualTaskId = this.validator.validateTaskId(taskId, context);
        String actualPropertyId = this.validator.validatePropertyId(propertyId, context);
        Row row = Row.select(this.executor, actualTaskId, actualPropertyId);
        if (row == null) {
            throw new NotFoundException(ErrorCodes.TASK_PROPERTY_NOT_FOUND);
        }
        row.categories(this.getCategories(actualPropertyId));
        TaskProperty property = toDomainObject(row);
        log.info("Complete to retrieve property.");
        return property;
    }

    @Override
    @Transactional
    public List<TaskProperty> list(String taskId, OperationContext context) {
        return nullIf(this.list(Collections.singletonList(taskId), context).get(taskId), Collections.emptyList());
    }

    @Override
    @Transactional
    public Map<String, List<TaskProperty>> list(List<String> taskIds, OperationContext context) {
        log.info("Start to list properties of tasks. [taskIds={}, context={}]", taskIds, context);
        List<String> actualTaskIds = taskIds.stream().filter(Entities::isId).collect(Collectors.toList());
        if (actualTaskIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Row> rows = Row.selectByTasks(this.executor, actualTaskIds);
        this.fillCategories(rows);
        Map<String, List<TaskProperty>> properties = rows.stream().collect(Collectors.groupingBy(Row::taskId,
                Collectors.mapping(this::toDomainObject, Collectors.toList())));
        log.info("Complete to list properties of tasks. [properties.size={}]", rows.size());
        return properties;
    }

    private boolean hasInstances(String taskId) {
        String sql = "SELECT COUNT(1) FROM \"task_instance_wide\" WHERE \"task_id\" = ?";
        List<Object> args = Collections.singletonList(taskId);
        log.info("===== has instances =====");
        log.info("sql: {}", sql);
        log.info("args: {}", args);
        Object count = this.executor.executeScalar(sql, args);
        return (ObjectUtils.<Number>cast(count)).longValue() > 0;
    }

    private String serializeAppearance(Map<String, Object> appearance) {
        Charset charset = StandardCharsets.UTF_8;
        byte[] bytes = this.serializer.serialize(appearance, charset);
        return new String(bytes, charset);
    }

    private Map<String, Object> deserializeAppearance(String appearance) {
        Charset charset = StandardCharsets.UTF_8;
        byte[] bytes = appearance.getBytes(charset);
        Type type = TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class});
        Object result = this.serializer.deserialize(bytes, charset, type);
        return cast(result);
    }

    private TaskProperty toDomainObject(Row row) {
        return TaskProperty.custom()
                .id(row.id())
                .name(row.name())
                .dataType(Enums.parse(PropertyDataType.class, row.dataType()))
                .sequence(row.sequence())
                .description(row.description())
                .isRequired(row.required())
                .isIdentifiable(row.identifiable())
                .scope(Enums.parse(PropertyScope.class, row.scope()))
                .appearance(this.deserializeAppearance(row.appearance()))
                .categories(row.categories())
                .build();
    }

    /**
     * 获取属性类目
     *
     * @param propertyId 表示属性id的{@link String}
     * @return 属性类目匹配器
     */
    protected List<PropertyCategory> getCategories(String propertyId) {
        Map<String, List<PropertyCategory>> categories = this.getCategories(Collections.singletonList(propertyId));
        return nullIf(categories.get(propertyId), Collections.emptyList());
    }

    /**
     * 获取属性类目
     *
     * @param propertyIds 表示属性id的{@link List}
     * @return 属性类目匹配器
     */
    protected Map<String, List<PropertyCategory>> getCategories(List<String> propertyIds) {
        return categoryService.matchers(propertyIds);
    }

    /**
     * 保存类目
     *
     * @param propertyId propertyId 表示属性id的{@link String}
     * @param categories 表示属性类目声明的{@link List}{@code <}{@link PropertyCategoryDeclaration}{@code >}
     * @return 属性类目匹配器
     */
    protected List<PropertyCategory> saveCategories(String propertyId,
            List<PropertyCategoryDeclaration> categories) {
        Map<String, List<PropertyCategoryDeclaration>> values = Collections.singletonMap(propertyId, categories);
        Map<String, List<PropertyCategory>> results = this.categoryService.saveMatchers(values);
        return nullIf(results.get(propertyId), Collections.emptyList());
    }

    private void fillCategories(List<Row> rows) {
        List<String> propertyIds = rows.stream().map(Row::id).collect(Collectors.toList());
        Map<String, List<PropertyCategory>> categories = this.getCategories(propertyIds);
        rows.forEach(row -> row.categories(categories.get(row.id())));
    }

    private abstract class AbstractOperation {
        private final String taskId;

        private final OperationContext context;

        private final LazyLoader<List<Row>> rows;

        private final LazyLoader<Boolean> hasInstances;

        private final LazyLoader<List<TaskTemplateProperty>> templates;

        AbstractOperation(String taskId, OperationContext context) {
            this.taskId = PostgresqlTaskPropertyRepo.this.validator.validateTaskId(taskId, context);
            this.context = context;
            this.rows = new LazyLoader<>(this::getRows);
            this.hasInstances = new LazyLoader<>(this::computeHasInstances);
            this.templates = new LazyLoader<>(this::getTemplates);
        }

        protected final OperationContext context() {
            return this.context;
        }

        private boolean computeHasInstances() {
            return PostgresqlTaskPropertyRepo.this.hasInstances(this.taskId);
        }

        private List<Row> getRows() {
            List<Row> results = Row.selectByTask(PostgresqlTaskPropertyRepo.this.executor, this.taskId);
            fillCategories(results);
            return results;
        }

        private List<TaskTemplateProperty> getTemplates() {
            return Row.selectTemplateByTask(PostgresqlTaskPropertyRepo.this.executor, this.taskId);
        }

        protected final String taskId() {
            return this.taskId;
        }

        /**
         * 获得行
         *
         * @return 返回行数据
         */
        protected final List<Row> rows() {
            return this.rows.get();
        }

        /**
         * 获得指定属性id的行
         *
         * @param propertyId 表示属性ID的{@link String}
         * @return 返回对应id的行
         */
        protected final Row row(String propertyId) {
            return this.rows().stream()
                    .filter(row -> StringUtils.equalsIgnoreCase(row.id(), propertyId))
                    .findAny()
                    .orElse(null);
        }

        /**
         * 获取任务模板属性
         *
         * @return 返回任务模板属性列表
         */
        protected final List<TaskTemplateProperty> templates() {
            return this.templates.get();
        }

        /**
         * 是否存在实例
         *
         * @return 返回是否存在instance
         */
        protected final boolean hasInstances() {
            return this.hasInstances.get();
        }

        /**
         * 获得给定数据类型的序列的长度
         *
         * @param dataType 表示数据类型的{@link PropertyDataType}
         * @return 返回序列长度
         */
        protected int nextSequence(PropertyDataType dataType) {
            return this.nextSequence(Enums.toString(dataType));
        }

        /**
         * 获得给定数据类型的序列的长度
         *
         * @param dataType 表示数据类型的{@link String}
         * @return 返回序列长度
         */
        protected int nextSequence(String dataType) {
            List<Integer> rowSequences = this.rows()
                    .stream()
                    .filter(row -> StringUtils.equalsIgnoreCase(row.dataType(), dataType))
                    .map(Row::sequence)
                    .collect(Collectors.toList());

            Set<Integer> templateSequences = this.templates()
                    .stream()
                    .filter(t -> StringUtils.equalsIgnoreCase(Enums.toString(t.dataType()), dataType))
                    .map(TaskTemplateProperty::sequence)
                    .collect(Collectors.toSet());

            templateSequences.addAll(rowSequences);

            List<Integer> sequences = new ArrayList<>(templateSequences);
            Collections.sort(sequences);
            return SequenceUtils.getSequenceFromList(sequences);
        }

        /**
         * 发布事件
         *
         * @param event 表示待发布的事件的{@link Event}
         */
        protected void publishEvent(Event event) {
            plugin.publisherOfEvents().publishEvent(event);
        }
    }

    private class Creator extends AbstractOperation {
        TaskTemplateProperty templateProperty = null;
        private final TaskProperty.Declaration declaration;
        private String templateId = Entities.emptyId();

        Creator(String taskId, TaskProperty.Declaration declaration, OperationContext context) {
            super(taskId, context);
            this.declaration = declaration;
        }

        TaskProperty create() {
            log.info("Start to create task property. [taskId={}, declaration={}, context={}]",
                    this.taskId(), this.declaration, this.context());
            String propertyId = Entities.generateId();
            Row row = new Row();
            row.id(propertyId);
            row.taskId(this.taskId());
            this.acceptNameAndTemplateId(row);
            this.acceptDataType(row);
            this.acceptDescription(row);
            this.acceptRequired(row);
            this.acceptIdentifiable(row);
            this.acceptScope(row);
            this.acceptAppearance(row);
            row.insert(PostgresqlTaskPropertyRepo.this.executor);
            this.declaration.categories()
                    .ifDefined(categories -> row.categories(saveCategories(propertyId, categories)));
            return toDomainObject(row);
        }

        private void acceptNameAndTemplateId(Row row) {
            String name = validator.validateName(this.declaration.name()
                    .required(() -> new BadRequestException(ErrorCodes.PROPERTY_NAME_REQUIRED)), this.context());
            if (this.rows().stream().anyMatch(current -> StringUtils.equalsIgnoreCase(current.name(), name))) {
                log.error("A property with the same name already exists. [taskId={}, propertyName={}]",
                        this.taskId(), name);
                throw new ConflictException(ErrorCodes.TASK_PROPERTY_NAME_EXIST);
            }
            if (this.declaration.templateId().defined()) {
                this.templateId = this.declaration.templateId().get();
                this.templateProperty = this.templates()
                        .stream()
                        .filter(tp -> StringUtils.equalsIgnoreCase(tp.id(), this.templateId))
                        .findAny()
                        .orElseThrow(() -> new NotFoundException(ErrorCodes.TEMPLATE_PROPERTY_NOT_FOUND));
                if (!StringUtils.equalsIgnoreCase(name, this.templateProperty.name())) {
                    throw new ConflictException(ErrorCodes.TASK_PROPERTY_NAME_EXIST);
                }
            } else {
                this.templateProperty = this.templates()
                        .stream()
                        .filter(tp -> StringUtils.equalsIgnoreCase(tp.name(), name))
                        .findAny()
                        .orElse(null);
                if (Objects.nonNull(this.templateProperty)) {
                    this.templateId = templateProperty.id();
                } else {
                    this.templateId = Entities.emptyId();
                }
            }
            row.templateId(this.templateId);
            row.name(name);
        }

        private void acceptDataType(Row row) {
            String value = StringUtils.trim(this.declaration.dataType().withDefault(null));
            PropertyDataType dataType = Enums.parse(PropertyDataType.class, value, PropertyDataType.TEXT,
                    ErrorCodes.TASK_PROPERTY_DATA_TYPE_INVALID);

            row.dataType(Enums.toString(dataType));

            if (dataType.listable()) {
                row.sequence(0);   // 有效的 sequence 从 1 开始，列表类型的数据不保存在宽表中，sequence 置为 0。
            } else if (this.templateProperty != null) {
                // 如果使用了模板，而且模板的数据类型和期望使用的数据类型不一致，则会报错
                if (!dataType.equals(this.templateProperty.dataType())) {
                    throw new ConflictException(ErrorCodes.PROPERTY_DATA_TYPE_NOT_EQUALS_TEMPLATE);
                }
                row.sequence(templateProperty.sequence());
            } else {
                row.sequence(this.nextSequence(dataType));
            }
        }

        private void acceptDescription(Row row) {
            String value = PostgresqlTaskPropertyRepo.this.validator.validateDescription(this.declaration.description()
                    .map(StringUtils::trim).withDefault(StringUtils.EMPTY), this.context());
            row.description(value);
        }

        private void acceptRequired(Row row) {
            boolean value = this.declaration.required().withDefault(false);
            if (value && this.hasInstances()) {
                log.error(
                        "Cannot add a property which is required when the owning task has instances. "
                                + "[taskId={}, propertyName={}]",
                        this.taskId(), row.name());
                throw new ConflictException(ErrorCodes.NEW_PROPERTY_REQUIRED_WITH_INSTANCES);
            }
            row.required(value);
        }

        private void acceptIdentifiable(Row row) {
            row.identifiable(this.declaration.identifiable().withDefault(false));
        }

        private void acceptScope(Row row) {
            String value = StringUtils.trim(this.declaration.scope().withDefault(null));
            PropertyScope scope = Enums.parse(PropertyScope.class, value, PropertyScope.PUBLIC,
                    ErrorCodes.TASK_PROPERTY_SCOPE_INVALID);
            row.scope(Enums.toString(scope));
        }

        private void acceptAppearance(Row row) {
            Map<String, Object> appearance = this.declaration.appearance().withDefault(null);
            appearance = nullIf(appearance, Collections.emptyMap());
            String actual = PostgresqlTaskPropertyRepo.this.serializeAppearance(appearance);
            row.appearance(actual);
        }
    }

    private class Patcher extends AbstractOperation {
        TaskTemplateProperty templateProperty = null;
        private final String propertyId;
        private final TaskProperty.Declaration declaration;
        private Row property;
        private String templateId = Entities.emptyId();

        private Patcher(String taskId, String propertyId, TaskProperty.Declaration declaration,
                OperationContext context) {
            super(taskId, context);
            this.propertyId = validator.validatePropertyId(propertyId, context);
            this.declaration = declaration;
        }

        void patch() {
            this.property = this.row(this.propertyId);
            if (this.property == null) {
                log.error("The task property to patch does not exist. [taskId={}, propertyId={}]",
                        this.taskId(), propertyId);
                throw new NotFoundException(ErrorCodes.TASK_PROPERTY_NOT_FOUND);
            }
            Row newRow = new Row(new HashMap<>(this.property.values));
            this.loadTemplateProperty();
            UpdateSql sql = UpdateSql.custom().table(Row.TABLE_NAME);
            this.accept(newRow, sql);
            TaskProperty oldProperty = toDomainObject(this.property);
            this.publishEvent(new TaskPropertyModifyingEvent(this, oldProperty, this.declaration));
            sql.where(expectEqual(Row.COLUMN_ID, this.property.id()));
            sql.execute(executor);
            this.declaration.categories().ifDefined(
                    categories -> newRow.categories(saveCategories(this.propertyId, categories)));
            TaskProperty newProperty = toDomainObject(newRow);
            this.publishEvent(new TaskPropertyModifiedEvent(this, newProperty, oldProperty));
        }

        private void loadTemplateProperty() {
            // 场景定义：
            // 1、本来就存在模板ID，未输入模板ID，从模板属性列表中找到模板
            // 2、本来就存在模板ID，又输入模板ID，而且二者不同， 报错：不允许修改属性的模板
            // 3、本来不存在模板ID，未输入模板ID，do nothing，templateId和templateProperty为null
            // 4、本来不存在模板ID，输入模板ID，增加模板，名称、数据类型必须和模板一致
            this.templateId = this.property.templateId();  // 本来就存在的模板ID
            if (!StringUtils.equalsIgnoreCase(Entities.emptyId(), templateId)) {
                // 本来存在模板ID
                this.templateProperty = this.templates()
                        .stream()
                        .filter(tp -> StringUtils.equalsIgnoreCase(tp.id(), this.templateId))
                        .findAny()
                        .orElseThrow(() -> new NotFoundException(ErrorCodes.TEMPLATE_PROPERTY_NOT_FOUND));
                if (this.declaration.templateId().defined()) {
                    String declarationTemplateId = this.declaration.templateId().get();
                    // 不可以修改任务属性的任务模板
                    if (!StringUtils.equals(templateId, declarationTemplateId)) {
                        throw new ConflictException(ErrorCodes.PROPERTY_HAS_USED_TEMPLATE);
                    }
                }
                return;
            }

            // 本来不存在模板ID，且输入了模板ID
            if (this.declaration.templateId().defined()) {
                this.templateId = this.declaration.templateId().get();
                this.templateProperty = this.templates()
                        .stream()
                        .filter(tp -> StringUtils.equalsIgnoreCase(tp.id(), this.templateId))
                        .findAny()
                        .orElseThrow(() -> new NotFoundException(ErrorCodes.TEMPLATE_PROPERTY_NOT_FOUND));
            }
        }

        private void accept(Row row, UpdateSql sql) {
            this.acceptName(row, sql);
            this.acceptDataType(row, sql);
            this.acceptDescription(row, sql);
            this.acceptRequired(row, sql);
            this.acceptIdentifiable(row, sql);
            this.acceptScope(row, sql);
            this.acceptAppearance(row, sql);
        }

        private void acceptName(Row row, UpdateSql sql) {
            this.declaration.name().ifDefined(name -> {
                String actual = PostgresqlTaskPropertyRepo.this.validator.validateName(name, this.context());
                if (Objects.nonNull(this.templateProperty) && !StringUtils.equalsIgnoreCase(actual,
                        this.templateProperty.name())) {
                    throw new ConflictException(ErrorCodes.PROPERTY_NAME_NOT_EQUALS_TEMPLATE);
                }
                // 原本没有模板，根据名字判断是否去应用模板
                if (Objects.isNull(this.templateProperty)) {
                    this.templateProperty = this.templates()
                            .stream()
                            .filter(tp -> StringUtils.equalsIgnoreCase(tp.name(), actual))
                            .findAny()
                            .orElse(null);
                    if (Objects.nonNull(this.templateProperty)) {
                        sql.set(Row.COLUMN_TEMPLATE_ID, this.templateProperty.id());
                        row.templateId(this.templateProperty.id());
                    }
                }
                Predicate<Row> test = property -> StringUtils.equalsIgnoreCase(property.name(), actual);
                test = test.and(property -> !StringUtils.equalsIgnoreCase(property.id(), this.property.id()));
                if (this.rows().stream().anyMatch(test)) {
                    log.error(
                            "A property with the same name already exists in the same task. "
                                    + "[taskId={}, propertyId={}, propertyName={}]",
                            this.property.taskId(), this.property.id(), actual);
                    throw new ConflictException(ErrorCodes.TASK_PROPERTY_NAME_EXIST);
                }
                sql.set(Row.COLUMN_NAME, actual);
                row.name(actual);
            });
        }

        private void acceptDataType(Row row, UpdateSql sql) {
            this.declaration.dataType().ifDefined(dataType -> {
                PropertyDataType actualDataType = Enums.parse(PropertyDataType.class, dataType,
                        PropertyDataType.TEXT, ErrorCodes.TASK_PROPERTY_DATA_TYPE_INVALID);
                if (this.templateProperty != null && this.templateProperty.dataType() != actualDataType) {
                    throw new ConflictException(ErrorCodes.PROPERTY_DATA_TYPE_NOT_EQUALS_TEMPLATE);
                }
                PropertyDataType currentDataType = Enums.parse(PropertyDataType.class, this.property.dataType());
                if (actualDataType != currentDataType && this.hasInstances()) {
                    log.error(
                            "Cannot modify data type of property when owning task has instances. "
                                    + "[taskId={}, propertyName={}]",
                            this.property.taskId(), this.property.name());
                    throw new ConflictException(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES);
                }
                String dataTypeValue = Enums.toString(actualDataType);
                sql.set(Row.COLUMN_DATA_TYPE, dataTypeValue);
                row.dataType(dataTypeValue);
                this.acceptSequence(row, sql, currentDataType, actualDataType);
            });
        }

        private void acceptSequence(Row row, UpdateSql sql, PropertyDataType current, PropertyDataType actual) {
            if (current == actual) {
                return;
            }
            if (actual.listable()) {
                sql.set(Row.COLUMN_SEQUENCE, 0);
                row.sequence(0);
            } else if (this.templateProperty != null) {
                sql.set(Row.COLUMN_SEQUENCE, this.templateProperty.sequence());
                row.sequence(this.templateProperty.sequence());
            } else {
                int sequence = this.nextSequence(actual);
                sql.set(Row.COLUMN_SEQUENCE, sequence);
                row.sequence(sequence);
            }
        }

        private void acceptDescription(Row row, UpdateSql sql) {
            this.declaration.description().ifDefined(description -> {
                String actualDescription = validator.validateDescription(description, this.context());
                sql.set(Row.COLUMN_DESCRIPTION, actualDescription);
                row.description(actualDescription);
            });
        }

        private void acceptRequired(Row row, UpdateSql sql) {
            this.declaration.required().ifDefined(required -> {
                boolean isActualTrue = nullIf(required, false);
                boolean isCurrentTrue = nullIf(this.property.required(), false);
                if (!isCurrentTrue && isActualTrue && this.hasInstances()) {
                    log.error(
                            "Cannot modify property to required when owning task has instances. "
                                    + "[taskId={}, propertyName={}]",
                            this.property.taskId(), this.property.name());
                    throw new ConflictException(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES);
                }
                sql.set(Row.COLUMN_REQUIRED, isActualTrue);
                row.required(isActualTrue);
            });
        }

        private void acceptIdentifiable(Row row, UpdateSql sql) {
            this.declaration.identifiable().ifDefined(identifiable -> {
                boolean isActualTrue = nullIf(identifiable, false);
                boolean isCurrentTrue = nullIf(this.property.identifiable(), false);
                if (!isCurrentTrue && isActualTrue && this.hasInstances()) {
                    log.error(
                            "Cannot modify identifiable of property when owning task has instances. "
                                    + "[taskId={}, propertyName={}]",
                            this.property.taskId(), this.property.name());
                    throw new ConflictException(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES);
                }
                sql.set(Row.COLUMN_IDENTIFIABLE, isActualTrue);
                row.identifiable(isActualTrue);
            });
        }

        private void acceptScope(Row row, UpdateSql sql) {
            this.declaration.scope().ifDefined(scope -> {
                PropertyScope actual = Enums.parse(PropertyScope.class, scope, PropertyScope.PUBLIC,
                        ErrorCodes.TASK_PROPERTY_SCOPE_INVALID);
                String value = Enums.toString(actual);
                sql.set(Row.COLUMN_SCOPE, value);
                row.scope(value);
            });
        }

        private void acceptAppearance(Row row, UpdateSql sql) {
            this.declaration.appearance().ifDefined(appearance -> {
                String actual = PostgresqlTaskPropertyRepo.this.serializeAppearance(appearance);
                sql.set(Row.COLUMN_APPEARANCE, json(actual));
                row.appearance(actual);
            });
        }
    }

    /**
     * 表示任务属性的数据行。
     *
     * @author 梁济时
     * @since 2023-10-23
     */
    static final class Row {
        static final String TABLE_NAME = "task_property";

        static final String COLUMN_ID = "id";

        static final String COLUMN_TASK_ID = "task_id";

        static final String COLUMN_NAME = "name";

        static final String COLUMN_REQUIRED = "required";

        static final String COLUMN_DESCRIPTION = "description";

        static final String COLUMN_SCOPE = "scope";

        static final String COLUMN_DATA_TYPE = "data_type";

        static final String COLUMN_SEQUENCE = "sequence";

        static final String COLUMN_APPEARANCE = "appearance";

        static final String COLUMN_IDENTIFIABLE = "identifiable";

        static final String COLUMN_TEMPLATE_ID = "template_id";

        static final String PROPERTY_CATEGORIES = "categories";

        private final Map<String, Object> values;

        Row() {
            this(null);
        }

        Row(Map<String, Object> values) {
            if (values == null) {
                this.values = new LinkedHashMap<>(12);
            } else {
                canonicalizeAppearance(values);
                this.values = values;
            }
        }

        private static void canonicalizeAppearance(Map<String, Object> values) {
            if (values.containsKey(COLUMN_APPEARANCE)) {
                Object value = values.get(COLUMN_APPEARANCE);
                if (!(value instanceof SqlValue)) {
                    value = json(Objects.toString(value));
                    values.put(COLUMN_APPEARANCE, value);
                }
            }
        }

        String id() {
            return cast(this.values.get(COLUMN_ID));
        }

        void id(String id) {
            this.values.put(COLUMN_ID, id);
        }

        String taskId() {
            return cast(this.values.get(COLUMN_TASK_ID));
        }

        void taskId(String taskId) {
            this.values.put(COLUMN_TASK_ID, taskId);
        }

        String name() {
            return cast(this.values.get(COLUMN_NAME));
        }

        void name(String name) {
            this.values.put(COLUMN_NAME, name);
        }

        Boolean required() {
            return cast(this.values.get(COLUMN_REQUIRED));
        }

        void required(Boolean isRequired) {
            this.values.put(COLUMN_REQUIRED, isRequired);
        }

        String description() {
            return cast(this.values.get(COLUMN_DESCRIPTION));
        }

        void description(String description) {
            this.values.put(COLUMN_DESCRIPTION, description);
        }

        String scope() {
            return cast(this.values.get(COLUMN_SCOPE));
        }

        void scope(String scope) {
            this.values.put(COLUMN_SCOPE, scope);
        }

        String dataType() {
            return cast(this.values.get(COLUMN_DATA_TYPE));
        }

        void dataType(String dataType) {
            this.values.put(COLUMN_DATA_TYPE, dataType);
        }

        Integer sequence() {
            return cast(this.values.get(COLUMN_SEQUENCE));
        }

        void sequence(Integer sequence) {
            this.values.put(COLUMN_SEQUENCE, sequence);
        }

        String templateId() {
            return cast(this.values.get(COLUMN_TEMPLATE_ID));
        }

        void templateId(String templateId) {
            this.values.put(COLUMN_TEMPLATE_ID, templateId);
        }

        String appearance() {
            SqlValue value = cast(this.values.get(COLUMN_APPEARANCE));
            return cast(value.get());
        }

        void appearance(String appearance) {
            this.values.put(COLUMN_APPEARANCE, json(appearance));
        }

        Boolean identifiable() {
            return cast(this.values.get(COLUMN_IDENTIFIABLE));
        }

        void identifiable(Boolean isIdentifiable) {
            this.values.put(COLUMN_IDENTIFIABLE, isIdentifiable);
        }

        List<PropertyCategory> categories() {
            List<PropertyCategory> categories = cast(this.values.get(PROPERTY_CATEGORIES));
            if (categories == null) {
                categories = Collections.emptyList();
                this.values.put(PROPERTY_CATEGORIES, categories);
            }
            return categories;
        }

        void categories(List<PropertyCategory> categories) {
            this.values.put(PROPERTY_CATEGORIES, categories);
        }

        void insert(DynamicSqlExecutor executor) {
            InsertSql sql = InsertSql.custom().into(TABLE_NAME);
            this.values.forEach(sql::value);
            sql.execute(executor);
        }

        static List<TaskTemplateProperty> selectTemplateByTask(DynamicSqlExecutor executor, String taskId) {
            String sql = "SELECT ttp.id, ttp.data_type, ttp.sequence,  ttp.name, ttp.task_template_id "
                    + "from task_template_property as ttp "
                    + "join task_template as tt on ttp.task_template_id = tt.id "
                    + "join task on task.template_id = tt.id "
                    + "where task.id = ?";
            List<Object> args = Collections.singletonList(taskId);
            List<Map<String, Object>> rows = executor.executeQuery(sql, args);

            return rows.stream().map(row -> TaskTemplateProperty.custom()
                    .taskTemplateId(ObjectUtils.cast(row.get("task_template_id")))
                    .name(ObjectUtils.cast(row.get("name")))
                    .sequence(ObjectUtils.cast(row.get("sequence")))
                    .id(ObjectUtils.cast(row.get("id")))
                    .dataType(Enums.parse(PropertyDataType.class, ObjectUtils.cast(row.get("data_type"))))
                    .build()).collect(Collectors.toList());
        }

        static List<Row> selectByTask(DynamicSqlExecutor executor, String taskId) {
            return select(executor, Condition.expectEqual(COLUMN_TASK_ID, taskId));
        }

        static List<Row> selectByTasks(DynamicSqlExecutor executor, List<String> taskIds) {
            if (taskIds.isEmpty()) {
                return Collections.emptyList();
            }
            return select(executor, Condition.expectIn(COLUMN_TASK_ID, taskIds));
        }

        static Row select(DynamicSqlExecutor executor, String taskId, String propertyId) {
            Condition condition = Condition.expectEqual(COLUMN_ID, propertyId);
            condition = condition.and(Condition.expectEqual(COLUMN_TASK_ID, taskId));
            List<Row> rows = select(executor, condition);
            if (rows.isEmpty()) {
                return null;
            } else {
                return rows.get(0);
            }
        }

        static List<Row> select(DynamicSqlExecutor executor, Condition condition) {
            SqlBuilder sql = SqlBuilder.custom().append("SELECT ")
                    .appendIdentifier(COLUMN_ID).append(", ")
                    .appendIdentifier(COLUMN_TASK_ID).append(", ")
                    .appendIdentifier(COLUMN_NAME).append(", ")
                    .appendIdentifier(COLUMN_REQUIRED).append(", ")
                    .appendIdentifier(COLUMN_DESCRIPTION).append(", ")
                    .appendIdentifier(COLUMN_SCOPE).append(", ")
                    .appendIdentifier(COLUMN_DATA_TYPE).append(", ")
                    .appendIdentifier(COLUMN_SEQUENCE).append(", ")
                    .appendIdentifier(COLUMN_APPEARANCE).append(", ")
                    .appendIdentifier(COLUMN_IDENTIFIABLE).append(", ")
                    .appendIdentifier(COLUMN_TEMPLATE_ID)
                    .append(" FROM ").appendIdentifier(TABLE_NAME).append(" WHERE ");
            List<Object> args = new LinkedList<>();
            condition.toSql(sql, args);
            List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
            return rows.stream().map(Row::new).collect(Collectors.toList());
        }
    }
}
