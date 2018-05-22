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
        listTEXT = new ArrayList<String>(); //Array for display names
        listHTML = new ArrayList<String>(); //array for fetching HTML href
        //compareList = new ArrayList<String>();
        tempUrlGames = "http://teamplaycup.se/cup/?games&home=kurirenspelen/17&scope=all&arena=A%2011-manna%20(Gstad)&field=";
        tempUrlPlayers = "http://teamplaycup.se/cup/?team&home=kurirenspelen/17&scope=A-2&name=Notvikens%20IK";
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?>adapterView, View v, int pos, long id){

                String item = ((TextView)v).getText().toString();
                //Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                //int index = listView.getSelectedItemPosition();
                splitTeams(item, pos);
            }
        });


        new urlParcer().execute();
    }

    public void openScreenFourTeam1(View view){
        Intent intent = new Intent(getApplicationContext(), ScreenFour.class);
        Bundle b = new Bundle(); //Creates new bundle for intent
        b.putString("key", "HomeTeam"); //puts String into bundle with ID
        intent.putExtras(b);    //Puts the bundle as extra content for the Intent
        DataHolder.getInstance().setActiveTeam(1);
        startActivity(intent);
    }
    public void openScreenFourTeam2(View view){
        Intent intent = new Intent(getApplicationContext(), ScreenFour.class);
        Bundle b = new Bundle();    //Creates new bundle for intent
        b.putString("key", "VisitTeam");    //puts String into bundle with ID
        intent.putExtras(b);    //Puts the bundle as extra content for the Intent
        DataHolder.getInstance().setActiveTeam(2);
        startActivity(intent);
    }

    //function for dividing fetched string.
    public void splitTeams(String item, int index){
        String tempHTML = listHTML.get(index).toString();
        String[] parts = tempHTML.split("<td>");
        String temp2 = parts[4];
        String test = parts[2];
        //Split Time
        String[] timeSplit = parts[3].split("<");
        String time = timeSplit[0];
        //Split group number
        String[] groupSplit = parts[2].split(">");
        String[] groupSplit2 = groupSplit[1].split("<");
        String group = groupSplit2[0];
        DataHolder.getInstance().setGroupCode(group);
        // Split match Number
        String[] nrSplit = parts[1].split(">");
        String[] nrSplit2 = nrSplit[1].split("<");
        String nr = nrSplit2[0];
        DataHolder.getInstance().setNr(nr);
        //Split team names and URL
        String[] parts2 = temp2.split("</a>");
        String[] teamOne = parts2[0].split(">");
        button.setText(teamOne[1]); //Set button text to teamname of hometeam
        DataHolder.getInstance().setTeam1Name(teamOne[1]);
        String[] teamTwo = parts2[1].split(">");
        button2.setText(teamTwo[1]); //set button text to teamname of awayteam
        DataHolder.getInstance().setTeam2Name(teamTwo[1]);

        String[] teamOneSplit = teamOne[0].split("\"");
        String[] teamTwoSplit = teamTwo[0].split("\"");

        teamOneUrl = "http://teamplaycup.se/cup/" + teamOneSplit[1];
        teamOneUrl = teamOneUrl.replace("amp;", "");
        DataHolder.getInstance().setTeam1Url(teamOneUrl); //set url for parcing player names
        teamTwoUrl = "http://teamplaycup.se/cup/" + teamTwoSplit[1];
        teamTwoUrl = teamTwoUrl.replace("amp;", "");
        DataHolder.getInstance().setTeam2Url(teamTwoUrl); //set url for parcing player names

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
