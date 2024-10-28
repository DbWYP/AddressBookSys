import Vue from 'vue'
import Router from 'vue-router'
import Index from '@/components/index/Index'
import AddressBook from '@/components/addressBook/AddressBook'


Vue.use(Router)

export default new Router({
  routes: [{
    path: '/',
    name: 'index',
    component: AddressBook
  }]
})

// 解决Vue-Router升级导致的Uncaught(in promise) navigation guard问题
// push
const originalPush = Router.prototype.push
Router.prototype.push = function push(location, onResolve, onReject) {
  //第一个形参：路由跳转的配置对象（query|params）
  //第二个参数：undefined|箭头函数（成功的回调）
  //第三个参数:undefined|箭头函数（失败的回调）
  if (onResolve || onReject) {
    return originalPush.call(this, location, onResolve, onReject);
  }
  return originalPush.call(this, location).catch(err => err)
}
// replace
const originalReplace = Router.prototype.replace
Router.prototype.replace= function replace(location) {
  return originalReplace.call(this, location).catch(err => err)
}

