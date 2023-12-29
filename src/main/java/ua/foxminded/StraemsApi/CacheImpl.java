package ua.foxminded.StraemsApi;

import java.util.HashMap;
import java.util.Map;

public class CacheImpl implements Cache<String, String> {

	private Map<String, String> cacheMap = new HashMap<>();

	@Override
	public String get(String key) {
		return cacheMap.get(key);
	}

	@Override
	public boolean containsKey(String key) {
		return cacheMap.containsKey(key);
	}

	@Override
	public void put(String key, String value) {
		cacheMap.put(key, value);
	}

}
