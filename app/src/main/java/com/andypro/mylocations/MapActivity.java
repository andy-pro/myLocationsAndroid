package com.andypro.mylocations;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.CameraPosition;

//public class MapActivity extends FragmentActivity implements
public class MapActivity extends AppCompatActivity implements
        MapFragment.MapCallbacks {

//    CameraPosition pos;

    /*
     http://startandroid.ru/ru/uroki/vse-uroki-spiskom/189-urok-115-odno-prilozhenie-na-raznyh-ekranah.html

    * Представим ситуацию. Мы поворачиваем планшет вертикально, у нас отобразятся только заголовки.
    * Мы нажимаем на какой-либо заголовок и переходим на DetailsActivity, которое покажет нам содержимое
    * (средствами DetailsFragment, разумеется). Т.е. мы имеем вертикальную ориентацию и видим содержимое.
    * Теперь поворачиваем планшет горизонтально. Что будет? DetailsActivity отобразится во весь
    * горизонтальный экран и покажет содержимое. Но наша концепция гласит, что в горизонтальной ориентации
    * приложение должно показывать и содержимое и заголовки, ширина экрана ведь позволяет это сделать.
    * А, значит, нам надо вернуться в MainActivity.
    *
    * Смотрим первый фрагмент кода. Приложение определяет, что ориентация горизонтальная и в этом случае
    * просто закрывает Activity. И т.к. это DetailsActivity у нас будет вызвано из MainActivity, то после
    * finish мы попадаем в MainActivity и видим то, что нужно – и заголовки, и содержимое. Причем MainActivity
    * хранит номер выбранного заголовка (независимо от ориентации) и содержимое отобразится то же самое,
    * что было в DetailsActivity.
    *
    * Смотрим второй фрагмент. Мы проверяем, что savedInstanceState == null – это означает, что
    * Activity создается первый раз, а не пересоздается после смены ориентации экрана. Далее мы создаем
    * фрагмент DetailsFragment, используя позицию из интента, и помещаем его в Activity.
    *
    * Почему создаем фрагмент только при создании Activity и при пересоздании - нет? Потому что
    * система сама умеет пересоздавать существующие фрагменты при поворотах экрана, сохраняя при
    * этом аргументы фрагмента. И нам совершенно незачем в данном случае пересоздавать фрагмент самим.
    *
    * Причем тут надо понимать, что система будет создавать фрагмент вовсе не через метод newInstance.
    * Она просто не знает такой метод. Система использует конструктор. И мы ничего не можем передать в
    * этот конструктор, чтобы повлиять на поведение или содержимое фрагмента. Именно в таких случаях
    * выручают аргументы. Система сохраняет аргументы фрагмента при его пересоздании. И при каждом
    * пересоздании наш фрагмент будет знать, какое содержимое он должен отобразить, т.к. использует
    * аргументы при создании экрана в методе onCreateView.
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE
                && isLarge()) {
            finish();
            return;
        }

        Log.d("myLogs", "MapActivity: create");

        setContentView(R.layout.activity_map);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Location location = intent.getParcelableExtra("location");
        setTitle(location.name);

        if (savedInstanceState == null) {

            Log.d("myLogs", "MapActivity: new instance of MapFragment");

            MapFragment map = MapFragment.newInstance(location);
            getSupportFragmentManager().beginTransaction()
//                    .add(android.R.id.content, map).commit();
                    .add(R.id.map_frame, map).commit();
        }
    }

    boolean isLarge() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onMapPositionIdle(CameraPosition pos) {
        /* class CameraPosition implements Parcelable */
        Log.d("myLogs", "MapActivity: onMapPositionIdle");
        Intent intent = new Intent();
        intent.putExtra("camera_position", pos);
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }

}