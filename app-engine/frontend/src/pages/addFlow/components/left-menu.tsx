
import React from 'react';
import { useParams } from 'react-router-dom';
import { Tabs } from "antd";
import { LeftArrowIcon } from '@assets/icon';
import { getAddFlowConfig } from '@shared/http/appBuilder';
import BasicItems from './basic-item';
import ToolItems from './tool-item';

const LeftMenu = (props) => {
  const { dragData, menuClick, setDragData } = props;
  const { tenantId, appId } = useParams();

  const tabClick = (key) => {
    getAddFlowConfig(tenantId,  {pageNum: 1, pageSize: 100, tag: key}).then(res => {
      if (res.code === 0) {
        if (key === 'HUGGINGFACE') {
          res.data.tool.forEach(item => {
            item.type = 'huggingFaceNodeState',
            item.context = {
              default_model: item.defaultModel
            }
          })
        };
        setDragData(res.data);
      }
    });
  }
  const items = [
    {
      key: 'basic',
      label: '基础',
      children: <BasicItems dragData={dragData.basic || []} />,
      icon: <LeftArrowIcon onClick={menuClick} />
    },
    {
      key: 'tool',
      label: '插件',
      children: <ToolItems dragData={dragData.tool || []} tabClick={tabClick} />,
    },
  ];
  return <>{(
    <div className='content-left'>
      <Tabs defaultActiveKey="basic" items={items} />
    </div>
  )}</>
};


export default LeftMenu;
