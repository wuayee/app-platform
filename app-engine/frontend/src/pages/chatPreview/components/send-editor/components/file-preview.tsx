
import React, { useState } from 'react';
import { ClearFileIcon } from '@assets/icon';
import UploadFile from './upload-file';
import '../styles/file-preview.scss';

const LinkFile = ({openUploadRef}) => {
  const [showPreview, setShowPreview] = useState(false);
  const [file, setFile] = useState({data:null, type:null});
  // 取消文件
  const cancleFile = () => {
    setShowPreview(false);
  }
  return (
    <>
      {/* 预览文件内容 */}
      {showPreview&&
      <div className='file-preview'>
        <div className='preview-inner'>
          <div>文件内容</div>
          <span className='delete-icon'>
            <ClearFileIcon onClick={() => cancleFile()}/>
          </span>
        </div>
      </div>
      }
      {/* 上传文件弹窗 */}
      <UploadFile 
        openUploadRef={openUploadRef} 
        fileSend={(data, type)=>{setFile({data, type})}}
      />
    </>
  )
};


export default LinkFile;
