/*
 * COPYRIGHT - MOTIONWELDER
 */
package com.studio.motionwelder;

import javax.microedition.lcdui.Image;

/**
 * 
		<p><font size="2">MSpriteImageLoader defines interface to load images for {@link MSpriteData}</font></p>
		<p><font size="2">It defines which images to load but it's leaves up to developer, how he load 
		his image, either from any binary source, or mixing palette and data, or 
		directly from res folder.</font></p>
		<p><font size="2">It defines two version of loadImage methods, will be called while loading 
		{@link MSpriteData}, either of one loadImage method will be called depending on 
		value of splitImageClips passed in {@link MSpriteLoader} constructor</font></p>
		<p><i><b><font size="2">splitImageClips = false&nbsp; -</font></b><font size="2">
		<font color="#808080">Load complete image and store it </font></font><b>
		<font size="2"><br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Image[] &nbsp;loadImage(spriteName,imageId,orientationUsedInStudio);<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </font></b></i></p>
		<p><b><i><font size="2">splitImageClips = true </font> </i></b><i><b><font size="2">-</font></b><font size="2">
		<font color="#808080">Instead of loading complete image, load image clips (clips 
		=&nbsp; small part of a singe image used for creating animation) </font></font>
		</i><b><i><font size="2"><br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;</font></i></b><i><b><font size="2">Image[]
		</font></b></i><b><i><font size="2">&nbsp;loadImageClip(String spriteName,int imageId,int x,int 
		y,int w,int h,int orientationUsedInStudio);<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </font></i>
		</b></p>
		<p><b><i><font size="2">-spriteName <br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </font> </i></b><i><b><font size="2">-
		</font></b><font size="2">name of the sprite getting loaded ( eg butterfly.anu, 
		prince.anu ) <b><br>
		</b></font><b><font size="2">-orientationUsedInStudio<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; -</font></b></i><font size="2"><i>Orientation 
		used in Studio, <br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		MSprite.ORIENTATION_NONE&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		- No orientation<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		MSprite.ORIENTATION_FLIP_H&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		- H Flip Version is&nbsp; used<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		MSprite.ORIENTATION_FLIP_V&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		- V Flip Version is&nbsp; used<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		MSprite.ORIENTATION_FLIP_BOTH_H_V&nbsp;&nbsp; - both flips are used<br>
		</i></font><i><b><font size="2">- imageId<br>
		</font>
		</b><font size="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; - id of 
		image to be loaded , 0 indicates first image, 1 indicates second one and so on..<br>
		</font></i><font size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		Total id's are number of images loaded in Motion Welder</i></font></p>
		<p><b><i><font size="2">-x,y,w,h&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </font> </i></b><i><font size="2">&nbsp;-cordinated of 
		image part to be loaded.</font></i></p>
		<p><i><font size="2"><b>Image[3] <br>
		</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Image[0] = Image without any 
		orientation<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Image[1] = Image Flipped Horizontally&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		// null if not required<br>
		</font></i><font size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </i></font><i>
		<font size="2">&nbsp;Image[2] = Image Flipped Vertically&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		// null if not required</font></i></p>
		<p><font size="2"><b><i>&nbsp;&nbsp;&nbsp; </i></b></font></p>
		<hr>
		<p><b><i>Which Image to load.??</i></b><i><b>:</b></i></p>
		<p><font size="2"><i>&nbsp;&nbsp;&nbsp; <b>ImageId:</b><br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Lets says 
		prince.anu is created from loading two images in MotionWelder Editor, then first 
		will be assigned as id=0, and second will be assigned id=1</i></font></p>
		<p><font size="2"><i>&nbsp;<b>&nbsp;&nbsp; OrientationUsedInStudio:</b><br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; OrientationUsedInStudio just gives 
		what is used in editor, but <br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Final total number of orientaion of image 
		to be loaded in memory = ( orientationUsedInStudio + orientationUsedInYourGame)<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; For eg: Consider 
		Prince animation, in which frames created using motion welder uses horizontal 
		flipped clips. We have two cases here:<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; (I) In which we have prince 
		moving just in one direction, lets say RIGHT, throughout&nbsp; the game (single 
		direction panning)<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		total number of orientaion of image to be loaded in memory = ( 
		orientationUsedInStudio(FLIP_H) +&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		orientationUsedInYourGame(NONE))</i></font></p>
		<p><font size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		(II) In which we have prince moving just in both direction, lets say LEFT, and 
		RIGHT<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		total number of orientaion of image to be loaded in memory = ( 
		orientationUsedInStudio(FLIP_H) +&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		orientationUsedInYourGame(FLIP_H))</i></font></p>
		<p><i><font size="2">Above seems to be confusing.. right..?? let us get 
		deeper.<br>
		While loading you MSpriteData, loadImage function will be called.. now question 
		is which image to be loaded..!! lets us chop it..</font></i></p>
		<blockquote>
		  <p><font color="#000080" size="2"><i>Image[] loadImage(String spriteName,int 
		  imageId,byte orientationUsedInStudio){</i></font></p>
		  <p><font color="#000080" size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		  Image[] image = new Image[3];</i></font></p>
		  <p><font color="#000080" size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		  image[0] = [ load image.. the way you want];</i></font></p>
		  <p><i><font size="2" color="#000080">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		  </font><font color="#000080"><b><font size="2">&nbsp; // FOR CASE I<br>
		  </font>
		  </b><font size="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		  </font><font size="2">boolean</font><font size="2"> </font> </font></i><font color="#000080"><i>
		  <font size="2">doYouNeedFlippedSpriteInYourgame = false; // for case (i) this is false</font></i></font></p>
		  <p><i><font size="2" color="#000080">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		  if(spriteName.equals(&quot;prince.anu&quot;)){<br>
		  </font></i><font color="#000080"><i><font size="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		  if(orientationUsedInStudio==FLIP_H || orientationUsedInStudio==BOTH_H_V || 
		  doYouNeedFlippedSpriteInYourgame) {<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		  image[1] = [ load image.. the way you want];&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		  <br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		  }&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		  </font></i></font>
		  </p>
		  <p><i><font size="2" color="#000080"><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		  </b></font><b><font size="2" color="#000080">// FOR CASE II<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </font></b>
		  <font color="#000080" size="2">&nbsp;boolean
		  </font></i><font color="#000080"><i><font size="2">doYouNeedFlippedSpriteInYourgame = true; 
		  // for case (ii) this is true</font></i></font></p>
		  <p><font color="#000080" size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		  if(spriteName.equals(&quot;prince.anu&quot;)){<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		  if(orientationUsedInStudio==FLIP_H || orientationUsedInStudio==BOTH_H_V || 
		  doYouNeedFlippedSpriteInYourgame) {<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		  image[1] = [ load image.. the way you want];&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		  <br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		  }&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; }&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		  </i></font></p>
		  <p><font color="#000080"><i><font size="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; // prince 
		  don't care about vertical flipping.. if yes.. same as above..<br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; image[2] = null;&nbsp;&nbsp; </font> </i>
		  </font></p>
		</blockquote>
		<p><font color="#000080" size="2"><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		return image;</i></font></p>
		<p><font color="#000080"><font size="2"><i>&nbsp;&nbsp;&nbsp; </i></font><i>
		<font size="2">&nbsp;&nbsp;&nbsp;&nbsp; }</font></i></font></p>

 *   @version 1.0
 *   @author Nitin Pokar (pokar.nitin@gmail.com)
 */
