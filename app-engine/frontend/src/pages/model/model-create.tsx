import React, { useState, useEffect, useRef } from 'react';
import { Select, Button, Drawer, Input, Form, message } from 'antd';
import { SearchOutlined, EllipsisOutlined, CloseOutlined } from '@ant-design/icons';

import { createModel, getModelList } from '../../shared/http/model';

interface createItem {
  name: string;
  image: Array<string>;
  precision: Array<string>;
  gpu: Array<number>;
}

interface StarAppsProps {
  open: boolean;
  setOpen: (val: boolean) => void;
  createItems: Array<createItem>;
  setModels: (val: Array<any>) => void;
}

const ModelCreate: React.FC<StarAppsProps> = ({ open, setOpen, createItems, setModels }) => {
  const [form] = Form.useForm();

  const nameOptions: any[] = [];
  // 下拉框联动
  const [nameOption, setNameOption] = useState(null);
  const [precisionOption, setPrecisionOption] = useState(null);
  const [imageOption, setImageOption] = useState(null);
  const [gpuOption, setGpuOption] = useState(null);
  const handleNameChange = (value: any) => {
    setNameOption(value);
    setPrecisionOption(null);
    setImageOption(null);
    setGpuOption(null);
  };
  const filteredPrecisionOption = createItems.find((item) => item.name === nameOption)?.precision;
  const filteredImageOption = createItems.find((item) => item.name === nameOption)?.image;
  const filteredGpuOption = createItems.find((item) => item.name === nameOption)?.gpu;

  if (createItems.length) {
    createItems.forEach((item) => {
      nameOptions.push({ value: item.name, label: item.name });
    });
  }

  const deployModel = () => {
    form.validateFields().then((values) => {
      const { name, inference_accuracy, image_name, des, npus, replicas } = values;
      const modelParams = {
        name,
        des,
        image_name,
        inference_accuracy,
        replicas,
        node_port: 80,
        npus: parseInt(npus),
      };
      createModel(modelParams).then((res) => {
        if (res && res.code === 200) {
          message.success('模型部署成功');
          getModelList().then((res) => {
            if (res) {
              setModels(res.llms);
            }
          });
          form.resetFields();
        } else {
          message.error('模型部署失败');
        }
      });
      setOpen(false);
    });
  };

  return (
    <Drawer
      destroyOnClose
      mask={false}
      title={
        <div
          className='app-title'
          style={{
            display: 'flex',
            gap: 200,
          }}
        >
          <div className='app-title-left'>
            <span>创建大模型服务</span>
          </div>
          <CloseOutlined style={{ fontSize: 12 }} onClick={() => setOpen(false)} />
        </div>
      }
      closeIcon={false}
      onClose={() => setOpen(false)}
      open={open}
    >
      <Form form={form} layout='vertical' autoComplete='off' className='edit-form-content'>
        <Form.Item
          label='大模型服务名称'
          name='name'
          rules={[{ required: true, message: '请输入大模型服务名称' }]}
        >
          <Select options={nameOptions} value={nameOption} onChange={handleNameChange} />
        </Form.Item>
        <Form.Item label='描述' name='des' rules={[{ required: true, message: '请输入描述' }]}>
          <Input placeholder='这里是描述信息~' />
        </Form.Item>
        <Form.Item
          label='大模型镜像名称'
          name='image_name'
          rules={[{ required: true, message: '请输入大模型镜像名称' }]}
        >
          <Select
            options={filteredImageOption?.map((option) => ({ label: option, value: option }))}
            value={imageOption}
            onChange={setImageOption}
          />
        </Form.Item>
        <Form.Item
          label='推理精度'
          name='inference_accuracy'
          rules={[{ required: true, message: '请输入推理精度' }]}
        >
          <Select
            options={filteredPrecisionOption?.map((option) => ({ label: option, value: option }))}
            value={precisionOption}
            onChange={setPrecisionOption}
          />
        </Form.Item>
        <Form.Item
          label='大模型实例数'
          name='replicas'
          rules={[
            { required: true, message: '请输入大模型实例数' }
          ]}
        >
           <Select
            options={filteredGpuOption?.map((option) => ({ label: option, value: option }))}
            value={gpuOption}
            onChange={setGpuOption}
          />
        </Form.Item>
        <Form.Item
          label='单实例消耗的NPU数'
          name='npus'
          rules={[
            { required: true, message: '请输入单实例消耗的NPU数' },
            {
              whitespace: true,
              type: 'number',
              transform(value) {
                if (value) {
                  return Number(value);
                }
              },
              message: '模型服务端口号仅支持数字',
            },
            { pattern: /^[^\s]*$/, message: '禁止输入空格' },
          ]}
        >
          <Input />
        </Form.Item>
      </Form>
      <div
        style={{
          display: 'flex',
          flexDirection: 'row-reverse',
          position: 'fixed',
          bottom: 10,
          right: 10,
        }}
      >
        <Button type='primary' onClick={() => deployModel()}>
          部署模型
        </Button>
        <Button onClick={() => setOpen(false)}>取消</Button>
      </div>
    </Drawer>
  );
};

export default ModelCreate;
