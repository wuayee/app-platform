/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.util.SequenceUtils;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.DeleteSql;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.UpdateSql;
import com.huawei.fit.jober.taskcenter.validation.PropertyValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 为 {@link TaskTemplateProperty.Repo} 提供实现
 *
 * @author yWX1299574
 * @since 2023-12-05
 */
@Component
@RequiredArgsConstructor
public class PostgresqlTaskTemplatePropertyRepo implements TaskTemplateProperty.Repo {
    private static final Logger log = Logger.get(PostgresqlTaskTemplatePropertyRepo.class);

    private final DynamicSqlExecutor executor;

    private final PropertyValidator validator;

    /**
     * 创建任务模板属性
     *
     * @param taskTemplateId 任务模板id {@link String}
     * @param declarations 任务模板属性声明列表 {@link List}{@code <}{@link TaskTemplateProperty.Declaration}{@code >}
     * @param context 操作上下文 {@link OperationContext}
     * @return 任务临时属性列表
     */
    @Override
    @Transactional
    public List<TaskTemplateProperty> create(String taskTemplateId, List<TaskTemplateProperty.Declaration> declarations,
            OperationContext context) {
        log.info("[PostgresqlTaskTemplatePropertyRepo]: Create task_template_properties start, size {}.",
                declarations.size());
        if (CollectionUtils.isEmpty(declarations)) {
            log.warn("[PostgresqlTaskTemplatePropertyRepo]: Create task_template_properties end, no property created.");
            return Collections.emptyList();
        }
        List<TaskTemplateProperty> result = new Creator(taskTemplateId, declarations, executor, context).create();

        log.info("[PostgresqlTaskTemplatePropertyRepo]: Create task_template_properties end.");
        return result;
    }

    /**
     * 更新任务模板属性
     *
     * @param taskTemplateId 任务模板id {@link String}
     * @param id 任务模板属性Id {@link String}
     * @param declaration 任务模板属性声明 {@link TaskTemplateProperty.Declaration}
     * @param context 操作上下文 {@link OperationContext}
     */
    @Override
    @Transactional
    public void patch(String taskTemplateId, String id, TaskTemplateProperty.Declaration declaration,
            OperationContext context) {
        log.info("[PostgresqlTaskTemplatePropertyRepo]: Modify task_template_property start.");
        if (!declaration.name().defined() && !declaration.dataType().defined()) {
            log.info("[PostgresqlTaskTemplatePropertyRepo]: Modify task_template_properties end, no field updated.");
            return;
        }
        new Patcher(id, declaration, taskTemplateId, this.executor, context).patch();
        log.info("[PostgresqlTaskTemplatePropertyRepo]: Modify task_template_property end.");
    }

    @Override
    @Transactional
    public void delete(String taskTemplateId, String id, OperationContext context) {
        log.info("Start delete a task_template_property, id={}, taskTemplateId={}, context={}", id, taskTemplateId,
                context);

        new Deleter(taskTemplateId, this.executor, context).deleteOne(id);

        log.info("Delete a task_template_property success.");
    }

    @Override
    public void delete(List<String> ids, OperationContext context) {
        new Deleter(null, executor, context).deleteMore(ids);
    }

    @Override
    @Transactional
    public void deleteByTaskTemplateId(String taskTemplateId, OperationContext context) {
        log.info("Start delete all task_template_properties in a task_template, taskTemplateId={}, context={}",
                taskTemplateId, context);

        new Deleter(taskTemplateId, this.executor, context).deleteAll();

        log.info("Delete all task_template_properties in a task_template success.");
    }

    @Override
    @Transactional
    public TaskTemplateProperty retrieve(String taskTemplateId, String id, OperationContext context) {
        log.info("Start retrieve TaskTemplateProperty by taskTemplateId={} and id={}, operationContext={}",
                taskTemplateId, id, context);
        TaskTemplateProperty result = new Selector(taskTemplateId, this.executor, context).selectOneById(id);

        log.info("Retrieve TaskTemplateProperty success.");
        return result;
    }

    @Override
    @Transactional
    public List<TaskTemplateProperty> list(String taskTemplateId, OperationContext context) {
        log.info("Start list TaskTemplateProperty by taskTemplateId={}, operationContext={}", taskTemplateId, context);
        List<TaskTemplateProperty> result = new Selector(taskTemplateId, this.executor, context).selectAllInTemplate();
        log.info("List TaskTemplateProperty success, result size={}.", result.size());
        return result;
    }

