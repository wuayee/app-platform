
import React, { useState, useEffect, useContext, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from 'antd';
import { CloseOutlined, EyeOutlined } from '@ant-design/icons';
import { getPlugins } from '@shared/http/plugin';
import { getWaterFlows } from "@shared/http/appBuilder";
import { ConfigFormContext } from '../../../aippIndex/context';
import AddSkill from '../../../addFlow/components/tool-modal';

const Skill = (props) => {
  const { pluginData, updateData } = props;
  const [ skillList, setSkillList] = useState([]);
  const [ showModal, setShowModal ] = useState(false);
  const { tenantId } = useContext(ConfigFormContext);
  const navigate=useNavigate();
  const modalRef = useRef();
  const pluginMap = useRef([]);

  useEffect(() => {
    if (pluginData.length) {
      pluginMap.current = [];
      getPluginList();
    }
  }, [props.pluginData])
  const addPlugin = () => {
    setShowModal(true);
  }
  // 选择数据后回调
  const confirmCallBack = (workFlowId, fitId) => {
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
    getPlugins({ pageNum: 0, pageSize: 1000, includeTags: 'FIT', })
      .then(({ data }) => {
        setSkillArr(data, 'tool');
        handleGetWaterFlows();
      })
  }
  // 获取工具流列表
  const handleGetWaterFlows = () => {
    getWaterFlows({ pageNum: 0, pageSize: 100, tenantId }).then(async (res) => {
      if (res.code === 0) {
        let list = res.data.map(item => {
          return {
            appId: item.appId,
            tenantId: item.tenantId,
            version: item.version,
            ...item.itemData
          }
        });
        setSkillArr(list, 'workflow');
        setSkillList([...pluginMap.current]);
      }
    })
  }
  // 回显设置
  const setSkillArr = (data, type) => {
    data.forEach(item => {
      if (pluginData.includes(item.uniqueName) ) {
        let obj = {
          uniqueName: item.uniqueName,
          name: item.name,
          tags: item.tags,
          type,
          appId: item.appId || '',
          tenantId: item.tenantId || '',
        };
        pluginMap.current.push(obj);
      }
    })
  }
  // 工具流详情
  const workflowDetail = (item) => {
    if (item.type === 'workflow') {
      navigate(`/app-develop/${item.tenantId}/app-detail/flow-detail/${item.appId}`);
    } else {
      navigate(`/plugin/detail/${item.uniqueName}`);
    }
  }
  return (
    <>
      <div className="control-container">
        <div className="control">
          <div className="control-header">
            <div className="control-title">
              <Button onClick={addPlugin}>添加</Button>
            </div>
          </div>
          <div className="control-inner">
            {
              skillList.map((item, index) => {
                return (
                  <div className="item" key={index} >
                    <span className="item-left">
                      { item.type === 'tool' ? 
                       <img src='/src/assets/images/ai/tool.png' alt='' />: 
                       <img src='/src/assets/images/ai/workflow.png' alt='' />
                      }
                      {item.name || item }
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
