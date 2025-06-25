import request from '@/utils/request'

// 查询学习记录列表
export function listLearningRecord(query) {
  return request({
    url: '/system/record/list',
    method: 'get',
    params: query
  })
}

// 根据用户ID和课程ID查询学习记录，若存在仅返回首条记录对象，否则返回 null
export async function getLearningRecordByUserAndCourse(userId, courseId) {
  const res = await request({
    url: '/system/record/list',
    method: 'get',
    params: {
      userId,
      courseId,
      pageNum: 1,
      pageSize: 1
    }
  })
  // RuoYi 列表接口返回 { rows: [], total: n }
  if (res && res.rows && res.rows.length > 0) {
    return res.rows[0]
  }
  return null
}

// 查询学习记录详细
export function getLearningRecord(recordId) {
  return request({
    url: '/system/record/' + recordId,
    method: 'get'
  })
}

// 新增学习记录
export function addLearningRecord(data) {
  if (!data.joinTime) {
    data.joinTime = new Date();
  }
  return request({
    url: '/system/record',
    method: 'post',
    data: data
  })
}

// 修改学习记录
export function updateLearningRecord(data) {
  return request({
    url: '/system/record',
    method: 'put',
    data: data
  })
}

// 删除学习记录
export function delLearningRecord(recordId) {
  return request({
    url: '/system/record/' + recordId,
    method: 'delete'
  })
}
