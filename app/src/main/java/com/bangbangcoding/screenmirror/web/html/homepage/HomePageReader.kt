package com.bangbangcoding.screenmirror.web.html.homepage

import com.anthonycr.mezzanine.FileStream

@FileStream("app/src/main/html/homepage.html")
interface HomePageReader {
    fun provideHtml(): String

}