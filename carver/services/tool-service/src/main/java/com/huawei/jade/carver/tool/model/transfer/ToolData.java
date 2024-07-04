/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.model.transfer;

import com.huawei.jade.carver.tool.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 表示工具的数据内容。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-18
 */
public class ToolData {
    /**
     * 表示工具的创建者。
     * <p>
     *     <ul>
     *         <li>添加工具时可选。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private String creator;

    /**
     * 表示工具的修改者。
     * <p>
     *     <ul>
     *         <li>添加工具时可选。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private String modifier;

    /**
     * 表示工具的名字。
     * <p>
     *     <ul>
     *         <li>添加工具时需传入。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private String name;

    /**
     * 表示工具的描述。
     * <p>
     *     <ul>
     *         <li>添加工具时需传入。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private String description;

    /**
     * 表示工具的唯一标识。
     * <p>
     *     <ul>
     *         <li>添加工具时不需要设置。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private String uniqueName;

    /**
     * 表示工具的格式规范。
     * <p>
     *     <ul>
     *         <li>添加工具时需传入。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private Map<String, Object> schema;

    /**
     * 表示工具的运行描述规范。
     * <p>
     *     <ul>
     *         <li>添加工具时需传入。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private Map<String, Object> runnables;

    /**
     * 表示工具的来源。
     * <p>
     *     <ul>
     *         <li>添加工具时可不传入。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private String source;

    /**
     * 表示工具的图标。
     * <p>
     *     <ul>
     *         <li>添加工具时可不传入。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private String icon;

    /**
     * 表示工具的标签集合。
     * <p>
     *     <ul>
     *         <li>添加工具时需传入。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private Set<String> tags;

    /**
     * 表示工具的版本。
     * <p>
     *     <ul>
     *         <li>添加工具时需传入。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private String version;

    /**
     * 表示当前版本工具是否最新。
     * <p>
     *     <ul>
     *         <li>添加工具时可选。</li>
     *         <li>查询工具时会返回。</li>
     *     </ul>
     * </p>
     */
    private Boolean isLatest;

    /**
     * 获取工具的创建者。
     *
     * @return 表示工具创建者的 {@link String}。
     */
    public String getCreator() {
        return creator;
    }

    /**
     * 设置工具的创建者。
     *
     * @param creator 表示工具创建者的 {@link String}。
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * 获取工具的修改者。
     *
     * @return 表示工具修改者的 {@link String}。
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * 设置工具的修改者。
     *
     * @param modifier 表示工具修改者的 {@link String}。
     */
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    /**
     * 获取工具的名字。
     *
     * @return 表示工具名字的 {@link String}。
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置工具的名字。
     *
     * @param name 表示工具名字的 {@link String}。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取工具的描述。
     *
     * @return 表示工具描述的 {@link String}。
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 设置工具的描述。
     *
     * @param description 表示待设置的工具描述的 {@link String}。
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取工具的唯一标识。
     *
     * @return 表示工具的唯一标识的 {@link String}。
     */
    public String getUniqueName() {
        return this.uniqueName;
    }

    /**
     * 设置工具的唯一标识。
     *
     * @param uniqueName 表示待设置的工具唯一标识的 {@link String}。
     */
    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    /**
     * 获取工具的格式规范。
     *
     * @return 表示工具格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Map<String, Object> getSchema() {
        return this.schema;
    }

    /**
     * 设置工具的格式规范。
     *
     * @param schema 表示待设置的工具格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
    }

    /**
     * 获取工具的格式规范。
     *
     * @return 表示工具格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Map<String, Object> getRunnables() {
        return this.runnables;
    }

    /**
     * 设置工具的格式规范。
     *
     * @param runnables 表示待设置的工具格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public void setRunnables(Map<String, Object> runnables) {
        this.runnables = runnables;
    }

    /**
     * 获取工具的来源。
     *
     * @return 表示工具来源的 {@link String}。
     */
    public String getSource() {
        return this.source;
    }

    /**
     * 设置工具的来源。
     *
     * @param source 表示待设置的工具来源的 {@link String}。
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 设置工具的来源。
     *
     * @return icon 表示待设置的工具图标的 {@link String}。
     */
    public String getIcon() {
        return this.icon;
    }

    /**
     * 设置工具的来源。
     *
     * @param icon 表示待设置的工具来源的 {@link String}。
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取工具的标签集合。
     *
     * @return 表示工具的标签集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public Set<String> getTags() {
        return this.tags;
    }

    /**
     * 设置工具的标签集合。
     *
     * @param tags 表示待设置的工具标签集合的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    /**
     * 获取工具的版本。
     *
     * @return 表示工具的版本的 {@link String}。
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * 设置工具的版本。
     *
     * @param version 表示待设置的工具版本的 {@link String}。
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取工具的可见性。
     *
     * @return 表示工具的可见性的 {@link Boolean}。
     */
    public Boolean getLatest() {
        return this.isLatest;
    }

    /**
     * 设置工具的可见性。
     *
     * @param isLatest 表示待设置的工具可见性的 {@link String}。
     */
    public void setLatest(Boolean isLatest) {
        this.isLatest = isLatest;
    }

    /**
     * 将工具信息转换为 {@link ToolData}。
     *
     * @param info 表示工具信息的 {@link Tool.Info}。
     * @return 表示工具信息的数据内容的 {@link ToolData}。
     */
    public static ToolData from(Tool.Info info) {
        ToolData toolData = new ToolData();
        toolData.setCreator(info.creator());
        toolData.setModifier(info.modifier());
        toolData.setName(info.name());
        toolData.setUniqueName(info.uniqueName());
        toolData.setDescription(info.description());
        toolData.setTags(info.tags());
        toolData.setSchema(info.schema());
        toolData.setRunnables(info.runnables());
        toolData.setSource(info.source());
        toolData.setIcon(info.icon());
        toolData.setVersion(info.version());
        toolData.setLatest(info.isLatest());
        return toolData;
    }

    /**
     * 将工具信息的数据内容转换为工具信息。
     *
     * @param toolData 表示工具信息的传输对象的 {@link ToolData}。
     * @return 表示工具信息的 {@link ToolData}。
     */
    public static Tool.Info convertToInfo(ToolData toolData) {
        return Tool.Info.custom()
                .creator(toolData.getCreator())
                .modifier(toolData.getModifier())
                .name(toolData.getName())
                .uniqueName(toolData.getUniqueName())
                .description(toolData.getDescription())
                .tags(toolData.getTags())
                .schema(toolData.getSchema())
                .runnables(toolData.getRunnables())
                .source(toolData.getSource())
                .icon(toolData.getIcon())
                .version(toolData.getVersion())
                .isLatest(toolData.getLatest())
                .build();
    }

    /**
     * 表示将一组工具转换为工具的传输结构。
     *
     * @param tools 表示待转化的工具列表 {@link List}{@code <}{@link Tool}{@code >}。
     * @return 表示返回的一组工具传输结构列表 {@link List}{@code <}{@link ToolData}{@code >}。
     */
    public static List<ToolData> fromTools(List<Tool> tools) {
        List<ToolData> res = new ArrayList<>();
        for (Tool tool : tools) {
            res.add(ToolData.from(tool.info()));
        }
        return res;
    }
}
