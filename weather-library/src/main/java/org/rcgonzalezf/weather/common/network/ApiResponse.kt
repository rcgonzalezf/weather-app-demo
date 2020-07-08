package org.rcgonzalezf.weather.common.network

import org.rcgonzalezf.weather.common.models.converter.Data

interface ApiResponse<D : Data?> {
    val data: List<D>?
}
