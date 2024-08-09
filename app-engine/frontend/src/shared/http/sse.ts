import { httpUrlMap } from './httpConfig';
import { get } from './http';
const { AIPP_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

// 工具流调试应用
export function workflowDebug(tenantId: string, params: any) {
  return new Promise((resolve, reject) => {
    fetch(`${AIPP_URL}/${tenantId}/water_flow_chat_debug`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(params)
    }).then((res) => {
      resolve(res);
    })
  });
}
// 调试对话
export function sseChat(tenantId: string, params: any, chatType: string) {
  let url = `${AIPP_URL}/${tenantId}/${chatType !== 'inactive' ? 'app_chat' : 'app_chat_debug'}`
  return new Promise((resolve, reject) => {
    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(params)
    }).then((res) => {
      resolve(res);
    })
  });
}
// 人工干预
export function saveContent(tenantId: string, instanceId: string, params: any) {
  let url = `${AIPP_URL}/${tenantId}/app/instances/${instanceId}`
  return new Promise((resolve, reject) => {
    fetch(url, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(params)
    }).then((res) => {
      resolve(res);
    })
  });
}
// 获取version
export function getTestVersion(tenantId: string, appId: string) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/aipp?isDebug=true`);
}