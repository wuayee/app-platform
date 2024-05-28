import { Form, Input } from 'antd';
import React, { useEffect, useState } from 'react';
import type { DescriptionsProps, PaginationProps } from 'antd';
import { TableIcons } from '../../../../../components/icons/table';

interface props {
  data: string;

  dataKey: string;
  saveCallback: Function;
}

type FieldType = {
  data: string
};

const ModifyBaseInfo = ({ data, saveCallback, dataKey }: props) => {
  const [edit, setEdit] = useState(false);

  const [form] = Form.useForm<FieldType>();

  const hanlderSubmit = async () => {
    try {
      const res = await form.validateFields();
      saveCallback(dataKey, res);
      setEdit(false);
    } catch (error) {
      
    }
  }
  return (
    <>
      {edit ? 
      // 修改场景
      (
        <>
          <div style={{
            display: 'flex',
            gap: 4,
            alignItems: 'center'
          }}>
            <Form<FieldType> form={form} >

              <Form.Item  name='data' required rules={[{ required: true, message: '输入不能为空' }]}>
                <Input />
              </Form.Item>
            </Form>
            <span
              style={{
                cursor: 'pointer'
              }}
              onClick={
                hanlderSubmit
              }
            >
              <TableIcons.save/>
            </span>
            <span 
              style={{
                cursor: 'pointer'
              }}
              onClick={()=> {
                setEdit(false);
              }}>
              <TableIcons.cancle/>
            </span>
          </div>
        </>
      ) 
      : 

      // 详情场景
      (
        <>
          <div style={{
            display: 'flex',
            gap: 4
          }}>
            <span>{data}</span>
            <span 
              style={{
                cursor: 'pointer',
              }}
              onClick={()=> {
                setEdit(true);
                form.setFieldValue('data', data)
              }}
            >
              <TableIcons.edit/>
            </span>
          </div>
        </>
      ) 
      }
    </>
  )
}

export default ModifyBaseInfo;
