import request from '@/utils/request'

// 查询课程能力要求，一个课程包含多个能力要求列表
export function listRequirement(query) {
  return request({
    url: '/system/requirement/list',
    method: 'get',
    params: query
  })
}

// 查询课程能力要求，一个课程包含多个能力要求详细
export function getRequirement(requirementId) {
  return request({
    url: '/system/requirement/' + requirementId,
    method: 'get'
  })
}

// 新增课程能力要求，一个课程包含多个能力要求
export function addRequirement(data) {
  return request({
    url: '/system/requirement',
    method: 'post',
    data: data
  })
}

// 修改课程能力要求，一个课程包含多个能力要求
export function updateRequirement(data) {
  return request({
    url: '/system/requirement',
    method: 'put',
    data: data
  })
}

// 删除课程能力要求，一个课程包含多个能力要求
export function delRequirement(requirementId) {
  return request({
    url: '/system/requirement/' + requirementId,
    method: 'delete'
  })
}
