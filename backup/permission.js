import auth from '@/plugins/auth'
import router, { constantRoutes, dynamicRoutes } from '@/router'
import { getRouters } from '@/api/menu'
import Layout from '@/layout/index'
import ParentView from '@/components/ParentView'
import InnerLink from '@/layout/components/InnerLink'
import { isExternal } from '@/utils/validate'

const permission = {
  state: {
    routes: [],
    addRoutes: [],
    defaultRoutes: [],
    topbarRouters: [],
    sidebarRouters: []
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.addRoutes = routes
      state.routes = constantRoutes.concat(routes)
    },
    SET_DEFAULT_ROUTES: (state, routes) => {
      state.defaultRoutes = constantRoutes.concat(routes)
    },
    SET_TOPBAR_ROUTES: (state, routes) => {
      state.topbarRouters = routes
    },
    SET_SIDEBAR_ROUTERS: (state, routes) => {
      state.sidebarRouters = routes
    },
  },
  actions: {
    // 生成路由
    GenerateRoutes({ commit }) {
      return new Promise(resolve => {
        // 清空全局集合，确保每次路由生成都是干净的
        globalNameSet.clear()
        globalPathSet.clear()
        
        // 向后端请求路由数据
        getRouters().then(res => {
          console.log('Backend routes data:', res.data)
          
          const sdata = JSON.parse(JSON.stringify(res.data))
          const rdata = JSON.parse(JSON.stringify(res.data))
          
          console.log('Processing sidebar routes...')
          const sidebarRoutes = filterAsyncRouter(sdata)
          console.log('Sidebar routes result:', sidebarRoutes)
          
          console.log('Processing rewrite routes...')
          const rewriteRoutes = filterAsyncRouter(rdata, false, true)
          console.log('Rewrite routes result:', rewriteRoutes)
          
          let asyncRoutes = filterDynamicRoutes(dynamicRoutes)

          // 额外去重，防止后台返回的路由与前端静态/动态路由重名
          asyncRoutes = dedupeRouteList(asyncRoutes, router.options.routes || [])

          rewriteRoutes.push({ path: '*', redirect: '/404', hidden: true })

          // 修正外链/无斜杠路径，防止 Vue Router 报警
          asyncRoutes.forEach(fixRoutePath)

          router.addRoutes(asyncRoutes)
          commit('SET_ROUTES', rewriteRoutes)
          commit('SET_SIDEBAR_ROUTERS', constantRoutes.concat(sidebarRoutes))
          commit('SET_DEFAULT_ROUTES', sidebarRoutes)
          commit('SET_TOPBAR_ROUTERS', sidebarRoutes)
          resolve(rewriteRoutes)
        })
      })
    }
  }
}

// 全局路由名称和路径集合，用于去重
const globalNameSet = new Set()
const globalPathSet = new Set()

// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  const handler = route => {
    // 生成唯一的路由名称
    let routeName = route.name
    let routePath = route.path
    
    // 如果路由名称为空，尝试从路径生成
    if (!routeName && routePath) {
      routeName = generateRouteName(routePath)
    }
    
    // 检查名称和路径是否重复
    if (routeName && globalNameSet.has(routeName)) {
      // 如果名称重复，生成新的唯一名称
      routeName = generateUniqueName(routeName)
    }
    
    // 优化路径重复检测逻辑
    if (routePath) {
      // 标准化路径格式
      const normalizedPath = normalizePath(routePath)
      
      if (globalPathSet.has(normalizedPath)) {
        // 如果路径重复，记录详细信息并跳过
        console.warn(`Duplicate route path detected: ${routePath} (normalized: ${normalizedPath}), skipping...`)
        console.warn('Route details:', {
          name: routeName,
          path: routePath,
          component: route.component,
          children: route.children ? route.children.length : 0
        })
        return false
      }
      
      // 添加到全局集合
      globalPathSet.add(normalizedPath)
    }
    
    // 添加到全局集合
    if (routeName) {
      globalNameSet.add(routeName)
      route.name = routeName
    }

    if (type && route.children) {
      route.children = filterChildren(route.children)
    }
    if (route.component) {
      // Layout ParentView 组件特殊处理
      if (route.component === 'Layout') {
        route.component = Layout
      } else if (route.component === 'ParentView') {
        route.component = ParentView
      } else if (route.component === 'InnerLink') {
        route.component = InnerLink
      } else {
        route.component = loadView(route.component)
      }
    }
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children, route, type)
    } else {
      delete route['children']
      delete route['redirect']
    }
    return true
  }

  return asyncRouterMap.filter(handler)
}

