<template>
  <div class="searchItem_style">

    <el-card style="width: 100%; height: 100px; position: fixed; top: 0;" shadow="hover">
      <el-row>
        <el-col :span="2">
          <div>
            <img width="80%" height="80%" src="../../../static/googo.png"/>
          </div>
        </el-col>
        <el-col :span="11">
          <el-input v-model="searchMessage" @keyup.enter.native="search(searchMessage)"  style="width: 80%">
            <el-button slot="append" style="font-size: 20px;color: orangered" icon="el-icon-search" @click="search(searchMessage)"></el-button>
            <el-button slot="append" style="font-size: 25px;color: #1263e4;" class="iconfont icon-zhinengyuyin" @click="speechInput()"></el-button>
          </el-input>
        </el-col>
      </el-row>
      <div style="margin-left: 7%">
        <el-tabs v-model="newType" @tab-click = "handleClick">
          <el-tab-pane label="全部" name="all"></el-tab-pane>
          <el-tab-pane label="信息门户" name="urp_news"></el-tab-pane>
          <el-tab-pane label="物理" name="cap_news"></el-tab-pane>
          <el-tab-pane label="哲学" name="cz_news"></el-tab-pane>
          <el-tab-pane label="部门公告" name="dep_news"></el-tab-pane>
          <el-tab-pane label="外国语" name="eng_news"></el-tab-pane>
          <el-tab-pane label="汉语" name="hyxy_news"></el-tab-pane>
          <el-tab-pane label="法学" name="law_news"></el-tab-pane>
          <el-tab-pane label="通知" name="nk_news"></el-tab-pane>
          <el-tab-pane label="文学" name="wxy_news"></el-tab-pane>
        </el-tabs>
      </div>
    </el-card>
    <!--  -----123546-----
    <el-card>
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="urp" name="first">用户管理</el-tab-pane>
        <el-tab-pane label="hyxy" name="second">配置管理</el-tab-pane>
        <el-tab-pane label="cz" name="third">角色管理</el-tab-pane>
        <el-tab-pane label="wxy" name="fourth">定时任务补偿</el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 11111 -->
    <el-card style="margin-top: 100px;width: 100%;" shadow="never">
      <div v-for="item in result" class="itemList">

        <el-collapse-transition>
          <div v-show = "show">
            <div class="news_title">
              <a v-bind:href="item.newsurl" style="text-decoration: none;" target="_blank" class="title_highlight">{{item.title}}</a>
            </div>
            <div class="news_url">
              <el-tooltip v-if="item.newsurl!=null" content="快照" effect="dark" placement="bottom">
                <i style="cursor: pointer;color: #1ac315; margin-left: 10px" class="el-icon-caret-bottom" @click="snapshot(item.id)"></i>
              </el-tooltip>
              <a v-bind:href="item.newsurl" target="_blank">{{item.newsurl}}</a>
              <span style="font-size: smaller; color: darkgray">-</span>
              <el-tooltip v-if="item.newsurl!=null && item.speechStop === false " content="听新闻" effect="dark" placement="bottom">
                <i style="cursor: pointer;color: #0d9eff; margin-left: 2px" class="el-icon-service" @click="speakNews(item)"></i>
              </el-tooltip>
              <el-tooltip v-if="item.newsurl!=null && item.speechStop === true" content="停止" effect="dark" placement="bottom">
                <i style="cursor: pointer;color: #0d9eff; margin-left: 2px" class="el-icon-close" @click="speakNewsStop(item)"></i>
              </el-tooltip>
              <el-popover
                v-if="item.speechStop===true"
                placement="bottom"
                title="新闻内容"
                width="400"
                height="200"
                trigger="hover"
                style="color: #a1a1a1"
                v-bind:content="item.content"
              >
                <i slot="reference" style="color: #c2c2c2; margin-left: 2px" class="el-icon-document"></i>
              </el-popover>
            </div>

            <div class="news_content">

              <span class="time">
                <i class="el-icon-time" v-if="item.time!=null"></i>
                <span v-if="item.time != 'null'">{{item.time}}</span>
                <span v-else>--/--/--</span>
              </span>
              <span> &nbsp;&nbsp;</span>
              <span class="content_highlight">
              {{item.highlightContent}}
              </span>
            </div>
          </div>
        </el-collapse-transition>
      </div>
      <div style="position: fixed;bottom: 0; margin-left: 135px;background-color: white;width: 100%;">
        <el-row>
          <el-col :span="6">
            <div class="block" >
              <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page.sync="currentPage"
                :page-size="10"
                layout="total, prev, pager, next"
                :total="resultTotal">
              </el-pagination>
            </div>
          </el-col>
          <el-col :span="5" style="margin-top: 5px">
            <span class="time" >当前 {{currentPage}} 页</span>
          </el-col>
        </el-row>
      </div>
    </el-card>


  </div>
