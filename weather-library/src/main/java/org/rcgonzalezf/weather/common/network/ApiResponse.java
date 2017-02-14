package org.rcgonzalezf.weather.common.network;

import java.util.List;
import org.rcgonzalezf.weather.common.models.converter.Data;

public interface ApiResponse<D extends Data> {

  List<D> getData();
}
