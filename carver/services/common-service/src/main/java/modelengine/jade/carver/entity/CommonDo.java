/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.entity;

/**
 * 数据库表公有字段。
 *
 * @author 鲁为
 * @since 2024-06-11
 */
public class CommonDo {
    /**
     * 表示数据库表的自增主键。
     */
    private String id;

    /**
     * 表示创建时间。
     */
    private String createdTime;

    /**
     * 表示更新时间。
     */
    private String updatedTime;

    /**
     * 表示创建者。
     */
    private String creator;

    /**
     * 表示修改者。
     */
    private String modifier;

    /**
     * 获取唯一标识符。
     *
     * @return 表示唯一标识符的 {@link String}。
     */
    public String getId() {
        return this.id;
    }

    /**
     * 设置唯一标识符。
     *
     * @param id 表示唯一标识符的 {@link String}。
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取创建时间。
     *
     * @return 表示创建时间的 {@link String}。
     */
    public String getCreatedTime() {
        return this.createdTime;
    }

    /**
     * 设置创建时间。
     *
     * @param createdTime 表示创建时间的 {@link String}。
     */
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 获取更新时间。
     *
     * @return 表示更新时间的 {@link String}。
     */
    public String getUpdatedTime() {
        return this.updatedTime;
    }

    /**
     * 设置更新时间。
     *
     * @param updatedTime 表示更新时间的 {@link String}。
     */
    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * 获取创建者。
     *
     * @return 表示创建者的 {@link String}。
     */
    public String getCreator() {
        return this.creator;
    }

    /**
     * 设置创建者。
     *
     * @param creator 表示创建者的 {@link String}。
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * 获取修改者。
     *
     * @return 表示修改者的 {@link String}。
     */
    public String getModifier() {
        return this.modifier;
    }

    /**
     * 设置修改者。
     *
     * @param modifier 表示修改者的 {@link String}。
     */
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
}