</template>

<script>
  import ElCollapseTransition from "element-ui/src/transitions/collapse-transition";

  var recognition = null;
  var tmp = '';
  var speaker = new window.SpeechSynthesisUtterance();
    export default {
        name: "item-list",
      data(){
          return{
            newSearchMessage:'',
            show:true,
            newType:'all',
            url:'http https',
            currentPage: 1,
            resultTotal:0,
            result:[{
              title:'没有结果。。。',
              newsurl:null,
              highlightContent:'没有结果。。。',
              time:'--/--/--',
              speechStop:false,
            },{},{},{},{},{},{},{},{},{}],
            searchMessage: this.$route.params.searchMessage,
          }
      },
      mounted(){
        this.search(this.searchMessage)
      },

      methods:{
        search(searchMessage){
          let real = searchMessage;
          this.newSearchMessage = searchMessage;
          if(tmp !== this.newSearchMessage){
            this.currentPage = 1;
          }

          if(real.split(":")[0] === "site"){
            let temp = real.split(":")[1];
            this.url = temp.split(" ")[0];
            real = temp.split(" ")[1];
          }

          this.$axios.get(
            'http://localhost:8080/search',{
              params:{
                searchMessage:real,
                page: this.currentPage-1,
                size: 10,
                newType:this.newType,
                url:this.url
              }
            }
          ).then(res=>{
            if(res.data!= null){
              this.result = res.data;
              this.resultTotal = res.data[0].total;
              if(res.data.length < 10){
                let i = 10 - res.data.length;
                for(let j = 0; j < i; j++){
                  this.result.push({title:'',time:null,highlightContent:'',newsurl:null});
                }
              }
              let arryTmp = [];
              this.result.map(((item,index)=>{
                arryTmp.push(Object.assign({},item,{speechStop:false}))
              }));
              this.result = arryTmp;
              console.log(this.result);
              this.highlightWords(res.data);
            }else{
              this.$message({
                type:'error',
                message:'网络连接错误'
              })
            }
          }).catch(error=>{
            console.log(error)
          });
          tmp = searchMessage;
          console.log(searchMessage);
          this.url='http https';
        },

        speechInput(){
          let vm = this;
          recognition = new webkitSpeechRecognition();
          recognition.start();
          recognition.onresult = function(event){

            if(event.results[0][0].confidence > 0.95){
              console.log(event.results[0][0].transcript);
              vm.searchMessage = event.results[0][0].transcript;
              vm.openFullScreen();
              setTimeout(() => {
                vm.search(vm.searchMessage);
              }, 500);
            } else{
              vm.$message({
                type:'warn',
                message:'没有听清楚鸭'
              })
            }
          }
        },

        handleSizeChange(val) {
          console.log(`每页 ${val} 条`);
        },
        handleCurrentChange(val) {
          this.search(this.searchMessage)
          console.log(`当前页: ${val}`);
        },

        highlightWords(data){
          let title = document.getElementsByClassName("title_highlight");
          let content = document.getElementsByClassName("content_highlight");
          let i = 0;
          for(i = 0 ; i < 10; i++){
            title.item(i).innerHTML = data[i].title;
            content.item(i).innerHTML = data[i].highlightContent;
          }
        },

        speakNews(item){
          item.speechStop = true;
          speaker.text = item.content;
          window.speechSynthesis.speak(speaker);
          console.log("speak news");
        },

        speakNewsStop(item){
          item.speechStop = false;
          window.speechSynthesis.cancel();
        },

        snapshot(id) {
          console.log(id);
          const {href} = this.$router.resolve({
            name: 'snapshot',
            path:'/snapshot',
            query: {
              newsId:id
            }
          });
          window.open(href, '_blank')
          //this.$router.push({name:'snapshot',params:{newsHtml:html}});
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
        },
        handleClick(){
          this.currentPage = 1;
          this.search(this.searchMessage);
        }
      },
      components:{
        ElCollapseTransition,

      }
    }
</script>

<style scoped>

  .time{
    font-size: 10px;
    color: darkgray;
  }

  .itemList{
    margin-left: 140px;
    margin-bottom: 25px;
    width: 750px;
  }

  .news_title{
    font-weight: bold;
    font-family: 等线;
    font-size: 18px;
    margin-bottom: 2px;
  }

  .news_content{
    font-size: 16px;
    font-family: "微软雅黑 Light";
  }

  .news_url{
    font-size: small;
    margin-top:1px;
    margin-bottom: 1px;
  }
  .title_highlight{
  }

  .content_highlight{
  }

</style>
