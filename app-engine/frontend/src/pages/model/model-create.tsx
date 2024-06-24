import React, { useState, useEffect, useRef } from 'react';
import { Select, Button, Drawer, Input, Form, message, InputNumber } from 'antd';
import { CloseOutlined } from '@ant-design/icons';

import { createModel, getModelList } from '../../shared/http/model';
import { ModelItem } from './cards-tab';

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
  modifyData: ModelItem;
}

const ModelCreate: React.FC<StarAppsProps> = ({ open, setOpen, createItems, setModels, modifyData }) => {
  const [form] = Form.useForm();
  const nameOptions: any[] = [];
  // 下拉框联动
  const [nameOption, setNameOption] = useState(null);
  const [precisionOption, setPrecisionOption] = useState(null);
  const [imageOption, setImageOption] = useState(null);
  const [gpuOption, setGpuOption] = useState(null);
  const [linkNumMax, setLinkNumMax] = useState(300);

  useEffect(() => {
    form.resetFields();
    form.setFieldValue('max_link_num', 300);
    if (modifyData) {
      handleModifyData();
    }
  }, [modifyData, open]);

  const handleNameChange = (value: any) => {
    setNameOption(value);
    setPrecisionOption(null);
    setImageOption(null);
    setGpuOption(null);
  };

  const handleModifyData = () => {
    form.setFieldValue('name', modifyData?.name);
    setNameOption(modifyData?.name);
    form.setFieldsValue({
      image_name: modifyData?.image,
      inference_accuracy: modifyData?.precision?.current,
      replicas: modifyData?.replicas,
      npus: modifyData?.npu?.current,
      max_link_num: modifyData?.max_link_num
    })
  }

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
      const { name, inference_accuracy, image_name, npus, replicas, max_link_num } = values;
      const modelParams = {
        name,
        image_name,
        inference_accuracy,
        replicas,
        node_port: 80,
        max_link_num,
        npus: parseInt(npus),
      };
      createModel(modelParams).then((res) => {
        if (res && res.code === 0) {
          message.success('模型部署成功');
          getModelList().then((res) => {
            if (res) {
              setModels(res.llms);
            }
          });
          form.resetFields();
          form.setFieldValue('max_link_num', 300);
        }
      });
      setOpen(false);
    });
  };

  const replicasChange = (e) => {
    setLinkNumMax(300 * e);
  }

  return (
    <Drawer
      destroyOnClose
      title={
        <div
          className='app-title'
          style={{
            display: 'flex',
            gap: 200,
          }}
        >
          <div className='app-title-left'>
            <span>{modifyData ? '修改' : '创建'}大模型服务</span>
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
          <Select disabled={modifyData} options={nameOptions} value={nameOption} onChange={handleNameChange} />
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
            { required: true, message: '请输入大模型实例数' },
            {
              type: 'number',
              max: 8,
              min: 1,
              message: `输入范围为1 - ${8}`
            }
          ]}
        >
          <InputNumber style={{ width: '100%' }} min={1} max={8} onChange={replicasChange} />
        </Form.Item>
        <Form.Item
          label='单实例消耗的NPU数'
          name='npus'
          rules={[
            { required: true, message: '请输入单实例消耗的NPU数' },
          ]}
        >
          <Select
            options={filteredGpuOption?.map((option) => ({ label: option, value: option }))}
            value={gpuOption}
          />
        </Form.Item>
        <Form.Item
          label='请求并发数'
          name='max_link_num'
          rules={[
            { required: true, message: '请输入请求并发数' },
            {
              type: 'number',
              max: linkNumMax,
              min: 1,
              message: `输入范围为1 - ${linkNumMax}`
            }
          ]}
        >
          <InputNumber style={{ width: '100%' }} min={1} max={linkNumMax} />
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
          {modifyData ? '修改' : '部署模型'}
        </Button>
        <Button onClick={() => setOpen(false)}>取消</Button>
      </div>
    </Drawer>
  );
};

export default ModelCreate;
