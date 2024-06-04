import { FormInstance, Radio, Form, Checkbox, Card, Input } from 'antd';
import React from 'react';
import { KnowledgeIcons } from '../icons';
import './text-split.scoped.scss';
import './style.scoped.scss';

// 单纯为text服务
interface props {
  form: FormInstance;
}

// 选择数据源表单配置
type FieldType = {
  // 选择清洗类型
  splitType: 'PARAGRAPH' | 'SENTENCE' | 'TOKEN';

  // 文本清洗算子
  operatorIds?: string[];

  // 设置分段数
  chunkSize?: number;

  // 设置重叠度
  chunkOverlap?: number;
};

const TextSplitClear = ({ form }: props) => {
  const initialValues: FieldType = {
    splitType: 'PARAGRAPH',
  };

  // 监听文本清洗算子
  const textCleanOperatorChange = Form.useWatch('operatorIds', form);

  // 监听文本分段
  const textSplit = Form.useWatch('splitType', form);

  const operatorOptions = [
    {
      label: '文档目录去除',
      value: 'com.huawei.eDataMate.operators.content_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: '文档表情去除',
      value: 'com.huawei.eDataMate.operators.emoji_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: '多余空格去除',
      value: 'com.huawei.eDataMate.operators.extra_space_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: '全角转半角',
      value: 'com.huawei.eDataMate.operators.full_width_characters_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: '文档乱码去除',
      value: 'com.huawei.eDataMate.operators.garble_characters_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: 'HTML标签去除',
      value: 'com.huawei.eDataMate.operators.html_tag_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: '不可见字符去除',
      value: 'com.huawei.eDataMate.operators.invisible_characters_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: '图注表注去除',
      value: 'com.huawei.eDataMate.operators.legend_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: '繁体转简体',
      value: 'com.huawei.eDataMate.operators.traditional_chinese_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: '空格标准化插件',
      value: 'com.huawei.eDataMate.operators.unicode_space_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
  ];

  const docSegmentOptions = [
    {
      label: '段落',
      value: 'PARAGRAPH',
      icon: <KnowledgeIcons.local />,
    },
    {
      label: '句子',
      value: 'SENTENCE',
      icon: <KnowledgeIcons.nas />,
    },
    {
      label: 'token',
      value: 'TOKEN',
      icon: <KnowledgeIcons.custom />,
    },
  ];

  return (
    <div>
      <Form<FieldType>
        layout={'vertical'}
        form={form}
        initialValues={initialValues}
        style={{ maxWidth: 800 }}
      >
        <Form.Item
          label='选择文本分段'
          name='splitType'
          style={{
            marginTop: 16,
          }}
        >
          <Radio.Group className='radio-card-group'>
            {docSegmentOptions.map((option) => (
              <Radio.Button
                value={option.value}
                style={{ borderColor: textSplit === option.value ? '#1677ff' : '' }}
              >
                <div className='radio-card-item'>
                  {option.icon}
                  <span>{option.label}</span>
                </div>
              </Radio.Button>
            ))}
          </Radio.Group>
        </Form.Item>

        {textSplit === 'TOKEN' ? (
          <>
            <Form.Item
              label='设置分片长度'
              rules={[{ required: true, message: '输入不能为空' }]}
              name='chunkSize'
              style={{
                marginTop: 16,
              }}
            >
              <Input placeholder='请输入分段数' />
            </Form.Item>
            <Form.Item
              label='重叠度'
              rules={[{ required: true, message: '输入不能为空' }]}
              name='chunkOverlap'
              style={{
                marginTop: 16,
              }}
            >
              <Input placeholder='请输入重叠度' />
            </Form.Item>
          </>
        ) : (
          <>
            <Form.Item
              label='选择清洗算子'
              
              name='operatorIds'
              style={{
                marginTop: 16,
              }}
            >
              <Checkbox.Group className='radio-card-group'>
                {operatorOptions.map((operator) => (
                  <Checkbox
                    value={operator.value}
                    style={{
                      borderColor: (textCleanOperatorChange || []).includes(operator.value)
                        ? '#1677ff'
                        : '',
                    }}
                  >
                    <div className='radio-card-item'>
                      {operator.icon}
                      <span>{operator.label}</span>
                    </div>
                  </Checkbox>
                ))}
              </Checkbox.Group>
            </Form.Item>
          </>
        )}
      </Form>
    </div>
  );
};

export { TextSplitClear };
