import request from '@/utils/request'

// 查询视频学习记录，记录学生观看视频的行为数据列表
export function listVideoLearningRecord(query) {
  return request({
    url: '/system/videoLearningRecord/list',
    method: 'get',
    params: query
  })
}

// 查询视频学习记录，记录学生观看视频的行为数据详细
export function getVideoLearningRecord(recordId) {
  return request({
    url: '/system/videoLearningRecord/' + recordId,
    method: 'get'
  })
}

// 新增视频学习记录，记录学生观看视频的行为数据
export function addVideoLearningRecord(data) {
  return request({
    url: '/system/videoLearningRecord',
    method: 'post',
    data: data
  })
}

// 修改视频学习记录，记录学生观看视频的行为数据
export function updateVideoLearningRecord(data) {
  return request({
    url: '/system/videoLearningRecord',
    method: 'put',
    data: data
  })
}

// 删除视频学习记录，记录学生观看视频的行为数据
export function delVideoLearningRecord(recordId) {
  return request({
    url: '/system/videoLearningRecord/' + recordId,
    method: 'delete'
  })
}
