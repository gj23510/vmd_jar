package com.amlogic.vmd.client.datadisposecenter;
import android.view.MotionEvent;

/**
 * TriangleCorner
 * @author  jun.gao
 * 
 *    *
 *    *    *         CosA=(c*c+b*b-a*a)/2bc >= Sin(90-A)
 *                   cos a =sin(90-a) 
 *    *            *
 *    *****************
 */
public class TriangleCorner {
    private  Point A = null ;private  Point B = null ;private  Point C = null; 
    private int  interrupt_diff_x                    =   10 ;
	private int  interrupt_diff_y                    =   10 ;
    private int  interrupt_degrees                   =   30 ;
   
    private boolean is_B = false ;
    
    public TriangleCorner(int touch_w , int touch_h , int interrupt_degrees) {
		this.interrupt_degrees = interrupt_degrees ;
        this.interrupt_diff_x = touch_w/ DataDisposeEnter.WH_SEGMENTATION_RATIO;
		this.interrupt_diff_y = touch_h/DataDisposeEnter.WH_SEGMENTATION_RATIO ;
		
	}
    private boolean is_C(Point       np ){
    	if((np.x - this.A.x >= this.interrupt_diff_x) || 
    			(np.y-this.A.y >= this.interrupt_diff_y)){
    		this.C = np ;
    		return true ;
    	}
    	
    	double aa = Math.pow(np.x - B.x, 2) + Math.pow(np.y - B.y, 2);
    	double bb = Math.pow(B.x  - A.x, 2) + Math.pow(B.y  - A.y, 2);
    	double cc = Math.pow(np.x - A.x, 2) + Math.pow(np.y - A.y, 2);
     if( (bb+cc-aa)/2*Math.sqrt(bb)*Math.sqrt(cc) >= Math.sin(Math.toRadians(90-interrupt_degrees ))){
    		this.C = np ;
    		return true ;
    	}
      return false ;
    }

    public boolean is_corner_point (DataDisposeEnter.TouchEvent event) {
    	 if(event.getAction() == DataDisposeEnter.TouchEvent.ACTION_MOVE){
    		 if(this.is_B){
    			 this.B = new Point(event.getX(), event.getY(), 0)  ;
    			 this.is_B = false ;
                 return false ;
    		 }
        if( is_C(new Point(event.getX(), event.getY(), 0) )){
  InstructionBufferQueue.getInstance().Push(DataDisposeEnter.InstructEnum.m_touchtrack.ordinal()+":"+(int)this.C.x+":"+(int)this.C.y);
        	return true;
        }
    		 
    	 }else if(event.getAction() ==DataDisposeEnter.TouchEvent.ACTION_DOWN){
    		this.A = new Point(event.getX(), event.getY(), 0)  ;
    		this.is_B = true; 
  InstructionBufferQueue.getInstance().Push(DataDisposeEnter.InstructEnum.m_touchtrack.ordinal()+":pressed");
  InstructionBufferQueue.getInstance().Push(DataDisposeEnter.InstructEnum.m_touchtrack.ordinal()+":"+(int)this.A.x+":"+(int)this.A.y);
    		return false ;
    	 }else if(event.getAction() == DataDisposeEnter.TouchEvent.ACTION_UP){
    		this.C = new Point(event.getX(), event.getY(), 0)  ;
    		this.is_B = false ;
  InstructionBufferQueue.getInstance().Push(DataDisposeEnter.InstructEnum.m_touchtrack.ordinal()+":"+(int)this.C.x+":"+(int)this.C.y);
  InstructionBufferQueue.getInstance().Push(DataDisposeEnter.InstructEnum.m_touchtrack.ordinal()+":release");
    		
    		return true ;
    	 }else{DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("type = " + event.getAction());}
    	return false ;
    }


	class Point { double x,y,z ; public Point(double x,double y,double z)
		{
			this.x =x;this.y =y;this.z =z;
		}
      }
}
