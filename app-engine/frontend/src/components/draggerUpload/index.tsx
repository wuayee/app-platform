import { InboxOutlined } from '@ant-design/icons';
import { Upload } from 'antd';
import React, { useEffect, useState } from 'react';

const DraggerUpload = (props) => {
  const { accept } = props;
  const uploadProps: UploadProps = {
    name: 'files',
    action: './',
    onChange(info) {
      const { status } = info.file;
      if (status !== 'uploading') {
        console.log(info.file, info.fileList);
      }
      if (status === 'done') {
        message.success(`${info.file.name} file uploaded successfully.`);
        return;
      }
      if (status === 'error') {
        message.error(`${info.file.name} file upload failed.`);
      }
    },
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files);
    },
    ...props,
  };
  return (
    <Upload.Dragger {...uploadProps}>
      <p className='ant-upload-drag-icon'>
        <InboxOutlined />
      </p>
      <p className='ant-upload-text'>将文件拖到这里，或者点击上传</p>
      <p className='ant-upload-hint'>支持 {accept} 格式文件</p>
    </Upload.Dragger>
  );
};

export default DraggerUpload;
