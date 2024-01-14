package org.tutorial;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;
import org.tutorial.pojo.HotelDoc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class HotelSearchTest {
	private RestHighLevelClient client;

	@Test
	void testMatchAll() throws IOException {
		// 1.創建Request 物件
		SearchRequest request = new SearchRequest("hotel");
		// 2.準備DSL
		request.source().query(QueryBuilders.matchAllQuery());
		// 3.發送請求
		SearchResponse response = client.search(request, RequestOptions.DEFAULT);
		// 4.解析結果
		handleResponse(response);
	}

	@Test
	void testMatch() throws IOException {
		// 1.創建Request 物件
		SearchRequest request = new SearchRequest("hotel");
		// 2.準備DSL
		request.source().query(QueryBuilders.matchQuery("all", "taipei"));
		// 3.發送請求
		SearchResponse response = client.search(request, RequestOptions.DEFAULT);
		// 4.解析結果
		handleResponse(response);
	}

	@Test
	void testBool() throws IOException {
		// 1.創建Request 物件
		SearchRequest request = new SearchRequest("hotel");
		// 2.準備DSL
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.matchQuery("score", "80"));
		boolQuery.filter(QueryBuilders.rangeQuery("price").lte(18000));
		request.source().query(boolQuery);
		// 3.發送請求
		SearchResponse response = client.search(request, RequestOptions.DEFAULT);
		// 4.解析結果
		handleResponse(response);
	}

	@Test
	void testPageAndSort() throws IOException {
		int page = 1, size = 5;

		// 1.創建Request 物件
		SearchRequest request = new SearchRequest("hotel");
		// 2.準備DSL
		request.source().query(QueryBuilders.matchAllQuery());
		request.source().sort("price", SortOrder.ASC);
		request.source().from((page - 1) * size).size(size);
		// 3.發送請求
		SearchResponse response = client.search(request, RequestOptions.DEFAULT);
		// 4.解析結果
		handleResponse(response);
	}

	@Test
	void testHighlight() throws IOException {
		// 1.創建Request 物件
		SearchRequest request = new SearchRequest("hotel");
		// 2.準備DSL
		request.source().query(QueryBuilders.matchQuery("all", "台北"));
		request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));
		// 3.發送請求
		SearchResponse response = client.search(request, RequestOptions.DEFAULT);
		// 4.解析結果
		handleResponse(response);
	}

	@BeforeEach
	void setUp() {
		this.client = new RestHighLevelClient(RestClient.builder(
			new HttpHost("192.168.191.133", 9200))
		);
	}

	@AfterEach
	void tearDown() throws IOException {
		this.client.close();
	}

	private void handleResponse(SearchResponse response) throws JsonMappingException, JsonProcessingException {
		SearchHits searchHits = response.getHits();

		System.out.println("總筆數：" + searchHits.getTotalHits().value);
		for (SearchHit hit : searchHits.getHits()) {
			String json = hit.getSourceAsString();

			HotelDoc hotelDoc = new ObjectMapper().readValue(json, HotelDoc.class);
			// highlight 處理
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			if (!CollectionUtils.isEmpty(highlightFields)) {
				HighlightField highlightField = highlightFields.get("name");
				if (highlightField != null) {
					String name = highlightField.getFragments()[0].string();
					hotelDoc.setName(name);
				}
			}
			System.out.println(hotelDoc);
		}
	}
}
