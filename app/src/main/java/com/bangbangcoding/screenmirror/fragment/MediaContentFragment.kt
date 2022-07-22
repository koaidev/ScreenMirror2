package com.bangbangcoding.screenmirror.fragment

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangbangcoding.screenmirror.adapter.MediaAdapter
import com.bangbangcoding.screenmirror.viewmodel.MediaViewModel
import com.bangbangcoding.screenmirror.databinding.FragmentMediaBinding
import com.bangbangcoding.screenmirror.model.MediaItem

class MediaContentFragment(private val position: Int) : Fragment() {
    constructor() : this(0)
    private lateinit var mediaViewModel: MediaViewModel
    private var _binding: FragmentMediaBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mediaViewModel = ViewModelProvider(this)[MediaViewModel::class.java]
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaViewModel.getAllVideos(requireActivity().contentResolver)
        mediaViewModel.getAllImages(requireActivity().contentResolver)
        mediaViewModel.getAllMediaFile(requireActivity().contentResolver)
        binding.rcvMedia.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        val mediaAdapter = MediaAdapter(activity = requireActivity())
        binding.rcvMedia.adapter = mediaAdapter

        mediaViewModel.images.observe(viewLifecycleOwner) {
            if (position == 2) {
                mediaAdapter.medias.clear()
                val medias = it
                mediaAdapter.medias.addAll(medias)

                mediaAdapter.notifyDataSetChanged()
            }
        }

        mediaViewModel.videos.observe(viewLifecycleOwner) {
            if (position == 1) {
                mediaAdapter.medias.clear()
                val medias = it
                mediaAdapter.medias.addAll(medias)
                mediaAdapter.notifyDataSetChanged()
            }
        }

        mediaViewModel.medias.observe(viewLifecycleOwner) {
            if (position == 0) {
                mediaAdapter.medias.clear()
                val medias = it
                mediaAdapter.medias.addAll(medias)
                mediaAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}