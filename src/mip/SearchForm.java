package mip;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;

import mip.comm.SearchAction;
import mip.comm.Icq;
import mip.comm.Util;
import mip.util.*;
import DrawControls.*;
import mip.Search;
/* Class for the search forms */
	public class SearchForm implements CommandListener, VirtualListCommands
	{
		/* Commands */
		
		public static Command backCommand;
		public Command searchCommand;
		public static Command addCommand;
		
		/* List for group selection */
		public static List groupList;
		/* Forms for results and query */
		public static Form searchForm;

		private TextList screen;


		/* Textboxes for search */
		private TextField uinSearchTextBox;

		private TextField nickSearchTextBox;

		private TextField firstnameSearchTextBox;

		private TextField lastnameSearchTextBox;

		private TextField emailSearchTextBox;

		private TextField citySearchTextBox;

		private TextField keywordSearchTextBox;

		private ChoiceGroup chgrAge;

		/* Choice boxes for gender and online choice */
		private ChoiceGroup gender;

		private ChoiceGroup onlyOnline;

		/* Selectet index in result screen */
		public int selectedIndex;

		/* constructor for search form */
		public SearchForm()
		{
			/* Commands */
			this.searchCommand = new Command(ResourceBundle.getString("user_search"), Command.OK, 1);
			this.backCommand = new Command(ResourceBundle.getString("back"), Command.BACK, 2);
			this.addCommand = new Command(ResourceBundle.getString("add_to_list"), Command.ITEM, 1);

			/* Form */
			this.searchForm = new Form(ResourceBundle.getString("search_user"));

			/* TextFields */
			this.uinSearchTextBox = new TextField(ResourceBundle.getString("uin"), "", 32, TextField.NUMERIC);
			this.nickSearchTextBox = new TextField(ResourceBundle.getString("nick"), "", 32, TextField.ANY);
			this.firstnameSearchTextBox = new TextField(ResourceBundle.getString("firstname"), "", 32, TextField.ANY);
			this.lastnameSearchTextBox = new TextField(ResourceBundle.getString("lastname"), "", 32, TextField.ANY);
			this.emailSearchTextBox = new TextField(ResourceBundle.getString("email"), "", 32, TextField.EMAILADDR);
			this.citySearchTextBox = new TextField(ResourceBundle.getString("city"), "", 32, TextField.ANY);
			this.keywordSearchTextBox = new TextField(ResourceBundle.getString("keyword"), "", 32, TextField.ANY);

			chgrAge = new ChoiceGroup(ResourceBundle.getString("age"), ChoiceGroup.EXCLUSIVE, Util.explode("-|13-17|18-22|23-29|30-39|40-49|50-59|> 60", '|'), null);

			/* Choice Groups */
			this.gender = new ChoiceGroup(ResourceBundle.getString("gender"), Choice.EXCLUSIVE);
			this.gender.append(ResourceBundle.getString("female_male"), null);
			this.gender.append(ResourceBundle.getString("female"), null);
			this.gender.append(ResourceBundle.getString("male"), null);
			this.onlyOnline = new ChoiceGroup("", Choice.MULTIPLE);
			this.onlyOnline.append(ResourceBundle.getString("only_online"), null);

			this.searchForm.append(this.onlyOnline);
			this.searchForm.append(this.uinSearchTextBox);
			this.searchForm.append(this.nickSearchTextBox);
			this.searchForm.append(this.firstnameSearchTextBox);
			this.searchForm.append(this.lastnameSearchTextBox);
			this.searchForm.append(this.citySearchTextBox);
			this.searchForm.append(this.gender);
			this.searchForm.append(this.emailSearchTextBox);
			this.searchForm.append(this.keywordSearchTextBox);
			this.searchForm.append(this.chgrAge);
			this.searchForm.setCommandListener(this);

			/* Result Screen */
			screen = new TextList(null);
			screen.setVLCommands(this);
			screen.setCursorMode(TextList.SEL_NONE);
                        screen.setSoftNames("cancel", "comlist");
			mipUI.setColorScheme(screen);
		}
		
		static final public int ACTIV_SHOW_RESULTS   = 1;
		static final public int ACTIV_JUST_SHOW      = 2;
		static final public int ACTIV_SHOW_NORESULTS = 3;

		/* Activate search form */
		public void activate(int type)
		{
			switch (type)
			{
			case ACTIV_SHOW_RESULTS:
				drawResultScreen(selectedIndex);
				MIP.setDsp(MIP.DSP_SEARCHRESULTS);
				this.screen.activate(MIP.display);
				break;
				
			case ACTIV_JUST_SHOW:
				this.searchForm.addCommand(this.searchCommand);
				this.searchForm.addCommand(this.backCommand);
				MIP.display.setCurrent(this.searchForm);
				break;
				
			case ACTIV_SHOW_NORESULTS:	
				this.searchForm.addCommand(this.searchCommand);
				this.searchForm.addCommand(this.backCommand);
            	Alert alert = new Alert(null, ResourceBundle.getString("no_results"), null, null);
            	alert.setTimeout(Alert.FOREVER);
            	MIP.display.setCurrent(alert, this.searchForm);
				break;
			}
		}

		public void drawResultScreen(int n)
		{
			/* Remove the older entrys here */
			screen.clear();

			if (Search.size() > 0)
			{
				screen.lock();

				mipUI.fillUserInfo(Search.getResult(n), screen);
				screen.setCaption(ResourceBundle.getString("results") + " " + Integer.toString(n + 1) + "/" + Integer.toString(Search.size()));
				screen.unlock();
			}
			else
			{
				/* Show a result entry */

				screen.lock();
				screen.setCaption(ResourceBundle.getString("results") + " 0/0");
				screen.addBigText(ResourceBundle.getString("no_results") + ": ", 0x0, Font.STYLE_BOLD, -1);
				screen.unlock();
			}
		}

		public void nextOrPrev(boolean next)
		{
			if (next)
			{
				selectedIndex = (selectedIndex + 1) % Search.size();
				this.activate(SearchForm.ACTIV_SHOW_RESULTS);
			}
			else
			{
				if (selectedIndex == 0) selectedIndex = Search.size() - 1;
				else
				{
					selectedIndex = (selectedIndex - 1) % Search.size();
				}
				this.activate(SearchForm.ACTIV_SHOW_RESULTS);
			}

		}

		public void onKeyPress(VirtualList sender, int keyCode, int type)
		{
			try{
			if (type == VirtualList.KEY_PRESSED)
			{
				switch (sender.getGameAction(keyCode))
				{
				case Canvas.LEFT:
					nextOrPrev(false);
					break;

				case Canvas.RIGHT:
					nextOrPrev(true);
					break;
				}
			}
			}
			catch(Exception e){}
		}

		public void onCursorMove(VirtualList sender)
		{
		}

		public void onItemSelected(VirtualList sender)
		{
		}

		public void commandAction(Command c, Displayable d)
		{
			if (c == this.backCommand)
			{
				if (mipUI.isControlActive(screen) && !Search.liteVersion)
				{
					activate(SearchForm.ACTIV_JUST_SHOW);
				}
				else
				{
					searchForm = null;
					ContextMenu.build(0, ContactList.getVisibleContactListRef());
				}
			}
			else if (c == this.searchCommand)
			{
				selectedIndex = 0;

				String[] data = new String[Search.LAST_INDEX];

				data[Search.UIN] = this.uinSearchTextBox.getString();
				data[Search.NICK] = this.nickSearchTextBox.getString();
				data[Search.FIRST_NAME] = this.firstnameSearchTextBox.getString();
				data[Search.LAST_NAME] = this.lastnameSearchTextBox.getString();
				data[Search.EMAIL] = this.emailSearchTextBox.getString();
				data[Search.CITY] = this.citySearchTextBox.getString();
				data[Search.KEYWORD] = this.keywordSearchTextBox.getString();
				data[Search.GENDER] = Integer.toString(this.gender.getSelectedIndex());
				data[Search.ONLY_ONLINE] = this.onlyOnline.isSelected(0) ? "1" : "0";
				data[Search.AGE] = Integer.toString(this.chgrAge.getSelectedIndex());

				SearchAction act = new SearchAction(Search._this, data, SearchAction.CALLED_BY_SEARCHUSER);
				try
				{
					Icq.requestAction(act);

				}
				catch (mipException e)
				{
					mipException.handleException(e);
					if (e.isCritical()) return;
				}

				/* Clear results */
				Search.results.removeAllElements();

				/* Start timer */ 
				SplashCanvas.addTimerTask("wait", act, true);
			}
			
			else if (c == this.addCommand && d == this.groupList)
			{
				String[] resultData = Search.getResult(selectedIndex);
				ContactListContactItem cItem = new ContactListContactItem(-1, ContactList.getGroupItems()[groupList.getSelectedIndex()].getId(),
						resultData[mipUI.UI_UIN_LIST], resultData[mipUI.UI_NICK], false, false);
				cItem.setBooleanValue(ContactListContactItem.CONTACTITEM_NO_AUTH, resultData[mipUI.UI_AUTH].equals("1"));
				cItem.setBooleanValue(ContactListContactItem.CONTACTITEM_IS_TEMP, true);
				cItem.setIntValue(ContactListContactItem.CONTACTITEM_STATUS, ContactList.STATUS_OFFLINE);
				Icq.addToContactList(cItem);
			}
		}
		
		
		
	
	}