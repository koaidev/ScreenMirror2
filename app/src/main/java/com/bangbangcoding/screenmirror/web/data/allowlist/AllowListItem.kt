package com.bangbangcoding.screenmirror.web.data.allowlist

/**
 * A model object representing a whitelisted URL.
 */
data class AllowListItem(
    val url: String,
    val timeCreated: Long
)
