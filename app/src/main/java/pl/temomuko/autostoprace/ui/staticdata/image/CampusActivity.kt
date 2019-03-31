package pl.temomuko.autostoprace.ui.staticdata.image

import pl.temomuko.autostoprace.domain.model.RaceInfoImages

class CampusActivity : FullscreenImageActivity() {

    override fun getImageUrl(raceInfoImages: RaceInfoImages): String {
        return raceInfoImages.campusMapImageUrl
    }
}
