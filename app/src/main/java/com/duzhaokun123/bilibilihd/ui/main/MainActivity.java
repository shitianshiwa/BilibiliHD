package com.duzhaokun123.bilibilihd.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duzhaokun123.bilibilihd.R;
import com.duzhaokun123.bilibilihd.databinding.ActivityMainBinding;
import com.duzhaokun123.bilibilihd.pbilibiliapi.api.PBilibiliClient;
import com.duzhaokun123.bilibilihd.ui.download.DownloadActivity;
import com.duzhaokun123.bilibilihd.ui.JumpActivity;
import com.duzhaokun123.bilibilihd.ui.LoginActivity;
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity;
import com.duzhaokun123.bilibilihd.ui.settings.SettingsActivity;
import com.duzhaokun123.bilibilihd.ui.widget.BaseActivity;
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil;
import com.duzhaokun123.bilibilihd.utils.LogUtil;
import com.duzhaokun123.bilibilihd.utils.Settings;
import com.duzhaokun123.bilibilihd.utils.ToastUtil;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.hiczp.bilibili.api.app.model.MyInfo;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private RelativeLayout mRlMyInfo;
    private TextView mTvUsername, mTvBBi, mTvCoins;
    private CircleImageView mCivFace;
    private ImageView mIvLevel;

    private Fragment homeFragment, historyFragment, dynamicFragment;

    private long lastBackPassTime = -1L;
    private PBilibiliClient pBilibiliClient = PBilibiliClient.Companion.getInstance();
    private MyInfo myInfo;
    private boolean first = true;
    private String title;

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        this.title = title.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        if (Settings.isUninited()) {
            ToastUtil.sendMsg(this, R.string.exception_warning);
            LogUtil.saveLog(this);
        }
    }

    @Override
    protected void restoreInstanceState(@NonNull Bundle savedInstanceState) {
        first = savedInstanceState.getBoolean("first");
        title = savedInstanceState.getString("title");
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadMyInfo();
    }

    @Override
    public void onBackPressed() {
        if (baseBind.dlMain != null && baseBind.dlMain.isOpen()) {
            baseBind.dlMain.close();
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (lastBackPassTime == -1L || currentTime - lastBackPassTime >= 2000) {
//            ToastUtil.sendMsg(MainActivity.this, R.string.passe_again_to_quit);
            Snackbar.make(baseBind.flMain, R.string.passe_again_to_quit, BaseTransientBottomBar.LENGTH_SHORT).show();
            lastBackPassTime = currentTime;
            return;
        }
        super.onBackPressed();
    }

    private void reloadMyInfo() {
        if (pBilibiliClient.getBilibiliClient().isLogin()) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        myInfo = pBilibiliClient.getPAppAPI().getMyInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> ToastUtil.sendMsg(MainActivity.this, e.getMessage()));
                    }

                    if (myInfo != null && handler != null) {
                        handler.sendEmptyMessage(0);
                    }
                }
            }.start();

        } else {
            if (handler != null) {
                handler.sendEmptyMessage(1);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (baseBind.dlMain != null) {
                    if (baseBind.dlMain.isOpen()) {
                        baseBind.dlMain.close();
                    } else {
                        baseBind.dlMain.open();
                    }
                } else {
                    // TODO: 20-3-10  
                }
                return true;
            case R.id.search:
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int initConfig() {
        return NEED_HANDLER | FIX_LAYOUT;
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void findViews() {
        mRlMyInfo = (RelativeLayout) baseBind.navMain.getHeaderView(0);
        mTvUsername = mRlMyInfo.findViewById(R.id.tv_username);
        mIvLevel = mRlMyInfo.findViewById(R.id.iv_level);
        mTvBBi = mRlMyInfo.findViewById(R.id.tv_bBi);
        mTvCoins = mRlMyInfo.findViewById(R.id.tv_coins);
        mCivFace = mRlMyInfo.findViewById(R.id.civ_face);
    }

    @Override
    protected void initView() {
        if (homeFragment == null && first) {
            first = false;
            homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fl_main, homeFragment).commitAllowingStateLoss();
            setTitle(R.string.home);
        } else {
            setTitle(title);
        }

        if (baseBind.dlMain != null) {
            baseBind.dlMain.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerOpened(View drawerView) {
                    reloadMyInfo();
                }
            });
        }

        baseBind.navMain.setNavigationItemSelectedListener(item -> {
            if (baseBind.dlMain != null) {
                baseBind.dlMain.closeDrawers();
            }
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.home:
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                    }
                    setTitle(R.string.home);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, homeFragment).commitAllowingStateLoss();
                    break;
                case R.id.dynamic:
                    if (dynamicFragment == null) {
                        dynamicFragment = new DynamicFragment();
                    }
                    setTitle(R.string.dynamic);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, dynamicFragment).commitAllowingStateLoss();
                    break;
                case R.id.history:
                    if (historyFragment == null) {
                        historyFragment = new HistoryFragment();
                    }
                    setTitle(R.string.history);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, historyFragment).commitAllowingStateLoss();
                    break;
                case R.id.download:
                    intent = new Intent(MainActivity.this, DownloadActivity.class);
                    break;
                case R.id.settings:
                    intent = new Intent(MainActivity.this, SettingsActivity.class);
                    break;
                case R.id.junp:
                    intent = new Intent(MainActivity.this, JumpActivity.class);
                    break;
            }
            if (intent != null) {
                startActivity(intent);
            }
            return true;
        });

        mRlMyInfo.setOnClickListener(v -> {
            if (pBilibiliClient.getBilibiliClient().isLogin()) {
                Intent intent = new Intent(MainActivity.this, UserSpaceActivity.class);
                intent.putExtra("uid", pBilibiliClient.getBilibiliClient().getUserId());
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void handlerCallback(@NonNull Message msg) {
        switch (msg.what) {
            case 0:
                Glide.with(MainActivity.this).load(myInfo.getData().getFace()).into(mCivFace);
                mTvUsername.setText(myInfo.getData().getName());
                ImageViewUtil.setLevelDrawable(mIvLevel, myInfo.getData().getLevel());
//                    mTvBBi.setText(getString(R.string.b_bi) + ": " + myInfo.getData().get);
                mTvBBi.setText(getString(R.string.b_bi) + ": --");
                mTvCoins.setText(getString(R.string.coins) + ": " + myInfo.getData().getCoins());
                if (myInfo.getData().getVip().getType() != 0) {
                    mTvUsername.setTextColor(getColor(R.color.colorAccent));
                }
                break;
            case 1:
                mCivFace.setImageDrawable(null);
                mTvUsername.setText(R.string.not_logged_in);
                mIvLevel.setImageDrawable(null);
                mTvBBi.setText(getString(R.string.b_bi) + ": --");
                mTvCoins.setText(getString(R.string.coins) + ": --");
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("first", first);
        outState.putString("title", title);
    }
}
