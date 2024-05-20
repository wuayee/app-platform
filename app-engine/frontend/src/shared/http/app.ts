import { del, get, post, put } from "./http";
import { httpUrlMap } from './httpConfig';

const { APP_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

// 应用概览
export function queryAppDetail(appId: string) {
  const tenantId = '31f20efc7e0848deab6a6bc10fc3021e';
  const url = `${APP_URL}/${tenantId}/app/${appId}`;
  return get(url);
}
