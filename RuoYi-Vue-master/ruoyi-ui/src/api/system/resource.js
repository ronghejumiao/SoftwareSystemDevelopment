import request from '@/utils/request'

// 查询学习资源，存储课程的学习资源信息列表
export function listResource(query) {
  return request({
    url: '/system/resource/list',
    method: 'get',
    params: query
  })
}

// 查询学习资源，存储课程的学习资源信息详细
export function getResource(resourceId) {
  return request({
    url: '/system/resource/' + resourceId,
    method: 'get'
  })
}

// 新增学习资源，存储课程的学习资源信息
export function addResource(data) {
  return request({
    url: '/system/resource',
    method: 'post',
    data: data
  })
}

// 修改学习资源，存储课程的学习资源信息
export function updateResource(data) {
  return request({
    url: '/system/resource',
    method: 'put',
    data: data
  })
}

// 删除学习资源，存储课程的学习资源信息
export function delResource(resourceId) {
  return request({
    url: '/system/resource/' + resourceId,
    method: 'delete'
  })
}
