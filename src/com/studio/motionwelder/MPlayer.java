/*
 * COPYRIGHT - MOTIONWELDER
 */
package com.studio.motionwelder;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


/**
 *    
	<p><font size="2">&nbsp;MPlayer plays a given {@link MSpriteData}.</font></p>
	<p><i><font size="2" color="#000080">&nbsp;For e.g.:</font></i></p>
	<p><i><font size="2" color="#000080">setAnimation(0);&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	// set player to animation 0. <br>
	&nbsp;setLoopOffset(-1);&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	// to play animation once.. <br>
	&nbsp;setLoopOffset(0);&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	// restart the player from frame 0 when animation ends<br>
	&nbsp;setLoopOffset(2);&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	// restart the player from frame 2 when animation ends<br>
	&nbsp;<br>
	&nbsp;// Call this update and paint function in a run loop to get the animation
	<br>
	&nbsp;player.update();&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	// will update it to next frame<br>
	&nbsp;player.drawFrame(g); // will paint the current frame</font></i></p>
	<p><font size="2">There are two version of MPlayer available {@link MSimpleAnimationPlayer}, 
	{@link MSpriteAnimationPlayer}</font></p>
	<p><i><b><font size="2">MSimpleAnimationPlayer</font></b><font size="2">:&nbsp; For simple animation, 
	as no {@link MSprite} class is involved, recommended if u don't have a class 
	encapsulating sprite&nbsp;&nbsp; <br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <font color="#000080">&nbsp; 
	MSimpleAnimationPlayer player = new MSimpleAnimationPlayer(spritedata, posX,posY);</font></font></i></p>
	<p><i><b><font size="2">MSpriteAnimationPlayer</font></b><font size="2">:&nbsp; For complex animation, 
	as {@link MSprite} class is involved, recommended if u have a class 
	encapsulating sprite&nbsp;&nbsp; <br>
	&nbsp;&nbsp;&nbsp;&nbsp; <font color="#000080">&nbsp;MSpriteAnimationPlayer 
	player = new MSpriteAnimationPlayer (spritedata,sprite);</font></font></i></p>
	<p>&nbsp;</p>
	<p><b><font size="2">What is Positioner Rects or Collision Rects</font></b><font size="2"><br>
	Most of the time in games, we just not need a sprite playing animation, but also 
	need it to interact with other sprites.<br>
	&nbsp;In such situation, we might need to mark various position/area on that 
	sprite for a given frame, which can be done by creating rectangle in Motion 
	Welder editor, using PositionerRect(<i> marked with dotted rectangle</i>) and 
	such rectangles can be read in using this MPlayer class<br>
	<br>
	</font><i><font size="2" color="#000080">For eg:</font></i></p>
	<p><font color="#000080" size="2"><i>getNumberOfCollisionRect()&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	// returns the number of collision rect for current animation and frame<br>
	player.getCollisionRect(index)&nbsp;&nbsp; // Returns rect (int[4]) at index<br>
	&nbsp;&nbsp;&nbsp; rect[0] = x<br>
	&nbsp;&nbsp;&nbsp; rect[1] = y<br>
	&nbsp;&nbsp;&nbsp; rect[2] = w<br>
	&nbsp;&nbsp;&nbsp; rect[3] = h</i></font></p>
	<p>&nbsp;</p>

 *   @version 1.0
 *   @author Nitin Pokar (pokar.nitin@gmail.com)
 */
public abstract class MPlayer {
	
	private MSpriteData data;
	
	/** Current Animation */
	private int animation;
	
	/** Current Frame */
	private int frame;
	
	/** Frame Count */
	private int frameCount;
	
	private int loopOffset;
	
	/** Delay Count */
	private int delayCount;
	
	private int framePoolPointer;
	
//	private boolean isPlaying;
	
	private int[] rect = new int[4];
	
	private final byte IMAGE_FLAG_NONE				= 0x00; // 0000 0000
	private final byte IMAGE_FLAG_HFLIP				= 0x02; // 0000 0010 
	private final byte IMAGE_FLAG_VFLIP				= 0x04; // 0000 0100
	
	private final byte ELLIPSE_FLAG_NONE				= 0x01; // 0000 0001
	private final byte ELLIPSE_FLAG_FILLED			= 0x03; // 0000 0011
	
	private final byte LINE_FLAG 						= 0x05; // 0000 0101 
	
	private final byte RECTANGLE_FLAG_NONE			= 0x07; // 0000 0111
	private final byte RECTANGLE_FLAG_FILLED			= 0x09; // 0000 1001
	
