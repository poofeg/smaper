/*
 * COPYRIGHT - MOTIONWELDER
 */
package com.studio.motionwelder;

import java.util.Vector;

/**
	<p><font size="2">MSpiteData encapsulate actual .anu data for a given animation and it is only 
	loaded by {@link MSpriteLoader}.</font></p>
	<p><font size="2">Note: Create only one instance of MSpriteData for a given .anu, for multiple 
	animation.<br>
	To play multiple animation of same MSpriteData use multiple {@link MPlayer} 
	instance but single instance of MSpriteData</font></p>
	<p><i><font color="#000080" size="2">For e.g.: </font></i></p>
	<p><i><font size="2" color="#000080">&nbsp;Load MSpriteData with butterfly.anu.</font></i></p>
	<p><i><font size="2" color="#000080">&nbsp;MSpriteData spriteData = 
	MSpriteLoader.loadMSprite(&quot;butterfly.anu&quot;, true, this);</font></i></p>
	<p><i><font size="2" color="#000080">&nbsp;// play that butterfly animation at 
	two different position.<br>
	&nbsp;MSimpleAnimationPlayer player1 = new 
	MSimpleAnimationPlayer(spriteData,10,10);<br>
	&nbsp;MSimpleAnimationPlayer player2 = new 
	MSimpleAnimationPlayer(spriteData,20,20);<br>
	&nbsp;<br>
	&nbsp;and finally set animation, update frame and play animation.</font></i></p>
	<p><i><font size="2" color="#000080"><br>
	</font><font size="2">&nbsp;<br>
	</font>
	<b><font size="2">Note: Donot create two instance of same anu data, it's waste, and 
	unnecessarily will eat memory</font></b><font size="2"><br>
	&nbsp;// load MSpriteData with butterfly.anu.<br>
	&nbsp;MSpriteData spriteData1 = 
	MSpriteLoader.loadMSprite(&quot;butterfly.anu&quot;,true,this);<br>
	&nbsp;MSpriteData spriteData2 = 
	MSpriteLoader.loadMSprite(&quot;butterfly.anu&quot;,true,this);<br>
	&nbsp;<br>
	&nbsp;// play that butterfly animation at two different position.<br>
	&nbsp;MSpritePlayer player1 = new MSpritePlayer(spriteData1,10,10);<br>
	&nbsp;MSpritePlayer player2 = new MSpritePlayer(spriteData2,20,20);</font></i></p>

 *   
 *   @param splitImageClips value indicates, whether to keep individual clips of image in memory or to keep single image in memory
 *   @version 1.0
 *   @author Nitin Pokar (pokar.nitin@gmail.com)
 */
public class MSpriteData {

	/*  Animation table 
	 *  FORMAT:
	 *    1. [frametable-start][frametable-end]
	 *    2. [frametable-start][frametable-end]
	 *    3. [frametable-start][frametable-end]
	 */
	public short[] animationTable;
	
	/* Animation Frame Table 
	 *  FORMAT
	 *      1. [FRAME-INDEX][delay][xinc][yinc]
	 * 		2. [FRAME-INDEX][delay][xinc][yinc]
	 * 		3. [FRAME-INDEX][delay][xinc][yinc]
	 * 
	 * 		4. [FRAME-INDEX][delay][xinc][yinc]
	 * 		5. [FRAME-INDEX][delay][xinc][yinc]
	 * 
	 *      6. [FRAME-INDEX][delay][xinc][yinc]
	 * 		7. [FRAME-INDEX][delay][xinc][yinc]
	 * 		8. [FRAME-INDEX][delay][xinc][yinc]
	 * 
	 * 		9. [FRAME-INDEX][delay][xinc][yinc]
	 * 		10.[FRAME-INDEX][delay][xinc][yinc]
	 */
	public short[] frameTable;
	
	
	/*   Frame Pool Table 
	 *  FORMAT
	 *  
	 *       [CLIP INDEX][X][Y][FLAG]
	 *       [CLIP INDEX][X][Y][FLAG]   - FRAME 0
	 *       [CLIP INDEX][X][Y][FLAG]
	 *       
	 *       [CLIP INDEX][X][Y][FLAG]
	 *       [CLIP INDEX][X][Y][FLAG]    - FRAME 1
	 *       [CLIP INDEX][X][Y][FLAG]
	 *       [CLIP INDEX][X][Y][FLAG]
	 *       
	 *       [CLIP INDEX][X][Y][FLAG]
	 *       [CLIP INDEX][X][Y][FLAG]   - FRAME 2
	 *       [CLIP INDEX][X][Y][FLAG]
	 *  
	 */

	public short[] framePoolTable;
	
	/*   Clip Pool Table
	 *  FORMAT
	 *  Image
	 *  	[x][y][w][h]
	 *  	[x][y][w][h]
	 *  	[x][y][w][h]
	 *  
	 *  Ellipse
	 *  	[w][h][startAngle][endAngle][color]
	 *  	[w][h][startAngle][endAngle][color]
	 *  	[w][h][startAngle][endAngle][color]
	 *  
	 *  Line
	 *     [x2][y2][color]
	 *     [x2][y2][color]
	 *     
	 *  Rect
	 *     [w][h][color]
	 *     [w][h][color]
	 *     
	 *  RoundedRect
	 *     [w][h][arcwidth][archeight][color]
	 *     [w][h][arcwidth][archeight][color]
	 *     
	 *  PositionerRoundedRect
	 *     [w][h]
	 *     [w][h]
	 */
	
	public short[] imageClipPool;
	public int[] ellipseClipPool;
	public int[] lineClipPool;
	public int[] rectangleClipPool;
	public int[] roundedRectangleClipPool;
	public short[] positionerRectangleClipPool;
	
	/*  Image Indexer
	 *  Indexing is not read from file, so we need to index it while reading
	 */ 

	public short[] imageIndexTable;
	
	
	/*  Frame Indexer
	 *  Indexing is not read from file, so we need to index it while reading
	 */ 
	
	short[] frameTableIndex;

	/*  Split individual image clips 
	 *  Set true when needed to split images from clips 
	 * 
	 */
	boolean splitImageClips;
	
	
	/*  Images 
	 *  imageVector will contain list of images array if splitImageClip is false
	 *  else if will contain list of two dimentional image[] imageArray 
	 * 
	 */
	Vector imageVector = new Vector();
	
	/**
	 *	 MSpiteData represents actual .anu data.
	 *   @param splitImageClips
	 *   			if true 
	 *              <br></br>
	 *  			Images will be chopped to it's clips. loadImage() of {@link MSpriteLoader} will be called to load individual image clips.
	 *  			<br></br>
	 *   			else 
	 *              <br></br>
	 *  			Single image will be kept in memory. loadImage() of {@link MSpriteLoader} will be called to load image.
	 * 	 			
	 */
	protected MSpriteData(boolean splitImageClips){
		this.splitImageClips = splitImageClips;
	}
}
