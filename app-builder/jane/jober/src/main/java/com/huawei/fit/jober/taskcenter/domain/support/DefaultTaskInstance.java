/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.support;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.util.Maps;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * 为 {@link TaskInstance} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-12-12
 */
public class DefaultTaskInstance implements TaskInstance {
    private final String id;

    private final TaskEntity task;

    private final TaskType type;

    private final SourceEntity source;

    private final Map<String, Object> info;

    private final List<String> tags;

    private final List<String> categories;

    public DefaultTaskInstance(String id, TaskEntity task, TaskType type, SourceEntity source, Map<String, Object> info,
            List<String> tags, List<String> categories) {
        this.id = id;
        this.task = task;
        this.type = type;
        this.source = source;
        this.info = info;
        this.tags = tags;
        this.categories = categories;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public TaskEntity task() {
        return this.task;
    }

    @Override
    public TaskType type() {
        return this.type;
    }

    @Override
    public SourceEntity source() {
        return this.source;
    }

    @Override
    public Map<String, Object> info() {
        return this.info;
    }

    @Override
    public List<String> tags() {
        return this.tags;
    }

    @Override
    public List<String> categories() {
        return this.categories;
    }

    @Override
    public Map<String, Object> diff(TaskInstance another) {
        if (another == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> thisInfo = this.info;
        Map<String, Object> thatInfo = another.info();
        Set<String> keys = new HashSet<>();
        keys.addAll(thisInfo.keySet());
        keys.addAll(thatInfo.keySet());
        Map<String, Object> diffs = new HashMap<>();
        for (String key : keys) {
            Object thisValue = thisInfo.get(key);
            Object thatValue = thatInfo.get(key);
            if (!this.equals(this.task().getPropertyByName(key), thisValue, thatValue)) {
                diffs.put(key, thatValue);
            }
        }
        return diffs;
    }

    private boolean equals(TaskProperty property, Object value1, Object value2) {
        if (property.dataType().listable()) {
            List<?> list1 = nullIf(cast(value1), Collections.emptyList());
            List<?> list2 = nullIf(cast(value2), Collections.emptyList());
            return list1.size() == list2.size() && IntStream.range(0, list1.size())
                    .allMatch(index -> Objects.equals(list1.get(index), list2.get(index)));
        } else {
            return Objects.equals(value1, value2);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == this.getClass()) {
            DefaultTaskInstance that = (DefaultTaskInstance) obj;
            return Objects.equals(this.id(), that.id())
                    && Objects.equals(this.task(), that.task())
                    && Objects.equals(this.type(), that.type())
                    && Objects.equals(this.source(), that.source())
                    && Maps.equals(this.info(), that.info())
                    && CollectionUtils.equals(this.tags(), that.tags())
                    && CollectionUtils.equals(this.categories(), that.categories());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {
                this.getClass(), this.id(), this.task(), this.type(), this.source(),
                this.info(), this.tags(), this.categories()
        });
    }

    @Override
    public String toString() {
        return StringUtils.format("[id={0}, task={1}, type={2}, source={3}, info={4}, tags={5}, categories={6}]",
                this.id(), this.task(), this.type(), this.source(), this.info(), this.tags(), this.categories());
    }

    /**
     * 为 {@link TaskInstance.Builder} 提供默认实现。
     *
     * @author 梁济时
     * @since 2023-12-12
     */
    public static class Builder implements TaskInstance.Builder {
        private String id;

        private TaskEntity task;

        private TaskType type;

        private SourceEntity source;

        private Map<String, Object> info;

        private List<String> tags;

        private List<String> categories;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public TaskInstance.Builder task(TaskEntity task) {
            this.task = task;
            return this;
        }

        @Override
        public Builder type(TaskType type) {
            this.type = type;
            return this;
        }

        @Override
        public Builder source(SourceEntity source) {
            this.source = source;
            return this;
        }

        @Override
        public Builder info(Map<String, Object> info) {
            this.info = info;
            return this;
        }

        @Override
        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        @Override
        public Builder categories(List<String> categories) {
            this.categories = categories;
            return this;
        }

        @Override
        public TaskInstance build() {
            return new DefaultTaskInstance(this.id, this.task, this.type, this.source, this.info, this.tags,
                    this.categories);
        }
    }

    /**
     * 为 {@link TaskInstance.Declaration} 提供默认实现。
     *
     * @author 梁济时
     * @since 2023-12-12
     */
    public static class Declaration implements TaskInstance.Declaration {
        private final UndefinableValue<String> typeId;

        private final UndefinableValue<String> sourceId;

        private final UndefinableValue<Map<String, Object>> info;

        private final UndefinableValue<List<String>> tags;

        public Declaration(UndefinableValue<String> typeId, UndefinableValue<String> sourceId,
                UndefinableValue<Map<String, Object>> info, UndefinableValue<List<String>> tags) {
            this.typeId = typeId;
            this.sourceId = sourceId;
            this.info = info;
            this.tags = tags;
        }

        @Override
        public UndefinableValue<String> typeId() {
            return this.typeId;
        }

        @Override
        public UndefinableValue<String> sourceId() {
            return this.sourceId;
        }

        @Override
        public UndefinableValue<Map<String, Object>> info() {
            return this.info;
        }

        @Override
        public UndefinableValue<List<String>> tags() {
            return this.tags;
        }

        /**
         * 为 {@link TaskInstance.Declaration.Builder} 提供默认实现。
         *
         * @author 梁济时
         * @since 2023-12-12
         */
        public static class Builder implements TaskInstance.Declaration.Builder {
            private UndefinableValue<String> typeId;

            private UndefinableValue<String> sourceId;

            private UndefinableValue<Map<String, Object>> info;

            private UndefinableValue<List<String>> tags;

            /**
             * 任务实例声明构造器
             *
             * @param declaration 任务实例声明
             */
            public Builder(TaskInstance.Declaration declaration) {
                if (declaration == null) {
                    this.typeId = UndefinableValue.undefined();
                    this.sourceId = UndefinableValue.undefined();
                    this.info = UndefinableValue.undefined();
                    this.tags = UndefinableValue.undefined();
                } else {
                    this.typeId = declaration.typeId();
                    this.sourceId = declaration.sourceId();
                    this.info = declaration.info();
                    this.tags = declaration.tags();
                }
            }

            @Override
            public Builder type(String typeId) {
                this.typeId = UndefinableValue.defined(typeId);
                return this;
            }

            @Override
            public Builder source(String sourceId) {
                this.sourceId = UndefinableValue.defined(sourceId);
                return this;
            }

            @Override
            public Builder info(Map<String, Object> info) {
                this.info = UndefinableValue.defined(info);
                return this;
            }

            @Override
            public Builder tags(List<String> tags) {
                this.tags = UndefinableValue.defined(tags);
                return this;
            }

            @Override
            public TaskInstance.Declaration build() {
                return new Declaration(this.typeId, this.sourceId, this.info, this.tags);
            }
        }
    }

    /**
     * 为 {@link TaskInstance.Filter} 提供默认实现。
     *
     * @author 梁济时
     * @since 2023-12-12
     */
    public static class Filter implements TaskInstance.Filter {
        private final List<String> ids;

        private final List<String> typeIds;

        private final List<String> sourceIds;

        private final Map<String, List<String>> infos;

        private final List<String> tags;

        private final List<String> categories;

        private final boolean isDeleted;

        public Filter(List<String> ids, List<String> typeIds, List<String> sourceIds, Map<String, List<String>> infos,
                List<String> tags, List<String> categories, boolean isDeleted) {
            this.ids = Entities.canonicalizeStringList(ids);
            this.typeIds = Entities.canonicalizeStringList(typeIds);
            this.sourceIds = Entities.canonicalizeStringList(sourceIds);
            this.infos = canonicalizeInfos(infos);
            this.tags = Entities.canonicalizeStringList(tags);
            this.categories = Entities.canonicalizeStringList(categories);
            this.isDeleted = isDeleted;
        }

        private static Map<String, List<String>> canonicalizeInfos(Map<String, List<String>> infos) {
            if (infos == null) {
                return Collections.emptyMap();
            }
            Map<String, List<String>> actual = new LinkedHashMap<>(infos.size());
            for (Map.Entry<String, List<String>> entry : infos.entrySet()) {
                String key = StringUtils.trim(entry.getKey());
                List<String> values = Entities.canonicalizeStringList(entry.getValue());
                if (StringUtils.isNotEmpty(key) && CollectionUtils.isNotEmpty(values)) {
                    actual.put(key, values);
                }
            }
            return actual;
        }

        @Override
        public List<String> ids() {
            return this.ids;
        }

        @Override
        public List<String> typeIds() {
            return this.typeIds;
        }

        @Override
        public List<String> sourceIds() {
            return this.sourceIds;
        }

        @Override
        public Map<String, List<String>> infos() {
            return this.infos;
        }

        @Override
        public List<String> tags() {
            return this.tags;
        }

        @Override
        public List<String> categories() {
            return this.categories;
        }

        @Override
        public boolean deleted() {
            return this.isDeleted;
        }

        /**
         * 为 {@link TaskInstance.Filter.Builder} 提供默认实现。
         *
         * @author 梁济时
         * @since 2023-12-12
         */
        public static class Builder implements TaskInstance.Filter.Builder {
            private List<String> ids = Collections.emptyList();

            private List<String> typeIds = Collections.emptyList();

            private List<String> sourceIds = Collections.emptyList();

            private Map<String, List<String>> infos = Collections.emptyMap();

            private List<String> tags = Collections.emptyList();

            private List<String> categories = Collections.emptyList();

            private boolean isDeleted = false;

            @Override
            public Builder ids(List<String> ids) {
                this.ids = nullIf(ids, Collections.emptyList());
                return this;
            }

            @Override
            public Builder typeIds(List<String> typeIds) {
                this.typeIds = nullIf(typeIds, Collections.emptyList());
                return this;
            }

            @Override
            public Builder sourceIds(List<String> sourceIds) {
                this.sourceIds = nullIf(sourceIds, Collections.emptyList());
                return this;
            }

            @Override
            public Builder infos(Map<String, List<String>> infos) {
                this.infos = nullIf(infos, Collections.emptyMap());
                return this;
            }

            @Override
            public Builder tags(List<String> tags) {
                this.tags = nullIf(tags, Collections.emptyList());
                return this;
            }

            @Override
            public Builder categories(List<String> categories) {
                this.categories = nullIf(categories, Collections.emptyList());
                return this;
            }

            @Override
            public TaskInstance.Filter.Builder deleted(boolean isDeleted) {
                this.isDeleted = isDeleted;
                return this;
            }

            @Override
            public TaskInstance.Filter build() {
                return new Filter(this.ids, this.typeIds, this.sourceIds, this.infos, this.tags, this.categories,
                        this.isDeleted);
            }
        }
    }
}