    @Override
    @Transactional
    public Map<String, List<TaskTemplateProperty>> list(List<String> taskTemplateIds, OperationContext context) {
        log.info("Start list TaskTemplateProperty by taskTemplateIds={}, operationContext={}", taskTemplateIds,
                context);
        return new Selector(null, this.executor, context).selectByTemplateIds(taskTemplateIds);
    }

    private abstract class AbstractOperation {
        /**
         * 任务临时id
         */
        public final String taskTemplateId;

        /**
         * 操作上下文
         */
        protected final OperationContext context;

        /**
         * 动态SQL执行器
         */
        protected final DynamicSqlExecutor executor;

        private final LazyLoader<List<Row>> rows;

        AbstractOperation(String taskTemplateId, DynamicSqlExecutor executor, OperationContext context) {
            this.taskTemplateId = taskTemplateId;
            this.context = context;
            this.executor = executor;
            this.rows = new LazyLoader<>(this::selectRows);
        }

        private List<Row> selectRows() {
            String sql = Row.BASE_SELECT_SQL.get()
                    + " WHERE task_template_id in (SELECT find_template_parents(?) AS id)";
            List<String> args = Collections.singletonList(Entities.validateId(this.taskTemplateId,
                    () -> new BadRequestException(ErrorCodes.TEMPLATE_ID_INVALID_IN_PROPERTY)));
            return this.executor.executeQuery(sql, args).stream().map(Row::new).collect(Collectors.toList());
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
         * 检查名称是否存在
         *
         * @param name 表示待检查的名称的{@link String}
         * @return 是否存在名称
         */
        protected final boolean checkNameExist(String name) {
            return this.rows.get().stream().anyMatch(row -> StringUtils.equalsIgnoreCase(name, row.name()));
        }

        /**
         * 检查特定id的名称是否存在
         *
         * @param name 表示待检查的名称的{@link String}
         * @param id 表示名称对应的id的{@link String}
         * @return 是否存在名称
         */
        protected final boolean checkNameExist(String name, String id) {
            return this.rows.get()
                    .stream()
                    .filter(row -> !StringUtils.equalsIgnoreCase(id, row.id()))
                    .anyMatch(row -> StringUtils.equalsIgnoreCase(name, row.name()));
        }

        /**
         * 获得给定数据类型的序列的长度
         *
         * @param dataType 表示数据类型的{@link PropertyDataType}
         * @return 返回序列长度
         */
        protected final int nextSequence(PropertyDataType dataType) {
            return this.nextSequence(Enums.toString(dataType));
        }

        /**
         * 获得给定数据类型的序列的长度
         *
         * @param dataType 表示数据类型的{@link String}
         * @return 返回序列长度
         */
        protected final int nextSequence(String dataType) {
            List<Integer> sequences = this.rows.get()
                    .stream()
                    .filter(row -> StringUtils.equalsIgnoreCase(row.dataType(), dataType))
                    .map(Row::sequence)
                    .sorted()
                    .collect(Collectors.toList());

            return SequenceUtils.getSequenceFromList(sequences);
        }

        /**
         * 检查任务临时属性是否存在
         *
         * @param id 表示属性的id的{@link String}
         * @return 是否已经存在
         */
        protected boolean checkTaskTemplatePropertyIsUsed(String id) {
            String sql = "SELECT 1 FROM task_property where template_id = ?";
            List<String> args = Collections.singletonList(id);

            return Objects.equals(executor.executeScalar(sql, args), 1);
        }
    }

    private class Creator extends AbstractOperation {
        private final List<TaskTemplateProperty.Declaration> declarations;

        private Creator(String taskTemplateId, List<TaskTemplateProperty.Declaration> declarations,
                DynamicSqlExecutor executor, OperationContext context) {
            super(taskTemplateId, executor, context);
            this.declarations = declarations;
        }

