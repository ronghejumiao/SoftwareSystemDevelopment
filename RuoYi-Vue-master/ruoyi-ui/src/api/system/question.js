import request from '@/utils/request'

// 查询题目，存储题库中的题目信息列表
export function listQuestion(query) {
  return request({
    url: '/system/question/list',
    method: 'get',
    params: query
  })
}

// 查询题目，存储题库中的题目信息详细
export function getQuestion(questionId) {
  return request({
    url: '/system/question/' + questionId,
    method: 'get'
  })
}

// 新增题目，存储题库中的题目信息
export function addQuestion(data) {
  return request({
    url: '/system/question',
    method: 'post',
    data: data
  })
}

// 修改题目，存储题库中的题目信息
export function updateQuestion(data) {
  return request({
    url: '/system/question',
    method: 'put',
    data: data
  })
}

// 删除题目，存储题库中的题目信息
export function delQuestion(questionId) {
  return request({
    url: '/system/question/' + questionId,
    method: 'delete'
  })
}
