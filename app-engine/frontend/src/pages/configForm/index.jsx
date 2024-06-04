
import React, { useEffect, useState} from 'react';
import { WorkFlowIcon } from '@assets/icon';
import { ConfigFormWrap } from './styled';
import ConfigUI from './configUi/index.jsx';

const ConfigForm = (props) => {
  const { 
    mashupClick, 
    configData, 
    handleConfigDataChange, 
    inspirationChange,
    showElsa 
  } = props;
  const [ activeKey, setActiveKey ] = useState('application');
  const tab = [
    { name: '应用能力配置', key: 'application' },
    { name: '界面配置', key: 'interface' },
  ]
  const handlePropDataChange = (data) => {
    // config ui 调用该方法传递更新的formData
    const newData = {...configData, form : data}
    handleConfigDataChange(newData);
  }
  const handleClick = (key) => {
    setActiveKey(key);
  }
  return <>{(
    <ConfigFormWrap>
      <div className={['config-form', showElsa ? 'config-form-elsa' : null].join(' ')}>
        <div className='config-title'>
          <span className="config-left">
            { tab.map(item => {
              return (
                <span className={ activeKey === item.key ? 'active' : null } key={item.key} onClick={() => handleClick(item.key)}>
                  <span className="text">{ item.name }</span> 
                  <span className="line"></span>
                </span>
              )
            })}
          </span>
          <span className='config-btn' onClick={mashupClick}>
            <WorkFlowIcon />
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
    </ConfigFormWrap>
    )}
  </>
};

export default ConfigForm;
