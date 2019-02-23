<template>
    <el-card>
      god , this is a test ! let me run it ok !

      <br>
      <el-button @click="testAxios()">test123</el-button>
    </el-card>

</template>

<script>
    export default {
        name: "test",
      data(){
          return{
            pageNum:1,
            pageSize:2
          }
      },
      methods:{
          testAxios(){
            this.$axios.get(
              'http://localhost:8080/user/all',{
                params: {
                  pageNum: this.pageNum,
                  pageSize: this.pageSize
                }
              }
            ).then(res=>{
              console.log(res.data)
            })
          }
      }
    }
</script>

<style scoped>

</style>
