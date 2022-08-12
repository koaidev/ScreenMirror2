package com.bangbangcoding.screenmirror.web.ui.paste

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.FragmentPasteLinkBinding
import com.bangbangcoding.screenmirror.web.ui.CustomToast
import com.bangbangcoding.screenmirror.web.ui.base.BaseFragment
import com.bangbangcoding.screenmirror.web.utils.UrlUtils
import com.bangbangcoding.screenmirror.web.utils.Utils
import java.net.URI
import java.util.Random
import javax.inject.Inject

class PasteLinkFragment : BaseFragment() {


    lateinit var progressDialogGenaratinglink: ProgressDialog

    companion object {
        fun newInstance(): PasteLinkFragment {
            val args = Bundle()

            val fragment = PasteLinkFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var pasteLinkViewModel: PasteLinkViewModel
    private var _binding: FragmentPasteLinkBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pasteLinkViewModel =
            ViewModelProvider(this, viewModelFactory)[PasteLinkViewModel::class.java]

        _binding = FragmentPasteLinkBinding.inflate(inflater, container, false)
        val root: View = binding.root

        pasteLinkViewModel.text.observe(viewLifecycleOwner, {
            binding.textDashboard.text = it
        })
        return root
    }

    override fun setupUI() {
        binding.btnSubmit.setOnClickListener {
            clickSubmitLink()
        }
    }

    private fun clickSubmitLink() {
        val link = binding.edtLink.text.toString().trim()
        downloadVideo(link)
    }

    fun downloadVideo(url: String) {
        Log.e("ttt", "download url $url")
        if (url.isEmpty() && UrlUtils.checkURL(url)) {
            CustomToast.makeText(requireContext(), getString(R.string.enter_valid),
                CustomToast.SHORT, CustomToast.WARNING).show()
        } else {
            val rand = Random()
            val randInt1 = rand.nextInt(2)
            Log.e("ttt", "random value = $randInt1")
//            if (randInt1 == 0) {
//                showAdmobAds()
//            } else {
//                showAdmobAds_int_video()
//            }
//            Log.d("ttt", "The interstitial wasn't loaded yet.")
            when {
                url.contains("instagram.com") -> {
                    progressDialogGenaratinglink.show()
                    startInstaDownload(url)
                }
                url.contains("myjosh.in") -> {
                    var myurl = url
                    myurl = myurl.substring(myurl.indexOf("http"))
                    myurl = myurl.substring(
                        myurl.indexOf("http://share.myjosh.in/"),
                        myurl.indexOf("Download Josh for more videos like this!")
                    )
//                    DownloadVideosMain.startDownload(this, requireContext(), myurl.trim(), false)
                    Log.e("downloadFileName12", url.trim())
                }
                url.contains("audiomack") ||
                        url.contains("zili") ||
                        url.contains("xhamster") ||
                        url.contains("zingmp3") ||
                        url.contains("vidlit") ||
                        url.contains("byte.co") ||
                        url.contains("fthis.gr") ||
                        url.contains("fw.tv") ||
                        url.contains("firework.tv") ||
                        url.contains("rumble") ||
                        url.contains("traileraddict")
                -> {
                    if (progressDialogGenaratinglink != null) {
                        progressDialogGenaratinglink.dismiss()
                    }

                    val intent = Intent(activity, GetLinkThroughWebView::class.java)
                    intent.putExtra("myurlis", url)
                    startActivityForResult(intent, 2)
                }


                url.contains("bemate") -> {
                    if (progressDialogGenaratinglink != null) {
                        progressDialogGenaratinglink.dismiss()
                    }
                    val urlq = url.substring(url.indexOf("https"), url.length)
                    val intent = Intent(activity, GetLinkThroughWebView::class.java)
                    intent.putExtra("myurlis", urlq)
                    startActivityForResult(intent, 2)
                }
                url.contains("chingari") -> {
                    var myurl = url
                    myurl = myurl.substring(
                        myurl.indexOf("https://chingari.io/"),
                        myurl.indexOf("For more such entertaining")
                    )
//                    DownloadVideosMain.startDownload(this, requireContext(), myurl.trim(), false)
                    Log.e("downloadFileName12", myurl.trim())
                }
                url.contains("sck.io") || url.contains("snackvideo") -> {
                    var myurl = url
                    try {
                        if (myurl.length > 30) {
                            myurl = myurl.substring(
                                myurl.indexOf("http"),
                                myurl.indexOf("Click this")
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("ttt", "downloadVideo exception: ", e)
                    }
//                    DownloadVideosMain.startDownload(this, requireContext(), myurl.trim(), false)
                    Log.e("downloadFileName12", myurl.trim())
                }
                else -> {
                    Log.d("ttt", "loadeed yet.")
                    var myUrl = url
                    try {
                        myUrl = myUrl.substring(myUrl.indexOf("http")).trim()
                    } catch (e: Exception) {
                        Log.e("ttt", "downloadVideo exception: ", e)
                    }

//                    DownloadVideosMain.startDownload(this, requireContext(), myUrl, false)
                }
            }
        }
    }

    private fun startInstaDownload(url: String) {
//         https://www.instagram.com/p/CLBM34Rhxek/?igshid=41v6d50y6u4w
//          https://www.instagram.com/p/CLBM34Rhxek/
//           https://www.instagram.com/p/CLBM34Rhxek/?__a=1
//           https://www.instagram.com/tv/CRyVpDSAE59/
        var urlWI: String?
        try {
            val uri = URI(url)
            urlWI = URI(
                uri.scheme,
                uri.authority,
                uri.path,
                null,  // Ignore the query part of the input url
                uri.fragment
            ).toString()
        } catch (ex: java.lang.Exception) {
            progressDialogGenaratinglink.dismiss()
            Utils.showToast(requireActivity(), getString(R.string.invalid_url))
            return
        }
        Log.e("ttt", "startInstaDownload: $url")
        var urlWithoutLetter: String? = urlWI
        Log.e("ttt", "startInstaDownload: $urlWithoutLetter")

        urlWithoutLetter = "$urlWithoutLetter?__a=1"
        Log.e("ttt", "startInstaDownload: $urlWithoutLetter")


        if (urlWithoutLetter.contains("/reel/")) {
            urlWithoutLetter = urlWithoutLetter.replace("/reel/", "/p/")
        }

        if (urlWithoutLetter.contains("/tv/")) {
            urlWithoutLetter = urlWithoutLetter.replace("/tv/", "/p/")
        }
        Log.e("ttt", "startInstaDownload: $urlWithoutLetter")

//        AlertDialog.Builder(requireActivity())
//            .setTitle("Select Methode")
//            .setCancelable(false)
//            .setNegativeButton("Methode 1"
//            ) { dialog, which ->
//                try {
//                    Log.e("ttt", "Methode:")
//                    val sharedPrefsFor = SharedPrefsForInstagram(requireActivity())
//                    val map = sharedPrefsFor.getPreference(SharedPrefsForInstagram.PREFERENCE)
//                    if (map != null && map.get(SharedPrefsForInstagram.PREFERENCE_USERID) != null && map.get(
//                            SharedPrefsForInstagram.PREFERENCE_USERID
//                        ) != "oopsDintWork" && map.get(SharedPrefsForInstagram.PREFERENCE_USERID) != ""
//                    ) {
//                        Log.e("ttt", "Methode: 4.7")
//                        downloadInstagramImageOrVideodata_old_withlogin(
//                            urlWithoutLetter, "ds_user_id=" + map.get(
//                                SharedPrefsForInstagram.PREFERENCE_USERID
//                            )
//                                    + "; sessionid=" + map.get(SharedPrefsForInstagram.PREFERENCE_SESSIONID)
//                        )
//
//                    } else {
//                        Log.e("ttt", "Methode: 4.8")
//
//                        downloadInstagramImageOrVideodataOld(
//                            urlWithoutLetter,
//                            ""
//                        )
//
//                    }
//
//
//                } catch (e: java.lang.Exception) {
//                    progressDralogGenaratinglink.dismiss()
//                    Log.e("ttt", "Methode: 5")
//                    e.printStackTrace()
//                    Utils.showToast(requireActivity(), getString(R.string.error_occ))
//                }
//            }.setPositiveButton("Methode 2"
//            ) { dialog, which ->
//                try {
//                    Log.e("ttt", "Methode 2: 4")
//                    val sharedPrefsFor = SharedPrefsForInstagram(requireActivity())
//                    val map = sharedPrefsFor.getPreference(SharedPrefsForInstagram.PREFERENCE)
//                    if (map != null && map.get(SharedPrefsForInstagram.PREFERENCE_USERID) != null && map.get(
//                            SharedPrefsForInstagram.PREFERENCE_USERID
//                        ) != "oopsDintWork" && map.get(SharedPrefsForInstagram.PREFERENCE_USERID) != ""
//                    ) {
//
//                        Log.e("ttt", "Methode 2: 5.2")
//                        downloadInstagramImageOrVideodata_withlogin(
//                            urlWithoutLetter, "ds_user_id=" + map.get(
//                                SharedPrefsForInstagram.PREFERENCE_USERID
//                            )
//                                    + "; sessionid=" + map.get(SharedPrefsForInstagram.PREFERENCE_SESSIONID)
//                        )
//
//                    } else {
//                        System.err.println("workkkkkkkkk 4.5")
//
////                        pasteLinkViewModel.downloadInstagramImageOrVideodata(url, Utils.showCookies(url))
////                        downloadInstagramImageOrVideodata(
////                            urlWithoutLetter,
////                            iUtils.myInstagramTempCookies
////                        )
//                    }
//                } catch (e: java.lang.Exception) {
//                    progressDralogGenaratinglink.dismiss()
//                    System.err.println("workkkkkkkkk 5.1")
//                    e.printStackTrace()
//                    Utils.showToast(requireActivity(), getString(R.string.error_occ))
//                }
//            }.show()
    }

//    fun downloadInstagramImageOrVideodataOld(url: String?, cookie: String?) {
//        val random1 = Random()
//        val j = random1.nextInt(UrlUtils.UserAgentsList.size)
//        object : Thread() {
//            override fun run() {
//                Looper.prepare()
//                val client = OkHttpClient().newBuilder()
//                    .build()
//                val request: Request = Request.Builder()
//                    .url(url!!)
//                    .method("GET", null)
//                    .addHeader("Cookie", cookie!!)
//                    .addHeader(
//                        "User-Agent",
//                        UrlUtils.UserAgentsList[j]
//                    )
//                    .build()
//                try {
//                    val response = client.newCall(request).execute()
//
//                    System.err.println("workkkkkkkkk 6 " + response.code)
//
//                    if (response.code == 200) {
//
//                        try {
//                            val listType: Type =
//                                object : TypeToken<ModelInstagramResponse?>() {}.type
//                            val modelInstagramResponse: ModelInstagramResponse = Gson().fromJson(
//                                response.body!!.string(),
//                                listType
//                            )
//
//
//                            if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
//                                val modelGetEdgetoNode: ModelGetEdgetoNode =
//                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children
//
//                                val modelEdNodeArrayList: List<ModelEdNode> =
//                                    modelGetEdgetoNode.modelEdNodes
//                                for (i in modelEdNodeArrayList.indices) {
//                                    if (modelEdNodeArrayList[i].modelNode.isIs_video) {
//                                        myVideoUrlIs = modelEdNodeArrayList[i].modelNode.video_url
//                                        DownloadFileMain.startDownloading(
//                                            activity,
//                                            myVideoUrlIs,
//                                            UrlUtils.getVideoFilenameFromURL(myVideoUrlIs),
//                                            ".mp4"
//                                        )
//                                        // etText.setText("");
//
//
//                                        activity?.runOnUiThread(Runnable {
//
//                                            progressDralogGenaratinglink.dismiss()
//
//                                        })
//
//
//                                        myVideoUrlIs = ""
//                                    } else {
//                                        myPhotoUrlIs =
//                                            modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src
//                                        DownloadFileMain.startDownloading(
//                                            activity,
//                                            myPhotoUrlIs,
//                                            iUtils.getImageFilenameFromURL(myPhotoUrlIs),
//                                            ".png"
//                                        )
//                                        myPhotoUrlIs = ""
//                                        activity?.runOnUiThread(Runnable {
//
//                                            progressDralogGenaratinglink.dismiss()
//
//                                        })
//                                        // etText.setText("");
//                                    }
//                                }
//                            } else {
//                                val isVideo =
//                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video
//                                if (isVideo) {
//                                    myVideoUrlIs =
//                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url
//                                    DownloadFileMain.startDownloading(
//                                        activity,
//                                        myVideoUrlIs,
//                                        iUtils.getVideoFilenameFromURL(myVideoUrlIs),
//                                        ".mp4"
//                                    )
//                                    activity?.runOnUiThread(Runnable {
//                                        progressDralogGenaratinglink.dismiss()
//                                    })
//                                    myVideoUrlIs = ""
//                                } else {
//                                    myPhotoUrlIs =
//                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src
//                                    DownloadFileMain.startDownloading(
//                                        activity,
//                                        myPhotoUrlIs,
//                                        iUtils.getImageFilenameFromURL(myPhotoUrlIs),
//                                        ".png"
//                                    )
//                                    activity?.runOnUiThread(Runnable {
//
//                                        progressDralogGenaratinglink.dismiss()
//
//                                    })
//                                    myPhotoUrlIs = ""
//                                }
//                            }
//                        } catch (e: java.lang.Exception) {
//                            System.err.println("workkkkkkkkk 5nn errrr " + e.message)
//
//
//                            e.printStackTrace()
//                            activity?.runOnUiThread(Runnable {
//                                view?.progress_loading_bar?.visibility = View.GONE
//
//                                progressDralogGenaratinglink.dismiss()
//
//
//                                val alertDialog = AlertDialog.Builder(activity!!).create()
//                                alertDialog.setTitle(getString(R.string.logininsta))
//                                alertDialog.setMessage(getString(R.string.urlisprivate))
//                                alertDialog.setButton(
//                                    AlertDialog.BUTTON_POSITIVE, getString(R.string.logininsta)
//                                ) { dialog, _ ->
//                                    dialog.dismiss()
//
//
//                                    val intent = Intent(
//                                        activity,
//                                        InstagramLoginActivity::class.java
//                                    )
//                                    startActivityForResult(intent, 200)
//
//                                }
//
//                                alertDialog.setButton(
//                                    AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)
//                                ) { dialog, _ ->
//                                    dialog.dismiss()
//
//
//                                }
//                                alertDialog.show()
//
//                            })
//
//
//                        }
//
//
//                    } else {
//
//                        object : Thread() {
//                            override fun run() {
//
//                                val client = OkHttpClient().newBuilder()
//                                    .build()
//                                val request: Request = Request.Builder()
//                                    .url(url)
//                                    .method("GET", null)
//                                    .addHeader("Cookie", iUtils.myInstagramTempCookies)
//                                    .addHeader(
//                                        "User-Agent",
//                                        iUtils.UserAgentsList[j]
//                                    ).build()
//                                try {
//
//
//                                    val response1: Response = client.newCall(request).execute()
//
//                                    if (response1.code == 200) {
//
//                                        try {
//                                            val listType: Type =
//                                                object :
//                                                    TypeToken<ModelInstagramResponse?>() {}.type
//                                            val modelInstagramResponse: ModelInstagramResponse =
//                                                Gson().fromJson(
//                                                    response1.body!!.string(),
//                                                    listType
//                                                )
//
//
//                                            if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
//                                                val modelGetEdgetoNode: ModelGetEdgeToNode =
//                                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children
//
//                                                val modelEdNodeArrayList: List<ModelEdNode> =
//                                                    modelGetEdgetoNode.modelEdNodes
//                                                for (i in 0 until modelEdNodeArrayList.size) {
//                                                    if (modelEdNodeArrayList[i].modelNode.isIs_video) {
//                                                        myVideoUrlIs =
//                                                            modelEdNodeArrayList[i].modelNode.video_url
//                                                        DownloadFileMain.startDownloading(
//                                                            activity,
//                                                            myVideoUrlIs,
//                                                            iUtils.getVideoFilenameFromURL(
//                                                                myVideoUrlIs
//                                                            ),
//                                                            ".mp4"
//                                                        )
//                                                        // etText.setText("");
//
//
//                                                        activity?.runOnUiThread(Runnable {
//
//                                                            progressDralogGenaratinglink.dismiss()
//
//                                                        })
//
//
//                                                        myVideoUrlIs = ""
//                                                    } else {
//                                                        myPhotoUrlIs =
//                                                            modelEdNodeArrayList[i].modelNode.dis[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src
//                                                        DownloadFileMain.startDownloading(
//                                                            activity,
//                                                            myPhotoUrlIs,
//                                                            iUtils.getImageFilenameFromURL(
//                                                                myPhotoUrlIs
//                                                            ),
//                                                            ".png"
//                                                        )
//                                                        myPhotoUrlIs = ""
//                                                        activity?.runOnUiThread(Runnable {
//
//                                                            progressDralogGenaratinglink.dismiss()
//
//                                                        })
//                                                        // etText.setText("");
//                                                    }
//                                                }
//                                            } else {
//                                                val isVideo =
//                                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video
//                                                if (isVideo) {
//                                                    myVideoUrlIs =
//                                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url
//                                                    DownloadFileMain.startDownloading(
//                                                        activity,
//                                                        myVideoUrlIs,
//                                                        iUtils.getVideoFilenameFromURL(myVideoUrlIs),
//                                                        ".mp4"
//                                                    )
//                                                    activity?.runOnUiThread(Runnable {
//
//                                                        progressDralogGenaratinglink.dismiss()
//
//                                                    })
//                                                    myVideoUrlIs = ""
//                                                } else {
//                                                    myPhotoUrlIs =
//                                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src
//                                                    DownloadFileMain.startDownloading(
//                                                        activity,
//                                                        myPhotoUrlIs,
//                                                        iUtils.getImageFilenameFromURL(myPhotoUrlIs),
//                                                        ".png"
//                                                    )
//                                                    activity?.runOnUiThread(Runnable {
//
//                                                        progressDralogGenaratinglink.dismiss()
//
//                                                    })
//                                                    myPhotoUrlIs = ""
//                                                }
//                                            }
//                                        } catch
//                                            (e: java.lang.Exception) {
//                                            System.err.println("workkkkkkkkk 4vvv errrr " + e.message)
//
//
//                                            e.printStackTrace()
//                                            activity?.runOnUiThread(Runnable {
//                                                view?.progress_loading_bar?.visibility = View.GONE
//
//                                                progressDralogGenaratinglink.dismiss()
//
//
//                                            })
//
//
//                                        }
//
//
//                                    } else {
//
//                                        System.err.println("workkkkkkkkk 6bbb errrr ")
//
//
//                                        activity?.runOnUiThread(Runnable {
//                                            view?.progress_loading_bar?.visibility = View.GONE
//
//                                            progressDralogGenaratinglink.dismiss()
//                                            val alertDialog =
//                                                AlertDialog.Builder(activity!!).create()
//                                            alertDialog.setTitle(getString(R.string.logininsta))
//                                            alertDialog.setMessage(getString(R.string.urlisprivate))
//                                            alertDialog.setButton(
//                                                AlertDialog.BUTTON_POSITIVE,
//                                                getString(R.string.logininsta)
//                                            ) { dialog, _ ->
//                                                dialog.dismiss()
//
//
//                                                val intent = Intent(
//                                                    activity,
//                                                    InstagramLoginActivity::class.java
//                                                )
//                                                startActivityForResult(intent, 200)
//
//                                            }
//
//                                            alertDialog.setButton(
//                                                AlertDialog.BUTTON_NEGATIVE,
//                                                getString(R.string.cancel)
//                                            ) { dialog, _ ->
//                                                dialog.dismiss()
//
//
//                                            }
//                                            alertDialog.show()
//                                        })
//
//                                    }
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
//                            }
//                        }.start()
//
//
//                    }
//                    println("working errpr \t Value: " + response.body!!.string())
//                } catch (e: Exception) {
//
//                    try {
//                        println("response1122334455:   " + "Failed1 " + e.message)
//                        activity?.runOnUiThread(Runnable {
//                            view?.progress_loading_bar?.visibility = View.GONE
//                            progressDralogGenaratinglink.dismiss(
//                        })
//                    } catch (e: Exception) {
//
//                    }
//                }
//            }
//        }.start()
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}