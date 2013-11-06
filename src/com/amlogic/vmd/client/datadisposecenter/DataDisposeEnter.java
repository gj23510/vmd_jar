package com.amlogic.vmd.client.datadisposecenter;


import android.util.Log;
import android.view.MotionEvent;
/*
 * @author jun  @time  version 0001
 * Copyright (C) jun.gao@amlogic.com, Inc. All Rights Reserved.

 *
 * Implements the DataDisposeEnter:
 *
 *
 */
public class DataDisposeEnter {
	public  final static int      WH_SEGMENTATION_RATIO          =          3;
	public  final static double    WH_TOUCHVALID_RATIO            =          0.82;
	public  final static int   INTERRUPT_DEGREES              =          30;
	public  static    int SERVER_UDP_PORT              =       6666;
	public  static String    SERVER_HOST_IP            =       "";
	public  final static boolean DEBUG                 =       false; 
	public  static final String TAG                    =       "gjaoun";
	public  static final String Request                =       "detect";
	public  static final String Response               =       "itsme";
    public  static int  device_display_axis_x   = 0 ;
    public  static int  device_display_axis_y   = 0 ;
    public  static int  touch_valid_axis_startx = 0;
    public  static int  touch_valid_axis_starty = 0 ;
    public  static int  touch_valid_axis_endx   = 0;
    public  static int  touch_valid_axis_endy   = 0 ;
    public  static int  touch_valid_width       = 0 ;
    public  static int  touch_valid_height      = 0 ;
    
    
	private TriangleCorner tc    = null; 
	
    private  final  int judge_pt = 5 ;  private int pt = 0; 
    private boolean isClickEvent = false ; 
    private boolean isWheelEvent = false ;
    private boolean isOutSlid = false ;
	 private double s_x ;private double s_y ;
	public  static enum  InstructEnum {m_initparam,m_touchtrack,m_keypad,m_wheel,m_exit, }

	public DataDisposeEnter(int axis_x ,int axis_y , int vstart_x ,int vstart_y ,int vend_x ,int vend_y){
		DataDisposeEnter.device_display_axis_x        = axis_x ;
		DataDisposeEnter.device_display_axis_y        = axis_y ;
		DataDisposeEnter.touch_valid_axis_startx      = vstart_x ;
		DataDisposeEnter.touch_valid_axis_starty      = vstart_y ;
		DataDisposeEnter.touch_valid_axis_endx        = vend_x ;
		DataDisposeEnter.touch_valid_axis_endy        = vend_y ;
		DataDisposeEnter.touch_valid_width   =  Math.abs(vend_x - vstart_x ) ;
		DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("touch_valid_width = "+ touch_valid_width) ;
		DataDisposeEnter.touch_valid_height  =  Math.abs(vend_y - vstart_y ) ;
		this.tc  = new TriangleCorner(touch_valid_width, touch_valid_height, DataDisposeEnter.INTERRUPT_DEGREES);
	/*	PostToInstructionBufferQueue(DataDisposeEnter.InstructEnum.m_initparam.ordinal()+":"+
				device_w+":"+device_h+":"+valid_w+":"+valid_h); */
	}
	public static void VMD_DEBUGPRINTEX_LOG(Object... args){	if(!DEBUG) return ; 
	String mes = "";for (Object v : args) mes = mes+v.toString() ;
         Log.d(TAG,mes);
	}
	
