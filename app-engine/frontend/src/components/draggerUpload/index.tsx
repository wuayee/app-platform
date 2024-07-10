import React, { useEffect, useState, useRef } from 'react';
import { InboxOutlined } from '@ant-design/icons';
import { Upload } from 'antd';
import JSZip from 'jszip';
import { getPluginPackageInfo } from '../../shared/http/plugin';
const zip = new JSZip();

const DraggerUpload = (props) => {
  const { accept,setResult } = props;
  const jsonMap = useRef();
  const customRequest=(val)=>{
    let fileObj:any = {};
    zip.loadAsync(val?.file).then((res) => {
      fileObj[val.file.uid] = [];
      Object.keys(res.files).forEach(item => {
        if (!res.files[item].dir && item.indexOf('json') !== -1) {
          res.file(item)?.async('blob').then((data) => {
            let fileStr = new File([data], item, { type: 'application/json' });
            fileStr.text().then(res => {
              const json = JSON.parse(res);
              fileObj[val.file.uid].push(json);
            });
          });
        }
      })
      console.log(fileObj);
    })
    // getPluginPackageInfo(val?.file).then((res)=>{
    //   val.onSuccess(res, val?.file);
    //     setResult(res);
    //   }).catch((e)=>{
    //     val.onError(e);
    //   });
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
