package uz.ssd.sdk.cardcamerascan


/**
 * Created by DostonbekIbragimov on 23/03/2021 Email: idostonbek1230@mail.ru
 */
fun main(){
    val str = "gghjhgkhlj;k'l6453 541 654 54 \n" +
            "8600 0000 0000 0000\n" +
            "03/25\n" +
            "03 / 25\n" +
            "04 /25\n" +
            "05/ 25\n" +
            "aa bb\n" +
            "aa / bb\n" +
            "\n"
    val cardExpire = expireDate(str) ?: ""
    println(cardExpire)
}
fun expireDate(detections: String): String? {
    val regex = "^(0[1-9]|1[0-2])([ ]?)/([ ]?)([0-9]{2})\$".toRegex()
    val matcher = regex.find(detections)
    return if (matcher != null) {
        val cardExpire = detections.substring(matcher.range.first, matcher.range.last + 1)
        cardExpire
    } else null

}