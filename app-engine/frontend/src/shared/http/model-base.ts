import { del, get, post, put } from './http';
import { httpUrlMap } from './httpConfig';

const { MODEL_BASE_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

// 模型仓详情接口
export function queryModelDetail(modelId: string) {
  const url = `${MODEL_BASE_URL}/v1/models/${modelId}`;
  return get(url);
}

//模型仓列表接口
export function queryModelbaseList(queryBody: any) {
  const url = `${MODEL_BASE_URL}/v1/models`;
  return post(url, queryBody);
}
