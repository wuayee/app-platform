import React, { useEffect, useRef, useState } from 'react';
import { InboxOutlined } from '@ant-design/icons';
import { Form, Upload } from 'antd';
import { UploadFile } from 'antd/lib';
import { Message } from '@shared/utils/message';
import useSearchParams from '@shared/hooks/useSearchParams';
import { deleteLocalFile, uploadLocalFile } from '../../shared/http/knowledge';
import { useTranslation } from 'react-i18next';

const { Dragger } = Upload;

const LocalUpload: React.FC<{ form: any, respId?: any, tableId?: any, type: string }> = ({ form, respId, tableId, type }) => {
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const filesKeys = useRef<Map<string, any>>(new Map());
  let { id, tableid } = useSearchParams();
  const selectedFile = Form.useWatch('selectedFile', form);
  const { t } = useTranslation();
  id = id || respId;
  tableid = tableid || tableId;
  useEffect(() => {
    setFileList(selectedFile);
  }, [selectedFile]);


  const handleFileChange = () => { };

  const setFiles = (): void => {
    const files = [...filesKeys.current.values()];
    setFileList(files);
    form.setFieldValue('selectedFile', files);
    form.validateFields(['selectedFile']);
  };

  const isFilesUnique = (file: UploadFile): boolean => !filesKeys.current.has(makeFileKey(file));

  const handleBeforeUpload = (file: UploadFile): boolean => {
    if (type === 'text' && file.type !== 'text/plain') {
      Message({ type: 'warning', content: t('fileFormatError1') });
      setFileList(fileList || []);
      return false
    }
    if (type === 'table' && file.type !== 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') {
      Message({ type: 'warning', content: t('fileFormatError2') });
      setFileList(fileList || []);
      return false
    }
    if (filesKeys.current.size != 0) {
      Message({ type: 'warning', content: t('fileFormatError3') });
      return false
    }
    if (isFilesUnique(file)) {
      filesKeys.current.set(makeFileKey(file), file);
      setFiles();
      return true;
    }
    return false;
  };

  const handleUpload = async ({ file }: any) => {
    await uploadLocalFile(id, tableid, file, `${file.uid}_${file.name}`);
  };

  function makeFileKey(file: UploadFile): string {
    return `${file.name}:(${file.size})`;
  }

  const handleRemoveFile = async (file: UploadFile) => {
    const key = makeFileKey(file);
    await deleteLocalFile(id, tableid, [`${file.uid}_${file.name}`]);
    filesKeys.current.delete(key);
    setFiles();
  };

  return (
    <Dragger
      multiple
      name='file'
      accept={type === 'text' ? '.txt' : '.xlsx'}
      fileList={fileList}
      onChange={handleFileChange}
      listType='picture'
      maxCount={1}
      beforeUpload={handleBeforeUpload}
      customRequest={handleUpload}
      onRemove={handleRemoveFile}
    >
      <p className='ant-upload-drag-icon'>
        <InboxOutlined />
      </p>
      <p className='ant-upload-text'>{t('fileUploadContent1')}</p>
    </Dragger>
  );
};

export default LocalUpload;
