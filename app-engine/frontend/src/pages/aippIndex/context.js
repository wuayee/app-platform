import { createContext } from 'react';

export const AippContext = createContext({
  appId: '',
  tenantId: '',
  agent: null,
  aippInfo: {},
  showElsa: false,
  messageChecked: false,
  chatRunning: false,
  updateAippCallBack: null,
  showHistory: true,
})
export const ChatContext = createContext({
  setShareClass: null,
  setInspiration: null,
  checkCallBack: null
});