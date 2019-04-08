package pl.temomuko.autostoprace.ui.competitions

import pl.temomuko.autostoprace.domain.model.Competition
import pl.temomuko.autostoprace.domain.repository.RaceInfoRepository
import pl.temomuko.autostoprace.ui.base.BasePresenter
import pl.temomuko.autostoprace.util.rx.RxUtil
import rx.subscriptions.Subscriptions
import javax.inject.Inject

class CompetitionsPresenter @Inject constructor(
    private val raceInfoRepository: RaceInfoRepository
) : BasePresenter<CompetitionsMvpView>() {

    private var loadCompetitionsSubscription = Subscriptions.unsubscribed()

    fun loadCompetitions() {
        mvpView.setLoadingEnabled(true)
        loadCompetitionsSubscription = raceInfoRepository.getCompetitions()
            .compose(RxUtil.applySingleIoSchedulers())
            .doAfterTerminate { mvpView.setLoadingEnabled(false) }
            .subscribe({ competitions ->
                showCompetitions(competitions)
            }, {
                mvpView.showErrorMessage()
            })
    }

    private fun showCompetitions(competitions: List<Competition>) {
        if(competitions.isEmpty()) {
            mvpView.showEmptyState()
        } else {
            mvpView.showCompetitions(competitions)
        }
    }

    override fun detachView() {
        loadCompetitionsSubscription.unsubscribe()
        super.detachView()
    }
}
