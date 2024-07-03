
import { Button, Form, Input, Space, Drawer, message } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { createExternalModel } from '../../../shared/http/model';
import { OutsideServerTypeE } from '../model';

interface props {
  createSignal: boolean;
  createCallback: Function;
  record: any;
  type: OutsideServerTypeE;
}

const CreateModel = ({ createSignal, createCallback, record, type }: props) => {
  const [form] = Form.useForm();
  const [open,setOpen]=useState(false);
  const [messageApi, contextHolder] = message.useMessage();
  const cancel = () => {
    setOpen(false);
    createCallback('cancel');
  }

  useEffect(()=>{
    if(createSignal>0){
      if(type===OutsideServerTypeE.CREATE){
        form.resetFields();
      }else{
      form.setFieldsValue(record);}
      setOpen(true);
    }
  },[createSignal])

  const onFinish = (value: any) => {
    createExternalModel(value).then(_ => {
      messageApi.open({
        type: 'success',
        content: '创建成功',
      });
      setOpen(false);
      createCallback('submit');
    })
  }

  // 点击确认按钮
  const clickSubmit = async () => {
    form.submit();
  }

  return (
    <>
      {contextHolder}
      <Drawer
        title={type===OutsideServerTypeE.CREATE?'新建':'编辑'}
        width={500}
        open={open}
        onClose={cancel}
        maskClosable={false}
        footer={
          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Space>
              <Button style={{ minWidth: 96 }} onClick={cancel}>取消</Button>
              <Button type='primary' style={{ minWidth: 96 }} onClick={clickSubmit}>确定</Button>
            </Space>
          </div>
        }
      >
        <Form form={form} layout='vertical' onFinish={onFinish} >
          <Form.Item
            label='名称'
            name='name'
            required
            validateFirst
            rules={[
              { required: true, message: '输入不能为空' },
              { type: 'string', max: 100, message: '长度范围1 - 100' },
              {
                validator: (_, value) => {
                  if (/^[a-zA-Z0-9](([a-zA-Z0-9-_])*[a-zA-Z0-9])*$/.test(value)) {
                    return Promise.resolve();
                  } else {
                    return Promise.reject('名称以字母和数字开头或结尾，仅可包含字母、数字、“_”和“-”');
                  }
                }
              }
            ]}
          >
            <Input disabled={type===OutsideServerTypeE.EDIT}/>
          </Form.Item>
          <Form.Item
            label='URL'
            name='url'
            required
            validateFirst
            rules={[
              { required: true, message: '输入不能为空' },
              {
                validator: (_, value) => {
                  if (/http[s]{0,1}:\/\/([\w.]+\/?)\S*/.test(value)) {
                    return Promise.resolve();
                  } else {
                    return Promise.reject('请输入正确的url地址');
                  }
                }
              }
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label='API Key'
            name='api_key'
            validateFirst
            rules=
            {[
              {
                validator: (_, value) => {
                  if (/^\S*$/.test(value)) {
                    return Promise.resolve();
                  } else {
                    return Promise.reject('请输入正确的API Key(不能包含空格)');
                  }
                }
              }
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label='HTTP代理'
            name='http_proxy'
            rules={[
              {
                validator: (_, value) => {
                  if (!value || /^https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)$/.test(value)) {
                    return Promise.resolve();
                  } else {
                    return Promise.reject('请输入正确的地址');
                  }
                }
              }
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label='HTTPS代理'
            name='https_proxy'
            rules={[
              {
                validator: (_, value) => {
                  if (!value || /^https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)$/.test(value)) {
                    return Promise.resolve();
                  } else {
                    return Promise.reject('请输入正确的地址');
                  }
                }
              }
            ]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Drawer>
    </>

  )
}

export default CreateModel;
