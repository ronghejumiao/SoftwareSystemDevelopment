import request from '@/utils/request'

// 查询视频学习记录，记录学生观看视频的行为数据列表
export function listRecord(query) {
  console.log('发送视频学习记录查询请求，参数:', query);
  return request({
    url: '/system/videoLearningRecord/list',
    method: 'get',
    params: query
  }).then(response => {
    console.log('视频学习记录查询响应:', response);
    return response;
  }).catch(error => {
    console.error('视频学习记录查询失败:', error);
    throw error;
  });
}

// 查询视频学习记录，记录学生观看视频的行为数据详细
export function getRecord(recordId) {
  console.log('获取视频学习记录详情，ID:', recordId);
  return request({
    url: '/system/videoLearningRecord/' + recordId,
    method: 'get'
  });
}

// 新增视频学习记录，记录学生观看视频的行为数据
export function addRecord(data) {
  console.log('新增视频学习记录，数据:', data);
  return request({
    url: '/system/videoLearningRecord',
    method: 'post',
    data: data
  });
}

// 修改视频学习记录，记录学生观看视频的行为数据
export function updateRecord(data) {
  console.log('更新视频学习记录，数据:', data);
  return request({
    url: '/system/videoLearningRecord',
    method: 'put',
    data: data
  });
}

// 删除视频学习记录，记录学生观看视频的行为数据
export function delRecord(recordId) {
  console.log('删除视频学习记录，ID:', recordId);
  return request({
    url: '/system/videoLearningRecord/' + recordId,
    method: 'delete'
  });
}

// 根据当前用户查询观看记录
export function listRecordByUser(userId, query = {}) {
  return request({
    url: '/system/videoLearningRecord/list',
    method: 'get',
    params: { ...query, userId }
  })
}
