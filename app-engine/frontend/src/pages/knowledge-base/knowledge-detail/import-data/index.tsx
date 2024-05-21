import React, { useEffect, useState } from 'react';
import { Form }from 'antd';
import { Button, Input, Radio, Select, Steps } from 'antd';
import type { TableProps } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useSearchParams } from "react-router-dom";
import BreadcrumbSelf from '../../../../components/breadcrumb';
import { KnowledgeIcons } from '../../../../components/icons';
import { SelectForm } from '../../../../components/select-form';

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



const KnowledgeBaseDetailImportData = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [currentSteps, setCurrentSteps] = useState(0);

  // 知识表id
  const id = searchParams.get("id");

  // 知识表id
  const table_id = searchParams.get("tableid");

  // 知识表格式 表格或者是文本，先做文本
  const table_type = searchParams.get("tabletype") as any as 'text' | 'table';

  // 是否在提交中
  const [loading, setLoading] = useState(false);

  // 选择数据源表单
  const [formDataSource] = Form.useForm();

  // 第二步的表单
  const [formStepSecond] = Form.useForm();

  const steps: any[] = [
    {
      title: '选择数据源',
    },
  ]

  if(table_type === 'text') {
    steps.push({
      title: '文本分段与清洗'
    });
    steps.push({
      title: '完成'
    })
  } else {
    steps.push({
      title: '表格配置'
    });
    steps.push({
      title: '预览'
    });
    steps.push({
      title: '创建'
    })
  }

  const submit = ()=> {
    // form.submit();
  }
  const onFinish = async (value: FieldType) => {
    // loading状态点击不触发，禁止多次触发提交
    if(loading) return;
    createKnowledgeTable()

    // await modifyData(value);
  };

  // 创建知识表
  const createKnowledgeTable = async () => {

  }

  const onCancle = ()=> {
    navigate(-1)
  }

  // 上一步
  const prevStep = () => {
    setLoading(true);
    setCurrentSteps(currentSteps - 1);
    setLoading(false);
  }

  // 下一步
  const nextStep = async () => {
    setLoading(true)
    try {
      if (currentSteps === 0) {
        await formDataSource.validateFields();
        // 校验成功
        setCurrentSteps(currentSteps + 1)
      }
    } catch (error) {
    }
    setLoading(false)

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
        <BreadcrumbSelf currentLabel={table_id ? '导入数据': '导入数据'} searchFlag={true}></BreadcrumbSelf>
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

          <Steps
            current={currentSteps}
            items={steps}
          />
          <SelectForm currentSteps={currentSteps} type={table_type} formDataSource={formDataSource} formStepSecond={formStepSecond}/>
        
      </div >
      <div style={{
          display: 'flex',
          justifyContent: 'end',
          gap: 16,
        }}>
          {currentSteps === 0 && 
            <Button onClick={onCancle} style={{
              borderRadius: 4,
            }}>取消</Button>
          }

          {currentSteps > 0 && 
            <Button onClick={prevStep} style={{
              borderRadius: 4,
            }}>上一步</Button>
          }

          {currentSteps < steps.length - 1 && 
            <Button onClick={nextStep} loading={loading} type="primary" style={{
              borderRadius: 4,
            }}>下一步</Button>
          }

          {currentSteps === steps.length - 1 && 
            <Button onClick={onCancle} type="primary" style={{
              borderRadius: 4,
            }}>确定</Button>
          } 
        </div>
    </div>
      </div>
    </>
  );
}
export default KnowledgeBaseDetailImportData;