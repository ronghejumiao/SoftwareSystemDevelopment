import request from '@/utils/request'

// 查询课程试卷库，一个课程对应一个试卷库列表
export function listLibrary(query) {
  return request({
    url: '/system/library/list',
    method: 'get',
    params: query
  })
}

// 查询课程试卷库，一个课程对应一个试卷库详细
export function getLibrary(libraryId) {
  return request({
    url: '/system/library/' + libraryId,
    method: 'get'
  })
}

// 新增课程试卷库，一个课程对应一个试卷库
export function addLibrary(data) {
  return request({
    url: '/system/library',
    method: 'post',
    data: data
  })
}

// 修改课程试卷库，一个课程对应一个试卷库
export function updateLibrary(data) {
  return request({
    url: '/system/library',
    method: 'put',
    data: data
  })
}

// 删除课程试卷库，一个课程对应一个试卷库
export function delLibrary(libraryId) {
  return request({
    url: '/system/library/' + libraryId,
    method: 'delete'
  })
}
