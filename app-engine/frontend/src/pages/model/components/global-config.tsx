
import { Button, Form, Input, Radio, Space, Drawer, message } from 'antd';
import React, { useEffect, useState } from 'react';
import { updateExternalProxy, getExternalProxy } from '../../../shared/http/model';

interface props {
  visible: boolean;
  configCallback: Function;
}

const GlobalConfig = ({ visible, configCallback }: props) => {

  const [form] = Form.useForm();

  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    getProxy();
  });

  const getProxy = () => {
    getExternalProxy().then(res => {
      form.setFieldsValue({
        http_proxy: res?.global_proxies?.http_proxy,
        https_proxy: res?.global_proxies?.https_proxy,
        no_proxy: res?.global_proxies?.no_proxy,
      })
    });
  }

  const cancel = () => {
    configCallback('cancel');
  }

  const onFinish = (value: any) => {
    updateExternalProxy(value).then(_ => {
      messageApi.open({
        type: 'success',
        content: '更新成功',
      });
      configCallback('submit');
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
        title={'新建'}
        width={500}
        open={visible}
        onClose={cancel}
        maskClosable={false}
        destroyOnClose={true}
        footer={
          <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Space>
              <Button style={{ minWidth: 96 }} onClick={cancel}>取消</Button>
              <Button type='primary' style={{ minWidth: 96 }} onClick={clickSubmit}>确定</Button>
            </Space>
          </div>
        }
      >
        <Form form={form} layout='vertical' onFinish={onFinish}>
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
          <Form.Item
            label='No Proxy'
            name='no_proxy'
            rules={[
              {
                validator: (_, value) => {
                  if (!value || /^(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])$/.test(value)) {
                    return Promise.resolve();
                  } else {
                    return Promise.reject('请输入正确的IP');
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

export default GlobalConfig;