// 标准化路径格式
function normalizePath(path) {
  if (!path) return ''
  
  // 移除开头的斜杠
  let normalized = path.startsWith('/') ? path.substring(1) : path
  
  // 移除结尾的斜杠
  normalized = normalized.endsWith('/') ? normalized.slice(0, -1) : normalized
  
  // 转换为小写，确保一致性
  return normalized.toLowerCase()
}

// 生成路由名称
function generateRouteName(path) {
  if (!path) return null
  
  // 移除开头的斜杠
  let name = path.startsWith('/') ? path.substring(1) : path
  
  // 将路径转换为驼峰命名
  name = name.split('/').map(segment => {
    return segment.charAt(0).toUpperCase() + segment.slice(1)
  }).join('')
  
  return name
}

// 生成唯一的路由名称
function generateUniqueName(baseName) {
  let counter = 1
  let uniqueName = `${baseName}${counter}`
  
  while (globalNameSet.has(uniqueName)) {
    counter++
    uniqueName = `${baseName}${counter}`
  }
  
  return uniqueName
}

function filterChildren(childrenMap, lastRouter = false) {
  var children = []
  childrenMap.forEach(el => {
    el.path = lastRouter ? lastRouter.path + '/' + el.path : el.path
    if (el.children && el.children.length && el.component === 'ParentView') {
      children = children.concat(filterChildren(el.children, el))
    } else {
      children.push(el)
    }
  })
  return children
}

// 动态路由遍历，验证是否具备权限
export function filterDynamicRoutes(routes) {
  const res = []
  routes.forEach(route => {
    if (route.permissions) {
      if (auth.hasPermiOr(route.permissions)) {
        res.push(route)
      }
    } else if (route.roles) {
      if (auth.hasRoleOr(route.roles)) {
        res.push(route)
      }
    }
  })
  return res
}

export const loadView = (view) => {
  // 去除可能的首尾空白，防止"Cannot find module"
  view = view && view.trim ? view.trim() : view;
  
  // 确保路径正确格式化，防止加载失败
  if (view && view.startsWith('/')) {
    view = view.substring(1);
  }
  
  if (process.env.NODE_ENV === 'development') {
    return (resolve) => require([`@/views/${view}`], resolve)
  } else {
    // 使用 import 实现生产环境的路由懒加载
    return () => import(`@/views/${view}`)
  }
}

// -------------------------------------
// 工具函数：去重复、修正 path
function dedupeRouteList(newRoutes, existRoutes) {
  const nameSet = new Set()
  const loop = routes => {
    routes.forEach(r => {
      if (r.name) nameSet.add(r.name)
      if (r.children) loop(r.children)
    })
  }
  loop(existRoutes)

  const filterRecursive = routes => {
    const res = []
    routes.forEach(r => {
      // 如果已有同名路由，则跳过
      if (r.name && nameSet.has(r.name)) return
      if (r.name) nameSet.add(r.name)
      const clone = { ...r }
      if (clone.children) clone.children = filterRecursive(clone.children)
      res.push(clone)
    })
    return res
  }
  return filterRecursive(newRoutes)
}

function fixRoutePath(route) {
  if (route.path && !route.path.startsWith('/') && !isExternal(route.path)) {
    route.path = '/' + route.path
  }
  if (route.children && route.children.length) {
    route.children.forEach(fixRoutePath)
  }
}

export default permission
