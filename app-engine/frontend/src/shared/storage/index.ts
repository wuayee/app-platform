/**
 * 本地缓存的操作对象
 * @type {{set(*=, *=): void, get(*=): (any|undefined), remove(*=): void}}
 */
import { FINANCE_APP_ID } from '../../pages/chatPreview/components/send-editor/common/config';
  
export const storage = {
  set(key, value) {
    if (value instanceof Object) {
      localStorage.setItem(key, JSON.stringify(value));
    } else {
      localStorage.setItem(key, value);
    }
  },
  get(key) {
    const data = localStorage.getItem(key);
    try {
      return JSON.parse(data);
    } catch (e) {
      // 打印错误日志
      return data;
    }
  },
  remove(key) {
    localStorage.removeItem(key);
  },
  /**
   * 获取应用对话chatId
   * @param {String} appId 应用Id
   * @return {String} 对话的chatId
   */
  getChatId(appId) {
    return storage.get('appChatMap')?.[appId]?.chatId || '';
  },
  /**
   * 获取对话chatId
   * @param {String} dimensionId 应用Id
   * @return {String} 对话的chatId
   */
  getDimensionChatId(appId, dimensionId) {
    return storage.get('appChatMap')?.[appId]?.dimensions?.[dimensionId] || '';
  },
};