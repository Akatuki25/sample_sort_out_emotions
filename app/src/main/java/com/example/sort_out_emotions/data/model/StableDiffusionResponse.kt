// GradioResponse.kt
package com.example.sort_out_emotions.data.model

data class StableDiffusionResponse(
    val data: List<List<ImageItem>>,
    val is_generating: Boolean,
    val duration: Float,
    val average_duration: Float,
    val render_config: Any?,
    val changed_state_ids: List<Any>
)

data class ImageItem(
    val image: ImageData,
    val caption: String?
)

data class ImageData(
    val path: String,
    val url: String,
    val size: Any?,
    val orig_name: String?,
    val mime_type: String?,
    val is_stream: Boolean,
    val meta: MetaData
)

data class MetaData(
    val _type: String
)
