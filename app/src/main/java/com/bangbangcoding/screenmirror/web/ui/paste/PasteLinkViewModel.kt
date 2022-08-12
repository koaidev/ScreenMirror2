package com.bangbangcoding.screenmirror.web.ui.paste

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangbangcoding.screenmirror.web.data.repository.ConfigRepository
import com.bangbangcoding.screenmirror.web.data.repository.TopPagesRepository
import com.bangbangcoding.screenmirror.web.data.repository.VideoRepository
import com.bangbangcoding.screenmirror.web.ui.base.BaseViewModel
import com.bangbangcoding.screenmirror.web.utils.scheduler.BaseSchedulers
import javax.inject.Inject

class PasteLinkViewModel @Inject constructor(
    private val topPagesRepository: TopPagesRepository,
    private val configRepository: ConfigRepository,
    private val videoRepository: VideoRepository,
    private val baseSchedulers: BaseSchedulers
) : BaseViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is paste link page"
    }
    val text: LiveData<String> = _text

    override fun start() {
        //TODO("Not yet implemented")
    }

    override fun stop() {
        //TODO("Not yet implemented")
    }

//    fun downloadInstagramImageOrVideodata(url: String, showCookies: String?) {
//
//        val random1 = Random()
//        val j = random1.nextInt(UrlUtils.UserAgentsList.size)
//        var Cookie = showCookies
//        if (TextUtils.isEmpty(Cookie)) {
//            Cookie = ""
//        }
//        val apiService: RetrofitApiInterface =
//            RetrofitClient.getClient().create(RetrofitApiInterface::class.java)
//
//
//        val callResult: Call<JsonObject> = apiService.getInstagramData(
//            URL,
//            Cookie,
//            iUtils.UserAgentsList[j]
//        )
//        callResult.enqueue(object : Callback<JsonObject?> {
//            override fun onResponse(
//                call: Call<JsonObject?>,
//                response: retrofit2.Response<JsonObject?>
//            ) {
//                println("response1122334455 ress :   " + response.body())
//                try {
//
//
////                                val userdata = response.body()!!.getAsJsonObject("graphql")
////                                    .getAsJsonObject("shortcode_media")
////                                binding.profileFollowersNumberTextview.setText(
////                                    userdata.getAsJsonObject(
////                                        "edge_followed_by"
////                                    )["count"].asString
////                                )
////                                binding.profileFollowingNumberTextview.setText(
////                                    userdata.getAsJsonObject(
////                                        "edge_follow"
////                                    )["count"].asString
////                                )
////                                binding.profilePostNumberTextview.setText(userdata.getAsJsonObject("edge_owner_to_timeline_media")["count"].asString)
////                                binding.profileLongIdTextview.setText(userdata["username"].asString)
////
//
//
//                    val listType = object : TypeToken<ModelInstagramResponse?>() {}.type
//                    val modelInstagramResponse: ModelInstagramResponse? = GsonBuilder().create()
//                        .fromJson<ModelInstagramResponse>(
//                            response.body().toString(),
//                            listType
//                        )
//
//
//                    if (modelInstagramResponse != null) {
//                        if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
//                            val modelGetEdgetoNode: ModelGetEdgetoNode =
//                                modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children
//
//                            val modelEdNodeArrayList: List<ModelEdNode> =
//                                modelGetEdgetoNode.modelEdNodes
//                            for (i in 0 until modelEdNodeArrayList.size) {
//                                if (modelEdNodeArrayList[i].modelNode.isIs_video) {
//                                    myVideoUrlIs =
//                                        modelEdNodeArrayList[i].modelNode.video_url
//                                    DownloadFileMain.startDownloading(
//                                        requireActivity(),
//                                        myVideoUrlIs,
//                                        iUtils.getVideoFilenameFromURL(
//                                            myVideoUrlIs
//                                        ),
//                                        ".mp4"
//                                    )
//                                    // etText.setText("");
//
//
//                                    requireActivity().runOnUiThread(Runnable {
//
//                                        progressDralogGenaratinglink.dismiss()
//
//                                    })
//
//
//                                    myVideoUrlIs = ""
//                                } else {
//                                    myPhotoUrlIs =
//                                        modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src
//                                    DownloadFileMain.startDownloading(
//                                        requireActivity(),
//                                        myPhotoUrlIs,
//                                        iUtils.getImageFilenameFromURL(
//                                            myPhotoUrlIs
//                                        ),
//                                        ".png"
//                                    )
//                                    myPhotoUrlIs = ""
//                                    requireActivity().runOnUiThread(Runnable {
//
//                                        progressDralogGenaratinglink.dismiss()
//
//                                    })
//                                    // etText.setText("");
//                                }
//                            }
//                        } else {
//                            val isVideo =
//                                modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video
//                            if (isVideo) {
//                                myVideoUrlIs =
//                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url
//                                DownloadFileMain.startDownloading(
//                                    requireActivity(),
//                                    myVideoUrlIs,
//                                    iUtils.getVideoFilenameFromURL(myVideoUrlIs),
//                                    ".mp4"
//                                )
//                                requireActivity().runOnUiThread(Runnable {
//
//                                    progressDralogGenaratinglink.dismiss()
//
//                                })
//                                myVideoUrlIs = ""
//                            } else {
//                                myPhotoUrlIs =
//                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src
//                                DownloadFileMain.startDownloading(
//                                    requireActivity(),
//                                    myPhotoUrlIs,
//                                    iUtils.getImageFilenameFromURL(myPhotoUrlIs),
//                                    ".png"
//                                )
//                                requireActivity().runOnUiThread(Runnable {
//
//                                    progressDralogGenaratinglink.dismiss()
//
//                                })
//                                myPhotoUrlIs = ""
//                            }
//                        }
//                    } else {
//                        Toast.makeText(
//                            requireActivity(),
//                            resources.getString(R.string.txt_some_thing),
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        requireActivity().runOnUiThread(Runnable {
//
//                            progressDralogGenaratinglink.dismiss()
//
//                        })
//
//                    }
//
//
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//
//                    Toast.makeText(
//                        requireActivity(),
//                        resources.getString(R.string.txt_some_thing),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    println("response1122334455 exe 1:   " + e.localizedMessage)
//
//                    requireActivity().runOnUiThread(Runnable {
//
//                        progressDralogGenaratinglink.dismiss()
//
//                    })
//                }
//            }
//
//            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
//                println("response1122334455:   " + "Failed0")
//                requireActivity().runOnUiThread(Runnable {
//                    progressDralogGenaratinglink.dismiss()
//                })
//
//                Toast.makeText(
//                    requireActivity(),
//                    resources.getString(R.string.txt_some_thing),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        })
//    }
}