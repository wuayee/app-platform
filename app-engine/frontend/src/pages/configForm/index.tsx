import React, { useState } from 'react';
import ConfigUI from './configUi/index.jsx';
import './index.scoped.scss';

const ConfigForm = (props) => {
  const { 
    mashupClick, 
    configData, 
    handleConfigDataChange, 
    inspirationChange,
    showElsa 
  } = props;
  const [activeKey, setActiveKey] = useState('application');
  const tab = [
    { name: '应用能力配置', key: 'application' },
    { name: '界面配置', key: 'interface' }
  ]
  const handlePropDataChange = (data) => {
    const newData = {...configData, form : data};
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
            { tab.map(item => {
              return (
                <span className={ activeKey === item.key ? 'active' : null } key={item.key} onClick={() => handleClick(item.key)}>
                  <span className='text'>{ item.name }</span> 
                  <span className='line'></span>
                </span>
              )
            })}
          </span>
          <span className='config-btn' onClick={mashupClick}>
            <img src='/src/assets/images/ai/mashup.png' width='16' height='16' />
            工作流编排  
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
