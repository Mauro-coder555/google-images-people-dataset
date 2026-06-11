package com.mycompany.imagesdataset;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

public class SearchFilters {

	private static final Map<String, String> RESOLUTIONS = Map.of(
			"400x300", "qsvga",
			"640x480", "vga",
			"800x600", "svga",
			"1024x768", "xga");
	private static final Map<String, String> LICENCES = Map.of(
			"Licencias comerciales y otras", "ol",
			"Licencias creative commons", "cl");
	private static final Map<String, String> FILTER_PARAMETERS = Map.of(
			"resolution", "isz:lt,islt",
			"type", "itp",
			"format", "ift",
			"licences", "sur");

	private SearchFilters() {
	}

	public static String getFilterParameterWithValue(String propertie, String value) {
		if (value.isEmpty())
			return "";
		else {
			if (propertie.equals("resolution") && value.contains("x"))
				value = RESOLUTIONS.get(value);
			if (propertie.equals("licences"))
				value = LICENCES.get(value);
			return FILTER_PARAMETERS.get(propertie) + ":" + value;
		}
	}

	public static List<String> getFilters(Map<String, String> props) {
		return props.keySet().stream()
				.filter(props::containsKey)
				.map(key -> getFilterParameterWithValue(key, props.get(key)))
				.filter(filter -> !Strings.isNullOrEmpty(filter))
				.collect(Collectors.toList());
	}

	public static List<String> getFilters(Properties props) {
		Map<String, String> map = FILTER_PARAMETERS.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> props.getProperty(e.getKey())));
		return getFilters(map);
	}

}
