import { get, post } from './http';
import { httpUrlMap } from './httpConfig';

const { PLUGIN_URL, AI_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

export function getPlugins(data: {
  pageNum: number;
  pageSize: number;
  includeTags: string;
  name: string;
}, excludeTags:string = '') {
  const url = `${PLUGIN_URL}/store/plugins/search?${excludeTags}`;
  return get(url, { ...data });
}

export function getPluginDetail(pluginId) {
  const url = `${PLUGIN_URL}/store/plugins/${pluginId}`;
  return get(url);
}
// 插件列表
export function getToolsList(params) {
  const url = `${PLUGIN_URL}/tools/search`;
  return get(url, params);
}

// 我的-工具
export function getPluginTool(tenantId, data: { pageNum: number; pageSize: number; }) {
  const url = `${PLUGIN_URL}/v1/api/${tenantId}/store/plugins?excludeTags=APP&excludeTags=WATERFLOW`;
  return get(url, data);
}

// 我的-工具流
export function getPluginWaterFlow(
  tenantId,
  data: { offset: number; limit: number; type: string }
) {
  const url = `${PLUGIN_URL}/v1/api/${tenantId}/app`;
  return get(url, data);
}

// 我的-已发布（工具+工具流)
export function getMyPlugin(tenantId, data, type = '') {
  let str = type !== 'modal' ? 'excludeTags=APP' : 'excludeTags=APP&excludeTags=WATERFLOW';
  const url = `${PLUGIN_URL}/v1/api/${tenantId}/store/plugins?${str}`;
  return get(url, data);
}
// 解析工具插件包内容
export function getPluginPackageInfo(file) {
  const formData = new FormData();
  formData.append('tool-filename', file);
  const url = `${PLUGIN_URL}/tools/parse/file`;
  return post(url, formData, {
    headers: {
      'Content-Type': 'application/form-data',
      'tool-filename': file?.name,
    },
  });
}

// 确认上传插件
export function uploadPlugin(param, toolsName) {
  const url = `${PLUGIN_URL}/plugins/save/tools?toolNames=${toolsName}`;
  return post(url, param,  {
    headers: {
      'Content-Type': 'application/form-data'
    },
  });
}

