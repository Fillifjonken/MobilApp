package example.gifmanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by FIlip on 2018-05-16.
 */

public class ScreenThree extends MainActivity{
    Button button;
    Button button2;
    ListView listView;
    String tempUrlGames;
    String tempUrlPlayers;
    ArrayAdapter adapter;
    ArrayList listTEXT;
    ArrayList listHTML;
    String teamOneUrl;
    String teamTwoUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_three);
        button = (Button)findViewById(R.id.button);
        button2 = (Button)findViewById(R.id.button2);
        listView = (ListView) findViewById(R.id.listView);
        listTEXT = new ArrayList<String>();
        listHTML = new ArrayList<String>();
        //compareList = new ArrayList<String>();
        tempUrlGames = "http://teamplaycup.se/cup/?games&home=kurirenspelen/17&scope=all&arena=A%2011-manna%20(Gstad)&field=";
        tempUrlPlayers = "http://teamplaycup.se/cup/?team&home=kurirenspelen/17&scope=A-2&name=Notvikens%20IK";
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?>adapterView, View v, int pos, long id){

                String item = ((TextView)v).getText().toString();
                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                //int index = listView.getSelectedItemPosition();
                splitTeams(item, pos);
            }
        });

        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Toast.makeText(getBaseContext(),teamOneUrl, Toast.LENGTH_LONG).show();
            }

        });
        button2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Toast.makeText(getBaseContext(),teamTwoUrl, Toast.LENGTH_LONG).show();
            }

        });
        new urlParcer().execute();
    }

    public void splitTeams(String item, int index){
        String tempHTML = listHTML.get(index).toString();
        String[] parts = tempHTML.split("<td>");
        String temp2 = parts[4];
        String[] parts2 = temp2.split("</a>");


        String[] teamOne = parts2[0].split(">");
        button.setText(teamOne[1]);
        String[] teamTwo = parts2[1].split(">");
        button2.setText(teamTwo[1]);

        String[] teamOneSplit = teamOne[0].split("\"");
        String[] teamTwoSplit = teamTwo[0].split("\"");

        teamOneUrl = "http://teamplaycup.se/cup/" + teamOneSplit[1];
        teamTwoUrl = "http://teamplaycup.se/cup/" + teamTwoSplit[1];






        //button.setText(parts2[1]);
    }



    public class urlParcer extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {


            try {
                Document doc = Jsoup.connect(tempUrlGames).maxBodySize(0).get();
                Elements ele = doc.select("div.content div.table-responsive table.table-condensed");

                for(Element element: ele){
                    Elements rows = element.children().select("tbody");
                    for(Element row: rows){
                        Elements names = row.children().select("tr");
                        for (Element name: names){
                            String tempHTML = name.outerHtml();
                            String temp = name.text();
                            listTEXT.add(temp);
                            listHTML.add(tempHTML);
                        }

                    }

                }
            }catch(Exception e){
                System.err.print(e);

            }
            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new ArrayAdapter(ScreenThree.this, android.R.layout.simple_list_item_1, listTEXT);
            listView.setAdapter(adapter);

        }
    }


}
