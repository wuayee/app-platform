import {del, get, post, patch} from "./http";
import { httpUrlMap } from './httpConfig';

const { AI_URL, PLUGIN_URL } = httpUrlMap[process.env.NODE_ENV];

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
