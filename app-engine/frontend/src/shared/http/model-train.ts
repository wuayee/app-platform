import { del, get, post, put } from './http';
import { httpUrlMap } from './httpConfig';

const { MODEL_TRAINING_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];


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
