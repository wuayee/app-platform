import { FormInstance, Progress } from 'antd';
import React, { useEffect, useState } from 'react';
import { SelectDataSource } from './select-data-source';
import TableConfig from './table-config';
import Preview from './preview';

interface inputProps {
  currentSteps: number;

  // 导入类型
  type: 'text' | 'table';

  // 数据源表单
  formDataSource: FormInstance;

  // 第二步的表单
  formStepSecond: FormInstance;
}

const SelectForm = ({ currentSteps, type, formDataSource, formStepSecond }: inputProps) => {
  return (
    <>
      {currentSteps === 0 && <SelectDataSource type={type} form={formDataSource} />}
      {currentSteps === 1 && <TableConfig />}
      {currentSteps === 2 && <Preview />}
      {currentSteps === 3 && <Progress percent={30} />}
    </>
  );
};

export { SelectForm };
