package com.studio.motionwelder;
/**
	<p><font size="2" face="Times New Roman">MSimpleAnimationPlayer</font><font size="2"></font><font size="2" face="Times New Roman"> plays simple animation 
	without encapsulating sprite in {@link MSprite}, </font> </p>
	<p><font color="#000080" size="2" face="Times New Roman"><i>For e.g.;&nbsp;&nbsp;&nbsp; </i></font></p>
	<p><font size="2" color="#000080" face="Times New Roman"><i>&nbsp; MSpriteData butterFlySpriteData = MSpriteLoader.loadSprite( -- - -- -- -);</i></font></p>
	<p><font size="2" color="#000080" face="Times New Roman"><i>&nbsp; MSimpleAnimationPlayer butterFlyPlayer1 = new 
	MSpriteAnimationPlayer(butterFlySpriteData,10,10);<br>
	&nbsp; MSimpleAnimationPlayer butterFlyPlayer2 = new 
	MSpriteAnimationPlayer(butterFlySpriteData,50,60);</i></font></p>
	<p><font face="Times New Roman">&nbsp; <font size="2" color="#000080"><i>
	butterFlyPlayer1.setOrientation(MSprite.ORIENTATION_FLIP_H);</i></font></font></p>
	<p><font size="2" color="#000080" face="Times New Roman"><i>&nbsp;&nbsp;&nbsp;butterFlyPlayer1.setLoopOffset(0); // will keep playing and will be looped from 
	0<br>
	&nbsp;&nbsp;&nbsp;butterFlyPlayer2.setLoopOffset(0);</i></font></p>
	<p><font size="2" color="#000080" face="Times New Roman"><i>&nbsp;&nbsp;&nbsp; 
	butterFlyPlayer1.update();<br>
	&nbsp;&nbsp;&nbsp; butterFlyPlayer2.update();</i></font></p>
	<p><font size="2" color="#000080" face="Times New Roman"><i>&nbsp;&nbsp;&nbsp; 
	butterFlyPlayer1.drawFrame(g);<br>
	&nbsp;&nbsp;&nbsp; butterFlyPlayer2.drawFrame(g);</i></font></p>
	<p>&nbsp;</p>
	
*   @version 1.0
*   @author Nitin Pokar (pokar.nitin@gmail.com)
*/
public class MSimpleAnimationPlayer extends MPlayer{

	private int spriteX;
	private int spriteY;
	
	private byte spriteOrientation; 
	
	private boolean isPlaying;
	/**
	 * 
	 * @param spriteData spriteData to be played
	 * @param spriteX    x position at which animation is to be played
	 * @param spriteY    y position at which animation is to be played
	 */
	public MSimpleAnimationPlayer(MSpriteData spriteData,int spriteX,int spriteY){
		super(spriteData);
		this.spriteX = spriteX;
		this.spriteY = spriteY;
	}
	
	/**
	 * Sets the x position
	 * @param val x position at which animation is to be played 
	 */
	public void setSpriteX(int val){
		this.spriteX = val;
	}
	
	/**
	 * Sets the y position
	 * @param val y position at which animation is to be played 
	 */
	public void setSpriteY(int val){
		this.spriteY = val;
	}
	
	public void notifyStartOfAnimation(){
		isPlaying = true;
	}
	
	public void notifyEndOfAnimation(){
		isPlaying = false;
	}
	
	/**
	 * Method to check if player is currently playing that animation, of animation has come to an end
	 * @return true- if animation is playing , else false
	 */
	public boolean isPlaying(){
		return isPlaying;
	}
	
	/**
	 * @param orientation sprite Orientation, can accept MSprite.ORIENTATION_NONE, MSprite.ORIENTATION_FLIP_H, MSprite.ORIENTATION_FLIP_V
	 */
	public void setSpriteOrientation(byte orientation){
		this.spriteOrientation = orientation;
	}
	
	/**
	 * Used by MPlayer to play animation at a given orientation
	 * @return sprite orientaion
	 */
	public byte getSpriteOrientation(){
		return spriteOrientation;
	}
	
	/**
	 * @return x position of sprite
	 */
	public int getSpriteDrawX(){
		return spriteX; 
	}
	
	/**
	 * @return y position of sprite
	 */
	public int getSpriteDrawY(){
		return spriteY;
	}
	
	/**
	 * Updates the sprite position by xinc, and yinc 
	 */
	public void updateSpritePositionBy(int xinc,int yinc){
		spriteX+=xinc;
		spriteY+=yinc;
	}
}
