package com.duzhaokun123.bilibilihd.utils

import android.widget.TextView
import androidx.core.text.util.LinkifyCompat

object LinkifyUtil {
    fun addAllLinks(text: TextView) {
        LinkifyCompat.addLinks(text, PatternUtil.avPattern, "bilibili://", null, { _, url ->
            val id = url.substring(2)
            "video/$id"
        })
        LinkifyCompat.addLinks(text, PatternUtil.bvPattern, "bilibili://", null, { _, url ->
            "video/$url"
        })
        LinkifyCompat.addLinks(text, PatternUtil.cvPattern, "bilibili://", null, { _, url ->
            val id = url.substring(2)
            "article/$id"
        })
        LinkifyCompat.addLinks(text, PatternUtil.smPattern, "https://", null, { _, url ->
            "sp.nicovideo.jp/watch/$url"
        })
        LinkifyCompat.addLinks(text, PatternUtil.acPattern, "https://", null, { _, url ->
            "www.acfun.cn/v/$url"
        })
        LinkifyCompat.addLinks(text, PatternUtil.auPattern, "bilibili://", null, { _, url ->
            val id = url.substring(2)
            "audio/$id"
        })
        LinkifyCompat.addLinks(text, PatternUtil.uidPattern, "bilibili://", null, { _, url ->
            val id = url.substring(3)
            "space/$id"
        })
    }
}