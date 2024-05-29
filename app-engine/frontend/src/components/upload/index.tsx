import { UploadOutlined } from '@ant-design/icons';
import { UploadFile } from 'antd';
import Upload from 'antd/es/upload/Upload';
import React, { useState } from 'react';
import './style.scoped.scss';

const LiveUpload: React.FC = ({customRequest}: any) => {
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const customRequestInner= async ({file}: any) => {
    try {
      await customRequest(file);
      setFileList([file])
    } catch (error) {
      
    }
  }
  return (
    <Upload 
      style={{ width: '100%'}} 
      customRequest={customRequestInner}
      fileList={fileList}
      multiple={false} 
      maxCount={1} 
      disabled={fileList.length ? true: false}
    >
      <div className='live-upload-trigger-container' style={{backgroundColor: fileList.length? '#ccc': ''}}>
        <span style={{
          display: 'block',
          height: '40px',
          paddingLeft: '8px',
          color: 'grey',
          lineHeight: '40px'
        }}>
          请选择文件
        </span>
        <span style={{
          position: 'absolute',
          top: 0,
          right: 0,
          padding: '7px 8px'
        }}><UploadOutlined /></span>
      </div>
    </Upload>
  )
};

export default LiveUpload;
