package com.anmolahuja.gsfhack;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

public class VoiceRegisteration extends Activity implements
TextToSpeech.OnInitListener {
	
	private TextToSpeech tts;
	private String []questions;
	private String []question_tags;
	private TextView voiceData;
	private int i=-1;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_voice );
	
		tts = new TextToSpeech(this, this);
		
		voiceData = (TextView)findViewById(R.id.voice_info);
		questions = getResources().getStringArray(R.array.profile_questions);
		question_tags = getResources().getStringArray(R.array.profile_question_ids);
		
	}
	
	protected void loop(){
		i++;
		voiceData.setText(questions[i]);
		speakOut(questions[i]);
		getInput(questions[i],i);
	}
	
	  @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	 
	            if (resultCode == RESULT_OK && null != data) {
	 
	                ArrayList<String> text = data
	                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	                Toast.makeText(getApplicationContext(), text.get(0), Toast.LENGTH_LONG).show();
	                loop();
	            }
	       
	      }

	private void getInput(String question, int i) {
		// TODO Auto-generated method stub
		 Intent intent = new Intent(
                 RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

         intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

         try {
             startActivityForResult(intent, i);
         } catch (ActivityNotFoundException a) {
             Toast t = Toast.makeText(getApplicationContext(),
                     "Opps! Your device doesn't support Speech to Text",
                     Toast.LENGTH_SHORT);
             t.show();
         }
	}

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		if (status == TextToSpeech.SUCCESS) {
			 
            int result = tts.setLanguage(Locale.US);
 
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(null);
                loop();
            }
 
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
 
	}

	private void speakOut(String msg) {
		// TODO Auto-generated method stub
		if(msg==null){
			tts.speak("Welcome to Shine dot com.", TextToSpeech.QUEUE_FLUSH, null);
		}
		else{
			tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
		}
	}
}
