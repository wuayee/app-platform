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
});

export const ConfigFormContext = createContext({
  appId: '',
  tenantId: '',
});
