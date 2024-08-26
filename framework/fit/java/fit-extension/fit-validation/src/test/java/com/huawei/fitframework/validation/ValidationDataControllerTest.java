/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.validation;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.MvcTest;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import com.huawei.fitframework.test.domain.mvc.request.MockRequestBuilder;
import com.huawei.fitframework.validation.data.Person;
import com.huawei.fitframework.validation.data.Product;
import com.huawei.fitframework.validation.data.ValidationDataController;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * {@link ValidationDataController} 的测试集。
 *
 * @author 吕博文
 * @since 2024-08-15
 */
@MvcTest(classes = {ValidationDataController.class})
@DisplayName("测试 EvalDataController")
public class ValidationDataControllerTest {
    @Fit
    private MockMvc mockMvc;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("合法 Person 对象校验")
    void shouldOKWhenCreateValidPerson() {
        Person validPerson = new Person(1, 1, "Mike", "male", -1, -1);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/validation/person/default")
                .jsonEntity(validPerson)
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("不合法 Person 对象校验")
    void shouldFailedWhenCreateInvalidPerson() {
        Person invalidPerson = new Person(0, 3, "", "abd", -1, -1);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/validation/person/default")
                .jsonEntity(invalidPerson)
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("自定义分组校验 Person 对象")
    void shouldOKWhenCreateValidPersonWithPersonGroup() {
        Person invalidPerson = new Person(0, 3, "", "abd", 50, -1);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/validation/person/personGroup")
                .jsonEntity(invalidPerson)
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("自定义分组校验 Person 对象")
    void shouldFailedWhenCreateInvalidPersonWithPersonGroup() {
        Person invalidPerson = new Person(0, 3, "", "abd", -1, -1);
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/validation/person/personGroup")
                .jsonEntity(invalidPerson)
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("合法 Product 对象校验")
    void shouldOKWhenCreateValidProduct() {
        Product product = new Product("mac", 10499.0, 100, "computer");
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/validation/product/default").jsonEntity(product).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("不合法 Product 对象校验")
    void shouldFailedWhenCreateInvalidProduct() {
        Product product = new Product("", 10499.0, -1, "computer");
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/validation/product/default").jsonEntity(product).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }
}