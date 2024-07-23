import React, { useState, useEffect } from 'react';
import { Upload } from 'antd';
import type { UploadProps } from 'antd';
import JSZip from 'jszip';
import { bytesToSize } from '@/common/util';
import { Message } from '@/shared/utils/message';
import './index.scoped.scss';

const DraggerUpload = (props) => {
  const [fileList, setFileList] = useState([]);
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
  const onRemove = (id) => {
    props.removeFileData(id);
  }
  const beforeUpload = (file) => {
    let name = fileList.filter(item => item.name === file.name)[0];
    if (name) {
      Message({ type: 'warning', content: `${file.name} 该文件已上传` })
      return false
    }
    return true
  }
  const uploadProps: UploadProps = {
    name: 'file',
    customRequest,
    beforeUpload,
    showUploadList: false,
    ...props
  };
  useEffect(() => {
    setFileList(props.fileList || []);
  }, [props.fileList])
  return (
    <div>
      <Upload.Dragger {...uploadProps}>
        <p className='ant-upload-drag-icon'>
          <img width={32} height={32} src='/src/assets/images/ai/upload.png' />
        </p>
        <p className='ant-upload-text'>将文件拖到这里，或者点击上传</p>
        <p className='ant-upload-hint'>支持 {props?.accept} 格式文件</p>
      </Upload.Dragger>
      <div className='file-upload-list'>
        {fileList?.map((item) => (
          <div className='file-item' key={item.uid}>
            <div className='file-item-left'>
              <span className='file-name'>{item.name}</span>
              <span>({bytesToSize(item.size)})</span>
            </div>
            <div className='file-item-right'>
              <img src='/src/assets/images/ai/complate.png' />
              <img src='/src/assets/images/ai/delete.png' onClick={() => onRemove(item.uid)}/>
            </div>
          </div>
        ))}
      </div>
    </div>
    
  );
};

export default DraggerUpload;
