/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.adapter;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fit.jober.taskcenter.dao.po.SourceObject;
import modelengine.fit.jober.taskcenter.declaration.SourceDeclaration;
import modelengine.fit.jober.taskcenter.domain.RefreshInTimeSourceEntity;
import modelengine.fit.jober.taskcenter.domain.SourceEntity;
import modelengine.fit.jober.taskcenter.domain.SourceType;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.util.Enums;
import modelengine.fit.jober.taskcenter.util.MapSerializer;
import modelengine.fit.jober.taskcenter.util.sql.Condition;
import modelengine.fit.jober.taskcenter.util.sql.DeleteSql;
import modelengine.fit.jober.taskcenter.util.sql.InsertSql;
import modelengine.fit.jober.taskcenter.util.sql.SqlBuilder;
import modelengine.fit.jober.taskcenter.util.sql.SqlValue;
import modelengine.fit.jober.taskcenter.util.sql.UpdateSql;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.ServerInternalException;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.common.exceptions.NotFoundException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 调用刷新数据源适配器。
 *
 * @author 陈镕希
 * @since 2023-08-15
 */
@Component
public class RefreshInTimeSourceAdapter extends AbstractSourceAdapter {
    private static final Logger log = Logger.get(RefreshInTimeSourceAdapter.class);

    private final DynamicSqlExecutor executor;

    private final MapSerializer mapSerializer;

    public RefreshInTimeSourceAdapter(DynamicSqlExecutor executor, MapSerializer mapSerializer) {
        this.executor = executor;
        this.mapSerializer = mapSerializer;
    }

    @Override
    public SourceType getType() {
        return SourceType.REFRESH_IN_TIME;
    }

    @Override
    public SourceEntity createExtension(SourceObject sourceObject, SourceDeclaration sourceDeclaration,
            OperationContext context) {
        Row row = new Row();
        row.id(sourceObject.getId());
        row.metadata(this.mapSerializer.serialize(UndefinableValue
                .withDefault(sourceDeclaration.getMetadata(), Collections.emptyMap())));
        row.createFitableId(withDefault(sourceDeclaration.getCreateFitableId()));
        row.patchFitableId(withDefault(sourceDeclaration.getPatchFitableId()));
        row.deleteFitableId(withDefault(sourceDeclaration.getDeleteFitableId()));
        row.retrieveFitableId(UndefinableValue.require(sourceDeclaration.getRetrieveFitableId(),
                () -> new BadRequestException(ErrorCodes.REFRESH_IN_TIME_REQUIRE_RETRIEVE_FITABLE)));
        row.listFitableId(UndefinableValue.require(sourceDeclaration.getListFitableId(),
                () -> new BadRequestException(ErrorCodes.REFRESH_IN_TIME_REQUIRE_LIST_FITABLE)));
        row.insert(this.executor);
        RefreshInTimeSourceEntity entity = new RefreshInTimeSourceEntity();
        this.fill(entity, sourceObject);
        this.fill(entity, row);
        return entity;
    }

    private void fill(RefreshInTimeSourceEntity entity, Row row) {
        entity.setMetadata(this.mapSerializer.deserialize(row.metadata()));
        entity.setCreateFitableId(row.createFitableId());
        entity.setPatchFitableId(row.patchFitableId());
        entity.setDeleteFitableId(row.deleteFitableId());
        entity.setRetrieveFitableId(row.retrieveFitableId());
        entity.setListFitableId(row.listFitableId());
    }

