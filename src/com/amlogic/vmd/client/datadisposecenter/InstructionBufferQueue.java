package com.amlogic.vmd.client.datadisposecenter;
import java.util.Vector;
/*
 * @author jun  @time  version 0001
 * Copyright (C) jun.gao@amlogic.com, Inc. All Rights Reserved.

 *
 * Implements InstructionBufferQueue:
 *
 */
public class InstructionBufferQueue {

	
	public static InstructionBufferQueue ibq = null;
	
	private Vector v;

	public static  InstructionBufferQueue getInstance() { 
   	 if(ibq==null) ibq= new InstructionBufferQueue();    return ibq; }
	private InstructionBufferQueue(){v=new Vector();}

    public int size(){return v.size();}
    public boolean isEmpty(){return size()==0;}
    public Object Push(Object obj){v.addElement(obj);return obj;}
     //last-in first-out
    public Object Pop(){int len=size();Object obj = Peek();if(obj != null)v.removeElementAt(len -1);return obj;}
    public Object Peek(){int len = size();if (len == 0)return null;return v.elementAt(len - 1);}
    //first-in first-out 
    public Object Pop1(){int len=size();Object obj = Peek1();if(obj != null)v.removeElementAt(0);return obj;}
    public Object Peek1(){int len = size();if (len == 0)return null;return v.elementAt(0);}
    public void remove() {	v.removeAllElements(); 	}
   

}
