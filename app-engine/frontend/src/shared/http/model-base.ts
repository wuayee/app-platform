import { del, get, post, put } from './http';
import { httpUrlMap } from './httpConfig';

const { MODEL_MANAGE_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

// 模型仓详情接口
export function queryModelDetail(modelId: string) {
  const url = `${MODEL_MANAGE_URL}/v1/models/${modelId}`;
  return get(url);
}

//模型仓列表接口
export function queryModelbaseList(queryBody: any) {
  const url = `${MODEL_MANAGE_URL}/v1/models`;
  return post(url, queryBody);
}

// 模型仓版本接口
export function queryModelVersionList(name) {
  const url = `${MODEL_MANAGE_URL}/v1/models/${name}`;
  return get(url);
}

//查询当前模型仓最新的任务刷新状态
export function modelbaseSyncStatus() {
  const url = `${MODEL_MANAGE_URL}/v1/models/sync_status`;
  return get(url);
}

//查询当前模型仓最新的任务刷新状态
export function modelbaseSync() {
  const url = `${MODEL_MANAGE_URL}/v1/models/sync`;
  return post(url, {});
}

export function deleteModelbase(modelId: string) {
  const url = `${MODEL_MANAGE_URL}/v1/models/${modelId}`;
  return del(url);
}

export function deleteModelbaseVersion(versionId: string) {
  const url = `${MODEL_MANAGE_URL}/v1/versions/${versionId}`;
  return del(url);
}

export function getModelSeries() {
  const url = `${MODEL_MANAGE_URL}/v1/models/seriesNames`;
  return get(url);
}