        /**
         * 创建任务模板属性
         *
         * @return 返回创建的任务模板属性列表
         */
        public List<TaskTemplateProperty> create() {
            String actualTemplateId = Entities.validateId(this.taskTemplateId,
                    () -> new BadRequestException(ErrorCodes.TEMPLATE_ID_INVALID_IN_PROPERTY));
            InsertSql insert = Row.insertInto();
            List<TaskTemplateProperty> result = new LinkedList<>();
            for (TaskTemplateProperty.Declaration declaration : this.declarations) {
                Row row = new Row();
                row.id(Entities.generateId());
                row.taskTemplateId(actualTemplateId);

                String name = declaration.name()
                        .required(() -> new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_NAME_REQUIRED));
                this.acceptName(name, row);

                String dataTypeStr = StringUtils.trim(declaration.dataType().withDefault(""));
                this.acceptDataType(dataTypeStr, row);

                this.rows().add(row);
                result.add(row.toTaskTemplateProperty());

                row.addValuesToInsertSql(insert);
                insert.next();
            }
            if (insert.execute(this.executor) != this.declarations.size()) {
                throw new ServerInternalException(
                        "An error occurred while inserting values into the table " + Row.TABLE_NAME + ".");
            }
            return result;
        }

        private void acceptName(String name, Row row) {
            PostgresqlTaskTemplatePropertyRepo.this.validator.validateName(name, this.context);
            if (this.checkNameExist(name)) {
                log.error("A property with the same name already exists. [taskTemplateId={}, propertyName={}]",
                        this.taskTemplateId, name);
                throw new ConflictException(ErrorCodes.TEMPLATE_PROPERTY_NAME_EXIST);
            }
            row.name(name);
        }

        private void acceptDataType(String dataTypeStr, Row row) {
            PropertyDataType dataType = Enums.parse(PropertyDataType.class, dataTypeStr, PropertyDataType.TEXT,
                    ErrorCodes.TEMPLATE_PROPERTY_DATA_TYPE_INVALID);
            row.dataType(Enums.toString(dataType));
            row.sequence(this.nextSequence(dataType));
        }
    }

    private class Patcher extends AbstractOperation {
        private final TaskTemplateProperty.Declaration declaration;

        private final String id;

