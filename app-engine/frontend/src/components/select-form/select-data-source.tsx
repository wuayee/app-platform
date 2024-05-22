import { FormInstance , Button, Input, Radio, Select, Form } from 'antd';
import React, { useEffect, useState } from 'react';
import { KnowledgeIcons } from '../icons';
import UploadFile from './upload';

interface props {
  type: 'text' | 'table';
  form: FormInstance;

  onFinish?: (value: FieldType) => any;
}

// 选择数据源表单配置
type FieldType = {
  // 选择文档类型
  datasourceType: 'local' | 'nas' | 'custom';

  // 上传文本文件 local类型
  selectedFile?: FileList;

  // NAS 类型
  nasUrl?: string;

  // nas文件路径
  nasFileUrl?: string;

  // 文本自定义内容 custom
  textCustom?: string;

  // 表格自定义内容 custom
  tableCustom?: any[]
};

const SelectDataSource = ({type, form}: props)=> {
  const initialValues: FieldType = {
    datasourceType: 'local'
  };

  // 监听类型变化
  const datasourceType = Form.useWatch('datasourceType', form);

  const submit = ()=> {
    form.submit();
  }
  const onFinish = async (value: FieldType) => {
    // loading状态点击不触发，禁止多次触发提交

  };
  return (
    <>
      <div>
      <Form<FieldType>
            layout={'vertical'}
            form={form}
            initialValues={initialValues}
            onFinish={onFinish}
            style={{ maxWidth: 800 }}
          >
            <Form.Item  name = 'datasourceType' style={{
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
                  }} value="local"> 
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: 4,
                      }}>
                        {<KnowledgeIcons.local></KnowledgeIcons.local>} 本地文档
                    </div>
                  </Radio.Button>
                  {type === 'text' && (<Radio.Button value="nas" style={{
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
                      {<KnowledgeIcons.nas></KnowledgeIcons.nas>} NAS文档
                    </div>
                  </Radio.Button>) }
                  
                  <Radio.Button value="custom" style={{
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
                      {<KnowledgeIcons.custom></KnowledgeIcons.custom>} 自定义
                    </div>
                  </Radio.Button>
              </Radio.Group>
            </Form.Item>
            
            {datasourceType === 'local' && <Form.Item label="上传文本文件" name = 'selectedFile' style={{
              marginTop: 16
            }}>
              {/* 需要对齐 */}
              <UploadFile/>
            </Form.Item>}

            {datasourceType === 'nas' && 
              <>
                <Form.Item label="NAS地址" rules={[{ required: true, message: '输入不能为空' }]} name = 'nasUrl' style={{
                    marginTop: 16
                  }}>
                    <Input placeholder='请输入NAS地址'/>
                  </Form.Item>
                
                  <Form.Item label="文本路径" rules={[{ required: true, message: '输入不能为空' }]} name = 'nasFileUrl' style={{
                    marginTop: 16
                  }}>
                    <Input placeholder='请输入文本路径'/>
                </Form.Item>
              </>
            }

            {datasourceType === 'custom' && type === 'text' && 
              <>
                <Form.Item label="添加内容" rules={[{ required: true, message: '输入不能为空' }]} name = 'textCustom' style={{
                    marginTop: 16
                  }}>
                    <Input placeholder='请输入要添加的内容'/>
                  </Form.Item>
              </>
            }

            {datasourceType === 'custom' && type === 'table' && 
              <>
                {/* 表格还未做 */}
              </>
            }
          

          </Form>

      </div>
    </>
  );
};

export { SelectDataSource }