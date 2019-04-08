package pl.temomuko.autostoprace.ui.competitions

import pl.temomuko.autostoprace.domain.model.Competition
import pl.temomuko.autostoprace.ui.base.MvpView

interface CompetitionsMvpView : MvpView {

    fun showCompetitions(competitions: List<Competition>)
    fun showErrorMessage()
    fun showEmptyState()
    fun setLoadingEnabled(enabled: Boolean)
}
