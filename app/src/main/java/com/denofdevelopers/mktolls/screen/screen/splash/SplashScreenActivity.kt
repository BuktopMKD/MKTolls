package com.denofdevelopers.mktolls.screen.screen.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.denofdevelopers.mktolls.R
import com.denofdevelopers.mktolls.application.App
import com.denofdevelopers.mktolls.di.NamedValues
import com.denofdevelopers.mktolls.model.Toll
import com.denofdevelopers.mktolls.screen.screen.main.MainActivity
import com.f2prateek.rx.preferences2.Preference
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Named

class SplashScreenActivity : AppCompatActivity() {

    @Inject
    @field:Named(NamedValues.tolls)
    lateinit var tolls: Preference<String>

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        setupActivityComponent()
        initTolls()
        MainActivity.start(this)
        finish()
    }

    private fun setupActivityComponent() {
        App.get(this).appComponent.plus(this)
    }

    private fun initTolls() {
        val tollList = mutableListOf<Toll>()
        tollList.add(Toll(41.988478, 21.292620, "Glumovo", "Глумово", 20.00, 40.00, 60.00, 110.00, 160.00, 0.50, 1.00, 1.50, 2.50, 4.00))
        tollList.add(Toll(41.819905, 20.917580, "Gostivar", "Гостивар", 20.00, 30.00, 40.00, 80.00, 110.00, 0.50, 0.50, 1.00, 1.50, 2.00))
        tollList.add(Toll(41.593404, 21.913106, "Stobi", "Стоби", 40.00, 60.00, 100.00, 180.00, 260.00, 1.00, 1.50, 2.00, 4.00, 6.00))
        tollList.add(Toll(41.983383, 21.641460, "Miladinovci", "Миладиновци", 20.00, 40.00, 60.00, 100.00, 150.00, 0.50, 1.00, 1.50, 2.50, 4.00))
        tollList.add(Toll(41.775878, 21.745353, "Sopot", "Сопот", 50.00, 80.00, 120.00, 220.00, 330.00, 1.00, 1.50, 2.00, 4.00, 5.50))
        tollList.add(Toll(41.778480, 21.767534, "Otovica", "Отовица", 50.00, 80.00, 120.00, 220.00, 330.00, 1.00, 1.50, 2.00, 4.00, 5.50))
        tollList.add(Toll(41.942027, 21.619643, "Petrovec", "Петровец", 20.00, 40.00, 50.00, 100.00, 150.00, 0.50, 1.00, 1.00, 2.00, 3.00))
        tollList.add(Toll(42.103704, 21.700195, "Romanovce", "Романовце", 40.00, 60.00, 80.00, 150.00, 220.00, 0.50, 1.00, 1.50, 2.50, 4.00))
        tollList.add(Toll(41.976341, 20.956737, "Tetovo", "Тетово", 20.00, 30.00, 40.00, 80.00, 110.00, 0.50, 0.50, 1.00, 1.50, 2.00))
        tollList.add(Toll(41.989927, 21.077206, "Zelino", "Желино", 20.00, 40.00, 60.00, 110.00, 160.00, 0.50, 1.00, 1.50, 2.50, 4.00))
        tollList.add(Toll(41.417236, 22.208121, "Demir Kapija", "Демир Капија", 50.00, 80.00, 130.00, 230.00, 340.00, 1.00, 1.50, 2.50, 4.00, 6.00))
        tollList.add(Toll(41.235711, 22.492559, "Gevgelija", "Гевгелија", 60.00, 100.00, 160.00, 290.00, 430.00, 1.50, 2.00, 3.00, 5.00, 7.50))
        tollList.add(Toll(41.905600, 21.782800, "Preod", "Преод", 40.00, 70.00, 100.00, 180.00, 270.00, 1.00, 1.50, 2.00, 3.00, 4.50))
        tollList.add(Toll(41.810100, 22.039800, "Kadrifakovo", "Кадрифаково", 30.00, 50.00, 80.00, 150.00, 220.00, 0.50, 1.00, 1.50, 2.50, 4.00))

        // Greek Tolls
        tollList.add(Toll(41.108700, 22.558600, "Evzoni", "Евзони", 101.50, 147.60, 147.60, 369.00, 516.60, 1.65, 2.40, 2.40, 6.00, 8.40))
        tollList.add(Toll(40.602400, 22.698200, "Malgara", "Малгара", 40.00, 55.30, 55.30, 144.50, 203.00, 0.65, 0.90, 0.90, 2.35, 3.30))
        tollList.add(Toll(40.573000, 22.613000, "Kleidi", "Клеиди", 80.00, 110.70, 110.70, 282.90, 393.60, 1.30, 1.80, 1.80, 4.60, 6.40))
        tollList.add(Toll(40.035700, 22.569800, "Leptokarya", "Лептокарија", 116.80, 166.00, 166.00, 418.20, 584.20, 1.90, 2.70, 2.70, 6.80, 9.50))
        tollList.add(Toll(40.367200, 22.060200, "Polymylos", "Полимилос", 83.00, 116.80, 116.80, 295.20, 412.00, 1.35, 1.90, 1.90, 4.80, 6.70))
        tollList.add(Toll(40.696140, 22.895804, "Thessaloniki Ring Road", "Солун (обиколница)", 24.60, 33.80, 33.80, 86.10, 116.80, 0.40, 0.55, 0.55, 1.40, 1.90))
        tollList.add(Toll(40.256544, 21.632969, "Siatista", "Сиатиста", 83.00, 116.80, 116.80, 295.20, 412.00, 1.35, 1.90, 1.90, 4.80, 6.70))
        tollList.add(Toll(39.789000, 21.266000, "Malakasi", "Малакаси (Мецово)", 83.00, 116.80, 116.80, 295.20, 412.00, 1.35, 1.90, 1.90, 4.80, 6.70))
        tollList.add(Toll(39.619000, 20.948000, "Pamvotida", "Памотида (Јанина)", 67.60, 95.30, 95.30, 246.00, 338.20, 1.10, 1.55, 1.55, 4.00, 5.50))
        tollList.add(Toll(39.540000, 20.674000, "Tyria", "Тириа (Игуменица)", 116.80, 166.00, 166.00, 418.20, 584.20, 1.90, 2.70, 2.70, 6.80, 9.50))
        tollList.add(Toll(39.425535, 20.905251, "Terovo", "Терово", 135.30, 193.70, 193.70, 482.70, 676.50, 2.20, 3.15, 3.15, 7.85, 11.00))
        tollList.add(Toll(38.940119, 20.766185, "Aktio-Preveza Tunnel", "Тунел Актио-Превеза", 43.00, 184.50, 184.50, 307.50, 492.00, 0.70, 3.00, 3.00, 5.00, 8.00))

        tolls.set(gson.toJson(tollList))
    }
}
