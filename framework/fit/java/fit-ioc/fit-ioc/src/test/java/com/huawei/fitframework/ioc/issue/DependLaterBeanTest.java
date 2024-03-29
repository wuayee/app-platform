/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.ioc.issue;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.ioc.AbstractBeanContainerTest;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@DisplayName("依赖后续待加载的Bean")
class DependLaterBeanTest extends AbstractBeanContainerTest {
    private BeanContainer container;

    @Component
    static class EarlierBean {
        private final LaterBean later;

        EarlierBean(LaterBean later) {
            this.later = later;
        }
    }

    @Component
    static class LaterBean {}

    @BeforeEach
    void setup() {
        this.container = container();
    }

    @Test
    @DisplayName("一个Bean在加载时，依赖一个将在后续被加载的Bean时，可以顺利解析依赖")
    void should_not_throw_when_depend_bean_will_be_loaded_later() {
        this.container.registry().register(EarlierBean.class);
        assertDoesNotThrow(() -> this.container.registry().register(LaterBean.class));
        Optional<EarlierBean> bean = this.container.factory(EarlierBean.class).map(BeanFactory::get);
        assertTrue(bean.isPresent());
        assertNotNull(bean.get().later);
    }
}
