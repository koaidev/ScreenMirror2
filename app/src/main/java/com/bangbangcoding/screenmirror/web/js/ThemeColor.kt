package com.bangbangcoding.screenmirror.web.js

import com.anthonycr.mezzanine.FileStream

/**
 * Reads the theme color from the DOM.
 */
@FileStream("app/src/main/js/ThemeColor.js")
interface ThemeColor {

    fun provideJs(): String

}