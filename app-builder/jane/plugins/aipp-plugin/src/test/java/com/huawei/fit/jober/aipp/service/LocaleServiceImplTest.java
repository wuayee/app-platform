/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

import com.huawei.fit.jober.aipp.mapper.I18nMapper;
import com.huawei.fit.jober.aipp.po.I18nPo;
import com.huawei.fit.jober.aipp.repository.I18nRepository;
import com.huawei.fit.jober.aipp.repository.impl.I18nRepositoryImpl;
import com.huawei.fit.jober.aipp.service.impl.LocaleServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 表示 {@link LocaleService} 的单元测试。
 *
 * @author 陈潇文
 * @since 2024-08-20
 */
@DisplayName("测试LocaleService")
public class LocaleServiceImplTest {
    private I18nMapper i18nMapper;
    private LocaleServiceImplTestExt localeServiceImplTestExt;

    @BeforeEach
    void setUp() {
        this.i18nMapper = mock(I18nMapper.class);
        I18nRepositoryImpl i18nRepositoryImpl = new I18nRepositoryImpl(this.i18nMapper);
        this.localeServiceImplTestExt = new LocaleServiceImplTestExt(i18nRepositoryImpl);
    }

    @Test
    @DisplayName("查询字段国际化信息时，返回正确结果")
    void shouldSuccessWhenGetLocaleResource() {
        List<I18nPo> i18nPoList = new ArrayList<>();
        i18nPoList.add(new I18nPo("123", "name", "zh", "张三"));
        i18nPoList.add(new I18nPo("123", "name", "en", "ZhangSan"));
        Mockito.when(this.i18nMapper.selectResource()).thenReturn(i18nPoList);
        this.localeServiceImplTestExt.loadResource();
        assertThat(this.localeServiceImplTestExt.getLocaleMessage("name", Locale.ENGLISH)).isEqualTo("ZhangSan");
        assertThat(this.localeServiceImplTestExt.getLocaleMessage("name", Locale.CHINESE)).isEqualTo("张三");
    }

    private static class LocaleServiceImplTestExt extends LocaleServiceImpl {
        public LocaleServiceImplTestExt(I18nRepository i18nRepository) {
            super(i18nRepository);
        }

        @Override
        public void loadResource() {
            super.loadResource();
        }
    }
}
