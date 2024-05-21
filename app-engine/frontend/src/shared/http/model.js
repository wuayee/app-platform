import { del, get, post, put } from "./http";
import { httpUrlMap } from "./httpConfig";

const { MODEL_LIST_URL } = httpUrlMap[process.env.NODE_ENV];

// 查询模型列表
export function getModelList(params) {
  return get(`${MODEL_LIST_URL}/list_supported_models`, params);
}