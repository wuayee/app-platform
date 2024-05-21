import { FormInstance, Radio, Form, Checkbox, Card, Input } from 'antd';
import React, { useEffect, useState } from 'react';
import { KnowledgeIcons } from '../icons';
import './text-split.scoped.scss'

// 单纯为text服务
interface props {
  form: FormInstance;
}

// 选择数据源表单配置
type FieldType = {
  // 选择清洗类型
  clearType: 'paragraphed' | 'sentences' | 'token';

  // 文本清洗算子
  textCleanOperator?: string[];

  // 设置分段数
  splitCount?: number;

  // 设置重叠度
  repeatNumber?: number;
};

const TextSplitClear = ({ form }: props)=> {
  const initialValues: FieldType = {
    clearType: 'paragraphed'
  };

  // 监听文本清洗算子
  const textCleanOperatorChange = Form.useWatch('textCleanOperator', form);

  // 监听文本分段
  const textSplit = Form.useWatch('clearType', form);


  const options = [
    { label: '文本清洗算子1', value: 'op1' },
    { label: '文本清洗算子2', value: 'op2' },
    { label: '文本清洗算子3', value: 'op3' },
    { label: '文本清洗算子4', value: 'op4' },
    { label: '文本清洗算子5', value: 'op5' },
    { label: '文本清洗算子6', value: 'op6' },
  ];

  return (
    <>
      <div>
      <Form<FieldType>
            layout={'vertical'}
            form={form}
            initialValues={initialValues}
            style={{ maxWidth: 800 }}
          >
            <Form.Item label="选择文本分段" name = 'clearType' style={{
              marginTop: 16
            }}>
              <Radio.Group size="large" style={{
                  display: 'flex',
                  justifyContent: 'space-between'
                }}>
                  <Radio.Button style={{
                    width: 257,
                    height: 64,
                    borderRadius: 4,
                    display: 'flex',
                    alignItems: 'center'
                  }} value="paragraphed"> 
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: 4,
                      }}>
                        {<KnowledgeIcons.local></KnowledgeIcons.local>} 段落
                    </div>
                  </Radio.Button>
                  <Radio.Button value="sentences" style={{
                    width: 257,
                    height: 64,
                    borderRadius: 4,
                    display: 'flex',
                    alignItems: 'center'
                  }}>
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: 4,
                      }}>
                      {<KnowledgeIcons.nas></KnowledgeIcons.nas>} 句子
                    </div>
                  </Radio.Button>
                  <Radio.Button value="token" style={{
                    width: 257,
                    height: 64,
                    borderRadius: 4,
                    display: 'flex',
                    alignItems: 'center'
                  }}>
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: 4,
                      }}>
                      {<KnowledgeIcons.custom></KnowledgeIcons.custom>} token
                    </div>
                  </Radio.Button>
              </Radio.Group>
            </Form.Item>

            { textSplit === 'token' ? (<>
              <Form.Item label="设置分段数" rules={[{ required: true, message: '输入不能为空' }]} name = 'splitCount' style={{
                    marginTop: 16
                  }}>
                    <Input placeholder='请输入分段数'/>
              </Form.Item>
              <Form.Item label="重叠度" rules={[{ required: true, message: '输入不能为空' }]} name = 'repeatNumber' style={{
                marginTop: 16
              }}>
                <Input placeholder='请输入重叠度'/>
              </Form.Item>
            </>) : (<>
              <Form.Item label="选择清洗算子" rules={[{ required: true, message: '选择不能为空' }]}  name = 'textCleanOperator' style={{
                marginTop: 16
              }}>
                <Checkbox.Group style={{
                        display: 'flex',
                        gap: 14
                      }}>
                    {options.map((item)=> (
                      <div >
                        <Checkbox value={item.value} className="checkbox-custom">
                          <Card style={{ width: 257, height: 78, boxSizing: 'border-box' }} className={(textCleanOperatorChange || []).includes(item.value) ? 'active-card' :'negative-card' }>
                            <div className='operator-card'>
                              <div className='operator-card-icon'><KnowledgeIcons.operator></KnowledgeIcons.operator></div>
                              <div className='operator-card-label'>{item.label}</div>
                            </div>
                          </Card>
                        </Checkbox>
                      </div>
                    ))}
                </Checkbox.Group>
            </Form.Item>

            </>)}

            
          </Form>

      </div>
    </>
  );
};

export { TextSplitClear }