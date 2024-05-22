import React, { useRef, useState } from 'react';
import { InboxOutlined } from '@ant-design/icons';
import { Upload } from 'antd';
import { UploadFile } from 'antd/lib';

const { Dragger } = Upload;

const UploadFile = () => {
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const filesKeys = useRef<Map<string, any>>(new Map());

  const handleFileChange = () => {};

  const setFiles = (): void => {
    const files = [...filesKeys.current.values()];
    setFileList(files);
  };

  const isFilesUnique = (file: UploadFile): boolean => {
    if (filesKeys.current.has(makeFileKey(file))) {
      return false;
    }

    return true;
  };

  const handleBeforeUpload = (file: UploadFile): boolean => {
    if (isFilesUnique(file)) {
      filesKeys.current.set(makeFileKey(file), file);
      setFiles();
      return true;
    }
    return false;
  };

  const handleUpload = () => {};

  function makeFileKey(file: UploadFile): string {
    return `${file.name}:(${file.size})`;
  }

  const handleRemoveFile = (file: UploadFile): void => {
    const key = makeFileKey(file);
    if (!filesKeys.current.has(key)) {
      return;
    }
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

export default UploadFile;
