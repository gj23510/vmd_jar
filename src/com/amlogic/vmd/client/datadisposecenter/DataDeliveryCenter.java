package com.amlogic.vmd.client.datadisposecenter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;



/*
 * @author jun  @time  version 0001
 * Copyright (C) jun.gao@amlogic.com, Inc. All Rights Reserved.

 *
 * Implements the DataDeliveryCenter:
 *
 *
 */

public class DataDeliveryCenter {

	private  static DatagramSocket sendSocket    =      null;
	private  static SocketAddress  sdd           =      null ;
	private  static boolean  endnotification     =      true;
    private static DataDeliveryCenter ddc        =      null ;
    private InstructionBufferQueue ibq           =      null ;

	private DataDeliveryCenter(){ibq = InstructionBufferQueue.getInstance();}
	
    public static boolean initiate(){if( DataDeliveryCenter.ddc == null) {
			 DataDeliveryCenter.ddc = new DataDeliveryCenter();
	if( DataDeliveryCenter.ddc.detect_byscantvinlan() ){
          DataDeliveryCenter.endnotification = false ; 
     	  DataDeliveryCenter.ddc. new working().start();
     	// InstructionBufferQueue.getInstance().Push(DataDisposeEnter.InstructEnum.m_exit.ordinal()+":exit");
     	       return true;}else return false;}else return true;
    }
	
	public static void destroy(){
	InstructionBufferQueue.getInstance().Push(DataDisposeEnter.InstructEnum.m_exit.ordinal()+":exit");
	DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("end /" + InstructionBufferQueue.getInstance().Peek1());
	   
		DataDeliveryCenter.endnotification = true; 
		DataDeliveryCenter.ddc.sendudp_packet(InstructionBufferQueue.getInstance().Pop1().toString());
	}
	
	private boolean detect_byscantvinlan (){
		DataDisposeEnter.SERVER_HOST_IP = getLocalIpAddress();
		 DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("cell ip : "+ DataDisposeEnter.SERVER_HOST_IP);
		if(DataDisposeEnter.SERVER_HOST_IP.equals("")) {
			DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("cant find host ip");
			return false;
		}
		StringBuffer sb =new StringBuffer(); 
		try{
			String[] arr=DataDisposeEnter.SERVER_HOST_IP.split("\\.");
			for(int i=0;i<arr.length-1;i++){
				sb.append(arr[i]+".");
			}
			String majorAddress=sb.toString();
			int startPort=1;int endPort=255;
			DataDeliveryCenter.sendSocket = new DatagramSocket();   
			
	         byte[] buf = DataDisposeEnter.Request.getBytes();  
    for(int nport=startPort;nport<=endPort;nport ++){
               DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("detect : "+ majorAddress+nport);
        sendSocket.send(new DatagramPacket(buf, buf.length,new InetSocketAddress(majorAddress+nport,DataDisposeEnter.SERVER_UDP_PORT))); 
             byte[] getBuf = new byte[100];  
             DatagramPacket getPacket = new DatagramPacket(getBuf, getBuf.length); 
             try {  
	               DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("wait receive ack...");
	               sendSocket.setSoTimeout(20);  
                   sendSocket.receive(getPacket);   
          } catch (Exception e) {  
    	     DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("receive timeout delay 20ms");
              continue ;
          }  
          String ack = new String(getBuf, 0, getPacket.getLength());  
          if(ack.equals(DataDisposeEnter.Response)){
        	  DataDisposeEnter.SERVER_HOST_IP  = getPacket.getAddress().toString().split("/")[1];
        	  DataDisposeEnter.SERVER_UDP_PORT = getPacket.getPort();
        	  DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("Pairing Success , "+DataDisposeEnter.SERVER_HOST_IP+":"+DataDisposeEnter.SERVER_UDP_PORT);
        	  DataDeliveryCenter.sdd = new InetSocketAddress(DataDisposeEnter.SERVER_HOST_IP, DataDisposeEnter.SERVER_UDP_PORT);
        	 return true;
            } }}catch(Exception e){}
		
           return false;
	    }
	private synchronized void sendudp_packet(String mes){
		int loop_m = 0;
		 byte[] buf = mes.getBytes(); while(true){ try {
			DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("buf :"+ mes);
			DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, DataDeliveryCenter.sdd);  
			 DataDeliveryCenter.sendSocket.send(sendPacket);  

			 byte[] getBuf = new byte[100];  
			DatagramPacket getPacket = new DatagramPacket(getBuf, getBuf.length); 
			 DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("waiting ack... ");
		       try {  
		    	     sendSocket.setSoTimeout(100);  
					 DataDeliveryCenter.sendSocket.receive(getPacket);  
          } catch (Exception e) {  
    	     DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("receive timeout delay 20ms");
    	     if(++loop_m > 2 ) break; 
    	     continue ;
           
          }  
			 String backMes = new String(getBuf, 0, getPacket.getLength());  
             DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("ack:"+backMes);
             break;
            // if(backMes.equals("ok")) break;
		} catch (SocketException e) {e.printStackTrace(); break;} catch (IOException e) {e.printStackTrace();break;}   
		 }
	}
	
 public class working extends Thread {
     public void run() {while(true){
    	 if (DataDeliveryCenter.endnotification) break;
    if(DataDeliveryCenter.sdd != null &&  ibq.size()> 0){
     String crc16 =  CRC16.getcrc16( ibq.Peek1().toString().getBytes(),ibq.Peek1().toString().length() );
     DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("crc16 = " + crc16 );
     DataDeliveryCenter.ddc.sendudp_packet(ibq.Pop1().toString()+"?"+crc16);
       }  }
    }
	}
   private String getLocalIpAddress() {     
        try {     
            for (Enumeration<NetworkInterface> en = NetworkInterface     
                    .getNetworkInterfaces(); en.hasMoreElements();) {     
                NetworkInterface intf = en.nextElement();     
                for (Enumeration<InetAddress> enumIpAddr = intf     
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {     
                    InetAddress inetAddress = enumIpAddr.nextElement();     
                    if (!inetAddress.isLoopbackAddress()) {     
                        return inetAddress.getHostAddress().toString();     
                    }     
                }     
            }     
        } catch (SocketException ex) {     
        	DataDisposeEnter.VMD_DEBUGPRINTEX_LOG("fail to catch ip:  ex ," +ex.toString());     
        }     
        return null;     
    } 
	
  /*	private String getLocalIpAddress_bywifi() {     
		WifiManager wifimanger = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiinfo = wifimanger.getConnectionInfo();
		String ip = intToIp(wifiinfo.getIpAddress());
		return ip;    
    }  
	private String intToIp(int i)
    {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    } */
}
