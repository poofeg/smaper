package mip;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import com.studio.motionwelder.*;


public class ResourceLoader implements MSpriteImageLoader{
	
	static private ResourceLoader resourceLoader;
	private ResourceLoader(){
		
	}
	
	static public ResourceLoader getInstance(){
		if(resourceLoader==null){
			resourceLoader = new ResourceLoader();
		}
		
		return resourceLoader;
	}
	
	/**
	 *  Function : LoadImage will be called while loading .anu.
	 *  This version of Load Image will be called when .anu is loaded without chopping images
     *  In this example we have loaded .anu where we have passed true to MSpriteLoader, hence this function will be called
	 */
	public Image[] loadImage(String spriteName,int imageId,int orientationUsedInStudio){
		return null;
	}
	
	/**
	 *  Function : LoadImageClip will be called while loading .anu.
	 *  This version of Load Image will be called when .anu is loaded with chopped images
     *  In this example we have not loaded .anu with passing true in MSpriteLoader, hence this function will never be called
	 */
	public Image[] loadImageClip(String spriteName,int imageId,int x,int y,int w,int h,int orientationUsedInStudio){
		// returning null, as here we dont want to clips the images 
		
		Image[] images = new Image[3];
		Image baseImage;
		if(spriteName.equals("/smiles.anu")){
			baseImage = loadImage("/smiles.png");
			images[0] = Image.createImage(baseImage,x,y,w,h,Sprite.TRANS_NONE);
			if(orientationUsedInStudio==MSprite.ORIENTATION_FLIP_H || orientationUsedInStudio==MSprite.ORIENTATION_FLIP_BOTH_H_V) images[1] = Image.createImage(baseImage,x,y,w,h,Sprite.TRANS_MIRROR);
			if(orientationUsedInStudio==MSprite.ORIENTATION_FLIP_V || orientationUsedInStudio==MSprite.ORIENTATION_FLIP_BOTH_H_V) images[2] = Image.createImage(baseImage,x,y,w,h,Sprite.TRANS_MIRROR_ROT180);
			
		}
		return images;
	}
	
	public static Image loadImage(String str){
		try{
			return Image.createImage(str);
		}catch (Exception e) {
			System.out.println("Error loading Image " + str);
		}
		return null;
	}
}
