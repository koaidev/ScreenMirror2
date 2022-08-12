package com.bangbangcoding.screenmirror.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangbangcoding.screenmirror.adapter.MediaAdapter
import com.bangbangcoding.screenmirror.db.viewmodel.MediaViewModel
import com.bangbangcoding.screenmirror.databinding.FragmentMediaBinding
import com.bangbangcoding.screenmirror.db.model.MediaItem
import com.bangbangcoding.screenmirror.db.model.group.DateItem
import com.bangbangcoding.screenmirror.db.model.group.GeneralItem
import com.bangbangcoding.screenmirror.db.model.group.ListItem

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
        binding.rcvMediaContainer.layoutManager =
            GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (binding.rcvMediaContainer.adapter?.getItemViewType(position) == ListItem.TYPE_DATE) {
                            3
                        } else {
                            1
                        }
                    }

                }
            }
        binding.rcvMediaContainer.setHasFixedSize(true)
        val mediaAdapter = MediaAdapter(activity = requireActivity())
        binding.rcvMediaContainer.adapter = mediaAdapter
        mediaViewModel.images.observe(viewLifecycleOwner) { it ->
            if (position == 2) {
                mediaAdapter.medias.clear()
                val consolidatedList = mutableListOf<ListItem>()
                val groupItems: List<MediaItem> = it
                groupItems.forEach {
                    consolidatedList.add(
                        GeneralItem(
                            it.id,
                            it.isVideo,
                            it.uri,
                            it.name,
                            it.duration,
                            it.size
                        )
                    )
                }
                mediaAdapter.medias.addAll(consolidatedList)

                mediaAdapter.notifyDataSetChanged()
            }
        }
        mediaViewModel.videos.observe(viewLifecycleOwner) { it ->
            if (position == 1) {
                mediaAdapter.medias.clear()


                val consolidatedList = mutableListOf<ListItem>()
                val groupItems: List<MediaItem> = it
                groupItems.forEach {
                    consolidatedList.add(
                        GeneralItem(
                            it.id,
                            it.isVideo,
                            it.uri,
                            it.name,
                            it.duration,
                            it.size
                        )
                    )
                }
                mediaAdapter.medias.addAll(consolidatedList)
                mediaAdapter.notifyDataSetChanged()
            }
        }

        mediaViewModel.medias.observe(viewLifecycleOwner) { it ->
            if (position == 0) {
                mediaAdapter.medias.clear()
                val groupedMapMap: Map<String, List<MediaItem>> = it.groupBy {
                    it.dateAdd!!
                }

                val consolidatedList = mutableListOf<ListItem>()
                for (date: String in groupedMapMap.keys) {
                    consolidatedList.add(DateItem(date))
                    val groupItems: List<MediaItem>? = groupedMapMap[date]
                    groupItems?.forEach {
                        consolidatedList.add(
                            GeneralItem(
                                it.id,
                                it.isVideo,
                                it.uri,
                                it.name,
                                it.duration,
                                it.size
                            )
                        )
                    }
                }
                mediaAdapter.medias.addAll(consolidatedList)
                mediaAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}