    @Override
    public void patchExtension(SourceObject sourceObject, SourceDeclaration declaration, OperationContext context) {
        UpdateSql sql = UpdateSql.custom().table(Row.TABLE);
        AtomicBoolean modified = new AtomicBoolean(false);
        UndefinableValue.ifDefined(declaration.getMetadata(), metadata -> {
            Map<String, Object> actual = nullIf(metadata, Collections.emptyMap());
            String value = this.mapSerializer.serialize(actual);
            log.debug("Modify metadata. [metadata={}]", value);
            sql.set(Row.COLUMN_METADATA, SqlValue.json(value));
            modified.set(true);
        });
        UndefinableValue.ifDefined(declaration.getCreateFitableId(), fitableId -> {
            String actualFitableId = withDefault(fitableId);
            log.debug("Modify fitable to create. [fitableId={}]", actualFitableId);
            sql.set(Row.COLUMN_CREATE_FITABLE_ID, actualFitableId);
            modified.set(true);
        });
        UndefinableValue.ifDefined(declaration.getPatchFitableId(), fitableId -> {
            String actualFitableId = withDefault(fitableId);
            log.debug("Modify fitable to patch. [fitableId={}]", actualFitableId);
            sql.set(Row.COLUMN_PATCH_FITABLE_ID, actualFitableId);
            modified.set(true);
        });
        UndefinableValue.ifDefined(declaration.getDeleteFitableId(), fitableId -> {
            String actualFitableId = withDefault(fitableId);
            log.debug("Modify fitable to delete. [fitableId={}]", actualFitableId);
            sql.set(Row.COLUMN_DELETE_FITABLE_ID, actualFitableId);
            modified.set(true);
        });
        UndefinableValue.ifDefined(declaration.getRetrieveFitableId(), fitableId -> {
            String actualFitableId = validateFitableId(fitableId, ErrorCodes.REFRESH_IN_TIME_REQUIRE_RETRIEVE_FITABLE);
            log.debug("Modify fitable to retrieve. [fitableId={}]", actualFitableId);
            sql.set(Row.COLUMN_RETRIEVE_FITABLE_ID, actualFitableId);
            modified.set(true);
        });
        UndefinableValue.ifDefined(declaration.getListFitableId(), fitableId -> {
            String actualFitableId = validateFitableId(fitableId, ErrorCodes.REFRESH_IN_TIME_REQUIRE_LIST_FITABLE);
            log.debug("Modify fitable to list. [fitableId={}]", actualFitableId);
            sql.set(Row.COLUMN_LIST_FITABLE_ID, actualFitableId);
            modified.set(true);
        });
        if (!modified.get()) {
            log.info("No values to patch. [sourceId={}, type={}]", sourceObject.getId(), sourceObject.getType());
            return;
        }
        sql.where(Condition.expectEqual(Row.COLUMN_ID, sourceObject.getId()));
        int affectedRows = sql.execute(this.executor);
        if (affectedRows < 1) {
            log.error("No row of REFRESH_IN_TIME source updated. [id={}]", sourceObject.getId());
            throw new NotFoundException(ErrorCodes.SOURCE_NOT_FOUND);
        }
    }

    private static String validateFitableId(String fitableId, ErrorCodes error) {
        return notBlank(StringUtils.trim(fitableId), () -> new BadRequestException(error));
    }

    private static String withDefault(String value) {
        return Optional.ofNullable(value)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .orElse(Entities.emptyId());
    }

    private static String withDefault(UndefinableValue<String> value) {
        return Optional.ofNullable(value)
                .filter(actual -> actual.defined())
                .map(UndefinableValue::get)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .orElse(Entities.emptyId());
    }

    @Override
    public void deleteExtension(String sourceId, OperationContext context) {
        DeleteSql.custom()
                .from(Row.TABLE)
                .where(Condition.expectEqual(Row.COLUMN_ID, sourceId))
                .execute(this.executor);
    }

    @Override
    public SourceEntity retrieveExtension(SourceObject sourceObject, OperationContext context) {
        Row row = Row.select(this.executor, sourceObject.getId());
        if (row == null) {
            log.error("The extension of REFRESH_IN_TIME source does not exist. [id={}]", sourceObject.getId());
            throw new NotFoundException(ErrorCodes.SOURCE_NOT_FOUND);
        }
        RefreshInTimeSourceEntity entity = new RefreshInTimeSourceEntity();
        this.fill(entity, sourceObject);
        this.fill(entity, row);
        return entity;
    }

    @Override
    public Map<String, List<SourceEntity>> listExtension(List<SourceObject> sourceObjects, OperationContext context) {
        String requiredType = Enums.toString(this.getType());
        Map<String, SourceObject> objects = sourceObjects.stream()
                .filter(source -> StringUtils.equalsIgnoreCase(source.getType(), requiredType))
                .collect(Collectors.toMap(SourceObject::getId, Function.identity()));
        List<String> ids = new ArrayList<>(objects.keySet());
        List<Row> rows = Row.select(executor, ids);
        Map<String, List<SourceEntity>> grouped = new LinkedHashMap<>();
        for (Row row : rows) {
            SourceObject object = objects.get(row.id());
            RefreshInTimeSourceEntity entity = new RefreshInTimeSourceEntity();
            this.fill(entity, object);
            this.fill(entity, row);
            grouped.computeIfAbsent(object.getTaskId(), key -> new LinkedList<>()).add(entity);
        }
        return grouped;
    }

    private static class Row {
        static final String COLUMN_ID = "id";

        static final String COLUMN_METADATA = "metadata";

        static final String COLUMN_CREATE_FITABLE_ID = "create_fitable_id";

        static final String COLUMN_PATCH_FITABLE_ID = "patch_fitable_id";

        static final String COLUMN_DELETE_FITABLE_ID = "delete_fitable_id";

        static final String COLUMN_RETRIEVE_FITABLE_ID = "retrieve_fitable_id";

