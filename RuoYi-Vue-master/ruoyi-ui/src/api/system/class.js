import request from '@/utils/request'

// 查询学生班级关联，实现学生与班级的多对多关系列表
export function listClass(query) {
  return request({
    url: '/system/class/list',
    method: 'get',
    params: query
  })
}

// 查询学生班级关联，实现学生与班级的多对多关系详细
export function getClass(id) {
  return request({
    url: '/system/class/' + id,
    method: 'get'
  })
}

// 新增学生班级关联，实现学生与班级的多对多关系
export function addClass(data) {
  return request({
    url: '/system/class',
    method: 'post',
    data: data
  })
}

// 修改学生班级关联，实现学生与班级的多对多关系
export function updateClass(data) {
  return request({
    url: '/system/class',
    method: 'put',
    data: data
  })
}

// 删除学生班级关联，实现学生与班级的多对多关系
export function delClass(id) {
  return request({
    url: '/system/class/' + id,
    method: 'delete'
  })
}
