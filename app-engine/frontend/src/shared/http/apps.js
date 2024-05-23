import {del, get, post, put} from "./http";
import { httpUrlMap } from './httpConfig';

const { JANE_URL, AIPP_URL,APP_URL } = httpUrlMap[process.env.NODE_ENV];

// 获取应用市场列表
export function queryAppsApi(tenantId,params) {
  return get(`/toolApi/store/tools`, params);
}
