/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.repository.support;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import com.huawei.jade.store.repository.pgsql.entity.TagDo;
import com.huawei.jade.store.repository.pgsql.mapper.TagMapper;
import com.huawei.jade.store.repository.pgsql.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 标签的仓库。
 *
 * @author 鲁为
 * @since 2024-07-20
 */
@Component
public class DefaultTagRepository implements TagRepository {
    private final TagMapper tagMapper;

    /**
     * 根据标签持久层实例构造 {@link DefaultTagRepository} 的实例。
     *
     * @param tagMapper 表示标签持久层实例的 {@link TagMapper}。
     */
    public DefaultTagRepository(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    @Transactional
    public void addTags(Set<String> tags, String uniqueName) {
        if (CollectionUtils.isEmpty(tags)) {
            return;
        }
        Set<String> tagNames = tags.stream().map(StringUtils::toUpperCase).collect(Collectors.toSet());
        List<TagDo> tagDos = new ArrayList<>();
        tagNames.forEach(tagName -> tagDos.add(new TagDo(uniqueName, tagName)));
        this.tagMapper.addTags(tagDos);
    }

    @Override
    public List<TagDo> getTags(String uniqueName) {
        return this.tagMapper.getTags(uniqueName);
    }

    @Override
    @Transactional
    public void deleteTagByUniqueName(String toolUniqueName) {
        this.tagMapper.deleteTagByUniqueName(toolUniqueName);
    }
}
