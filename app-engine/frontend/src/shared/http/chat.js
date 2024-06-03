import { del, get, post, patch } from './http';
import { httpUrlMap } from './httpConfig';

const { AI_URL, MODEL_LIST_URL, PLUGIN_URL } = httpUrlMap[process.env.NODE_ENV];

export const tenantId = '282b963640544f148dbdfa49718075bc';

// 聊天界面---详情
export function queryAppDetail(id) {
  const url = `${AI_URL}/hisp/api/v1/platform/app/${id}`;
  return get(url);
}

// 点赞点灭
export function feedbacksRq(params) {
  const url = `${PLUGIN_URL}/aipp/usr/feedback`;
  return post(url, params);
}

// 创建对话
export function creatChat(tenantId, params) {
  const url = `${PLUGIN_URL}/v1/api/${tenantId}/chat`;
  return post(url, params);
}

// 更新会话
export function updateChat(tenantId, chatId, params) {
  const url = `${PLUGIN_URL}/v1/api/${tenantId}/chat/${chatId}`;
  return post(url, params);
}

// 查询应用对话列表
export function getChatList(tenantId, params) {
  const url = `${PLUGIN_URL}/v1/api/${tenantId}/chat/chat_list`;
  return post(url, params);
}

// 删除某次对话
export function deleteChat(tenantId, chatId) {
  const url = `${PLUGIN_URL}/v1/api/${tenantId}/chat?chat_id=${chatId}`;
  return del(url);
}

//查询对话详情
export function getChatDetail(tenantId, chatId, params) {
  const url = `${PLUGIN_URL}/v1/api/${tenantId}/chat/chat_list/${chatId}`;
  return post(url, params);
}
// 猜你想问
export function getRecommends(params) {
  const url = `${AI_URL}/recommend`;
  return post(url, params);
}

// 获取点赞点灭详情
export function queryFeedback(id) {
  const url = `${PLUGIN_URL}/aipp/usr/feedback/${id}`;
  return get(url);
}

// 更新点赞点灭
export function updateFeedback(id, data) {
  const url = `${PLUGIN_URL}/aipp/usr/feedback/${id}`;
  return patch(url, data);
}

// 取消点赞点灭
export function deleteFeedback(id) {
  const url = `${PLUGIN_URL}/aipp/usr/feedback/${id}`;
  return del(url);
}
