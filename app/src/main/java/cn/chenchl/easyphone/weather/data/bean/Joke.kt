package cn.chenchl.easyphone.weather.data.bean

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Joke(
    @SerializedName("content") val content: String, // 高中一班主任陈老师，因其身高比较矮，同学给起了个外号叫“武大郎”，后来被老师得知，要求同学带家长，同学跟家里说了原委，家长带着同学到老师办公室致歉，该同学家长进门就说:“武老师，小孩子不懂事，还忘您能海涵”，后来同学告诉我们班主任当时那表情都抽搐了，足足让我们笑了一学期。
    @SerializedName("hashId") val hashId: String, // 07f01a03526f4f3f8310dc34a55e0eb2
    @SerializedName("unixtime") val unixtime: String // 1431447231
)