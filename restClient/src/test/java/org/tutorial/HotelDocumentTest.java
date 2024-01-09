package org.tutorial;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.tutorial.pojo.Hotel;
import org.tutorial.pojo.HotelDoc;
import org.tutorial.repository.HotelRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class HotelDocumentTest {
	private RestHighLevelClient client;
	
	@Autowired
	HotelRepository repository;

	@Test
	void addDocument() throws IOException {
		// 根據id 查詢資料
		Hotel hotel = repository.findById(1L).get();
		// 轉換文檔類型
		HotelDoc hotelDoc = new HotelDoc(hotel);
		
		// 1.創建Request 物件
		IndexRequest request = new IndexRequest("hotel").id(hotel.getId().toString());
		// 2.準備JSON 文檔
		request.source(new ObjectMapper().writeValueAsString(hotelDoc), XContentType.JSON);
		// 3.發送請求
		client.index(request, RequestOptions.DEFAULT);
	}
	
	@Test
	void bulkRequest() throws IOException {
		// 根據id 查詢資料
		List<Hotel> hotels = repository.findAll();
		
		// 1.創建Request 物件
		BulkRequest request = new BulkRequest();
		// 2.準備請求參數，添加多個新增的Request (也接受更新和刪除)
		for (Hotel hotel : hotels) {
			// 轉換文檔類型
			HotelDoc hotelDoc = new HotelDoc(hotel);
			request.add(new IndexRequest("hotel")
					.id(hotel.getId().toString())
					.source(new ObjectMapper().writeValueAsString(hotelDoc), XContentType.JSON));
		}
		
		// 3.發送請求
		client.bulk(request, RequestOptions.DEFAULT);
	}

	@Test
	void getDocument() throws IOException{
		// 1.創建Request 物件
		GetRequest request = new GetRequest("hotel","1");
		// 2.發送請求
		GetResponse response = client.get(request, RequestOptions.DEFAULT);
		// 3.解析回應結果
		String json = response.getSourceAsString();
		
		HotelDoc hotelDoc = new ObjectMapper().readValue(json, HotelDoc.class);
		System.out.println(hotelDoc);
	}

	@Test
	void updateDocument() throws IOException {
		// 1.創建Request 物件
		UpdateRequest request = new UpdateRequest("hotel", "1");
		// 2.準備請求參數
		// 2.1 request.doc(Object object) 全量更新
		// 2.2 request.doc(String... fields) 局部更新
		request.doc(
			"price", "10000",
			"starName", "四星"
		);
		// 3.發送請求
		client.update(request, RequestOptions.DEFAULT);
	}
	
	@Test
	void deleteDocument() throws IOException {
		// 1.創建Request 物件
		DeleteRequest request = new DeleteRequest("hotel","1");
		// 2.發送請求
		client.delete(request, RequestOptions.DEFAULT);
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
