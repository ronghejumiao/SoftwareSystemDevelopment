import request from '@/utils/request'

// 查询课程信息，存储课程的基本信息列表
export function listCourse(query) {
  return request({
    url: '/system/course/list',
    method: 'get',
    params: query
  })
}

// 查询课程信息，存储课程的基本信息详细
export function getCourse(courseId) {
  return request({
    url: '/system/course/' + courseId,
    method: 'get'
  })
}

// 新增课程信息，存储课程的基本信息
export function addCourse(data) {
  return request({
    url: '/system/course',
    method: 'post',
    data: data
  })
}

// 修改课程信息，存储课程的基本信息
export function updateCourse(data) {
  return request({
    url: '/system/course',
    method: 'put',
    data: data
  })
}

// 删除课程信息，存储课程的基本信息
export function delCourse(courseId) {
  return request({
    url: '/system/course/' + courseId,
    method: 'delete'
  })
}

// AI评分
export function aiGrade(data) {
  return request({
    url: '/system/course/ai-grade',
    method: 'post',
    data: data
  })
}
