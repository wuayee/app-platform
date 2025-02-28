/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.repository.support;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.store.repository.pgsql.entity.TagDo;
import modelengine.jade.store.repository.pgsql.mapper.TagMapper;
import modelengine.jade.store.repository.pgsql.repository.TagRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        List<TagDo> tagDos = tags.stream()
                .map(StringUtils::toUpperCase)
                .map(tagName -> new TagDo(uniqueName, tagName))
                .collect(Collectors.toList());
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

    @Transactional
    @Override
    public void addTagsList(List<Set<String>> tagsList, List<String> uniqueNameList) {
        if (CollectionUtils.isEmpty(tagsList) || CollectionUtils.isEmpty(uniqueNameList)) {
            return;
        }
        List<TagDo> tagDos = IntStream.range(0, uniqueNameList.size()).mapToObj(i -> {
            String uniqueName = uniqueNameList.get(i);
            Set<String> tags = tagsList.get(i);
            return tags.stream()
                    .map(StringUtils::toUpperCase)
                    .map(tagName -> new TagDo(uniqueName, tagName))
                    .collect(Collectors.toList());
        }).flatMap(List::stream).collect(Collectors.toList());
        this.tagMapper.addTags(tagDos);
    }
}
