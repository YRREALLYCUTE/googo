# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://doc.scrapy.org/en/latest/topics/item-pipeline.html
import json
import pymysql


class LiepPipeline(object):
    #
    # def __init__(self):
    #     self.file = open('liepin.json','wb+')
    #
    # def process_item(self, item, spider):
    #     text = json.dumps(dict(item),ensure_ascii=False)
    #     self.file.write(text.encode('utf-8'))
    #     print('------正在写入数据------')
    #     return item
    #
    # def close(self):
    #     self.file.close()
    def __init__(self):
        # 建立数据库连接
        self.connection = pymysql.connect(host='127.0.0.1', port=3306, user='root', password='root', db='database',
                                          charset='utf8')
        # 创建操作游标
        self.cursor = self.connection.cursor()

    def process_item(self, item, spider):
        # 定义sql语句
        # sql = "INSERT INTO `database`.`cz_news` (`title`, `time`, `content`, `newsUrl`,`pageHtml`) VALUES('" + item[
        #     'title'] + "','" + item['time'] + "','" + item['content'] + "','" + item['newsUrl'] + "','" + item[
        #           'pageHtml'] + "');"
        self.cursor.execute(
            """insert into database.hyxy_news(title,content,pageHtml,newsUrl)
            value (%s, %s, %s, %s)""",
            (
             item['title'],
             item['content'],
             item['pageHtml'],
             item['newsUrl'],
             ))

        # 保存修改
        self.connection.commit()

        return item

    def __del__(self):
        # 关闭操作游标
        self.cursor.close()
        # 关闭数据库连接
        self.connection.close()
