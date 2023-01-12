package com.mxy.bbs_client.ui.component

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mxy.bbs_client.utility.Utility
import compose.icons.FeatherIcons
import compose.icons.feathericons.Download
import compose.icons.feathericons.Heart
import kotlinx.coroutines.launch
import okio.use
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URL

var vibrator: VibratorManager? = null

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SavableAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale,
) {
    val context = LocalContext.current
    var expanded by remember {
        mutableStateOf(false)
    }
    val sz by animateFloatAsState(targetValue = if (expanded) 1.07f else 1.0f)
    val alpha by animateFloatAsState(targetValue = if (expanded) 0.7f else 1.0f)
    Box(modifier = modifier.pointerInput(Unit) {
        detectTapGestures(
            onLongPress = {
                try {
                    vibrator?.vibrate(
                        CombinedVibration.createParallel(
                            VibrationEffect.startComposition()
                                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK)
                                .compose()
                        )
                    )
                } catch (e: Exception) {
                    Log.d("SavableAsyncImage", "振动失败")
                }
                expanded = true
            }
        )
    }) {
        DropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false },
        )
        {
            DropdownMenuItem(
                onClick = {
                    saveToGallery(model, context)
                    expanded = false
                },
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {

                Text(
                    text = "保存",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(5.dp)
                )
                Icon(
                    FeatherIcons.Download,
                    contentDescription = "Download Icon",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(5.dp)
                )
            }
            Divider()
            //收藏
            DropdownMenuItem(
                onClick = {
                    /*TODO:收藏*/
                    expanded = false
                },
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {

                Text(
                    text = "收藏",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(5.dp)
                )
                Icon(
                    FeatherIcons.Heart,
                    contentDescription = "Download Icon",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(5.dp)
                )
            }
        }
        SubcomposeAsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
                .scale(sz)
                .alpha(alpha)
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                CircularProgressIndicator()
            } else {
                SubcomposeAsyncImageContent()
            }
        }
    }
}

private fun saveToGallery(url: Any?, context: Context) {
    with(Utility.IOCoroutineScope) {
        launch {
            try {
                saveMediaToStorage(url, context)
            } catch (e: Exception) {
                with(Utility.UICoroutineScope) {
                    launch {
                        Toast.makeText(context, "不受支持的图片格式", Toast.LENGTH_SHORT).show()
                    }
                }
                Log.d("Exception", "$e")
            }
        }
    }
}

private fun saveMediaToStorage(url: Any?, context: Context) {
    val urlStr = url as String
    Log.d("Exception", "得到的url: $url")
    val filename = URLUtil.guessFileName(urlStr, null, null)
    Log.d("Exception", "猜测的文件名: $filename")
    var fos: OutputStream? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver?.also { resolver ->
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/${FilenameUtils.getExtension(filename)}")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }
    } else {
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
    }
    fos?.use {
        IOUtils.copy(URL(urlStr), it)
    }
    with(Utility.UICoroutineScope) {
        launch {
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
        }
    }
}
