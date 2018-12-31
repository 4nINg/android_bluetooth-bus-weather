package com.example.j.total2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class BusActivity extends AppCompatActivity {
    TextView textView;
    String data;
    AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_main);

        textView = (TextView)findViewById(R.id.textView);
        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.autocompleteId);

        String[] buslist = {"경북대학교정문앞", "경북대학교정문건너", "경북대학교북문앞", "경북대학교북문건너"};

        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,buslist));

    }

    public void buttonClicked(View v){
        switch (v.getId()){
            case R.id. button:
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                data = getData();
                                runOnUiThread(new Runnable() {
                                        @Override
                                    public void run() {
                                        textView.setText(data);
                                    }
                                });
                            }
                        }).start();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask,0,30000);
                break;
        }
    }

    String getData(){
        StringBuffer buffer = new StringBuffer();
        /*String str = editText.getText().toString();
        String location = URLEncoder.encode(str);*/
        String str = autoCompleteTextView.getText().toString();
        String NODE_ID = "";
        if(str.equals("경북대학교북문앞")){
            NODE_ID = "DGB7021025800";
        } else if(str.equals("경북대학교정문앞")){
            NODE_ID = "DGB7011010400";
        } else if(str.equals("경북대학교북문건너")){
            NODE_ID = "DGB7021025900";
        } else if(str.equals("경북대학교정문건너")){
            NODE_ID = "DGB7011010300";
        }

        String queryURL = "http://openapi.tago.go.kr/openapi/service/ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList?"
                + "serviceKey=" // serviceKey request
                + "&cityCode=22&nodeId="+NODE_ID ;

        try{
            URL url = new URL(queryURL);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("검색 시작 \n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if(tag.equals("item"));
                        else if(tag.equals("arrprevstationcnt")){
                            buffer.append("도착까지 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append(" 정거장\n");
                        }
                        else if (tag.equals("arrtime")){
                            buffer.append("도착까지 : ");
                            xpp.next();
                            String time = xpp.getText();
                            int cho = Integer.parseInt(time);
                            buffer.append(cho/60);
                            buffer.append("분\n");
                        }

                        else if(tag.equals("nodenm")){
                            buffer.append("정류장 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                        else if(tag.equals("routeno")){
                            buffer.append("버스 번호 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }

                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if(tag.equals("item")){
                            buffer.append("\n");
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        buffer.append("30초마다 검색을 반복합니다.\n");
        return buffer.toString();
    }
}

