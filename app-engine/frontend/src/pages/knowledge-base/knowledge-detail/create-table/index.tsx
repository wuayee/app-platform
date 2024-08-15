import React, { useEffect, useState } from 'react';
import { Form } from 'antd';
import { Button, Input, Radio, Select } from 'antd';
import type { TableProps } from 'antd';
import { useHistory, useLocation } from 'react-router-dom';
import qs from 'qs';
import BreadcrumbSelf from '../../../../components/breadcrumb';
import { KnowledgeIcons } from '../../../../components/icons';
import { createKnowledgeTableRow, getKnowledgeTableType } from '../../../../shared/http/knowledge';

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
  const searchParams = qs.parse(useLocation().search.replace('?', ''));
  const id = searchParams.id;
  const navigate = useHistory().push;
  const [selectValue, setSelectValue] = useState();

  // 表格id判断提交还是修改
  const table_id = searchParams.tableid;

  const [form] = Form.useForm();

  const initialValues = {
    knowledgeBaseName: '',
    knowledgeBaseType: '',
    knowledgeBaseRemoteService: '',
    knowledgeBaseFormat: 'text',
    importData: true,
  };


  const [formLayout, setFormLayout] = useState<LayoutType>('vertical');


  // 是否在提交中
  const [loading, setLoading] = useState(false);

  // 类型列表
  const [groupList, setGroupList] = useState<any[]>([]);

  // 后端服务列表
  const [serviceList, setServiceList] = useState<any[]>([]);
  let [seviceMap, setServiceMap] = useState<any>({});


  // 监听类型变化
  const knowledgeBaseTypeChange = Form.useWatch('knowledgeBaseType', form);

  const formItemLayout =
    formLayout === 'horizontal' ? { labelCol: { span: 8 }, wrapperCol: { span: 20 } } : null;

  const submit = () => {
    form.submit();
  }
  const onFinish = async (value: FieldType) => {
    // loading状态点击不触发，禁止多次触发提交
    if (loading) return;
    createKnowledgeTable(value)
  };

  // 获取类型
  const getTableType = async () => {
    try {
      const typeList = await getKnowledgeTableType();
      if (typeList && typeList.length) {
        const data = typeList.reduce((prev: any, next: any) => {
          if (prev[next.type]) {
            prev[next.type].push(next);
          } else {
            prev[next.type] = [next];
          };
          return prev;
        }, {});

        setServiceMap({ ...data })

        const typeOptionList: any[] = Object.keys(data).map(item => ({
          value: item,
          label: item,
        }));

        setGroupList([...typeOptionList]);
      }
    } catch (error) {

    }
  }

  const textMap: any = {
    text: 'TEXT',
    table: 'TABLE'
  }

  // 创建知识表
  const createKnowledgeTable = async (value: FieldType) => {
    setLoading(true);
    try {
      const res = await createKnowledgeTableRow(id, {
        name: value.knowledgeBaseName,
        serviceType: value.knowledgeBaseType,
        serviceId: value.knowledgeBaseRemoteService,
        format: textMap[value.knowledgeBaseFormat as any] as any,
        repositoryId: id,
      });
      if (value.importData && res) {
        // 创建成功保存id，跳转至导入数据表单
        navigate(`/knowledge-base/knowledge-detail/import-data?id=${id}&tableid=${res}&tabletype=${value.knowledgeBaseFormat}`, { replace: true });
        return;
      };
      if (res) {
        window.history.back();
      };
    } finally {
      setLoading(false);
    }
  }

  const onCancle = () => {
    window.history.back();
  }

  useEffect(() => {
    if (table_id) {
    }
    getTableType();
  }, []);

  const changeName = () => {
    const name = form.getFieldValue('knowledgeBaseName');
    if (name.length) {
      let n = 0;
      for (let i = 0; i < name.length; i++) {
        let code = name.charCodeAt(i);
        if (code > 255) {
          n += 2;
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
  const selectChange = (e) => {
    e === 'RDB' && form.setFieldValue('knowledgeBaseFormat', 'table');
    e === 'VECTOR' && form.setFieldValue('knowledgeBaseFormat', 'text');
    setSelectValue(e);
  }

  useEffect(() => {
    form.setFieldValue('knowledgeBaseRemoteService', '')
    if (seviceMap[knowledgeBaseTypeChange]) {
      setServiceList([...seviceMap[knowledgeBaseTypeChange].map((item: any) => {
        return ({
          value: item.id,
          label: item.url
        })
      })])
    } else {
      setServiceList([])
    }
  }, [knowledgeBaseTypeChange]);




  return (
    <>
      <div className='aui-fullpage'>
        <div className='aui-header-1'>
          <div className='aui-title-1'>
            <BreadcrumbSelf currentLabel={table_id ? '修改知识表' : '添加知识表'} searchFlag={true}></BreadcrumbSelf>
          </div>
        </div>
        <div className='aui-block' style={{
          display: 'flex',
          flexDirection: 'column',
          gap: 8
        }}>
          <div style={{
            width: '100%',
            flex: 1,
            background: '#fff',
          }}>
            <Form<FieldType>
              {...formItemLayout}
              layout={formLayout}
              form={form}
              initialValues={initialValues}
              onFinish={onFinish}
              style={{ maxWidth: formLayout === 'inline' ? 'none' : 800 }}
            >
              <Form.Item
                label="知识表名称"
                name='knowledgeBaseName'
                rules={[
                  { required: true, message: '输入不能为空' },
                  { validator: changeName }
                ]}
              >
                <Input placeholder='请输入' />
              </Form.Item>

              <Form.Item label="类型选择" name='knowledgeBaseType' rules={[{ required: true, message: '输入不能为空' }]} style={{
                marginTop: 16
              }}>
                <Select options={groupList} allowClear onChange={selectChange} />
              </Form.Item>

              <Form.Item label="后端服务" name='knowledgeBaseRemoteService' rules={[{ required: true, message: '输入不能为空' }]} style={{
                marginTop: 16
              }}>
                <Select options={serviceList} allowClear />
              </Form.Item>
              <Form.Item label="格式" name='knowledgeBaseFormat' style={{
                marginTop: 16
              }}>
                <Radio.Group size="large" style={{
                  display: 'flex',
                  justifyContent: 'space-between'
                }}>
                  <Radio.Button disabled={!selectValue || selectValue === 'RDB'} style={{
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

                  <Radio.Button disabled={!selectValue || selectValue === 'VECTOR'} value="table" style={{
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
              <Form.Item label="是否导入数据" name='importData' style={{
                width: 150,
                marginTop: 16,
              }}>
                <Radio.Group size="large" style={{
                  display: 'flex',
                  justifyContent: 'space-between'
                }}>
                  <Radio value={true}>是</Radio>
                  <Radio value={false}> 否</Radio>
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
