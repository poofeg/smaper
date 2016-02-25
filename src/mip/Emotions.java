package mip;

//#sijapp cond.if modules_SMILES is "true" #

import java.util.Vector;
import javax.microedition.lcdui.*;

import java.io.*;

import DrawControls.*;

public class Emotions implements VirtualListCommands
{
	private static Emotions _this; 
	final private static Vector findedEmotions = new Vector();
	private static boolean used;
	private static int[] selEmotionsSmileStatic, selEmotionsIndexes, textCorrIndexes;
	private static String[] selEmotionsWord, textCorrWords;
	private static boolean[] emoFinded;
	
	public Emotions()
	{
		int iconsSize;
		used = false;
		_this = this;
                if (MIP.animationData == null) return;
		
		Vector textCorr = new Vector();
		Vector selEmotions = new Vector();
		
		// Load file "smiles.txt"
		InputStream stream = this.getClass().getResourceAsStream("/smiles.ini");
		if (stream == null) return;
		
		DataInputStream dos = new DataInputStream(stream); 

		try
		{
			StringBuffer strBuffer = new StringBuffer();
			boolean eof = false, clrf = false;
			
			for (;;)
			{
				// Read smile index
				readStringFromStream(strBuffer, dos);
				Integer currIndex = Integer.valueOf(strBuffer.toString());
				
				// Read smile name				
				readStringFromStream(strBuffer, dos);
				Integer currStaticIndex = Integer.valueOf(strBuffer.toString());
				
				// Read smile strings
				for (int i = 0;; i++)
				{
					try
					{
						clrf = readStringFromStream(strBuffer, dos);
					}
					catch (EOFException eofExcept)
					{
						eof = true;
					}
					
					String word = new String(strBuffer).trim();
				
					// Add pair (word, integer) to textCorr
					if (word.length() != 0) insertTextCorr(textCorr, word, currIndex);
					
					// Add triple (index, word, name) to selEmotions  
					if (i == 0) selEmotions.addElement(new Object[] {currIndex, word, currStaticIndex});
					
					if (clrf || eof) break;
				}
				if (eof) break;
			}
			
			stream.close();
			
		}
		catch (Exception e)
		{
			return;
		}
		
		// Write emotions data from vectors to arrays
		int size = selEmotions.size();
		selEmotionsIndexes = new int[size];
		selEmotionsWord = new String[size];
		selEmotionsSmileStatic = new int[size];
		for (int i = 0; i < size; i++)
		{
			Object[] data = (Object[])selEmotions.elementAt(i);
			selEmotionsIndexes[i]    = ((Integer)data[0]).intValue();
			selEmotionsWord[i]       = (String)data[1];
			selEmotionsSmileStatic[i] = ((Integer)data[0]).intValue();
		}
		
		size = textCorr.size();
		textCorrWords = new String[size];
		textCorrIndexes = new int[size];
		emoFinded = new boolean[size];
		for (int i = 0; i < size; i++)
		{
			Object[] data = (Object[])textCorr.elementAt(i);
			textCorrWords[i]   = (String)data[0];
			textCorrIndexes[i] = ((Integer)data[1]).intValue();
		}
		
		dos = null;
		stream = null;
		
		used = true;
	}
	
	// Add smile text and index to textCorr in decreasing order of text length 
	static void insertTextCorr(Vector textCorr, String word, Integer index)
	{
		Object[] data = new Object[] {word, index};
		int wordLen = word.length();
		int size = textCorr.size();
		int insIndex = 0;
		for (; insIndex < size; insIndex++)
		{
			Object[] cvtData = (Object[])textCorr.elementAt(insIndex);
			int cvlDataWordLen = ((String)cvtData[0]).length();
			if (cvlDataWordLen <= wordLen)
			{
				textCorr.insertElementAt(data, insIndex);
				return;
			}
		}
		textCorr.addElement(data);
	}

	// Reads simple word from stream. Used in Emotions(). 
	// Returns "true" if break was found after word
	static boolean readStringFromStream(StringBuffer buffer, DataInputStream stream) throws IOException, EOFException
	{
		byte chr;
		buffer.setLength(0);
		for (;;)
		{
			chr = stream.readByte();
			if ((chr == ' ') || (chr == '\n') || (chr == '\t')) break;
			if (chr == '_') chr = ' ';
			if (chr >= ' ') buffer.append((char)chr);
		}
		return (chr == '\n');
	}
	
