package com.avs.moviefinder.ui.watch_later

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.avs.moviefinder.R
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.main.MainActivity
import javax.inject.Inject

class WatchLaterFragment : BaseFragment() {

    @Inject
    lateinit var watchLaterViewModel: WatchLaterViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_watch_later, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        watchLaterViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}