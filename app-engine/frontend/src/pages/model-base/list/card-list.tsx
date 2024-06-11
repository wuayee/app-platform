
import { Pagination } from 'antd';
import React, { useEffect, useState } from 'react';
import CardItem from './card-item';

interface props {
  data: any
}

const ModelBaseCard = () => {
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

  return (
    <>
      <div style={{
        height: '100%',
        minHeight: "500px",
        maxHeight: "calc(100% - 200px)",
        display: 'flex',
        gap: '16px',
        flexWrap: 'wrap',
        alignContent: 'flex-start',
        marginBottom: 16
      }}>
        {data?.data.map((item: any) => <CardItem data={item} />)}
      </div>
      <div style={{
        width: '100%',
        display: 'flex',
        'justifyContent': 'space-between',
        fontSize: '12px'
      }}>
        <span>Total: {data.total}</span>
        <Pagination size='small' total={data.total} showSizeChanger showQuickJumper />
      </div>

    </>

  );
};
export default ModelBaseCard;
