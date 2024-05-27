import { del, get, post, put } from './http';
import { httpUrlMap } from './httpConfig';

const { JANE_URL, AIPP_URL, APP_URL } = httpUrlMap[process.env.NODE_ENV];

// 获取应用市场列表
export function queryAppsApi(tenantId, params) {
  return get(`${AIPP_URL}/store/tools`, params);
}

export function createEvalDataset(requestBody) {
  return post(`${AIPP_URL}/evalDataset/createEvalDataset`, requestBody);
}

export function getEvalDatasetList(requestBody) {
  return post(`${AIPP_URL}/evalDataset/getEvalDatasetList`, requestBody);
}

export function getEvalTaskList(requestBody) {
  return post(`${AIPP_URL}/evalTask/list`, requestBody);
}

export function copyEvalTask(id, author) {
  return post(`${AIPP_URL}/evalTask/copy?id=${id}&author=${author}`, {});
}

//应用测评=查看报告-根据报告ID查看调用轨迹
export function getEvalReportTrace(id) {
  return get(`${AIPP_URL}/evalTask/report?reportId=${id}`, {});
}

//应用测评=查看报告-根据任务ID查看报告详情
export function getEvalTaskReport(id) {
  return get(`${AIPP_URL}/evalTask/reportSummary?evalTaskId=${id}`, {});
}
