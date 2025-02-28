/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { Button, Upload } from 'antd';
import { ToTopOutlined } from '@ant-design/icons';
import { fileValidate } from '@/shared/utils/common';
import { httpUrlMap } from '@/shared/http/httpConfig';
import { uploadImage } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { useAppSelector } from '@/store/hook';
import { useTranslation } from 'react-i18next';

const ImgUpload = () => {
  const { t } = useTranslation();
  const [filePath, setFilePath] = useState('');
  const [fileName, setFileName] = useState('');
  const { AIPP_URL } =
    process.env.NODE_ENV === 'development'
      ? { AIPP_URL: `${window.location.origin}/api/jober/v1/api` }
      : httpUrlMap[process.env.NODE_ENV];
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  // 上传图片
  async function pictureUpload(file) {
    const headers = {
      'attachment-filename': encodeURI(file.name || ''),
    };
    try {
      const formData = new FormData();
      formData.append('file', file);
      const res = await uploadImage(tenantId, formData, headers);
      if (res.code === 0) {
        setFileName(res.data.file_name);
        setFilePath(res.data.file_path);
      }
    } catch (err) {
      Message({ type: 'error', content: err.message || t('uploadImageFail') });
    }
  }
  const beforeUpload = (file) => false;
  const onChange = ({ file }) => {
    let validateResult = fileValidate(file);
    if (!validateResult) {
      setFilePath('');
    }
    validateResult && pictureUpload(file);
  };

  return (
    <>
      <div className='avatar'>
        {filePath ? (
          <img
            className='img-send-item'
            src={`${AIPP_URL}/${tenantId}/file?filePath=${filePath}&fileName=${fileName}`}
          />
        ) : (
          <img src='./src/assets/images/knowledge/knowledge-base.png' />
        )}
        <Upload
          beforeUpload={beforeUpload}
          onChange={onChange}
          showUploadList={false}
          accept='.jpg,.png,.gif,.jpeg'
        >
          <Button size='small' icon={<ToTopOutlined />}>
            {t('uploadManually')}
          </Button>
        </Upload>
      </div>
    </>
  );
};

export default ImgUpload;
