package example.gifmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import java.util.regex.Pattern;

public class ScreenFour extends AppCompatActivity {
    public static boolean signatureFlag1;    //Signifies that the user has entered its signature
    public static boolean signatureFlag2;
    static final int REQUEST_CODE = 1;
    private int overage_counter = 0;
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
        //teamName = (TextView) findViewById(R.id.textView_teamname);
        //teamName.setText("Konfigurera lag: " + team);
        mConfirm = (Button) findViewById(R.id.confirm_button);
        player_list = (ListView) findViewById(R.id.players_list);
        //player_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        //player_list.setSelector(R.color.colorPrimaryDark);
        player_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            //onclick adds players to temporary array
            @Override
            public void onItemClick(AdapterView<?>adapterView, View v, int pos, long id){

                String item = ((TextView)v).getText().toString();
                //player_list.setItemChecked(pos, true);
                //Toast.makeText(getBaseContext(), item + " tillagd i spelarlistan", Toast.LENGTH_SHORT).show();
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
            if(getCurrentPlayerAge(player) == 999){
                playerNames.add(player);
            }
            else if(getCurrentPlayerAge(player)<= DataHolder.getInstance().getTarget_age()){
                playerNames.add(player);
            }else if((getCurrentPlayerAge(player) > DataHolder.getInstance().getTarget_age()) && (overage_counter < 2)){
                playerNames.add(player);
                Toast.makeText(ScreenFour.this, "Antal spelare över åldersgräns: " + overage_counter, Toast.LENGTH_SHORT).show();
                overage_counter++;
                if(overage_counter == 2){
                    Toast.makeText(ScreenFour.this, player + " är över åldersgräns, lades ej till i spelarlistan pga överskridet maxantal", Toast.LENGTH_LONG).show();
                }
            }

        }else{
            for(int i = 0; i < playerNames.size(); i++){
                if(playerNames.get(i) == player){
                    if(getCurrentPlayerAge(player) > DataHolder.getInstance().getTarget_age()){
                        overage_counter--;
                        playerNames.remove(i);
                    }else{
                        playerNames.remove(i);
                    }
                }
            }
        }
    }

    public int getCurrentPlayerAge(String player){
        String lastTwo = null;
        int foo = 0;
        if (player != null && player.length() >= 4) {
            lastTwo = player.substring(player.length() - 4, player.length() - 2);
        }
        if(Pattern.matches("[a-zA-Z]+", lastTwo) == false){
            foo = Integer.parseInt(lastTwo);
        }else{
            foo = 999;
        }

        return foo;
    }
    //Opens SignField activity with result request
    //(this enables updating the current activity after SignField has finished
    public void openSignature(View view){
        Intent signIntent = new Intent(getApplicationContext(), SignField.class);
        Bundle b = new Bundle();
        b.putString("key", team); //Declares which team for next Intent
        signIntent.putExtras(b); //Puts team to the next Intent
        startActivityForResult(signIntent, REQUEST_CODE);
    }

    //adds selected players to team in DataHolder
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        checkSignatureFlag();
    }

    //Checks if SignatureFlag is true (meaning user has entered signature)
    public void checkSignatureFlag(){
        //Checks which signature has been done
        if ( (signatureFlag1 && (DataHolder.getInstance().getActiveTeam() == 1)) || (signatureFlag2 && (DataHolder.getInstance().getActiveTeam() == 2)) ) {
            mConfirm.setEnabled(true); //Enables "confirm"-button
            mConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_back_ready));
        } else {
            mConfirm.setEnabled(false); //Disables "confirm"-button
            //mConfirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_back_not_ready));
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
        //Adds all parced playernames into the listview on the screen
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            playerAdapter = new ArrayAdapter(ScreenFour.this, android.R.layout.simple_list_item_multiple_choice, adapterBuffer);
            //playerAdapter = new ArrayAdapter(ScreenFour.this, android.R.layout.simple_list_item_1, adapterBuffer);
            player_list.setAdapter(playerAdapter);

        }
    }
}
