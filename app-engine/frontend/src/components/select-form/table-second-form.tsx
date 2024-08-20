import React from 'react';
import { FormInstance, Form } from 'antd';
import CustomTable from './table-config';
import { useTranslation } from 'react-i18next';
interface props {
  form: FormInstance,
}

// 选择数据源表单配置
type FieldType = {
  tableCustom: any[];
};


const TableSecondForm = ({ form }: props) => {
  const { t } = useTranslation();
  const initialValues: FieldType = {
    tableCustom: [],
  };
  // 监听类型变化
  const checkTableRead = (_: any, value: any[]) => {
    const { t } = useTranslation();
    if (value.length) {
      for (let index = 0; index < value.length; index++) {
        const item = value[index];
        const keys = Object.keys(item);
        for (let j = 0; j < keys.length; j++) {
          const key = keys[index];
          if (!item[key] && key !== 'description' && key !== 'indexType') {
            return Promise.reject(new Error(t('cannotBeEmpty')));
          }
        }
      }
    }
    return Promise.resolve();
  };

  return (
    <>
      <div>
        <Form<FieldType>
          layout={'vertical'}
          form={form}
          initialValues={initialValues}
        >
          <Form.Item
            label={t('tableStructure')}
            rules={[
              { required: true, message: t('plsEnterRequiredItem') },
              { validator: checkTableRead, message: t('plsEnterRequiredItem') }
            ]}
            name='tableCustom'
            style={{
              marginTop: 16,
            }}
          >
            <CustomTable />
          </Form.Item>
        </Form>
      </div>
    </>
  );
};

export default TableSecondForm;
