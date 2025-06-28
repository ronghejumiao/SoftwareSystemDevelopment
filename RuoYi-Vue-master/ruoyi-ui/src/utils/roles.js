import store from '@/store'

/**
 * 判断当前用户是否为学生角色
 * @returns {boolean} 是否为学生
 */
export function isStudent() {
  const roles = store.getters && store.getters.roles
  return roles.includes('student')
}

/**
 * 判断当前用户是否为教师角色
 * @returns {boolean} 是否为教师
 */
export function isTeacher() {
  const roles = store.getters && store.getters.roles
  return roles.includes('teacher')
}

/**
 * 判断当前用户是否为管理员
 * @returns {boolean} 是否为管理员
 */
export function isAdmin() {
  const roles = store.getters && store.getters.roles
  return roles.includes('admin')
}

/**
 * 判断当前用户是否为学生或特定用户
 * @param {number} userId 用户ID
 * @returns {boolean} 是否为学生且是特定用户
 */
export function isStudentUser(userId) {
  return isStudent() && store.getters.id === userId
} 