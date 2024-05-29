
import { Button, Form, Input, Drawer, Space, Select, InputNumber } from 'antd';
import React, { useEffect, useState } from 'react';
import { getAlgorithmsList } from '../../../../../shared/http/apps';

const { TextArea } = Input;

interface props {
  visible: boolean;
  callback: any;
}

interface DataSetInterface {
  algorithms: string;
  scope: number;
}

const AppEvalute = ({ visible, callback }: props) => {

  const [form] = Form.useForm<DataSetInterface>();
  const [open, setOpen] = useState(false);
  const [algorithmsList, setAlgorithmsList] = useState<{
    value: string;
    label: string
  }[]>([]);

  useEffect(() => {
    setOpen(visible);
    getMethods()
  }, []);


  const closeDrawer = () => {
    callback('cancel', {});
  }

  const onFinish = (value: any) => {
    callback('submit', value);
  }

  const submit = () => {
    form.submit();
  }

  // 获取评估算法
  const getMethods = async () => {
    try {
      const res = await getAlgorithmsList();
      const data = (res||[]).map((al: any) => ({
        label: al.name,
        value: al.id
      }));
      setAlgorithmsList([...data]);
    } catch (error) {
      
    }
  }

  return (
    <Drawer
      title="应用评估"
      width={1600}
      open={open}
      maskClosable={false}
      onClose={closeDrawer}
      destroyOnClose={true}
      footer={
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Space>
            <Button style={{ minWidth: 96 }} onClick={closeDrawer}>取消</Button>
            <Button type='primary' style={{ minWidth: 96 }} onClick={submit}>确定</Button>
          </Space>
        </div>
      }
    >
      <Form<DataSetInterface>
        form={form}
        onFinish={onFinish}
        layout='vertical'
      >
        <div style={{
          display: 'flex',
          justifyContent: 'space-between'
        }}>
            <Form.Item label='评估算法' required name='algorithms' rules={[{ required: true, message: '输入不能为空' }]} style={{
              width: '45%'
            }}>
              <Select
                options={algorithmsList}
              />
            </Form.Item>
            <Form.Item label='及格分' required name='scope' rules={[{ required: true, message: '输入不能为空' }]}
              style={{
                width: '45%'
              }}
            >
              <InputNumber min={0} style={{
                width: '100%'
              }}/>
            </Form.Item>
        </div>

      </Form>
    </Drawer>

  )
}

export default AppEvalute;
