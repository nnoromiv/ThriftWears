package com.nnorom.thriftwears.item

import android.net.Uri

data class UploadedImagesItem(
    var image1: Uri? = null,
    var image2: Uri? = null,
    var image3: Uri? = null,
    var image4: Uri? = null,
    var image5: Uri? = null
)