import request from '@/utils/request'

// 查询学习记录列表
export function listLearningRecord(query) {
  return request({
    url: '/system/learningRecord/list',
    method: 'get',
    params: query
  })
}

// 根据用户ID和课程ID查询学习记录
export function getLearningRecordByUserAndCourse(userId, courseId) {
  return request({
    url: '/system/learningRecord/user/' + userId + '/course/' + courseId,
    method: 'get'
  })
}

// 查询学习记录详细
export function getLearningRecord(recordId) {
  return request({
    url: '/system/learningRecord/' + recordId,
    method: 'get'
  })
}

// 新增学习记录
export function addLearningRecord(data) {
  if (!data.joinTime) {
    data.joinTime = new Date();
  }
  return request({
    url: '/system/learningRecord',
    method: 'post',
    data: data
  })
}

// 修改学习记录
export function updateLearningRecord(data) {
  return request({
    url: '/system/learningRecord',
    method: 'put',
    data: data
  })
}

// 删除学习记录
export function delLearningRecord(recordId) {
  return request({
    url: '/system/learningRecord/' + recordId,
    method: 'delete'
  })
}
