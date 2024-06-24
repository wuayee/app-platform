import { Button, Col, Flex, Form, InputNumber, Modal, Radio, Row, Select } from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import GoBack from '../../../components/go-back/GoBack';
import './index.scoped.scss';

const ModelTrainingCreate = () => {

  const [modeSelected, setModeSelected] = useState('');
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [globalBatchSize, setGlobalBatchSize] = useState<any>('--');

  const [form] = Form.useForm();

  useEffect(() => {
    form.setFieldsValue({
      loraR: 8,
      loraAlpha: 16,
      maxSequence: 1024,
      epoch: 5,
      learningRate: 0.00005,
      warmupRatio: 0.05,
      localBatchSize: 1,
      gradientAccuStep: 1,
      mode: 'full',
    });
  }, []);

  const inputWidth = 380;
  const navigate = useNavigate();

  const modeOptions = [
    {
      value: 'full',
      label: '全参训练'
    },
    {
      value: 'lora',
      label: 'LoRA微调'
    }
  ];

  const onFinish = (value: any) => {
    console.log(value);
  }

  const culculateGBSize = () => {
    const lbs = form.getFieldValue('localBatchSize');
    const gas = form.getFieldValue('gradientAccuStep');
    const npu = form.getFieldValue('npuNum');
    if (lbs * gas * npu) {
      setGlobalBatchSize(lbs * gas * npu);
    } else {
      setGlobalBatchSize('--');
    }
  }


  return (
    <div className='aui-fullpage'>
      <div className='aui-header-1'>
        <div className='aui-title-1' style={{ alignItems: 'center' }}>
          <GoBack path={'/model-training'} title='创建训练任务' />
        </div>
      </div>
      <div className='aui-block'
        style={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'space-between'
        }}>
        <Form form={form} layout='vertical' onFinish={onFinish}>
          <h3 style={{ fontSize: 18, marginBottom: 16 }}>模型和训练类型</h3>
          <Flex gap={16}>
            <Form.Item required label='模型' name='model' rules={[
              {
                required: true, message: '不能为空',
              }
            ]}>
              <Select style={{ width: inputWidth }}></Select>
            </Form.Item>
            <Form.Item required label='模型版本' name='modelVersion' rules={[
              {
                required: true, message: '不能为空',
              }
            ]}>
              <Select style={{ width: inputWidth }}></Select>
            </Form.Item>
          </Flex>
          <Form.Item label='训练类型' name='mode' rules={[
            {
              required: true, message: '不能为空',
            }
          ]}>
            <Radio.Group value={modeSelected} onChange={(val) => { setModeSelected(val.target.value) }}>
              {modeOptions.map(item => (
                <Radio value={item.value}>{item.label}</Radio>
              ))}
            </Radio.Group>
          </Form.Item>
          {modeSelected === 'lora' &&
            <Flex gap={16} style={{ width: 800 }} wrap>
              <Form.Item required label='lora-rank' name='loraR' tooltip='test' rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}>
                <Select
                  style={{ width: inputWidth }}
                  options={[
                    { value: 4, label: 4 },
                    { value: 8, label: 8 },
                    { value: 16, label: 16 },
                    { value: 32, label: 32 }
                  ]} />
              </Form.Item>
              <Form.Item required label='lora-alpha' name='loraAlpha' tooltip='test' rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}>
                <Select
                  style={{ width: inputWidth }}
                  options={[
                    { value: 8, label: 8 },
                    { value: 16, label: 16 },
                    { value: 32, label: 32 },
                    { value: 64, label: 64 }
                  ]} />
              </Form.Item>
            </Flex>
          }
          <h3 style={{ fontSize: 18, margin: '16px 0' }}>训练策略</h3>
          <Flex gap={16}>
            <Form.Item required
              label='TP'
              name='tp'
              tooltip='test'
              rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}
            >
              <InputNumber style={{ width: inputWidth }} />
            </Form.Item>
            <Form.Item required
              label='PP'
              name='pp'
              tooltip='test'
              rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}
            >
              <InputNumber style={{ width: inputWidth }} />
            </Form.Item>
          </Flex>
          <p style={{
            fontSize: 12,
            color: '#808080',
            margin: 0
          }}>TP=8，PP&gt;1 的配置需要多机多卡分布式训练
          </p>
          <h3 style={{ fontSize: 18, margin: '16px 0' }}>数据集</h3>
          <Flex gap={16}>
            <Form.Item required label='数据集' name='dataset'>
              <Select style={{ width: inputWidth }}></Select>
            </Form.Item>
            <Form.Item required label='数据集版本' name='datasetVersion'>
              <Select style={{ width: inputWidth }}></Select>
            </Form.Item>
          </Flex>
          <Row style={{ width: 800 }}>
            <Col span={6}>
              <div className='input-label'>数据集名称</div>
              <div className='input-value'>alpaca1</div>
            </Col>
            <Col span={6}>
              <div className='input-label'>数据集大小</div>
              <div className='input-value'>5GB</div>
            </Col>
            <Col span={6}>
              <div className='input-label'>数据集规格</div>
              <div className='input-value'>3.4k问答对</div>
            </Col>
            <Col span={6}>
              <div className='input-label'>数据集描述</div>
              <div className='input-value'>test</div>
            </Col>
          </Row>
          <h3 style={{ fontSize: 18, margin: '16px 0' }}>训练参数</h3>
          <Flex gap={16} style={{ width: 800 }} wrap>
            <Form.Item required
              label='最大序列长度'
              name='maxSequence'
              rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}
              tooltip='test'
            >
              <Select
                style={{ width: inputWidth }}
                options={[
                  { value: 256, label: 256 },
                  { value: 512, label: 512 },
                  { value: 1024, label: 1024 },
                  { value: 2048, label: 2048 },
                  { value: 4096, label: 4096 },
                  { value: 8192, label: 8192 }
                ]} />
            </Form.Item>
            <Form.Item required
              label='训练数据遍历次数'
              name='epoch'
              rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}
              tooltip='test'
            >
              <Select
                style={{ width: inputWidth }}
                options={Array.from({ length: 7 }).fill(null).map((_, index) => (
                  { value: index + 2, label: index + 2 }
                ))} />
            </Form.Item>
            <Form.Item required
              label='Local batch size'
              name='localBatchSize'
              rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}
              tooltip='test'
            >
              <Select onChange={culculateGBSize}
                style={{ width: inputWidth }}
                options={[
                  { value: 1, label: 1 },
                  { value: 2, label: 2 },
                  { value: 4, label: 4 }
                ]} />
            </Form.Item>
            <Form.Item required
              label='梯度累计步数'
              name='gradientAccuStep'
              rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}
              tooltip='test'
            >
              <Select onChange={culculateGBSize}
                style={{ width: inputWidth }}
                options={[
                  { value: 1, label: 1 },
                  { value: 2, label: 2 },
                  { value: 4, label: 4 },
                  { value: 8, label: 8 },
                  { value: 16, label: 16 }
                ]} />
            </Form.Item>
            <Form.Item required
              label='NPU数'
              name='npuNum'
              rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}
              tooltip='test'
            >
              <InputNumber onChange={culculateGBSize} style={{ width: inputWidth }} />
            </Form.Item>
            <Form.Item label='Global batch size' tooltip='Global batch size = Local batch size * number of NPU * Gradient Accumulation Steps'>
              <span>{globalBatchSize}</span>
            </Form.Item>
            <Form.Item required
              label='学习率'
              name='learningRate'
              rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}
              tooltip='test'
            >
              <Select
                style={{ width: inputWidth }}
                options={[
                  { value: 0.00002, label: (0.00002).toExponential() },
                  { value: 0.00005, label: (0.00005).toExponential() },
                  { value: 0.0001, label: (0.0001).toExponential() },
                  { value: 0.0002, label: (0.0002).toExponential() },
                  { value: 0.0003, label: (0.0003).toExponential() }
                ]} />
            </Form.Item>
            <Form.Item required label='学习率 warmup-ratio' name='warmupRatio' rules={[
              {
                required: true, message: '不能为空',
              }
            ]}>
              <Select
                style={{ width: inputWidth }}
                options={[
                  { value: 0.03, label: '3%' },
                  { value: 0.05, label: '5%' },
                  { value: 0.1, label: '10%' }
                ]} />
            </Form.Item>
          </Flex>
        </Form>
        <div style={{
          display: 'flex',
          justifyContent: 'end',
          gap: 16,
        }}>
          <Button
            onClick={() => setConfirmOpen(true)}
            style={{
              minWidth: 96,
              borderRadius: 4,
            }}>取消</Button>
          <Button
            type="primary"
            style={{
              minWidth: 96,
              borderRadius: 4,
            }}
            onClick={form.submit}
          >确定</Button>
        </div>
      </div>
      <Modal
        title='确定要退出创建吗？'
        open={confirmOpen}
        onCancel={() => { setConfirmOpen(false) }}
        onOk={() => { navigate('/model-training') }}
      >
        <p>点击确认将退出创建，所填数据不会被保留。点击取消可继续进行创建。</p>
      </Modal>
    </div>
  );
};
export default ModelTrainingCreate;
