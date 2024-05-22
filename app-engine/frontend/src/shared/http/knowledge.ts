import { del, get, post, put } from './http';
import { httpUrlMap } from './httpConfig';

const { KNOWLEDGE_URL } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

// 获取知识库 传参name offset size
export function queryKnowledgeBase(data: { offset: number; size: number; name?: string }) {
  const url = `${KNOWLEDGE_URL}/repos/list`;
  return post(url, data);
}

// 修改知识库
export function modifyKnowledgeBase(data: { name: string; description: string; id: string }) {
  const url = `${KNOWLEDGE_URL}/repos/update`;
  return post(url, data);
}

// 删除知识库
export function deleteKnowledgeBase(id: string) {
  const url = `${KNOWLEDGE_URL}/repos/${id}`;
  return del(url);
}

// 创建知识库
export function createKnowledgeBase(data: { name: string; description: string; owner: string }) {
  const url = `${KNOWLEDGE_URL}/repos`;
  return post(url, data);
}

// 获取知识库详情
export function getKnowledgeBaseById(id: string) {
  const url = `${KNOWLEDGE_URL}/repos/${id}`;
  return get(url);
}

// 获取知识库下知识表
export function getKnowledgeDetailById(id: string, data: { pageNum: number; pageSize: number }) {
  const url = `${KNOWLEDGE_URL}/repos/${id}/tables/query`;
  return post(url, data);
}

// 创建知识库，知识表
export function createKnowledgeTableRow(
  id: string,
  data: {
    name: string;
    serviceType: string;
    format: 'TEXT' | 'TABLE';
    serviceId: string;
    repositoryId: string;
  }
) {
  const url = `${KNOWLEDGE_URL}/repos/${id}/tables`;
  return post(url, data);
}

// 获取知识表类型和服务
export function getKnowledgeTableType() {
  const url = `${KNOWLEDGE_URL}/storages`;
  return get(url);
}

// 删除知识表
export function deleteKnowledgeTableType(id: string) {
  const url = `${KNOWLEDGE_URL}/tables/${id}`;
  return del(url);
}

// 更新知识表
export function updateKnowledgeTable(data: { name: string; id: string }) {
  const url = `${KNOWLEDGE_URL}/tables/update`;
  return post(url, data);
}

export function uploadLocalFile(
  knowledgeId: number | string,
  tableId: number | string,
  data: File,
  filename: string
) {
  const url = `${KNOWLEDGE_URL}/${knowledgeId}/table/${tableId}/files`;
  return post(url, data, {
    headers: {
      'Content-Type': 'application/octet-stream',
      'attachment-filename': encodeURI(filename),
    },
  });
}

export function textSegmentWash(data: {
  knowledgeId: number;
  tableId: number;
  fileNames: string[];
  splitType: string;
  chunkSize: number;
  chunkOverlap: number;
  operatorIds: string[];
}) {
  const url = `${KNOWLEDGE_URL}/knowledge/import-knowledge/text`;
  return post(url, data);
}
