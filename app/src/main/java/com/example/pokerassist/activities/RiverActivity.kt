package com.example.pokerassist.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pokerassist.ActivityEnum
import com.example.pokerassist.CardModel
import com.example.pokerassist.R
import com.example.pokerassist.SuitEnum
import kotlinx.android.synthetic.main.activity_river.*
import java.util.*
import kotlin.collections.ArrayList

class RiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_river)

        initPlayers()

        visibleCards = ArrayList()
        initCards()

//        //debug
//        for (card in visibleCards) {
//            Toast.makeText(this, card.toString(), Toast.LENGTH_SHORT).show()
//        }

        //initView()

        foldButton2.setOnClickListener { foldSubmit() }
        proceedButton2.setOnClickListener { proceedSubmit() }
    }

    private fun initPlayers() {
        val sharedPref = getSharedPreferences(getString(R.string.settings_file_key), Context.MODE_PRIVATE)
        players = sharedPref.getInt(resources.getString(R.string.players), SettingsActivity.defaultPlayers)
    }

    private fun initCards() {
        visibleCards.addAll(intent.getParcelableArrayListExtra(resources.getString(R.string.drawn_cards_tag)) ?: ArrayList())
        visibleCards.addAll(intent.getParcelableArrayListExtra(resources.getString(R.string.selected_cards_tag)) ?: ArrayList())
        visibleCards.sort()
    }

    private fun foldSubmit() {
        var cards = java.util.ArrayList<java.util.ArrayList<CardModel>>().apply {
            for (suit in SuitEnum.values()) {
                add(java.util.ArrayList<CardModel>().apply {
                    for (i in 1..SplashActivity.POKER_NUMBER)
                        add(CardModel(i, suit, false))
                })
            }
        }
        startActivity(Intent(this, ActivityEnum.SELECT.activityClass).apply {
            putExtra(resources.getString(R.string.title_tag), resources.getString(R.string.select_drawn_cards))
            putExtra(resources.getString(R.string.next_act_tag), ActivityEnum.PREFLOP)
            putExtra(resources.getString(R.string.num_sel_tag), 2)
            for (cardList in cards) {
                if (cardList.size > 0)
                    putParcelableArrayListExtra(cardList[0].suit.suit, cardList)
            }
        })
        finish()
    }

    private fun proceedSubmit() {

    }

    private fun royalFlushProb() : Double {
        return 0.0
    }

    private fun straightFlushProb() : Double {
        return 0.0
    }

    private fun fourKindProb() : Double {
        return 0.0
    }

    private fun fullHouseProb() : Double {
        return 0.0
    }

    private fun flushProb() : Double {
        return 0.0
    }

    private fun straightProb() : Double {
        return 0.0
    }

    private fun threeKindProb() : Double {
        return 0.0
    }

    private fun twoPairProb() : Double {
        return 0.0
    }

    private fun onePairProb() : Double {
        return 0.0
    }

    private fun highCardProb() : Double {
        return 0.0
    }

    private var players = SettingsActivity.defaultPlayers
    private lateinit var visibleCards: ArrayList<CardModel>
}
