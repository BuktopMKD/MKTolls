package com.denofdevelopers.mktolls.screen.screen.splash;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.denofdevelopers.mktolls.R;
import com.denofdevelopers.mktolls.application.App;
import com.denofdevelopers.mktolls.di.NamedValues;
import com.denofdevelopers.mktolls.model.Toll;
import com.denofdevelopers.mktolls.screen.screen.main.MainActivity;
import com.f2prateek.rx.preferences2.Preference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;

public class SplashScreenActivity extends AppCompatActivity {

    @Inject
    @Named(NamedValues.tolls)
    Preference<String> tolls;
    @Inject
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        setupActivityComponent();
        initTolls();
        MainActivity.start(this);
        finish();
    }

    protected void setupActivityComponent() {
        App.get(this).getAppComponent().plus(this);
    }

    private void initTolls() {
//        if (TextUtils.isEmpty(tolls.get())) {

        List<Toll> tollList = new ArrayList<>();
        tollList.add(new Toll(41.988478, 21.292620, "Glumovo", "Глумово"
                , 20.00, 40.00, 60.00, 110.00, 160.00
                , 0.50, 1.00, 2.00, 3.00, 4.50));
        tollList.add(new Toll(41.819905, 20.917580, "Gostivar", "Гостивар"
                , 20.00, 30.00, 40.00, 80.00, 110.00
                , 0.50, 0.50, 1.00, 1.50, 2.00));
        tollList.add(new Toll(41.593404, 21.913106, "Stobi", "Стоби"
                , 40.00, 60.00, 100.00, 180.00, 260.00
                , 0.50, 1.00, 2.00, 3.00, 4.50));
        tollList.add(new Toll(41.983383, 21.641460, "Miladinovci", "Миладиновци"
                , 20.00, 40.00, 50.00, 100.00, 150.00
                , 0.50, 1.00, 1.00, 2.00, 2.50));
        tollList.add(new Toll(41.775878, 21.745353, "Sopot", "Сопот"
                , 50.00, 80.00, 120.00, 220.00, 330.00
                , 1.00, 1.50, 2.00, 4.00, 5.50));
        tollList.add(new Toll(41.778480, 21.767534, "Otovica", "Отовица"
                , 50.00, 80.00, 120.00, 220.00, 330.00
                , 1.00, 1.50, 2.00, 4.00, 5.50));
        tollList.add(new Toll(41.942027, 21.619643, "Petrovec", "Петровец"
                , 20.00, 40.00, 50.00, 100.00, 150.00
                , 0.50, 1.00, 1.00, 2.00, 3.00));
        tollList.add(new Toll(42.103704, 21.700195, "Romanovce", "Романовце"
                , 40.00, 60.00, 80.00, 150.00, 220.00
                , 0.50, 1.00, 1.50, 2.50, 4.00));
        tollList.add(new Toll(41.976341, 20.956737, "Tetovo", "Тетово"
                , 20.00, 30.00, 40.00, 80.00, 110.00
                , 0.50, 0.50, 1.00, 1.50, 2.00));
        tollList.add(new Toll(41.989927, 21.077206, "Zelino", "Желино"
                , 20.00, 40.00, 60.00, 110.00, 160.00
                , 0.50, 1.00, 1.00, 2.00, 3.00));

        // Патарина Демир Капија
        // Демир Капија = Градско - Демир Капија и обратно		80 130 230 340 ден + 1.50 2.50 4.00 6.00 еур
        // Темп Локација: 41.411137, 22.247452
        // Локација: 41.417236, 22.208121
        tollList.add(new Toll(41.417236, 22.208121, "Demir Kapija"
                , "Демир Капија", 50.00, 80.00, 130.00, 230.00, 340.00
                , 1.00, 1.50, 2.50, 4.00, 6.00));

        // Патарина Гевгелија
        // Гевгелија =	Демир Капија - Гевгелија и обратно		100	160	290	430 ден + 2.00 3.00 5.00 7.00 еур
        // Темп Локација: 41.147486, 22.526037
        // Локација: 41.235711, 22.492559
        tollList.add(new Toll(41.235711, 22.492559, "Gevgelija", "Гевгелија"
                , 60.00, 100.00, 160.00, 290.00, 430.00
                , 1.00, 2.00, 3.00, 5.00, 7.00));

        /* --- Миладиновци - Штип --- */

        // Патарина Кадрифаково - Сеуште не е изградена
        // Штип =	Штип - Св.Николе и обратно		50 80 150 220 ден + 1.00 1.50 2.50 4.00 еур
        // Темп Локација: 41.810914, 22.045404
        // tollList.add(new Toll(41.810914, 22.045404, "Kadrifakovo", "Кадрифаково"
        // , 30.00, 50.00, 80.00, 150.00, 220.00
        // , 0.50, 1.00, 1.50, 2.50, 4.00));

        // Патарина Порој - Сеуште не е изградена
        // Св.Николе =	Св.Николе - Миладиновци и обратно		70 100 180 270 ден + 1.50 2.00 3.00 4.50 еур
        // Темп Локација: 41.923960, 21.843075
        // tollList.add(new Toll(41.923960, 21.843075, "Preod", "Преод"
        // , 40.00, 70.00, 100.00, 180.00, 270.00
        // , 0.50, 1.50, 2.00, 3.00, 4.50));

        /* Да се провери точноста на податоците */
        // Клучка Миравци
        //tollList.add(new Toll(41.298470, 22.430885, "Miravci - Demir Kapija"
        // , "Миравци - Демир Капија", 30.00, 60.00, 90.00, 160.00, 240.00
        // , 0.50, 1.00, 1.50, 2.50, 4.00));
        //tollList.add(new Toll(41.295548, 22.431566, "Miravci - Gevgelija", "Миравци - Гевгелија"
        // , 30.00, 50.00, 70.00, 130.00, 190.00
        // , 0.50, 1.00, 1.50, 2.00, 3.00));

        // Клучка Смоквица
        //tollList.add(new Toll(41.263354, 22.471630, "Smokvica - Demir Kapija", "Смоквица - Демир Капија"
        // , 40.00, 70.00, 110.00, 200.00, 290.00
        // , 0.50, 1.50, 2.00, 3.50, 5.00));
        //tollList.add(new Toll(41.263440, 22.469588, "Smokvica - Gevgelija", "Смоквица - Гевгелија"
        // , 20.00, 30.00, 50.00, 90.00, 140.00
        // , 0.50, 0.50, 1.00, 1.50, 2.50));

        tolls.set(gson.toJson(tollList));
//        }
    }
}
