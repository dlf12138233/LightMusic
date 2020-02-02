package com.brins.lightmusic

import android.content.Context
import android.util.Log
import com.brins.lib_common.utils.SpUtils
import com.brins.lightmusic.common.AppConfig
import com.brins.lightmusic.di.component.AppComponent
import com.brins.lightmusic.di.component.DaggerAppComponent
import com.brins.lightmusic.di.module.AppModule
import com.brins.lightmusic.model.database.DatabaseFactory
import com.brins.lightmusic.utils.*
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import io.reactivex.plugins.RxJavaPlugins


class LightMusicApplication : BaseApplication() {


    companion object {
        @JvmStatic
        fun getLightApplication(): LightMusicApplication {
            return sInstance as LightMusicApplication
        }

        fun getAppComponent(): AppComponent {
            return DaggerAppComponent.builder()
                .appModule(AppModule(getLightApplication()))
                .build()
        }
    }


    override fun onCreate() {
        super.onCreate()
        if (isMainProcess(this)) {
            initRxJava()
            initUserData()
        }
    }

    private fun initUmeng() {
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true)
        }
        UMConfigure.init(
            this,
            AppConfig.UMAPPKEY,
            "",
            UMConfigure.DEVICE_TYPE_PHONE,
            ""
        )

/*        PlatformConfig.setWeixin(Constant.WEIXIN_APPKEY, Constant.WEIXIN_APPSCECRE)
        PlatformConfig.setSinaWeibo(
            Constant.WEIBO_APPKEY,
            Constant.WEIBO_APPSCECRE,
            "http://sns.whalecloud.com"
        )
        PlatformConfig.setQQZone(Constant.QQ_APPKEY, Constant.QQ_APPSCECRE)*/
        MobclickAgent.setCatchUncaughtExceptions(true)
    }

    fun isMainProcess(context: Context): Boolean {
        return AppConfig.Package.MAIN_PROCESS_NAME == getCurrProcessName(context)
    }


    private fun initRxJava() {
        RxJavaPlugins.setErrorHandler { Log.e("RxJava", "RX error handler") }
    }

    private fun initUserData() {
        AppConfig.isLogin = SpUtils.obtain(SP_USER_INFO, this).getBoolean(KEY_IS_LOGIN, false)
        AppConfig.UserCookie = SpUtils.obtain(SP_USER_INFO, this).getString(KEY_COOKIE,"")
        if (AppConfig.isLogin) {
            DatabaseFactory.getUserInfo().subscribeDbResult({
                AppConfig.userAccount = it
            }, {
                it.printStackTrace()
            })

            DatabaseFactory.getUserProfile().subscribeDbResult({
                AppConfig.userProfile = it
            }, {
                it.printStackTrace()
            })
        }
    }
}