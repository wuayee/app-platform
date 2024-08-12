/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.service.entity;

import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示服务实现的元数据信息。
 *
 * @author 季聿阶
 * @since 2023-05-06
 */
public class FitableMeta {
    private FitableInfo fitable;
    private List<String> aliases = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<Integer> formats = new ArrayList<>();

    /**
     * 获取服务实现。
     *
     * @return 表示服务实现的 {@link FitableInfo}。
     */
    public FitableInfo getFitable() {
        return this.fitable;
    }

    /**
     * 设置服务实现。
     *
     * @param fitable 表示服务实现的 {@link FitableInfo}。
     */
    public void setFitable(FitableInfo fitable) {
        this.fitable = fitable;
    }

    /**
     * 获取服务实现的别名列表。
     *
     * @return 表示服务实现的别名列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public List<String> getAliases() {
        return this.aliases;
    }

    /**
     * 设置服务实现的别名列表。
     *
     * @param aliases 表示待设置的服务实现的别名列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public void setAliases(List<String> aliases) {
        this.aliases = getIfNull(aliases, ArrayList::new);
    }

    /**
     * 获取服务实现的标签列表。
     *
     * @return 表示服务实现的标签列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public List<String> getTags() {
        return this.tags;
    }

    /**
     * 设置服务实现的标签列表。
     *
     * @param tags 表示待设置的服务实现的标签列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public void setTags(List<String> tags) {
        this.tags = getIfNull(tags, ArrayList::new);
    }

    /**
     * 获取服务支持的序列化方式列表。
     *
     * @return 表示服务支持的序列化方式的列表的 {@link List}{@code <}{@link Integer}{@code >}。
     */
    public List<Integer> getFormats() {
        return this.formats;
    }

    /**
     * 设置服务支持的序列化方式列表。
     *
     * @param formats 表示服务支持的序列化方式的列表的 {@link List}{@code <}{@link Integer}{@code >}。
     */
    public void setFormats(List<Integer> formats) {
        this.formats = getIfNull(formats, ArrayList::new);
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (another == null || this.getClass() != another.getClass()) {
            return false;
        }
        FitableMeta that = ObjectUtils.cast(another);
        return Objects.equals(this.fitable, that.fitable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fitable);
    }

    @Override
    public String toString() {
        String aliasesContent = this.getAliases()
                .stream()
                .map(alias -> StringUtils.surround(alias, '"'))
                .collect(Collectors.joining(", ", "[", "]"));
        String tagsContent = this.getTags()
                .stream()
                .map(alias -> StringUtils.surround(alias, '"'))
                .collect(Collectors.joining(", ", "[", "]"));
        return StringUtils.format("/{\"fitable\": {0}, \"aliases\": {1}, \"tags\": {2}, \"formats\": {3}/}",
                this.getFitable(),
                aliasesContent,
                tagsContent,
                this.getFormats());
    }
}
