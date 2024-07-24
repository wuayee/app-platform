
import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button } from 'antd';
import { CloseOutlined, EyeOutlined } from '@ant-design/icons';
import { getToolsList } from '@shared/http/plugin';
import AddSkill from '../../../addFlow/components/tool-modal';

const Skill = (props) => {
  const { pluginData, updateData } = props;
  const [ skillList, setSkillList] = useState([]);
  const [ showModal, setShowModal ] = useState(false);
  const navigate=useNavigate();
  const { tenantId } = useParams();
  const pluginMap = useRef([]);

  const addPlugin = () => {
    setShowModal(true);
  }
  // 选择数据后回调
  const confirmCallBack = (workFlowId, fitId) => {
    if (workFlowId.length === 0 && fitId.length === 0) {
      setSkillList([]);
    }
    updateData(fitId, 'tools');
    updateData(workFlowId, 'workflows');
  }
  // 删除
  const deleteItem = (item) => {
    let workFlowList = [];
    let fitList = [];
    pluginMap.current = pluginMap.current.filter(pItem => pItem.uniqueName !== item.uniqueName);
    pluginMap.current.forEach(item => {
      if (item.tags.includes('WATERFLOW')) {
        workFlowList.push(item);
      } else {
        fitList.push(item);
      }
    });
    setSkillList([...pluginMap.current]);
    let workFlowId = workFlowList.map(item => item.uniqueName);
    let fitId = fitList.map(item => item.uniqueName);
    confirmCallBack(workFlowId, fitId);
  }
  // 获取插件列表
  const getPluginList = ()=> {
    getToolsList({ pageNum: 1, pageSize: 1000, excludeTags: 'App', })
      .then(({ data }) => {
        setSkillArr(data);
      })
  }
  // 回显设置
  const setSkillArr = (data) => {
    pluginMap.current = [];
    data.forEach(item => {
      if (pluginData.includes(item.uniqueName) ) {
        let obj = {
          uniqueName: item.uniqueName,
          name: item.name,
          tags: item.tags,
          type: item.tags.includes('WATERFLOW') ? 'workflow' : 'tool',
          appId: item.runnables?.APP?.appId || '',
          runnables: item.runnables
        };
        pluginMap.current.push(obj);
      }
    });
    setSkillList([...pluginMap.current]);
  }
  // 工具流详情
  const workflowDetail = (item) => {
    if (item.type === 'workflow') {
      if (item.appId.length) {
        navigate(`/app-develop/${tenantId}/app-detail/flow-detail/${item.appId}`);
      }
    } else {
      navigate(`/plugin/detail/${item.uniqueName}`);
    }
  }
  useEffect(() => {
    if (pluginData.length) {
      pluginMap.current = [];
      getPluginList();
    }
  }, [props.pluginData])
  return (
    <>
      <div className='control-container'>
        <div className='control'>
          <div className='control-header'>
            <div className='control-title'>
              <Button onClick={addPlugin}>添加</Button>
            </div>
          </div>
          <div className='control-inner'>
            {
              skillList.map((item, index) => {
                return (
                  <div className='item' key={index} >
                    <span className='item-left'>
                      { item.type === 'tool' ? 
                       <img src='/src/assets/images/ai/tool.png' alt='' />: 
                       <img src='/src/assets/images/ai/workflow.png' alt='' />
                      }
                      <span className='text'>{item.name || item }</span>
                    </span>
                    <span>
                      { <EyeOutlined style={{ cursor: 'pointer', fontSize: '14px', color: '#4D4D4D', marginRight: '8px' }} onClick={() => workflowDetail(item)}/> }
                      <CloseOutlined style={{ cursor: 'pointer', fontSize: '14px', color: '#4D4D4D' }} onClick={() => deleteItem(item)} />
                    </span>
                  </div>
                )
              })
            }
          </div>
          <AddSkill 
            type='addSkill'
            showModal={showModal} 
            setShowModal={setShowModal}
            checkData={skillList}
            confirmCallBack={confirmCallBack}
          />
        </div>
      </div>
    </>
  )
};

export default Skill;
