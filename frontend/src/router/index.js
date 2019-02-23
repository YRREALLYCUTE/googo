import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'

Vue.use(Router);

export default new Router({
  routes: [
    {
      path: '/HelloWorld',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path:'/test',
      name:'test',
      component:resolve=>require(["@/view/test/test"],resolve)
    },
    {
      path:'/',
      name:'default',
      component:resolve=>require(["@/view/searchPage/default"],resolve)
    },
    {
      path:'/itemList',
      name:'item-list',
      component:resolve=>require(["@/view/searchPage/itemList"],resolve)
    },
    {
      path:'/snapshot',
      name:'snapshot',
      component:resolve=>require(["@/view/searchPage/snapshot"],resolve)
    }
  ]
})
