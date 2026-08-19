package uz.myprint.feature.feature.printshop.domain.model

/**
 * Ish vaqti yarim tundan boshlab daqiqalarda saqlanadi: 09:00 -> 540.
 * Bitta son bilan solishtirish soat/daqiqa juftligidan osonroq.
 */
data class WorkingHours(

    val opensAtMinutes: Int = 9 * 60,

    val closesAtMinutes: Int = 18 * 60,

    val worksOnSaturday: Boolean = true,

    val worksOnSunday: Boolean = false,

    val isOpen24: Boolean = false

) {

    fun isOpenAt(minutesOfDay: Int, isWeekend: Boolean = false): Boolean {

        if (isOpen24) return true

        if (isWeekend && !worksOnSaturday && !worksOnSunday) return false

        return minutesOfDay in opensAtMinutes until closesAtMinutes
    }

    fun formatted(): String {

        if (isOpen24) return "24/7"

        return "${format(opensAtMinutes)} - ${format(closesAtMinutes)}"
    }

    private fun format(minutes: Int): String =
        "%02d:%02d".format(minutes / 60, minutes % 60)
}
