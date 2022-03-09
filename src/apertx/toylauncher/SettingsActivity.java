package apertx.toylauncher;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;

public class SettingsActivity extends Activity{
 protected void onCreate(Bundle b){
  super.onCreate(b);
  pref=getSharedPreferences("me",0);
  setContentView(R.layout.main);
  grid_width=(SeekBar)findViewById(R.id.set_vert);
  grid_height=(SeekBar)findViewById(R.id.set_hor);
  label_color=(EditText)findViewById(R.id.set_fg);
  background_color=(EditText)findViewById(R.id.set_bg);
  final TextView ta=(TextView)findViewById(R.id.text_vert);
  final TextView tb=(TextView)findViewById(R.id.text_hor);
  final TextView tc=(TextView)findViewById(R.id.text_fg);
  final TextView td=(TextView)findViewById(R.id.text_bg);
  grid_width.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
    public void onProgressChanged(SeekBar v,int p,boolean m){
     ta.setText(getString(R.string.grid_width)+": "+(p+2));
    }
    public void onStartTrackingTouch(SeekBar v){
    }
    public void onStopTrackingTouch(SeekBar v){
    }
   });
  grid_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
    public void onProgressChanged(SeekBar v,int p,boolean m){
     tb.setText(getString(R.string.grid_height)+": "+(p+2));
    }
    public void onStartTrackingTouch(SeekBar v){
    }
    public void onStopTrackingTouch(SeekBar v){
    }
   });
 }

 protected void onResume(){
  super.onResume();
  grid_width.setProgress(pref.getInt("grid_width",4)-2);
  grid_height.setProgress(pref.getInt("grid_height",6)-2);
 }

 protected void onPause(){
  SharedPreferences.Editor edit=pref.edit();
  edit.putInt("grid_width",grid_width.getProgress()+2).
   putInt("grid_height",grid_height.getProgress()+2).
   putInt("label_color",0xFF3F1F5F).
   putInt("background_color",0).
   commit();
  super.onPause();
 }

 private SharedPreferences pref;
 private SeekBar grid_width;
 private SeekBar grid_height;
 private EditText label_color;
 private EditText background_color;
}
