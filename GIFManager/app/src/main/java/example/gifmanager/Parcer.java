package example.gifmanager;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;



public class Parcer extends MainActivity {
    public String url;
    public ArrayList<String> list;
    public ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String hej = "HELLO DIN JÄVLA VÄRLD";
    }

    public void getParce(String url) {
        this.url = url;
        try {
            new urlParcer().execute();
        }catch (Error e){
            System.err.print(e);
        }
    }



    public class urlParcer extends AsyncTask<Void, Void, Void>{


        protected Void doInBackground(Void... params) {


                try {
                    Document doc = Jsoup.connect(url).maxBodySize(0).get();
                    Elements ele = doc.select("div.content div.table-responsive table.table-condensed");

                    for(Element element: ele){
                        Elements rows = element.children().select("tbody");
                        for(Element row: rows){
                            Elements names = row.children().select("tr");
                            for (Element name: names){
                                String temp = name.text();
                                list.add(temp);
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
                adapter = new ArrayAdapter(Parcer.this, android.R.layout.simple_list_item_1, list);


        }
    }
}