	static private void findEmotionInText(String text, String emotion, int index, int startIndex, int recIndex)
	{
		if ( !emoFinded[recIndex] ) return;
		int findedIndex, len = emotion.length();
		
		findedIndex = text.indexOf(emotion, startIndex);
		if (findedIndex == -1)
 	                 {
	 	                         emoFinded[recIndex] = false;
	 	                         return;
	 	             }
		
		findedEmotions.addElement( new int[] {findedIndex, len, index} );
	}
	
	static public void addTextWithEmotions(TextList textList, String text, int fontStyle, int textColor, int bigTextIndex, int uin)
	{
		if (!used || !Options.getBoolean(Options.OPTION_USE_SMILES))
		{
			textList.addBigText(text, textColor, fontStyle, bigTextIndex);
			return;
		}
		for (int i = emoFinded.length-1; i >= 0; i--) emoFinded[i] = true;
		int startIndex = 0;
		for (;;)
		{
			findedEmotions.removeAllElements();
			
			int size = textCorrWords.length;
			for (int i = 0; i < size; i++)
			{
				findEmotionInText
				(
					text,
					textCorrWords[i],
					textCorrIndexes[i],
					startIndex,
					i
				);  
			}
			
			if (findedEmotions.isEmpty()) break;
			int count = findedEmotions.size();
			int minIndex = 100000, data[] = null, minArray[] = null;
			for (int i = 0; i < count; i++)
			{
				data = (int[])findedEmotions.elementAt(i);
				if (data[0] < minIndex)
				{
					minIndex = data[0];
					minArray = data;
				}
			}
			
			if (startIndex != minIndex)
				textList.addBigText(text.substring(startIndex, minIndex), textColor, fontStyle, bigTextIndex);
			Smile smile = new Smile(minArray[2] - 1, 0, 0, MIP.animationData);
			
			textList.addSmile
			(
				smile,
				text.substring(minIndex, minIndex+minArray[1]),
				bigTextIndex
			);
			
			startIndex = minIndex+minArray[1];
		}
		
		int lastIndex = text.length();
		
		if (lastIndex != startIndex) 
			textList.addBigText(text.substring(startIndex, lastIndex), textColor, fontStyle, bigTextIndex);
	}
	
	
	///////////////////////////////////
	//                               // 
	//   UI for emotion selection    //
	//                               //
	///////////////////////////////////
	
	static private Displayable lastDisplay;
	static private String emotionText; 
	
	static private Selector selector;

	static public void selectEmotion(Displayable lastDisplay_)
	{
		lastDisplay       = lastDisplay_;
		selector = new Selector();
                ContextMenu.saveDisplayable();
                MIP.setDsp(MIP.DSP_EMOTIONS);
                selector.setSoftNames("cancel", "select");
		mipUI.setColorScheme(selector);
		
		selector.activate(MIP.display);
	}
	
	public static void takeMeBack()
	{
		MIP.display.setCurrent(lastDisplay);
                ContextMenu.restoreDisplayable();
	}
	
	public void onKeyPress(VirtualList sender, int keyCode,int type) {}
	public void onCursorMove(VirtualList sender) {}
	public void onItemSelected(VirtualList sender) 
	{
		select();
	}

	static public void select()
	{
		MIP.display.setCurrent(lastDisplay);
		selector = null;
		mipUI.insertSmile(getSelectedEmotion());
	}
	
	static public String getSelectedEmotion()
	{
		return emotionText;
	}
	
	
	
	
	/////////////////////////
	//                     //
	//    class Selector   //
	//                     //
	/////////////////////////
			
		
	static class Selector extends VirtualList implements VirtualListCommands
	{
		static private int cols, rows, itemHeight, curCol, num;
		static private Selector _this;
		static private Smile[] smiles = new Smile[selEmotionsIndexes.length];
		Selector()
		{
			super(null);
			_this = this;
			setVLCommands(this);
			
			int drawWidth = getWidth()-8;
			
			setCursorMode(SEL_NONE);
			num = selEmotionsIndexes.length;
			int imgHeight = 22;
			for (int i = 0; i < selEmotionsSmileStatic.length; i++)
			{
				smiles[i] = new Smile(selEmotionsIndexes[i] -1, 0, 0, mip.MIP.animationData,selEmotionsSmileStatic[i]);
                                
                        }
			itemHeight = imgHeight+2;
			
			cols = drawWidth/30;
			rows = (selEmotionsIndexes.length+cols-1)/cols;
			curCol = 0;
			
			showCurrSmileName();
		}
		
