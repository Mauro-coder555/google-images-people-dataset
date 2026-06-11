package com.mycompany.imagesdataset;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

public class UrlQuery {

	private static final String URL_GUIDE = "https://images.google.com.ar/images?q=%s&tbs=";
	private static final Predicate<String> NOT_NULL_NOR_EMPTY = s -> !Strings.isNullOrEmpty(s);
	private final String query;
	private final String url;

	public UrlQuery(String query, List<String> filters) {
		this.query = query;
		this.url = buildUrl(filters);
	}

	public String getUrl() {
		return url;
	}

	public String getQuery() {
		return query;
	}

	private static String normalizeName(String name) {
		return name.replace(' ', '+');
	}

	private String buildUrl(List<String> filters) {
		List<String> nonNullAndNonEmptyFilters = filters.stream()
				.filter(NOT_NULL_NOR_EMPTY)
				.collect(Collectors.toList());
		return String.format(URL_GUIDE, normalizeName(query)) + String.join(",", nonNullAndNonEmptyFilters);
	}
}
