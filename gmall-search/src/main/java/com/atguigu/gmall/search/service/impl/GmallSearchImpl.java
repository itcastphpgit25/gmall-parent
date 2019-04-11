package com.atguigu.gmall.search.service.impl;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.constant.EsConstant;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.search.GmallSearchService;
import com.atguigu.gmall.to.es.EsProduct;
import com.atguigu.gmall.to.es.SearchParam;
import com.atguigu.gmall.to.es.SearchResponse;
import com.atguigu.gmall.to.es.SearchResponseAttrVo;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.ChildrenAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Component
@Service(version = "1.0")
public class GmallSearchImpl implements GmallSearchService{

    @Autowired
    JestClient jestClient;

    //上下架：除了添加添加到es中，还要修改商品的相关信息
    @Reference
    ProductService productService;

    @Override
    public void publishStatus(List<Long> ids, Integer publishStatus) {
         //1.修改数据库上下架
        /**
         * 1.修改数据库上下架
         * 2.将商品数据保存到es
         *   - 商品需要检索的数据进入ES即可
         *   -商品的数据那些需要检索、过滤、排序
         * 3.搜索展示的是SPU信息，sku销售属性也要筛选
         * 4.上架一款商品，是将这个SPU下的所有SKU的信息全部放在es中
         * 5.SKU商品的标题：SPU的标题+SKU的属性
         *
         *
         */
        ids.forEach((productId)->{
            //传入的是商品id，上架的是sku
            //1）根据商品id查询sku信息，改写标题，上架到es

            Long id = productId;
        });
    }

    /**
     *
     * 保存商品信息到es
     * @param esProduct
     * @return
     */
    @Override
    public boolean saveProductInfoToES(EsProduct esProduct) {
        Index index=new Index.Builder(esProduct)
                .index(EsConstant.ES_PRODUCT_INDEX)
                .type(EsConstant.ES_PRODUCT_TYPE)
                .id(esProduct.getId().toString())
                .build();
        DocumentResult execute=null;
        try {
            execute=jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return execute.isSucceeded();
    }

    /**
     *
     * 检索商品
     */
    @Override
    public SearchResponse searchProduct(SearchParam param)  throws IOException{

        //1.根据页面传递的参数构建检索的DSL语句
        String queryDSL = buildSearchDsl(param);
        Search search = new Search.Builder(queryDSL)
                .addIndex("gulishop")
                .addType("product")
                .build();

        //2.执行查询
        SearchResult result = jestClient.execute(search);

        //3.封装和分析查询结果:将查询出来的结果进行整理使前端输入对应数据格式 查询
        SearchResponse response = buildSearchResult(result);

        //4、封装分页信息
        response.setPageNum(param.getPageNum());
        response.setPageSize(param.getPageSize());
        response.setTotal(result.getTotal());
        return response;
    }

    /**
     *
     * 2).封装检索的结果
     */
    private SearchResponse buildSearchResult(SearchResult result){
//        System.out.println("---------------"+result.get);
        System.out.println("===========>"+result.getTotal());
        //======最终返回结果======
        SearchResponse searchresponse = new SearchResponse();

        //1.封装所有的商品信息
        List<SearchResult.Hit<EsProduct, Void>> hits = result.getHits(EsProduct.class);
        for (SearchResult.Hit<EsProduct, Void> hit : hits) {

            EsProduct source = hit.source;
            System.out.println("-------"+source);
            searchresponse.getProducts().add(source);

        }
        /**
         * List<SearchResult.Hit<EsProduct, Void>> hits = result.getHits(EsProduct.class);
         for (SearchResult.Hit<EsProduct, Void> hit : hits) {
         EsProduct source = hit.source;
         searchResponse.getProducts().add(source);
         }
         */

        //2.封装属性信息
        //2.1 封装品牌到response
        MetricAggregation aggregations = result.getAggregations();
        SearchResponseAttrVo brandId = new SearchResponseAttrVo();
        brandId.setName("品牌");
        //2.2 获取到品牌
        aggregations.getTermsAggregation("brandIdAgg").getBuckets().forEach((b)->{
            b.getTermsAggregation("brandNameAgg").getBuckets().forEach(a->{
                String key = a.getKey();
                brandId.getValue().add(key);
            });
        });
        searchresponse.setBrand(brandId);

        //2.3获取到到分类
        SearchResponseAttrVo category=new SearchResponseAttrVo();
        category.setName("分类");
        aggregations.getTermsAggregation("categoryIdAgg").getBuckets().forEach((b)->{
            b.getTermsAggregation("productCategoryNameAgg").getBuckets().forEach(bb->{
                String key = bb.getKey();
                category.getValue().add(key);
            });
        });
        searchresponse.setCatelog(category);

        //2.4获取到属性
        TermsAggregation termsAggregation = aggregations.getChildrenAggregation("productAttrAgg")
                .getChildrenAggregation("productAttrIdAgg")
                .getTermsAggregation("productAttrIdAgg");

        termsAggregation.getBuckets().forEach((b)->{
            SearchResponseAttrVo attrVo=new SearchResponseAttrVo();

            //第一层属性id
            attrVo.setProductAttributeId(Long.parseLong(b.getKey()));
            b.getTermsAggregation("productAttrNameAgg").getBuckets().forEach((bb)->{
                //第二层属性名
                attrVo.setName(bb.getKey());
                bb.getTermsAggregation("productAttrValueAgg").getBuckets().forEach((bbb)->{
                    //第三层封装的属性值
                    attrVo.getValue().add(bbb.getKey());
                });
            });
            searchresponse.getAttrs().add(attrVo);
        });
        return searchresponse;
    }

    /**
     *
     * 3).封装检索条件
     */
    private String buildSearchDsl(SearchParam param) {

        //2.创建搜索源
        //SearchSourceBuilder searchSource = new SearchSourceBuilder().query(name);
        SearchSourceBuilder searchSource = new SearchSourceBuilder();

        //1.所有条件都在SearchSourceBuilder中
        //QuerBuilders构造各个条件
        //MatchQueryBuilder name = QueryBuilders.matchQuery("name", param.getKeyword());
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();


        //查询
        if(!StringUtils.isEmpty(param.getKeyword())){
            //查询XXX must是必须满足的条件  match是用来做模糊的  term是用来做精确的
            //filter和match一样但不计算相关评分的
            boolQuery.must(QueryBuilders.matchQuery("name",param.getKeyword()));
            //subTitle和keyWorld作为加分项
            boolQuery.should(QueryBuilders.matchQuery("subTitle",param.getKeyword()));
            boolQuery.should(QueryBuilders.matchQuery("keywords",param.getKeyword()));
        }else{
            boolQuery.must(QueryBuilders.matchAllQuery());
        }

        //过滤
        if(param.getCatelog3Id()!=null){
            //传了分类id
            boolQuery.filter(QueryBuilders.termQuery("productCategoryId",param.getCatelog3Id()));
        }
        if(param.getBrandId()!=null){
            boolQuery.filter(QueryBuilders.termQuery("brandId",param.getBrandId()));
        }

        //过滤属性
        if(param.getProps()!=null&&param.getProps().length>0){
            String[] props=param.getProps();

            for (String prop : props) {
                String productAttrId = prop.split(":")[0];
                String productAttrValue = prop.split(":")[1];

                boolQuery.filter(
                        QueryBuilders.nestedQuery("attrValueList",
                                QueryBuilders.boolQuery()
                                        .must(
                                                QueryBuilders.termQuery("attrValueList.productAttributeId",productAttrId)
                                        )
                                        .must(
                                                QueryBuilders.termQuery("attrValueList.value",productAttrValue)
                                        ), ScoreMode.None)
                );
            }
        }

        String[] props=param.getProps();
        if(props!=null){

            for (String prop : props) {
                String valus = prop.split(":")[1];

                String[] split = valus.split("-");

                BoolQueryBuilder must = QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("attrValueList.productAttributeId", prop.split(":")[0]))
                        .must(QueryBuilders.termQuery("attrValueList.value", split));
                //过滤属性
                boolQuery.filter(QueryBuilders.nestedQuery("attrValueList",must,ScoreMode.None));
            }
        }



        //价格区间过滤
        if(param.getPriceFrom()!=null){
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(param.getPriceFrom()));
        }
        if(param.getPriceTo()!=null){
            boolQuery.filter(QueryBuilders.rangeQuery("price").lte(param.getPriceTo()));
        }

