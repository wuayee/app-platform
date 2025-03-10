/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.service.impl;

import static modelengine.fit.jade.aipp.document.code.DocumentExtractRetCode.DOCUMENT_EXTRACT_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;

import modelengine.fit.jade.aipp.document.exception.DocumentExtractException;
import modelengine.fit.jade.aipp.document.extractor.AudioExtractor;
import modelengine.fit.jade.aipp.document.extractor.BaseExtractor;
import modelengine.fit.jade.aipp.document.extractor.ImageExtractor;
import modelengine.fit.jade.aipp.document.extractor.TextExtractor;
import modelengine.fit.jade.aipp.document.param.FileExtractionParam;
import modelengine.fit.jade.aipp.document.service.DocumentExtractService;
import modelengine.fit.jade.aipp.document.utils.ContentUtils;
import modelengine.fit.jober.aipp.entity.FileExtensionEnum;
import modelengine.fit.jober.aipp.service.OperatorService.FileType;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文档提取节点服务。
 *
 * @author 马朝阳
 * @since 2024-12-12
 */
@Component
public class DocumentExtractServiceImpl implements DocumentExtractService {
    private static final Logger LOG = Logger.get(DocumentExtractServiceImpl.class);

    private final Set<FileType> textTypeSet = new HashSet<>(Arrays.asList(FileType.MARKDOWN,
            FileType.HTML,
            FileType.TXT,
            FileType.WORD,
            FileType.EXCEL,
            FileType.PDF));
    private final TextExtractor textExtractor;
    private final ImageExtractor imageExtractor;
    private final AudioExtractor audioExtractor;
    private final LazyLoader<Map<FileType, BaseExtractor>> repository;

    private final ObjectMapper objectMapper;

    public DocumentExtractServiceImpl(TextExtractor textExtractor, ImageExtractor imageExtractor,
            AudioExtractor audioExtractor, BeanContainer container) {
        this.textExtractor = textExtractor;
        this.imageExtractor = imageExtractor;
        this.audioExtractor = audioExtractor;
        this.objectMapper = new ObjectMapper();
        this.repository = new LazyLoader<>(() -> container.all(BaseExtractor.class)
                .stream()
                .map(BeanFactory::<BaseExtractor>get)
                .collect(Collectors.toMap(BaseExtractor::type,
                        Function.identity(),
                        (k1, k2) -> {
                    throw new IllegalStateException(StringUtils.format("Duplicate type {0}", k1));
                })));
    }

    @Override
    @Fitable("document.service.extract")
    public String invoke(FileExtractionParam fileExtractionParam) {
        StringBuilder fileContent = new StringBuilder();
        Map<String, Object> context = objectMapper.convertValue(fileExtractionParam, Map.class);

        if (fileExtractionParam.getFiles() == null) {
            return StringUtils.EMPTY;
        }

        for (String fileUrl : fileExtractionParam.getFiles()) {
            FileType fileType = FileExtensionEnum.findType(fileUrl)
                    .orElseThrow(() -> new DocumentExtractException(DOCUMENT_EXTRACT_ERROR, fileUrl));
            if (textTypeSet.contains(fileType)) {
                fileType = FileType.TXT;
            }
            BaseExtractor extractor = this.repository.get().get(fileType);
            fileContent.append(ContentUtils.buildContent(ContentUtils.getFileName(fileUrl),
                    extractor.extract(fileUrl, context)));
        }
        return fileContent.toString();
    }
}
