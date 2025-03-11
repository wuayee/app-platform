/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useRef, useState } from 'react';
import { Upload } from 'antd';
import { uploadMultipleFile } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { useTranslation } from 'react-i18next';
import uploadImg from '@/assets/images/upload_icon.svg';
const supportFileTypes = ['pdf', 'txt', 'docx', 'md', 'markdown', 'html', 'png', 'jpg', 'jpeg', 'wav'];
const pictureTypes = ['png', 'jpg', 'jpeg'];

const { Dragger } = Upload;
/**
 * 对话框上传文件弹框组件
 *
 * @param fileList 多文件列表.
 * @param updateFileList 更新文件列表的方法.
 * @param updateModal 修改外层modal的打开状态.
 * @return {JSX.Element}
 * @constructor
 */
const UploadFile = (props) => {
  const { fileList, updateFileList, updateModal } = props;
  const { t } = useTranslation();
  const [addFileList, setAddFileList] = useState([]);
  const isWarnLimit = useRef(false);

  const beforeUpload = async (curFile, curfileList) => {
    if (isWarnLimit.current) {
      if (curfileList.length + fileList.length <= 5) {
        isWarnLimit.current = false;
      } else {
        return;
      }
    }
    if (curfileList.length + fileList.length > 5 && !isWarnLimit.current) {
      isWarnLimit.current = true;
      Message({ type: 'warning', content: t('currentAvailableUpload') + (5 - fileList.length) + t('num') + t('file') });
      return;
    }
    curfileList.forEach(file => {
      let suffix = '';
      try {
        const fileArr = file.name.split('.');
        suffix = fileArr.pop();
      } catch {
        suffix = '';
      }
      file.file_type = suffix;
      file.file_name = file.name;
      file.uploadStatus = file.size / (1024 * 15) > 1024 ? 'failed' : 'uploading';
      if (file.uploadStatus === 'failed') {
        file.failedReason = t('uploadExceedLimit');
      }
      if (fileList.find(item => item.file_name === file.name)) {
        Message({ type: 'warning', content: t('alreadyUploaded') + file.name });
      }
      if (pictureTypes.includes(file.file_type.toLocaleLowerCase())) {
        file.file_url = URL.createObjectURL(file);
      }
    });
    curfileList = curfileList.filter(item => {
      return supportFileTypes.includes(item.file_type.toLocaleLowerCase()) && !fileList.find(it => it.file_name === item.name);
    });
    if (curfileList.length) {
      setAddFileList(curfileList);
      updateFileList([...fileList, ...curfileList]);
      if (updateModal) {
        updateModal(false);
      }
    }
    return false;
  };

  const getFileType = () => {
    const typeList = supportFileTypes.map(item => '.' + item);
    return typeList.join();
  };

  // 批量上传文件
  const uploadFiles = async () => {
    const formData = new FormData();
    addFileList.filter(item => item.uploadStatus !== 'failed').forEach((file => {
      formData.append('file', file);
    }))
    let resultData = [];
    try {
      const result = await uploadMultipleFile(formData);
      if (result.code === 0 && result.data) {
        resultData = result.data;
      }
    } finally {
      updateFileList((preList) => {
        const curList = preList.map(file => {
          const resultItem = resultData.find(item => item.file_name === file.name);
          if (resultItem) {
            return Object.assign(file, resultItem, { uploadStatus: 'success' });
          } else {
            const addItem = addFileList.find(item => item.name === file.name);
            if (addItem) {
              return Object.assign(file, { uploadStatus: 'failed' });
            } else {
              return file;
            }
          }
        })
        return curList;
      });
    }
  }

  const handleClick = (e) => {
    if (fileList.length > 4) {
      Message({ type: 'error', content: t('clickUploadCountTip') });
    }
  };

  useEffect(() => {
    if (addFileList.length) {
      // 发送请求
      uploadFiles();
    }
  }, [addFileList]);
  return <>{(
    <div className='dragger-modal'>
      <Dragger
        fileList={[]}
        multiple
        maxCount={5}
        accept={getFileType()}
        beforeUpload={beforeUpload}
        openFileDialogOnClick={fileList.length < 5}
      >
        <div className='import-upload' onClick={handleClick}>
          <img src={uploadImg} />
          <div className='upload-word'>{t('fileUploadContent1')}</div>
          <div className='tips'>{t('chatSupportedFileTypes')}</div>
          <div className='tips'>
            <span>{t('chatFileSizeTip')}</span>
            <span>{t('chatFileCountTip')}</span>
            <span>{t('currentAvailableUpload')}</span>
            <span className='available-count'>{5 - fileList.length}</span>
            <span>{t('num') + t('file')}</span>
          </div>
        </div>
      </Dragger>
    </div>
  )}</>
};


export default UploadFile;
