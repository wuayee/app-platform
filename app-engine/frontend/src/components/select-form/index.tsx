import React from 'react';
import { Button, Empty, FormInstance, Progress } from 'antd';
import SelectDataSource from './select-data-source';
import { TextSplitClear } from './text-split-clear';
import TableSecondForm from './table-second-form';
import { useHistory } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

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
  const navigate = useHistory().push;
  const { t } = useTranslation();
  return (
    <>
      {currentSteps === 0 && <SelectDataSource type={type} form={formDataSource} />}
      {currentSteps === 1 &&
        (type === 'text' ? <TextSplitClear form={formStepSecond} /> : <TableSecondForm form={formStepSecond} />)}
      {currentSteps === 2 &&
        (true ? (
          <Empty
            imageStyle={{ height: 60 }}
            description={<span>{t('knowledgeCreated')}</span>}
          >
            <Button type='primary' onClick={() => window.history.back()}>
              {t('clickReturn')}
            </Button>
          </Empty>
        ) : (
            <Progress percent={30} />
          ))}
    </>
  );
};

export { SelectForm };
