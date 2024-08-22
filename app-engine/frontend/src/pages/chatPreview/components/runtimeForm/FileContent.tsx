import React, { useEffect, useState } from 'react';
import { Input, Button, Typography } from 'antd';
import { saveContent } from '@shared/http/appBuilder';
import { Message } from '@shared/utils/message';
import { useAppSelector } from '@/store/hook';
import { useTranslation } from 'react-i18next';
import './styles/file-content.scoped.scss';
const { TextArea } = Input;

const FileContent = (props) => {
  const { t } = useTranslation();
  const { data, instanceId, mode } = props;
  const id = 'FileContent';
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const [result, setResult] = useState('');

  const handleSave = () => {
    saveContent(tenantId, appId, instanceId, { 'businessData': { [id]: result } }).then((res) => {
      if (res.code !== 0) {
        Message({ type: 'warning', content: res.msg || t('savingFailed') });
      }
    })
  }
  const handleChange = (value) => {
    setResult(value);
  };

  useEffect(() => {
    if (!data) return;
    setResult(data[id]);
  }, [data]);

  return (
    <div className='form-warp'>
      <div style={{ pointerEvents: mode === 'history' ? 'none' : 'auto', width: '100%' }}>
        {(result) ? (
          <TextArea
            rows={8}
            value={result}
            onChange={(e) => handleChange(e.target.value)}
            style={{ margin: '0 0 10px 0' }}
          />
        ) : (
            <div className='thumb'></div>
          )}
        <Button onClick={handleSave} className='save-button'>{t('save')}</Button>
      </div>
    </div>
  );
};

export default FileContent;
