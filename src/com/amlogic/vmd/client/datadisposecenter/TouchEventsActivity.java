package com.amlogic.vmd.client.datadisposecenter;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class TouchEventsActivity  extends Activity  implements OnTouchListener{
    private DataDisposeEnter dde = null; private boolean isPairingOK = false;
    public int screen_width ;public int screen_height ; 
    public enum  KeyPadCode { KEY_LEFT,KEY_RIGHT,KEY_UP,KEY_DOWN,KEY_ENTER ,KEY_BACK,}
    public void onCreate(Bundle savedInstanceState,int x0,int y0,int x1,int y1) {
	        super.onCreate(savedInstanceState);
	        DisplayMetrics dm = new DisplayMetrics();
	        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
	        this.screen_width = dm.widthPixels;
	        this.screen_height = dm.heightPixels;
            dde = new DataDisposeEnter(this.screen_width, this.screen_height,x0,y0, x1, y1);
	        new Thread( new Runnable(){public void run() {isPairingOK  = dde.AutoPairingTVByWiFi();}} ).start(); 
	  }
	  
	@Override
	public boolean onTouch(View v, MotionEvent event) {return false;}
    public boolean onTouchEvent(MotionEvent event){if(isPairingOK) dde.HandleTrackPoints(event);return true ;             
	  }
	 
	 public void onKeyPad(KeyPadCode keycode){  
		 if(isPairingOK)
		    dde.PostToInstructionBufferQueue(DataDisposeEnter.InstructEnum.m_keypad.ordinal()+":"+(keycode.ordinal()+1));
	 }
	  protected void onStop() {
		  super.onStop();
		  if(isPairingOK)
		     dde.DestoryNotification(); 	 
     }
	 
}
