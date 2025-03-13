/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.northbound;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.dto.chat.FileUploadInfo;
import modelengine.fit.jober.aipp.genericable.adapter.FileServiceAdapter;
import modelengine.fit.jober.aipp.service.FileService;
import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fit.jober.aipp.util.MetaUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.beans.BeanUtils;

import java.io.IOException;
import java.util.List;

/**
 * {@link FileService} 的适配器类的实现类。
 *
 * @author 曹嘉美
 * @since 2024-12-20
 */
@Component
public class FileServiceAdapterImpl implements FileServiceAdapter {
    private final FileService fileService;

    private final MetaService metaService;

    public FileServiceAdapterImpl(FileService fileService, MetaService metaService) {
        this.fileService = notNull(fileService, "The file service cannot be null.");
        this.metaService = notNull(metaService, "The meta service cannot be null.");
    }

    @Override
    public FileUploadInfo uploadFile(OperationContext context, String tenantId, String fileName, String appId,
            PartitionedEntity receivedFile) throws IOException {
        String aippId;
        try {
            aippId = MetaUtils.getAippIdByAppId(this.metaService, appId, context);
        } catch (AippTaskNotFoundException e) {
            throw new AippException(AippErrCode.APP_NOT_FOUND);
        }
        List<FileEntity> files = AippFileUtils.getFileEntity(receivedFile);
        if (files.isEmpty()) {
            throw new AippException(AippErrCode.UPLOAD_FAILED);
        }
        return BeanUtils.copyProperties(this.fileService.uploadFile(context, tenantId, fileName, aippId, files.get(0)),
                FileUploadInfo.class);
    }
}
