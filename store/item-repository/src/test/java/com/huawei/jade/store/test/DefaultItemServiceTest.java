/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.jade.store.controller.ItemController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link DefaultItemServiceTest} 的测试类。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-30
 */
public class DefaultItemServiceTest {
    private ItemController controller;

    @BeforeEach
    void setup() {
        this.controller = mock(ItemController.class);
    }

    @Test
    @DisplayName("添加商品应该返回唯一标识。")
    public void shouldReturnUniqueNameWhenAddItems() {
        // given
        String platform = "jade";
        String category = "TOOL";
        String genericableId = "gid";
        String name = "demo";
        List<String> tags = Collections.singletonList("FIT");
        String schema = "{\"description\": \"desc\"}";
        when(this.controller.addItem(platform,
                category,
                genericableId,
                name,
                tags,
                schema)).thenReturn("testUniqueName");

        // when
        String res = this.controller.addItem(platform, category, genericableId, name, tags, schema);

        // then
        assertThat(res).isEqualTo("testUniqueName");
    }

    @Test
    @DisplayName("查询所有商品应该返回所有商品列表。")
    public void shouldReturnAllItemsWhenGetAllItems() {
        // given
        String platform = "jade";
        String category = "TOOL";
        List<String> includeTags = Collections.singletonList("FIT");
        List<String> excludeTags = Collections.singletonList("WATERFLOW");
        int offset = 0;
        int limit = 1;
        when(this.controller.getAllItems(platform, category, includeTags, excludeTags, offset, limit)).thenReturn(
                "testItem");

        // when
        String res = this.controller.getAllItems(platform, category, includeTags, excludeTags, offset, limit);

        // then
        assertThat(res).isEqualTo("testItem");
    }

    @Test
    @DisplayName("查询所有分组应该返回所有分组列表。")
    public void shouldReturnAllGenericableIdsWhenGetItemsByGroup() {
        // given
        String platform = "jade";
        int offset = 0;
        int limit = 1;
        when(this.controller.getAllGenericableIds(platform, offset, limit)).thenReturn("testGenericableIds");

        // when
        String res = this.controller.getAllGenericableIds(platform, offset, limit);

        // then
        assertThat(res).isEqualTo("testGenericableIds");
    }

    @Test
    @DisplayName("根据分组查询商品应该返回商品列表。")
    public void shouldReturnAllItemsWhenGetItemsByGroup() {
        // given
        String platform = "jade";
        String category = "TOOL";
        String genericableId = "gid";
        List<String> includeTags = Collections.singletonList("FIT");
        List<String> excludeTags = Collections.singletonList("WATERFLOW");
        int offset = 0;
        int limit = 1;
        when(this.controller.getItemsByGroup(platform,
                category,
                genericableId,
                includeTags,
                excludeTags,
                offset,
                limit)).thenReturn("testItems");

        // when
        String res = this.controller.getItemsByGroup(platform,
                category,
                genericableId,
                includeTags,
                excludeTags,
                offset,
                limit);

        // then
        assertThat(res).isEqualTo("testItems");
    }

    @Test
    @DisplayName("查询 FitTools 应该返回 FitTools 列表。")
    public void shouldReturnAllFitToolsWhenGetFitTools() {
        // given
        String platform = "jade";
        String genericableId = "gid";
        int offset = 0;
        int limit = 1;
        when(this.controller.getFitTools(platform, genericableId, offset, limit)).thenReturn("testFitTools");

        // when
        String res = this.controller.getFitTools(platform, genericableId, offset, limit);

        // then
        assertThat(res).isEqualTo("testFitTools");
    }

    @Test
    @DisplayName("基于商品的唯一标识查询某个商品应该返回唯一商品。")
    public void shouldReturnOneItemWhenGetItemByUniqueName() {
        // given
        String platform = "jade";
        String uniqueName = "testUniqueName";
        when(this.controller.getItemByUniqueName(platform, uniqueName)).thenReturn("testItem");

        // when
        String res = this.controller.getItemByUniqueName(platform, uniqueName);

        // then
        assertThat(res).isEqualTo("testItem");
    }

    @Test
    @DisplayName("基于商品的分类、分组、名字和标签查询某个商品应该返回唯一商品。")
    public void shouldReturnOneItemWhenGetItemByGroupCategoryNameAndTags() {
        // given
        String platform = "jade";
        String category = "TOOL";
        String genericableId = "gid";
        String name = "demo";
        List<String> includeTags = Collections.singletonList("FIT");
        List<String> excludeTags = Collections.singletonList("WATERFLOW");
        when(this.controller.getItem(platform, category, genericableId, name, includeTags, excludeTags)).thenReturn(
                "testItem");

        // when
        String res = this.controller.getItem(platform, category, genericableId, name, includeTags, excludeTags);

        // then
        assertThat(res).isEqualTo("testItem");
    }
}
