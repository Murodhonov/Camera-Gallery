package uz.umarxon.cameragallery

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import uz.umarxon.cameragallery.databinding.ActivityGalleryBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class GalleryActivity : AppCompatActivity() {

    lateinit var binding : ActivityGalleryBinding
    val CODE_REQUEST = 1
    lateinit var absolutePath:ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addImage.setOnClickListener {
            pickImageFromGalleryOld()
        }
        binding.addImageNew.setOnClickListener {
            getImageContent.launch("image/*")
        }
        binding.remove.setOnClickListener {
            clearImages()
        }
    }

    private fun clearImages() {
        val extrenalFilesDir = filesDir
        if (extrenalFilesDir?.isDirectory == true){
            val listFiles = extrenalFilesDir.listFiles()
            if (listFiles?.isEmpty() == true){
                Snackbar.make(binding.root,"Rasm yo'q",Snackbar.LENGTH_SHORT).show()
                return
            }

            listFiles?.forEach {
                println(it)
                it.delete()
            }
        }
    }

    private val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri ->
        uri ?: return@registerForActivityResult
        binding.image.setImageURI(uri)

        val inputStream = contentResolver?.openInputStream(uri)
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val file = File(filesDir,"${simpleDateFormat}rasm.jpg")
        val fileOutputStream = FileOutputStream(file)
        inputStream?.copyTo(fileOutputStream)

        inputStream?.close()
        fileOutputStream.close()

        /*val fileInputStream = FileInputStream(file)

        val readBytes = fileInputStream.readBytes()

        binding.image.setImageBitmap(BitmapFactory.decodeByteArray(readBytes, 0, readBytes.size))*/

    }

    private fun pickImageFromGalleryOld() {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        },CODE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (CODE_REQUEST == requestCode && resultCode == Activity.RESULT_OK){

            val uri = data?.data ?: return
            binding.image.setImageURI(uri)
            val inputStream = contentResolver?.openInputStream(uri)
            val format = SimpleDateFormat("yyMMdd_hhmmss").format(Date())
            val file = File(filesDir, "${format}_image.jpg")
            val fileOutputStream = FileOutputStream(file)
            inputStream?.copyTo(fileOutputStream)
            inputStream?.close()
            fileOutputStream.close()

            val fileInputStream = FileInputStream(file)

            val readBytes = fileInputStream.readBytes()

            absolutePath = readBytes

        }
    }

}