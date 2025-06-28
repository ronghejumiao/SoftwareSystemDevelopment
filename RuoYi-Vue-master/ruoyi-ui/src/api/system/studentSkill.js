import request from '@/utils/request'

// 查询学生能力列表
export function listStudentSkill(query) {
  return request({
    url: '/system/skill/list',
    method: 'get',
    params: query
  })
}

// 查询学生能力详细
export function getStudentSkill(id) {
  return request({
    url: '/system/skill/' + id,
    method: 'get'
  })
}

// 新增学生能力
export function addStudentSkill(data) {
  return request({
    url: '/system/skill',
    method: 'post',
    data: data
  })
}

// 修改学生能力
export function updateStudentSkill(data) {
  return request({
    url: '/system/skill',
    method: 'put',
    data: data
  })
}

// 删除学生能力
export function delStudentSkill(id) {
  return request({
    url: '/system/skill/' + id,
    method: 'delete'
  })
}

// 根据学生ID和课程ID查询学生能力
export function getStudentSkillByStudentAndCourse(studentId, courseId) {
  return request({
    url: '/system/skill/student/' + studentId + '/course/' + courseId,
    method: 'get'
  })
}

// 初始化学生课程能力（为缺失的能力要求创建记录）
export function initStudentCourseSkills(studentId, courseId) {
  return request({
    url: '/system/skill/init',
    method: 'post',
    data: {
      studentId: studentId,
      courseId: courseId
    }
  })
} 