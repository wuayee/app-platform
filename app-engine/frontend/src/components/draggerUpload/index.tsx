import React from 'react';
import { InboxOutlined } from '@ant-design/icons';
import { Upload } from 'antd';
import type { UploadProps } from 'antd';
import JSZip from 'jszip';

const DraggerUpload = (props) => {
  const customRequest= async (val)=>{
    val.onSuccess();
    let fileObj:any = {};
    const zip = new JSZip();
    const res = await zip.loadAsync(val?.file);
    fileObj[val.file.uid] = [];
    Object.keys(res.files).forEach(item => {
      if (!res.files[item].dir && item.indexOf('tools.json') !== -1) {
        res.file(item)?.async('blob').then((data) => {
          let fileStr = new File([data], item, { type: 'application/json' });
          fileStr.text().then(res => {
            const json = JSON.parse(res);
            fileObj[val.file.uid].push(json);
            props.addFileData(fileObj, val.file);
          });
        });
      }
    });
   
  }
  const onRemove = (file) => {
    props.removeFileData(file);
  }
  const uploadProps: UploadProps = {
    name: 'file',
    customRequest:customRequest,
    listType: 'picture',
    onRemove,
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
