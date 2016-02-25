//Class for color selecting....(C) DG-SC & D@rkNeo, 2007.

package mip;
import javax.microedition.lcdui.*;

import mip.util.ResourceBundle;

public class ColorSelector implements CommandListener {
	private ColorSelector _this;
	private static Command backCommand; 
    private static Command selectCommand;
	private Displayable lastDisplay;
	private CommandListener selectionListener;
	private ColorSelectUI UI;
	static
	{
		backCommand    = new Command(ResourceBundle.getString("back"),  Command.SCREEN, 2);
		selectCommand = new Command(ResourceBundle.getString("select"), Command.OK, 1);
	}
	
	public ColorSelector()
	{
	_this = this;
	}
	
	
	public void commandAction(Command c, Displayable d)
    {
        // Activate main menu
        if (c == backCommand)
        {
           MIP.display.setCurrent(lastDisplay);
        }
        
        // Contact item has been selected
        else if (c == selectCommand)
        {
        	MIP.display.setCurrent(lastDisplay);
			UI = null;
			selectionListener.commandAction(selectCommand, UI);
        }
		
	}
	
	public void selectColor(Displayable lastDisplayable, CommandListener selListener, int curColor)
	{
		lastDisplay = lastDisplayable;
		selectionListener = selListener;
		selectedColor = curColor;
		UI = new ColorSelectUI();
		//#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
		UI.setFullScreenMode(true);
		//#sijapp cond.end#
		
		UI.addCommand(selectCommand);
		UI.addCommand(backCommand);
		UI.setCommandListener(_this);
		
		MIP.display.setCurrent(UI);
	}
	
	//Class ColorSelectUI - UI for selecting color...
	
	class ColorSelectUI extends Canvas
	{
		private final Font font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_LARGE);
		private final int fontwidth = font.charWidth('0') + (2);
		private ColorSelectUI _this;
		public final void paint(Graphics g) {
				int scrwidth=MIP.display.getCurrent().getWidth();
				int scrheight=MIP.display.getCurrent().getHeight();
               int fieldh = (scrheight = getHeight()) / 3;
               // �������
               int begx = (scrwidth - 6 * fontwidth) >> 1;
               int begy = scrheight - font.getHeight() - 7;
               // ������� ������
               g.setColor(0xffffff);
               g.fillRect(0, 0, scrwidth, scrheight);

               g.setColor(selectedColor);
               //(x, y, w, h)
               g.fillRect(2, 2, scrwidth - 4, fieldh);
               
			   // ���������
               g.setColor(0);
               g.drawRect(0, 0, scrwidth - 1, fieldh + 3);
               g.setColor(~selectedColor);
               String s = getHexColor(selectedColor);
               int strw = font.stringWidth(s);
               g.drawString(s, ((scrwidth - strw) >> 1) + (strw >> 1), (fieldh >> 1) - 2, Graphics.HCENTER | Graphics.TOP);

               // ���������
               g.setColor(curcolor);
               //(x, y, w, h)
               g.fillRect(2, fieldh + 7, scrwidth - 4, fieldh);
               g.setColor(0);
               g.drawRect(0, fieldh + 5, scrwidth - 1, fieldh + 3);

               // ������ ����� � ���������� �� 6 ��������
               s = getHexColor(curcolor);
               int somecolor;

               for (int indx = 0; indx < s.length(); indx++) {
                   
                   // ���� �������
                   somecolor = indx >= 2 ? indx >= 4 ? 0x0000ff : 0x00ff00 : 0xff0000;
                   g.setColor(somecolor);
                   g.drawSubstring(s, indx, 1, begx + indx * fontwidth, begy + 4, 20);
                   // ���������� �������
                   if (indx == editPos) {
                       g.setColor(somecolor);

                       g.fillRect(begx + indx * fontwidth, begy + 1, fontwidth - 3, 2);
                       //g.fillRect(begx + indx * fontwidth, begy + font.getHeight() + 5, fontwidth - 3, 2);
                       g.fillRect(begx + indx * fontwidth, begy + font.getHeight(), fontwidth - 3, 2);
                   }
               }
           }
        public final void keyPressed(int i) {
		
           int j = getGameAction(i);
           // ����� �� ������ ������������ �����
           if (j == 2 || j == 5) {
               {
                   editPos = (editPos + (j != 2 ? 1 : -1) + 6) % 6;
               }
           } else
               // ��������� �����
               if (j == 1 || j == 6) {
                   
                       // ����� ����: ���������� �������� �������
                       int maskstep = 0x100000 >> (editPos << 2);
                       // ����� �� ������ f
                       int mask = 0xf00000 >> (editPos << 2);
                       curcolor = curcolor & ~mask | curcolor + (j != 1 ? -maskstep : maskstep) & mask;
                   
               }
           repaint();
   }	
		ColorSelectUI()
		{
			_this = this;
		}
	}
	
	public boolean isMyOkCommand(Command command)
	{
		return (command == selectCommand);
	}

	private static String getHexColor(int color) {
       String res = "00000" + Integer.toHexString(color).toUpperCase();
       return res.substring(res.length() - 6);
	}
	
	/**
	* ��������� ����
	*/
	public int selectedColor;

	public int curcolor;

	/**
	* ������� ������������ ����� 0..5
	*/
	private int editPos;
}