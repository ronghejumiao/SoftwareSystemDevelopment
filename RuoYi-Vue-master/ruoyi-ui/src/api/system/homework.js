import request from '@/utils/request'

// 查询课程作业列表
export function listHomework(query) {
  return request({
    url: '/system/homework/list',
    method: 'get',
    params: query
  })
}

// 查询作业详情
export function getHomework(homeworkId) {
  return request({
    url: '/system/homework/' + homeworkId,
    method: 'get'
  })
}

// 新增作业
export function addHomework(data) {
  return request({
    url: '/system/homework',
    method: 'post',
    data: data
  })
}

// 修改作业
export function updateHomework(data) {
  return request({
    url: '/system/homework',
    method: 'put',
    data: data
  })
}

// 删除作业
export function delHomework(homeworkId) {
  return request({
    url: '/system/homework/' + homeworkId,
    method: 'delete'
  })
}

// 获取用户作业完成情况
export function getUserHomeworkStatus(courseId, userId) {
  return request({
    url: '/system/homework/user/status',
    method: 'get',
    params: { courseId, userId }
  })
}

// 提交作业
export function submitHomework(data) {
  return request({
    url: '/system/homework/submit',
    method: 'post',
    data: data
  })
}

// 上传作业附件
export function uploadHomeworkFile(data) {
  return request({
    url: '/system/homework/upload',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
} 