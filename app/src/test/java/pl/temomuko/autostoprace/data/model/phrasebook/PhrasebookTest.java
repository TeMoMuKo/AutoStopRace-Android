package pl.temomuko.autostoprace.data.model.phrasebook;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import pl.temomuko.autostoprace.data.model.Phrasebook;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by Szymon Kozak on 2016-04-10.
 */
@RunWith(JUnit4.class)
public class PhrasebookTest {

    private static final int LANGUAGE_ROW_POSITION = 0;
    private static final int ORIGINAL_LANG_COLUMN_POSITION = 0;

    @Test
    public void testCreateFromCsvRows() throws Exception {
        //given
        List<String[]> csvRows = new ArrayList<>();
        String[] languagesContent = new String[]{"polski", "angielski", "niemiecki", "włoski"};
        String[] translationsContent = new String[]{"Cześć", "Hi", "hallo", "Ciao"};
        csvRows.add(languagesContent);
        csvRows.add(translationsContent);

        //when
        Phrasebook phrasebook = Phrasebook.createFromCsvRows(
                csvRows,
                LANGUAGE_ROW_POSITION,
                ORIGINAL_LANG_COLUMN_POSITION
        );

        //then
        String expectedOriginalLanguage = "polski";
        String actualOriginalLanguage = phrasebook.getLanguagesHeader().getOriginalLanguage();
        assertEquals(expectedOriginalLanguage, actualOriginalLanguage);

        String[] expectedForeignLanguages = new String[]{"angielski", "niemiecki", "włoski"};
        String[] actualForeignLanguages = phrasebook.getLanguagesHeader().getForeignLanguages();
        assertArrayEquals(expectedForeignLanguages, actualForeignLanguages);

        String expectedOriginalPhrase = "Cześć";
        String actualOriginalPhrase = phrasebook.getPhraseItems().get(0).getOriginalPhrase();
        assertEquals(expectedOriginalPhrase, actualOriginalPhrase);

        String[] expectedTranslations = new String[]{"Hi", "hallo", "Ciao"};
        String[] actualTranslations = phrasebook.getPhraseItems().get(0).getTranslations();
        assertArrayEquals(expectedTranslations, actualTranslations);
    }
}