		//#sijapp cond.if target is "MIDP2"#
		protected boolean pointerPressedOnUtem(int index, int x, int y)
		{
			int lastCol = curCol; 
			curCol = x/*itemHeight*/;
			if (curCol < 0) curCol = 0;
			if (curCol >= cols) curCol = cols-1;
			if (lastCol != curCol)
			{
				showCurrSmileName();
				invalidate();
			}
			return false;
		}
		//#sijapp cond.end#
		
		protected void drawItemData
		(
			Graphics g, 
		    int index, 
		    int x1, int y1, int x2, int y2,
		    int fontHeight
		)
		{
			int xa, xb;
			int startIdx = cols*index;
                        //if (startIdx < 0) startIdx = 0;
                        //if (index > cols) startIdx -= 1;
			boolean isSelected = (index == getCurrIndex());
			//int imagesCount = images.size();
			xa = x1;
			for (int i = 0; i < cols; i++, startIdx++)
			{
				if (startIdx >= selEmotionsIndexes.length) break;
                                int selIdx = _this.getCurrIndex()*cols+curCol;
                                if (selIdx >= selEmotionsSmileStatic.length) break;
				int smileIdx = selEmotionsIndexes[startIdx]; 
				
				xb = xa+30;
				
				if (isSelected && (i == curCol))
				{
					drawGradient(g, Options.getInt(Options.OPTION_COLOR10), transformColorLight(Options.getInt(Options.OPTION_COLOR10), 32), xa, y1, xb, y2);
					g.setColor(Options.getInt(Options.OPTION_COLOR2));
					g.setStrokeStyle(Graphics.DOTTED);
					//g.drawRect(xa, y1, itemHeight, itemHeight - 1);
				}
                                if (smileIdx < 31)
				{
					//g.drawImage(images.elementAt(smileIdx), xa+1, y1+1, Graphics.TOP|Graphics.LEFT);
					smiles[smileIdx - 1].setX(xa+2 + 11);
					smiles[smileIdx - 1].setY(y1+2 + 11);
					try{smiles[smileIdx - 1].paint(g);}
					catch (Exception e) {e.printStackTrace();}
				}
				xa = xb;
			}
		}
		
		static private void showCurrSmileName()
		{
			int selIdx = _this.getCurrIndex()*cols+curCol;
			if (selIdx >= selEmotionsSmileStatic.length) return;
			emotionText = selEmotionsWord[selIdx];			
			_this.setCaption(selEmotionsWord[selIdx]);
		}
		
		public int getItemHeight(int itemIndex)
		{
			return itemHeight;
		}
		
		protected int getSize()
		{
			return rows;
		}
		
		protected void get(int index, ListItem item)
		{
			
		}
		
		public void onKeyPress(VirtualList sender, int keyCode, int type) 
		{
			if ((type == VirtualList.KEY_PRESSED) || (type == VirtualList.KEY_REPEATED))
			{
			int lastCol = curCol;
			int curRow = getCurrIndex();
			int rowCount = getSize();
			try{
			switch (getGameAction(keyCode))
			{
			case Canvas.LEFT:
				if ((curRow == 0) && (curCol == 0))
				{
					curCol = cols-1;
					curRow = rowCount - 1;
				}
				else if (curCol != 0) curCol--;
				else if (curRow != 0)
				{
					curCol = cols-1;
					curRow--;
				}				
				break;
				
			case Canvas.RIGHT:
				if ((curRow == rowCount - 1) && (curCol == (num-1)%cols))
				{
					curCol = 0;
					curRow = 0;
				}
				else if (curCol < (cols-1)) curCol++;
				else if (curRow <= rowCount)
				{
					curCol = 0;
					curRow++;
				}
				break;
				
			case Canvas.UP: 
				if (curRow == 0)
				{
					curRow = rowCount;
					if (curCol > (num-1)%cols) curCol = (num-1)%cols;
				}
				break;
				
			case Canvas.DOWN:
				if ((curCol > (num-1)%cols) && (curRow == rowCount -2)) curCol = (num-1)%cols;
				break;
			}
			}
			catch (Exception e) {}
			
			setCurrentItem(curRow);
			
			int index = curCol+getCurrIndex()*cols;
			if (index >= selEmotionsIndexes.length) curCol = (selEmotionsIndexes.length-1)%cols; 
			
			if (lastCol != curCol)
			{
				invalidate();
				showCurrSmileName();
			}
		}
		}
		
		public void onCursorMove(VirtualList sender) 
		{
			showCurrSmileName();
		}
		
		public void onItemSelected(VirtualList sender) 
		{
			select();
		}
	}
}

//#sijapp cond.end#
