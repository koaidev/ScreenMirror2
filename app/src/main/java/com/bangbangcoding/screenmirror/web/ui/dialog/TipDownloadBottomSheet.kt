package com.bangbangcoding.screenmirror.web.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.bangbangcoding.screenmirror.web.data.local.model.DomainAllow
import com.bangbangcoding.screenmirror.databinding.BottomsheetTipsBinding
import com.bangbangcoding.screenmirror.web.ui.home.TopPageAdapter
import com.bangbangcoding.screenmirror.web.ui.model.apis_models.VideoInfo

class TipDownloadBottomSheet : BottomSheetDialogFragment() {
    companion object {

        private const val KEY_DATA = "KEY_DATA_TIPS"

        fun newInstance(
            domainAllows: ArrayList<DomainAllow>
        ): TipDownloadBottomSheet {
            val args = Bundle()
            args.putParcelableArrayList(KEY_DATA, domainAllows)
            val fragment = TipDownloadBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var topPageAdapter: TopPageAdapter
    private var _binding: BottomsheetTipsBinding? = null
    private val binding get() = _binding!!
    private var videoSelected: VideoInfo? = null
    var mListener: TipDomainClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            topPageAdapter = TopPageAdapter(ArrayList(0),
                object : TopPageAdapter.TopPagesListener {
                    override fun onItemClicked(pageInfo: DomainAllow) {
                        //no-op
                        mListener?.onClick(domain = pageInfo)
                        dismiss()
                    }

                    override fun onItemDelete(pageInfo: DomainAllow) {
                        TODO("Not yet implemented")
                    }
                })
            binding.recycler.apply {
                this.layoutManager = GridLayoutManager(requireContext(), 4)
                adapter = topPageAdapter
            }

            val domains = it.getParcelableArrayList<DomainAllow>(KEY_DATA)

            domains?.let { d ->
                topPageAdapter.setData(d)
            }
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        videoSelected?.isSelect = false
        super.onDismiss(dialog)
    }

    interface TipDomainClickListener {
        fun onClick(domain: DomainAllow)
    }
}
