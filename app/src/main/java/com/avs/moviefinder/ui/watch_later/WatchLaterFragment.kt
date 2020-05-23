package com.avs.moviefinder.ui.watch_later

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.avs.moviefinder.R
import com.avs.moviefinder.ui.MainActivity
import com.avs.moviefinder.ui.find.FindViewModel
import javax.inject.Inject

class WatchLaterFragment : Fragment() {

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