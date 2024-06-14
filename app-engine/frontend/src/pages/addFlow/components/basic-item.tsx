
import React from 'react';
import {
  AddFlowIcon,
  StartIcon,
  DataRetrievalIcon,
  EndIcon,
  ManualCheckIcon,
  LlmIcon,
  IfIcon,
  FitIcon
} from '@assets/icon';
import { handleClickAddBasicNode, handleDragBasicNode} from '../utils'

const BasicItems = (props:any) => {
  const { dragData } = props;

  // 根据类型设置图标
  const getIconByType = (type) => {
    return {
      "startNodeStart": <StartIcon />,
      "retrievalNodeState": <DataRetrievalIcon />,
      "llmNodeState": <LlmIcon />,
      "endNodeEnd": <EndIcon />,
      "manualCheckNodeState": <ManualCheckIcon />,
      "fitInvokeNodeState": <FitIcon />,
      "conditionNodeCondition": <IfIcon />,
      "toolInvokeNodeState": <FitIcon />
    }[type];
  }
  return <>
    { dragData.map((item, index) => {
      return (
        <div
          className='drag-item'
          onDragStart={(e) => handleDragBasicNode(item, e)}
          draggable={true}
          key={index}
        >
          <div className='drag-item-title'>
            <div>
              { getIconByType(item.type) }
              <span className='content-node-name'>{ item.name }</span>
            </div>
            <span className='drag-item-icon' onClick={(event) => handleClickAddBasicNode(item, event)}><AddFlowIcon /></span>
          </div>
        </div>
      )
      })
    }
  </>
};


export default BasicItems;
