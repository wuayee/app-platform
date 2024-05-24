import { del, get, post, put } from "./http";
import { httpUrlMap } from './httpConfig';

const { JANE_URL, AIPP_URL, APP_URL, APP_EVALUATE_URL } = httpUrlMap[process.env.NODE_ENV];

// 获取应用市场列表
export function queryAppsApi(tenantId, params) {
  return get(`${AIPP_URL}/store/tools`, params);
}

export function createEvalDataset(requestBody) {
  return post(`${APP_EVALUATE_URL}/evalDataset/createEvalDataset`,requestBody);
}

export function getEvalDatasetList(requestBody) {
  return get(`${APP_EVALUATE_URL}/evalDataset/getEvalDatasetList`,requestBody);
}
