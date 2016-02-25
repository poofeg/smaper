package com.studio.motionwelder;
/**
	<p><font size="2">MSpriteAnimationPlayer plays sprite animation which are encapsulated in a 
	class {@link MSprite}</font></p>
	<p><font color="#000080" size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; class PrinceCharacter implements 
	MSprite{</i></font></p>
	<p><font color="#000080" size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; int princeX,princeY;<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; MSpriteAnimationPlayer player;</i></font></p>
	<p><font color="#000080" size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; public 
	PrinceCharacter(MSpriteData princeAnimationAnuData){<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; player = new 
	MSpriteAnimationPlayer(princeAnimationAnuData,this);&nbsp; // player takes animation 
	data, and sprite object<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }</i></font></p>
	<p><font color="#000080" size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; public int getSpriteDrawX(){ 
	return princeX;}<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; public int getSpriteDrawY(){ return princeY;}</i></font></p>
	<p><font size="2" color="#000080"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	public byte getSpriteOrientation(){<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	if( character looking towards right )&nbsp; // animation was designed for right<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	return MSprite.ORIENTATION_NONE;<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	else<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	return MSprite.ORIENTATION_FLIP_H;<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }</i></font></p>
	<p><font color="#000080" size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; // also other sprite methods 
	.....<br>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }</i></font></p>
*   @version 1.0
*   @author Nitin Pokar (pokar.nitin@gmail.com)
*/

public class MSpriteAnimationPlayer extends MPlayer{

	private MSprite sprite;
	
	/**
	 * 
	 * @param data   spriteData to be played
	 * @param sprite sprite to be played. Position, orientation etc, are taked from this sprite object
	 */
	public MSpriteAnimationPlayer(MSpriteData data,MSprite sprite){
		super(data);
		this.sprite = sprite;
	}
	
	public void notifyStartOfAnimation(){
		// do nothing..
	}
	
	public void notifyEndOfAnimation(){
		sprite.endOfAnimation();
	}
	
	/**
	 * @return Sprite Orientaion
	 */
	public byte getSpriteOrientation(){
		return sprite.getSpriteOrientation();
	}

	/**
	 * @return Sprite x position
	 */
	public int getSpriteDrawX(){
		return sprite.getSpriteDrawX(); 
	}

	/**
	 * @return Sprite y position
	 */
	public int getSpriteDrawY(){
		return sprite.getSpriteDrawY();
	}
	
	/**
	 * Updates the sprite position by xinc, and yinc 
	 */
	public void updateSpritePositionBy(int xinc,int yinc){
		sprite.updateSpritePosition(xinc,yinc);
	}
}
