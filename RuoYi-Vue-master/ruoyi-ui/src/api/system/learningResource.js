import request from '@/utils/request'

// 查询内容摘要
export function getResourceAnalysis(resourceId) {
  console.log('[DEBUG] getResourceAnalysis 调用 resourceId:', resourceId);
  return request({
    url: '/system/resource/analysis/' + resourceId,
    method: 'get'
  })
}
// 触发AI分析
export function analyzeResource(resourceId) {
  console.log('[DEBUG] analyzeResource 调用 resourceId:', resourceId);
  return request({
    url: '/system/resource/analyze/' + resourceId,
    method: 'post'
  })
}
// 批量AI内容分析
export function batchAnalyzeResource() {
  return request({
    url: '/system/resource/batchAnalyze',
    method: 'post'
  })
}
// AI课程智能推荐
export function recommendResource(studentId, courseId, forceRefresh = false) {
  return request({
    url: `/system/resource/recommend/${studentId}/${courseId}` + (forceRefresh ? `?forceRefresh=true` : ''),
    method: 'get'
  })
} 