import { FormInstance, Input, Radio, Form } from 'antd';
import React, { ReactElement, useEffect, useState } from 'react';
import { KnowledgeIcons } from '../icons';

import './style.scoped.scss';
import CustomTable from './custom-table';
import LocalUpload from './local-upload';
import { getPlugins } from '../../shared/http/knowledge';

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

const DataSourceStyle: { [key: string]: React.CSSProperties } = {
  FormItem: {
    marginTop: 16,
    width: 800,
  },
};

const SelectDataSource = ({ type, form }: props) => {
  const initialValues: FieldType = {
    datasourceType: 'local',
  };

  // 监听类型变化
  const datasourceType = Form.useWatch('datasourceType', form);
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

  const checkTableRead = (_: any, value: any[]) => {

    if (value.length) {
      for (let index = 0; index < value.length; index++) {
        const item = value[index];

        const keys = Object.keys(item);

        for (let j = 0; j < keys.length; j++) {
          const key = keys[index];
          if(!item[key]) {
            return Promise.reject(new Error('值不能为空'));
          }
        }
      }
    }

    return Promise.resolve();
  };

  return (
    <>
      <div>
        <Form<FieldType> layout='vertical' form={form} initialValues={initialValues}>
          <Form.Item name='datasourceType' style={DataSourceStyle.FormItem}>
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
              style={DataSourceStyle.FormItem}
              required
              validateStatus={'error'}
              rules={[
                {
                  required: true,
                  message: '文件不能为空',
                  validator: (_, value) => {
                    return value && value.length
                      ? Promise.resolve()
                      : Promise.reject(new Error('无文件'));
                  },
                },
              ]}
            >
              <LocalUpload form={form} />
            </Form.Item>
          )}

          {datasourceType === 'nas' && (
            <>
              <Form.Item
                label='NAS地址'
                rules={[{ required: true, message: '输入不能为空' }]}
                name='nasUrl'
                style={DataSourceStyle.FormItem}
              >
                <Input placeholder='请输入NAS地址' />
              </Form.Item>

              <Form.Item
                label='文本路径'
                rules={[{ required: true, message: '输入不能为空' }]}
                name='nasFileUrl'
                style={DataSourceStyle.FormItem}
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
              style={DataSourceStyle.FormItem}
            >
              <Input placeholder='请输入要添加的内容' />
            </Form.Item>
          )}

          {datasourceType === 'custom' && type === 'table' && (
            <Form.Item
              label='自定义知识表'
              rules={[{ required: true, message: '输入不能为空' }, {validator: checkTableRead, message: '输入的值不能为空'}]}
              name='tableCustom'
              style={{
                marginTop: 16,
              }}
            >
              <CustomTable />
            </Form.Item>
          )}
        </Form>
      </div>
    </>
  );
};

export default SelectDataSource;
