
import React, { useEffect, useState, useContext } from 'react';
import Markdown from 'react-markdown';
import { useParams, useLocation } from 'react-router-dom';
import { Tooltip, Checkbox } from "antd";
import { LinkIcon } from '@/assets/icon';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { oneDark } from 'react-syntax-highlighter/dist/esm/styles/prism'
import { AippContext } from '../../../aippIndex/context';
import { ChatContext } from '../../../aippIndex/context';
import ChartMessage from '../chart-message.jsx';
import FileContent from '../runtimeForm/FileContent.jsx';
import Feedbacks from './feedbacks';
import InterviewQuestions from '../runtimeForm/InterviewQuestions.jsx';
import ManageCubeCreateReport from '../runtimeForm/ManageCubeCreateReport.jsx';
import robot from '@/assets/images/ai/robot1.png';
import '../../styles/recieve-box.scss';

const ReciveBox = (props) => {
  const { aippInfo }  = useContext(AippContext);
  const { checkCallBack, showCheck }  = useContext(ChatContext);
  const { content, recieveType, formConfig, loading, checked, markdownSyntax, chartConfig } = props.chatItem;
  const [ showIcon, setShowIcon ] = useState(true);
  const location = useLocation();

  useEffect(() => {
    const { pathname } = location;
    if (pathname.includes('/chatShare/')) {
      setShowIcon(false);
    } 
  }, [location])
  function onChange(e) {
    props.chatItem.checked = e.target.checked;
    checkCallBack();
  }
  // 设置显示类型
  function setRecieveDom(type) {
    if (type === 'form') {
      return <RuntimeForm  formConfig={formConfig}/>
    }
    return <MessageBox content={content} markdownSyntax={markdownSyntax} chartConfig={chartConfig}/> 
  }
  return <>{(
    <div className='recieve-box'>
      { showCheck && <Checkbox className='check-box' checked={checked} onChange={onChange}></Checkbox>}
      <div className='user-image'>
        <Img />
        <span>{ aippInfo?.name || 'xxx' }</span>
      </div>
      <span className="recieve-info-inner">
        { loading ? <Loading /> : setRecieveDom(recieveType) }
        { showIcon && <div className='message-tip-box-send'>
          <div className='inner'>
            <Tooltip title="分享" color="white" overlayInnerStyle={{color: '#212121' }}>
              <div>
                <LinkIcon/>
              </div>
            </Tooltip>
            <Tooltip title="复制" color="white" overlayInnerStyle={{color: '#212121' }}>
              <div>
                <LinkIcon/>
              </div> 
            </Tooltip>
            <Tooltip title="删除" color="white" overlayInnerStyle={{color: '#212121' }}>
            <div>
              <LinkIcon/>
            </div>
            </Tooltip>
          </div>
        </div> }
        {/* { showIcon && <Feedbacks /> } */}
      </span>
    </div>
  )}</>
}
// 接收消息loading
const Loading = () => {
  return(
   <>
    <div class="recieve-loading">
      <div class="bounce1"></div>
      <div class="bounce2"></div>
      <div class="bounce3"></div>
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
