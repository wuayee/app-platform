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
  setChatRunning: '',
  chatList: [],
  setChatList: [],
  chatId: '',
  setChatId: '',
  setRequestLoading: '',
  clearChat: false,
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
