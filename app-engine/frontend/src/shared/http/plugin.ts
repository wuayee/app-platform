import { get } from './http';
import { httpUrlMap } from './httpConfig';

const { PLUGIN_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

export function getPlugins(data: {
  pageNum: number;
  pageSize: number;
  includeTags: string;
  name: string;
}) {
  const url = `${PLUGIN_URL}/tools/search`;
  return get(url, data);
}

export function getPluginDetail(pluginId) {
  const url = `${PLUGIN_URL}/tools/${pluginId}`;
  return get(url);
}
