package com.brins.lightmusic.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.brins.lightmusic.R
import com.brins.lightmusic.utils.*

class VideoPagerAdapter(
    fm: FragmentManager,
    var list: MutableList<out Fragment>,
    var tabtitle: Array<String> = arrayOf(
        MAINLAND,
        HONGKONG_TAIWAN,
        EUROPE_AMERICA,
        JAPAN,
        KOREA
    )
) :
    FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    fun getTabView(context: Context, position: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.main_tab, null)
        val tab_item: TextView = view.findViewById(R.id.tab_item)
        tab_item.textSize = 15f
        tab_item.setTextColor(Color.GRAY)
        if (position == 0) {
            tab_item.setTextColor(Color.BLACK)
        }
        tab_item.text = tabtitle[position]
        return view
    }
}