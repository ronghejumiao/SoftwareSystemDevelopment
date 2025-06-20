import request from '@/utils/request'

// 查询学生能力，基于课程能力要求构建列表
export function listSkill(query) {
  return request({
    url: '/system/skill/list',
    method: 'get',
    params: query
  })
}

// 查询学生能力，基于课程能力要求构建详细
export function getSkill(id) {
  return request({
    url: '/system/skill/' + id,
    method: 'get'
  })
}

// 新增学生能力，基于课程能力要求构建
export function addSkill(data) {
  return request({
    url: '/system/skill',
    method: 'post',
    data: data
  })
}

// 修改学生能力，基于课程能力要求构建
export function updateSkill(data) {
  return request({
    url: '/system/skill',
    method: 'put',
    data: data
  })
}

// 删除学生能力，基于课程能力要求构建
export function delSkill(id) {
  return request({
    url: '/system/skill/' + id,
    method: 'delete'
  })
}
