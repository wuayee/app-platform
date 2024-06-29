
import React, { useEffect, useState } from 'react';
import InterviewQuestions from '../runtimeForm/InterviewQuestions.jsx';
import ManageCubeCreateReport from '../runtimeForm/ManageCubeCreateReport.jsx';
import FileContent from '../runtimeForm/FileContent.jsx';

// runtime表单渲染
const RuntimeForm = (props) => {
  const { formName, formMap, instanceId } = props.formConfig;
  const questions = [
    {
      question: '分享一下你最近在车联网或者深度学习领域有哪些具有突破性的科研成果。',
    },
    {
      question: '在你的研究生涯中,有没有哪位导师或者家人给予了重大影响?请具体阐述。',
    },
    {
      question: '如 Leonard Cimini Jr.教授,对你的科研方法或思维方式产生了重大影响?',
    },
  ]
  function setFormDom(type) {
    switch (type) {
      case 'file':
        return <FileContent data={formMap.value}/>
        break;
      case 'interview':
        return <InterviewQuestions questions={questions}/>
        break;
      case 'manageCubeCreateReport':
        return <ManageCubeCreateReport data={props.formConfig} mode={props.formConfig.type} />
        break;
      default:
        return <div>未找到对应的表单类型</div>
    }
  }
  return <>{(
    <div className="recieve-form-item">
      { setFormDom(formName) }
    </div>
  )}</>
}


export default RuntimeForm;