	public boolean AutoPairingTVByWiFi () {  //if pairing failed  return false 
         return DataDeliveryCenter.initiate();
	}
     public void  DestoryNotification (){
    	   DataDeliveryCenter.destroy() ;
     }
     
     
     public  void HandleTrackPoints (MotionEvent ev){
    	 TouchEvent event = new TouchEvent(ev.getAction(), ev.getX(), ev.getY()); 
    	 double x= event.getX(); double y= event.getY();
    	 if(x < DataDisposeEnter.touch_valid_axis_startx || x > DataDisposeEnter.touch_valid_axis_endx 
    	    || y < DataDisposeEnter.touch_valid_axis_starty || y > DataDisposeEnter.touch_valid_axis_endy)  {
    		  if(!isOutSlid ){
    			  isOutSlid = true ;
    		  event.setAction(TouchEvent.ACTION_UP);
    		  }else   return ;
    		 
    		 }
    	 
    	 DataDisposeEnter.VMD_DEBUGPRINTEX_LOG(" x = "+x +" , y = "+y);
        
          if(event.getAction() == TouchEvent.ACTION_DOWN){
        	  DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("---------ACTION_DOWN");
	         isClickEvent = true ; pt =  0 ;
	         s_x = x ; s_y = y ; isOutSlid = false ;
            if(Math.abs(x - DataDisposeEnter.touch_valid_axis_startx ) >= DataDisposeEnter.touch_valid_width * WH_TOUCHVALID_RATIO   ){
            	 Log.d("gjaoun","down wheel ="+pt+ ","+x+"-"+s_x+","+y+"-"+s_y+ "R="+DataDisposeEnter.touch_valid_width * WH_TOUCHVALID_RATIO);
            	isWheelEvent = true ;
            }
	       
		 }
	    else if(event.getAction() == TouchEvent.ACTION_MOVE){
	    	DataDisposeEnter.VMD_DEBUGPRINTEX_LOG( "---------ACTION_MOVE");
	    	if (isClickEvent &&  ( (Math.abs(x -s_x) > 2) || (Math.abs(y-s_y) > 2)) )
	    		{
	    		Log.d("gjaoun","daduan pt ="+pt+ ","+x+"-"+s_x+","+y+"-"+s_y);
	    		isClickEvent = false;
	    		}
	    	
	    	
	    	 if(isWheelEvent && (Math.abs(x - DataDisposeEnter.touch_valid_axis_startx ) < DataDisposeEnter.touch_valid_width * WH_TOUCHVALID_RATIO )  ){
	    		 Log.d("gjaoun","daduan wheel ="+pt+ ","+x+"-"+s_x+","+y+"-"+s_y+ "R="+DataDisposeEnter.touch_valid_width * WH_TOUCHVALID_RATIO);
	            	isWheelEvent = false;
	            }
		       
	    	
		 }
	    else if(event.getAction() == TouchEvent.ACTION_UP){
	    	DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("---------ACTION_UP");
	    	 if(pt <= judge_pt && isClickEvent ){
	    		if(x <= DataDisposeEnter.touch_valid_width/2){
	    			DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("---------touch left click");
	    		   PostToInstructionBufferQueue(InstructEnum.m_keypad.ordinal()+":7");}
					else{
						DataDisposeEnter.VMD_DEBUGPRINTEX_LOG( "---------touch right click");
						PostToInstructionBufferQueue(InstructEnum.m_keypad.ordinal()+":8");
					}
	    	 }
	    	 
	    	if(isWheelEvent  && !isClickEvent){
	    		isWheelEvent = false ;  int diff = (int)Math.abs(y-s_y) ; 
	    		 boolean direction = true ; // true = - ,down ; false = + ,up 
	    		 if(y > s_y) direction = true ; else direction = false ;
	    		if(diff <= DataDisposeEnter.touch_valid_height/4){
	    		 	DataDisposeEnter.VMD_DEBUGPRINTEX_LOG( "wheelEvent : "+ (direction?"-10":"10") );
	    		 	PostToInstructionBufferQueue(InstructEnum.m_wheel.ordinal()+":"+(direction?"-10":"10"));	
	    		}
	    		else if (diff > DataDisposeEnter.touch_valid_height/4   && diff < DataDisposeEnter.touch_valid_height*3/4  ){
	    			DataDisposeEnter.VMD_DEBUGPRINTEX_LOG( "wheelEvent : "+ (direction?"-20":"20") );
	    			PostToInstructionBufferQueue(InstructEnum.m_wheel.ordinal()+":"+(direction?"-20":"20"));	
	    		}
	    		else if (diff >= DataDisposeEnter.touch_valid_height*3/4 ){
	    		 DataDisposeEnter.VMD_DEBUGPRINTEX_LOG( "wheelEvent : "+ (direction?"-30":"30") );
	    		 PostToInstructionBufferQueue(InstructEnum.m_wheel.ordinal()+":"+(direction?"-30":"30"));	
	    		 }
	    	} 
	    	 
	    	 
		 }
          pt++ ;
       if( this.tc.is_corner_point(event) )
		  DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("send / " +InstructEnum.m_touchtrack.ordinal()+":"+(int)event.getX()+":"+(int)event.getY());
		
     }

	

    public void PostToInstructionBufferQueue(String instruction){
		InstructionBufferQueue.getInstance().Push(instruction);
	}
    
    class TouchEvent {
    	 public static final int ACTION_DOWN             = 0;
         public static final int ACTION_UP               = 1;
         public static final int ACTION_MOVE             = 2;
    	public TouchEvent(int type , double x ,double y ){
    		this.type = type ;
    		this.x    =   x ;
    		this.y    =   y ;
    	}  
    	int type = -1 ;
    	double x ,y ;
    	public double getX() {
			return x;
		}
		public void setX(double x) {
			this.x = x;
		}
		public double getY() {
			return y;
		}
		public void setY(double y) {
			this.y = y;
		}
		public int getAction(){
    		return type ;
    	}
		public int setAction( int action){
    		return this.type = action ;
    	}
    	
    }

}
