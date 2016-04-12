package pl.temomuko.autostoprace.data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szymon Kozak on 2016-04-09.
 */
public class Phrasebook {

    private LanguagesHeader mLanguagesHeader;
    private List<Item> mPhraseItems;

    public static Phrasebook createFromCsvRows(List<String[]> csvRows, int languageRowPosition,
                                               int originalLangColumnPosition) {
        Phrasebook.LanguagesHeader languagesHeader = new Phrasebook.LanguagesHeader();
        List<Phrasebook.Item> phraseItems = new ArrayList<>();
        for (int i = 0; i < csvRows.size(); i++) {
            String[] fullRow = csvRows.get(i);
            String originalItem = fullRow[originalLangColumnPosition];
            String[] foreignValues = getForeignValues(fullRow, originalLangColumnPosition);
            if (i == languageRowPosition) {
                languagesHeader = new Phrasebook.LanguagesHeader(originalItem, foreignValues);
            } else {
                phraseItems.add(new Phrasebook.Item(originalItem, foreignValues));
            }
        }
        return new Phrasebook(languagesHeader, phraseItems);
    }

    private static String[] getForeignValues(String[] fullRow, int originalLangColumnPosition) {
        String[] foreignValues = new String[fullRow.length - 1];
        System.arraycopy(fullRow, 0, foreignValues, 0, originalLangColumnPosition);
        System.arraycopy(fullRow, originalLangColumnPosition + 1, foreignValues,
                originalLangColumnPosition, fullRow.length - originalLangColumnPosition - 1);
        return foreignValues;
    }

    private Phrasebook(LanguagesHeader languagesHeader, List<Item> phraseItems) {
        mLanguagesHeader = languagesHeader;
        mPhraseItems = phraseItems;
    }

    public List<Item> getPhraseItems() {
        return mPhraseItems;
    }

    public LanguagesHeader getLanguagesHeader() {
        return mLanguagesHeader;
    }

    public static class Item {

        private String mOriginalPhrase;
        private String[] mTranslations;

        public Item(String originalPhrase, String[] translations) {
            mOriginalPhrase = originalPhrase;
            mTranslations = translations;
        }

        public String getOriginalPhrase() {
            return mOriginalPhrase;
        }

        public String[] getTranslations() {
            return mTranslations;
        }

        public String getTranslation(int languagePosition) {
            return mTranslations[languagePosition];
        }
    }

    public static class LanguagesHeader {

        private String mOriginalLanguage;
        private String[] mForeignLanguages;

        public LanguagesHeader() {
            //no-op
        }

        public LanguagesHeader(String originalLanguage, String[] foreignLanguages) {
            mOriginalLanguage = originalLanguage;
            mForeignLanguages = foreignLanguages;
        }

        public String getOriginalLanguage() {
            return mOriginalLanguage;
        }

        public String[] getForeignLanguages() {
            return mForeignLanguages;
        }
    }
}
