import React, { useImperativeHandle, useState } from 'react';
import { Modal, Upload } from 'antd';
import { uploadChatFile } from '@/shared/http/aipp';
import { Message } from '@/shared/utils/message';
import { fileTypeSet } from '../../../utils/chat-process';
import exportImg from '@/assets/images/ai/export.png'
import { useAppSelector } from '@/store/hook';
import { useTranslation } from 'react-i18next';

const { Dragger } = Upload;
const UploadFile = ({ openUploadRef, fileSend }) => {
  const { t } = useTranslation();
  const [modalOpen, setModalOpen] = useState(false);
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
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
      Message({ type: 'warning', content: t('fileFormatError') });
      return
    }
    let fileType = fileTypeSet(suffix);
    if (['video', 'extras'].includes(fileType)) {
      Message({ type: 'warning', content: t('noSupportFileType') });
      return
    }
    let headers = {
      'attachment-filename': encodeURI(file.name || ''),
    };
    const formData = new FormData();
    formData.append('file', file);
    const result = await uploadChatFile(tenantId, appId, formData, headers);
    if (result.code === 0) {
      fileSend(result.data, fileType);
      setModalOpen(false);
    } else {
      Message({ type: 'error', content: result.msg || t('uploadFileFail') });
    }
  };
  return <>{(
    <Modal
      title={t('uploadFile')}
      open={modalOpen}
      onCancel={() => setModalOpen(false)}
      footer={null}
      centered>
      <div style={{ margin: '12px 0' }}>{t('uploadFileContent')}</div>
      <div className='dragger-modal'>
        <Dragger
          beforeUpload={beforeUpload}
          onChange={onChange}
          fileList={[]}
          maxCount={1}
        >
          <p className='ant-upload-drag-icon'>
            <img src={exportImg} alt='' />
          </p>
          <p className='ant-upload-text'>{t('dragFile')}</p>
          <p className='ant-upload-hint'>
            {t('fileType')}
          </p>
        </Dragger>
      </div>
    </Modal>
  )}</>
};


export default UploadFile;
