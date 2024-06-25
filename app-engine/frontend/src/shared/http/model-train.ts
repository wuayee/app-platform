import { del, get, post, put } from './http';
import { httpUrlMap } from './httpConfig';

const { MODEL_TRAINING_URL, AI_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

//模型训练任务列表接口
export function queryModelTaskList(queryBody: any) {
  const url = `${MODEL_TRAINING_URL}/v1/fine-tune/task/query`;
  return post(url, queryBody);
}

//查询指定taskId的checkpoint列表
export function getCheckpointList(taskId: string) {
  const url = `${MODEL_TRAINING_URL}/v1/fine-tune/check-points/${taskId}`;
  return get(url);
}

//选择checkpoint进行保存
export function saveCheckpoints(taskId: string, queryBody: any) {
  const url = `${MODEL_TRAINING_URL}/v1/fine-tune/check-points/${taskId}`;
  return post(url, queryBody);
}

// 登录edataMate
export function login_eDataMate(queryBody: any) {
  const url = `${MODEL_TRAINING_URL}/v1/fine-tune/dataset/upsert-user`;
  return post(url, queryBody);
}

// 获取edataMate数据集
export function getDatasets(queryBody: any) {
  const url = `${MODEL_TRAINING_URL}/v1/fine-tune/dataset/query`;
  return post(url, queryBody);
}

// 获取edataMate数据集的版本list
export function getDatasetVersions(datasetId, queryBody: any) {
  const url = `${MODEL_TRAINING_URL}/v1/fine-tune/dataset/${datasetId}/version`;
  return post(url, queryBody);
}

// 获取edataMate登录状态
export function get_eDataMateLogin() {
  const url = `${MODEL_TRAINING_URL}/v1/fine-tune/dataset/login`;
  return get(url);
}