        private Patcher(String id, TaskTemplateProperty.Declaration declaration, String taskTemplateId,
                DynamicSqlExecutor executor, OperationContext context) {
            super(taskTemplateId, executor, context);
            this.declaration = declaration;
            this.id = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_ID_INVALID));
        }

        /**
         * 更新任务模板属性
         */
        public void patch() {
            UpdateSql update = UpdateSql.custom().table(Row.TABLE_NAME);
            Row oldRow = this.getOneById();
            if (this.declaration.name().defined()) {
                String name = this.declaration.name().get();
                this.acceptName(oldRow, name, update);
            }

            if (this.declaration.dataType().defined()) {
                String dataTypeStr = StringUtils.trim(declaration.dataType().get());
                this.acceptDataType(dataTypeStr, oldRow, update);
            }

            update.where(Condition.expectEqual(Row.COLUMN_ID, id));
            if ((this.declaration.name().defined() || this.declaration.dataType().defined())
                    && update.execute(this.executor) != 1) {
                throw new ServerInternalException(
                        "An error occurred while updating " + Row.TABLE_NAME + ". [id=" + this.id + "]");
            }
        }

        private void acceptDataType(String dataTypeStr, Row oldRow, UpdateSql update) {
            PropertyDataType dataType = Enums.parse(PropertyDataType.class, dataTypeStr, PropertyDataType.TEXT,
                    ErrorCodes.TEMPLATE_PROPERTY_DATA_TYPE_INVALID);
            if (StringUtils.equals(Enums.toString(dataType), oldRow.dataType())) {
                log.info("Modify task_template_property error, old dataType equals new dataType.");
                throw new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_DATA_TYPE_NO_MODIFY);
            }
            if (this.checkTaskTemplatePropertyIsUsed(this.id)) {
                log.info("Modify task_template_property error, this property is used.");
                throw new ConflictException(ErrorCodes.TEMPLATE_PROPERTY_USED);
            }
            update.set(Row.COLUMN_DATA_TYPE, Enums.toString(dataType));
            update.set(Row.COLUMN_SEQUENCE, this.nextSequence(dataType));
        }

        private void acceptName(Row oldRow, String name, UpdateSql update) {
            if (StringUtils.equals(oldRow.name(), name)) {
                log.info("Modify task_template_property error, old name equals new name.");
                throw new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_NAME_NO_MODIFY);
            }

            PostgresqlTaskTemplatePropertyRepo.this.validator.validateName(name, this.context);
            if (this.checkNameExist(name, this.id)) {
                log.error("A property with the same name already exists. [taskTemplateId={}, propertyName={}]",
                        this.taskTemplateId, name);
                throw new ConflictException(ErrorCodes.TEMPLATE_PROPERTY_NAME_EXIST);
            }
            update.set(Row.COLUMN_NAME, name);
        }

        private Row getOneById() {
            return this.rows()
                    .stream()
                    .filter(row -> StringUtils.equals(row.id(), this.id))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(ErrorCodes.TEMPLATE_PROPERTY_NOT_FOUND));
        }
    }

    private class Selector extends AbstractOperation {
        Selector(String taskTemplateId, DynamicSqlExecutor executor, OperationContext context) {
            super(taskTemplateId, executor, context);
        }

        /**
         * 根据id查询一个任务临时属性
         *
         * @param id 表示id的{@link String}
         * @return 返回一个id对应的任务临时属性
         */
        public TaskTemplateProperty selectOneById(String id) {
            String actualId = Entities.validateId(id,
                    () -> new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_ID_INVALID));
            String sql = Row.BASE_SELECT_SQL.get() + " WHERE " + Row.COLUMN_TASK_TEMPLATE_ID + " = ? AND "
                    + Row.COLUMN_ID + " = ?";
            List<String> args = Arrays.asList(this.taskTemplateId, actualId);

            List<Map<String, Object>> rowMaps = this.executor.executeQuery(sql, args);

            if (CollectionUtils.isEmpty(rowMaps)) {
                throw new NotFoundException(ErrorCodes.TEMPLATE_PROPERTY_NOT_FOUND);
            }

            if (rowMaps.size() > 1) {
                throw new ServerInternalException("Find two or more row in database by id " + actualId + ".");
            }
            return new Row(rowMaps.get(0)).toTaskTemplateProperty();
        }

        /**
         * 从模板中选择所有模板属性
         *
         * @return 返回所有的任务模板属性
         */
        public List<TaskTemplateProperty> selectAllInTemplate() {
            return this.rows().stream().map(Row::toTaskTemplateProperty).collect(Collectors.toList());
        }

        /**
         * 根据临时ID进行选择
         *
         * @param templateIds 表示临时id的列表的{@link List}{@code <}{@link String}{@code >}
         * @return 返回查询的数据
         */
        public Map<String, List<TaskTemplateProperty>> selectByTemplateIds(List<String> templateIds) {
            List<String> actualIds = templateIds.stream().filter(Entities::isId).collect(Collectors.toList());
            if (actualIds.isEmpty()) {
                throw new BadRequestException(ErrorCodes.TEMPLATE_ID_INVALID_IN_PROPERTY);
            }
            Map<String, List<TaskTemplateProperty>> mapRow = new HashMap<>(actualIds.size());
            for (String actualId : actualIds) {
                String sql = Row.BASE_SELECT_SQL.get() + " WHERE " + Row.COLUMN_TASK_TEMPLATE_ID
                        + " IN (SELECT find_template_parents(?) as id)";
                List<Map<String, Object>> rowMaps = this.executor.executeQuery(sql,
                        Collections.singletonList(actualId));
                List<TaskTemplateProperty> templatePropertyList = rowMaps.stream()
                        .map(Row::new)
                        .map(Row::toTaskTemplateProperty)
                        .collect(Collectors.toList());
                mapRow.put(actualId, templatePropertyList);
            }
            return mapRow;
        }
    }

    private class Deleter extends AbstractOperation {
        Deleter(String taskTemplateId, DynamicSqlExecutor executor, OperationContext context) {
            super(taskTemplateId, executor, context);
        }

        /**
         * 删除一个任务临时属性
         *
         * @param id 表示id的{@link String}
         */
        public void deleteOne(String id) {
            String actualId = Entities.validateId(id,
                    () -> new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_ID_INVALID));

            if (this.checkTaskTemplatePropertyIsUsed(actualId)) {
                throw new ConflictException(ErrorCodes.TEMPLATE_PROPERTY_USED);
            }

            DeleteSql delete = DeleteSql.custom()
                    .from(Row.TABLE_NAME)
                    .where(Condition.expectEqual(Row.COLUMN_ID, actualId));

            if (delete.execute(this.executor) != 1) {
                throw new ServerInternalException(
                        "Failed delete the data in " + Row.TABLE_NAME + ", " + Row.COLUMN_ID + "=" + actualId);
            }
        }

        /**
         * 删除多个id
         *
         * @param ids 表示多个id的列表的{@link List}{@code <}{@link String}{@code >}
         */
        public void deleteMore(List<String> ids) {
            List<String> actualIds = ids.stream()
                    .map(id -> Entities.validateId(id,
                            () -> new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_ID_INVALID)))
                    .collect(Collectors.toList());
            if (actualIds.stream().anyMatch(this::checkTaskTemplatePropertyIsUsed)) {
                throw new ConflictException(ErrorCodes.TEMPLATE_PROPERTY_USED);
            }

            DeleteSql delete = DeleteSql.custom()
                    .from(Row.TABLE_NAME)
                    .where(Condition.expectIn(Row.COLUMN_ID, actualIds));

            if (actualIds.size() != delete.execute(executor)) {
                throw new ServerInternalException("Failed delete the data in " + Row.TABLE_NAME + ".");
            }
        }

        /**
         * 删除所有
         */
        public void deleteAll() {
            String actualTemplateId = Entities.validateId(this.taskTemplateId,
                    () -> new BadRequestException(ErrorCodes.TEMPLATE_ID_INVALID_IN_PROPERTY));

            DeleteSql delete = DeleteSql.custom()
                    .from(Row.TABLE_NAME)
                    .where(Condition.expectEqual(Row.COLUMN_TASK_TEMPLATE_ID, actualTemplateId));

            delete.execute(this.executor);
        }
    }

    /**
     * 表示任务模板属性的数据行
     *
     * @author yWX1299574
     * @since 2023-12-06
     */
    private static final class Row {
        /**
         * 表名
         */
        public static final String TABLE_NAME = "task_template_property";

        /**
         * id列
         */
        public static final String COLUMN_ID = "id";

        /**
         * task_template_id列
         */
        public static final String COLUMN_TASK_TEMPLATE_ID = "task_template_id";

        /**
         * name列
         */
        public static final String COLUMN_NAME = "name";

        /**
         * data_type列
         */
        public static final String COLUMN_DATA_TYPE = "data_type";

        /**
         * squence列
         */
        public static final String COLUMN_SEQUENCE = "sequence";

        /**
         * 列数组
         */
        public static final List<String> COLUMNS = Arrays.asList(COLUMN_ID, COLUMN_TASK_TEMPLATE_ID, COLUMN_NAME,
                COLUMN_DATA_TYPE, COLUMN_SEQUENCE);

        private static final LazyLoader<String> BASE_SELECT_SQL = new LazyLoader<>(() ->
                COLUMNS.stream().map(column -> '"' + column + '"').collect(Collectors.joining(",", "SELECT ", " FROM "))
                        + TABLE_NAME);

        private final Map<String, Object> row;

        Row() {
            this(new LinkedHashMap<>(COLUMNS.size()));
        }

        Row(Map<String, Object> row) {
            this.row = row;
        }

        static InsertSql insertInto() {
            return InsertSql.custom().into(TABLE_NAME);
        }

        void addValuesToInsertSql(InsertSql sql) {
            sql.value(COLUMN_ID, id());
            sql.value(COLUMN_NAME, name());
            sql.value(COLUMN_SEQUENCE, sequence());
            sql.value(COLUMN_DATA_TYPE, dataType());
            sql.value(COLUMN_TASK_TEMPLATE_ID, taskTemplateId());
        }

        TaskTemplateProperty toTaskTemplateProperty() {
            return TaskTemplateProperty.custom()
                    .id(id())
                    .name(name())
                    .taskTemplateId(taskTemplateId())
                    .sequence(sequence())
                    .dataType(Enums.parse(PropertyDataType.class, dataType()))
                    .build();
        }

        String id() {
            return ObjectUtils.cast(this.row.get(COLUMN_ID));
        }

        void id(String id) {
            this.row.put(COLUMN_ID, id);
        }

        String name() {
            return ObjectUtils.cast(this.row.get(COLUMN_NAME));
        }

        void name(String name) {
            this.row.put(COLUMN_NAME, name);
        }

        String dataType() {
            return ObjectUtils.cast(this.row.get(COLUMN_DATA_TYPE));
        }

        void dataType(String dataType) {
            this.row.put(COLUMN_DATA_TYPE, dataType);
        }

        Integer sequence() {
            return ObjectUtils.cast(this.row.get(COLUMN_SEQUENCE));
        }

        void sequence(Integer sequence) {
            this.row.put(COLUMN_SEQUENCE, sequence);
        }

        String taskTemplateId() {
            return ObjectUtils.cast(this.row.get(COLUMN_TASK_TEMPLATE_ID));
        }

        void taskTemplateId(String taskTemplateId) {
            this.row.put(COLUMN_TASK_TEMPLATE_ID, taskTemplateId);
        }
    }
}
