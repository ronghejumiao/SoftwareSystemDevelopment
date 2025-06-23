import request from '@/utils/request'

// 查询试卷，一个试卷库包含多个试卷列表
export function listPaper(query) {
  return request({
    url: '/system/paper/list',
    method: 'get',
    params: query
  })
}

// 查询试卷，一个试卷库包含多个试卷详细
export function getPaper(paperId) {
  return request({
    url: '/system/paper/' + paperId,
    method: 'get'
  })
}

// 新增试卷，一个试卷库包含多个试卷
export function addPaper(data) {
  return request({
    url: '/system/paper',
    method: 'post',
    data: data
  })
}

// 修改试卷，一个试卷库包含多个试卷
export function updatePaper(data) {
  return request({
    url: '/system/paper',
    method: 'put',
    data: data
  })
}

// 删除试卷，一个试卷库包含多个试卷
export function delPaper(paperId) {
  return request({
    url: '/system/paper/' + paperId,
    method: 'delete'
  })
}

// 根据课程ID查询试卷列表
export function listPaperByCourseId(courseId) {
  return request({
    url: '/system/paper/course/' + courseId,
    method: 'get'
  })
}
