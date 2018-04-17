package pl.temomuko.autostoprace.ui.staticdata

import pl.temomuko.autostoprace.R

object PartnerDrawables {

    @JvmStatic val strategic = listOf(
        R.drawable.strategiczny_kaufland,
        R.drawable.strategiczny_auto24
    )

    @JvmStatic val gold = listOf(
        R.drawable.zloty_akvo,
        R.drawable.zloty_decathlon,
        R.drawable.zloty_hatchi,
        R.drawable.zloty_luba,
        R.drawable.zloty_mamut,
        R.drawable.zloty_sygnet,
        R.drawable.zloty_wachitgall
    )

    @JvmStatic val silver = listOf(
        R.drawable.srebrny_beskidzkie,
        R.drawable.srebrny_burgerking,
        R.drawable.srebrny_findyourbuddy,
        R.drawable.srebrny_kravmaga,
        R.drawable.srebrny_lovex,
        R.drawable.srebrny_narny,
        R.drawable.srebrny_profi,
        R.drawable.srebrny_snowz,
        R.drawable.srebrny_tacoslocos
    )

    @JvmStatic val media = listOf(
        R.drawable.mowia_o_nas_luz,
        R.drawable.mowia_o_nas_meloradio,
        R.drawable.mowia_o_nas_nationalgeographic,
        R.drawable.mowia_o_nas_radiogra
    )

    @JvmStatic val splashPartners = listOf(strategic, gold, silver).flatten()
}
