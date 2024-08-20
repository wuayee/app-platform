import React, { ReactElement, useEffect, useState } from 'react';
import { FormInstance, Input, Radio, Form } from 'antd';
import { KnowledgeIcons } from '../icons';
import CustomTable from './custom-table';
import LocalUpload from './local-upload';
import { useTranslation } from 'react-i18next';
import './style.scoped.scss';

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
  const { t } = useTranslation();
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
      label: `${t('localDocument')}`,
      value: 'local',
      icon: <KnowledgeIcons.local />,
    };
    const customOption = {
      label: `${t('consumer')}`,
      value: 'custom',
      disabled: true,
      icon: <KnowledgeIcons.custom />,
    };
    if (type === 'text') {
      const nasOption = {
        label: `${t('nasDocument')}`,
        value: 'nas',
        disabled: true,
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
          if (!item[key]) {
            return Promise.reject(new Error(`${t('cannotBeEmpty')}`));
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
                  disabled={option?.disabled ? true : false}
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
              label={t('uploadFileLabel')}
              name='selectedFile'
              style={DataSourceStyle.FormItem}
              required
              validateStatus={'error'}
              rules={[
                {
                  required: true,
                  message: `${t('fileCannotEmpty')}`,
                  validator: (_, value) => {
                    return value && value.length
                      ? Promise.resolve()
                      : Promise.reject(new Error(`${t('noFile')}`));
                  },
                },
              ]}
            >
              <LocalUpload form={form} type={type} />
            </Form.Item>
          )}

          {datasourceType === 'nas' && (
            <>
              <Form.Item
                label={t('nasAddress')}
                rules={[{ required: true, message: `${t('plsEnterRequiredItem')}` }]}
                name='nasUrl'
                style={DataSourceStyle.FormItem}
              >
                <Input placeholder={t('inputNasAddress')} />
              </Form.Item>

              <Form.Item
                label={t('textPath')}
                rules={[{ required: true, message: `${t('plsEnterRequiredItem')}` }]}
                name='nasFileUrl'
                style={DataSourceStyle.FormItem}
              >
                <Input placeholder={t('inputTextPath')} />
              </Form.Item>
            </>
          )}

          {datasourceType === 'custom' && type === 'text' && (
            <Form.Item
              label={t('addContent')}
              rules={[{ required: true, message: `${t('plsEnterRequiredItem')}` }]}
              name='textCustom'
              style={DataSourceStyle.FormItem}
            >
              <Input placeholder={t('inputAddContent')} />
            </Form.Item>
          )}

          {datasourceType === 'custom' && type === 'table' && (
            <Form.Item
              label={t('customKnowledgeTable')}
              rules={[{ required: true, message: `${t('plsEnterRequiredItem')}` }, { validator: checkTableRead, message: `${t('plsEnterRequiredItem')}` }]}
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
