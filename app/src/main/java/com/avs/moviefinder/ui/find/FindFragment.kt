package com.avs.moviefinder.ui.find

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.avs.moviefinder.R
import com.avs.moviefinder.databinding.FragmentFindBinding
import com.avs.moviefinder.network.ErrorType
import com.avs.moviefinder.ui.BaseFragment
import com.avs.moviefinder.ui.main.MainActivity
import javax.inject.Inject

class FindFragment : BaseFragment() {

    @Inject
    lateinit var findViewModel: FindViewModel

    private lateinit var binding: FragmentFindBinding
    private lateinit var fragmentContext: Context

    private lateinit var choices: Array<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
        this.fragmentContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_find, container, false
        )
        val root: View = binding.root
        binding.findViewModel = findViewModel
        binding.lifecycleOwner = this
        val adapter = FindAdapter(MovieListener({ movieId ->
            Toast.makeText(context, "on movie item clicked $movieId", Toast.LENGTH_LONG).show()
        }, { movieId ->
            Toast.makeText(context, "on share icon clicked $movieId", Toast.LENGTH_LONG).show()
        }, { movieId ->
            Toast.makeText(context, "on watched icon clicked $movieId", Toast.LENGTH_LONG).show()
        }, { movieId ->
            Toast.makeText(context, "on watch later icon clicked $movieId", Toast.LENGTH_LONG).show()
        }))
        binding.rvFindRecyclerView.adapter = adapter
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        findViewModel.movies.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
        findViewModel.isProgressVisible.observe(viewLifecycleOwner, Observer {
            binding.pbFindProgress.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        findViewModel.errorType.observe(viewLifecycleOwner, Observer {
            handleErrorEvent(it)
        })
        choices = arrayOf(
            resources.getString(R.string.popular_movies),
            resources.getString(R.string.top_rated_movies)
        )
        setUpSpinner()
        return root
    }

    private fun setUpSpinner() {
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter(fragmentContext, android.R.layout.simple_spinner_item, choices)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = arrayAdapter
        binding.spinner.onItemSelectedListener = (object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                findViewModel.onSpinnerItemSelected(position)
            }
        })
    }

    private fun handleErrorEvent(it: ErrorType?) {
        when (it) {
            null -> {
                binding.ivError.visibility = View.INVISIBLE
                binding.tvErrorText.visibility = View.INVISIBLE
                binding.spinner.visibility = View.VISIBLE
            }
            ErrorType.NETWORK -> {
                binding.ivError.visibility = View.VISIBLE
                binding.tvErrorText.visibility = View.INVISIBLE
                showSnackBar(resources.getString(R.string.network_error_occurred))
                binding.spinner.visibility = View.GONE
            }
            ErrorType.NO_RESULTS -> {
                binding.ivError.visibility = View.INVISIBLE
                binding.tvErrorText.visibility = View.VISIBLE
                showSnackBar(resources.getString(R.string.no_results_found))
                binding.spinner.visibility = View.GONE
            }
            else -> {
            }
        }
    }
}