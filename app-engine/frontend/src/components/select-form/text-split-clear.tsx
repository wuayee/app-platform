import { FormInstance , Button, Input, Radio, Select, Form, Checkbox } from 'antd';
import React, { useEffect, useState } from 'react';
import { KnowledgeIcons } from '../icons';
import UploadFile from './upload';


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
};

const SelectDataSource = ({ form }: props)=> {
  const initialValues: FieldType = {
    clearType: 'paragraphed'
  };


  const options = [
    { label: 'Apple', value: 'Apple' },
    { label: 'Pear', value: 'Pear' },
    { label: 'Orange', value: 'Orange' },
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
            <Form.Item  name = 'clearType' style={{
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

            <Form.Item  name = 'datasourceType' style={{
              marginTop: 16
            }}>
                <Checkbox.Group >
                    {options.map((item)=> (
                      <>
                        <Checkbox value={item.value}>{item.label}</Checkbox>
                      </>
                    ))}
                </Checkbox.Group>
            </Form.Item>
          </Form>

      </div>
    </>
  );
};

export { SelectDataSource }