/*
 * COPYRIGHT - MOTIONWELDER
 */
package com.studio.motionwelder;

/**
	<p><font size="2">MSprite interface defines basic functions required to make any sprite to be 
	played by {@link MSpriteAnimationPlayer}.</font></p>
	<p><font size="2">For implementation refer examples provided.</font></p>
	<p><i><font size="2">For e.g.:</font></i></p>
	<p><font size="2" color="#000080"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; class 
	PrinceCharacter implements MSprite{</i></font></p>
	<p><font size="2" color="#000080"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	int princeX,princeY;<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	MSpriteAnimationPlayer player;</i></font></p>
	<p><font size="2" color="#000080"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	public PrinceCharacter(MSpriteData princeAnimationAnuData){<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	player = new MSpriteAnimationPlayer(princeAnimationAnuData,this);&nbsp; // 
	player takes animation data, and sprite object<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }</i></font></p>
	<p><font size="2" color="#000080"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	public int getSpriteDrawX(){ return princeX;}<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	public int getSpriteDrawY(){ return princeY;}</i></font></p>
	<p><font size="2" color="#000080"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	// also other sprite function .....<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }</i></font></p>
	<p><font size="2">Refer {@link MSpriteAnimationPlayer} constructor for detail.</font></p>
	<p><font color="#808080" size="2"><b>Note: </b>Simple Animation can be played without 
	implementing MSprite, in such case events like <br>
	sprite position update, end of animation, is polled and not notified by {@link 
	MSimpleAnimationPlayer}</font></p>
 *  
 * @version 1.0
 * @author Nitin Pokar (pokar.nitin@gmail.com)
 *   
 * */
public interface MSprite {
	
	public static final int ORIENTATION_NONE   = 0;
	public static final int ORIENTATION_FLIP_H = 1;
	public static final int ORIENTATION_FLIP_V = 2;
	public static final int ORIENTATION_FLIP_BOTH_H_V = ORIENTATION_FLIP_H | ORIENTATION_FLIP_V;
	
	/**
	 *  @return x-position of sprite to render
	 * */
	public int getSpriteDrawX();
	
	/**
	 * @return y-position of sprite to render
	 * */
	public int getSpriteDrawY();
	
	/**
	 *  It can return ORIENTATION_NONE,ORIENTATION_FLIP_H,ORIENTATION_FLIP_V
	 *  Note: ORIENTATION_FLIP_BOTH_H_V is not supported
	 *  @return orientation of sprite
	 * */
	public byte getSpriteOrientation();
	
	/**
	 *  Method to provide notification that sprite position is to be updated by deltaX, and deltaY in x and y direction respectively  
	 */
	public void updateSpritePosition(int deltaX,int deltaY);
	
	/**
	 *  Method to provide notification that current animation has come to end. 
	 */
	public void endOfAnimation();
}
