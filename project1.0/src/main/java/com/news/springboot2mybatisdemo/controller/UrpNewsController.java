package com.news.springboot2mybatisdemo.controller;

//import com.github.pagehelper.Page;
import Jama.Matrix;
import com.news.springboot2mybatisdemo.dao.UrpNewsDao;
import com.news.springboot2mybatisdemo.model.UrpNews;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
public class UrpNewsController {

    @Autowired
    private UrpNewsDao urpNewsDao;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ElasticsearchRepository elasticsearchRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @RequestMapping("/findByNewsId")
    @CrossOrigin
    public UrpNews find(@RequestParam Integer id){
        UrpNews urpNews = urpNewsDao.queryUrpNewsById(id);
        System.out.println(urpNews.getNewsurl());
        return urpNews;
    }

    @RequestMapping("/findByTitle")
    public List<UrpNews> findByTitle(@RequestParam String title){
        List<UrpNews> urpNews = urpNewsDao.queryAllByTitleLike(title);

        return urpNews;
    }

    @RequestMapping("/test")
    public List<UrpNews> highlightQuery(@RequestParam String field,@RequestParam String type, @RequestParam String SearchMessage, @RequestParam  int pageNum, @RequestParam int pageSize){

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(field,SearchMessage)))
                .withPageable(PageRequest.of(pageNum,pageSize))
                .withHighlightFields(new HighlightBuilder.Field(field).preTags("<span style='color: red'>").postTags("</span>"))
                .build();

        Page<UrpNews> page = elasticsearchTemplate.queryForPage(searchQuery, UrpNews.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                ArrayList<UrpNews> urpNewes = new ArrayList<UrpNews>();
                SearchHits hits = searchResponse.getHits();

                for(SearchHit searchHit : hits){
                    if(hits.getHits().length == 0){
                        return null;
                    }
                    else if(searchHit.getSourceAsMap()!=null) {
                        UrpNews urpNews1 = new UrpNews();
                        String highLightMessage = String.valueOf(searchHit.getSourceAsMap().get(field));

                        if(searchHit.getHighlightFields().size() != 0)
                            highLightMessage = searchHit.getHighlightFields().get(field).fragments()[0].toString();

                        urpNews1.setId(Integer.parseInt(searchHit.getId()));
                        urpNews1.setTitle(String.valueOf(searchHit.getSourceAsMap().get("title")));
                        urpNews1.setContent(String.valueOf(searchHit.getSourceAsMap().get("content")));
                        urpNews1.setNewsurl(String.valueOf(searchHit.getSourceAsMap().get("newsurl")));
                        urpNews1.setPagehtml(String.valueOf(searchHit.getSourceAsMap().get("pagehtml")));
                        urpNews1.setTime(String.valueOf(searchHit.getSourceAsMap().get("time")));
                        urpNews1.setTotal(hits.getHits().length);
                        urpNews1.setNewstype(String.valueOf(searchHit.getSourceAsMap().get("newstype")));
                        try {
                            String setMethodName = parSetName(field);
                            Class<? extends UrpNews> urpNewsClazz = urpNews1.getClass();
                            Method setMethod = urpNewsClazz.getMethod(setMethodName, String.class);
                            setMethod.invoke(urpNews1, highLightMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        urpNewes.add(urpNews1);
                    }
                }
                if(urpNewes.size()>0)
                    return new AggregatedPageImpl <T>((List<T>) urpNewes);
                return null;
            }


        });

        return page.getContent();
    }


    @RequestMapping("/search")
    @CrossOrigin
    public List<UrpNews> test2(@RequestParam String url, @RequestParam String newType,@RequestParam String searchMessage, @PageableDefault (sort = "_score" ,direction = Sort.Direction.DESC) Pageable pageable){

        NativeSearchQuery searchQuery;
        if( newType.equals("all")) {
             searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.boolQuery().must(QueryBuilders.multiMatchQuery(searchMessage, "title", "content").analyzer("ik_smart"))
                            .must(QueryBuilders.matchQuery("newsurl",url).operator(Operator.OR))
                    )
                    .withHighlightFields(new HighlightBuilder
                                    .Field("title")
                                    .preTags("<span style = 'color : red;'>")
                                    .postTags("</span>"),
                            new HighlightBuilder
                                    .Field("content")
                                    .preTags("<span style = 'color:red;'>")
                                    .postTags("</span>"))
                    .withPageable(pageable)
                    .build();
        }else {
            searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.multiMatchQuery(searchMessage, "title", "content").analyzer("ik_smart"))
                            .must(QueryBuilders.matchQuery("newsurl",url).operator(Operator.OR))
                            .must(QueryBuilders.termQuery("newstype",newType))
                    )
                    .withHighlightFields(new HighlightBuilder
                                    .Field("title")
                                    .preTags("<span style = 'color : red;'>")
                                    .postTags("</span>"),
                            new HighlightBuilder
                                    .Field("content")
                                    .preTags("<span style = 'color:red;'>")
                                    .postTags("</span>"))
                    .withPageable(pageable)
                    .build();
        }


       Page<UrpNews> urpNewsList = elasticsearchTemplate.queryForPage(searchQuery, UrpNews.class, new SearchResultMapper() {
           @Override
           public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

               ArrayList<UrpNews> urpNewsArrayList = new ArrayList<UrpNews>();
               SearchHits searchHits = searchResponse.getHits();

               for(SearchHit hit : searchHits){
                   if(searchHits.getHits().length == 0)
                       return null;
                   else if(hit.getSourceAsMap()!= null){
                       UrpNews urpNews = new UrpNews();
                       String highLightTitleMessage = String.valueOf(hit.getSourceAsMap().get("title"));
                       String highLightContentMessage = String.valueOf(hit.getSourceAsMap().get("content"));
                       if(hit.getHighlightFields().size()!=0) {
                           if(hit.getHighlightFields().get("title")!=null)
                               highLightTitleMessage = hit.getHighlightFields().get("title").fragments()[0].toString();
                           if(hit.getHighlightFields().get("content")!=null)
                               highLightContentMessage = hit.getHighlightFields().get("content").fragments()[0].toString();
                            else{
                                if (highLightContentMessage.length()>= 70)
                                    highLightContentMessage = highLightContentMessage.substring(0,69);
                           }
                       }
                       //String highLightContentMessage = hit.getHighlightFields().get("content").fragments()[0].toString();
                       urpNews.setId(Integer.parseInt(hit.getId()));
                       urpNews.setTitle(String.valueOf(hit.getSourceAsMap().get("title")));
                       urpNews.setContent(String.valueOf(hit.getSourceAsMap().get("content")));
                       urpNews.setNewsurl(String.valueOf(hit.getSourceAsMap().get("newsurl")));
                       urpNews.setPagehtml(String.valueOf(hit.getSourceAsMap().get("pagehtml")));
                       urpNews.setTime(String.valueOf(hit.getSourceAsMap().get("time")));
                       urpNews.setHighlightContent(highLightContentMessage);
                       urpNews.setTotal(Integer.parseInt(String.valueOf(searchHits.getTotalHits())));
                       urpNews.setNewstype(String.valueOf(hit.getSourceAsMap().get("newstype")));
                       try{
                           String setMethodName = parSetName("title");
                           Class<? extends UrpNews> urpNewsClazz = urpNews.getClass();
                           Method setMethod = urpNewsClazz.getMethod(setMethodName,String.class);
                           setMethod.invoke(urpNews,highLightTitleMessage);
                       }catch (Exception e){
                           e.printStackTrace();
                       }

                       urpNewsArrayList.add(urpNews);
                   }


               }
               if(urpNewsArrayList.size()>0)
                   return new AggregatedPageImpl <T>((List<T>) urpNewsArrayList);
               return null;
           }
       });
        if(urpNewsList!=null)
            return urpNewsList.getContent();
        else {
            UrpNews urpNews =  new UrpNews();
            urpNews.setTotal(0);
            urpNews.setTitle("没有结果");
            urpNews.setContent("没有结果");
            List<UrpNews> temp = new ArrayList<>();
            temp.add(urpNews);
            return temp;
        }
    }

    private static String parSetName(String fieldName) {

        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()+ fieldName.substring(startIndex + 1);
    }


    private double[]pageRank(){
        List<UrpNews> news =  urpNewsDao.queryAllByIdExists();
        double matrix[][] = new double[news.size()][news.size()];
        double PR[] = new double[news.size()];
        //初始化矩阵
        for(int i = 0; i < news.size(); i++){
            for(int j = 0; j < news.size(); j++){
                matrix[i][j] = 0;
            }
        }
        int j = 0;
        for(UrpNews urpNews : news){
            for(int i = 0; i < news.size(); i++){
                int[] temp = getHref(urpNews.getPagehtml());
                if( temp.length != 0){
                    for(int t : temp){
                        matrix[i][t] = 1/(double)t;//形成矩阵并归一化
                    }
                }
            }
        }
        //计算pageRank
        Matrix matrixPr = new Matrix(matrix);
        matrix =  matrixPr.eig().getD().getArray();
        for(int i = 0;i < news.size(); i++){
            PR[i] = matrix[i][i];
        }
        return PR;
    }
    //获取每个网页指向的网页
    private int[] getHref(String pageHtml){
        Pattern p = Pattern.compile("href=\".*\"");//正则表达式
        Matcher m = p.matcher(pageHtml);//匹配所有形如href="http:://baidu.com" 的字符串
        String url;
        List<String> urls = new ArrayList<>();
        if(m.find()){
            url = m.group(0);
            urls.add(url);//将m中的每个分组交给url
        }
        int j = 0;
        int all[] = new int[urls.size()];
        for(String i: urls){
            all[j] = urpNewsDao.queryUrpNewsByNewsurl(i.split("\"")[1]).getId();//提取出url
            j++;
        }
        return all;
    }
  }
