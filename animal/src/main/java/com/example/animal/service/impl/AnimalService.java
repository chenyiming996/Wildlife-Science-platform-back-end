package com.example.animal.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.animal.common.MyException;
import com.example.animal.dto.AnimalDoc;
import com.example.animal.dto.AnimalDocPage;
import com.example.animal.dto.RequestParams;
import com.example.animal.entity.Animal;
import com.example.animal.mapper.AnimalMapper;
import com.example.animal.service.IAnimalService;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AnimalService extends ServiceImpl<AnimalMapper, Animal> implements IAnimalService {
    @Autowired
    private RestHighLevelClient client;

    @Override
    public AnimalDocPage search(RequestParams params) {
        try {
            // 1.准备Request
            SearchRequest request = new SearchRequest("animal");
            // 2.准备DSL
            //2.1query
            buildBasicQuery(params, request);
            //2.2分页
            int page = params.getPage();
            int size = 10;
            request.source().from((page - 1) * size).size(size);
            // 3.发送请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 4.解析响应
            return handleResponse(response);
        } catch (IOException e) {
            throw new MyException(500,"服务器异常，查询失败");
        }
    }

    @Override
    public Map<String, List<String>> filters(RequestParams params) {
        try {
            //1.准备request
            SearchRequest request = new SearchRequest("animal");
            //2.准备DSL
            //2.1query
            buildBasicQuery(params, request);
            //2.2设置size
            request.source().size(0);
            //2.3聚合
            buildAggregation(request);
            //3.发出请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            //4.解析结果
            Map<String, List<String>> result = new HashMap<>();
            Aggregations aggregations = response.getAggregations();
            //4.1根据类型名称，放入类型结果
            List<String> typeList = getAggByName(aggregations, "typeAgg");
            result.put("type", typeList);
            //4.2根据标签名称，放入标签结果
            List<String> tagList = getAggByName(aggregations, "tagAgg");
            result.put("tag", tagList);
            return result;
        } catch (IOException e) {
            throw new MyException(500,"服务器异常，查询失败");
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            // 1.准备Request
            DeleteRequest request = new DeleteRequest("animal", id.toString());
            // 2.发送请求
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertById(Long id) {
        try {
            // 0.根据id查询动物数据
            Animal animal = getById(id);
            // 转换为文档类型
            AnimalDoc animalDoc = new AnimalDoc(animal);

            // 1.准备Request对象
            IndexRequest request = new IndexRequest("animal").id(animal.getId().toString());
            // 2.准备Json文档
            request.source(JSON.toJSONString(animalDoc), XContentType.JSON);
            // 3.发送请求
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getAggByName(Aggregations aggregations, String aggName) {
        //4.1根据集合名称获取集合结果
        Terms brandTerms = aggregations.get(aggName);
        //4.2获取buckets
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        //遍历
        List<String> aggList = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            aggList.add(key);
        }
        return aggList;
    }

    private void buildAggregation(SearchRequest request) {
        request.source().aggregation(AggregationBuilders
                .terms("typeAgg")
                .field("type")
                .size(100));
        request.source().aggregation(AggregationBuilders
                .terms("tagAgg")
                .field("tag")
                .size(100));
    }

    private void buildBasicQuery(RequestParams params, SearchRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //关键字搜索
        String key = params.getKey();
        if (key == null || key.equals("")) {
            boolQuery.must(QueryBuilders.matchAllQuery());
        } else {
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        }
        //类型条件过滤
        if (params.getType() != null && !params.getType().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("type", params.getType()));
        }
        //标签条件过滤
        if (params.getTag() != null && !params.getTag().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("tag", params.getTag()));
        }
        //放入source
        request.source().query(boolQuery);
    }

    private AnimalDocPage handleResponse(SearchResponse response) {
        // 4.解析响应
        SearchHits searchHits = response.getHits();
        // 4.1.获取总条数
        long total = searchHits.getTotalHits().value;
        // 4.2.文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        List<AnimalDoc> animals = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            AnimalDoc animalDoc = JSON.parseObject(json, AnimalDoc.class);
            animals.add(animalDoc);
        }
        //4.4封装返回
        return new AnimalDocPage(total, animals);
    }

}
