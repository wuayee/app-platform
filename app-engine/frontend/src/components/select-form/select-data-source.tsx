import { FormInstance, Input, Radio, Form, Upload, Table, TableColumnsType } from 'antd';
import React, { ReactElement, useEffect, useRef, useState } from 'react';
import { KnowledgeIcons } from '../icons';
import { InboxOutlined } from '@ant-design/icons';
import { UploadFile } from 'antd/lib';
import './style.scoped.scss';
import CustomTable from './custom-table';

interface props {
  type: 'text' | 'table';
  form: FormInstance;

  onFinish?: (value: FieldType) => any;
}

// 选择数据源表单配置
type FieldType = {
  // 选择文档类型
  datasourceType: 'local' | 'nas' | 'custom';

  // 上传文本文件 local类型
  selectedFile?: FileList;

  // NAS 类型
  nasUrl?: string;

  // nas文件路径
  nasFileUrl?: string;

  // 文本自定义内容 custom
  textCustom?: string;

  // 表格自定义内容 custom
  tableCustom?: any[];
};

const { Dragger } = Upload;

const SelectDataSource = ({ type, form }: props) => {
  const initialValues: FieldType = {
    datasourceType: 'local',
  };

  // 监听类型变化
  const datasourceType = Form.useWatch('datasourceType', form);

  const submit = () => {
    form.submit();
  };
  const onFinish = async (value: FieldType) => {
    // loading状态点击不触发，禁止多次触发提交
  };

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

  interface DataSourceOption {
    label: string;
    value: string;
    icon: ReactElement;
  }
  const [dataSourceOptions, setDataSourceOptions] = useState<DataSourceOption[]>([]);

  useEffect(() => {
    const localOption = {
      label: '本地文档',
      value: 'local',
      icon: <KnowledgeIcons.local />,
    };
    const customOption = {
      label: '自定义',
      value: 'custom',
      icon: <KnowledgeIcons.custom />,
    };
    if (type === 'text') {
      const nasOption = {
        label: 'NAS文档',
        value: 'nas',
        icon: <KnowledgeIcons.nas />,
      };
      setDataSourceOptions([localOption, nasOption, customOption]);
    } else {
      setDataSourceOptions([localOption, customOption]);
    }
  }, [type]);

  return (
    <>
      <div>
        <Form<FieldType>
          layout={'vertical'}
          form={form}
          initialValues={initialValues}
          onFinish={onFinish}
        >
          <Form.Item
            name='datasourceType'
            style={{
              marginTop: 16,
              width: 800,
            }}
          >
            <Radio.Group className='radio-card-group'>
              {dataSourceOptions.map((option) => (
                <Radio.Button
                  value={option.value}
                  style={{ borderColor: datasourceType === option.value ? '#1677ff' : '' }}
                >
                  <div className='radio-card-item'>
                    {option.icon}
                    <span>{option.label}</span>
                  </div>
                </Radio.Button>
              ))}
            </Radio.Group>
          </Form.Item>

          {datasourceType === 'local' && (
            <Form.Item
              label='上传文本文件'
              name='selectedFile'
              style={{
                marginTop: 16,
                width: 800,
              }}
            >
              {/* 需要对齐 */}
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
            </Form.Item>
          )}

          {datasourceType === 'nas' && (
            <>
              <Form.Item
                label='NAS地址'
                rules={[{ required: true, message: '输入不能为空' }]}
                name='nasUrl'
                style={{
                  marginTop: 16,
                }}
              >
                <Input placeholder='请输入NAS地址' />
              </Form.Item>

              <Form.Item
                label='文本路径'
                rules={[{ required: true, message: '输入不能为空' }]}
                name='nasFileUrl'
                style={{
                  marginTop: 16,
                }}
              >
                <Input placeholder='请输入文本路径' />
              </Form.Item>
            </>
          )}

          {datasourceType === 'custom' && type === 'text' && (
            <Form.Item
              label='添加内容'
              rules={[{ required: true, message: '输入不能为空' }]}
              name='textCustom'
              style={{
                marginTop: 16,
              }}
            >
              <Input placeholder='请输入要添加的内容' />
            </Form.Item>
          )}

          {datasourceType === 'custom' && type === 'table' && <CustomTable />}
        </Form>
      </div>
    </>
  );
};

export { SelectDataSource };
