package com.brins.lightmusic.ui.fragment.video

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jzvd.Jzvd
import com.brins.lightmusic.R
import com.brins.lightmusic.model.musicvideo.Mv
import com.brins.lightmusic.model.musicvideo.MvCommentsBean
import com.brins.lightmusic.ui.activity.MainActivity
import com.brins.lightmusic.ui.base.BaseFragment
import com.brins.lightmusic.ui.base.adapter.CommonViewAdapter
import com.brins.lightmusic.ui.base.adapter.ViewHolder
import com.brins.lightmusic.ui.customview.CommonHeaderView
import com.brins.lightmusic.utils.SpacesItemDecoration
import com.brins.lightmusic.utils.launch
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_video_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoDetailFragment : BaseFragment<VideoContract.Presenter>(),
    CommonHeaderView.OnBackClickListener, VideoContract.View {


    private var mPresenter: VideoContract.Presenter? = null
    private var mCurrentMv: Mv? = null
    private lateinit var mCommentAdapter: CommonViewAdapter<MvCommentsBean>

    override fun getLayoutResID(): Int {
        return R.layout.fragment_video_detail
    }

    override fun onCreateViewAfterBinding(view: View) {
        super.onCreateViewAfterBinding(view)
        VideoPresent(this).subscribe(this)
        head.setOnBackClickListener(this)
        mCurrentMv = arguments?.getParcelable("Mv")
        if (mCurrentMv != null) {
            loadMvComments()
            head.title = "${mCurrentMv!!.dataBean.artistName} ${mCurrentMv!!.dataBean.name}"
            videoPlayer.setAllControlsVisiblity(GONE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE)
            Glide.with(context!!).load(mCurrentMv!!.dataBean.cover)
                .into(videoPlayer.thumbImageView)
            videoPlayer.setUp(
                mCurrentMv!!.metaDataBean.url,
                mCurrentMv!!.dataBean.name,
                Jzvd.SCREEN_NORMAL
            )
        }
    }

    override fun onBackClick(view: View) {
        (activity as MainActivity).onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }


    override fun setPresenter(presenter: VideoContract.Presenter) {
        mPresenter = presenter
    }

    //MVP View

    private fun loadMvComments(){
        launch({
            showLoading()
            val result = loadComments()
            if (result?.comments != null){
                val list = result.comments
                mCommentAdapter = object : CommonViewAdapter<MvCommentsBean>(
                    context!!,
                    R.layout.item_comment,
                    list!!
                ) {
                    override fun converted(holder: ViewHolder, t: MvCommentsBean, position: Int) {
                        val playlist = (list!![position])
                        holder.setImageResource(R.id.imgCover, playlist.user!!.avatarUrl)
                        holder.setText(R.id.textViewName, playlist.user!!.nickname)
                        holder.setText(R.id.textViewArtist, playlist.content)
                    }

                }
            }
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = mCommentAdapter
            recyclerView.addItemDecoration(SpacesItemDecoration(10))
            hideLoading()
            },{
            Toast.makeText(context, R.string.connect_error, Toast.LENGTH_SHORT).show()
            hideLoading()
        })
    }

    private suspend fun loadComments() = withContext(Dispatchers.IO){
        val result = mPresenter?.loadVideoComments(mCurrentMv!!.dataBean.id)
        result
    }
}