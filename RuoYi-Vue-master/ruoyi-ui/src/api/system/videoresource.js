import request from '@/utils/request'

// 查询视频学习资源列表
export function listVideoresource(query) {
  return request({
    url: '/system/videoresource/list',
    method: 'get',
    params: query
  })
}

// 查询视频学习资源详细
export function getVideoresource(videoId) {
  return request({
    url: '/system/videoresource/' + videoId,
    method: 'get'
  })
}

// 新增视频学习资源
export function addVideoresource(data) {
  return request({
    url: '/system/videoresource',
    method: 'post',
    data: data
  })
}

// 修改视频学习资源
export function updateVideoresource(data) {
  return request({
    url: '/system/videoresource',
    method: 'put',
    data: data
  })
}

// 删除视频学习资源
export function delVideoresource(videoId) {
  return request({
    url: '/system/videoresource/' + videoId,
    method: 'delete'
  })
}
