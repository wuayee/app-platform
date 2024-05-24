import { UploadOutlined } from '@ant-design/icons';
import Upload from 'antd/es/upload/Upload';
import React from 'react';
import './style.scoped.scss';

const LiveUpload: React.FC = () => {
  return (
    <Upload style={{ width: '100%' }}>
      <div className='live-upload-trigger-container'>
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
