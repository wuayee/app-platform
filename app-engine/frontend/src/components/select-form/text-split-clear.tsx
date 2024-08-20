import React from 'react';
import { FormInstance, Radio, Form, Checkbox, Input } from 'antd';
import { KnowledgeIcons } from '../icons';
import { useTranslation } from 'react-i18next';
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
  const { t } = useTranslation();
  const initialValues: FieldType = {
    splitType: 'PARAGRAPH',
  };
  // 监听文本清洗算子
  const textCleanOperatorChange = Form.useWatch('operatorIds', form);
  // 监听文本分段
  const textSplit = Form.useWatch('splitType', form);
  const operatorOptions = [
    {
      label: t('documentDirectoryRemoval'),
      value: 'com.huawei.eDataMate.operators.content_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: t('documentEmoticonRemoval'),
      value: 'com.huawei.eDataMate.operators.emoji_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: t('redundantSpaceRemoval'),
      value: 'com.huawei.eDataMate.operators.extra_space_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: t('fullToHalf'),
      value: 'com.huawei.eDataMate.operators.full_width_characters_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: t('documentGarbleRemoval'),
      value: 'com.huawei.eDataMate.operators.garble_characters_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: t('htmlTagRemoval'),
      value: 'com.huawei.eDataMate.operators.html_tag_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: t('invisibleCharactersRemoval'),
      value: 'com.huawei.eDataMate.operators.invisible_characters_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: t('figureTableRemoval'),
      value: 'com.huawei.eDataMate.operators.legend_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: t('traditionalToSimplified'),
      value: 'com.huawei.eDataMate.operators.traditional_chinese_plugin',
      icon: <KnowledgeIcons.operator />,
    },
    {
      label: t('spaceNormalizationPlug'),
      value: 'com.huawei.eDataMate.operators.unicode_space_cleaner_plugin',
      icon: <KnowledgeIcons.operator />,
    },
  ];

  const docSegmentOptions = [
    {
      label: t('paragraphed'),
      value: 'PARAGRAPH',
      icon: <KnowledgeIcons.local />,
    },
    {
      label: t('sentences'),
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
          label={t('selectTextSegment')}
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
              label={t('setsFragmentlength')}
              rules={[{ required: true, message: t('plsEnterRequiredItem') }]}
              name='chunkSize'
              style={{
                marginTop: 16,
              }}
            >
              <Input placeholder={t('plsEnter')} />
            </Form.Item>
            <Form.Item
              label={t('degreeOfOverlap')}
              rules={[{ required: true, message: t('plsEnterRequiredItem') }]}
              name='chunkOverlap'
              style={{
                marginTop: 16,
              }}
            >
              <Input placeholder={t('plsEnter')} />
            </Form.Item>
          </>
        ) : (
            <>
              <Form.Item
                label={t('selectingCleansing')}
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
