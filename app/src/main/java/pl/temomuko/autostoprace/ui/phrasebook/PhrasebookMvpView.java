package pl.temomuko.autostoprace.ui.phrasebook;

import java.util.List;

import pl.temomuko.autostoprace.domain.model.Phrasebook;
import pl.temomuko.autostoprace.ui.base.drawer.DrawerMvpView;

/**
 * Created by Szymon Kozak on 2016-04-09.
 */
public interface PhrasebookMvpView extends DrawerMvpView {

    void updateSpinner(int languagePosition, Phrasebook.LanguagesHeader languagesHeader);

    void updatePhrasebookData(int languagePosition, List<Phrasebook.Item> translationItems);

    void clearPhrasesFilter();

    void filterPhrases(String query);

    void changePhrasebookLanguage(int position);
}
