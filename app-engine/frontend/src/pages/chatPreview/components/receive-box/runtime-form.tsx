
import React, { useContext } from 'react';
import InterviewQuestions from '../runtimeForm/InterviewQuestions';
import ManageCubeCreateReport from '../runtimeForm/ManageCubeCreateReport';
import FileContent from '../runtimeForm/FileContent';
import QuestionClar from '../runtimeForm/QuestionClar';
import ConditionForm from '../runtimeForm/conditionForm/conditionForm';
import { setFormReceived } from '@/store/chatStore/chatStore';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { ChatContext } from '@/pages/aippIndex/context';
import { useTranslation } from 'react-i18next';

// runtime表单渲染
const RuntimeForm = (props) => {
  const { t } = useTranslation();
  const { formName, formMap } = props.formConfig;
  const { chatStreaming } = useContext(ChatContext);
  const dispatch = useAppDispatch();
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const dataDimension = useAppSelector((state) => state.commonStore.dimension);
  const questions = []
  const saveCallBack = (response) => {
    dispatch(setFormReceived(true));
    chatStreaming(response);
  }
  function setFormDom(type) {
    switch (type) {
      case 'file':
        return <FileContent data={formMap.value} />
        break;
      case 'interview':
        return <InterviewQuestions questions={questions} />
        break;
      case 'manageCubeCreateReport':
        return <ManageCubeCreateReport
          data={props.formConfig}
          mode={props.formConfig.type}
          confirmCallBack={props.confirmCallBack}
          tenantId={tenantId}
          saveCallBack={saveCallBack}
        />
        break;
      case 'questionClar':
        return <QuestionClar
          dataDimension={dataDimension}
          data={props.formConfig} mode={props.formConfig.type}
          tenantId={tenantId}
          confirmCallBack={props.confirmCallBack}
        />
        break;
      case 'conditionForm':
        return <ConditionForm
          data={props.formConfig}
          chatRunning={chatRunning}
          tenantId={tenantId}
          confirmCallBack={props.confirmCallBack}
        />
        break;
      default:
        return <div>{t('formTypeFail')}</div>
    }
  }
  return <>{(
    <div className='receive-form-item'>
      { setFormDom(formName)}
    </div>
  )}</>
}


export default RuntimeForm;
