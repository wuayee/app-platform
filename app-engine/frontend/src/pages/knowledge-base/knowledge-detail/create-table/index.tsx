import React, { useEffect, useState } from 'react';
import { Form }from 'antd';
import { Button, Input, Radio, Select } from 'antd';
import type { TableProps } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useSearchParams } from "react-router-dom";
import BreadcrumbSelf from '../../../../components/breadcrumb';
import { KnowledgeIcons } from '../../../../components/icons';

type LayoutType = Parameters<typeof Form>[0]['layout'];


// 创建知识库配置
type FieldType = {
  // 知识表名称
  knowledgeBaseName: string;

  // 类型选择
  knowledgeBaseType: string;

  // 后端服务
  knowledgeBaseRemoteService: string;

  // 格式
  knowledgeBaseFormat: string;

  // 是否导入数据
  importData: boolean;
};


const KnowledgeBaseDetailCreateTable = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const id = searchParams.get("id");

  // 表格id判断提交还是修改
  const table_id = searchParams.get("tableid");

  const [form] = Form.useForm();

  const initialValues = {
    knowledgeBaseName: '',
    knowledgeBaseType: 'Vector',
    knowledgeBaseRemoteService: '',
    knowledgeBaseFormat: 'text',
    importData: true,
  };


  const [formLayout, setFormLayout] = useState<LayoutType>('vertical');


  // 是否在提交中
  const [loading, setLoading] = useState(false);

  // 团队列表
  const groupList =[{ value: 'Vector', label: 'Vector' }]

  const formItemLayout =
    formLayout === 'horizontal' ? { labelCol: { span: 8 }, wrapperCol: { span: 20 } } : null;

  const submit = ()=> {
    form.submit();
  }
  const onFinish = async (value: FieldType) => {
    // loading状态点击不触发，禁止多次触发提交
    if(loading) return;
    createKnowledgeTable(value)

    // await modifyData(value);
  };

  // 创建知识表
  const createKnowledgeTable = async (value: FieldType) => {
    setLoading(true)

    // 创建成功保存id，跳转至导入数据表单
    navigate(`/knowledge-base/knowledge-detail/import-data?id=${id}&tableid=${'1111'}&tabletype=${value.knowledgeBaseFormat}`, { replace: true })
    setLoading(false)
  }

  const onCancle = ()=> {
    navigate(-1)
  }

  useEffect(()=> {
    if(table_id) {
    }
  }, []);




  return (
    <>
    <div className='aui-fullpage'>
    <div className='aui-header-1'>
      <div className='aui-title-1'>
        <BreadcrumbSelf currentLabel={table_id ? '修改知识表': '添加知识表'}></BreadcrumbSelf>
      </div>
    </div>
      <div className='aui-block' style={{
        display: 'flex',
        flexDirection: 'column',
        gap: 8
      }}>
                <div style={{
          width: '100%',
          flex:1,
          background: '#fff',
          borderRadius: '8px 8px 0px 0px',
          padding: '24px 24px 0 25px',
        }}>
           <Form<FieldType>
            {...formItemLayout}
            layout={formLayout}
            form={form}
            initialValues={initialValues}
            onFinish={onFinish}
            style={{ maxWidth: formLayout === 'inline' ? 'none' : 800 }}
          >
            <Form.Item label="知识表名称"  name = 'knowledgeBaseName' rules={[{ required: true, message: '输入不能为空' }]}>
              <Input placeholder='请输入'/>
            </Form.Item>
            
            <Form.Item label="类型选择" name = 'knowledgeBaseType'>
              <Select options={groupList} allowClear/>
            </Form.Item>
            
            <Form.Item label="后端服务"  name = 'knowledgeBaseRemoteService'>
              <Input placeholder='请输入'/>
            </Form.Item>
            <Form.Item label="格式" name = 'knowledgeBaseFormat'>
              <Radio.Group size="large" style={{
                  display: 'flex',
                  justifyContent: 'space-between'
                }}>
                  <Radio.Button style={{
                    width: 391,
                    height: 40,
                    borderRadius: 4,
                    display: 'flex',
                    alignItems: 'center'
                  }} value="text"> 
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: 4,
                      }}>
                        {<KnowledgeIcons.text></KnowledgeIcons.text>} 文本
                    </div>
                  </Radio.Button>
                  
                  <Radio.Button value="table" style={{
                    width: 391,
                    height: 40,
                    borderRadius: 4,
  
                  }}>
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: 4,
                      }}>
                      {<KnowledgeIcons.table></KnowledgeIcons.table>} 表格
                    </div>
                  </Radio.Button>
              </Radio.Group>
            </Form.Item>
            <Form.Item label="是否导入数据" name = 'importData' style={{
              width: 150,
            }}>
              <Radio.Group size="large" style={{
                  display: 'flex',
                  justifyContent: 'space-between'
                }}>
                  <Radio  value={true}>是</Radio>
                  <Radio  value={false}> 否</Radio>
              </Radio.Group>
            </Form.Item>

          </Form>
        </div>
        <div style={{
          display: 'flex',
          justifyContent: 'end',
          gap: 16,
        }}>
          <Button onClick={onCancle} style={{
            borderRadius: 4,
          }}>取消</Button>
          <Button type="primary" loading={loading} onClick={submit} style={{
            borderRadius: 4,
          }}>确定</Button>
        </div>
      <div />
    </div>
    </div>

    </>
  )
}
export default KnowledgeBaseDetailCreateTable;