package com.example.rabbitapp.ui.mainTab.add

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.utils.Gender
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


abstract class AddFragment : Fragment() {

    protected val viewModel: MainListViewModel by activityViewModels()

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

    fun setPictureToSelectedOrDefault(
        pictureView: ImageView,
        entityToEdit: HomeListItem?,
        drawable: Int
    ) {
        if (entityToEdit?.imagePath != null && entityToEdit.imagePath!!.isNotEmpty()) {
            pictureView.setImageBitmap(BitmapFactory.decodeFile(entityToEdit.imagePath!!))
            pictureView.tag = entityToEdit.imagePath!!
        } else {
            pictureView.setImageResource(drawable)
            pictureView.tag = drawable
        }
    }

    fun setParents(item: HomeListItem?) {
        viewModel.selectedMother =
            item?.fkMother?.let { viewModel.getRabbitFromId(it) }
        viewModel.selectedFather =
            item?.fkFather?.let { viewModel.getRabbitFromId(it) }
    }

    fun setGalleryLauncher(picture: ImageView) {
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
                    picture.tag = UUID.randomUUID().toString()
                }
            }

        picture.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
    }

    fun saveNewPicture(item: HomeListItem?, picture: ImageView): String? {
        if (picture.tag == R.drawable.rabbit_back || picture.tag == R.drawable.rabbit_2_back) {
            return null
        }
        removeOldImage(item)
        val internalStorage: File = requireContext().getDir("RabbitPictures", Context.MODE_PRIVATE)
        val imageFilePath = File(internalStorage, UUID.randomUUID().toString() + ".png")
        var picturePath = imageFilePath.toString()

        val fos: FileOutputStream?
        try {
            fos = FileOutputStream(imageFilePath)

            val drawableBitmap = (picture.drawable as BitmapDrawable).bitmap

            val bitmapWidth = drawableBitmap.width
            val bitmapHeight = drawableBitmap.height

            // Ensure the ImageView has been laid out
            if (bitmapWidth > 0 && bitmapHeight > 0) {
                val softwareBitmap =
                    Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)

                // Draw the ImageView onto the software-rendered bitmap
                val canvas = Canvas(softwareBitmap)
                if (picture.drawable is BitmapDrawable) {
                    // Convert the drawable to a software bitmap
                    val softwareDrawableBitmap = drawableBitmap.copy(Bitmap.Config.ARGB_8888, false)
                    canvas.drawBitmap(softwareDrawableBitmap, 0f, 0f, null)
                } else {
                    picture.draw(canvas)
                }

                // Compress and save the bitmap
                softwareBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }

            fos.close()
        } catch (ex: Exception) {
            Log.i("DATABASE", "Problem updating picture", ex)
            picturePath = ""
            picture.tag = R.drawable.rabbit_back
        }

        return picturePath
    }

    private fun removeOldImage(item: HomeListItem?) {
        if (item?.imagePath == null || item.imagePath!!.isEmpty()) {
            return
        }
        val reportFilePath = File(item.imagePath!!)
        reportFilePath.delete()
    }

}