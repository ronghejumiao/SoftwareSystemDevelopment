import request from '@/utils/request'

// 查询任务提交记录，记录学生提交任务的信息列表
export function listSubmission(query) {
  console.log('发送任务提交列表查询:', query)
  return request({
    url: '/system/submission/list',
    method: 'get',
    params: query
  }).then(res => { console.log('任务提交列表响应:', res); return res; }).catch(err => { console.error('任务提交查询失败:', err); throw err; })
}

// 查询任务提交记录，记录学生提交任务的信息详细
export function getSubmission(submissionId) {
  return request({
    url: '/system/submission/' + submissionId,
    method: 'get'
  })
}

// 新增任务提交记录，记录学生提交任务的信息
export function addSubmission(data) {
  return request({
    url: '/system/submission',
    method: 'post',
    data: data
  })
}

// 修改任务提交记录，记录学生提交任务的信息
export function updateSubmission(data) {
  return request({
    url: '/system/submission',
    method: 'put',
    data: data
  })
}

// 删除任务提交记录，记录学生提交任务的信息
export function delSubmission(submissionId) {
  return request({
    url: '/system/submission/' + submissionId,
    method: 'delete'
  })
}