        static final String COLUMN_LIST_FITABLE_ID = "list_fitable_id";

        private static final String TABLE = "task_source_refresh_in_time";

        private final Map<String, Object> values;

        Row() {
            this(new HashMap<>(6));
        }

        Row(Map<String, Object> values) {
            this.values = values;
        }

        String id() {
            return cast(this.values.get(COLUMN_ID));
        }

        void id(String id) {
            this.values.put(COLUMN_ID, id);
        }

        String metadata() {
            Object value = this.values.get(COLUMN_METADATA);
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return cast(value);
            }
            String actual = value.toString();
            this.values.put(COLUMN_METADATA, actual);
            return actual;
        }

        void metadata(String metadata) {
            this.values.put(COLUMN_METADATA, metadata);
        }

        String createFitableId() {
            return cast(this.values.get(COLUMN_CREATE_FITABLE_ID));
        }

        void createFitableId(String createFitableId) {
            this.values.put(COLUMN_CREATE_FITABLE_ID, createFitableId);
        }

        String patchFitableId() {
            return cast(this.values.get(COLUMN_PATCH_FITABLE_ID));
        }

        void patchFitableId(String patchFitableId) {
            this.values.put(COLUMN_PATCH_FITABLE_ID, patchFitableId);
        }

        String deleteFitableId() {
            return cast(this.values.get(COLUMN_DELETE_FITABLE_ID));
        }

        void deleteFitableId(String deleteFitableId) {
            this.values.put(COLUMN_DELETE_FITABLE_ID, deleteFitableId);
        }

        String retrieveFitableId() {
            return cast(this.values.get(COLUMN_RETRIEVE_FITABLE_ID));
        }

        void retrieveFitableId(String retrieveFitableId) {
            this.values.put(COLUMN_RETRIEVE_FITABLE_ID, retrieveFitableId);
        }

        String listFitableId() {
            return cast(this.values.get(COLUMN_LIST_FITABLE_ID));
        }

        void listFitableId(String listFitableId) {
            this.values.put(COLUMN_LIST_FITABLE_ID, listFitableId);
        }

        void insert(DynamicSqlExecutor executor) {
            int count = InsertSql.custom().into(TABLE)
                    .value(Row.COLUMN_ID, this.id())
                    .value(Row.COLUMN_METADATA, SqlValue.json(this.metadata()))
                    .value(Row.COLUMN_CREATE_FITABLE_ID, this.createFitableId())
                    .value(Row.COLUMN_PATCH_FITABLE_ID, this.patchFitableId())
                    .value(Row.COLUMN_DELETE_FITABLE_ID, this.deleteFitableId())
                    .value(Row.COLUMN_RETRIEVE_FITABLE_ID, this.retrieveFitableId())
                    .value(Row.COLUMN_LIST_FITABLE_ID, this.listFitableId())
                    .execute(executor);
            if (count < 1) {
                throw new ServerInternalException("Failed to insert extension of REFRESH_IN_TIME into database.");
            }
        }

        static Row select(DynamicSqlExecutor executor, String id) {
            SqlBuilder sql = SqlBuilder.custom();
            fillSelectPrefix(sql).append(" WHERE ").appendIdentifier(COLUMN_ID).append(" = ?");
            List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), Collections.singletonList(id));
            if (rows.isEmpty()) {
                return null;
            } else {
                return new Row(rows.get(0));
            }
        }

        static List<Row> select(DynamicSqlExecutor executor, List<String> ids) {
            if (ids.isEmpty()) {
                return Collections.emptyList();
            }
            SqlBuilder sql = SqlBuilder.custom();
            fillSelectPrefix(sql).append(" WHERE ").appendIdentifier(COLUMN_ID).append(" IN (")
                    .appendRepeatedly("?, ", ids.size()).backspace(2).append(")");
            List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), ids);
            return rows.stream().map(Row::new).collect(Collectors.toList());
        }

        private static SqlBuilder fillSelectPrefix(SqlBuilder sql) {
            return sql.append("SELECT ").appendIdentifier(COLUMN_ID)
                    .append(", ").appendIdentifier(COLUMN_METADATA)
                    .append(", ").appendIdentifier(COLUMN_CREATE_FITABLE_ID)
                    .append(", ").appendIdentifier(COLUMN_PATCH_FITABLE_ID)
                    .append(", ").appendIdentifier(COLUMN_DELETE_FITABLE_ID)
                    .append(", ").appendIdentifier(COLUMN_RETRIEVE_FITABLE_ID)
                    .append(", ").appendIdentifier(COLUMN_LIST_FITABLE_ID)
                    .append(" FROM ").appendIdentifier(TABLE);
        }
    }
}
