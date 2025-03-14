/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState } from 'react';
import { markedProcess } from '../../utils/marked-process';
import { useTranslation } from 'react-i18next';
import ReferenceDrawer from './reference-drawer';
import { Message } from '@/shared/utils/message';
import { isChatRunning } from '@/shared/utils/chat';
import { useAppSelector } from '@/store/hook';
import Feedbacks from './feedbacks';
import PictureList from './picture-list';
import ThinkBlock from './think-block';
import 'highlight.js/styles/monokai-sublime.min.css';
import './styles/message-detail.scss';

/**
 * 消息详情
 * @return {JSX.Element}
 * @content 消息内容
 * @instanceId 租户ID
 * @feedbackStatus 点赞点踩状态
 * @reference 溯源返回reference列表
 * @msgType 溯源返回状态
 * @pictureList 图片列表
 */
const MessageBox = (props: any) => {
  const { content, thinkTime, instanceId, finished, feedbackStatus, status, reference, msgType, pictureList } = props;
  const { t } = useTranslation();
  const [isOpen, setIsOpen] = useState(false);
  const [referenceStr, setReferenceStr] = useState('');
  const [thinkContent, setThinkContent] = useState('');
  const [answerContent, setAnswerContent] = useState('');
  const [referenceIndex, setReferenceIndex] = useState('0');
  const [replacedText, setReplacedText] = useState<any>(null);
  const chatReference = useAppSelector((state) => state.chatCommonStore.chatReference);
  const referenceList = useAppSelector((state) => state.chatCommonStore.referenceList);
  
  // 正则替换
  const regExpReplace = (content: string, index: any) => {
    let strs = content;
    let replacedStrs = strs.replace(/<\/ref><ref>/g, '_');
    let replacedStr = replacedStrs.replace(/<ref>(.*?)<\/ref>/g, (match, key) => {
      let splitStr = key.split('_');
      let refenrenceStr = Array.isArray(reference)
        ? Object.keys(reference[0] || {})
        : referenceList
          ? Object.keys(referenceList)
          : [];
      let isReference = splitStr.filter((item: any) => refenrenceStr.includes(item));
      if (isReference.length) {
        return `<span data-reference=${index}-${key} class=source-word></span>`;
      }
      return '';
    });
    return replacedStr;
  };

  // 拼接content与reference
  const replaceInfo = (content, type = '') => {
    let metaContent = [content];
    let mataStr = metaContent.map((item: any, index: any) => {
      return regExpReplace(item, index);
    });
    type ? setThinkContent(mataStr.join('')) : setReplacedText(mataStr.join(''));
  };

  // 点击弹出溯源的抽屉回调
  const onClickHTML = (e: any) => {
    if (isChatRunning()) {
      Message({ type: 'warning', content: t('tryLater') });
      return;
    }
    if (e.target.classList.contains('source-word')) {
      e.target.classList.add('source-word-click');
      let strs = e.target.dataset.reference;
      let referenceIndex = strs.split('-')[0];
      let referenceStr = strs.split('-')[1];
      if (strs) {
        setReferenceStr(referenceStr);
        setReferenceIndex(referenceIndex);
        setIsOpen(true);
      }
    }
  };

  const getMessageContent = () => {
    if (pictureList) {
      return <PictureList pictureList={pictureList}></PictureList>;
    } else {
      return (
        <div
          className='receive-info-html'
          onClick={(e) => onClickHTML(e)}
          dangerouslySetInnerHTML={{ __html: markedProcess(replacedText) }}
        ></div>
      );
    }
  };

  const recieveClick = (event) => {
    if (event.target && event.target.nodeName.toLowerCase() === 'a') {
      event.preventDefault();
      window.open(event.target.href, '_blank');
    }
  }

  useEffect(() => {
    if (msgType === 'META_MSG' || chatReference) {
      replaceInfo(answerContent);
    } else {
      setReplacedText(answerContent);
    }
  }, [answerContent]);

  useEffect(() => {
    const thinkStartIdx = content.indexOf('<think>');
    let thinkEndIdx = content.indexOf('</think>');
    if (thinkStartIdx > -1 && thinkEndIdx < 0) {
      thinkEndIdx = content.length - '</think>'.length;
    }
    if (thinkEndIdx > 0) {
      thinkEndIdx = thinkEndIdx + '</think>'.length;
    }
    if (thinkStartIdx > -1) {
      const thinkContent = content.slice(thinkStartIdx, thinkEndIdx);
      if (msgType === 'META_MSG' || chatReference) {
        replaceInfo(thinkContent, 'deepseek');
      } else {
        setThinkContent(thinkContent);
      }
      setAnswerContent(content.slice(thinkEndIdx));
    } else {
      setAnswerContent(content);
    }
  }, [content]);

  // 接受消息点击事件
  useEffect(() => {
    const container = document.querySelector('.message-box');
    if (container) {
      container.addEventListener('click', recieveClick);
    }
    return () => {
      container && container.removeEventListener('click', recieveClick);
    }
  }, []);
  
  return (
    <>
      <div className='receive-info'>
        { (thinkContent && status !== 'TERMINATED') && <ThinkBlock content={thinkContent} thinkTime={thinkTime} />}
        {getMessageContent()}
        { finished &&  
        <div className='feed-footer'>
          <Feedbacks
            instanceId={instanceId}
            feedbackStatus={feedbackStatus}
            refreshFeedbackStatus={props.refreshFeedbackStatus}
          />
        </div> }
        {reference?.length > 0 && (
          <ReferenceDrawer
            isOpen={isOpen}
            setIsOpen={setIsOpen}
            reference={reference}
            referenceStr={referenceStr}
            referenceIndex={referenceIndex}
          />
        )}
      </div>
    </>
  );
};

export default MessageBox;
