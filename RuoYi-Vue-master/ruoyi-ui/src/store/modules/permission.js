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
        // 向后端请求路由数据
        getRouters().then(res => {
          const sdata = JSON.parse(JSON.stringify(res.data))
          const rdata = JSON.parse(JSON.stringify(res.data))
          const sidebarRoutes = filterAsyncRouter(sdata)
          const rewriteRoutes = filterAsyncRouter(rdata, false, true)
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
          commit('SET_TOPBAR_ROUTES', sidebarRoutes)
          resolve(rewriteRoutes)
        })
      })
    }
  }
}

// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  // 使用静态集合避免后台返回的同名或同路径路由重复
  const nameSet = new Set()
  const pathSet = new Set()

  const handler = route => {
    // 去重：如果 name 或 path 已存在，直接跳过
    if ((route.name && nameSet.has(route.name)) || (route.path && pathSet.has(route.path))) {
      return false
    }
    if (route.name) nameSet.add(route.name)
    if (route.path) pathSet.add(route.path)

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
