import store from '@/store/store';

/**
 * 检验是否有未结束的对话
 * @return {boolean} 如果有未结束的对话返回true，否则返回false
 */
export const isChatRunning = () => {
  const commonStore = store.getState().chatCommonStore;
  const chatList:any = commonStore.chatList || [];
  const chatRunning = commonStore.chatRunning || false;
  let hasRunning = chatList.filter(item => item.status === 'RUNNING')[0];
  if (chatRunning || hasRunning) {
    return true;
  }
  return false;
}