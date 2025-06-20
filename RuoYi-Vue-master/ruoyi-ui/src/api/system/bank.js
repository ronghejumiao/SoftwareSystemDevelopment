import request from '@/utils/request'

// 查询题库，存储课程的题库信息列表
export function listBank(query) {
  return request({
    url: '/system/bank/list',
    method: 'get',
    params: query
  })
}

// 查询题库，存储课程的题库信息详细
export function getBank(bankId) {
  return request({
    url: '/system/bank/' + bankId,
    method: 'get'
  })
}

// 新增题库，存储课程的题库信息
export function addBank(data) {
  return request({
    url: '/system/bank',
    method: 'post',
    data: data
  })
}

// 修改题库，存储课程的题库信息
export function updateBank(data) {
  return request({
    url: '/system/bank',
    method: 'put',
    data: data
  })
}

// 删除题库，存储课程的题库信息
export function delBank(bankId) {
  return request({
    url: '/system/bank/' + bankId,
    method: 'delete'
  })
}
