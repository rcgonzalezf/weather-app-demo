package org.rcgonzalezf.weather.common.network;

import java.util.Map;

public interface RequestParameters<T> {

  String getQueryString();

  Map<String, String> getKeyValueParameters();

}
