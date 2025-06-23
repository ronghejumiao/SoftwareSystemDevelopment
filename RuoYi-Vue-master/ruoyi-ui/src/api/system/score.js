import request from '@/utils/request'

// 查询成绩列表
export function listScore(query) {
  return request({
    url: '/system/score/list',
    method: 'get',
    params: query
  })
}

// 根据用户ID和课程ID查询成绩
export function getScoreByUserAndCourse(userId, courseId) {
  return request({
    url: '/system/score/user/' + userId + '/course/' + courseId,
    method: 'get'
  })
}

// 根据用户ID查询所有成绩
export function getScoreByUserId(userId) {
  return request({
    url: '/system/score/user/' + userId,
    method: 'get'
  })
}

// 查询成绩，记录学生的学习成绩信息详细
export function getScore(scoreId) {
  return request({
    url: '/system/score/' + scoreId,
    method: 'get'
  })
}

// 新增成绩，记录学生的学习成绩信息
export function addScore(data) {
  return request({
    url: '/system/score',
    method: 'post',
    data: data
  })
}

// 修改成绩，记录学生的学习成绩信息
export function updateScore(data) {
  return request({
    url: '/system/score',
    method: 'put',
    data: data
  })
}

// 删除成绩，记录学生的学习成绩信息
export function delScore(scoreId) {
  return request({
    url: '/system/score/' + scoreId,
    method: 'delete'
  })
}
