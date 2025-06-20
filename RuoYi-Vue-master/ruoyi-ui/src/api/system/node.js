import request from '@/utils/request'

// 查询知识图谱节点，存储课程的知识图谱结构列表
export function listNode(query) {
  return request({
    url: '/system/node/list',
    method: 'get',
    params: query
  })
}

// 查询知识图谱节点，存储课程的知识图谱结构详细
export function getNode(nodeId) {
  return request({
    url: '/system/node/' + nodeId,
    method: 'get'
  })
}

// 新增知识图谱节点，存储课程的知识图谱结构
export function addNode(data) {
  return request({
    url: '/system/node',
    method: 'post',
    data: data
  })
}

// 修改知识图谱节点，存储课程的知识图谱结构
export function updateNode(data) {
  return request({
    url: '/system/node',
    method: 'put',
    data: data
  })
}

// 删除知识图谱节点，存储课程的知识图谱结构
export function delNode(nodeId) {
  return request({
    url: '/system/node/' + nodeId,
    method: 'delete'
  })
}
