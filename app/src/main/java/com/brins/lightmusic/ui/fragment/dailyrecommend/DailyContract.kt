package com.brins.lightmusic.ui.fragment.dailyrecommend

import com.brins.lightmusic.model.dailyrecommend.DailyRecommendResult
import com.brins.lightmusic.ui.base.BasePresenter
import com.brins.lightmusic.ui.base.BaseView

interface DailyContract {

    interface View : BaseView<Presenter> {
        fun onMusicLoad(recommendResult: DailyRecommendResult)
    }

    interface Presenter : BasePresenter<View> {
        fun loadDailyRecommend()
    }
}