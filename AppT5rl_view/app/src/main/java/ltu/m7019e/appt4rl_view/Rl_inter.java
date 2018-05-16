package ltu.m7019e.appt4rl_view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Rl_inter extends Activity {

	private TextView State;
	private EditText EditState;
	private Button Submitbtn;
	private ToggleButton toggle;	
	private boolean HOLD=false;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rl_inter);
		State= (TextView)findViewById(R.id.txtState);
		EditState= (EditText)findViewById(R.id.edtState);
		Submitbtn = (Button) findViewById(R.id.btnSbm);
		toggle = (ToggleButton) findViewById(R.id.tglbtn);	
				
		Submitbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			String text;
			text=EditState.getText().toString();
			if(!HOLD){
			// Case when ToggleButton is off	
				State.setText(text);
				EditState.setText("");				
			}			
			else{
			// Case when ToggleButton is clicked
			}
				
			}
		});
		
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            // The toggle is enabled
		        	HOLD=true;
		        	
		        } else {
		            // The toggle is disabled
		        	HOLD=false;
		        }
		    }
		});
	}


}
