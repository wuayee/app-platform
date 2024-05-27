import { del, get, post, put } from './http';
import { httpUrlMap } from './httpConfig';

const { KNOWLEDGE_URL = '/api/jober/knowledge' } = (httpUrlMap as any)[(process.env as any).NODE_ENV];

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

// 根据知识表id 查询知识表
export function getKnowledgeTableById(id: string) {
  const url = `${KNOWLEDGE_URL}/tables/${id}`;
  return post(url, {});
}

// 根据知识表id 知识库id 文件名查询列
export function getTableColums(data: {
  repositoryId: string,
  knowledgeTableId: string,
  fileName?: string,
}) {
  const url = `${KNOWLEDGE_URL}/table-knowledge/columns`;
  return post(url, data)
}

// 创建表格逻辑
export function createTableColumns(data: {
  repositoryId: string,
  knowledgeTableId: string,
  fileName?: string,
  columns?: {
    name: string,
    dataType: string,
    indexType: string,
    embedServiceId: string,
    desc: string,
  }[]
}) {
  const url = `${KNOWLEDGE_URL}/table-knowledge/construct`;
  return post(url, data)
}

// 查询文本chunks
export function getTextList(filterConfig: {
  // 知识库id
  knowledgeId: string,

  // 知识表id
  tableId: string,

  // 分页
  pageNo: number,

  // 分页大小
  pageSize: number,

  // 查询关键字
  content?: string,

  // topK
  topK?: number,

  // 阈值
  threshold?: number,

  // 列
  columnId?: number,
}) {
  // 如果有content 就设置topk为12

  if(!filterConfig.content) {
    const url = `${KNOWLEDGE_URL}/table/text/chunk-list`;
    return post(url, filterConfig);
  } else {
    const url = `${KNOWLEDGE_URL}/table/chunks`;
    filterConfig.topK = 12;
    return post(url, filterConfig);
  }
}

// 查询表格列
export function getTableColumns(reposId: string, tableId: string) {
  const url =`${KNOWLEDGE_URL}/table-knowledge/column/${reposId}/${tableId}`
  return get(url);
}

// 查询表格
export function getTableList(filterConfig: {
  // 仓库id
  repositoryId: string,

  // 表格id,
  knowledgeTableId: string,

  // 当前页
  pageNum: number,

  // 页码
  pageSize: number
}): Promise<{
  count: number;
  result: any[][];
}> {
  const url =  `${KNOWLEDGE_URL}/table-knowledge/rows`;

  filterConfig.pageNum--;

  return post(url, filterConfig)
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

export function deleteLocalFile(
  knowledgeId: number | string,
  tableId: number | string,
  filename: string[]
) {
  const url = `${KNOWLEDGE_URL}/${knowledgeId}/table/${tableId}/files/delete`;
  return del(url, filename);
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
  const url = `${KNOWLEDGE_URL}/import-knowledge/text`;
  return post(url, data);
}
