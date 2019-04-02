package pl.temomuko.autostoprace.ui.staticdata.image

import pl.temomuko.autostoprace.domain.repository.RaceInfoRepository
import pl.temomuko.autostoprace.ui.base.BasePresenter
import pl.temomuko.autostoprace.util.rx.RxUtil
import rx.subscriptions.Subscriptions
import javax.inject.Inject

class FullscreenImagePresenter @Inject constructor(
    private val raceInfoRepository: RaceInfoRepository
) : BasePresenter<FullscreenImageView>() {

    private var loadRaceSubscription = Subscriptions.unsubscribed()

    fun loadRaceInfoImages() {
        loadRaceSubscription = raceInfoRepository.getRaceInfoImages()
            .compose(RxUtil.applySingleIoSchedulers())
            .subscribe(mvpView::setRaceInfoImages)
    }

    override fun detachView() {
        loadRaceSubscription.unsubscribe()
        super.detachView()
    }
}
