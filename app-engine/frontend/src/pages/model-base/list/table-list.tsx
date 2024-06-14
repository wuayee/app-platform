
import { Space, Table } from 'antd';
import React, { useEffect, useState } from 'react';
import type { PaginationProps, TableColumnsType } from 'antd';
import TableTextSearch from '../../../components/table-text-search';
import { useNavigate } from 'react-router';

interface props {
  data: any
}
const showTotal: PaginationProps['showTotal'] = (total) => `Total: ${total}`;

const ModelBaseTable = () => {

  const navigate = useNavigate();

  const data = {
    data: [
      {
        id: 1,
        name: 'Qwen2-7B-Instruct',
        creator: 'Alibaba',
        description: 'Qwen2 is the new series of Qwen large language models. For Qwen2, we release a number of base language models and instruction-tuned language models ranging from 0.5 to 72 billion parameters, including a Mixture-of-Experts model. This repo contains the instruction-tuned 7B Qwen2 model.',
        tags: ['Qwen', 'Alibaba Cloud'],
        versionNum: 2,
        type: '语言模型',
        series: 'Qwen2',
        size: '14GB'
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
        size: '140GB'
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
        size: '16GB'
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
        size: '18GB'
      },
    ],
    total: 4
  };

  const typeOptions = [
    {
      text: '语言模型',
      value: 'llm',
    },
  ];

  const seriesOptions = [
    {
      text: '千问',
      value: 'qwen'
    },
    {
      text: 'Llama2',
      value: 'llama2'
    }
  ];

  const columns: TableColumnsType = [
    {
      key: 'id',
      dataIndex: 'id',
      title: 'ID',
      hidden: true
    },
    {
      key: 'name',
      dataIndex: 'name',
      title: '模型',
      sorter: true,
      ...TableTextSearch('name', true),
      render: (_, record) => (
        <a onClick={() => { navigate(`/model-base/${record.id}/detail`); }}>
          {record.name}
        </a>
      ),
    },
    {
      key: 'type',
      dataIndex: 'type',
      title: '类型',
      filters: typeOptions,
      sorter: true,
    },
    {
      key: 'creator',
      dataIndex: 'creator',
      title: '作者',
      sorter: true,
      ...TableTextSearch('creator', true),
    },
    {
      key: 'series',
      dataIndex: 'series',
      title: '模型系列',
      filters: seriesOptions,
      sorter: true,
    },
    {
      key: 'description',
      dataIndex: 'description',
      title: '描述',
      ellipsis: true,
    },
    {
      key: 'versionNum',
      dataIndex: 'versionNum',
      title: '版本数量',
      sorter: true,
    },
    {
      key: 'size',
      dataIndex: 'size',
      title: '模型大小',
      sorter: true,
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

  //TODO：列表分页，筛选和排序项变更时的回调方法，触发数据调用方法
  const fetchData = (pagination, filters, sorter) => {
    console.log(pagination, filters, sorter);
  }

  return (
    <Table
      dataSource={data?.data}
      columns={columns}
      scroll={{ y: '800px' }}
      pagination={{
        size: 'small',
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: showTotal,
      }}
      onChange={fetchData}
    />
  );
};
export default ModelBaseTable;
