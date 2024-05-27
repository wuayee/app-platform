import React, { useEffect, useRef, useState } from 'react';
import { Form } from 'antd';
import { Button, Steps } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useSearchParams } from 'react-router-dom';
import BreadcrumbSelf from '../../../../components/breadcrumb';
import { SelectForm } from '../../../../components/select-form';
import SegmentPreview from '../../../../components/select-form/segment-preview';
import './style.scoped.scss';
import {
  deleteLocalFile,
  textSegmentWash,
  getTableColums,
  createTableColumns,
} from '../../../../shared/http/knowledge';

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

const segmentData = new Array(4).fill(0).map((_, i) => ({
  title: `#00${i + 1}`,
  content:
    '0.06049343,0.037972502,0.02068533,0.0036318663,0.0040750.06687770.06049343,0.037972502,0.02068533,0.0036318663,0.0040750.0668777037972502,0.02068533,0.0036318663,0.0040750.06687770.06049343,0.037972502,0.02068533,0.0036318663,0.0040750.0668777',
  chars: '829',
}));

const KnowledgeBaseDetailImportData = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [currentSteps, setCurrentSteps] = useState(0);

  // 知识表id
  const id = searchParams.get('id') || 0;

  // 知识表id
  const table_id = searchParams.get('tableid') || 0;

  // 知识表格式 表格或者是文本，先做文本
  const table_type = searchParams.get('tabletype') as any as 'text' | 'table';

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
  ];

  if (table_type === 'text') {
    steps.push({
      title: '文本分段与清洗',
    });
    steps.push({
      title: '完成',
    });
  } else {
    steps.push({
      title: '表格配置',
    });
    steps.push({
      title: '开始导入',
    });
  }

  const submit = () => {
    // form.submit();
  };
  const onFinish = async (value: FieldType) => {
    // loading状态点击不触发，禁止多次触发提交
    if (loading) return;
    createKnowledgeTable();

    // await modifyData(value);
  };

  // 创建知识表
  const createKnowledgeTable = async () => {};

  const onCancle = async () => {
    navigate(-1);
    const fileIds = formDataSource.getFieldValue('selectedFile').map((file) => `${file.uid}_${file.name}`);
    await deleteLocalFile(id, table_id, fileIds);
    formDataSource.setFieldValue('selectedFile', []);
  };

  // 上一步
  const prevStep = () => {
    setLoading(true);
    setCurrentSteps(currentSteps - 1);
    setLoading(false);
  };

  const formValue = useRef({
    dataSource: {},
    second: {},
  });

  // 下一步
  const nextStep = async () => {
    setLoading(true);
    try {
      if (currentSteps === 0) {
        const res = await formDataSource.validateFields();
        if (!res.selectedFile) {
          return;
        }
        formValue.current.dataSource = { ...res };

        if(table_type === 'table') {
          // 获取表格列
          const result = await getTableColums({
            repositoryId: id as string,
            knowledgeTableId: table_id as string,
            fileName: formValue.current.dataSource?.selectedFile?.map((file) => `${file.uid}_${file.name}`)?.[0] || ''
          });

          if(result && result?.length) {
            formStepSecond.setFieldValue('tableCustom', result.map((item, index)=> ({
              description: item.desc,
              vectorService: item.embedServiceId,
              dataType: item.dataType,
              colName: item.name,
              indexType: item.indexType,
              key: `${index + 1}`
            })));
          }
        }
        setCurrentSteps(currentSteps + 1);
      }

      if (currentSteps === 1) {
        const res = await formStepSecond.validateFields();
        formValue.current.second = { ...res };
        if (table_type === 'text') {
          // 文本分段清洗
          const fileNames = formValue.current.dataSource?.selectedFile?.map((file) => `${file.uid}_${file.name}`);
          const secondRes = formValue.current.second;
          await textSegmentWash({
            knowledgeId: id,
            tableId: table_id,
            fileNames,
            ...secondRes,
          });
        }

        // 表格创建逻辑
        if(table_type === 'table') {
          const fileName = formValue.current.dataSource?.selectedFile?.map((file) => `${file.uid}_${file.name}`)?.[0] || '';

          const data = (res?.tableCustom || []).map(item => ({
            name: item.colName,
            dataType: item.dataType,
            indexType: item.indexType,
            embedServiceId: item.vectorService ?? null,
            desc: item.description ?? null,
          }));
          createTableColumns({
            repositoryId: id as string,
            knowledgeTableId: table_id as string,
            fileName,
            columns:data
          })
        }

        setCurrentSteps(currentSteps + 1);
      }
    } catch (error) {}
    setLoading(false);
  };

  useEffect(() => {
  }, []);

  const handleSubmit = async () => {
    navigate(-1);
  };

  return (
    <>
      <div className='aui-fullpage'>
        <div className='aui-header-1'>
          <div className='aui-title-1'>
            <BreadcrumbSelf
              currentLabel={table_id ? '导入数据' : '导入数据'}
              searchFlag={true}
            ></BreadcrumbSelf>
          </div>
        </div>
        <div className='import-data-wrapper'>
          <div
            className='aui-block'
            style={{
              display: 'flex',
              flexDirection: 'column',
              gap: 8,
            }}
          >
            <div
              style={{
                width: '100%',
                flex: 1,
                background: '#fff',
                borderRadius: '8px 8px 0px 0px',
                padding: '24px 24px 0 25px',
              }}
            >
              <Steps current={currentSteps} items={steps} />
              <SelectForm
                currentSteps={currentSteps}
                type={table_type}
                formDataSource={formDataSource}
                formStepSecond={formStepSecond}
                segmentData={segmentData}
              />
            </div>
            <div
              style={{
                display: 'flex',
                justifyContent: 'end',
                gap: 16,
              }}
            >
              {currentSteps === 0 && (
                <Button
                  onClick={onCancle}
                  style={{
                    borderRadius: 4,
                  }}
                >
                  取消
                </Button>
              )}

              {currentSteps > 0 && (
                <Button
                  onClick={prevStep}
                  style={{
                    borderRadius: 4,
                  }}
                >
                  上一步
                </Button>
              )}

              {currentSteps < steps.length - 1 && (
                <Button
                  onClick={nextStep}
                  loading={loading}
                  type='primary'
                  style={{
                    borderRadius: 4,
                  }}
                >
                  下一步
                </Button>
              )}

              {currentSteps === steps.length - 1 && (
                <Button
                  onClick={handleSubmit}
                  type='primary'
                  style={{
                    borderRadius: 4,
                  }}
                >
                  确定
                </Button>
              )}
            </div>
          </div>
          {currentSteps === 1 && table_type === 'text' && <SegmentPreview data={segmentData} />}
        </div>
      </div>
    </>
  );
};
export default KnowledgeBaseDetailImportData;
