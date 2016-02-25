package DrawControls;
import java.util.Vector;
import javax.microedition.lcdui.*;
import com.studio.motionwelder.*;

public class Smile
{
	MSimpleAnimationPlayer player;
	boolean disableUpdate = false;

	public Smile() {}
	
	public Smile(int anim, int x1, int y1, MSpriteData animData)
	{
		player = new MSimpleAnimationPlayer(animData, x1, y1);
		player.setAnimation(anim);
		disableUpdate = (!mip.Options.getBoolean(mip.Options.OPTION_ANIMSMILES));
	}
	//todo
	public int getWidth()
	{
		
		switch(player.getAnimation())
		{
			case 19:
				return 30;
			case 22:
				return 32;
			default:
				return 22;
		}
	}
	
	public int getHeight()
	{
		return 21;
	}
	
	public Smile(int anim, int x1, int y1, MSpriteData animData, int frame)
	{
		disableUpdate = mip.Options.getBoolean(mip.Options.OPTION_ANIMSMILES);
		player = new MSimpleAnimationPlayer(animData, x1, y1);
		player.setAnimation(anim);
		disableUpdate = (!mip.Options.getBoolean(mip.Options.OPTION_ANIMSMILES));
		try{
		player.setFrame(frame);}
		catch (Exception e) {}
	}
	
	public void setX(int x)
	{
		player.setSpriteX(x);
	}
	
	public void setY(int y)
	{
		player.setSpriteY(y);
	}
	
	public void paint(Graphics g)
	{
		update();
		player.drawFrame(g);
	}
	
	public void update()
	{
		if (!disableUpdate) player.update();
	}

}