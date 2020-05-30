package com.avs.moviefinder.ui.find

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.FragmentFindBinding
import com.avs.moviefinder.ui.MainActivity
import javax.inject.Inject

class FindFragment : Fragment() {

    @Inject
    lateinit var findViewModel: FindViewModel

    private lateinit var binding: FragmentFindBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_find, container, false)
        val root: View = binding.root
        binding.findViewModel = findViewModel
        binding.lifecycleOwner = this
        val adapter = FindAdapter()
        binding.rvFindRecyclerView.adapter = adapter

        findViewModel.movies.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.d("jjj", it.toString())
                adapter.submitList(it)
            }
        })
        return root
    }
}