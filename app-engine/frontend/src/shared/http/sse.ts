import { httpUrlMap } from './httpConfig';
import { get } from './http';
const { AIPP_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

// 工具流调试应用
/**
 * @param {string} tenantId - 租户ID
 * @param {any} params - 调试参数
 * @return {Promise} 返回一个Promise对象，resolve的参数为调试结果
 * @throws {Error} 如果请求失败，会抛出错误
 */
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
/**
 * @param {string} tenantId - 租户ID。
 * @param {any} params - 请求参数。
 * @param {string} chatType - 聊天类型。如果chatType不是'inactive'，则URL后缀为'app_chat'，否则为'app_chat_debug'。
 * @return {Promise} 返回一个Promise对象，该对象在请求成功时解析为响应对象。
 * @throws {Error} 如果请求失败，Promise对象将被拒绝。
 */
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
/**
 * @param {string} tenantId - 租户ID
 * @param {string} instanceId - 实例ID
 * @param {any} params - 需要保存的参数
 * @return {Promise} 返回一个Promise对象，resolve的参数为请求的响应
 * @throws {Error} 如果请求失败，会抛出错误
 */
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
/**
 * @param {string} tenantId - 租户ID
 * @param {string} appId - 应用ID
 * @return {Promise} 返回一个Promise，其解析的结果是一个对象，包含了版本信息
 * @throws {Error} 如果请求失败，会抛出错误
 */
export function getTestVersion(tenantId: string, appId: string) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/aipp?isDebug=true`);
}
export function saveChart(tenantId: string, instanceId: string, params: any) {
  let url = `${AIPP_URL}/${tenantId}/instances/${instanceId}`
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