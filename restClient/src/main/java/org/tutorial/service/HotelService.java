package org.tutorial.service;

import java.util.List;
import java.util.Map;

import org.tutorial.pojo.PageResult;
import org.tutorial.pojo.RequestParams;

public interface HotelService {
	PageResult search(RequestParams params);

	Map<String, List<String>> filters(RequestParams params);

	List<String> getSuggestions(String prefix);
}
