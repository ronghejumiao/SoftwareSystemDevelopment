import request from '@/utils/request'

// 查询学习记录，记录学生的课程学习关联信息列表
export function listLearningRecord(query) {
  return request({
    url: '/system/learningRecord/list',
    method: 'get',
    params: query
  })
}

// 查询学习记录，记录学生的课程学习关联信息详细
export function getLearningRecord(recordId) {
  return request({
    url: '/system/learningRecord/' + recordId,
    method: 'get'
  })
}

// 新增学习记录，记录学生的课程学习关联信息
export function addLearningRecord(data) {
  return request({
    url: '/system/learningRecord',
    method: 'post',
    data: data
  })
}

// 修改学习记录，记录学生的课程学习关联信息
export function updateLearningRecord(data) {
  return request({
    url: '/system/learningRecord',
    method: 'put',
    data: data
  })
}

// 删除学习记录，记录学生的课程学习关联信息
export function delLearningRecord(recordId) {
  return request({
    url: '/system/learningRecord/' + recordId,
    method: 'delete'
  })
}
