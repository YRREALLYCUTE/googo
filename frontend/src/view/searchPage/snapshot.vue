<template>
  <div>
    <div id="shot">
    </div>
  </div>
</template>

<script>
    export default {
        name: "snapshot",
      data(){
        return {
          newsId: this.$route.query.newsId,
        }
      },
        mounted(){
          this.getHtml(this.newsId);
        },
      methods:{
          getHtml(newsId){

            this.$axios.get(
              'http://localhost:8080/findByNewsId',{
                params:{
                  id: newsId
                }
              }
            ).then(res=>{
              let temp = document.getElementById("shot");
              temp.innerHTML = res.data.pagehtml;
            }).catch(error=>{
              console.log(error);
            });

          }
      }
    }
</script>

<style scoped>

</style>
