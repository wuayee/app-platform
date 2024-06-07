import { Col, Flex, Row, Space, Table } from 'antd';
import React, { useEffect, useState } from 'react';
import GoBack from '../../../components/go-back/GoBack';
import type { PaginationProps, TableColumnsType } from 'antd';
import ModelConfig from './config';
import { useParams } from 'react-router';

const showTotal: PaginationProps['showTotal'] = (total) => `Total: ${total}`;

const ModelBaseDetail: React.FC = () => {

  const { id } = useParams();
  const [configOpen, setConfigOpen] = useState(false);
  const [data, setData] = useState({});
  const [versionData, setVersionData] = useState<any[]>([]);

  const baseInfoKey = [
    {
      label: '作者',
      key: 'creator'
    },
    {
      label: '模型系列',
      key: 'series'
    },
    {
      label: '最大序列长度',
      key: 'maxSequenceLength'
    },
    {
      label: '模型大小',
      key: 'size'
    },
    {
      label: '创建时间',
      key: 'createTime'
    },
    {
      label: '更新时间',
      key: 'updateTime'
    }
  ];

  useEffect(() => {
    console.log(id);
    setData({ ...detailData[Number(id) - 1] });
    setVersionData([...generateVersionData(Number(id) === 1 ? 2 : 1)]);
  }, []);

  const detailData = [
    {
      id: 1,
      name: 'Qwen2-7B-Instruct',
      creator: 'Alibaba',
      description: 'Qwen2 is the new series of Qwen large language models. For Qwen2, we release a number of base language models and instruction-tuned language models ranging from 0.5 to 72 billion parameters, including a Mixture-of-Experts model. This repo contains the instruction-tuned 7B Qwen2 model.',
      tags: ['Qwen', 'Alibaba Cloud'],
      versionNum: 2,
      type: '语言模型',
      series: 'Qwen2',
      size: '14GB',
      maxSequenceLength: '128K',
      createTime: '2024-6-7 16:32:47',
      updateTime: '2024-6-7 16:32:58'
    },
    {
      id: 2,
      name: 'Qwen2-72B-Instruct',
      creator: 'Alibaba',
      description: 'Qwen2 is the new series of Qwen large language models. For Qwen2, we release a number of base language models and instruction-tuned language models ranging from 0.5 to 72 billion parameters, including a Mixture-of-Experts model. This repo contains the instruction-tuned 72B Qwen2 model.',
      tags: ['Qwen', 'Alibaba Cloud'],
      versionNum: 1,
      type: '语言模型',
      series: 'Qwen2',
      size: '140GB',
      maxSequenceLength: '128K',
      createTime: '2024-6-7 16:32:47',
      updateTime: '2024-6-7 16:32:58'
    },
    {
      id: 3,
      name: 'Meta-Llama-3-8B-Instruct',
      creator: 'Meta',
      description: 'Meta developed and released the Meta Llama 3 family of large language models (LLMs), a collection of pretrained and instruction tuned generative text models in 8 and 70B sizes. The Llama 3 instruction tuned models are optimized for dialogue use cases and outperform many of the available open source chat models on common industry benchmarks. Further, in developing these models, we took great care to optimize helpfulness and safety.',
      tags: ['Meta', 'Llama3'],
      versionNum: 1,
      type: '语言模型',
      series: 'LLaMA3',
      size: '16GB',
      maxSequenceLength: '8192',
      createTime: '2024-6-7 16:32:47',
      updateTime: '2024-6-7 16:32:58'
    },
    {
      id: 4,
      name: 'glm-4-9b-chat',
      creator: 'THUDM',
      description: 'GLM-4-9B 是智谱 AI 推出的最新一代预训练模型 GLM-4 系列中的开源版本。 在语义、数学、推理、代码和知识等多方面的数据集测评中， GLM-4-9B 及其人类偏好对齐的版本 GLM-4-9B-Chat 均表现出超越 Llama-3-8B 的卓越性能。除了能进行多轮对话，GLM-4-9B-Chat 还具备网页浏览、代码执行、自定义工具调用（Function Call）和长文本推理（支持最大 128K 上下文）等高级功能。本代模型增加了多语言支持，支持包括日语，韩语，德语在内的 26 种语言。我们还推出了支持 1M 上下文长度（约 200 万中文字符）的 GLM-4-9B-Chat-1M 模型和基于 GLM-4-9B 的多模态模型 GLM-4V-9B。GLM-4V-9B 具备 1120 * 1120 高分辨率下的中英双语多轮对话能力，在中英文综合能力、感知推理、文字识别、图表理解等多方面多模态评测中，GLM-4V-9B 表现出超越 GPT-4-turbo-2024-04-09、Gemini 1.0 Pro、Qwen-VL-Max 和 Claude 3 Opus 的卓越性能。',
      tags: ['Meta', 'ChatGLM4'],
      versionNum: 1,
      type: '语言模型',
      series: 'ChatGLM4',
      size: '18GB',
      maxSequenceLength: '128K',
      createTime: '2024-6-7 16:32:47',
      updateTime: '2024-6-7 16:32:58'
    },
  ];

  const generateVersionData = (length: number) => {
    return Array.from({ length }).fill(null).map((_, index) => ({
      version: `${index + 1}.0`,
      description: detailData[Number(id) - 1].description,
      updateTime: '2024-6-7 17:07:40',
      framework: index > 0 ? 'ModelLink' : 'N/A',
      type: index > 0 ? '全参微调' : 'N/A',
      policy: index > 0 ? `TP8PP1` : 'N/A',
      duration: index > 0 ? '6分30秒' : 'N/A',
      loss: index > 0 ? 0.6123 : 'N/A'
    }));
  }

  const columns: TableColumnsType = [
    {
      key: 'id',
      dataIndex: 'id',
      title: 'ID',
      hidden: true
    },
    {
      key: 'version',
      dataIndex: 'version',
      title: '版本号',
    },
    {
      key: 'description',
      dataIndex: 'description',
      title: '版本说明',
      ellipsis: true
    },
    {
      key: 'updateTime',
      dataIndex: 'updateTime',
      title: '更新时间',
    },
    {
      key: 'framework',
      dataIndex: 'framework',
      title: '训练框架',
    },
    {
      key: 'type',
      dataIndex: 'type',
      title: '训练类型',
    },
    {
      key: 'policy',
      dataIndex: 'policy',
      title: '训练策略',
    },
    {
      key: 'duration',
      dataIndex: 'duration',
      title: '训练时长',
    },
    {
      key: 'loss',
      dataIndex: 'loss',
      title: '最终loss',
    },
    {
      key: 'action',
      title: '操作',
      render() {
        return (
          <Space size='middle'>
            <a>删除</a>
          </Space>
        )
      },
    }
  ];

  const configCallback = () => {
    setConfigOpen(false);
  }

  return (
    <div className='aui-fullpage'>
      <div className='aui-header-1'>
        <div className='aui-title-1' style={{ alignItems: 'center' }}>
          <GoBack path={'/model-base'} title='模型详情' />
        </div>
      </div>
      <div className='aui-block'>
        <Flex gap={16} vertical>
          {/* 模型名称头部 */}
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <Flex gap={16} style={{ alignItems: 'center' }}>
              <h2 style={{ fontSize: 20, fontWeight: 400 }}>{data.name}</h2>
              <span>版本数：{data.versionNum}</span>
            </Flex>
            <a onClick={() => { setConfigOpen(true) }}>配置详情</a>
          </div>
          {/* 模型描述 */}
          <div title={data.description}
            style={{
              display: '-webkit-box',
              textOverflow: 'ellipsis',
              overflow: 'hidden',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
            }}>
            {data.description}
          </div>
          {/* 模型其他基础信息 */}
          <div>
            <Row>
              {baseInfoKey.map(item => (
                <Col span={4}>
                  <Flex vertical>
                    <span style={{ fontSize: 12, color: '#4D4D4D' }}>{item.label}</span>
                    <span style={{ fontSize: 14, color: '#1A1A1A' }}>{data[item.key]}</span>
                  </Flex>
                </Col>
              ))}
            </Row>
          </div>
          {/* 版本列表*/}
          <div>
            <Table
              columns={columns}
              dataSource={versionData}
              pagination={{
                size: 'small',
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: showTotal,
              }}
            />
          </div>
        </Flex>
      </div>
      <ModelConfig visible={configOpen} callback={configCallback} />
    </div>
  );
};

export default ModelBaseDetail;
