package com.duzhaokun123.bilibilihd.pBilibiliApi.api

import com.hiczp.bilibili.api.player.PlayerAPI
import com.hiczp.bilibili.api.player.model.VideoPlayUrl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class PPlayerAPI {
    private var playerAPI: PlayerAPI

    constructor(playerAPI: PlayerAPI) {
        this.playerAPI = playerAPI
    }

    fun videoPlayUrl(aid: Long, cid: Long): VideoPlayUrl {
        return GlobalScope.future { playerAPI.videoPlayUrl(aid = aid, cid = cid).await() }.get()
    }
}