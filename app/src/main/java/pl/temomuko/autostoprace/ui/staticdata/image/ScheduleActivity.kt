package pl.temomuko.autostoprace.ui.staticdata.image

import pl.temomuko.autostoprace.domain.model.RaceInfoImages

class ScheduleActivity : FullscreenImageActivity() {

    override fun getImageUrl(raceInfoImages: RaceInfoImages): String {
        return raceInfoImages.scheduleImageUrl
    }
}
