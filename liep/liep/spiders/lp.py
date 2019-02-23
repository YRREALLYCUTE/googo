# #-*- coding: utf-8 -*-
# from scrapy.spiders import CrawlSpider, Rule
# from scrapy.linkextractors import LinkExtractor
# from liep.items import LiepItem
# import re
#
#
# class LpSpider(CrawlSpider):
#     reg = re.compile('\s*')
#     name = 'lp'
#     # allowed_domains = ['www.liepin.com']
#     start_urls = ['http://wxy.nankai.edu.cn/Article/Index/26/1',]
#     page_link = LinkExtractor(
#         allow='Article/Index/26/d+')
#
#     rules = {
#         Rule(link_extractor=page_link, callback='parse_content', follow=True)
#     }
#
#     def parse_content(self, response):
#         item = LiepItem()
#         job_list = response.xpath('/html/body/div[4]/div[2]/ul/li[1]/a')
#         for job in job_list:
#             name = job.xpath('.//h3/a')
#             item['time'] = infolist.xpath('./td[4]/@text()').extract()[0]
#             item['ISBN'] = infolist.xpath('./td[2]/@text()').extract()[0]
#             item['publish'] = infolist.xpath('./td[3]/@text()').extract()[0]
#             item['title'] = infolist.xpath('./td[1]/@text()').extract()[0]
#             item['bookUrl'] = infolist.xpath('./td[1]/a/@href').extract()[0]
#             # divs = infolist.xpath('//div[@class=" news-content"]')
#             item['[pageHtml'] = scrapy.Request(
#                 self.base_site + infolist.xpath('./td[1]/a/@href').split("..")[1]).body.decode('utf-8')
#             item['searchNum'] = infolist.xpath('./td[5]/@text()').extract()[0]
#             yield item

import scrapy
from liep.items import LiepItem


class LpSpider(scrapy.Spider):
    name = 'lp'
    allowed_domains = ['hyxy.nankai.edu.cn']
    start_urls = ['http://hyxy.nankai.edu.cn/index.php/Home/News/newsMore/cid/4']

    # 主站链接 用来拼接
    base_site = 'http://hyxy.nankai.edu.cn'

    def parse(self, response):
        news_urls = response.xpath('//ul[@class="articleList"]//li/a/@href').extract()

        for news_url in news_urls:
            url = self.base_site + news_url
            yield scrapy.Request(url, callback=self.getInfo)

        # 获取下一页
        next_page_url = self.base_site + response.xpath('//div[@class="manu"]/div/a[@class="next"]/@href').extract()[0]

        yield scrapy.Request(next_page_url, callback=self.parse)

    def getInfo(self, response):
        item = LiepItem()
        # 提取信息
        #item['time'] = response.xpath('//*[@id="container"]/div/div/div/p/span[@class="arti_update"]/text()').extract()[0].split("：")[1]
        item['title'] = response.xpath('/html/body/div[2]/div[2]/h1/text()').extract()[0]
        item['newsUrl'] = response.url
        item['pageHtml'] = response.body.decode('utf-8')
        # item['viewNum'] = response.xpath('//*[@id="container"]/div/div/div/p/span[@class="arti_views"]/span/text()').extract()[0]
        divs = response.xpath('/html/body/div[2]/div[2]/div')
        body = ''
        for p in divs.xpath('.//p/text()|.//p//span/text()'):
            body += p.extract().strip()
        item['content'] = body
        yield item
