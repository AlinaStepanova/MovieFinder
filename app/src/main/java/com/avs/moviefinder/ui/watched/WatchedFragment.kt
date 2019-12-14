package com.avs.moviefinder.ui.watched

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.avs.moviefinder.R

class WatchedFragment : Fragment() {

    private lateinit var watchedViewModel: WatchedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        watchedViewModel =
            ViewModelProviders.of(this).get(WatchedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_watched, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        watchedViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}