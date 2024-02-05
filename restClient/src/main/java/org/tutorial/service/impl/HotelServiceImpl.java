package org.tutorial.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tutorial.service.HotelService;

@Service
public class HotelServiceImpl implements HotelService {

	@Autowired
	private RestHighLevelClient client;

	@Override
	public Map<String, List<String>> filters() {
		Map<String, List<String>> result = new HashMap<>();
		try {
			// 1.創建Request 物件
			SearchRequest request = new SearchRequest("hotel");
			// 2.準備DSL
			request.source().size(0);
			buildAggregation(request);
			// 3.發送請求
			SearchResponse response;
			response = client.search(request, RequestOptions.DEFAULT);
			// 4.解析結果
			Aggregations aggregations = response.getAggregations();
			List<String> brandList = getAggByName(aggregations, "brandAgg");
			result.put("品牌", brandList);
			List<String> cityList = getAggByName(aggregations, "cityAgg");
			result.put("城市", cityList);
			List<String> starList = getAggByName(aggregations, "starAgg");
			result.put("星級", starList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private List<String> getAggByName(Aggregations aggregations, String aggName) {
		Terms brandTerms = aggregations.get(aggName);
		List<? extends Bucket> buckets = brandTerms.getBuckets();
		List<String> list = new ArrayList<>();
		for (Bucket bucket : buckets) {
			String key = bucket.getKeyAsString();
			list.add(key);
		}
		return list;
	}

	private void buildAggregation(SearchRequest request) {
		request.source().aggregation(AggregationBuilders.terms("brandAgg").field("brand").size(10));
		request.source().aggregation(AggregationBuilders.terms("cityAgg").field("city").size(10));
		request.source().aggregation(AggregationBuilders.terms("starAgg").field("starName.keyword").size(10));
	}

}
