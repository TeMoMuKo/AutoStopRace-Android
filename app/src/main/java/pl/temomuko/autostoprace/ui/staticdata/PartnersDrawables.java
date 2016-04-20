package pl.temomuko.autostoprace.ui.staticdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.temomuko.autostoprace.R;

/**
 * Created by Szymon Kozak on 2016-04-18.
 */
public class PartnersDrawables {

    public final static Integer[] DRAWABLES_ID_ARRAY = {
            R.drawable.logo_beactive,
            R.drawable.logo_cafe_borowka,
            R.drawable.logo_grzeski,
            R.drawable.logo_kf,
            R.drawable.logo_kravmaga,
            R.drawable.logo_lerni,
            R.drawable.logo_linuxpl,
            R.drawable.logo_mdt,
            R.drawable.logo_partybusy,
            R.drawable.logo_pot,
            R.drawable.logo_profi,
            R.drawable.logo_profi_lingua,
            R.drawable.logo_smscenter,
            R.drawable.logo_st,
            R.drawable.logo_sueno,
            R.drawable.logo_sygnet,
            R.drawable.logo_szczypta_swiata,
            R.drawable.logo_tarczynski,
            R.drawable.logo_tchibo,
            R.drawable.logo_ttwarsaw,
            R.drawable.logo_unicar_wroclaw,
            R.drawable.logo_unilever,
            R.drawable.logo_xiaoyi,
            R.drawable.logo_zona
    };

    public static Integer[] getAsArray() {
        return DRAWABLES_ID_ARRAY;
    }

    public static List<Integer> getAsList() {
        return new ArrayList<>(Arrays.asList(DRAWABLES_ID_ARRAY));
    }
}
