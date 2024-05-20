
import React, { useEffect, useState, useContext } from 'react';
import Markdown from 'react-markdown';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism'
import { AippContext } from '../../aippIndex/context';
import ChartMessage from './chart-message.jsx';
import FileContent from '../components/runtimeForm/FileContent.jsx';
import InterviewQuestions from '../components/runtimeForm/InterviewQuestions.jsx';
import ManageCubeCreateReport from '../components/runtimeForm/ManageCubeCreateReport.jsx';
import robot from '../../../assets/images/ai/robot1.png';
import '../styles/recieve-box.scss';

const ReciveBox = (props) => {
  const { content, recieveType, formConfig, loading, markdownSyntax, chartConfig } = props.chatItem;

  // 设置显示类型
  function setRecieveDom(type) {
    if (type === 'form') {
      return <RuntimeForm  formConfig={formConfig}/>
    }
    return <MessageBox content={content} markdownSyntax={markdownSyntax} chartConfig={chartConfig}/> 
  }
  return <>{(
    <div className='recieve-box'>
      <div className='user-image'>
        <Img />
      </div>
      { loading ? <Loading /> : setRecieveDom(recieveType) }
    </div>
  )}</>
}
// 接收消息loading
const Loading = () => {
  return(
   <>
    <div className='recieve-info recieve-loading'>
      <span>回答生成中</span>
      <span className="loading-span"></span>
    </div> 
   </>
  )
}
const Img = () => {
  const { aippInfo }  = useContext(AippContext);
  return <>{(
    <span>
      { aippInfo.attributes?.icon ? <img src={aippInfo.attributes.icon}/> : <img src={robot}/> }
    </span>
  )}</>
}
// 消息详情
const MessageBox = (props) => {
  const { content, markdownSyntax, chartConfig } = props;
  const CodeBlock = ({ children = [], className, ...props }) => {
    const match = /language-(\w+)/.exec(className || '');
    if (className) {
      return (
        <SyntaxHighlighter
            language={match?.[1]}
            showLineNumbers={ false }
            style={ oneDark }
            PreTag='div'
            className='syntax-hight-wrapper'
            {...props}
          >
            {children}
        </SyntaxHighlighter>)
    }
    return (<code>{children}</code>) 
  } 
  return(
    <>{(
      <div className="recieve-info">
        { 
          chartConfig ? 
          ( <ChartMessage chartConfig={ chartConfig } /> ) : 
          (
            markdownSyntax ? (<Markdown
              components={{ code: CodeBlock, p: 'div'}}>
              { content }
            </Markdown>) : <div>{ content }</div>
          ) 
        }
        <div className="recieve-tips">
          以上内容为AI生成，不代表开发者立场，请勿删除或修改本标记
        </div>
      </div>
    )}</>
  )
}

// runtime表单渲染
const RuntimeForm = (props) => {
  const { formType, formMap, reportData } = props.formConfig;
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
      case 'report':
        return <ManageCubeCreateReport data={reportData}/>
        break;
      default:
        return <div>44444444</div>
    }
  }
  return <>{(
    <div className="recieve-form-item">
      { setFormDom(formType) }
    </div>
  )}</>
}

export default ReciveBox;
