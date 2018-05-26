package example.gifmanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
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

public class ScreenFour extends AppCompatActivity {
    public static boolean signatureFlag;    //Signifies that the user has entered its signature
    static final int REQUEST_CODE = 1;
    String team = "Lag"; // or other values
    TextView teamName;
    Button mConfirm;
    private ArrayList<String> adapterBuffer; //Array for adding to the list view adapter
    private ArrayList<String> playerNames; //temporary array for storing player names
    private ArrayAdapter playerAdapter; //adapter for the list view
    ListView player_list;
    private String urlPlayers; //url for fetching url to players


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapterBuffer = new ArrayList<String>();
        playerNames = new ArrayList<String>();
        Bundle b = getIntent().getExtras();
        if(b != null)
            this.team = b.getString("key"); //Fetches the team from Intent
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_screen_four);
        teamName = (TextView) findViewById(R.id.textView_teamname);
        teamName.setText("Konfigurera lag: " + team);
        mConfirm = (Button) findViewById(R.id.confirm_button);
        player_list = (ListView) findViewById(R.id.players_list);
        player_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            //onclick adds players to temporary array
            @Override
            public void onItemClick(AdapterView<?>adapterView, View v, int pos, long id){

                String item = ((TextView)v).getText().toString();
                Toast.makeText(getBaseContext(), item + " tillagd i spelarlistan", Toast.LENGTH_SHORT).show();
                addPlayer(item);
            }
        });
        fetchUrl(); //fetches parcing url
        checkSignatureFlag(); //checks Signature flag
        new playerParcer().execute(); //start parcer
    }

    //Function for fetching the correct url for parcing
    public void fetchUrl(){
        if(DataHolder.getInstance().getActiveTeam() == 1){
            urlPlayers = DataHolder.getInstance().getTeam1Url();
        }if(DataHolder.getInstance().getActiveTeam() == 2){
            urlPlayers = DataHolder.getInstance().getTeam2Url();
        }
    }

    //add player name to temporary array
    public void addPlayer(String player){
        if(!playerNames.contains(player)){
            playerNames.add(player);
        }
    }

    //Opens SignField activity with result request
    //(this enables updating the current activity after SignField has finished
    public void openSignature(View view){
        Intent intent = new Intent(getApplicationContext(), SignField.class);
        Bundle b = new Bundle();
        b.putString("key", team); //Declares which team for next Intent
        intent.putExtras(b); //Puts team to the next Intent
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void confirmTeam(View view){
        if(DataHolder.getInstance().getActiveTeam() == 1){
            DataHolder.getInstance().setTeam1Members(playerNames);
        }
        if(DataHolder.getInstance().getActiveTeam()==2){
            DataHolder.getInstance().setTeam2Members(playerNames);
        }

        finish();
    }

    @Override
    //On return from other activity (SignField), button availability is updated
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        checkSignatureFlag();
    }

    //Checks if SignatureFlag is true (meaning user has entered signature)
    public void checkSignatureFlag(){
        if (signatureFlag) {
            mConfirm.setEnabled(true); //Enables "confirm"-button
        } else {
            mConfirm.setEnabled(false); //Disables "confirm"-button
        }
    }

    //Parce information from teamplaycup.se
    public class playerParcer extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            try {
                Document doc = Jsoup.connect(urlPlayers).maxBodySize(0).get();
                Elements ele = doc.select("div.content div.table-responsive table.table-condensed");

                for(Element element: ele){
                    Elements rows = element.children().select("tbody");
                    for(Element row: rows){
                        Elements names = row.children().select("tr");
                        for (Element name: names){
                            String temp = name.text();
                            adapterBuffer.add(temp);
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
            playerAdapter = new ArrayAdapter(ScreenFour.this, android.R.layout.simple_list_item_1, adapterBuffer);
            player_list.setAdapter(playerAdapter);

        }
    }
}
