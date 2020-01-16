package cn.chenchl.libs.phone

import androidx.annotation.Keep

/**
 * created by ccl on 2020/1/9
 **/
@Keep
data class ContentCallLog(
    val name: String?,
    val phoneNumber: String?,
    val date: String?,
    val duration: String?,
    val callType: Int?
)