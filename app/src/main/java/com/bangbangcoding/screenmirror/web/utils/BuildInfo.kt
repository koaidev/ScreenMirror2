package com.bangbangcoding.screenmirror.web.utils

data class BuildInfo(val buildType: BuildType)

/**
 * The types of builds that this instance of the app could be.
 */
enum class BuildType {
    DEBUG,
    RELEASE
}
