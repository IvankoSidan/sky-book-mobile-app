package com.wheezy.myjetpackproject.Repository

import com.wheezy.myjetpackproject.Data.Model.LocationModel
import com.wheezy.myjetpackproject.Network.ApiService
import retrofit2.Response
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getLocations(token: String): Response<List<LocationModel>> {
        return apiService.getLocations("Bearer $token")
    }
}