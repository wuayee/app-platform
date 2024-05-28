import {del, get, post, put, patch} from "./http";
import { httpUrlMap } from './httpConfig';

const { JANE_URL, AIPP_URL,APP_URL } = httpUrlMap[process.env.NODE_ENV];
// TODO: baseUrl规范还未确定
// 获取应用市场列表
export function queryAppsApi(tenantId,params) {
  return get(`/api/jober/tools`, params);
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

// 查询单个数据集列表
export function getDataSetListById(params) {
  const url = `${AIPP_URL}/evalDataset`;
  return get(url, params);
}

/**
 * @typedef {Object} Data
 * @property {string} id - 数据集id.
 * @property {string} datasetName - 数据集名称.
 * @property {string} description - 数据集描述.
 * */ 

/**
 * @description 修改数据集基本信息
 * @param {Data} data - 修改数据集基本信息.
 * @property {string} id - 数据集id.
 * @property {string} datasetName - 数据集名称.
 * @property {string} description - 数据集描述.
 * */ 
export function modifyDataSetBaseInfo(data) {
  const url = `${AIPP_URL}/evalDataset`;
  return patch(url, data)
}

/**
 * @typedef {Object} DataSet
 * @property {string} id - 评估数据集id.
 * @property {string} output - .
 * @property {string} input - .
 * */ 

/**
 * @description 修改评估数据集
 * @param {DataSet} dataSet - 修改数据集基本信息.
 * @property {string} id - 数据集id.
 * @property {string} output - 输入.
 * @property {string} input - 输出.
 * */
export function modifyDataSetListData(dataSet) {
  const url = `${AIPP_URL}/evalDataset/evalData`;
  return patch(url, dataSet)
}

/**
 * @description 创建评估数据集
 * @property {string} datasetId - 数据集id.
 * @property {string} output - 输入.
 * @property {string} input - 输出.
 * */
export function createDataSetListData(dataSet) {
  const url = `${AIPP_URL}/evalDataset/evalData`;
  return post(url, dataSet)
}

/**
 * @description 删除单条数据
 * @property {string} id - 数据集id.
 * */
export function deleteDataSetListData(id) {
  const url = `${AIPP_URL}/evalDataset/evalData/${id}`;
  return del(url)
}

/**
 * @description 删除测试集
 * @property {string} id - 数据集id.
 * */
export function deleteDataSetData(id) {
  const url = `${AIPP_URL}/evalDataset/${id}`;
  return del(url)
}

// 下载模板链接
export const downTemplateUrl = `${AIPP_URL}/eval_dataset_template.xlsx`;