import { del, get, post, put } from './http';
import { httpUrlMap } from './httpConfig';

const { JANE_URL, AIPP_URL, PLUGIN_URL, CONDITION_URL, TT_URL } = httpUrlMap[process.env.NODE_ENV];
const sso_url = '/v1/user/sso_login_info';

// 获取当前用户信息
export const getCurUser = () => {
  return new Promise((resolve, reject) => {
    get(`${PLUGIN_URL}` + sso_url).then(
      (res) => {
        resolve(res);
      },
      (error) => {
        reject(error);
      }
    );
  });
};
// 上传图片
export function uploadFile(data, headers) {
  return post(`${JANE_URL}/jober/v1/jane/files`, data, { headers });
}

// 根据返回的地址获取语音转换的文字信息
export function voiceToText(tenantId, voicePath, fileName) {
  let url = process.env.NODE_ENV === 'development' ? 'http://80.11.128.86:30020/api/jober/v1/api' :
    process.env.NODE_ENV === 'production' ? window.location.origin + AIPP_URL : AIPP_URL
  return get(`${PLUGIN_URL || '/api/jober'}/voice/toText`, { voicePath: `${url}/${tenantId}/file?filePath=${voicePath}`, fileName });
}
// 文字转语音
export function textToVoice(text, tone) {
  return get(`${PLUGIN_URL || '/api/jober'}/voice/toVoice`, { text, tone });
}

// 查询应用列表
export function getAippList(tenant_id, params, limit, offset, name) {
  let url = `${AIPP_URL}/${tenant_id}/app?offset=${offset}&limit=${limit}`;
  if (name) {
    url += `&name=${name}`;
  }
  return get(url, params);
}
// 创建应用
export function createAipp(tenantId, appId, params) {
  return post(`${AIPP_URL}/${tenantId}/app/${appId}`, params);
}
// 获取应用详情
export function getAppInfo(tenantId, appId) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}`);
}
// 点击去编排
export function getAppInfoByVersion(tenantId, appId) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/latest_orchestration`);
}
// 更新应用全部详情
export function updateAppInfo(tenantId, appId, params) {
  return put(`${AIPP_URL}/${tenantId}/app/${appId}`, params);
}
// 更新表单详情
export function updateFormInfo(tenantId, appId, params) {
  return put(`${AIPP_URL}/${tenantId}/app/${appId}/config`, params);
}
// 更新工作流详情
export function updateFlowInfo(tenantId, appId, params) {
  return put(`${AIPP_URL}/${tenantId}/app/${appId}/graph`, params);
}
// 获取灵感大全
export function getInspiration(tenantId, appId) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/inspiration/department`);
}
// 根据部门获取灵感大全
export function getInspirationB(tenantId, appId) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/inspiration/department`);
}
// 调试应用
export function aippDebug(tenantId, appId, params, chatType = '') {
  let url = `${AIPP_URL}/${tenantId}/app/${appId}/debug`;
  if (chatType !== 'inactive') {
    url = `${AIPP_URL}/${tenantId}/app/${appId}/latest_published `;
    return get(url);
  } else {
    return post(url, params);
  }
}

// 发布应用
export function appPublish(tenantId, appId, params) {
  return post(`${AIPP_URL}/${tenantId}/app/${appId}/publish`, params);
}
// 轮训接口
export function reGetInstance(tenantId, appId, instanceId, version) {
  return get(`${AIPP_URL}/${tenantId}/aipp/${appId}/instances/${instanceId}?version=${version}`);
}
// 获取历史对话
export function getRecentInstances(tenantId, appId, type) {
  return get(`${AIPP_URL}/${tenantId}/log/app/${appId}/recent?type=${type}`);
}
// 获取多应用历史对话
export function getAppRecentlog(tenantId, appId, type) {
  return get(`${AIPP_URL}/${tenantId}/log/app/${appId}/chat/recent?type=${type}`);
}
// 获取会话历史对话
export function getChatRecentLog(tenantId, chatId, appId) {
  return get(`${AIPP_URL}/${tenantId}/log/app/${appId}/chat/${chatId}`);
}
// 删除历史对话记录
export function clearInstance(tenantId, appId, type) {
  return del(`${AIPP_URL}/${tenantId}/log/app/${appId}?type=${type}`);
}
// 终止当前对话
export function stopInstance(tenantId, instanceId, params) {
  return put(`${AIPP_URL}/${tenantId}/instances/${instanceId}/terminate`, params);
}

