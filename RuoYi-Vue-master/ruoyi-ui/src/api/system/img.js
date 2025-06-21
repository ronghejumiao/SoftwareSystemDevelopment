import request from '@/utils/request'

// 查询课程概念图，存储课程的概念图URL（1对多关系）列表
export function listImg(query) {
  return request({
    url: '/system/img/list',
    method: 'get',
    params: query
  })
}

// 查询课程概念图，存储课程的概念图URL（1对多关系）详细
export function getImg(mapId) {
  return request({
    url: '/system/img/' + mapId,
    method: 'get'
  })
}

// 新增课程概念图，存储课程的概念图URL（1对多关系）
export function addImg(data) {
  return request({
    url: '/system/img',
    method: 'post',
    data: data
  })
}

// 修改课程概念图，存储课程的概念图URL（1对多关系）
export function updateImg(data) {
  return request({
    url: '/system/img',
    method: 'put',
    data: data
  })
}

// 删除课程概念图，存储课程的概念图URL（1对多关系）
export function delImg(mapId) {
  return request({
    url: '/system/img/' + mapId,
    method: 'delete'
  })
}
