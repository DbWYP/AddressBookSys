// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css' /* 引入css样式*/
import axios from "axios"

//配置全局的后台服务域名，作用：在axios发送后台接口请求时，不需要添加后台域名，axios会自动根据baseURL添加
axios.defaults.baseURL="http://localhost:8088"

axios.interceptors.request.use(config =>{
  //给请求头设置请求参数的数据格式为json
  config.headers['Content-Type'] = 'application/json;charset=utf-8';
  if(sessionStorage.getItem("token") != null){
    config.headers["Authorization"] = sessionStorage.getItem("token");
  }
  return config;
}, err => Promise.reject())

//定义全局axios变量，变量名就是$axios
Vue.prototype.$axios = axios

Vue.config.productionTip = false

Vue.use(ElementUI)
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
