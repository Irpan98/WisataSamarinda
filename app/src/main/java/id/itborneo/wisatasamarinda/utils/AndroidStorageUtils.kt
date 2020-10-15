package id.itborneo.wisatasamarinda.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class AndroidStorageUtils(private val context: Context) {
    private val TAG = "AndroidStorageUtils"
    fun saveFile(b: Bitmap, picName: String?) {
        val fos: FileOutputStream
        try {
            fos = context.openFileOutput(picName, Context.MODE_PRIVATE)
            b.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "file not found")
            e.printStackTrace()
        } catch (e: IOException) {
            Log.d(TAG, "io exception")
            e.printStackTrace()
        } finally {

//            fos.close()
        }
    }

//    fun getContactBitmapFromURI(context: Context, uri: Uri?): Bitmap? {
//        try {
//            stream.write(uriStr.getBytes());
//        } finally {
//            stream.close();
//        }
//    }

    fun loadBitmap(picName: String?): Bitmap? {
        var b: Bitmap? = null
        val fis: FileInputStream
        try {
            fis = context.openFileInput(picName)
            b = BitmapFactory.decodeStream(fis)
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "file not found")
            e.printStackTrace()
        } catch (e: IOException) {
            Log.d(TAG, "io exception")
            e.printStackTrace()
        } finally {
//            fis.close()
        }
        return b
    }
}