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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.rabbitapp.R
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.utils.MainListViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


abstract class FragmentWithPicture : Fragment() {

    protected val viewModel: MainListViewModel by activityViewModels()

    fun setPictureToSelectedOrDefault(
        pictureView: ImageView,
        entity: HomeListItem?,
        drawable: Int
    ) {
        if (entity?.imagePath != null && entity.imagePath.isNotEmpty()) {
            entity.imagePath.stream().filter { path -> BitmapFactory.decodeFile(path) != null }
                .findFirst().ifPresent {
                pictureView.setImageBitmap(BitmapFactory.decodeFile(it))
                pictureView.tag = it
            }
        } else {
            pictureView.setImageResource(drawable)
            pictureView.tag = drawable
        }
    }

    fun setGalleryLauncher(picture: ImageView) {
        if (!viewModel.getEditable()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.non_editable), Toast.LENGTH_SHORT
            ).show()
            return
        }
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
        if (item?.imagePath == null || item.imagePath.isEmpty()) {
            return
        }
        item.imagePath.stream().filter { path -> BitmapFactory.decodeFile(path) != null }
            .findFirst().ifPresent {
            val reportFilePath = File(it)
            reportFilePath.delete()
        }

    }

}