package pl.temomuko.autostoprace.ui.staticdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pl.temomuko.autostoprace.R;

/**
 * Created by Szymon Kozak on 2016-04-18.
 */
public class PartnersDrawables {

    public static final List<Integer> STRATEGIC = Collections.unmodifiableList(Arrays.asList(
            R.drawable.strategic_logo_kf,
            R.drawable.strategic_logo_wroclaw
    ));

    public static final List<Integer> GOLD = Collections.unmodifiableList(Arrays.asList(
            R.drawable.gold_logo_tarczynski,
            R.drawable.gold_logo_grzeski,
            R.drawable.gold_logo_profi_lingua,
            R.drawable.gold_logo_akvo_active,
            R.drawable.gold_logo_capoeira,
            R.drawable.gold_logo_wachtigall,
            R.drawable.gold_logo_cafe_borowka
    ));

    public static final List<Integer> SILVER = Collections.unmodifiableList(Arrays.asList(
            R.drawable.silver_logo_profi,
            R.drawable.silver_logo_71zona,
            R.drawable.silver_logo_meray,
            R.drawable.silver_logo_hydropolis,
            R.drawable.silver_logo_krav_maga,
            R.drawable.silver_logo_lirene,
            R.drawable.silver_logo_motyla_noga,
            R.drawable.silver_logo_beactive,
            R.drawable.silver_logo_sueno
    ));

    public static List<Integer> getAll() {
        ArrayList<Integer> allPartners = new ArrayList<>();
        allPartners.addAll(STRATEGIC);
        allPartners.addAll(GOLD);
        allPartners.addAll(SILVER);
        return allPartners;
    }
}
