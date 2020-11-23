package com.duzhaokun123.bilibilihd.utils

import android.util.Log
import com.duzhaokun123.danmakuview.danmaku.*
import com.duzhaokun123.danmakuview.interfaces.DanmakuBlocker
import com.duzhaokun123.danmakuview.model.DanmakuConfig
import com.duzhaokun123.danmakuview.ui.DanmakuView

object DanmakuUtil {
    private const val TAG = "DanmakuUtil"

    val simpleDanmakuFactory by lazy { SimpleDanmakuFactory() }

    private val danmakuConfig
        get() = DanmakuView.defaultDanmakuConfig

    fun Int.toDanmakuType(): SimpleDanmakuFactory.Type {
        return when (this) {
            1 -> SimpleDanmakuFactory.Type.R2L_DANMAKU
            4 -> SimpleDanmakuFactory.Type.BOTTOM_DANMAKU
            5 -> SimpleDanmakuFactory.Type.TOP_DANMAKU
            6 -> SimpleDanmakuFactory.Type.L2R_DANMAKU
            7 -> SimpleDanmakuFactory.Type.BILI_SPECIAL_DANMAKU
            else -> {
                Log.e(TAG, "unknowen type $this, R2L_DANMAKU as default")
                SimpleDanmakuFactory.Type.R2L_DANMAKU
            }
        }
    }

    fun syncDanmakuSettings() {
        danmakuConfig.blockers.add(ClassDanmakuBlocker)
        danmakuConfig.allowCovering = Settings.danmaku.allowDanmakuOverlapping

        // TODO: 20-11-23 Settings.danmaku.isDuplicateMerging
        // TODO: 20-11-23 Settings.danmaku.maximumVisibleSizeInScreen

        danmakuConfig.durationCoeff = Settings.danmaku.durationCoeff

        when (Settings.danmaku.danmakuStyle) {
            0 -> danmakuConfig.drawMode = DanmakuConfig.DrawMode.DEFAULT
            1 -> {
                danmakuConfig.drawMode = DanmakuConfig.DrawMode.SHADOW
                danmakuConfig.shadowDx = Settings.danmaku.shadowDx
                danmakuConfig.shadowDy = Settings.danmaku.shadowDy
                danmakuConfig.shadowRadius = Settings.danmaku.shadowRadius
            }
        }

        danmakuConfig.textSizeCoeff = Settings.danmaku.textSize
        danmakuConfig.lineHeight = Settings.danmaku.lineHeight

        ClassDanmakuBlocker.reset()
        ClassDanmakuBlocker.apply {
            for (place in Settings.danmaku.blockByPlace) {
                when (place) {
                    "top" -> blockTop = true
                    "bottom" -> blockBottom = true
                    "r2l" -> blockR2L = true
                    "l2r" -> blockL2R = true
                    "special" -> blockSpecial = true
                }
            }
        }

        // TODO: 20-11-23 其他配置: 字体, 边距
    }

    object ClassDanmakuBlocker : DanmakuBlocker {
        var blockTop = false
        var blockBottom = false
        var blockR2L = false
        var blockL2R = false
        var blockSpecial = false

        override fun shouldBlock(danmaku: Danmaku): Boolean {
            if (blockTop && danmaku is TopDanmaku) return true
            if (blockBottom && danmaku is BottomDanmaku) return true
            if (blockR2L && danmaku is R2LDanmaku) return true
            if (blockL2R && danmaku is L2RDanmaku) return true
            if (blockSpecial && danmaku is BiliSpecialDanmaku) return true
            return false
        }

        fun reset() {
            blockTop = false
            blockBottom = false
            blockR2L = false
            blockL2R = false
            blockSpecial = false
        }
    }
}