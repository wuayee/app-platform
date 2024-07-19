import { createContext } from 'react';

export const AippContext = createContext({
  agent: null,
  showElsa: false,
  messageChecked: false,
  chatRunning: false,
  updateAippCallBack: null,
  setRequestLoading: '',
});
export const ChatContext = createContext({
  setShareClass: null,
  setInspiration: null,
  checkCallBack: null,
  handleRejectClar: null,
  useMemory: true,
  dataDimension: null,
  tenantId: ''
});

export const FlowContext = createContext({
  appInfo: {},
  type: '',
  modalInfo: {},
  showTime: false,
  setModalInfo: undefined,
  setFlowInfo: undefined,
  setShowTime: undefined
});
