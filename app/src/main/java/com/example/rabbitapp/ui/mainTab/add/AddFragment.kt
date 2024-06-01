package com.example.rabbitapp.ui.mainTab.add

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.utils.Gender
import java.time.format.DateTimeFormatter

abstract class AddFragment : Fragment() {

    protected val viewModel: MainListViewModel by activityViewModels()
    protected val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    fun parentSelect(
        parentGender: Gender,
        pickMotherListFragment: Int,
        pickFatherListFragment: Int
    ) {
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        if (parentGender == Gender.FEMALE) {
            view?.findNavController()
                ?.navigate(pickMotherListFragment)
        }
        if (parentGender == Gender.MALE) {
            view?.findNavController()
                ?.navigate(pickFatherListFragment)
        }
        transaction.commit()
    }

//    private fun setPictureToSelectedOrDefault() {
//        if (viewModel.selectedRabbit != null && viewModel.selectedRabbit!!.imagePath != null && viewModel.selectedRabbit!!.imagePath!!.isNotEmpty()) {
//            binding.addLitterPicture.setImageBitmap(BitmapFactory.decodeFile(viewModel.selectedRabbit!!.imagePath!!));
//        } else {
//            binding.addLitterPicture.setImageResource(R.drawable.rabbit_2_back)
//        }
//    }

    fun getGalleryLauncher(picture: ImageView): ActivityResultLauncher<String> {
        val galleryLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(
                                requireContext().contentResolver,
                                it
                            )
                        )
                    } else {
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
                    }
                    picture.setImageBitmap(bitmap)
                }
            }
        return galleryLauncher
    }

}