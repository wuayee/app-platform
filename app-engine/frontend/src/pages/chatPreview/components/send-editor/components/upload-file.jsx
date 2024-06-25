
import React, { useImperativeHandle, useState, useContext, useEffect } from 'react';
import { Modal, Upload  } from 'antd';
import { uploadChatFile } from "@shared/http/aipp";
import { Message } from '@shared/utils/message';
import { fileTypeSet } from '../../../utils/chat-process';
import { docArr } from '../common/config';
import exportImg from '@assets/images/ai/export.png'
import { useAppSelector } from '../../../../../store/hook';

const { Dragger } = Upload;

const UploadFile = ({ openUploadRef, fileSend }) => {
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const [ modalOpen, setModalOpen] = useState(false);
  const showModal = () => {
    setModalOpen(true);
  };
  useImperativeHandle(openUploadRef, () => {
    return {
      'showModal': showModal
    }
  });
  
  const beforeUpload = (file) => {
    return false;
  };
  // 文件上传
  const onChange = async ({ file }) => {
    let suffix = '';
    try {
      const fileArr = file.name.split('.');
      suffix = fileArr[fileArr.length - 1];
    } catch {
      suffix = '';
    }
    if (!suffix) {
      Message({ type: 'warning', content: '文件格式错误' });
      return
    }
    let fileType = fileTypeSet(suffix);
    let headers = {
      "attachment-filename": encodeURI(file.name || ""),
    };
    const formData = new FormData();
    formData.append("file", file);
    const result = await uploadChatFile(tenantId, appId, formData, headers);
    if (result.code === 0) {
      fileSend(result.data, fileType);
      setModalOpen(false);
    } else {
      Message({ type: "error", content: result.msg || "上传文件失败" });
    }
  };
  return <>{(
      <Modal 
        title="上传文件" 
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        footer={null} 
        centered>
        <div style={{ margin: '12px 0' }}>解析文件或通过文件与应用对话</div>
        <div className="dragger-modal">
        <Dragger
          beforeUpload={beforeUpload}
          onChange={onChange}
          fileList={[]}
          maxCount={1}
        >
          <p className="ant-upload-drag-icon">
            <img src={exportImg} alt="" />
          </p>
          <p className="ant-upload-text">将文件拖到此处 或 点击上传文件</p>
          <p className="ant-upload-hint">
            文件最大不超过500MB. 持文件类型 .jpg, .png, .pdf, .mp4, .mov…
          </p>
        </Dragger>
        </div>
      </Modal>
  )}</>
};


export default UploadFile;