// 获取灵感大全部门数据
export function queryDepartMent(tenantId, appId) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/prompt`);
}
// 获取灵感大全列表数据
export function queryInspiration(tenantId, appId, categoryId) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/prompt/${categoryId}`);
}
// 获取灵感大全下拉数据
export function queryInspirationSelect(tenantId, fitableid, params) {
  return post(`${AIPP_URL}/${tenantId}/genericables/fitables/${fitableid}`, params);
}
// 文件上传
export function uploadChatFile(tenantId, appId = '', data, headers) {
  return post(`${AIPP_URL}/${tenantId}/file?aipp_id=${appId}`, data, { ...headers, 'Content-Type': 'multipart/form-data' });
}

// 文件上传
export function uploadImage(tenantId, data, headers) {
  return post(`${AIPP_URL}/${tenantId}/file`, data, { ...headers, 'Content-Type': 'multipart/form-data' });
}
// 图片预览
export function picturePreview(tenantId, params) {
  return get(`${AIPP_URL}/${tenantId}/file`, params);
}
// 分享对话
export function shareDialog(tenantId, data) {
  return post(`${AIPP_URL}/${tenantId}/share`, data);
}
// 获取分享对话内容
export function getSharedDialog(tenantId, shareId) {
  return get(`${AIPP_URL}/${tenantId}/share/${shareId}`);
}
// 用户自勾选
export function getReportInstance(tenantId, instanceId, data) {
  return post(`${AIPP_URL}/${tenantId}/start/instances/${instanceId}`, data);
}
// 启动对话实例
export function startInstance(tenantId, appId, params) {
  return post(`${AIPP_URL}/${tenantId}/aipp/${appId}/start`, params);
}
// 调试轮询
export function reTestInstance(tenantId, aippId, instanceId, version) {
  return get(
    `${AIPP_URL}/${tenantId}/aipp/${aippId}/instances/${instanceId}/runtime?version=${version}`
  );
}
// 获取版本历史记录
export function getVersion(tenantId, appId) {
  return get(`${AIPP_URL}/${tenantId}/app/${appId}/recentPublished`);
}
// 获取插件接口
export function getToolList(params) {
  return get(`${AIPP_URL}/store/plugins/search`, params);
}

// 获取溯源下拉选项
export function getOptions(data) {
  return post(`${AIPP_URL}/api/v1/platform/db/search`, data);
}
// 继续会话
export function resumeInstance(tenantId, instanceId, params) {
  return put(`${AIPP_URL}/${tenantId}/app/instances/${instanceId}`, params);
}
// 溯源表单重新生成对话
export function reSendChat(tenant_id, current_instance_id, data) {
  return post(`${AIPP_URL}/${tenant_id}/chat/instances/${current_instance_id}`, data);
}

// 获取澄清字段下拉
export function getClarifyOptions(data) {
  return post(`${TT_URL}/hisp/api/v1/platform/finance/option-nodes-name`,data);
}
// 澄清-辅产品
export function getFuClarifyOptions(params) {
  return get(`${TT_URL}/hisp/api/v1/platform/finance/option-nodes`, params);
}

// 获取溯源字段下拉接口
export function getOptionNodes(data) {
  return post(`${TT_URL}/hisp/api/v1/platform/finance/option-nodes`, data);
}
// 获取溯源字段下拉接口
export function getFinanceOptions(params) {
  return get(`${TT_URL}/hisp/api/v1/platform/finance/fieldValues`, params);
}