	private final byte ROUNDEDRECTANGLE_FLAG_NONE 	= 0x0b; // 0000 1011
	private final byte ROUNDEDRECTANGLE_FLAG_FILLED 	= 0x0d; // 0000 1101
	
	private final byte POSITIONERRECTANGLE_FLAG 		= 0x0f; // 0000 1111
	
	/**
	 * @param data sprite data to be played
	 */
	public MPlayer(MSpriteData data){
		this.data = data;
	}
	
	/**
	 * Sets Animation
	 * @param id Sets player to play animation referring to this id.
	 */
	public void setAnimation(int id){
		animation = id;
		
		int pos = (animation<<1);
		frameCount = (data.animationTable[pos+1]-data.animationTable[pos] +1);
		
		setFrame(0);
		notifyStartOfAnimation();
	}
	
	/**
	 * @return animtion id for the current animation of a player
	 */
	public int getAnimation(){
		return animation;
	}
	
	/**
	 * @return frameCount for current animation
	 */
	public int getFrameCount(){
		return frameCount;
	}
	
	/**
	 * @return current frame that is played
	 */
	public int getCurrentFrame(){
		return frame;
	}
	
	/**
	 * Sets Frame
	 * @param frame sets the player to this frame
	 */
	public void setFrame(int frame){
		this.frame = frame;
		delayCount = 0;
		int frameIndex = data.animationTable[animation<<1] +frame;
		framePoolPointer = data.frameTable[(frameIndex<<2)];
	}
	
	/**	
	*   Sets loop offset for a animation
	*   @param val Loop Offset
	*	<p><font size="2" color="#000080"><i>&nbsp;Loop offset stores from where the 
	*	next frame is to start after one round of animation is completed</i></font></p>
	*	<p><font size="2" color="#000080"><i>if frameNo =-1, it will not loop and throw 
	*	end of animation on completion of animation<br>
	*	if frameNo &gt;=0, it will loop from frame number</i></font></p>
	*	<p><font size="2" color="#000080"><i>For eg.<br>
	*	&nbsp;we have 5 frames in a animation<br>
	*	setLoop(-1); <br>
	*	&nbsp;&nbsp;&nbsp; 0,1,2,3,4,(end of animation)<br>
	*	setLoop(2);<br>
	*	&nbsp;&nbsp;&nbsp; 0,1,2,3,4,2,3,4,2,3,4,2,.....</i></font></p>
	*	<p>&nbsp;</p>
	*	<p><font size="2" color="#000080"><i><br>
	*	&nbsp;</i></font></p>
	*	<p><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </i></p>
	*	<p>&nbsp;</p>
	*/
	
	public void setLoopOffset(int val){
		this.loopOffset = val;
	}
	
	/**
	 * @return Animation Count
	 */
	public int getAnimationCount(){
		return (data.animationTable.length>>>1);
	}
	
	
	/**
	 * @return Number of Collisiton rect for current frame
	 */
	
