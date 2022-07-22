package com.bangbangcoding.screenmirror.model

import androidx.annotation.Keep

@Keep
class Shortcut(@Keep val image: Int, @Keep val name: String) {
    constructor() : this(0, "")
}