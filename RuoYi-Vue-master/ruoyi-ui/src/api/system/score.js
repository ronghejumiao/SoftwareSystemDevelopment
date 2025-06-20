import request from '@/utils/request'

// 查询成绩，记录学生的学习成绩信息列表
export function listScore(query) {
  return request({
    url: '/system/score/list',
    method: 'get',
    params: query
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
