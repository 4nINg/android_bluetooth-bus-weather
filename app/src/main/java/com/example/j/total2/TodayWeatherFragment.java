package com.example.j.total2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.j.total2.Common.Common;
import com.example.j.total2.Retrofit.IOpenWeatherMap;
import com.example.j.total2.Retrofit.RetrofitClient;
import com.example.j.total2.model.WeatherResult;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment {

    ImageView imgweather;
    TextView cityname,humidity,sunrise,sunset,pressure,temperature,description,datetime,wind,geocoord;
    LinearLayout weatherpanel;
    ProgressBar loading;

    static TodayWeatherFragment instance;
    private IOpenWeatherMap mService;
    CompositeDisposable compositeDisposable;

    public static TodayWeatherFragment getInstance(){
        if(instance ==null)
            instance = new TodayWeatherFragment();
        return instance;
    }


    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_today_weather, container, false);



        imgweather = (ImageView)itemView.findViewById(R.id.imgweather);
        cityname = (TextView)itemView.findViewById(R.id.cityname);
        humidity = (TextView)itemView.findViewById(R.id.humidity);
        /*sunrise = (TextView)itemView.findViewById(R.id.sunrise);
        sunset = (TextView)itemView.findViewById(R.id.sunset);
        pressure = (TextView)itemView.findViewById(R.id.pressure);*/
        temperature = (TextView)itemView.findViewById(R.id.temperature);
        description = (TextView)itemView.findViewById(R.id.description);
        datetime = (TextView)itemView.findViewById(R.id.datetime);
        wind = (TextView)itemView.findViewById(R.id.wind);
        /*geocoord = (TextView)itemView.findViewById(R.id.geocoord);*/

        weatherpanel = (LinearLayout)itemView.findViewById(R.id.weatherpanel);
        loading = (ProgressBar)itemView.findViewById(R.id.loading);

        getWeatherInformation();

        return itemView;
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<WeatherResult>() {
                               @Override
                               public void accept(WeatherResult weatherResult) throws Exception {
                                   Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                           .append(weatherResult.getWeather().get(0).getIcon())
                                           .append(".png").toString()).into(imgweather);
                                   cityname.setText(weatherResult.getName());
                                   description.setText(new StringBuilder("Weather in ")
                                           .append(weatherResult.getName()).toString());
                                   temperature.setText(new StringBuilder(
                                           String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
                                   datetime.setText(Common.convertUnixToDate(weatherResult.getDt()));
                                   /*pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());*/
                                   humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append(" %").toString());
                                   /*sunrise.setText(Common.converUnixToHour(weatherResult.getSys().getSunrise()));
                                   sunset.setText(Common.converUnixToHour(weatherResult.getSys().getSunset()));*/
                                   /*geocoord.setText(new StringBuilder(weatherResult.getCoord().toString()).toString());*/
                                   wind.setText(new StringBuilder(String.valueOf(weatherResult.getWind().getSpeed())).append(" m/s"));
                                   /*wind.setText(new StringBuilder("speed : ").append(weatherResult.getWind().getSpeed())
                                           .append(" deg : ").append(weatherResult.getWind().getDeg()));*/

                                   weatherpanel.setVisibility(View.VISIBLE);
                                   loading.setVisibility(View.GONE);
                               }
                           }, new io.reactivex.functions.Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(getActivity(),""+throwable.getMessage(), Toast.LENGTH_LONG).show();
                               }
                           })
        );

    }

}
