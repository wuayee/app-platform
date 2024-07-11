// 添加工具
export const handleClickAddBasicNode = (type, e) => {
  e.clientX += 100;
  window.agent.createNode(type, e);
}
// 拖动工具添加
export const handleDragBasicNode = (item, e) => {
  e.dataTransfer.setData('itemTab', 'basic');
  e.dataTransfer.setData('itemType', item.type);
  e.dataTransfer.setData('itemMetaData', JSON.stringify(item));
}
// 添加插件
export const handleClickAddToolNode = (type, e, metaData) => {
  e.clientX += 100;
  window.agent.createNode(type, e, metaData);
}
// 拖动插件添加
export const handleDragToolNode = (item, e) => {
  console.log(item);
  
  e.dataTransfer.setData('itemTab', 'tool');
  e.dataTransfer.setData('itemType', item.type || 'toolInvokeNodeState');
  e.dataTransfer.setData('itemMetaData', JSON.stringify(item));
}