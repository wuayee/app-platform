import {del, get, post, put} from "./http";
import { httpUrlMap } from './httpConfig';

const { AI_URL } = httpUrlMap[process.env.NODE_ENV];

// 聊天界面---详情
export function queryAppDetail(id) {
  const url = `${AI_URL}/hisp/api/v1/platform/app/${id}`;
  return get(url);
}

export function feedbacksRq(params) {
  const url = `${AI_URL}/aipp/usr/feedback`;
  return post(url, params);
}