public interface MSpriteImageLoader {
	/** 
	 *  Method to provide implementation for loading image.
	 *  <br></br>-This method will be called when splitImageClips = true.
	 *  <br></br>-Indicates not to chop the image, keep complete image in heap
	 *  <br></br>-Load image corresponding to image-id, and spriteName
	 *  <br></br>-This function is called to that user load the image in advance before playing this image.
	 *  <br></br>-It is upto user how he loads and store image, this is just a notification, that image has to be loaded
	 *  
	 *  @param spriteName Indicates the name of the sprite whose image is to be loaded
	 *  @param imageId    Image to be loaded, image is starts from 0
	 *  @param orientationUsedInStudio denotes kind of orientation used in studio for this given image
	 *  
	 *  @return Image[] of dimension 3 - for No-Orientation, Flip-H, Flip-V
	 *  <pre><blockquote>
	 *  Image[0] = Image without any orientation
	 *  Image[1] = Image Flipped Horizontally  // null if not required
	 *  Image[2] = Image Flipped Vertically    // null if not required
	 *  </pre></blockquote>
	 */
	public Image[] loadImage(String spriteName,int imageId,int orientationUsedInStudio);

	/** 
	 *  Method to provide implementation for loading image.
	 *  <br></br>-This method will be called when splitImageClips = false.
	 * <br></br>-Indicates chop the image to it's small image clips, keep complete image in heap.
	 *  
	 * @param spriteName Indicates the name of the sprite whose image is to be loaded
	 * @param imageId    Image to be loaded
	 * @param x  X position of a clip in it's image demoted by image id
	 * @param y  Y position of a clip in it's image demoted by image id
	 * @param w  Width of a clip in it's image demoted by image id
	 * @param h  Height of a clip in it's image demoted by image id
	 * @param orientationUsedInStudio denotes kind of orientation used in studio for this given image clip
	 * 
	 * @return Image[] of dimension 3 - for No-Orientation, Flip-H, Flip-V
	 * 	<pre><blockquote>	
	 *  Image[0] = Image without any orientation
	 *  Image[1] = Image Flipped Horizontally // null if not required
	 *  Image[2] = Image Flipped Vertically   // null if not required
	 *  </pre></blockquote>
	 *        
	 */
	public Image[] loadImageClip(String spriteName,int imageId,int x,int y,int w,int h,int orientationUsedInStudio);
}
