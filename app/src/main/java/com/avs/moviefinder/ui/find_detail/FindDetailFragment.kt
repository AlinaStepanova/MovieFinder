package com.avs.moviefinder.ui.find_detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.FragmentFindDetailBinding
import com.avs.moviefinder.ui.main.MainActivity
import javax.inject.Inject

class FindDetailFragment : Fragment() {

    @Inject
    lateinit var findDetailViewModel: FindDetailViewModel

    private lateinit var binding: FragmentFindDetailBinding

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
            inflater, R.layout.fragment_find_detail, container, false
        )
        val root: View = binding.root
        return root
    }
}