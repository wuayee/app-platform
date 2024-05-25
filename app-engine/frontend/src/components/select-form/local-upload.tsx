import React, { useEffect, useRef, useState } from 'react';
import { InboxOutlined } from '@ant-design/icons';
import { Form, Upload } from 'antd';
import { UploadFile } from 'antd/lib';
import useSearchParams from '../../shared/hooks/useSearchParams';
import { deleteLocalFile, uploadLocalFile } from '../../shared/http/knowledge';

const { Dragger } = Upload;

const LocalUpload: React.FC<{ form: any }> = ({ form }) => {
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const filesKeys = useRef<Map<string, any>>(new Map());
  const { id, tableid } = useSearchParams();
  const selectedFile = Form.useWatch('selectedFile', form);

  useEffect(() => {
    setFileList(selectedFile);
  }, [selectedFile]);

  const handleFileChange = () => {};

  const setFiles = (): void => {
    const files = [...filesKeys.current.values()];
    setFileList(files);
    form.setFieldValue('selectedFile', files);
    form.validateFields(['selectedFile']);
  };

  const isFilesUnique = (file: UploadFile): boolean => !filesKeys.current.has(makeFileKey(file));

  const handleBeforeUpload = (file: UploadFile): boolean => {
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
    if (!filesKeys.current.has(key)) {
      return;
    }
    await deleteLocalFile(id, tableid, [`${file.uid}_${file.name}`]);
    filesKeys.current.delete(key);
    setFiles();
  };

  return (
    <Dragger
      multiple
      name='file'
      fileList={fileList}
      onChange={handleFileChange}
      beforeUpload={handleBeforeUpload}
      customRequest={handleUpload}
      onRemove={handleRemoveFile}
    >
      <p className='ant-upload-drag-icon'>
        <InboxOutlined />
      </p>
      <p className='ant-upload-text'>拖拽文件至此或者点击选择文件</p>
    </Dragger>
  );
};

export default LocalUpload;
