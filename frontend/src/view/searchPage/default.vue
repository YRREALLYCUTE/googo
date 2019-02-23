<template>
  <div>
    <div class="firstpage_style">
      <img src="../../../static/googo.png" />
      <br>
      <el-input class="input_style" placeholder="请输入内容" @keyup.enter.native="search()" v-model="inputData"  x-webkit-speech >
        <el-tooltip slot="prepend" placement="bottom" effect="dark" content="语音输入">
          <el-button style="font-size: 25px;color: #1263e4;" class="iconfont icon-zhinengyuyin" @click="speechInput()"></el-button>
        </el-tooltip>
        <el-tooltip slot="append" content="搜索" effect="dark" placement="bottom">
          <el-button @click="search()" style="font-size: 20px;color: orangered" icon="el-icon-search"></el-button>
        </el-tooltip>
      </el-input>
    </div>
  </div>
</template>

<script>
   // import ElPopover from "element-ui/packages/popover/src/main";
    //import ElTooltip from "element-ui/packages/tooltip/src/main";

   var recognition = null;

    export default {
      name: "default",
      data(){
        return{
          inputData:''
        }
      },
      methods:{
        search(){
          this.openFullScreen();
          setTimeout(()=>{
              this.$router.push({name: 'item-list',params:{ searchMessage: this.inputData}});
            },800)

          //console.log(this.inputData)
        },

        speechInput(){
          let vm = this;
          //this.inputData="123";
          recognition = new webkitSpeechRecognition();
          recognition.start();
          recognition.onresult = function(event){

            if(event.results[0][0].confidence > 0.95){
              console.log(event.results[0][0].transcript);
              vm.inputData = event.results[0][0].transcript;
              vm.openFullScreen();
              vm.search();
            } else{
              vm.$message({
                type:'warn',
                message:'没有听清楚鸭'
              })
            }
          }
        },

        openFullScreen() {
          const loading = this.$loading({
            lock: true,
            text: '正在寻找答案',
            spinner: 'el-icon-loading',
            background: 'rgba(255, 255, 255, 0.5)'
          });
          setTimeout(() => {
            loading.close();
          }, 500);
        }

      }
    }



</script>

<style scoped>

  .firstpage_style{
    text-align: center;
    margin-top: 15%;
  }

  .input_style{
    width: 50%;
    padding-top: 35px;
  }
</style>
