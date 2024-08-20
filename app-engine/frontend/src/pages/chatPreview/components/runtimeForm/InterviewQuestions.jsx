import React, { useEffect, useState } from 'react';
import { Input, Button, Typography } from 'antd';
import { CommentOutlined } from '@ant-design/icons';
import { saveContent } from '@shared/http/appBuilder';
import { Message } from '@shared/utils/message';
import { uuid } from '../../../../common/utils';
import { useAppSelector } from '@/store/hook';
import { useTranslation } from 'react-i18next';
import './styles/interview-questions.scoped.scss';

const { TextArea } = Input;
const { Text } = Typography;

const InterviewQuestions = (props) => {
  const { t } = useTranslation();
  const { data, instanceId, mode } = props;
  const id = 'interviewResult';
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const [qa, setQA] = useState([]);

  useEffect(() => {
    if (!data) return;
    const result = data[id];
    let newQa = [...result];
    result && result.forEach((item, index) => {
      newQa[index].answer = '';
      newQa[index].text = item.question;
      newQa[index].uuid = uuid();
    });
    setQA(newQa);
  }, [data])

  const [expandedQuestionIds, setExpandedQuestionIds] = useState([]);

  const handleAnswerChange = (answerIndex, value) => {
    const data = qa.map((q, index) => {
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
    saveContent(tenantId, appId, instanceId, { 'businessData': { [id]: qa } }).then((res) => {
      if (res.code !== 0) {
        Message({ type: 'warning', content: res.msg || t('savingFailed') });
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

  return (<>
    <div className='form-wrap'>
      <div style={{ pointerEvents: mode === 'history' ? 'none' : 'auto', width: '100%' }}>
        {(qa && qa.length > 0) ? (
          qa.map((question, index) => (
            <div className='question-item' key={index}>
              <div className='inner'>
                <Text strong>{`${index + 1}. ${question.text}`}</Text>
                <Button
                  type='text'
                  icon={<CommentOutlined />}
                  onClick={() => toggleExpandQuestion(question.uuid)}
                />
              </div>
              {expandedQuestionIds.includes(question.uuid) && (
                <TextArea
                  rows={4}
                  defaultValue={qa[index].answer}
                  placeholder={t('plsEnter')}
                  onBlur={(e) => handleAnswerChange(index, e.target.value)}
                  style={{ marginTop: '8px' }}
                />
              )}
            </div>
          ))
        ) : (
            <div className={'thumb'}>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <Text strong className={'block-bg block big'}></Text>
                <Button
                  type='text'
                  icon={<CommentOutlined />}
                  style={{ marginLeft: 'auto' }}
                />
              </div>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <Text strong className={'block-bg block'}></Text>
                <Button
                  type='text'
                  icon={<CommentOutlined />}
                  style={{ marginLeft: 'auto' }}
                />
              </div>
              <div className={'block-bg big'} style={{ marginTop: '8px', height: '120px' }} />
            </div>
          )}
        <Button onClick={handleSave} className='save-button'>{t('save')}</Button>
      </div>
    </div>
  </>);
};

export default InterviewQuestions;
