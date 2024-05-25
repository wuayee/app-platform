import { del, get, post, put } from "./http";
import { httpUrlMap } from "./httpConfig";

const { MODEL_LIST_URL } = httpUrlMap[process.env.NODE_ENV];

// 查询模型列表
export function getModelList(params) {
  return get(`${MODEL_LIST_URL}/list_supported_models`, params);
}

// 创建模型
export function createModel(params) {
  return post(`${MODEL_LIST_URL}/start_up`, params);
}

// 删除模型
export function deleteModelByName(_object) {
  return del(`${MODEL_LIST_URL}/delete`,{}, _object);
}

// 刷新模型列表
export function getModelListMeta(params) {
  return get(`${MODEL_LIST_URL}/list_supported_models_meta`, params);
}