package com.wheezy.myjetpackproject.data.repository

import com.wheezy.myjetpackproject.core.model.LocationModel
import com.wheezy.myjetpackproject.core.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getLocations(token: String): Response<List<LocationModel>> {
        return apiService.getLocations("Bearer $token")
    }
}