/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
import React, {useContext, useState} from 'react';
import { Input, Button, Typography } from 'antd';
import { CommentOutlined } from '@ant-design/icons';
import {saveContent} from "../../../../shared/http/appBuilder";
import {Message} from "../../../../shared/utils/message";
import styled from "styled-components";
import { AippContext } from "../../../aippIndex/context";
import { saveReport } from "../../../../shared/http/appBuilder";
import { uuid } from "../../../../common/utils";


const { TextArea } = Input;
const { Text } = Typography;
const FormWrap = styled.div`
    width: 100%;
    color: rgb(37, 43, 58);
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    .question-item {
      width: 100%;
      margin-bottom:16px;
      .inner {
        display: flex;
        align-items: center;
      }
    }
    .save-button {
      background-color: rgb(4, 123, 252);
      border-radius: 4px;
      font-size: 14px;
      color: white;
      border-color: white;
      width: 60px;
      height: 32px;
      margin: auto;
    }
    .thumb {
        width: 100%;
        min-height: 200px;
        border: 1px dashed #d9d9d9;
        border-radius: 4px;
        padding: 12px;
        font-size: 14px;
        color: #bfbfbf;
        cursor: text;
        box-sizing: border-box;
        margin: 0 0 10px 0;
        .block-bg {
            width: 76%;
            background-color: rgb(245, 245, 245);
            display: inline-block;
            border-radius: 3px;
            &.big {
               width: 92%;
            }
        }
        .block {
            height: 18px;
        }
    }
`;
const InterviewQuestions = ({ data, instanceId, mode }) => {
    const id = "interviewResult";
    const {appId, tenantId, showElsa, agent, chatRunning} = useContext(AippContext);
    const [qa, setQA] = useState(() => {
      let qa = data ? [...data] : [];
      data && data.forEach((item, index) => {
        qa[index].answer = '';
        qa[index].text = item.question;
        qa[index].uuid = uuid();
      });
      return qa;
    });

    const [expandedQuestionIds, setExpandedQuestionIds] = useState([]);

    const handleAnswerChange = (answerIndex, value) => {
      const data = qa.map((q, index) =>{
        if (answerIndex === index) {
          return {
            ...q,
            answer: value
          }
        }
        return q;
      })
      setQA(data);
    };

    const handleSave = () => {
        saveContent(tenantId, appId, instanceId, {"businessData": {[id]: qa}}).then((res) => {
            if (res.code !== 0) {
                Message({ type: 'warning', content: res.msg || '保存失败' });
            }
        })
    }

    const toggleExpandQuestion = (uuid) => {
      setExpandedQuestionIds((prevExpandedQuestionIds) =>
        prevExpandedQuestionIds.includes(uuid)
          ? prevExpandedQuestionIds.filter((id) => id !== uuid)
          : [...prevExpandedQuestionIds, uuid]
      );
    };

    return (
      <FormWrap>
      <div style={{pointerEvents: mode === "history" ? "none" : "auto", width: "100%"}}>
        {(qa && qa.length > 0) ? (
          qa.map((question, index) => (
            <div className="question-item" key={index}>
              <div className="inner">
                <Text strong>{`${index + 1}. ${question.text}`}</Text>
                <Button
                  type="text"
                  icon={<CommentOutlined />}
                  onClick={() => toggleExpandQuestion(question.uuid)}
                />
              </div>
              {expandedQuestionIds.includes(question.uuid) && (
                <TextArea
                  rows={4}
                  defaultValue={qa[index].answer}
                  placeholder={"输入面试者的答复"}
                  onBlur={(e) => handleAnswerChange(index, e.target.value)}
                  style={{ marginTop: '8px' }}
                />
              )}
            </div>
          ))
        ) : (
          <div className={"thumb"}>
            <div style={{ display: 'flex', alignItems: 'center' }}>
                <Text strong className={"block-bg block big"}></Text>
                <Button
                  type="text"
                  icon={<CommentOutlined />}
                  style={{ marginLeft: 'auto' }}
                />
            </div>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <Text strong className={"block-bg block"}></Text>
              <Button
                type="text"
                icon={<CommentOutlined />}
                style={{ marginLeft: 'auto' }}
              />
            </div>
            <div className={"block-bg big"} style={{ marginTop: '8px', height: '120px' }}/>
          </div>
        )}
        <Button onClick={handleSave} className="save-button">保存</Button>
      </div>
      </FormWrap>
    );
};

export default InterviewQuestions;
