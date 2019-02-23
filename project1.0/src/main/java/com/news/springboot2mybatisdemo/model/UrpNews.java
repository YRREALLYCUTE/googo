package com.news.springboot2mybatisdemo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "news_ik_t", type = "doc")
public class UrpNews {
    @Field(analyzer = "ik_smart" , searchAnalyzer = "ik_smart" , type = FieldType.Text)
    private String title;
    @Field(analyzer = "ik_smart" , searchAnalyzer = "ik_smart" , type = FieldType.Text)
    private String content;
    @Field(type = FieldType.Text)
    private String pagehtml;
    @Field(type = FieldType.Text)
    private String newsurl;
    @Field(type = FieldType.Text)
    private String time;
    @Id
    private Integer id;
    @Field(type = FieldType.Text)
    private  String newstype;

    private Integer total;

    private String highlightContent;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPagehtml() {
        return pagehtml;
    }

    public void setPagehtml(String pagehtml) {
        this.pagehtml = pagehtml;
    }

    public String getNewsurl() {
        return newsurl;
    }

    public void setNewsurl(String newsurl) {
        this.newsurl = newsurl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getHighlightContent() {
        return highlightContent;
    }

    public void setHighlightContent(String highlightContent) {
        this.highlightContent = highlightContent;
    }

    public String getNewstype() {
        return newstype;
    }

    public void setNewstype(String newstype) {
        this.newstype = newstype;
    }
}
