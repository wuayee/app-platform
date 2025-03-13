/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.document.exception.DocumentExtractException;
import modelengine.fit.jade.aipp.document.extractor.AudioExtractor;
import modelengine.fit.jade.aipp.document.extractor.ImageExtractor;
import modelengine.fit.jade.aipp.document.extractor.TextExtractor;
import modelengine.fit.jade.aipp.document.param.FileExtractionParam;
import modelengine.fit.jade.aipp.document.service.impl.DocumentExtractServiceImpl;
import modelengine.fit.jade.aipp.document.utils.ContentUtils;
import modelengine.fit.jober.aipp.service.OperatorService.FileType;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * 表示 {@link DocumentExtractServiceImpl} 的测试类。
 *
 * @author 兰宇晨
 * @since 2025-01-15
 */
@FitTestWithJunit(includeClasses = DocumentExtractServiceImpl.class)
public class DocumentExtractServiceImplTest {
    @Fit
    private DocumentExtractService documentExtractService;

    @Mock
    private TextExtractor textExtractor;

    @Mock
    private ImageExtractor imageExtractor;

    @Mock
    private AudioExtractor audioExtractor;

    @Test
    @DisplayName("测试解析文件成功")
    void shouldReturnOKWhenInvokeDocumentExtract() {
        String imageUrl = "/mock/mock.jpg";
        String docUrl = "/mock/mock.docx";
        String audioUrl = "/mock/mock.mp3";
        when(this.textExtractor.extract(eq(docUrl), anyMap())).thenReturn("textContent");
        when(this.imageExtractor.extract(eq(imageUrl), anyMap())).thenReturn("imageContent");
        when(this.audioExtractor.extract(eq(audioUrl), anyMap())).thenReturn("audioContent");
        when(this.textExtractor.type()).thenReturn(FileType.TXT);
        when(this.imageExtractor.type()).thenReturn(FileType.IMAGE);
        when(this.audioExtractor.type()).thenReturn(FileType.AUDIO);
        FileExtractionParam param = new FileExtractionParam();
        param.setFiles(Arrays.asList(docUrl, imageUrl, audioUrl));
        param.setPrompt("");
        String content = documentExtractService.invoke(param);
        assertThat(content).isEqualTo(
                ContentUtils.buildContent("mock.docx", "textContent") + ContentUtils.buildContent("mock.jpg",
                        "imageContent") + ContentUtils.buildContent("mock.mp3", "audioContent"));
    }

    @Test
    @DisplayName("测试解析非法文件，抛出异常")
    void shouldReturnNotOKWhenInvokeDocumentExtract() {
        String imageUrl = "http://mock/mock.illegal";
        FileExtractionParam param = new FileExtractionParam();
        param.setFiles(Arrays.asList(imageUrl));
        param.setPrompt("");
        AssertionsForClassTypes.assertThatThrownBy(() -> this.documentExtractService.invoke(param))
                .isInstanceOf(DocumentExtractException.class);
    }
}
