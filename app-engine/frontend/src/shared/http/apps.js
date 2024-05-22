import {del, get, post, put} from "./http";
import { httpUrlMap } from './httpConfig';

const { JANE_URL, AIPP_URL,APP_URL } = httpUrlMap[process.env.NODE_ENV];

// 获取应用列表
export function queryAppsApi(tenantId,params) {
  return get(`${AIPP_URL}/${tenantId}/app`, params);
}

// 删除应用
export function deleteAppApi(tenantId,appId) {
  return del(`${AIPP_URL}/${tenantId}/app/${appId}`);
}