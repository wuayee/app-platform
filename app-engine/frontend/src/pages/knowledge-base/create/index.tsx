import React, { useEffect, useState } from 'react';
import { Form } from 'antd';
import { Button, Input, Radio, Select } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useSearchParams } from "react-router-dom";

import BreadcrumbSelf from '../../../components/breadcrumb';
import { Icons } from '../../../components/icons';
import { createKnowledgeBase, getKnowledgeBaseById, modifyKnowledgeBase } from '../../../shared/http/knowledge';

type LayoutType = Parameters<typeof Form>[0]['layout'];


// 创建知识库配置
type FieldType = {
  knowledgeType: 'user' | 'userGroup';
  knowledgeName: string;
  knowledgeDesc: string;
  knowledgeGroup?: string;
};

const KnowledgeBaseCreate = () => {

  const [searchParams] = useSearchParams();
  const id = searchParams.get("id");

  const [form] = Form.useForm();

  const initialValues = {
    knowledgeType: 'user',
    knowledgeName: '',
    knowledgeDesc: '',
  };
  const [formLayout, setFormLayout] = useState<LayoutType>('vertical');

  // 监听类型变化
  const knowledgeType = Form.useWatch('knowledgeType', form);

  // 是否在提交中
  const [loading, setLoading] = useState(false);

  // 团队列表
  const groupList = [{ value: 'storage', label: '存储团队' }]

  const formItemLayout =
    formLayout === 'horizontal' ? { labelCol: { span: 4 }, wrapperCol: { span: 14 } } : null;

  const submit = () => {
    form.submit();
  }
  const onFinish = async (value: FieldType) => {
    // loading状态点击不触发，禁止多次触发提交
    if (loading) return;
    await modifyData(value);
  }

  // 知识库创建修改
  const modifyData = async (value: FieldType) => {
    setLoading(true)
    try {
      if (id) {
        await modifyKnowledgeBase({ name: value.knowledgeName, description: value.knowledgeDesc, id })
      } else {
        await createKnowledgeBase({ name: value.knowledgeName, description: value.knowledgeDesc, owner: '1' })
      }
      navigate('/knowledge-base')

    } catch (error) {
      setLoading(false)
    }
    setLoading(false)
  }


  // 获取知识库详情
  const getKnowledgeBase = async (id: string) => {
    try {
      let res = await getKnowledgeBaseById(id);
      form.setFieldsValue({
        knowledgeType: 'user',
        knowledgeName: res?.name,
        knowledgeDesc: res?.description,
      })
    } catch (error) {

    }
  }

  // 创建知识库
  const navigate = useNavigate()

  // 取消提交
  const onCancle = () => {
    navigate('/knowledge-base')
  }
  // 字符限制长度不能超过255
  const changeName = () => {
    const name = form.getFieldValue('knowledgeName');
    if (name.length) {
      let n = 0;
      for (let i = 0; i < name.length; i++) {
        let code = name.charCodeAt(i);
        if (code > 255) {
          n +=2;
        } else {
          n += 1
        }
      }
      if (n > 255) {
        return Promise.reject('字符串长度不能大于255');
      } else {
        return Promise.resolve();
      }
    } else {
      return Promise.reject('');
    }
  }
  useEffect(() => {
    if (id) {
      getKnowledgeBase(id);
    }
  }, [])

  return (
    <>
      <div className='aui-fullpage' style={{
        height: 'calc(100vh - 100px)'
      }}>
        <div className='aui-header-1'>
          <div className='aui-title-1'>
            <BreadcrumbSelf currentLabel={id ? '修改知识库' : '创建知识库'}></BreadcrumbSelf>
          </div>
        </div>
        <div className='aui-block' style={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'space-between'
        }}>
          <div style={{
            width: '100%',
            flex: 1,
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
              <Form.Item label="个人/团队" name='knowledgeType'>
                <Radio.Group size="large" style={{
                  display: 'flex',
                  justifyContent: 'space-between'
                }}>
                  <Radio.Button style={{
                    width: 391,
                    height: 40,
                    borderRadius: 4,
                  }} value="user"> {<Icons.user></Icons.user>}  个人</Radio.Button>
                  <Radio.Button className='userGroup' value="userGroup" style={{
                    width: 391,
                    height: 40,
                    borderRadius: 4,
                  }}
                    // 禁用团队选择
                    disabled={true}
                  >{<Icons.userGroup></Icons.userGroup>}  团队</Radio.Button>
                </Radio.Group>
              </Form.Item>
              {knowledgeType === 'userGroup' ? (
                <>
                  <Form.Item label="知识库团队" rules={[{ required: true, message: '选择不能为空' }]} name='knowledgeGroup'>
                    <Select options={groupList} allowClear />
                  </Form.Item>
                </>
              ) : ''}

              <Form.Item
                label="知识库名称"
                rules={[
                  { required: true },
                  { validator: changeName}
                ]}
                name='knowledgeName'
              >
                <Input placeholder='请输入' />
              </Form.Item>
              <Form.Item label="知识库描述" rules={[{ required: true, message: '输入不能为空' }]} name='knowledgeDesc'>
                <Input.TextArea size='large' autoSize={{ minRows: 2, maxRows: 6 }} placeholder='请输入' />
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
export default KnowledgeBaseCreate;
