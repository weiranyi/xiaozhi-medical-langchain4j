package com.atguigu.java.ai.langchain4j.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import jakarta.annotation.Resource;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponseInterceptor;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingStoreConfig {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Resource
    private RestClient restClient;

//    @Bean
//    public EmbeddingStore<TextSegment> embeddingStore() {
//        //创建向量存储
//        EmbeddingStore<TextSegment> embeddingStore = PineconeEmbeddingStore.builder()
//                .apiKey(System.getenv("PINECONE_API_KEY"))
//                .index("xiaozhi-index")//如果指定的索引不存在，将创建一个新的索引
//                .nameSpace("xiaozhi-namespace") //如果指定的名称空间不存在，将创建一个新的名称空间
//                .createIndex(PineconeServerlessIndexConfig.builder()
//                        .cloud("AWS") //指定索引部署在 AWS 云服务上。
//                        .region("us-east-1") //指定索引所在的 AWS 区域为 us-east-1。
//                        .dimension(embeddingModel.dimension()) //指定索引的向量维度，该维度与 embeddedModel 生成的向量维度相同。
//                        .build())
//                .build();
//
//        return embeddingStore;
//    }

    // 使用ES做向量数据库
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
//         var restClient = RestClient
//                .builder(HttpHost.create("http://192.168.10.188:9200"))
//                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
//                        .addInterceptorLast((HttpResponseInterceptor)
//                                (response, context) ->
//                                        response.addHeader("X-Elastic-Product", "Elasticsearch"))
//                ) // 这部分为了避免旧版本406报错（如果es更新到了8.xx，把这个去掉即可）
//                .build();
        ElasticsearchEmbeddingStore embeddingStore = ElasticsearchEmbeddingStore.builder()
                .indexName("xiaozhi")
                .restClient(restClient)
                .build();
        return embeddingStore;
    }
}
