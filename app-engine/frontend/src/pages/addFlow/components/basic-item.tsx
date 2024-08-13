
import React from 'react';
import {
  StartIcon,
  DataRetrievalIcon,
  EndIcon,
  ManualCheckIcon,
  LlmIcon,
  IfIcon,
  FitIcon,
  CodeIcon
} from '@assets/icon';
import { handleClickAddBasicNode, handleDragBasicNode } from '../utils'

const BasicItems = (props: any) => {
  const { dragData } = props;

  // 根据类型设置图标
  const getIconByType = (type) => {
    return {
      'startNodeStart': <StartIcon />,
      'retrievalNodeState': <DataRetrievalIcon />,
      'llmNodeState': <LlmIcon />,
      'endNodeEnd': <EndIcon />,
      'manualCheckNodeState': <ManualCheckIcon />,
      'fitInvokeNodeState': <FitIcon />,
      'conditionNodeCondition': <IfIcon />,
      'toolInvokeNodeState': <FitIcon />,
      'codeNodeState': <CodeIcon />
    }[type];
  }
  return <>
    <div className='basic-drag-list'>
      {dragData.map((item, index) => {
        return (
          <div
            className='drag-item'
            onDragStart={(e) => handleDragBasicNode(item, e)}
            draggable={true}
            key={index}
          >
            <div className='drag-item-title'>
              <div>
                {getIconByType(item.type)}
                <span className='content-node-name'>{item.name}</span>
              </div>
              <span className='drag-item-icon' onClick={(event) => handleClickAddBasicNode(item, event)}>
                <img src='./src/assets/images/ai/flow.png' />
              </span>
            </div>
          </div>
        )
      })
      }
    </div>
  </>
};


export default BasicItems;
