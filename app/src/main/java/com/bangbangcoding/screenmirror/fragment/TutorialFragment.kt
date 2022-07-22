package com.bangbangcoding.screenmirror.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bangbangcoding.screenmirror.R
import com.bangbangcoding.screenmirror.databinding.FragmentTutorialBinding
import com.bangbangcoding.screenmirror.viewmodel.TutorialViewModel

class TutorialFragment(private val position: Int) : Fragment() {
    private lateinit var binding: FragmentTutorialBinding
    private lateinit var viewModel: TutorialViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTutorialBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[TutorialViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         val imageList: ArrayList<Drawable> =
            arrayListOf(
                resources.getDrawable(R.drawable.tutorial1, resources.newTheme()),
                resources.getDrawable(R.drawable.tutorial2, resources.newTheme()),
                resources.getDrawable(R.drawable.tutorial3, resources.newTheme()),

                )

         val titleList =
            arrayListOf("Kết nối cùng wifi", "Kết nối Miracast", "Nhấn vào nút “ Screen Mirroring”")

         val subTitles = arrayListOf(
            "Đảm bảo rằng điện thoại và Tivi( hoặc thiết bị) của bạn được kết nối cùng một wifi và tắt VPN.",
            "Mở ứng dụng kết nối Miracast hoặc bật chức năng “ Miracast” trên Tivi của bạn.",
            "Nhấn vào nút “ Screen Mirroring” bật chức năng hiển thị không dây trên điện thoại và chọn thiết bị bạn muốn kết nối."
        )

        binding.txtPosition.text = (position + 1).toString()
        binding.txtTitle.text = titleList[position]
        binding.imgTutorial.setImageDrawable(imageList[position])
        binding.txtContent.text = subTitles[position]

    }

    override fun onResume() {
        viewModel.currentPage.value=position
        super.onResume()
    }
}