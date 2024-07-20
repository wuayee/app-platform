import React from 'react';
import { Upload } from 'antd';
import { InboxOutlined } from '@ant-design/icons';
import { getPluginPackageInfo } from '@/shared/http/plugin';

const DraggerUpload = (props) => {
  const { setResult } = props;
  const customRequest=(val)=>{
    getPluginPackageInfo(val?.file).then((res)=>{
    val.onSuccess(res, val?.file);
    setResult(res);
    }).catch((e)=>{
      val.onError(e);
    });
  }
  const uploadProps: UploadProps = {
    name: 'file',
    customRequest:customRequest,
    ...props
  };
  return (
    <Upload.Dragger {...uploadProps}>
      <p className='ant-upload-drag-icon'>
        <InboxOutlined />
      </p>
      <p className='ant-upload-text'>将文件拖到这里，或者点击上传</p>
      <p className='ant-upload-hint'>支持 {props?.accept} 格式文件</p>
    </Upload.Dragger>
  );
};

export default DraggerUpload;
