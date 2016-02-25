/*
 * COPYRIGHT - MOTIONWELDER
 */
package com.studio.motionwelder;

import java.io.DataInputStream;

import javax.microedition.lcdui.Image;

/**
 * 
	<p><font size="2">MSpriteLoader is responsible for loading {@link MSpriteData}.</font></p>
	<p><font size="2">MSpriteLoader decides whether to load complete image in memory, or load it's 
	chopped clips based on value of splitImageClips passed in loadMSprite</font></p>
 *
 *   @version 1.0
 *   @author Nitin Pokar (pokar.nitin@gmail.com)
 */
public class MSpriteLoader {
	
	public static MSpriteData loadMSprite(String spriteName,boolean splitImageClips,MSpriteImageLoader imageloader) throws Exception{

		if(imageloader == null){
			throw new IllegalArgumentException("Image Loader cannot be null");
		}

		short[] animationTable;
		short[] frameTable;
		short[] framePoolTable;
		short[] imageClipPool;
		int[]   ellipseClipPool;
		int[]   lineClipPool;
		int[]   rectangleClipPool;
		int[]   roundedRectangleClipPool;
		short[] positionerRectangleClipPool;
		
		short[] frameTableIndex;
		short[] imageIndex;
		
		MSpriteData data = new MSpriteData(splitImageClips); 

		DataInputStream dstrm = new DataInputStream(new String().getClass().getResourceAsStream(spriteName));
		try{
			dstrm.readShort(); 
			dstrm.readUTF();
			
			/** Animation table */
			int noOfAnimation = dstrm.readByte();
			animationTable = new short[noOfAnimation<<1];
			for(int i=0;i<noOfAnimation;i++){
				animationTable[2*i] = dstrm.readShort();
				animationTable[2*i+1] = dstrm.readShort();
			}
			
			
			/** Animation Frame Table*/
			int totalNoOfFrame = dstrm.readShort();
			frameTable = new short[totalNoOfFrame*4];
			for(int i=0;i<totalNoOfFrame;i++){
				frameTable[4*i] = dstrm.readShort();
				frameTable[4*i+1] = dstrm.readByte(); // delay
				frameTable[4*i+2] = dstrm.readShort();
				frameTable[4*i+3] = dstrm.readShort();
			}
			
			/** Frame Pool */
			int length = dstrm.readShort();
			int totalNumberOfClips = length>>2;
			framePoolTable = new short[length];
			int noOfFrameInPool =  dstrm.readShort();
			short index = 0;
			frameTableIndex = new short[noOfFrameInPool<<1];
			for(int i=0;i<noOfFrameInPool;i++){
				frameTableIndex[2*i] = index; 
				short noOfClips = dstrm.readShort();
				for(int j=0;j<noOfClips;j++){
					framePoolTable[index++] = dstrm.readShort(); // index
					framePoolTable[index++] = dstrm.readShort(); // xpos
					framePoolTable[index++] = dstrm.readShort(); // ypos
					framePoolTable[index++] = dstrm.readByte(); // flag
				}
				frameTableIndex[2*i+1] = (short)(index-1);
			}
			
			/** Clip Pool */
			// image
			int noOfImagesClips = dstrm.readShort();
			int noOfImages = dstrm.readByte();
			
			imageClipPool = new short[noOfImagesClips<<2];
			index=0;
			imageIndex = new short[noOfImages];
			short noOfClipsRead = 0;
			for(int i=0;i<noOfImages;i++){
				imageIndex[i] = noOfClipsRead;
				int noOfClipsInThisImage = dstrm.readShort();
			
				Image[][] imageArrayForClips = new Image[noOfClipsInThisImage][];
				
				for(int j=0;j<noOfClipsInThisImage;j++){
					int x = imageClipPool[index++] = dstrm.readShort(); 
					int y = imageClipPool[index++] = dstrm.readShort();
					int w = imageClipPool[index++] = dstrm.readShort();
					int h = imageClipPool[index++] = dstrm.readShort();
					
					if(splitImageClips){
						// check which orientation is used..
						byte orientationUsedInStudio=0;
						for(int k=0;k<totalNumberOfClips;k++){
							int pos = (k<<2);
							int clipIndex = framePoolTable[pos];
							short flag = framePoolTable[pos+3];
							byte imageId = (byte)((flag&0xf8)>>3);
							clipIndex = clipIndex-imageIndex[imageId];
							if(clipIndex!=j) continue;
							if((flag&0x01)!=0) continue;
							if(imageId!=i) continue;
							
							orientationUsedInStudio |= (byte)(flag&0x07);
						}
						
						imageArrayForClips[j] = imageloader.loadImageClip(spriteName,i,x,y,w,h,(byte)(orientationUsedInStudio>>1)); 
					}
				}
				noOfClipsRead +=noOfClipsInThisImage;
			
				// inform listener about loading images
				if(splitImageClips){
					data.imageVector.addElement(imageArrayForClips);
				} else {
					
					byte orientationUsedInStudio=0;
					for(int k=0;k<totalNumberOfClips;k++){
						short flag = framePoolTable[(k<<2)+3];
						byte imageId = (byte)((flag&0xf8)>>3);
						if((flag&0x01)!=0) continue; // it sud be image flag
						if(imageId!=i) continue;
						
						orientationUsedInStudio |= (byte)(flag&0x07);
					}
					data.imageVector.addElement(imageloader.loadImage(spriteName,i,(byte)(orientationUsedInStudio>>1)));
				}
			}
				
			// ellipse
			int noOfEllipseClip = dstrm.readShort();
			ellipseClipPool = new int[noOfEllipseClip*5];
			for(int i=0;i<noOfEllipseClip;i++){
				ellipseClipPool[5*i] = dstrm.readShort();
				ellipseClipPool[5*i+1] = dstrm.readShort();
				ellipseClipPool[5*i+2] = dstrm.readShort();
				ellipseClipPool[5*i+3] = dstrm.readShort();
				ellipseClipPool[5*i+4] = dstrm.readInt();
			}

			// Line
			int noOfLineClip = dstrm.readShort();
			lineClipPool = new int[noOfLineClip*3];
			for(int i=0;i<noOfLineClip;i++){
				lineClipPool[3*i] = dstrm.readShort();
				lineClipPool[3*i+1] = dstrm.readShort();
				lineClipPool[3*i+2] = dstrm.readInt();
			}
			
			// Rectangle
			int noOfRectangleClip = dstrm.readShort();
			rectangleClipPool = new int[noOfRectangleClip*3];
			for(int i=0;i<noOfRectangleClip;i++){
				rectangleClipPool[3*i] = dstrm.readShort();
				rectangleClipPool[3*i+1] = dstrm.readShort();
				rectangleClipPool[3*i+2] = dstrm.readInt();
			}
			
			// rounded Rect
			int noOfRoundedRectangleClip = dstrm.readShort();
			roundedRectangleClipPool = new int[noOfRoundedRectangleClip*5];
			for(int i=0;i<noOfRoundedRectangleClip;i++){
				roundedRectangleClipPool[5*i]   = dstrm.readShort();
				roundedRectangleClipPool[5*i+1] = dstrm.readShort();
				roundedRectangleClipPool[5*i+2] = dstrm.readShort();
				roundedRectangleClipPool[5*i+3] = dstrm.readShort();
				roundedRectangleClipPool[5*i+4] = dstrm.readInt();
			}
			
			// rounded Rect
			int noOfPositionerRectangleClip = dstrm.readShort();
			positionerRectangleClipPool = new short[noOfPositionerRectangleClip<<1];
			for(int i=0;i<noOfPositionerRectangleClip;i++){
				positionerRectangleClipPool[2*i] = dstrm.readShort();
				positionerRectangleClipPool[2*i+1] = dstrm.readShort();
			}
			
		} catch (Exception e) {
			throw e;
		} finally{
			if(dstrm!=null)
				dstrm.close();
		}
		
		data.animationTable = animationTable;
		data.frameTable = frameTable;
		data.frameTableIndex = frameTableIndex;
		
		data.framePoolTable = framePoolTable;
		
		data.imageClipPool = imageClipPool;
		data.ellipseClipPool = ellipseClipPool;
		data.lineClipPool = lineClipPool;
		data.rectangleClipPool = rectangleClipPool;
		data.roundedRectangleClipPool = roundedRectangleClipPool;
		data.positionerRectangleClipPool = positionerRectangleClipPool;
		data.imageIndexTable = imageIndex;
		
		return data;
	}
}
