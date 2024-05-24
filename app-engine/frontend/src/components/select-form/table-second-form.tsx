import { FormInstance, Input, Radio, Form, Upload, Table, TableColumnsType } from 'antd';
import React, { ReactElement, useEffect, useRef, useState } from 'react';
import CustomTable from './table-config';

interface props {
  form: FormInstance,
}

// 选择数据源表单配置
type FieldType = {
  tableCustom: any[];
};


const TableSecondForm = ({ form }: props) => {
  const initialValues: FieldType = {
    tableCustom: [],
  };

  // 监听类型变化

  const checkTableRead = (_: any, value: any[]) => {

    if (value.length) {
      for (let index = 0; index < value.length; index++) {
        const item = value[index];

        const keys = Object.keys(item);

        for (let j = 0; j < keys.length; j++) {
          const key = keys[index];
          if(!item[key] &&key !=='description' && key !=='indexType' && key !== 'vectorService') {
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
        <Form<FieldType>
          layout={'vertical'}
          form={form}
          initialValues={initialValues}
        >
        <Form.Item
          label='表结构'
          rules={[{ required: true, message: '输入不能为空' }, {validator: checkTableRead, message: '输入的值不能为空'}]}
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
