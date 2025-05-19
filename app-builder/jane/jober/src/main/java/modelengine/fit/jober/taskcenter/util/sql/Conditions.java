/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link Condition} 接口提供补充能力。
 *
 * @author 梁济时
 * @since 2023-10-25
 */
class Conditions {
    static class Group implements Condition {
        private final Connector connector;

        private final List<Condition> conditions;

        Group(Connector connector) {
            this.connector = connector;
            this.conditions = new LinkedList<>();
        }

        void add(Condition condition) {
            this.conditions.addAll(this.flat(condition));
        }

        private List<Condition> flat(Condition condition) {
            if (condition instanceof Group) {
                Group group = (Group) condition;
                if (group.connector == this.connector) {
                    return group.conditions;
                }
            }
            return Collections.singletonList(condition);
        }

        @Override
        public void toSql(SqlBuilder sql, List<Object> args) {
            append(sql, args, this.conditions.get(0));
            for (int i = 1; i < this.conditions.size(); i++) {
                sql.append(' ').append(this.connector.toString()).append(' ');
                append(sql, args, this.conditions.get(i));
            }
        }

        @Override
        public boolean isGroup() {
            return true;
        }

        private static void append(SqlBuilder sql, List<Object> args, Condition condition) {
            if (condition.isGroup()) {
                sql.append('(');
                condition.toSql(sql, args);
                sql.append(')');
            } else {
                condition.toSql(sql, args);
            }
        }
    }

    static Condition combine(Condition.Connector connector, Condition[] conditions) {
        List<Condition> actual = Optional.ofNullable(conditions).map(Stream::of).orElseGet(Stream::empty)
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (actual.isEmpty()) {
            return null;
        } else if (actual.size() > 1) {
            Group group = new Group(nullIf(connector, Condition.Connector.AND));
            actual.forEach(group::add);
            return group;
        } else {
            return actual.get(0);
        }
    }
}
