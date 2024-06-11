
import { Space, Table } from 'antd';
import React, { useEffect, useState } from 'react';
import type { PaginationProps, TableColumnsType } from 'antd';
import TableTextSearch from '../../../components/table-text-search';

interface props {
  data: any
}
const showTotal: PaginationProps['showTotal'] = (total) => `Total: ${total}`;

const ModelBaseTable = () => {

  const data = {
    data: [
      {
        name: 'Qwen-7B-Chat',
        creator: 'Alibaba',
        description: '通义千问-7B（Qwen-7B）',
        tags: ['Qwen', 'Alibaba Cloud'],
        versionNum: 1,
        type: '语言模型',
        series: 'Qwen',
        size: '15GB'
      },
      {
        name: 'Qwen-14B-Chat',
        creator: 'Alibaba',
        description: '通义千问-14B（Qwen-14B）是阿里云研发的通义千问大模型系列的140亿参数规模的模型。Qwen-14B是基于Transformer的大语言模型, 在超大规模的预训练数据上进行训练得到。预训练数据类型多样，覆盖广泛，包括大量网络文本、专业书籍、代码等。',
        tags: ['Qwen', 'Alibaba Cloud'],
        versionNum: 1,
        type: '语言模型',
        series: 'Qwen',
        size: '50GB'
      },
      {
        name: 'Llama2-70B-Chat',
        creator: 'Meta',
        description: 'Meta developed and publicly released the Llama 2 family of large language models (LLMs), a collection of pretrained and fine-tuned generative text models ranging in scale from 7 billion to 70 billion parameters. Our fine-tuned LLMs, called Llama-2-Chat, are optimized for dialogue use cases. Llama-2-Chat models outperform open-source chat models on most benchmarks we tested, and in our human evaluations for helpfulness and safety, are on par with some popular closed-source models like ChatGPT and PaLM.',
        tags: ['Meta', 'Llama2'],
        versionNum: 1,
        type: '语言模型',
        series: 'Llama2',
        size: '40GB'
      },
      {
        name: 'Llama2-14B-Chat',
        creator: 'Alibaba',
        description: 'Meta developed and publicly released the Llama 2 family of large language models (LLMs), a collection of pretrained and fine-tuned generative text models ranging in scale from 7 billion to 70 billion parameters. Our fine-tuned LLMs, called Llama-2-Chat, are optimized for dialogue use cases. Llama-2-Chat models outperform open-source chat models on most benchmarks we tested, and in our human evaluations for helpfulness and safety, are on par with some popular closed-source models like ChatGPT and PaLM.',
        tags: ['Meta', 'Llama2'],
        versionNum: 1,
        type: '语言模型',
        series: 'Llama2',
        size: '40GB'
      },
    ],
    total: 4
  };

  const typeOptions = [
    {
      text: '语言模型',
      value: 'llm'
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