	public int getNumberOfCollisionRect(){
		int count=0;
		int startIndex = data.frameTableIndex[framePoolPointer<<1];
		int endIndex = data.frameTableIndex[(framePoolPointer<<1)+1];
		while(startIndex<endIndex){
			startIndex+=3;
			if((byte)data.framePoolTable[startIndex++]==POSITIONERRECTANGLE_FLAG){
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * @return Returns Collision Rect at given index
	 */
	public int[] getCollisionRect(int index){
		int count=-1;
		int startIndex = data.frameTableIndex[framePoolPointer<<1];
		int endIndex = data.frameTableIndex[(framePoolPointer<<1)+1];
		while(startIndex<endIndex){
			int clipIndex = data.framePoolTable[startIndex++];
			int x = data.framePoolTable[startIndex++]; // + sprite.getSpriteX()
			int y = data.framePoolTable[startIndex++]; // + sprite.getSpriteY()
			if((byte)data.framePoolTable[startIndex++]==POSITIONERRECTANGLE_FLAG){
				count++;
				if(count==index){
					clipIndex=clipIndex<<1;
					rect[2] = data.positionerRectangleClipPool[clipIndex];
					rect[3] = data.positionerRectangleClipPool[clipIndex+1];
					// changing x and y
					byte spriteOrientation = getSpriteOrientation();
					if(spriteOrientation==1){// flip h
						x = -x-rect[2];
					} else if(spriteOrientation==2){// flip v
						y = -y-rect[3];
					}
					rect[0] = x;
					rect[1] = y;

					return rect;
				}
			}
		}
		
		return null;
	}

	
	/**  
	 * This method will update sprite to next frame  
	 */
	
	public void update(){
		int frameIndex = data.animationTable[animation<<1] +frame;
		if(delayCount<data.frameTable[(frameIndex<<2) +1]){
			delayCount++;
			return;
		}
		
		// check for end of animation
		if(frame>=frameCount-1){
			if(loopOffset<0){
				notifyEndOfAnimation();
//				isPlaying = false;
//				if(sprite!=null)
//					sprite.endOfAnimation();
				return;
			} else {
				frame = loopOffset-1;
			}
		}
		
		setFrame(frame+1);
		frameIndex = data.animationTable[animation<<1] +frame;
		
		int xInc = data.frameTable[(frameIndex<<2) +2];
		int yInc = data.frameTable[(frameIndex<<2) +3];
//		data.frameTable[(frameIndex<<2) +2]
//		data.frameTable[(frameIndex<<2) +3]
//		if(sprite!=null)
//			sprite.updateSpritePosition(getSpriteOrientation()==1?-xInc:xInc,getSpriteOrientation()==2?-yInc:yInc);
//		else{
//			spriteX+=getSpriteOrientation()==1?xInc:-xInc;
//			spriteY+=getSpriteOrientation()==2?yInc:-yInc;
//		}
		updateSpritePositionBy(getSpriteOrientation()==1?-xInc:xInc,getSpriteOrientation()==2?-yInc:yInc);
		delayCount++;
	}
	
/*
 * 
 *       [CLIP INDEX][X][Y][FLAG]
 *       [CLIP INDEX][X][Y][FLAG]   - FRAME 0
 *       [CLIP INDEX][X][Y][FLAG]
 */
	/**
	 *  @param g Graphics object on which frame is rendered
	 */
	public void drawFrame(Graphics g){
		int startIndex = data.frameTableIndex[framePoolPointer<<1];
		int endIndex = data.frameTableIndex[(framePoolPointer<<1)+1];
		
		int clipX = g.getClipX();
		int clipY = g.getClipY();
		int clipW = g.getClipWidth();
		int clipH = g.getClipHeight();
		
		while(startIndex<endIndex){
			int clipIndex = data.framePoolTable[startIndex++];
			int x = data.framePoolTable[startIndex++]; // + sprite.getSpriteX()
			int y = data.framePoolTable[startIndex++]; // + sprite.getSpriteY()
			byte flag = (byte)data.framePoolTable[startIndex++];
			drawClip(g,x,y,clipIndex,flag);
			
			// reset the clip position 
			g.setClip(clipX,clipY,clipW,clipH);
		}
	}
	
	protected void drawClip(Graphics g,int x,int y,int clipIndex,byte flag){
//		byte type = (byte)(flag&0x0f);
		// check for image type flag
		if((flag&0x01)==0){
			byte imageId = (byte)((flag&0xf8)>>3);
			byte orientation = (byte)(flag&0x07);
			orientation=(byte)(orientation>>1);
			
			drawImageClip(g,x,y,imageId,clipIndex,orientation);
		} else if(flag == ELLIPSE_FLAG_NONE || flag == ELLIPSE_FLAG_FILLED) {
			//[w][h][startAngle][endAngle][color]
			int index = clipIndex*5;
			drawEllipseClip(g,x,y,data.ellipseClipPool[index],data.ellipseClipPool[index+1],data.ellipseClipPool[index+2],data.ellipseClipPool[index+3],data.ellipseClipPool[index+4],flag == ELLIPSE_FLAG_FILLED);
		} else if(flag ==LINE_FLAG) {
			 //[x2][y2][color]
			int index = clipIndex*3;
			drawLineClip(g,x,y,data.lineClipPool[index],data.lineClipPool[index+1],data.lineClipPool[index+2]);
		} else if(flag == RECTANGLE_FLAG_NONE || flag == RECTANGLE_FLAG_FILLED) {
			//[w][h][color]
			int index = clipIndex*3;
			drawRectangleClip(g,x,y,data.rectangleClipPool[index],data.rectangleClipPool[index+1],data.rectangleClipPool[index+2],flag == RECTANGLE_FLAG_FILLED);
		} else if(flag == ROUNDEDRECTANGLE_FLAG_NONE || flag == ROUNDEDRECTANGLE_FLAG_FILLED) {
			//[w][h][arcwidth][archeight][color]
			int index = clipIndex*5;
			drawRoundedRectangleClip(g,x,y,data.roundedRectangleClipPool[index],data.roundedRectangleClipPool[index+1],data.roundedRectangleClipPool[index+2],data.roundedRectangleClipPool[index+3],data.roundedRectangleClipPool[index+4],flag == ROUNDEDRECTANGLE_FLAG_FILLED);
		}
	}
	
/*	
 sprite oritn->			| 0   1   2
 					 ---------------				
 clip oritn			  0 | 0   1   2
 					  1 | 1   0   3
 					  2 | 2   3   0
 	         
 	         0 - no orientation
 	         1 - flip H
 	         2 - flip V
 	         3 - rotate 180 - NOT RECOMENDED
*/	
	
	// please don't use this if you are using Nokia Direct Graphics, instead use one below this.  
	protected void drawImageClip(Graphics g,int x,int y,byte imageId,int clipIndex,byte orientation){
		
		int index = clipIndex*4;
		int clipX = data.imageClipPool[index++];
		int clipY = data.imageClipPool[index++];
		int clipW = data.imageClipPool[index++];
		int clipH = data.imageClipPool[index++];
		
		byte spriteOrientation = getSpriteOrientation();
		
		if(orientation == spriteOrientation){ // if user have same operation as wat clip is having.. than.. flip&flip = normal
			orientation = 0;
		} else if(orientation==0 || spriteOrientation==0){
			orientation = (byte)(orientation + spriteOrientation); // take non zero value	
		} else {
			//orientation = 3;
			System.out.println("FLIP H and FLIP V, cannot be used at a same time, use your own implementation");
			return;
		}
		
		// changing x and y
		if(spriteOrientation==1){// flip h
			x = -x-clipW;
		} else if(spriteOrientation==2){// flip v
			y = -y-clipH;
		}
		
		
		// render image based on whether it is cliped or not  
		if(data.splitImageClips){
			Image img = ((Image[][])data.imageVector.elementAt(imageId))[clipIndex-data.imageIndexTable[imageId]][orientation];
			
			int xPos = x + getSpriteDrawX();
			int yPos = y + getSpriteDrawY();
			
			g.drawImage(img,xPos,yPos,20);
		} else {
			Image[] imageArr = (Image[])data.imageVector.elementAt(imageId);
			
			if(orientation==1){ // flip h
				clipX = imageArr[0].getWidth() - clipW - clipX;
			} else if(orientation==1){
				clipY = imageArr[0].getHeight() - clipH - clipY;
			}
			
			int xPos = x + getSpriteDrawX();
			int yPos = y + getSpriteDrawY();
			g.clipRect(xPos,yPos,clipW,clipH);
			
			g.drawImage(imageArr[orientation],xPos-clipX,yPos-clipY,20);
		}
	}
	
	/**
	 *  USE THIS DRAW METHOD, IF YOU ARE USING NOKIA DIRECT GRAPHICS
	 */
	/*
	protected void drawImageClip(Graphics g,int x,int y,byte imageId,int clipIndex,byte orientation){
		
		int index = clipIndex*4;
		int clipX = data.imageClipPool[index++];
		int clipY = data.imageClipPool[index++];
		int clipW = data.imageClipPool[index++];
		int clipH = data.imageClipPool[index++];
		
		byte spriteOrientation = getSpriteOrientation();
		
		if(orientation == spriteOrientation){ // if user have same operation as wat clip is having.. than.. flip&flip = normal
			orientation = 0;
		} else if(orientation==0 || spriteOrientation==0){
			orientation = (byte)(orientation + spriteOrientation); // take non zero value	
		} else {
			//orientation = 3;
			System.out.println("FLIP H and FLIP V, cannot be used at a same time, use your own implementation");
			return;
		}
		
		// changing x and y
		if(spriteOrientation==1){// flip h
			x = -x-clipW;
		} else if(spriteOrientation==2){// flip v
			y = -y-clipH;
		}
		
		
		// render image based on whether it is cliped or not  
		if(data.splitImageClips){
			Image img = ((Image[][])data.imageVector.elementAt(imageId))[clipIndex-data.imageIndexTable[imageId]][0];
			
			int xPos = x + getSpriteDrawX();
			int yPos = y + getSpriteDrawY();
			
			if(orientation==MSprite.ORIENTATION_NONE)
				g.drawImage(img,xPos,yPos,20);
			else if(orientation==MSprite.ORIENTATION_FLIP_H){
				DirectGraphics dg = DirectUtils.getDirectGraphics(g);
				dg.drawImage(img,xPos,yPos,20,DirectGraphics.FLIP_HORIZONTAL);
			}else if(orientation==MSprite.ORIENTATION_FLIP_V){
				DirectGraphics dg = DirectUtils.getDirectGraphics(g);
				dg.drawImage(img,xPos,yPos,20,DirectGraphics.FLIP_VERTICAL);
			}
		} else {
			Image[] imageArr = (Image[])data.imageVector.elementAt(imageId);
			
			if(orientation==1){ // flip h
				clipX = imageArr[0].getWidth() - clipW - clipX;
			} else if(orientation==1){
				clipY = imageArr[0].getHeight() - clipH - clipY;
			}
			
			int xPos = x + getSpriteDrawX();
			int yPos = y + getSpriteDrawY();
			g.clipRect(xPos,yPos,clipW,clipH);
			
			if(orientation==MSprite.ORIENTATION_NONE){
				g.drawImage(imageArr[0],xPos-clipX,yPos-clipY,20);
		    }else if(orientation==MSprite.ORIENTATION_FLIP_H){
				DirectGraphics dg = DirectUtils.getDirectGraphics(g);
				dg.drawImage(imageArr[0],xPos-clipX,yPos-clipY,20,DirectGraphics.FLIP_HORIZONTAL);
			}else if(orientation==MSprite.ORIENTATION_FLIP_V){
				DirectGraphics dg = DirectUtils.getDirectGraphics(g);
				dg.drawImage(imageArr[0],xPos-clipX,yPos-clipY,20,DirectGraphics.FLIP_VERTICAL);
			}
		}
	}
	*/
	
	protected void drawEllipseClip(Graphics g,int x,int y,int width,int height,int startAngle,int endAngle,int color,boolean isFilled){
		
		byte spriteOrientation = getSpriteOrientation();
		// changing x and y
		if(spriteOrientation==1){// flip h
			x = -x-width;
		} else if(spriteOrientation==2){// flip v
			y = -y-height;
		}

		int xPos = x + getSpriteDrawX();
		int yPos = y + getSpriteDrawY();
		
		g.setColor(color);
		if(isFilled)
			g.fillArc(xPos,yPos,width,height,startAngle,endAngle);
		else
			g.drawArc(xPos,yPos,width,height,startAngle,endAngle);
	}
	
	protected void drawLineClip(Graphics g,int x1,int y1,int x2,int y2,int color){
		byte spriteOrientation = getSpriteOrientation();
		// changing x and y
		if(spriteOrientation==1){// flip h
			x1 = -x1;
			x2 = -x2;
		} else if(spriteOrientation==2){// flip v
			y1 = -y1;
			y2 = -y2;
		}

		int xPos1 = x1 + getSpriteDrawX();
		int xPos2 = x2 + getSpriteDrawX();
		
		int yPos1 = y1 + getSpriteDrawY();
		int yPos2 = y2 + getSpriteDrawY();
		
		g.setColor(color);
		g.drawLine(xPos1,yPos1,xPos2,yPos2);
	}
	
	protected void drawRectangleClip(Graphics g,int x,int y,int width,int height,int color,boolean isFilled){
		g.setColor(color);

		byte spriteOrientation = getSpriteOrientation();
		// changing x and y
		if(spriteOrientation==1){// flip h
			x = -x-width;
		} else if(spriteOrientation==2){// flip v
			y = -y-height;
		}
		
		int xPos = x + getSpriteDrawX();
		int yPos = y + getSpriteDrawY();

		if(isFilled)
			g.fillRect(xPos,yPos,width,height);
		else
			g.drawRect(xPos,yPos,width,height);		
	}
	
	protected void drawRoundedRectangleClip(Graphics g,int x,int y,int width,int height,int arcWidth,int arcHeight,int color,boolean isFilled){
		byte spriteOrientation = getSpriteOrientation();
		// changing x and y
		if(spriteOrientation==1){// flip h
			x = -x-width;
		} else if(spriteOrientation==2){// flip v
			y = -y-height;
		}

		int xPos = x + getSpriteDrawX();
		int yPos = y + getSpriteDrawY();
		
		g.setColor(color);
		if(isFilled)
			g.fillRoundRect(xPos,yPos,width,height,arcWidth,arcHeight);
		else	
			g.drawRoundRect(xPos,yPos,width,height,arcWidth,arcHeight);		
	}
	
	/**
	 * @return spriteX
	 */
	protected abstract int getSpriteDrawX();
	
	/**
	 * @return spriteY
	 */
	protected abstract int getSpriteDrawY();
	
	/**
	 * Updates the sprite position by xinc, and yinc 
	 */
	protected abstract void updateSpritePositionBy(int xinc,int yinc);
	
	/**
	 * @return sprite orientation
	 */
	protected abstract byte getSpriteOrientation();
	
	/**
	 * Method to notify start of animation
	 */
	protected abstract void notifyStartOfAnimation();
	
	/**
	 * Method to notify end of animation
	 */
	protected abstract void notifyEndOfAnimation();
}
