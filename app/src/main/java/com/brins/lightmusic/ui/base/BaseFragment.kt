package com.brins.lightmusic.ui.base


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : Fragment() {

    val TAG = javaClass.simpleName
    protected var mBindDestroyDisposable: CompositeDisposable? = null

    /*实现懒加载*/
    protected var mIsViewBinding: Boolean = false
    protected var mIsVisibleToUser: Boolean = false
    protected var mHadLoaded: Boolean = false

    abstract fun getLayoutResID(): Int

    protected open fun onCreateViewAfterBinding(view : View){
        mIsViewBinding = true
        checkLoad()
    }

    private fun checkLoad() {
        if (!mHadLoaded && mIsViewBinding && mIsVisibleToUser) {
            onLazyLoad()
            bindUntilDestroy(subscribeEvents())
            mHadLoaded = true
        }
    }

    open fun subscribeEvents(): Disposable? {
        return null
    }

    private fun bindUntilDestroy(disposable: Disposable?) {
        if (disposable == null){
            return
        }
        if (mBindDestroyDisposable == null) {
            mBindDestroyDisposable = CompositeDisposable()
        }
        mBindDestroyDisposable!!.add(disposable)
    }

    open fun onLazyLoad() {}

    protected open fun beforeCreateView() {}

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mIsVisibleToUser = isVisibleToUser
        if (isVisibleToUser) {
            checkLoad()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(getLayoutResID(), container, false)
        onCreateViewAfterBinding(view)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mBindDestroyDisposable != null){
            mBindDestroyDisposable!!.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mIsViewBinding = false
        mIsVisibleToUser = false
        mHadLoaded = false
    }
}
