package com.example.sort_out_emotions.data.repository

import com.example.sort_out_emotions.network.api.StableDiffusionApi
import com.example.sort_out_emotions.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StableDiffusionRepository {

    private val api: StableDiffusionApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.STABLE_DIFFUSION_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(StableDiffusionApi::class.java)
    }

    suspend fun generateImages(prompt: String): List<String> {
        val response = api.generateImage(prompt = prompt)
        return response.images
    }
}
