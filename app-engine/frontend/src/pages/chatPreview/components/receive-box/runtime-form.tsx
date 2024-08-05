
import React from 'react';
import InterviewQuestions from '../runtimeForm/InterviewQuestions.jsx';
import ManageCubeCreateReport from '../runtimeForm/ManageCubeCreateReport.jsx';
import FileContent from '../runtimeForm/FileContent';
import QuestionClar from "../runtimeForm/QuestionClar";
import ConditionForm from '../runtimeForm/conditionForm/conditionForm';
import { setFormReceived } from "@/store/chatStore/chatStore";
import { useAppDispatch, useAppSelector } from "@/store/hook";

// runtime表单渲染
const RuntimeForm = (props) => {
  const { formName, formMap } = props.formConfig;
  const dispatch = useAppDispatch();
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const questions = []
  const saveCallBack = () => {
    dispatch(setFormReceived(true));
  }
  function setFormDom(type) {
    switch (type) {
      case 'file':
        return <FileContent data={formMap.value}/>
        break;
      case 'interview':
        return <InterviewQuestions questions={questions}/>
        break;
      case 'manageCubeCreateReport':
        return  <ManageCubeCreateReport 
                  data={props.formConfig} 
                  mode={props.formConfig.type}
                  tenantId={tenantId} 
                  saveCallBack={saveCallBack}
                />
        break;
      case 'QuestionClar':
        return <QuestionClar data={props.formConfig} mode={props.formConfig.type}/>
        break;
      case 'conditionForm':
        return  <ConditionForm 
                  data={props.formConfig}
                  chatRunning={chatRunning}
                />
        break;
      default:
        return <div>未找到对应的表单类型</div>
    }
  }
  return <>{(
    <div className='receive-form-item'>
      { setFormDom(formName) }
    </div>
  )}</>
}


export default RuntimeForm;
