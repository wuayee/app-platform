import { get } from "./http" ;
import { httpUrlMap } from './httpConfig';

const { PLUGIN_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

export function getPlugins(data: {pageNum: number, pageSize: number, includeTags: string, toolName: string}) {
  const url = `${PLUGIN_URL}/tools`;
  return get(url, data);
}
