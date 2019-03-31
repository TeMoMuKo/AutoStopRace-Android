package pl.temomuko.autostoprace.ui.staticdata.image

import pl.temomuko.autostoprace.domain.model.RaceInfoImages
import pl.temomuko.autostoprace.ui.base.MvpView

interface FullscreenImageView : MvpView {

    fun setRaceInfoImages(raceInfoImages: RaceInfoImages)
}
