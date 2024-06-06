
import React, { useEffect, useState } from 'react';

const Stage = (props) => {
  
  // 拖拽完成回调
  function handleDragEnter(e) {
    const nodeTab = e.dataTransfer.getData('itemTab');
    let nodeType;
    switch (nodeTab) {
      case 'basic':
        nodeType = e.dataTransfer.getData('itemType');
        window.agent.createNode(nodeType, e);
        break;
      case 'tool':
        nodeType = e.dataTransfer.getData('itemType');
        let nodeMetaData = e.dataTransfer.getData('itemMetaData');
        window.agent.createNode(nodeType, e, JSON.parse(nodeMetaData));
        break;
      default:
        break;
    }

  }
  return <>{(
    <div
      className='content-right'
      onDragOver={(e) => e.preventDefault()}
      onDrop ={handleDragEnter}>
        <div className='elsa-canvas' id='stage'></div>
    </div>
  )}</>
};


export default Stage;
