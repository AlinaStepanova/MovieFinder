package com.avs.moviefinder.ui.watch_later

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.avs.moviefinder.R

class WatchLaterFragment : Fragment() {

    private lateinit var watchLaterViewModel: WatchLaterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        watchLaterViewModel =
            ViewModelProviders.of(this).get(WatchLaterViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_watch_later, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        watchLaterViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}