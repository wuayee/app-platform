/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.jade.common.oms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.jade.common.oms.nacos.NacosClient;
import modelengine.jade.common.oms.nacos.support.DefaultNacosClient;
import modelengine.jade.oms.OmsClient;
import modelengine.jade.oms.entity.FileEntity;
import modelengine.jade.oms.entity.NamedEntity;
import modelengine.jade.oms.entity.PartitionedEntity;
import modelengine.jade.oms.entity.TextEntity;
import modelengine.jade.oms.response.ResultVo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 表示 {@link DefaultOmsClient} 的测试集合。
 *
 * @author 李金绪
 * @since 2024-11-30
 */
@MvcTest(classes = {MockController.class})
@DisplayName("测试 DefaultOmsClient")
public class DefaultOmsClinetTest {
    private OmsClient omsClient;

    @Fit
    private MockMvc mockMvc;

    private HttpClassicClientResponse<?> response;

    @BeforeEach
    void setup() throws NoSuchAlgorithmException, KeyManagementException {
        NacosClient nacosClient = mock(DefaultNacosClient.class);
        HttpClassicClientFactory httpClassicClientFactory = mock(HttpClassicClientFactory.class);
        JacksonObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);
        this.omsClient = new DefaultOmsClient(serializer, nacosClient, httpClassicClientFactory);
    }

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Disabled
    @Test
    @DisplayName("当使用 okhttp 发送文件时，成功")
    void shouldOkWhenUploadByOkhttp() throws IOException {
        File file = new File("src/test/resources/test.txt");
        try (InputStream inputStream = new FileInputStream(file)) {
            long length = file.length();
            FileEntity fileEntity = new FileEntity(file.getName(), inputStream, length);
            TextEntity textEntity = new TextEntity("123");
            PartitionedEntity partitionedEntity =
                    new PartitionedEntity(Arrays.asList(new NamedEntity("test.txt", fileEntity),
                            new NamedEntity("456", textEntity)));
            String url = "http://localhost:" + this.mockMvc.getPort() + "/mock/upload";
            ResultVo<String> response =
                    this.omsClient.upload("Framework", HttpRequestMethod.POST, url, partitionedEntity, String.class);
            assertThat(response.getData()).isEqualTo("test.txt;123;");
        }
    }
}