        //String[] props=param.getProps();

        searchSource.query(boolQuery);

        //3.聚合
        //3.1聚合品牌信息
        TermsAggregationBuilder brandAggs = AggregationBuilders.terms("brandIdAgg")
                .field("brandId")
                .size(100)
                .subAggregation(AggregationBuilders.terms("brandNameAgg")
                        .field("brandName")
                        .size(100));
        //放入搜索源中
        searchSource.aggregation(brandAggs);

        //3.2聚合分类信息
        TermsAggregationBuilder categoryAggs = AggregationBuilders.terms("categoryIdAgg")
                .field("productCategoryId")
                .size(100)
                .subAggregation(AggregationBuilders.terms("productCategoryNameAgg")
                        .field("productCategoryName")
                        .size(100));
        searchSource.aggregation(categoryAggs);
        //3.3属性聚合

        FilterAggregationBuilder filter = AggregationBuilders.filter("productAttrIdAgg",
                QueryBuilders.termQuery("attrValueList.type", "1"));

        //???
        filter.subAggregation(AggregationBuilders.terms("productAttrIdAgg")
                .field("attrValueList.productAttributeId")
                .size(100)
                .subAggregation(AggregationBuilders.terms("productAttrNameAgg")
                        .field("attrValueList.name")
                        .size(100)
                        .subAggregation(AggregationBuilders.terms("productAttrValueAgg")
                                .field("attrValueList.value")
                                .size(100))));
        //属性聚合
        NestedAggregationBuilder attAgg = AggregationBuilders.nested("productAttrAgg", "attrValueList")
                .subAggregation(filter);

        searchSource.aggregation(attAgg);
        //4高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name").preTags("<b style='color:red'>").postTags("</b>");
        searchSource.highlighter(highlightBuilder);

        //5.分页
        searchSource.from((param.getPageNum()-1)*param.getPageSize());
        searchSource.size(param.getPageSize());

        //6.排序
        if(!StringUtils.isEmpty(param.getOrder())){
            String order=param.getOrder();
            String type = order.split(":")[0];
            String asc = order.split(":")[1];

            if("0".equals(type)){
                searchSource.sort(SortBuilders.scoreSort().order(SortOrder.fromString(asc)));
            }
            if("1".equals(type)){
                searchSource.sort(SortBuilders.fieldSort("sale").order(SortOrder.fromString(asc)));
            }
            if("2".equals(type)){
                searchSource.sort(SortBuilders.fieldSort("price").order(SortOrder.fromString(asc)));
            }
        }
        System.out.println("======>>>"+searchSource.toString());
        System.out.println(searchSource.toString());
        return searchSource.toString();


    }
}
