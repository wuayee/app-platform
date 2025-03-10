/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.transaction.Transactional;
import modelengine.jade.store.repository.pgsql.entity.TagDo;
import modelengine.jade.store.repository.pgsql.repository.TagRepository;
import modelengine.jade.store.service.TagService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 标签的 Http 请求的服务层实现。
 *
 * @author 李金绪
 * @since 2024-09-13
 */
@Component
public class DefaultTagService implements TagService {
    private static final String FITABLE_ID = "store-repository-pgsql";

    private final TagRepository tagRepo;

    /**
     * 通过持久层接口来初始化 {@link DefaultStoreToolService} 的实例。
     *
     * @param tagRepo 表示标签的持久层接口的 {@link TagRepository}。
     */
    public DefaultTagService(TagRepository tagRepo) {
        this.tagRepo = notNull(tagRepo, "The tag repository can not be null.");
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public Set<String> getTags(String uniqueName) {
        return this.tagRepo.getTags(uniqueName).stream().map(TagDo::getName).collect(Collectors.toSet());
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void addTags(Set<String> tags, String uniqueName) {
        this.tagRepo.addTags(tags, uniqueName);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void deleteTagByUniqueName(String toolUniqueName) {
        this.tagRepo.deleteTagByUniqueName(toolUniqueName);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void addTagsList(List<Set<String>> tagsList, List<String> uniqueNameList) {
        this.tagRepo.addTagsList(tagsList, uniqueNameList);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void updateAppTag(String appTag, String uniqueName) {
        this.tagRepo.updateAppTag(appTag, uniqueName);
    }
}
