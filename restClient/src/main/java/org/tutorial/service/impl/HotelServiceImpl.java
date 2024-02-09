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
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tutorial.pojo.HotelDoc;
import org.tutorial.pojo.PageResult;
import org.tutorial.pojo.RequestParams;
import org.tutorial.service.HotelService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HotelServiceImpl implements HotelService {

	@Autowired
	private RestHighLevelClient client;

	@Override
	public PageResult search(RequestParams params) {
		try {
			// 1.創建Request 物件
			SearchRequest request = new SearchRequest("hotel");
			// 2.準備DSL
			buildBasicQuery(params, request);

			// 分頁
			int page = params.getPage();
			int size = params.getSize();
			request.source().from((page - 1) * size).size(size);

			// 排序(距離排序，由經緯度計算)
			String location = params.getLocation(); // ex: "25.051234567890, 121.519876543210"
			if (location != null && !location.equals("")) {
				request.source().sort(SortBuilders
						.geoDistanceSort("location", new GeoPoint(location))
						.order(SortOrder.ASC)
						.unit(DistanceUnit.KILOMETERS)
					);
			}

			// 3.發送請求
			SearchResponse response;
			// 4.解析結果
			response = client.search(request, RequestOptions.DEFAULT);
			return handleResponse(response);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, List<String>> filters(RequestParams params) {
		Map<String, List<String>> result = new HashMap<>();
		try {
			// 1.創建Request 物件
			SearchRequest request = new SearchRequest("hotel");
			// 2.準備DSL
			buildBasicQuery(params, request);
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
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<String> getSuggestions(String prefix) {
		List<String> list = new ArrayList<>();
		try {
			// 1.創建Request 物件
			SearchRequest request = new SearchRequest("hotel");
			// 2.準備DSL
			request.source().suggest(new SuggestBuilder().addSuggestion(
					"mySuggestions", 
					SuggestBuilders.completionSuggestion("suggestion")
					.prefix(prefix)
					.skipDuplicates(true)
					.size(10)
				));
			// 3.發送請求
			SearchResponse response;
			response = client.search(request, RequestOptions.DEFAULT);
			// 4.解析結果
			Suggest suggest = response.getSuggest();
			CompletionSuggestion suggestions = suggest.getSuggestion("mySuggestions");
			List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
			for (Option option : options) {
				list.add(option.getText().toString());
			}
			return list;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void buildBasicQuery(RequestParams params, SearchRequest request) {
		// 1.原始條件
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery(); // 組合多條件
		// 關鍵字
		String key = params.getKey();
		if (key == null || "".equals(key)) {
			boolQuery.must(QueryBuilders.matchAllQuery());
		} else {
			boolQuery.must(QueryBuilders.matchQuery("all", key));
		}
		// 城市條件
		if (params.getCity() != null && !params.getCity().equals("")) {
			boolQuery.filter(QueryBuilders.termQuery("city", params.getCity()));
		}
		// 品牌條件
		if (params.getBrand() != null && !params.getBrand().equals("")) {
			boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
		}
		// 星級條件
		if (params.getStarName() != null && !params.getStarName().equals("")) {
			boolQuery.filter(QueryBuilders.termQuery("starName", params.getStarName())); // 星級的mapping是
		}
		// 價格條件
		if (params.getMinPrice() != null && params.getMaxPrice() != null) {
			boolQuery.filter(QueryBuilders.rangeQuery("price")
					.gte(params.getMinPrice())
					.lte(params.getMaxPrice())
				);
		}
		
		// 2.算分控制
		FunctionScoreQueryBuilder functionScoreQuery = 
				QueryBuilders.functionScoreQuery(
						// 原始查詢，相關性算分的查詢
						boolQuery, 
						// function score的陣列
						new FunctionScoreQueryBuilder.FilterFunctionBuilder[] {
								// 其中一個function score元素
								new FunctionScoreQueryBuilder.FilterFunctionBuilder(
										// 過濾條件
										QueryBuilders.termQuery("isAD", true), 
										// 算分函數(這裡是乘10)
										ScoreFunctionBuilders.weightFactorFunction(10))
						});

		request.source().query(functionScoreQuery);
	}
	
	private PageResult handleResponse(SearchResponse response) throws JsonMappingException, JsonProcessingException {
		SearchHits searchHits = response.getHits();
		long total = searchHits.getTotalHits().value;
		List<HotelDoc> hotels = new ArrayList<>();
		for (SearchHit hit : searchHits.getHits()) {
			// 獲取文檔source
			String json = hit.getSourceAsString();
			// 反序列化
			HotelDoc hotelDoc = new ObjectMapper().readValue(json, HotelDoc.class);
			// 獲取排序值
			Object[] sortValues = hit.getSortValues();
			if (sortValues.length > 0) {
				Object sortValue = sortValues[0];
				hotelDoc.setDistance(sortValue);
			}
			hotels.add(hotelDoc);
		}
		return new PageResult(total, hotels);
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
		request.source().aggregation(AggregationBuilders
				.terms("brandAgg")
				.field("brand")
				.size(10)
			);
		request.source().aggregation(AggregationBuilders
				.terms("cityAgg")
				.field("city")
				.size(10)
			);
		request.source().aggregation(AggregationBuilders
				.terms("starAgg")
				.field("starName")
				.size(10)
			);
	}
}
