package pl.temomuko.autostoprace.ui.competitions

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_competitions.*
import pl.temomuko.autostoprace.R
import pl.temomuko.autostoprace.domain.model.Competition
import pl.temomuko.autostoprace.ui.base.drawer.DrawerActivity
import pl.temomuko.autostoprace.ui.widget.VerticalDividerItemDecoration
import javax.inject.Inject

class CompetitionsActivity : DrawerActivity(), CompetitionsMvpView {

    @Inject lateinit var presenter: CompetitionsPresenter

    private val competitionsAdapter = CompetitionsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competitions)
        activityComponent.inject(this)
        presenter.attachView(this)
        setupCompetitionsRecyclerView()
        presenter.loadCompetitions()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    private fun setupCompetitionsRecyclerView() {
        with(competitionsRecyclerView) {
            setHasFixedSize(true)
            adapter = competitionsAdapter
            addItemDecoration(VerticalDividerItemDecoration(this@CompetitionsActivity))
        }
    }

    override fun showCompetitions(competitions: List<Competition>) {
        competitionsRecyclerView.visibility = View.VISIBLE
        messageTextView.visibility = View.GONE
        competitionsAdapter.submitList(competitions)
    }

    override fun showErrorMessage() {
        competitionsRecyclerView.visibility = View.GONE
        messageTextView.visibility = View.VISIBLE
        messageTextView.setText(R.string.error_cannot_load_competitions)
    }

    override fun showEmptyState() {
        competitionsRecyclerView.visibility = View.GONE
        messageTextView.visibility = View.VISIBLE
        messageTextView.setText(R.string.msg_empty_state_competitions)
    }

    override fun setLoadingEnabled(enabled: Boolean) {
        loadingProgressBar.visibility = if (enabled) View.VISIBLE else View.GONE
    }
}
