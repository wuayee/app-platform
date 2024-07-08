import { Button, Col, Flex, Form, Input, InputNumber, Modal, Radio, Row, Select, message } from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import GoBack from '../../../components/go-back/GoBack';
import { queryModelbaseList, queryModelVersionList } from '../../../shared/http/model-base';
import './index.scoped.scss';
import { bytesToSize } from '../../../common/util';
import { getDatasetVersions, getDatasets, get_eDataMateLogin, login_eDataMate, postModelTraningTask } from '../../../shared/http/model-train';
import { validate } from 'webpack';
import { FormatQaNumber } from '../../helper';

const ModelTrainingCreate = () => {
  const [typeSelected, setTypeSelected] = useState('');
  const [model,setModel]=useState(undefined);
  const [modelOptions, setModelOptions] = useState<any[]>([]);
  const [versionOptions, setVersionOptions] = useState<any[]>([]);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [globalBatchSize, setGlobalBatchSize] = useState<any>('--');
  const [datasetVerified, setDatasetVerified] = useState(0);  //0认证中，1认证成功，2认证失败
  const [openVerify, setOpenVerify] = useState(false);
  const [datasetList,setDatasetList]=useState([]);
  const [datasetVersionList,setDatasetVersionList]=useState([]);
  const [dataset,setDastaset] = useState(undefined);
  const [datasetVersion,setDastasetVersion] = useState(undefined);
  const [form] = Form.useForm();
  const [verifyForm] = Form.useForm();
  const [messageApi, contextHolder] = message.useMessage();

  useEffect(() => {
    getModelOptions();
    datasetLogin();
    form.setFieldsValue({
      loraR: 8,
      loraAlpha: 16,
      maxSeqLength: 1024,
      epoch: 5,
      learningRate: 0.00005,
      warmUpRatio: 0.05,
      localBatchSize: 1,
      gradientAccuStep: 1,
      mode:'FULL'
    });
  }, []);

  //获取model全量列表
  const getModelOptions = () => {
    queryModelbaseList({ limit: 0, offset: 0 }).then(res => {
      setModelOptions(res?.modelInfoList);
    });
  }

  //选择model后获取对应的版本列表
  const getModelVersions =async(val,option) => {
    //TODO:调用获取模型详情接口获取版本列表
    setModel(option);
    form.resetFields(['modelVersionNo']);
    const res= await queryModelVersionList(option?.model_name)
    setVersionOptions(res?.versionInfo)
  }

  //TODO:获取dataset的认证连通性检查，修改datasetVerified状态；进入创建页面时调用一次
  const datasetLogin = async() => {
    const res= await get_eDataMateLogin();
    if(res===true){
    //认证成功...
    setDatasetVerified(1); 
    getDatasetList();}
    else{
    //认证失败...
    setDatasetVerified(2);
    }
  }

  const inputWidth = 380;
  const navigate = useNavigate();

  const typeOptions = [
    {
      value: 'FULL',
      label: '全参训练'
    },
    {
      value: 'LORA',
      label: 'LoRA微调'
    }
  ];

  //TODO：创建任务表单提交
  const onFinish = async(value: any) => {
    const { version_path } = versionOptions.find(item=>item.version_no === value.modelVersionNo);
    const paramBody={
      ...value,
      datasetName:dataset?.name,
      modelType:model?.model_type,
      versionPath:version_path,
    }
   const res= await postModelTraningTask(paramBody);
   if(res)
   {
    navigate('/model-training');
   }
  } 

  //TODO：身份认证提交
  const verifySubmit =async (value: any) => {
    const res = await login_eDataMate(value);
    //认证成功...
    if(res===true)
    {
    setDatasetVerified(1);
    getDatasetList();
    setOpenVerify(false);}
    else{
    messageApi.open({
      type: 'error',
      content: '登录失败，请检查用户名或密码，或者联系管理员',
    });
    }
  }

  // 获取数据集接口
  const getDatasetList = async() => {
   const res= await getDatasets({datasetType:1, pagination: {
      page: 0,
      limit: 100
   }});
   setDatasetList(res);
  }

  // 选择数据集后
  const selectDataset=async(value,option)=>{
    form.resetFields(['datasetVersionId']);
    setDastasetVersion(null);
    // 数据集详情
    setDastaset(option);
    // 获取数据集版本
    const res= await getDatasetVersions(value,{typeFilter:1,sourceType:['local'],pagination: {
    page: 0,
    limit: 100
  }})
    setDatasetVersionList(res);
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
      {contextHolder}
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
        <Form form={form} layout='vertical'  onFinish={onFinish}>
          <h3 style={{ fontSize: 18, marginBottom: 16 }}>模型和训练类型</h3>
          <Flex gap={16}>
            <Form.Item required label='模型' name='modelName' rules={[
              {
                required: true, message: '不能为空。',
              }
            ]}>
              <Select
                style={{ width: inputWidth }}
                options={modelOptions}
                onChange={getModelVersions}
                fieldNames={{label:'model_name',value:'model_name'}}
              ></Select>
            </Form.Item>
            <Form.Item required label='模型版本' name='modelVersionNo' rules={[
              {
                required: true, message: '不能为空。',
              }
            ]}>
              <Select style={{ width: inputWidth }} options={versionOptions} fieldNames={{label:'version_no',value:'version_no'}}></Select>
            </Form.Item>
          </Flex>
          <Form.Item label='训练类型' name='mode' rules={[
            {
              required: true, message: '不能为空。',
            }
          ]}>
            <Radio.Group value={typeSelected} onChange={(e) => {setTypeSelected(e.target.value)}}>
              {typeOptions.map(item => (
                <Radio value={item.value}>{item.label}</Radio>
              ))}
            </Radio.Group>
          </Form.Item>
          {typeSelected === 'LORA' &&
            <Flex gap={16} style={{ width: 800 }} wrap>
              <Form.Item required label='lora-rank' name='loraR' tooltip='lora微调中低秩矩阵的维度。' rules={[
                {
                  required: true, message: '不能为空。',
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
              <Form.Item required label='lora-alpha' name='loraAlpha' tooltip='lora学习率放缩因子，控制LORA参数的更新（一般为r的2倍）。' rules={[
                {
                  required: true, message: '不能为空。',
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
              tooltip='张量并行，把线性层按行或列对模型权重进行划分。'
              rules={[
                {
                  required: true,type:'number',min:1
                }
              ]}
            >
              <InputNumber style={{ width: inputWidth }} />
            </Form.Item>
            <Form.Item required
              label='PP'
              name='pp'
              tooltip='管道并行，对模型进行层间划分。'
              rules={[
                {
                  required: true,type:'number',min:1
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
          <Flex gap={16} align='center' style={{ marginTop: 8 }}>
            <h3 style={{ fontSize: 18, margin: '16px 0' }}>数据集</h3>
            <div hidden={datasetVerified === 1}>
              使用数据集需要先进行 <a onClick={() => setOpenVerify(true)}>身份认证</a>
            </div>
          </Flex>
          <Flex gap={16}>
            <Form.Item  required label='数据集' name='datasetId' rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}>
              <Select options={datasetList} fieldNames={{label:'name',value:'id'}} style={{ width: inputWidth }}
              onSelect={selectDataset} disabled={datasetVerified !== 1}></Select>
            </Form.Item>
            <Form.Item required label='数据集版本' name='datasetVersionId' rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}>
              <Select options={datasetVersionList} style={{ width: inputWidth }} onSelect={(val,option)=>{setDastasetVersion(option)}}
               labelRender={(option)=>{return `V${option?.label}`}}
               fieldNames={{label:'version',value:'versionId'}} optionRender={(option)=>{ return `V${option?.label}`}}
                disabled={datasetVerified !== 1}></Select> 
            </Form.Item>
          </Flex>
          <Row style={{ width: 800 }}>
            <Col span={6}>
              <div className='input-label'>数据集名称</div>
              <div className='input-value'>{dataset?.name||'--'}</div>
            </Col>
            <Col span={6}>
              <div className='input-label'>版本大小</div>
              <div className='input-value'>{bytesToSize(datasetVersion?.totalSize)}</div>
            </Col>
            <Col span={6}>
              <div className='input-label'>版本规格</div>
              <div className='input-value'>{FormatQaNumber(datasetVersion?.prompts)}</div>
            </Col>
            <Col span={6}>
              <div className='input-label'>版本描述</div>
              <div className='input-value'>{datasetVersion?.description|| '--'}</div>
            </Col>
          </Row>
          <h3 style={{ fontSize: 18, margin: '16px 0' }}>训练参数</h3>
          <Flex gap={16} style={{ width: 800 }} wrap>
            <Form.Item required
              label='最大序列长度'
              name='maxSeqLength'
              rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}
              tooltip='决定了模型可以处理的输入文本的最大长度，长度越长，显存开销越大。'
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
              tooltip='epoch ，遍历数据集的次数。（训练中会被转换成iter的形式）。'
            >
              <Select
                style={{ width: inputWidth }}
                options={Array.from({ length: 7 }).fill(null).map((_, index) => (
                  { value: index + 2, label: index + 2 }
                ))} />
            </Form.Item>
            <Form.Item required
              label='Local Batch Size'
              name='localBatchSize'
              rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}
              tooltip='所有节点和机器上，进行一次权重更新，全部训练数据总量。由Local Batch Size * NPU数得到。'
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
              label='Gradient Accumulation Steps'
              name='gradientAccuStep'
              rules={[
                {
                  required: true, message: '不能为空',
                }
              ]}
              tooltip='梯度累计步数。'
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
                  required: true, type:'number',min:1,max:8,
                }
              ]}
              tooltip='NPU数最大值为8'
            >
              <InputNumber onChange={culculateGBSize} style={{ width: inputWidth }} />
            </Form.Item>
            <Form.Item label='Global Batch Size' tooltip='所有节点和机器上，进行一次权重更新，全部训练数据总量。由Local Batch Size * NPU * Gradient Accumulation Steps得到。'>
              <span>{globalBatchSize}</span>
            </Form.Item>
            <Form.Item required
              label='学习率'
              name='learningRate'
              rules={[
                {
                  required: true, message: '不能为空。',
                }
              ]}
              tooltip='学习率，控制权重参数的更新速度。'
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
            <Form.Item required label='预热比例' name='warmUpRatio' rules={[
              {
                required: true, message: '不能为空。',
              }
            ]} tooltip='学习率预热比例, 表明预热过程中，学习率的增长轮数和总训练轮数的比例。'>
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
      <Modal
        title='身份认证'
        open={openVerify}
        onCancel={() => setOpenVerify(false)}
        onOk={verifyForm.submit}
      >
        <p>请输入eDataMate系统用户名和密码进行身份认证。</p>
        <Form form={verifyForm} layout='vertical' onFinish={verifySubmit}>
          <Form.Item required
            label='用户名'
            name='userName'
            rules={[
              {
                required: true, message: '不能为空'
              }
            ]}
          >
            <Input style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item required
            label='密码'
            name='password'
            rules={[
              {
                required: true, message: '不能为空'
              }
            ]}
          >
            <Input style={{ width: '100%' }} type='password' />
          </Form.Item>
        </Form>
      </Modal>
    </div >
  );
};
export default ModelTrainingCreate;
