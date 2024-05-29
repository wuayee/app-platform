import {del, get, post, patch} from "./http";
import { httpUrlMap } from './httpConfig';

const { AI_URL } = httpUrlMap[process.env.NODE_ENV];

// 聊天界面---详情
export function queryAppDetail(id) {
  const url = `${AI_URL}/hisp/api/v1/platform/app/${id}`;
  return get(url);
}

// 点赞点灭
export function feedbacksRq(params) {
  const url = `${AI_URL}/aipp/usr/feedback`;
  return post(url, params);
}

// 获取点赞点灭详情
export function queryFeedback(id) {
  const url = `${AI_URL}/aipp/usr/feedback/${id}`;
  return get(url);
}

// 更新点赞点灭
export function updateFeedback(id, data) {
  const url = `${AI_URL}/aipp/usr/feedback/${id}`;
  return patch(url, data);
}

// 取消点赞点灭
export function deleteFeedback(id) {
  const url = `${AI_URL}/aipp/usr/feedback/${id}`;
  return del(url);
}
