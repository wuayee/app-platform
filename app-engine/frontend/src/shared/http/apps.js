import { del, get, post, put } from './http';
import { httpUrlMap } from './httpConfig';

const appurl = window.localStorage.getItem('evalTask_URL') || '/api/jober'
const { JANE_URL, AIPP_URL, APP_URL } = httpUrlMap[process.env.NODE_ENV];

// 获取应用市场列表
export function queryAppsApi(tenantId, params) {
  return get(`${appurl}/tools`, params);
}

export function getEvalTaskList(requestBody) {
  return post(`${appurl}/evalTask/list`, requestBody);
}

export function copyEvalTask(id, author) {
  return post(`${appurl}/evalTask/copy?id=${id}&author=${author}`, {});
}

// 应用测评=查看报告-根据报告ID查看调用轨迹
export function getEvalReportTrace(id) {
  return get(`${appurl}/evalTask/report?reportId=${id}`, {});
}

// 应用测评=查看报告-根据任务ID查看报告详情
export function getEvalTaskReport(id) {
  return get(`${appurl}/evalTask/reportSummary?evalTaskId=${id}`, {});
}

// 获取评估测试集
export function getEvalDataList(params) {
  return post(`${appurl}/evalDataset/list`, params);
}

// 创建测试集
export function createEvalData(data) {
  return post(`${appurl}/evalDataset`, data);
}

// 上传测试集文件
export function uploadTestSetFile(data) {
  const url = `${appurl}/evalDataset/upload`;
  return post(url, data);
}

// 查询单个数据集列表
export function getDataSetListById(params) {
  const url = `${appurl}/evalDataset`;
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
  const url = `${appurl}/evalDataset`;
  return patch(url, data);
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
  const url = `${appurl}/evalDataset/evalData`;
  return patch(url, dataSet);
}

/**
 * @description 创建评估数据集
 * @property {string} datasetId - 数据集id.
 * @property {string} output - 输入.
 * @property {string} input - 输出.
 * */
export function createDataSetListData(dataSet) {
  const url = `${appurl}/evalDataset/evalData`;
  return post(url, dataSet);
}

/**
 * @description 删除单条数据
 * @property {string} id - 数据集id.
 * */
export function deleteDataSetListData(id) {
  const url = `${appurl}/evalDataset/evalData/${id}`;
  return del(url);
}

/**
 * @description 删除测试集
 * @property {string} id - 数据集id.
 * */
export function deleteDataSetData(id) {
  const url = `${appurl}/evalDataset/${id}`;
  return del(url);
}

// 下载模板链接
export const downTemplateUrl = `${appurl}/eval_dataset_template.xlsx`;

// 查询分析数据 appId ,  timeType 
export function getAnalysisData(data) {
  const url = `${appurl}/metrics/analysis`;
  return get(url, data);
}

// 查询反馈数据 isSortByCreateTime  isSortByResponseTime  answer  question pageIndex createTime appId pageSize orderDirection startTime createUser endTime
export function getFeedBackData(data) {
  const url = `${appurl}/metrics/feedback`;
  return post(url, data);
}

// 导出数据
export function exportFeedBackData(data) {
  const url = `${appurl}/metrics/export`;
  var xhr = new XMLHttpRequest();
  xhr.open('POST', url, true);
  xhr.setRequestHeader('Content-Type', 'application/json');
  xhr.responseType = 'blob';
  xhr.onload = function() {
    if (xhr.status === 200) {
      var contentDisposition = xhr.getResponseHeader('Content-Disposition');
      var match = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      var filename = match[1].replace(/['"]/g, '');
      filename = decodeURI(filename.split('UTF-8')[1])
      var blob = new Blob([xhr.response], {type: 'application/octet-stream'});
      var url = URL.createObjectURL(blob);
      var a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
    }
  };
  xhr.send(JSON.stringify(data));
  return post(url, data);
}
