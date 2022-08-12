package com.bangbangcoding.screenmirror.web.ui.paste

class DownloadVideoMain {

//    var dialog = Dialog(DownloadVideosMain.mContext)
//    dialog.setContentView(R.layout.tiktok_optionselect_dialog)
//    var finalUrl1: String = url
//    var methode0 = dialog.findViewById<Button>(R.id.dig_btn_met0)
//    var methode1 = dialog.findViewById<Button>(R.id.dig_btn_met1)
//    var methode2 = dialog.findViewById<Button>(R.id.dig_btn_met2)
//    var methode3 = dialog.findViewById<Button>(R.id.dig_btn_met3)
//    var methode4 = dialog.findViewById<Button>(R.id.dig_btn_met4)
//    var dig_btn_cancel = dialog.findViewById<Button>(R.id.dig_btn_cancel)
//    var finalUrl2: String = url
//    methode0.setOnClickListener(android.view.View.OnClickListener (
//    {
//        v:android.view.View? -> dialog.dismiss()
//        object : Thread() {
//            override fun run() {
//                try {
//                    Looper.prepare()
//                    Log.e("ttt", "myresponseis111 exp 888")
//                    val client = OkHttpClient().newBuilder()
//                        .build()
//                    val mediaType: MediaType =
//                        parse.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
//                    val body: RequestBody = Builder().setType(MultipartBody.FORM)
//                        .addFormDataPart("ic-request", "true")
//                        .addFormDataPart("id", finalUrl1)
//                        .addFormDataPart("ic-element-id", "main_page_form")
//                        .addFormDataPart("ic-id", "1")
//                        .addFormDataPart("ic-target-id", "active_container")
//                        .addFormDataPart("ic-trigger-id", "main_page_form")
//                        .addFormDataPart(
//                            "token",
//                            "493eaebbf47aa90e1cdfa0f8faf7d04cle0f45a38aa759c6a13fea91d5036dc3b"
//                        )
//                        .addFormDataPart("ic-current-url", "")
//                        .addFormDataPart("ic-select-from-response", "#id4fbbea")
//                        .addFormDataPart("_method", "nPOST")
//                        .build()
//                    val request: Request = Builder()
//                        .url("https://tiktokdownload.online/results")
//                        .method("POST", body)
//                        .addHeader("cache-contro", "no-cache")
//                        .addHeader(
//                            "content-type",
//                            "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW"
//                        )
//                        .addHeader("origin", "https://tiktokdownload.online")
//                        .addHeader("postman-token", "c866af6b-b900-cf0f-2043-1296b0e5362a")
//                        .addHeader("sec-fetch-dest", "empty")
//                        .addHeader("sec-fetch-mode", "cors")
//                        .addHeader("sec-fetch-site", "same-origin")
//                        .addHeader(
//                            "user-agent",
//                            "Mozilla/5.0 (Windows; U; Windows NT 5.1; rv:1.7.3) Gecko/20041001 Firefox/0.10.1"
//                        )
//                        .addHeader("x-http-method-override", "POST")
//                        .addHeader("x-ic-request", "true")
//                        .addHeader("x-requested-with", "XMLHttpRequest")
//                        .addHeader("Cookie", "PHPSESSID=9db511ae60292e341ab84afc4a6127cf")
//                        .build()
//                    val response = client.newCall(request).execute()
//                    val document = Jsoup.parse(response.body.string())
//                    val arrayListLinks = ArrayList<String>()
//                    val elements = document.select("a")
//                    for (element in elements) {
//                        if (element.attr("href")
//                                .contains("tiktok") && element.attr("href").length > 150
//                        ) arrayListLinks.add(element.attr("href"))
//                    }
//                    if (!DownloadVideosMain.fromService) {
//                        DownloadVideosMain.pd.dismiss()
//                    }
//                    sleep(500)
//                    DownloadFileMain.startDownloading(
//                        context,
//                        arrayListLinks[1], "Tiktok_" + System.currentTimeMillis(), ".mp4"
//                    )
//                } catch (unused: Exception) {
//                    Log.e("ttt", "myresponseis111 exp " + unused.message)
//                    if (!DownloadVideosMain.fromService) {
//                        DownloadVideosMain.pd.dismiss()
//                    }
//                    Utils.showToast(
//                        DownloadVideosMain.mContext,
//                        DownloadVideosMain.mContext.resources.getString(R.string.txt_some_thing)
//                    )
//                }
//            }
//        }.start()
//    }))
//    methode1.setOnClickListener(android.view.View.OnClickListener (
//    {
//        v:android.view.View? -> dialog.dismiss()
//        Log.e("ttt", "wojfdjhfdjh myfgsdyfsfus=$finalUrl1")
//        if (!DownloadVideosMain.fromService) {
//            DownloadVideosMain.pd.dismiss()
//        }
//        val intent =
//            Intent(DownloadVideosMain.mContext, TikTokDownloadCloudBypassWebview::class.java)
//        intent.putExtra("myvidurl", finalUrl1)
//        DownloadVideosMain.mContext.startActivity(intent)
//    }))
//    methode2.setOnClickListener(android.view.View.OnClickListener (
//    {
//        v:android.view.View? -> dialog.dismiss()
//        object : Thread() {
//            override fun run() {
//                try {
//                    Looper.prepare()
//                    Log.e("ttt", "myresponseis111 exp 888")
//                    val client = OkHttpClient().newBuilder()
//                        .build()
//                    val mediaType: MediaType = parse.parse("application/json")
//                    val body = RequestBody.create(
//                        mediaType,
//                        "{\n    \"url\":\"$finalUrl2\",\n    \"key\":\"ZGJUevKRf\",\n    \"s\":\"azzzzSdtIGFuIG9sZCBtb3RoZXIgd2l0aCBzbWFsbCBjaGlsZHJlbiA6KCwgcGxlYXNlIGRvbid0IGRvIHRoYXQgYW55bW9yZQ==\",\n    \"v\":\"1\"\n\n}"
//                    )
//                    val request: Request = Builder()
//                        .url("https://tiktokfull.com/download")
//                        .method("POST", body)
//                        .addHeader("Content-Type", "application/json")
//                        .addHeader(
//                            "Authorization",
//                            "Bearer " + Utils.showCookies("https://tiktokfull.com/")
//                        )
//                        .addHeader(
//                            "Cookie",
//                            "x-token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhNDcyNjQ1MS0zNGMwLTQ4MzYtOWRkYS03ZjdiNTgxYTQwYTMiLCJpYXQiOjE2MzAzNDAwNTgsImV4cCI6MTYzMDM0MDk1OH0.ui0xLnrsntL2ktMe5AwwT9NfAyskZq9jdvO5yWmBEWI; x-tt=1630340058861"
//                        )
//                        .build()
//                    val response = client.newCall(request).execute()
//                    var urlisddd = ""
//                    val jsonObject = JSONObject(response.body.string())
//                    Log.e("ttt", "mysjdjhdjkh tiktok_res $jsonObject")
//                    if (jsonObject.getInt("status_code") == 200) {
//                        urlisddd =
//                            jsonObject.getJSONObject("data").getJSONArray("links").getJSONObject(1)
//                                .getString("url")
//                        if (!DownloadVideosMain.fromService) {
//                            DownloadVideosMain.pd.dismiss()
//                        }
//                        sleep(500)
//                        DownloadFileMain.startDownloading(
//                            context,
//                            urlisddd,
//                            "Tiktok_" + System.currentTimeMillis(),
//                            ".mp4"
//                        )
//                    } else {
//                        if (!DownloadVideosMain.fromService) {
//                            DownloadVideosMain.pd.dismiss()
//                        }
//                        Toast.makeText(
//                            DownloadVideosMain.mContext,
//                            DownloadVideosMain.mContext.resources.getString(R.string.txt_some_thing),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                } catch (unused: Exception) {
//                    Log.e("ttt", "myresponseis111 exp " + unused.message)
//                    if (!DownloadVideosMain.fromService) {
//                        DownloadVideosMain.pd.dismiss()
//                    }
//                    Toast.makeText(
//                        DownloadVideosMain.mContext,
//                        DownloadVideosMain.mContext.resources.getString(R.string.txt_some_thing),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }.start()
//    }))
//    methode3.setOnClickListener(android.view.View.OnClickListener (
//    {
//        v:android.view.View? -> dialog.dismiss()
//        Log.e("ttt", "wojfdjhfdjh myfgsdyfsfus=$finalUrl1")
//        if (!DownloadVideosMain.fromService) {
//            DownloadVideosMain.pd.dismiss()
//        }
//        val intent = Intent(
//            DownloadVideosMain.mContext,
//            TikTokDownloadCloudBypassWebview_method_3::class.java
//        )
//        intent.putExtra("myvidurl", finalUrl1)
//        DownloadVideosMain.mContext.startActivity(intent)
//    }))
//    methode4.setOnClickListener(android.view.View.OnClickListener (
//    {
//        v:android.view.View? -> dialog.dismiss()
//        Log.e("ttt", "wojfdjhfdjh myfgsdyfsfus=$finalUrl1")
//        if (!DownloadVideosMain.fromService) {
//            DownloadVideosMain.pd.dismiss()
//        }
//        val intent = Intent(
//            DownloadVideosMain.mContext,
//            TikTokDownloadCloudBypassWebview_method_5::class.java
//        )
//        intent.putExtra("myvidurl", finalUrl1)
//        DownloadVideosMain.mContext.startActivity(intent)
//    }))
//    dig_btn_cancel.setOnClickListener(android.view.View.OnClickListener (
//    {
//        v:android.view.View? -> dialog.dismiss()
//        if (!DownloadVideosMain.fromService) {
//            DownloadVideosMain.pd.dismiss()
//        }
//    }))
//    dialog.setCancelable(false)
//    dialog.show()

//    companion object {
//        private val DOWNLOAD_NOTIFICATION_ID = 231
//
//        //        fun createDialog() {
////
////            if(!((Activity) Mcontext).isFinishing()){
////
////                Dialog dialog = new Dialog(Mcontext);
////
////                dialog.setContentView(R.layout.tiktok_optionselect_dialog);
////
////                String finalUrl1 = url;
////
////                Button methode0 = dialog.findViewById(R.id.dig_btn_met0);
////                Button methode1 = dialog.findViewById(R.id.dig_btn_met1);
////                Button methode2 = dialog.findViewById(R.id.dig_btn_met2);
////                Button methode3 = dialog.findViewById(R.id.dig_btn_met3);
////                Button methode4 = dialog.findViewById(R.id.dig_btn_met4);
////
////                Button dig_btn_cancel = dialog.findViewById(R.id.dig_btn_cancel);
////
////
////                String finalUrl2 = url;
////                methode0.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        dialog.dismiss();
////                        new Thread() {
////                            @Override
////                            public void run() {
////                                try {
////                                    Looper.prepare();
////
////                                    System.out.println("myresponseis111 exp 888");
////
////                                    OkHttpClient client = new OkHttpClient().newBuilder()
////                                        .build();
////                                    MediaType mediaType = MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
////                                    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
////                                        .addFormDataPart("ic-request", "true")
////                                        .addFormDataPart("id", finalUrl1)
////                                        .addFormDataPart("ic-element-id", "main_page_form")
////                                        .addFormDataPart("ic-id", "1")
////                                        .addFormDataPart("ic-target-id", "active_container")
////                                        .addFormDataPart("ic-trigger-id", "main_page_form")
////                                        .addFormDataPart("token", "493eaebbf47aa90e1cdfa0f8faf7d04cle0f45a38aa759c6a13fea91d5036dc3b")
////                                        .addFormDataPart("ic-current-url", "")
////                                        .addFormDataPart("ic-select-from-response", "#id4fbbea")
////                                        .addFormDataPart("_method", "nPOST")
////                                        .build();
////                                    Request request = new Request.Builder()
////                                        .url("https://tiktokdownload.online/results")
////                                        .method("POST", body)
////                                        .addHeader("cache-contro", "no-cache")
////                                        .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
////                                        .addHeader("origin", "https://tiktokdownload.online")
////                                        .addHeader("postman-token", "c866af6b-b900-cf0f-2043-1296b0e5362a")
////                                        .addHeader("sec-fetch-dest", "empty")
////                                        .addHeader("sec-fetch-mode", "cors")
////                                        .addHeader("sec-fetch-site", "same-origin")
////                                        .addHeader("user-agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; rv:1.7.3) Gecko/20041001 Firefox/0.10.1")
////                                        .addHeader("x-http-method-override", "POST")
////                                        .addHeader("x-ic-request", "true")
////                                        .addHeader("x-requested-with", "XMLHttpRequest")
////                                        .addHeader("Cookie", "PHPSESSID=9db511ae60292e341ab84afc4a6127cf")
////                                        .build();
////
////                                    Response response = client.newCall(request).execute();
////                                    Document document = Jsoup.parse(response.body().string());
////                                    ArrayList<String> arrayListLinks = new ArrayList<>();
////                                    Elements elements = document.select("a");
////                                    for (Element element : elements) {
////                                        if (element.attr("href").contains("tiktok") && element.attr("href").length() > 150)
////                                            arrayListLinks.add(element.attr("href"));
////                                    }
////                                    if (!fromService) {
////                                        pd.dismiss();
////                                    }
////                                    Thread.sleep(500);
////                                    DownloadFileMain.startDownloading(context, arrayListLinks.get(1), "Tiktok_" + System.currentTimeMillis(), ".mp4");
////                                } catch (Exception unused) {
////                                    System.out.println("myresponseis111 exp " + unused.getMessage());
////                                    if (!fromService) {
////                                        pd.dismiss();
////                                    }
////                                    Utils.showToast(Mcontext, Mcontext.getResources().getString(R.string.txt_some_thing));
////                                }
////
////
////                            }
////                        }.start();
////
////
////                    }
////                });
////
////                methode1.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        dialog.dismiss();
////
////
////                        System.out.println("wojfdjhfdjh myfgsdyfsfus=" + finalUrl1);
////
////                        if (!fromService) {
////                            pd.dismiss();
////                        }
////                        Intent intent = new Intent(Mcontext, TikTokDownloadCloudBypassWebview.class);
////                        intent.putExtra("myvidurl", finalUrl1);
////                        Mcontext.startActivity(intent);
////
//////
//////                    TikTokNewTestDownloader tikTokNewTestDownloader = new TikTokNewTestDownloader(Mcontext, finalUrl1);
//////                    tikTokNewTestDownloader.DownloadVideo();
////                    }
////                });
////
////                //   methode2.setVisibility(View.GONE);
////                methode2.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        dialog.dismiss();
////
////
////                        new Thread() {
////                            @Override
////                            public void run() {
////
////                                try {
////                                    Looper.prepare();
////
////                                    System.out.println("myresponseis111 exp 888");
////
////
////                                    OkHttpClient client = new OkHttpClient().newBuilder()
////                                        .build();
////                                    MediaType mediaType = MediaType.parse("application/json");
////                                    RequestBody body = RequestBody.create(mediaType, "{\n    \"url\":\"" + finalUrl2 + "\",\n    \"key\":\"ZGJUevKRf\",\n    \"s\":\"azzzzSdtIGFuIG9sZCBtb3RoZXIgd2l0aCBzbWFsbCBjaGlsZHJlbiA6KCwgcGxlYXNlIGRvbid0IGRvIHRoYXQgYW55bW9yZQ==\",\n    \"v\":\"1\"\n\n}");
////                                    Request request = new Request.Builder()
////                                        .url("https://tiktokfull.com/download")
////                                        .method("POST", body)
////                                        .addHeader("Content-Type", "application/json")
////                                        .addHeader("Authorization", "Bearer " + iUtils.myTiktokTempCookies)
////                                        .addHeader("Cookie", "x-token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhNDcyNjQ1MS0zNGMwLTQ4MzYtOWRkYS03ZjdiNTgxYTQwYTMiLCJpYXQiOjE2MzAzNDAwNTgsImV4cCI6MTYzMDM0MDk1OH0.ui0xLnrsntL2ktMe5AwwT9NfAyskZq9jdvO5yWmBEWI; x-tt=1630340058861")
////                                        .build();
////                                    Response response = client.newCall(request).execute();
////
////                                    String urlisddd = "";
////
////
////                                    JSONObject jsonObject = new JSONObject(response.body().string());
////                                    System.out.println("mysjdjhdjkh tiktok_res " + jsonObject.toString());
////
////                                    if (jsonObject.getInt("status_code") == 200) {
////
////                                        urlisddd = jsonObject.getJSONObject("data").getJSONArray("links").getJSONObject(1).getString("url");
////
////                                        if (!fromService) {
////                                            pd.dismiss();
////                                        }
////                                        Thread.sleep(500);
////                                        DownloadFileMain.startDownloading(context, urlisddd, "Tiktok_" + System.currentTimeMillis(), ".mp4");
////
////
////                                    } else {
////                                        if (!fromService) {
////                                            pd.dismiss();
////                                        }
////                                        Toast.makeText(Mcontext, Mcontext.getResources().getString(R.string.txt_some_thing), Toast.LENGTH_SHORT).show();
////                                    }
////
////
////                                } catch (Exception unused) {
////                                    System.out.println("myresponseis111 exp " + unused.getMessage());
////
////                                    if (!fromService) {
////                                        pd.dismiss();
////                                    }
////                                    Toast.makeText(Mcontext, Mcontext.getResources().getString(R.string.txt_some_thing), Toast.LENGTH_SHORT).show();
////                                }
////
////
////                            }
////                        }.start();
////
////
////                    }
////                });
////
////
////                methode3.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        dialog.dismiss();
////
////
////                        System.out.println("wojfdjhfdjh myfgsdyfsfus=" + finalUrl1);
////
////                        if (!fromService) {
////                            pd.dismiss();
////                        }
////                        Intent intent = new Intent(Mcontext, TikTokDownloadCloudBypassWebview_method_3.class);
////                        intent.putExtra("myvidurl", finalUrl1);
////                        Mcontext.startActivity(intent);
////
////                    }
////                });
////
////                methode4.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        dialog.dismiss();
////
////
////                        System.out.println("wojfdjhfdjh myfgsdyfsfus=" + finalUrl1);
////
////                        if (!fromService) {
////                            pd.dismiss();
////                        }
////                        Intent intent = new Intent(Mcontext, TikTokDownloadCloudBypassWebview_method_5.class);
////                        intent.putExtra("myvidurl", finalUrl1);
////                        Mcontext.startActivity(intent);
////
//////
//////                    TikTokNewTestDownloader tikTokNewTestDownloader = new TikTokNewTestDownloader(Mcontext, finalUrl1);
//////                    tikTokNewTestDownloader.DownloadVideo();
////                    }
////                });
////
////
////                dig_btn_cancel.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        dialog.dismiss();
////                        if (!fromService) {
////                            pd.dismiss();
////                        }
////                    }
////                });
////
////                dialog.setCancelable(false);
////                dialog.show();
////
////            }
////        }
////
//        fun start(context: Context, url: String, service: Boolean) {
//            try {
//                Mcontext = context;
//                fromService = service;
//                Log.i("LOGClipboard111111 clip", "work 2");
//
//                myURLIS = url;
//                if (!fromService) {
//                    pd = new ProgressDialog (context);
//                    pd.setMessage(
//                        Mcontext.getResources().getString(R.string.genarating_download_link)
//                    );
//                    pd.setCancelable(false);
//                    pd.show();
//                }
//                if (url.contains("tiktok")) {
//
//                    if (!((Activity) Mcontext).isFinishing()) {
//
//                        Dialog dialog = new Dialog(Mcontext);
//
//                        dialog.setContentView(R.layout.tiktok_optionselect_dialog);
//
//                        String finalUrl1 = url;
//
//                        Button methode0 = dialog . findViewById (R.id.dig_btn_met0);
//                        Button methode1 = dialog . findViewById (R.id.dig_btn_met1);
//                        Button methode2 = dialog . findViewById (R.id.dig_btn_met2);
//                        Button methode3 = dialog . findViewById (R.id.dig_btn_met3);
//                        Button methode4 = dialog . findViewById (R.id.dig_btn_met4);
//
//                        Button dig_btn_cancel = dialog . findViewById (R.id.dig_btn_cancel);
//
//
//                        String finalUrl2 = url;
//                        methode0.setOnClickListener(new View . OnClickListener () {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                                new Thread () {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            Looper.prepare();
//
//                                            System.out.println("myresponseis111 exp 888");
//
//                                            OkHttpClient client = new OkHttpClient().newBuilder()
//                                                .build();
//                                            MediaType mediaType = MediaType . parse ("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
//                                            RequestBody body = new MultipartBody.Builder()
//                                                .setType(MultipartBody.FORM)
//                                                .addFormDataPart("ic-request", "true")
//                                                .addFormDataPart("id", finalUrl1)
//                                                .addFormDataPart("ic-element-id", "main_page_form")
//                                                .addFormDataPart("ic-id", "1")
//                                                .addFormDataPart("ic-target-id", "active_container")
//                                                .addFormDataPart("ic-trigger-id", "main_page_form")
//                                                .addFormDataPart(
//                                                    "token",
//                                                    "493eaebbf47aa90e1cdfa0f8faf7d04cle0f45a38aa759c6a13fea91d5036dc3b"
//                                                )
//                                                .addFormDataPart("ic-current-url", "")
//                                                .addFormDataPart(
//                                                    "ic-select-from-response",
//                                                    "#id4fbbea"
//                                                )
//                                                .addFormDataPart("_method", "nPOST")
//                                                .build();
//                                            Request request = new Request.Builder()
//                                                .url("https://tiktokdownload.online/results")
//                                                .method("POST", body)
//                                                .addHeader("cache-contro", "no-cache")
//                                                .addHeader(
//                                                    "content-type",
//                                                    "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW"
//                                                )
//                                                .addHeader(
//                                                    "origin",
//                                                    "https://tiktokdownload.online"
//                                                )
//                                                .addHeader(
//                                                    "postman-token",
//                                                    "c866af6b-b900-cf0f-2043-1296b0e5362a"
//                                                )
//                                                .addHeader("sec-fetch-dest", "empty")
//                                                .addHeader("sec-fetch-mode", "cors")
//                                                .addHeader("sec-fetch-site", "same-origin")
//                                                .addHeader(
//                                                    "user-agent",
//                                                    "Mozilla/5.0 (Windows; U; Windows NT 5.1; rv:1.7.3) Gecko/20041001 Firefox/0.10.1"
//                                                )
//                                                .addHeader("x-http-method-override", "POST")
//                                                .addHeader("x-ic-request", "true")
//                                                .addHeader("x-requested-with", "XMLHttpRequest")
//                                                .addHeader(
//                                                    "Cookie",
//                                                    "PHPSESSID=9db511ae60292e341ab84afc4a6127cf"
//                                                )
//                                                .build();
//
//                                            Response response = client . newCall (request).execute();
//                                            Document document = Jsoup . parse (response.body()
//                                                .string());
//                                            ArrayList<String> arrayListLinks = new ArrayList<>();
//                                            Elements elements = document . select ("a");
//                                            for (Element element : elements) {
//                                                if (element.attr("href")
//                                                        .contains("tiktok") && element.attr("href")
//                                                        .length() > 150
//                                                )
//                                                    arrayListLinks.add(element.attr("href"));
//                                            }
//                                            if (!fromService) {
//                                                pd.dismiss();
//                                            }
//                                            Thread.sleep(500);
//                                            DownloadFileMain.startDownloading(
//                                                context,
//                                                arrayListLinks.get(1),
//                                                "Tiktok_" + System.currentTimeMillis(),
//                                                ".mp4"
//                                            );
//                                        } catch (Exception unused) {
//                                            System.out.println("myresponseis111 exp " + unused.getMessage());
//                                            if (!fromService) {
//                                                pd.dismiss();
//                                            }
//                                            Utils.showToast(
//                                                Mcontext,
//                                                Mcontext.getResources().getString(R.string.txt_some_thing)
//                                            );
//                                        }
//
//
//                                    }
//                                }.start();
//
//
//                            }
//                        });
//
//                        methode1.setOnClickListener(new View . OnClickListener () {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//
//
//                                System.out.println("wojfdjhfdjh myfgsdyfsfus=" + finalUrl1);
//
//                                if (!fromService) {
//                                    pd.dismiss();
//                                }
//                                Intent intent = new Intent(
//                                    Mcontext,
//                                    TikTokDownloadCloudBypassWebview.class);
//                                intent.putExtra("myvidurl", finalUrl1);
//                                Mcontext.startActivity(intent);
//
////
////                    TikTokNewTestDownloader tikTokNewTestDownloader = new TikTokNewTestDownloader(Mcontext, finalUrl1);
////                    tikTokNewTestDownloader.DownloadVideo();
//                            }
//                        });
//
//                        //   methode2.setVisibility(View.GONE);
//                        methode2.setOnClickListener(new View . OnClickListener () {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//
//
//                                new Thread () {
//                                    @Override
//                                    public void run() {
//
//                                        try {
//                                            Looper.prepare();
//
//                                            System.out.println("myresponseis111 exp 888");
//
//
//                                            OkHttpClient client = new OkHttpClient().newBuilder()
//                                                .build();
//                                            MediaType mediaType = MediaType . parse ("application/json");
//                                            RequestBody body = RequestBody . create (mediaType, "{\n    \"url\":\""+finalUrl2+"\",\n    \"key\":\"ZGJUevKRf\",\n    \"s\":\"azzzzSdtIGFuIG9sZCBtb3RoZXIgd2l0aCBzbWFsbCBjaGlsZHJlbiA6KCwgcGxlYXNlIGRvbid0IGRvIHRoYXQgYW55bW9yZQ==\",\n    \"v\":\"1\"\n\n}");
//                                            Request request = new Request.Builder()
//                                                .url("https://tiktokfull.com/download")
//                                                .method("POST", body)
//                                                .addHeader("Content-Type", "application/json")
//                                                .addHeader(
//                                                    "Authorization",
//                                                    "Bearer " + iUtils.myTiktokTempCookies
//                                                )
//                                                .addHeader(
//                                                    "Cookie",
//                                                    "x-token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhNDcyNjQ1MS0zNGMwLTQ4MzYtOWRkYS03ZjdiNTgxYTQwYTMiLCJpYXQiOjE2MzAzNDAwNTgsImV4cCI6MTYzMDM0MDk1OH0.ui0xLnrsntL2ktMe5AwwT9NfAyskZq9jdvO5yWmBEWI; x-tt=1630340058861"
//                                                )
//                                                .build();
//                                            Response response = client . newCall (request).execute();
//
//                                            String urlisddd = "";
//
//
//                                            JSONObject jsonObject = new JSONObject(
//                                                response.body().string()
//                                            );
//                                            System.out.println("mysjdjhdjkh tiktok_res " + jsonObject.toString());
//
//                                            if (jsonObject.getInt("status_code") == 200) {
//
//                                                urlisddd = jsonObject.getJSONObject("data")
//                                                    .getJSONArray("links").getJSONObject(1)
//                                                    .getString("url");
//
//                                                if (!fromService) {
//                                                    pd.dismiss();
//                                                }
//                                                Thread.sleep(500);
//                                                DownloadFileMain.startDownloading(
//                                                    context,
//                                                    urlisddd,
//                                                    "Tiktok_" + System.currentTimeMillis(),
//                                                    ".mp4"
//                                                );
//
//
//                                            } else {
//                                                if (!fromService) {
//                                                    pd.dismiss();
//                                                }
//                                                Toast.makeText(
//                                                    Mcontext,
//                                                    Mcontext.getResources()
//                                                        .getString(R.string.txt_some_thing),
//                                                    Toast.LENGTH_SHORT
//                                                ).show();
//                                            }
//
//
//                                        } catch (Exception unused) {
//                                            System.out.println("myresponseis111 exp " + unused.getMessage());
//
//                                            if (!fromService) {
//                                                pd.dismiss();
//                                            }
//                                            Toast.makeText(
//                                                Mcontext,
//                                                Mcontext.getResources()
//                                                    .getString(R.string.txt_some_thing),
//                                                Toast.LENGTH_SHORT
//                                            ).show();
//                                        }
//
//
//                                    }
//                                }.start();
//
//
//                            }
//                        });
//
//
//                        methode3.setOnClickListener(new View . OnClickListener () {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//
//
//                                System.out.println("wojfdjhfdjh myfgsdyfsfus=" + finalUrl1);
//
//                                if (!fromService) {
//                                    pd.dismiss();
//                                }
//                                Intent intent = new Intent(
//                                    Mcontext,
//                                    TikTokDownloadCloudBypassWebview_method_3.class);
//                                intent.putExtra("myvidurl", finalUrl1);
//                                Mcontext.startActivity(intent);
//
//                            }
//                        });
//
//                        methode4.setOnClickListener(new View . OnClickListener () {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//
//
//                                System.out.println("wojfdjhfdjh myfgsdyfsfus=" + finalUrl1);
//
//                                if (!fromService) {
//                                    pd.dismiss();
//                                }
//                                Intent intent = new Intent(
//                                    Mcontext,
//                                    TikTokDownloadCloudBypassWebview_method_5.class);
//                                intent.putExtra("myvidurl", finalUrl1);
//                                Mcontext.startActivity(intent);
//
////
////                    TikTokNewTestDownloader tikTokNewTestDownloader = new TikTokNewTestDownloader(Mcontext, finalUrl1);
////                    tikTokNewTestDownloader.DownloadVideo();
//                            }
//                        });
//
//
//                        dig_btn_cancel.setOnClickListener(new View . OnClickListener () {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                                if (!fromService) {
//                                    pd.dismiss();
//                                }
//                            }
//                        });
//
//                        dialog.setCancelable(false);
//                        dialog.show();
//
//                    }
//                    Log.d("ttt", " url= $url")
//                } else if
//                               (url.contains("popcornflix")) {
//
//
//                    url = url.substring(url.lastIndexOf("/") + 1);
//
//                    System.out.println("fjhjfhjsdfsdhf " + url);
//
//                    AndroidNetworking.get("https://api.unreel.me/v2/sites/popcornflix/videos/" + url + "/play-url?__site=popcornflix&__source=web&embed=false&protocol=https&tv=false")
//                        .addHeaders(
//                            "User-Agent",
//                            "Mozilla/5.0 (Linux; Android 10;TXY567) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/8399.0.9993.96 Mobile Safari/599.36"
//                        )
//                        .setPriority(Priority.MEDIUM)
//                        .build()
//                        .getAsJSONObject(new JSONObjectRequestListener () {
//                            @Override
//                            public void onResponse(JSONObject response) {
//
//
//                                System.out.println("fjhjfhjsdfsdhf " + response);
//
//
//                                if (!fromService) {
//                                    pd.dismiss();
//                                }
//                                String matag;
//                                try {
//
//                                    JSONObject jsonObject = new JSONObject(response.toString());
//
//                                    matag = jsonObject.getString("url");
//                                    System.out.println("wojfdjhfdjh " + matag);
//                                    DownloadFileMain.startDownloading(
//                                        context,
//                                        matag,
//                                        "Popcornflex_" + System.currentTimeMillis(),
//                                        ".mp4"
//                                    );
//
//
//                                } catch (Exception e) {
//                                    matag = "";
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                    e.printStackTrace();
//                                }
//
//
//                            }
//
//                            @Override/**/
//                            public void onError(ANError error) {
//                                if (!fromService) {
//                                    pd.dismiss();
//                                }
//                            }
//                        });
//
//
//                } else if (url.contains("veoh")) {
//
//
//                    url = url.substring(url.lastIndexOf("/") + 1);
//
//                    System.out.println("fjhjfhjsdfsdhf " + url);
//
//                    AndroidNetworking.get("http://www.veoh.com/watch/getVideo/" + url)
//                        .addHeaders(
//                            "User-Agent",
//                            "Mozilla/5.0 (Linux; Android 10;TXY567) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/8399.0.9993.96 Mobile Safari/599.36"
//                        )
//                        .setPriority(Priority.MEDIUM)
//                        .build()
//                        .getAsJSONObject(new JSONObjectRequestListener () {
//                            @Override
//                            public void onResponse(JSONObject response) {
//
//
//                                System.out.println("fjhjfhjsdfsdhf " + response);
//
//
//                                if (!fromService) {
//                                    pd.dismiss();
//                                }
//                                String matag;
//                                try {
//
//                                    JSONObject jsonObject = new JSONObject(response.toString());
//
//
//                                    matag = jsonObject.getJSONObject("video").getJSONObject("src")
//                                        .getString("HQ");
//                                    System.out.println("wojfdjhfdjh " + matag);
//                                    DownloadFileMain.startDownloading(
//                                        context,
//                                        matag,
//                                        "Veoh_" + System.currentTimeMillis(),
//                                        ".mp4"
//                                    );
//
//
//                                } catch (Exception e) {
//                                    matag = "";
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                    e.printStackTrace();
//                                }
//
//
//                            }
//
//                            @Override/**/
//                            public void onError(ANError error) {
//                                if (!fromService) {
//                                    pd.dismiss();
//                                }
//                            }
//                        });
//
//
//                } else if (url.contains("moj")) {
//
//                    try {
//                        url = url.substring(url.lastIndexOf("/") + 1);
//
//                        if (url.contains("?referrer=share")) {
//                            url = url.substring(0, url.indexOf("?"));
//                            System.out.println(
//                                "fjhjfhjsdfsdhf 000=" + url + " size " + url.indexOf(
//                                    "?"
//                                )
//                            );
//                            System.out.println("fjhjfhjsdfsdhf 000=" + "https://moj-apis.sharechat.com/videoFeed?postId=" + url + "&firstFetch=true");
//                        }
//
//                        JSONObject jsonObject = new JSONObject(
//                            "{\"appVersion\":83,\"bn\":\"broker1\",\"client\":\"android\",\"deviceId\":\"ebb088d29e7287b1\",\"message\":{\"adData\":{\"adsShown\":0,\"firstFeed\":false},\"deviceInfoKey\":\"OSyQoHJLJ4NsXPLyQePkAICh3Q0ih0bveFwm1KEV+vReMuldqo+mSyMjdhb4EeryKxk1ctAbYaDH\\\\nTI+PMRPZVYH5pBccAm7OT2uz69vmD/wPqGuSgWV2aVNMdM75DMb8NZn1JU2b1bo/oKs80baklsvx\\\\n1X7jrFPL6M5EDTdPDhs=\\\\n\",\"deviceInfoPayload\":\"M6g+6j6irhFT/H6MsQ/n/tEhCl7Z5QgtVfNKU8M90zTJHxqljm2263UkjRR9bRXAjmQFXXOTXJ25\\\\nOHRjV7L5Lw+tUCONoYfyUEzADihWfAiUgXJEcKePfZONbdXXuwGgOPeD0k4iSvI7JdzroRCScKXd\\\\n41CkmXFayPaRL9aqgAgs6kSoIncCWBU2gEXiX1lgPVvdmUzCZ+yi2hFA+uFOmv1MJ6dcFKKcpBM6\\\\nHSPIrGV+YtTyfd8nElx0kyUbE4xmjOuMrctkjnJkd2tMdxB8qOFKeYrcLzy4LZJNXyUmzs29XSE+\\\\nhsrMZib8fFPJhJZIyGCWqfWiURut4Bg5HxYhYhg3ejPxFjNyXxS3Ja+/pA+A0olt5Uia7ync/Gui\\\\n58tlDQ4SKPthCzGa1tCVN+2y/PW30+LM79t0ltJ/YrNZivQx4eEnszlM9nwmIuj5z5LPniQghA6x\\\\nrfQ8IqVUZfiitXj/Fr7UjKg1cs/Ajj8g4u/KooRvVkg9tMwWePtJFqrkk1+DU4cylnSEG3XHgfer\\\\nslrzj5NNZessMEi+4Nz0O2D+b8Y+RjqN6HqpwZPDHhZwjz0Iuj2nhZLgu1bgNJev5BwxAr8akDWv\\\\nvKsibrJS9auQOYVzbYZFdKMiBnh+WHq0qO2aW1akYWCha3ZsSOtsnyPnFC+1PnMbBv+FiuJmPMXg\\\\nSODFoRIXfxgA/qaiKBipS+kIyfaPxn6O1i6MOwejVuQiWdAPTO132Spx0cFtdyj2hX6wAMe21cSy\\\\n8rs3KQxiz+cq7Rfwzsx4wiaMryFunfwUwnauGwTFOW98D5j6oO8=\\\\n\",\"lang\":\"Hindi\",\"playEvents\":[{\"authorId\":\"18326559001\",\"networkBitrate\":1900000,\"initialBufferPercentage\":100,\"isRepost\":false,\"sg\":false,\"meta\":\"NotifPostId\",\"md\":\"Stream\",\"percentage\":24.68405,\"p\":\"91484006\",\"radio\":\"wifi\",\"r\":\"deeplink_VideoPlayer\",\"repeatCount\":0,\"timeSpent\":9633,\"duration\":15,\"videoStartTime\":3916,\"t\":1602255552820,\"clientType\":\"Android\",\"i\":79,\"appV\":83,\"sessionId\":\"72137847101_8863b3f5-ad2d-4d59-aa7c-cf1fb9ef32ea\"},{\"authorId\":\"73625124001\",\"networkBitrate\":1900000,\"initialBufferPercentage\":100,\"isRepost\":false,\"sg\":false,\"meta\":\"list2\",\"md\":\"Stream\",\"percentage\":17.766666,\"p\":\"21594412\",\"radio\":\"wifi\",\"r\":\"First Launch_VideoPlayer\",\"repeatCount\":0,\"tagId\":\"0\",\"tagName\":\"\",\"timeSpent\":31870,\"duration\":17,\"videoStartTime\":23509,\"t\":1602218215942,\"clientType\":\"Android\",\"i\":79,\"appV\":83,\"sessionId\":\"72137847101_db67c0c9-a267-4cec-a3c3-4c0fa4ea16e1\"}],\"r\":\"VideoFeed\"},\"passCode\":\"9e32d6145bfe53d14a0c\",\"resTopic\":\"response/user_72137847101_9e32d6145bfe53d14a0c\",\"userId\":\"72137847101\"}"
//                        );
//
//
//                        AndroidNetworking.post("https://moj-apis.sharechat.com/videoFeed?postId=" + url + "&firstFetch=true")
//                            .addHeaders("X-SHARECHAT-USERID", "72137847101")
//                            .addHeaders("X-SHARECHAT-SECRET", "9e32d6145bfe53d14a0c")
//                            .addHeaders("APP-VERSION", "83")
//                            .addHeaders("PACKAGE-NAME", "in.mohalla.video")
//                            .addHeaders("DEVICE-ID", "ebb088d29e7287b1")
//                            .addHeaders("CLIENT-TYPE", "Android:")
//                            .addHeaders("Content-Type", "application/json; charset=UTF-8")
//                            .addHeaders("Host", "moj-apis.sharechat.com")
//                            .addHeaders("Connection", "Keep-Alive:")
//                            .addHeaders("User-Agent", "okhttp/3.12.12app-version:83")
//                            .addHeaders("cache-control", "no-cache")
//                            .addHeaders("client-type", "Android")
//                            .addHeaders("connection", "Keep-Alive")
//                            .addHeaders("content-type", "application/json;charset=UTF-8")
//                            .addHeaders("device-id", "ebb088d29e7287b1")
//                            .addHeaders("host", "moj-apis.sharechat.com")
//                            .addHeaders("package-name", "in.mohalla.video")
//                            .addHeaders("postman-token", "37d59a7c-f247-3b70-ab3c-1dedf4079852")
//                            .addHeaders("user-agent", "okhttp/3.12.12")
//                            .addHeaders("x-sharechat-secret", "9e32d6145bfe53d14a0c")
//                            .addHeaders("x-sharechat-userid", "72137847101")
//                            .addJSONObjectBody(jsonObject)
//                            .setPriority(Priority.MEDIUM)
//                            .build()
//                            .getAsJSONObject(new JSONObjectRequestListener () {
//                                @Override
//                                public void onResponse(JSONObject response) {
//
//
//                                    System.out.println("fjhjfhjsdfsdhf " + response);
//
//
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                    String matag;
//                                    try {
//
//                                        JSONObject jsonObject = new JSONObject(response.toString());
//
//                                        matag = jsonObject.getJSONObject("payload")
//                                            .getJSONArray("d")
//                                            .getJSONObject(0)
//                                            .getJSONArray("bandwidthParsedVideos")
//                                            .getJSONObject(3)
//                                            .getString("url");
//
//                                        System.out.println("wojfdjhfdjh " + matag);
//                                        DownloadFileMain.startDownloading(
//                                            context,
//                                            matag,
//                                            "Moj_" + System.currentTimeMillis(),
//                                            ".mp4"
//                                        );
//
//
//                                    } catch (Exception e) {
//                                        matag = "";
//                                        if (!fromService) {
//                                            pd.dismiss();
//                                        }
//                                        e.printStackTrace();
//                                    }
//
//
//                                }
//
//                                @Override/**/
//                                public void onError(ANError error) {
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                }
//                            });
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        if (!fromService) {
//                            pd.dismiss();
//                        }
//                    }
//                } else if (url.contains("fairtok")) {
//
//                    try {
//                        url = url.substring(url.lastIndexOf("/") + 1);
//
//
//                        JSONObject jsonObject = new JSONObject(
//                            "{\"device_fingerprint_id\":\"838529202513017602\",\"identity_id\":\"838529202537882206\",\"hardware_id\":\"ebb088d29e7287b1\",\"is_hardware_id_real\":true,\"brand\":\"samsung\",\"model\":\"SM-J200G\",\"screen_dpi\":240,\"screen_height\":960,\"screen_width\":540,\"wifi\":true,\"ui_mode\":\"UI_MODE_TYPE_NORMAL\",\"os\":\"Android\",\"os_version\":22,\"cpu_type\":\"armv7l\",\"build\":\"LMY47X.J200GDCU2ARL1\",\"locale\":\"en_GB\",\"connection_type\":\"wifi\",\"os_version_android\":\"5.1.1\",\"country\":\"GB\",\"language\":\"en\",\"local_ip\":\"192.168.43.18\",\"app_version\":\"1.19\",\"facebook_app_link_checked\":false,\"is_referrable\":0,\"debug\":false,\"update\":1,\"latest_install_time\":1601158937336,\"latest_update_time\":1601158937336,\"first_install_time\":1601158937336,\"previous_update_time\":1601158937336,\"environment\":\"FULL_APP\",\"android_app_link_url\":\"https:\\/\\/fairtok.app.link\\/" + url + "\",\"external_intent_uri\":\"https:\\/\\/fairtok.app.link\\/Y7ov2al149\",\"cd\":{\"mv\":\"-1\",\"pn\":\"com.fairtok\"},\"metadata\":{},\"advertising_ids\":{\"aaid\":\"094dfa1f-77cf-4f84-b373-2c15bf74f9d1\"},\"lat_val\":0,\"google_advertising_id\":\"094dfa1f-77cf-4f84-b373-2c15bf74f9d1\",\"instrumentation\":{\"v1\\/open-qwt\":\"0\"},\"sdk\":\"android5.0.1\",\"branch_key\":\"key_live_hjLYp0Wi3i6R1qQ1Lr51TlpcBvkxEp53\",\"retryNumber\":0}"
//                        );
//
//                        AndroidNetworking.post("https://api2.branch.io/v1/open")
//                            .addHeaders("cache-control", "no-cache")
//
//                            .addJSONObjectBody(jsonObject)
//                            .setPriority(Priority.MEDIUM)
//                            .build()
//                            .getAsJSONObject(new JSONObjectRequestListener () {
//                                @Override
//                                public void onResponse(JSONObject response) {
//
//
//                                    System.out.println("fjhjfhjsdfsdhf " + response);
//
//
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                    String matag;
//                                    try {
//
//                                        JSONObject jsonObject = new JSONObject(response.toString());
//
//                                        matag =
//                                            "https://bucket-fairtok.s3.ap-south-1.amazonaws.com/" + jsonObject.getString(
//                                                "post_video"
//                                            );
//
//                                        System.out.println("wojfdjhfdjh " + matag);
//                                        DownloadFileMain.startDownloading(
//                                            context,
//                                            matag,
//                                            "Fairtok_" + System.currentTimeMillis(),
//                                            ".mp4"
//                                        );
//
//
//                                    } catch (Exception e) {
//                                        matag = "";
//                                        if (!fromService) {
//                                            pd.dismiss();
//                                        }
//                                        e.printStackTrace();
//                                    }
//
//
//                                }
//
//                                @Override/**/
//                                public void onError(ANError error) {
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                }
//                            });
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        if (!fromService) {
//                            pd.dismiss();
//                        }
//                    }
//                } else if (url.contains("vlipsy")) {
//
//                    try {
//                        url = url.substring(url.lastIndexOf("/") + 1);
//                        if (url.length() > 8) {
//                            String[] aa = url . split ("-");
//
//                            url = aa[aa.length - 1];
//
//                        }
//
//
//                        AndroidNetworking.get("https://apiv2.vlipsy.com/v1/vlips/" + url + "?key=vl_R8daJGhs67i7Ej7y")
//                            .setPriority(Priority.MEDIUM)
//                            .build()
//                            .getAsJSONObject(new JSONObjectRequestListener () {
//                                @Override
//                                public void onResponse(JSONObject response) {
//
//
//                                    //  System.out.println("fjhjfhjsdfsdhf " + response);
//
//
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                    String matag;
//                                    try {
//
//                                        JSONObject jsonObject = new JSONObject(response.toString());
//
//                                        matag =
//                                            jsonObject.getJSONObject("data").getJSONObject("media")
//                                                .getJSONObject("mp4").getString("url");
//
//                                        System.out.println("wojfdjhfdjh " + matag);
//                                        DownloadFileMain.startDownloading(
//                                            context,
//                                            matag,
//                                            "Vlipsy_" + System.currentTimeMillis(),
//                                            ".mp4"
//                                        );
//
//
//                                    } catch (Exception e) {
//                                        matag = "";
//                                        if (!fromService) {
//                                            pd.dismiss();
//                                        }
//                                        e.printStackTrace();
//                                    }
//
//
//                                }
//
//                                @Override/**/
//                                public void onError(ANError error) {
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                }
//                            });
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        if (!fromService) {
//                            pd.dismiss();
//                        }
//                    }
//                } else if (url.contains("likee")) {
//
////            https://likee.video/@Aish4/video/6989882553836555260?postId=6989882553836555260
//
//                    String finalUrl3 = url;
//
//                    new Thread (new Runnable () {
//                        @Override
//                        public void run() {
//                            Looper.prepare();
//                            HttpURLConnection con = null;
//                            try {
//                                con = (HttpURLConnection)(new URL (finalUrl3).openConnection());
//
//                                con.setInstanceFollowRedirects(false);
//                                con.connect();
//                                int responseCode = con . getResponseCode ();
//                                System.out.println(responseCode);
//                                String location = con . getHeaderField ("Location");
//                                System.out.println(location);
//
//                                Uri uri;
//                                if (location != null) {
//                                    uri = Uri.parse(location);
//                                } else {
//                                    uri = Uri.parse(finalUrl3);
//
//                                }
//                                String v = uri . getQueryParameter ("postId");
//
//                                OkHttpClient client = new OkHttpClient().newBuilder()
//                                    .build();
//                                RequestBody body = new MultipartBody.Builder()
//                                    .setType(MultipartBody.FORM)
//                                    .addFormDataPart("postIds", v)
//                                    .build();
//                                Request request = new Request.Builder()
//                                    .url("https://likee.video/official_website/VideoApi/getVideoInfo")
//                                    .method("POST", body)
//                                    .addHeader(
//                                        "User-Agent",
//                                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36"
//                                    )
//                                    .build();
//                                Response response = client . newCall (request).execute();
//
//
//                                if (response.code() == 200) {
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                    String urls = new JSONObject(
//                                        response.body().string()
//                                    ).getJSONObject("data").getJSONArray("videoList")
//                                        .getJSONObject(0).getString("videoUrl");
//                                    if (urls.contains("_4")) {
//                                        urls = urls.replace("_4", "");
//                                    }
//                                    if (!urls.equals("")) {
//
//                                        String nametitle = "Likee_"+
//                                        System.currentTimeMillis();
//
//                                        DownloadFileMain.startDownloading(
//                                            Mcontext,
//                                            urls,
//                                            nametitle,
//                                            ".mp4"
//                                        );
//                                    }
//
//                                } else {
//
//                                    if (!fromService) {
//                                        pd.dismiss();
//
//                                        // Toast.makeText(Mcontext, Mcontext.getResources().getString(R.string.txt_some_thing), Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(
//                                            Mcontext,
//                                            Mcontext.getResources().getString(R.string.txt_some_thing),
//                                            Toast.LENGTH_SHORT
//                                        ).show();
//                                    }
//                                }
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                if (!fromService) {
//                                    pd.dismiss();
//
//                                    // Toast.makeText(Mcontext, Mcontext.getResources().getString(R.string.txt_some_thing), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(
//                                        Mcontext,
//                                        Mcontext.getResources().getString(R.string.txt_some_thing),
//                                        Toast.LENGTH_SHORT
//                                    ).show();
//                                }
//                            }
//                        }
//                    }).start();
//
//
//                } else if (url.contains("gfycat")) {
//
//                    CalldlApisDataData(url, true);
//                } else if (url.contains("funimate")) {
//
//
//                    CalldlApisDataData(url, true);
//                } else if (url.contains("kooapp")) {
//
//
//                    KooDownloader kooDownloader = new KooDownloader(Mcontext, url);
//                    kooDownloader.DownloadVideo();
//
//                } else if (url.contains("wwe")) {
//
//
//                    CalldlApisDataData(url, true);
//                } else if (url.contains("1tv.ru")) {
//
//
//                    CalldlApisDataData(url, true);
//                } else if (url.contains("naver")) {
//
//
//                    CalldlApisDataData(url, true);
//                } else if (url.contains("gloria.tv")) {
//
//
//                    CalldlApisDataData(url, true);
//                } else if (url.contains("vidcommons.org")) {
//
//
//                    CalldlApisDataData(url, true);
//                } else if (url.contains("media.ccc.de")) {
//
//
//                    CalldlApisDataData(url, true);
//                } else if (url.contains("vlive")) {
//
//
//                    CalldlApisDataData(url, true);
//                } else if (url.contains("sharechat.com")) {
//                    Log.i("LOGClipboard111111 clip", "work 66666");
//                    try {
//                        // new CallGetShareChatData().execute(url);
//
//                        url = iUtils.extractUrls(url).get(0);
//                        System.out.println("subssssssss11 " + url);
//
//                        int index = url . lastIndexOf ('/') + 1;
//                        url = url.substring(index);
//                        System.out.println("subssssssss " + url);
//
//                        JSONObject jsonObject = new JSONObject("{\"bn\":\"broker3\",\"userId\":644045091,\"passCode\":\"52859d76753457f8dcae\",\"client\":\"web\",\"message\":{\"key\":\"" + url + "\",\"ph\":\"" + url + "\"}}");
//                        AndroidNetworking.post("https://apis.sharechat.com/requestType45")
//                            .addJSONObjectBody(jsonObject)
//                            .addHeaders("Content-Type", "application/json")
//                            .setPriority(Priority.MEDIUM)
//                            .build()
//                            .getAsJSONObject(new JSONObjectRequestListener () {
//                                @Override
//                                public void onResponse(JSONObject response) {
//
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                    String matag;
//                                    try {
//
//                                        JSONObject jsonObject = new JSONObject(response.toString());
//
//                                        matag =
//                                            jsonObject.getJSONObject("payload").getJSONObject("d")
//                                                .getString("v");
//                                        System.out.println("wojfdjhfdjh " + matag);
//                                        DownloadFileMain.startDownloading(
//                                            context,
//                                            matag,
//                                            "Sharechat_" + System.currentTimeMillis(),
//                                            ".mp4"
//                                        );
//
//
//                                    } catch (Exception e) {
//                                        matag = "";
//
//                                        e.printStackTrace();
//                                        if (!fromService) {
//                                            pd.dismiss();
//                                        }
//                                    }
//
//
//                                }
//
//                                @Override
//                                public void onError(ANError error) {
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                }
//                            });
//
//                    } catch (Exception e) {
//                        if (!fromService) {
//                            pd.dismiss();
//                        }
//                    }
//
//
//                } else if (url.contains("roposo.com")) {
//                    Log.i("LOGClipboard111111 clip", "work 66666");
//
//                    new CallGetRoposoData ().execute(url);
//                    Log.i("LOGClipboard111111 clip", "work 1111111");
//
//                } else if (url.contains("snackvideo.com") || url.contains("sck.io")) {
//
//                    //    new callGetSnackAppData().execute(url);
//
//                    if (url.contains("snackvideo.com")) {
//                        new callGetSnackAppData ().execute(url);
//                    } else if (url.contains("sck.io")) {
//                        getSnackVideoData(url, Mcontext);
//                    }
//
//                } else if (url.contains("facebook.com") || url.contains("fb.watch")) {
//
//                    if (!((Activity) Mcontext).isFinishing()) {
//                        Dialog dialog = new Dialog(Mcontext);
//
//                        dialog.setContentView(R.layout.tiktok_optionselect_dialog);
//
//                        String finalUrl1 = url;
//
//                        Button methode0 = dialog . findViewById (R.id.dig_btn_met0);
//                        Button methode1 = dialog . findViewById (R.id.dig_btn_met1);
//                        Button methode2 = dialog . findViewById (R.id.dig_btn_met2);
//                        Button methode3 = dialog . findViewById (R.id.dig_btn_met3);
//                        Button methode4 = dialog . findViewById (R.id.dig_btn_met4);
//
//                        Button dig_btn_cancel = dialog . findViewById (R.id.dig_btn_cancel);
//
//                        methode0.setVisibility(View.GONE);
//                        methode4.setVisibility(View.GONE);
//
//                        methode1.setOnClickListener(new View . OnClickListener () {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//
//
////                    FbVideoDownloader fbVideoDownloader = new FbVideoDownloader(Mcontext, finalUrl1, 0);
////                    fbVideoDownloader.DownloadVideo();
//                                //    new CallGetFacebookData().execute(new String[]{finalUrl1});
//
//                                try {
//
//                                    System.out.println("myurliss = " + finalUrl1);
//
//                                    new Thread () {
//
//                                        @Override
//                                        public void run() {
//                                            super.run();
//                                            try {
//                                                Looper.prepare();
//                                                OkHttpClient client = new OkHttpClient().newBuilder()
//                                                    .build();
//                                                Request request = new Request.Builder()
//                                                    .url(finalUrl1)
//                                                    .method("GET", null)
//                                                    .build();
//
//                                                Response response = null;
//
//                                                response = client.newCall(request).execute();
//
//                                                Document document = Jsoup . parse (response.body()
//                                                    .string());
//                                                if (!fromService) {
//                                                    pd.dismiss();
//                                                }
//
//                                                String unused = document . select ("meta[property=\"og:video\"]").last()
//                                                    .attr("content");
//                                                if (!unused.equals("")) {
//                                                    String nametitle = "Facebook_"+
//                                                    System.currentTimeMillis();
//
//                                                    DownloadFileMain.startDownloading(
//                                                        Mcontext,
//                                                        unused,
//                                                        nametitle,
//                                                        ".mp4"
//                                                    );
//
//                                                } else {
//
//
//                                                }
//
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                                if (!fromService) {
//                                                    pd.dismiss();
//
//                                                    // Toast.makeText(Mcontext, Mcontext.getResources().getString(R.string.txt_some_thing), Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(
//                                                        Mcontext,
//                                                        Mcontext.getResources()
//                                                            .getString(R.string.txt_some_thing),
//                                                        Toast.LENGTH_SHORT
//                                                    ).show();
//                                                }
//                                            }
//
//
//                                        }
//                                    }.start();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    if (!fromService) {
//                                        pd.dismiss();
//
//                                        // Toast.makeText(Mcontext, Mcontext.getResources().getString(R.string.txt_some_thing), Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(
//                                            Mcontext,
//                                            Mcontext.getResources().getString(R.string.txt_some_thing),
//                                            Toast.LENGTH_SHORT
//                                        ).show();
//                                    }
//                                }
//
//                            }
//                        });
//
//                        methode2.setOnClickListener(new View . OnClickListener () {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//
//
//                                System.out.println("wojfdjhfdjh myfgsdyfsfus=" + finalUrl1);
//
//                                if (!fromService) {
//                                    pd.dismiss();
//                                }
//                                Intent intent = new Intent(
//                                    Mcontext,
//                                    FacebookDownloadCloudBypassWebview_method_1.class);
//                                intent.putExtra("myvidurl", finalUrl1);
//                                Mcontext.startActivity(intent);
////                    FbVideoDownloader fbVideoDownloader = new FbVideoDownloader(Mcontext, finalUrl1, 1);
////                    fbVideoDownloader.DownloadVideo();
//
//
//                            }
//                        });
//
//
//                        methode3.setOnClickListener(new View . OnClickListener () {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//
//                                FbVideoDownloader fbVideoDownloader = new FbVideoDownloader(
//                                    Mcontext,
//                                    finalUrl1,
//                                    1
//                                );
//                                fbVideoDownloader.DownloadVideo();
//                            }
//                        });
//
//
//                        dig_btn_cancel.setOnClickListener(new View . OnClickListener () {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                                if (!fromService) {
//                                    pd.dismiss();
//                                }
//                            }
//                        });
//
//                        dialog.setCancelable(false);
//                        dialog.show();
//                    }
//
//                } else if (url.contains("blogspot.com")) {
//
//
//                    CalldlApisDataData(url, true);
//                } else if (url.contains("instagram.com")) {
//
//                    new GetInstagramVideo ().execute(url);
//
//                } else if (url.contains("bilibili.com")) {
//
//                    new callGetbilibiliAppData ().execute(url);
//
//                } else if (url.contains("mitron.tv")) {
//
//                    new CallMitronData ().execute(url);
//                } else if (url.contains("josh")) {
//
//                    new CallJoshData ().execute(url);
//                } else if (url.contains("triller")) {
//
//                    //  new CallTrillerData().execute(url);
//
//                    getTrillerData(url);
//                } else if (url.contains("rizzle")) {
//
//                    new CallRizzleData ().execute(url);
//                } else if (url.contains("solidfiles")) {
//
//                    SolidfilesDownloader solidfilesDownloader = new SolidfilesDownloader(
//                        Mcontext,
//                        url
//                    );
//                    solidfilesDownloader.DownloadVideo();
//
//                } else if (url.contains("audioboom")) {
//
//                    AudioboomDownloader audioboomDownloader = new AudioboomDownloader(
//                        Mcontext,
//                        url
//                    );
//                    audioboomDownloader.DownloadVideo();
//
//                } else if (url.contains("ifunny")) {
//
//                    new CallIfunnyData ().execute(url);
//                } else if (url.contains("trell.co")) {
//
//                    new CalltrellData ().execute(url);
//                } else if (url.contains("boloindya.com")) {
//
//                    new CallBoloindyaData ().execute(url);
//                } else if (url.contains("chingari")) {
//
//                    CallchingariData(url);
//                } else if (url.contains("dubsmash")) {
//
//                    new CalldubsmashData ().execute(url);
//                } else if (url.contains("bittube")) {
//
//                    String myurlis1 = url;
//                    if (myurlis1.contains(".tv")) {
//                        String str = "/";
//                        myurlis1 = myurlis1.split(str)[myurlis1.split(str).length - 1];
//                        myurlis1 = "https://bittube.video/videos/watch/" +
//                                myurlis1;
//                    }
//                    new CallgdriveData ().execute(myurlis1);
//
//                } else if (url.contains("drive.google.com") ||
//                    url.contains("mp4upload") ||
//
//                    url.contains("ok.ru") ||
//
//                    url.contains("mediafire") ||
//                    url.contains("gphoto") ||
//                    url.contains("uptostream") ||
//
//                    url.contains("fembed") ||
//                    url.contains("cocoscope") ||
//                    url.contains("sendvid") ||
//
//                    url.contains("vivo") ||
//                    url.contains("fourShared")
//                ) {
//
//
//                    new CallgdriveData ().execute(url);
//                } else if (url.contains("hind")) {
//
//                    new CallhindData ().execute(url);
//                } else if (url.contains("topbuzz.com")) {
//
//                    TopBuzzDownloader downloader = new TopBuzzDownloader(Mcontext, url, 12);
//                    downloader.DownloadVideo();
//
//                } else if (url.contains("vimeo.com")) {
//                    // VimeoVideoDownloader downloader = new VimeoVideoDownloader(Mcontext, url);
//                    //  downloader.DownloadVideo();
//                    CalldlApisDataData(url, true);
//
//                } else if (url.contains("twitter.com")) {
//                    TwitterVideoDownloader downloader = new TwitterVideoDownloader(Mcontext, url);
//                    downloader.DownloadVideo();
//                    //  CallDailymotionData(url, true);
//                    //CalldlApisDataData(url, true);
//
//                }
//                //new
//                //working
//                else if (url.contains("gag.com")) {
////            https://9gag.com/gag/aXowVXz
//                    //  CalldlApisDataData(url, false);
//                    new Call9gagData ().execute(url);
//
//
//                } else if (url.contains("buzzfeed.com")) {
//
//
//                    if (!fromService) {
//                        pd.dismiss();
//
//                        Toast.makeText(
//                            Mcontext,
//                            Mcontext.getResources().getString(R.string.txt_some_thing),
//                            Toast.LENGTH_SHORT
//                        ).show();
//
//                    }
//
//                }
//
//                //TODO Add quality list
//                else if (url.contains("flickr") && url.contains("flic.kr")) {
//                    // CallDailymotionData(url, true);
//                    CalldlApisDataData(url, true);
//
//                } else if (url.contains("streamable")) {
//                    // CallDailymotionData(url, true);
//                    CalldlApisDataData(url, true);
//
//                } else if (url.contains("vk.com")) {
//
//                    //    CalldlApisDataData(url, false);
//
//                    CallVKData(url, false);
//
//
//                } else if (url.contains("redd.it") || url.contains("reddit")) {
//
//                    //  CalldlApisDataData(url, true);
//                    new CallRedditData ().execute("https://redditsave.com/info?url=" + url);
//                    //  CallREditData(url, true);
//
//
//                } else if (url.contains("soundcloud")) {
//
//
//                    // CallsoundData(url, false);
//
//
//                    if (Constants.showsoundcloud) {
//                        url = url.replace("//m.", "//");
//
//                        CalldlApisDataData(url, true);
//                    } else {
//                        if (!fromService) {
//                            pd.dismiss();
//
//                            Toast.makeText(
//                                Mcontext,
//                                Mcontext.getResources().getString(R.string.txt_some_thing),
//                                Toast.LENGTH_SHORT
//                            ).show();
//
//                        }
//                    }
//
//
//                } else if (url.contains("bandcamp")) {
////TODO Has multiple video json array
//                    CalldlApisDataData(url, true);
//                    //   CallsoundData(url, false);
//
//
//                } else if (url.contains("mxtakatak")) {
//
//
//                    String finalUrl3 = url;
//                    if (finalUrl3.contains("share.mxtakatak.com")) {
//                        new Thread (new Runnable () {
//                            @Override
//                            public void run() {
//                                Looper.prepare();
//                                HttpURLConnection con = null;
//                                try {
//                                    con = (HttpURLConnection)(new URL (finalUrl3).openConnection());
//
//                                    con.setInstanceFollowRedirects(false);
//                                    con.connect();
//                                    int responseCode = con . getResponseCode ();
//                                    System.out.println(responseCode);
//                                    String location = con . getHeaderField ("Location");
//                                    System.out.println(location);
//
//                                    if (location != null && !location.equals("") && location.contains(
//                                            "https://www.mxtakatak.com/"
//                                        )
//                                    ) {
//
//                                        String urls = location . split ("/")[5];
//                                        urls = urls.substring(0, urls.indexOf("?"));
//
//
//                                        String newuuu = "https://mxshorts.akamaized.net/video/"+urls+"/download/1/h264_high_720.mp4";
//
//
//                                        String nametitle = "Mxtaktak_"+
//                                        System.currentTimeMillis();
//
//                                        DownloadFileMain.startDownloading(
//                                            Mcontext,
//                                            newuuu,
//                                            nametitle,
//                                            ".mp4"
//                                        );
//
//                                    }
//
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//
//
//                                } catch (Exception e) {
//                                    if (!fromService) {
//                                        pd.dismiss();
//
//                                        Toast.makeText(
//                                            Mcontext,
//                                            Mcontext.getResources().getString(R.string.txt_some_thing),
//                                            Toast.LENGTH_SHORT
//                                        ).show();
//
//                                    }
//                                }
//                            }
//                        }).start();
//                    } else {
//                        try {
//
//
//                            String urls = finalUrl3 . split ("/")[5];
//                            urls = urls.substring(0, urls.indexOf("?"));
//
//
//                            String newuuu = "https://mxshorts.akamaized.net/video/"+urls+"/download/1/h264_high_720.mp4";
//
//
//                            String nametitle = "Mxtaktak_"+
//                            System.currentTimeMillis();
//                            if (!fromService) {
//                                pd.dismiss();
//                            }
//                            DownloadFileMain.startDownloading(Mcontext, newuuu, nametitle, ".mp4");
//
//                        } catch (Exception e) {
//                            if (!fromService) {
//                                pd.dismiss();
//
//                                Toast.makeText(
//                                    Mcontext,
//                                    Mcontext.getResources().getString(R.string.txt_some_thing),
//                                    Toast.LENGTH_SHORT
//                                ).show();
//
//                            }
//                        }
//                    }
//
//                    //  new CallmxtaktakData().execute(url);
//
//
////            AndroidNetworking.post("http://mxtakatakvideodownloader.shivjagar.co.in/MXTakatak-service.php")
////                    .addBodyParameter("url", url)
////                    .setPriority(Priority.MEDIUM)
////                    .build()
////                    .getAsJSONObject(new JSONObjectRequestListener() {
////                        @Override
////                        public void onResponse(JSONObject response) {
////
////                            if (!fromService) {
////                                pd.dismiss();
////                            }
////                            String matag;
////                            try {
////
////                                JSONObject jsonObject = new JSONObject(response.toString());
////
////                                matag = jsonObject.getString("videourl");
////                                System.out.println("wojfdjhfdjh " + matag);
////                                DownloadFileMain.startDownloading(context, matag, "Mxtakatak_" + System.currentTimeMillis(), ".mp4");
////
////
////                            } catch (Exception e) {
////                                matag = "";
////
////                                e.printStackTrace();
////                                if (!fromService) {
////                                    pd.dismiss();
////                                }
////                            }
////
////
////                        }
////
////                        @Override/**/
////                        public void onError(ANError error) {
////                            if (!fromService) {
////                                pd.dismiss();
////                            }
////                        }
////                    });
//
//
//                } else if (url.contains("cocoscope")) {
//
//
//                    // CallsoundData(url, false);
//                    CalldlApisDataData(url, true);
//
//                } else if (url.contains("test.com")) {
//
//                    new CallgaanaData ().execute(url);
//                    // CallsoundData(url, false);
//                    //  CalldlApisDataData(url, true);
//
//                } else if (url.contains("20min.ch")) {
//
//
//                    Twenty_min_ch_Downloader twenty_min_ch_downloader = new Twenty_min_ch_Downloader(
//                        Mcontext,
//                        url
//                    );
//                    twenty_min_ch_downloader.DownloadVideo();
//
//                } else if (url.contains("gaana")) {
//
//
//                    String finalUrl = url;
//                    new Thread (new Runnable () {
//                        @Override
//                        public void run() {
//                            HttpURLConnection con = null;
//                            try {
//                                con =
//                                    (HttpURLConnection)(new URL ("https://tinyurl.com/f67p797b").openConnection());
//
//                                con.setInstanceFollowRedirects(false);
//                                con.connect();
//                                int responseCode = con . getResponseCode ();
//                                System.out.println(responseCode);
//                                String location = con . getHeaderField ("Location");
//                                System.out.println(location);
//
//
//                                AndroidNetworking.post(location)
//                                    .addBodyParameter("url", finalUrl)
//                                    .addBodyParameter("weburl", "https://video.infusiblecoder.com/")
//                                    .setPriority(Priority.MEDIUM)
//                                    .build()
//                                    .getAsJSONObject(new JSONObjectRequestListener () {
//                                        @Override
//                                        public void onResponse(JSONObject response) {
//
//                                            if (!fromService) {
//                                                pd.dismiss();
//                                            }
//                                            String matag;
//                                            try {
//
//                                                JSONObject jsonObject = new JSONObject(response.toString());
//
//                                                matag = jsonObject.getJSONArray("songlinks")
//                                                    .getJSONObject(0).getString("songurl");
//                                                System.out.println("wojfdjhfdjh " + matag);
//                                                DownloadFileMain.startDownloading(
//                                                    context,
//                                                    matag,
//                                                    "Gaana_" + System.currentTimeMillis(),
//                                                    ".mp3"
//                                                );
//
//
//                                            } catch (Exception e) {
//                                                matag = "";
//
//                                                e.printStackTrace();
//                                                if (!fromService) {
//                                                    pd.dismiss();
//                                                }
//                                            }
//
//
//                                        }
//
//                                        @Override/**/
//                                        public void onError(ANError error) {
//
//                                            System.out.println("wojfdjhfdjh error = " + error.getMessage());
//
//
//                                            if (!fromService) {
//                                                pd.dismiss();
//                                            }
//                                        }
//                                    });
//
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//
//
//                } else if (url.contains("izlesene")) {
//
//
//                    //CallsoundData(url, false);
//                    CalldlApisDataData(url, false);
//
//                } else if (url.contains("linkedin")) {
//
//
//                    //   CalldlApisDataData(url, false);
//                    new CallLinkedinData ().execute(url);
//
//                } else if (url.contains("kwai") || url.contains("kw.ai")) {
//
//                    //http://s.kw.ai/p/BhhKA7HG
//                    //  CalldlApisDataData(url, false);
//
//                    KwaiDownloader kwaiDownloader = new KwaiDownloader(Mcontext, url);
//                    kwaiDownloader.DownloadVideo();
//
//                } else if (url.contains("bitchute")) {
//
//
//                    CalldlApisDataData(url, false);
//
//
//                } else if (url.contains("douyin")) {
//
//                    try {
//
//
//                        String[] idis = url . split ("/");
//
//
//                        AndroidNetworking.get("https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + idis[idis.length - 1])
//                            .setPriority(Priority.MEDIUM)
//                            .build()
//                            .getAsJSONObject(new JSONObjectRequestListener () {
//                                @Override
//                                public void onResponse(JSONObject response) {
//
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                    String matag;
//                                    try {
//
////                                $video_info["item_list"][0]["video"]["play_addr"]["url_list"][0];
//                                        JSONObject jsonObject = new JSONObject(response.toString());
//
//                                        JSONArray itemlist = jsonObject . getJSONArray ("item_list");
//                                        matag = itemlist.getJSONObject(0).getJSONObject("video")
//                                            .getJSONObject("play_addr").getJSONArray("url_list")
//                                            .getString(0);
//
//
//                                        System.out.println("wojfdjhfdjh " + matag);
//                                        DownloadFileMain.startDownloading(
//                                            context,
//                                            matag,
//                                            "Douyin_" + System.currentTimeMillis(),
//                                            ".mp4"
//                                        );
//
//
//                                    } catch (Exception e) {
//                                        matag = "";
//
//                                        e.printStackTrace();
//                                        if (!fromService) {
//                                            pd.dismiss();
//                                        }
//                                    }
//
//
//                                }
//
//                                @Override/**/
//                                public void onError(ANError error) {
//                                    if (!fromService) {
//                                        pd.dismiss();
//                                    }
//                                }
//                            });
//
//
//                    } catch (Exception e) {
//
//                    }
//
//
//                } else if (url.contains("dailymotion") || url.contains("dai.ly")) {
//
//
//                    // DailyMotionDownloader mashableDownloader = new DailyMotionDownloader(Mcontext, url);
//                    // mashableDownloader.DownloadVideo();
//                    Log.i("DailyMotionDownloader", "work 0");
//
//                    String mnnn = DailyMotionDownloader . getVideoId (url);
//                    Log.i("DailyMotionDownloader", "work 2 " + mnnn);
//
//                    AndroidNetworking.get("https://api.1qvid.com/api/v1/videoInfo?videoId=" + mnnn + "&host=dailymotion")
//                        //    .addHeaders("User-Agent", "Mozilla/5.0 (Linux; Android 10;TXY567) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/8399.0.9993.96 Mobile Safari/599.36")
//                        .setPriority(Priority.MEDIUM)
//                        .build()
//                        .getAsJSONArray(new JSONArrayRequestListener () {
//                            @Override
//                            public void onResponse(JSONArray response) {
//                                Log.i("DailyMotionDownloader", "work 2");
//
//                                if (!fromService) {
//                                    pd.dismiss();
//
//                                    Toast.makeText(
//                                        Mcontext,
//                                        Mcontext.getResources().getString(R.string.txt_some_thing),
//                                        Toast.LENGTH_SHORT
//                                    ).show();
//
//                                }
//                                try {
//
//
//                                    JSONArray jsonArray12 = new JSONArray(response.toString());
//                                    JSONArray jsonArraye = jsonArray12 . getJSONObject (0).getJSONArray(
//                                        "formats"
//                                    );
//
//                                    ArrayList<String> urls = new ArrayList<>();
//                                    ArrayList<String> qualities = new ArrayList<>();
//
//
//
//                                    for (int i = 0; i < jsonArraye.length(); i++) {
//
//                                        String urlis = jsonArraye . getJSONObject (i).getString("url");
//                                        urls.add(urlis);
//                                        String resolutionis = jsonArraye . getJSONObject (i).getString(
//                                            "formatNote"
//                                        );
//                                        qualities.add(resolutionis);
//
//
//                                    }
//
//                                    String[] arr = new String[qualities.size()];
//                                    arr = qualities.toArray(arr);
//
//                                    new AlertDialog . Builder (Mcontext).setTitle("Quality!")
//                                        .setItems(arr, new DialogInterface . OnClickListener () {
//                                            public void onClick(
//                                                DialogInterface dialogInterface,
//                                                int i
//                                            ) {
//
//                                                DownloadFileMain.startDownloading(
//                                                    Mcontext,
//                                                    urls.get(i),
//                                                    "Dailymotion_" + System.currentTimeMillis(),
//                                                    ".mp4"
//                                                );
//
//                                            }
//                                        }).setPositiveButton(
//                                        "OK",
//                                        new DialogInterface . OnClickListener () {
//                                            @Override
//                                            public void onClick(
//                                                DialogInterface dialogInterface,
//                                                int i
//                                            ) {
//                                                if (!fromService) {
//
//                                                    pd.dismiss();
//                                                }
//                                            }
//                                        }).setCancelable(false).show();
//
//
//                                } catch (Exception e) {
//                                    Log.i(
//                                        "DailyMotionDownloader",
//                                        "work 3" + e.getLocalizedMessage()
//                                    );
//
//                                }
//                            }
//
//                            @Override
//                            public void onError(ANError anError) {
//                                if (!fromService) {
//                                    pd.dismiss();
//
//                                    Toast.makeText(
//                                        Mcontext,
//                                        Mcontext.getResources().getString(R.string.txt_some_thing),
//                                        Toast.LENGTH_SHORT
//                                    ).show();
//
//                                }
//                                Log.i(
//                                    "DailyMotionDownloader",
//                                    "work 4" + anError.getLocalizedMessage()
//                                );
//
//                            }
//                        });
//
//
////            if (!fromService) {
////                pd.dismiss();
////                Toast.makeText(Mcontext, Mcontext.getResources().getString(R.string.txt_some_thing), Toast.LENGTH_SHORT).show();
////            }
//
//
//                } else if (url.contains("espn.com")) {
//                    CalldlApisDataData(url, true);
//
//                } else if (url.contains("mashable.com")) {
//                    //   CalldlApisDataData(url, true);
//                    MashableDownloader mashableDownloader = new MashableDownloader(Mcontext, url);
//                    mashableDownloader.DownloadVideo();
//
//
//                } else if (url.contains("coub")) {
//                    CalldlApisDataData(url, true);
////            CoubDownloader coubDownloader = new CoubDownloader(Mcontext, url);
////            coubDownloader.DownloadVideo();
//
//
//                } else if (url.contains("kickstarter")) {
//                    KickstarterDownloader kickstarterDownloader = new KickstarterDownloader(
//                        Mcontext,
//                        url
//                    );
//                    kickstarterDownloader.DownloadVideo();
//
//
//                } else if (url.contains("aparat")) {
//                    AparatDownloader aparatDownloader = new AparatDownloader(Mcontext, url);
//                    aparatDownloader.DownloadVideo();
//
//
//                } else if (url.contains("allocine.fr")) {
//                    AllocineDownloader allocineDownloader = new AllocineDownloader(Mcontext, url);
//                    allocineDownloader.DownloadVideo();
//
//
//                } else if (url.contains("ted.com")) {
//
//                    CalldlApisDataData(url, true);
//                } else if (url.contains("twitch")) {
//                    CalldlApisDataData(url, true);
//
//                } else if (url.contains("imdb.com")) {
//                    CalldlApisDataData(url, false);
//
//                } else if (url.contains("camdemy")) {
//                    CalldlApisDataData(url, false);
//
//                } else if (url.contains("pinterest") || url.contains("pin.it")) {
//                    CalldlApisDataData(url, false);
//
//                } else if (url.contains("imgur.com")) {
//                    url = url.replace("//m.", "//");
//                    CalldlApisDataData(url, false);
//
//                } else if (url.contains("tumblr.com")) {
//                    new CalltumblerData ().execute(url);
//                } else if (url.contains("youtube.com") || url.contains("youtu.be")) {
//
//                    if (Constants.showyoutube) {
//                        Log.i("LOGClipboard111111 clip", "work 3");
//                        // getYoutubeDownloadUrl(url);
//                        CalldlApisDataData(url, true);
//                    } else {
//                        if (!fromService) {
//                            pd.dismiss();
//                            Toast.makeText(
//                                context,
//                                context.getResources().getString(R.string.txt_some_thing),
//                                Toast.LENGTH_SHORT
//                            ).show();
//                        }
//                    }
//                } else {
//                    if (!fromService) {
//                        pd.dismiss();
//                        Toast.makeText(
//                            context,
//                            context.getResources().getString(R.string.txt_some_thing),
//                            Toast.LENGTH_SHORT
//                        ).show();
//                    }
//                }
//                prefs = Mcontext.getSharedPreferences("AppConfig", MODE_PRIVATE);
//            } catch (Exception e) {
//                Log.e("ttt", "Error: ", e)
//            }
//        }
//
//
//        var Mcontext: Context? = null
//        var pd: ProgressDialog? = null
//        var dialog: Dialog? = null
//        var prefs: SharedPreferences? = null
//        var fromService: Boolean? = null
//        var VideoUrl: String? = null
//        var SessionID: String? = null
//        var Title: kotlin.String? = null
//        var error = 1
//        var mainLayout: LinearLayout? = null
//        var dialogquality: Dialog? = null
//        var windowManager2: WindowManager? = null
//        var params: WindowManager.LayoutParams? = null
//        var mChatHeadView: View? = null
//        var img_dialog: ImageView? = null
//        var dataModelArrayList: ArrayList<*>? = null
//        var myURLIS = ""
//        var dialog_quality_allvids: Dialog? = null
//        var show_ytd_inpip: ImageView? = null
//        private val newurl: String? = null
//
//
//    }
}