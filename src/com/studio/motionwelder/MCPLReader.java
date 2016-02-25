package com.studio.motionwelder;

import java.io.DataInputStream;
/**
*   MCPLReader is used to load .cpl file which is used to load coordinates of font character, in image file.
*   Refer examples for more details
*    
*   @version 1.0
*   @author Nitin Pokar (pokar.nitin@gmail.com)
*/
public class MCPLReader {
	public static short[][] readCplFile(String cplFile) throws Exception{
		DataInputStream dstrm = null;
		short[][] cordinates = null;
		try{
		  dstrm = new DataInputStream(new String().getClass().getResourceAsStream(cplFile));
		  dstrm.readInt();
		  byte flag = dstrm.readByte();
		   int noOfClips = dstrm.readShort();
		   int noOfCordinate  = (((flag & 1)!=0)?1:0) + (((flag & 2)!=0)?1:0) + (((flag & 4)!=0)?1:0) + (((flag & 8)!=0)?1:0);
		   cordinates = new short[noOfClips][noOfCordinate];

	       for(int i=0;i<noOfClips;i++){
	          for(int j=0;j<noOfCordinate;j++){
	        	  cordinates[i][j] = dstrm.readShort();
			  }
	       }
		}catch (Exception e) {
			throw e;
		}finally{
			if(dstrm!=null){
				dstrm.close();
			}
		}
	       
	  return cordinates;
	}
}
