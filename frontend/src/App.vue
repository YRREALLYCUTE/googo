<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  /*text-align: center;*/
  color: #2c3e50;
  width: 100%;
  margin: auto auto 0 auto;
}

/* 手机 */
@media (max-width: 319px){
  #app {
    width: 100%;
  }
}

/* 小屏幕（平板，大于等于 768px） */
@media (min-width: 320px) and (max-width: 991px) {
  #app {
    width: 95%;
  }
}

/* 中等屏幕（桌面显示器，大于等于 992px） */
@media (min-width: 992px) and (max-width: 1309px) {
  #app {
    width: 90%;
  }
}



/* 大屏幕（大桌面显示器，大于等于 1200px） */
@media (min-width: 1310px) and (max-width: 1600px) {
  #app {
    width: 85%;
  }
}
</style>
