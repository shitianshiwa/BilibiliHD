package com.duzhaokun123.bilibilihd.ui.universal.reply

import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity
import com.duzhaokun123.bilibilihd.databinding.ActivityRootReplyBinding

class RootReplyActivity : BaseActivity<ActivityRootReplyBinding>() {
    companion object {
        const val EXTRA_TYPE = "type"
        const val EXTRA_OID = "oid"
        const val EXTRA_MODE = "mode"
    }

    override fun initConfig() = FIX_LAYOUT

    override fun initLayout() = R.layout.activity_root_reply

    override fun initView() {
        startIntent.let {
            baseBind.tvType.text = it.getIntExtra(EXTRA_TYPE, 0).toString()
            baseBind.tvOid.text = it.getLongExtra(EXTRA_OID, 0L).toString()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fl_content,
                            RootReplyFragment(it.getLongExtra(EXTRA_OID, 0L),
                                    it.getIntExtra(EXTRA_MODE, 3),
                                    it.getIntExtra(EXTRA_TYPE, 0)
                            )
                    ).commitAllowingStateLoss()
        }
    }

    override fun initData() {
        val model: RootReplyFragment.AllCountViewModel by viewModels()
        model.allCount.observe(this, Observer { allCount ->
            baseBind.tvAllCount.text = allCount.toString()
        })
    }

    override fun initRegisterCoordinatorLayout() = baseBind.clRoot
}