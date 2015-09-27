package org.rcgonzalezf.weather.common.network;

public interface ApiRequest<T extends RequestParameters> {

  String getBaseUrl();

  String getMethodName();

  void execute(ApiCallback apiCallback);

  void addRequestParameters(T requestParameters);
}
