package org.tutorial;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.tutorial.constants.HotelConstants;

@SpringBootTest
public class HotelIndexTest {
	private RestHighLevelClient client;

	@Test
	void createHotelIndex() throws IOException {
		// 1.創建Request 物件
		CreateIndexRequest request = new CreateIndexRequest("hotel");
		// 2.準備請求的參數，DSL
		request.source(HotelConstants.MAPPING_TEMPLATE, XContentType.JSON);
		// 3.發送請求
		client.indices().create(request, RequestOptions.DEFAULT);
	}

	@Test
	void existsHotelIndex() throws IOException {
		// 1.創建Request 物件
		GetIndexRequest request = new GetIndexRequest("hotel");
		// 2.發送請求
		boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);

		System.out.println(exists ? "索引庫存在" : "索引庫不存在");
	}

	@Test
	void deleteHotelIndex() throws IOException {
		// 1.創建Request 物件
		DeleteIndexRequest request = new DeleteIndexRequest("hotel");
		// 2.發送請求
		client.indices().delete(request, RequestOptions.DEFAULT);
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
}
