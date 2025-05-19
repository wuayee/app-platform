/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util.sql;

/**
 * 为可命名的对象提供基类。
 *
 * @author 梁济时
 * @since 2023-12-11
 */
public class AbstractNameable implements Nameable {
    private final String name;

    private final String alias;

    public AbstractNameable(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String alias() {
        return this.alias;
    }
}
