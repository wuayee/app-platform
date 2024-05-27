import {del, get, post, put} from "./http";
import { httpUrlMap } from './httpConfig';

const { JANE_URL, AIPP_URL,APP_URL } = httpUrlMap[process.env.NODE_ENV];

// 获取应用市场列表
export function queryAppsApi(tenantId,params) {
  return get(`${AIPP_URL}/tools`, params);
}


// 获取评估测试集
export function getEvalDataList(params) {
  return post(`${AIPP_URL}/evalDataset/list`, params);
}

// 创建测试集
export function createEvalData(data) {
  return post(`${AIPP_URL}/evalDataset`, data);
}

// 上传测试集文件
export function uploadTestSetFile(data) {
  const url = `${AIPP_URL}/evalDataset/upload`;
  return post(url, data);
}


// 下载模板链接
export const downTemplateUrl = `${AIPP_URL}/eval_dataset_template.xlsx`;