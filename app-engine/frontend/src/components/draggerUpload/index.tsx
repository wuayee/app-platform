import React, { useState, useEffect } from 'react';
import { Upload } from 'antd';
import type { UploadProps } from 'antd';
import JSZip from 'jszip';
import { bytesToSize } from '@/common/util';
import { Message } from '@/shared/utils/message';
import { fileValidate } from '@/shared/utils/common';
import './index.scoped.scss';

const DraggerUpload = (props) => {
  const [fileList, setFileList] = useState([]);
  const customRequest = async (val) => {
    if (fileValidate(val.file, ['zip'], 100)) {
      val.onSuccess();
      let fileObj: any = {};
      let hasTool = false;
      const zip = new JSZip();
      const res = await zip.loadAsync(val?.file);
      fileObj[val.file.uid] = [];
      Object.keys(res.files).forEach(item => {
        if (!res.files[item].dir && item.indexOf('tools.json') !== -1) {
          hasTool = true;
          res.file(item)?.async('blob').then((data) => {
            let fileStr = new File([data], item, { type: 'application/json' });
            fileStr.text().then(res => {
              try {
                const toolJson = JSON.parse(res);
                const fileJson = validatePlugin(toolJson);
                fileObj[val.file.uid].push(fileJson);
                props.addFileData(fileObj, val.file);
              } catch {
                Message({ type: 'warning', content: `${val.file.name} tools.json文件解析错误` })
              }
            });
          });
        }
      });
      if (!hasTool) {
        Message({ type: 'warning', content: `${val.file.name}解析错误` })
      }
    }
  }
  // 插件上传前格式校验
  const validatePlugin = (toolJson) => {
    const reg = /^[\u4e00-\u9fa5a-zA-Z0-9]+$/;
    let toolsArr = toolJson.tools || [];
    toolsArr = toolsArr.filter(item => {
      return item.schema.name.length < 64 && item.schema.description.length < 256 && reg.test(item.schema.name);
    }).slice(0, 20);
    return {
      tools: toolsArr
    }
  }
  const onRemove = (id) => {
    props.removeFileData(id);
  }
  const beforeUpload = (file) => {
    let name = fileList.filter(item => item.name === file.name)[0];
    if (name) {
      Message({ type: 'warning', content: `${file.name} 该文件已上传` });
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
        <p className='ant-upload-hint'>支持 {props?.accept} 格式文件, 最大文件不能大于100M, 最多同时上传5个zip包</p>
        <p className='ant-upload-hint'>插件名称不允许非字母数字中文</p>
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
              <img src='/src/assets/images/ai/delete.png' onClick={() => onRemove(item.uid)} />
            </div>
          </div>
        ))}
      </div>
    </div>

  );
};

export default DraggerUpload;
