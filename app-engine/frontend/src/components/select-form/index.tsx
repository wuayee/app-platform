import { Button, Empty, FormInstance, Progress } from 'antd';
import React from 'react';
import SelectDataSource from './select-data-source';
import Preview from './preview';
import { TextSplitClear } from './text-split-clear';
import TableSecondForm from './table-second-form';
import { useNavigate } from 'react-router-dom';

interface inputProps {
  currentSteps: number;

  // 导入类型
  type: 'text' | 'table';

  // 数据源表单
  formDataSource: FormInstance;

  // 第二步的表单
  formStepSecond: FormInstance;

  segmentData: {
    title: string;
    content: string;
    chars: string;
  }[];
}

const SelectForm = ({ currentSteps, type, formDataSource, formStepSecond }: inputProps) => {
  const navigate = useNavigate();
  return (
    <>
      {currentSteps === 0 && <SelectDataSource type={type} form={formDataSource} />}
      {currentSteps === 1 &&
        (type === 'text' ? <TextSplitClear form={formStepSecond} /> : <TableSecondForm form={formStepSecond}/>)}
      {currentSteps === 2 &&
        ( true ? (
          <Empty
            image='https://gw.alipayobjects.com/zos/antfincdn/ZHrcdLPrvN/empty.svg'
            imageStyle={{ height: 60 }}
            description={<span>恭喜您，知识库已完成创建</span>}
          >
            <Button type='primary' onClick={() => navigate(-1)}>
              点击返回
            </Button>
          </Empty>
        ) : (
          <Progress percent={30} />
        ))}
    </>
  );
};

export { SelectForm };
