import React, { useState } from 'react';
import ConfigUI from './configUi/index';
import { useTranslation } from 'react-i18next';
import './index.scoped.scss';

const ConfigForm = (props) => {
  const { t } = useTranslation();
  const {
    mashupClick,
    configData,
    handleConfigDataChange,
    inspirationChange,
    showElsa
  } = props;
  const [activeKey, setActiveKey] = useState('application');
  const tab = [
    { name: t('appConfiguration'), key: 'application' },
    { name: t('interfaceConfiguration'), key: 'interface' }
  ]
  const handlePropDataChange = (data) => {
    const newData = { ...configData, form: data };
    handleConfigDataChange(newData);
  }
  const handleClick = (key) => {
    setActiveKey(key);
  }
  return (<>{(
    <div className='config-form-wrap'>
      <div className={['config-form', showElsa ? 'config-form-elsa' : null].join(' ')}>
        <div className='config-title'>
          <span className='config-left'>
            {tab.map(item => {
              return (
                <span className={activeKey === item.key ? 'active' : null} key={item.key} onClick={() => handleClick(item.key)}>
                  <span className='text'>{item.name}</span>
                  <span className='line'></span>
                </span>
              )
            })}
          </span>
          <span className='config-btn' onClick={mashupClick}>
            <img src='./src/assets/images/ai/mashup.png' width='16' height='16' />
            {t('workflowOrchestration')}
          </span>
        </div>
        <ConfigUI
          formData={configData?.form}
          handleConfigDataChange={handlePropDataChange}
          inspirationChange={inspirationChange}
          activeKey={activeKey}
        />
      </div>
    </div>
  )}
  </>);
};

export default ConfigForm;
