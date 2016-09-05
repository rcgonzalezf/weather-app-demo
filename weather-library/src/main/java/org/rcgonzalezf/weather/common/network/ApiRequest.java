package org.rcgonzalezf.weather.common.network;

public interface ApiRequest<T extends RequestParameters, A extends ApiCallback<? extends ApiResponse, ? extends ApiError>> {

  String getBaseUrl();

  String getMethodName();

  void execute(A apiCallback);

  void addRequestParameters(T requestParameters);
}
