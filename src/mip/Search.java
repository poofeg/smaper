package mip;

import java.util.Vector;

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
import mip.SearchForm;

public class Search
{
	static SearchForm searchForm;

	public static boolean liteVersion;

	final public static int UIN = 0;

	final public static int NICK = 1;

	final public static int FIRST_NAME = 2;

	final public static int LAST_NAME = 3;

	final public static int EMAIL = 4;

	final public static int CITY = 5;

	final public static int KEYWORD = 6;

	final public static int GENDER = 7;

	final public static int ONLY_ONLINE = 8;

	final public static int AGE = 9;

	final public static int LAST_INDEX = 10;

	/* Results */
	public static Vector results;
	public static Search _this;
	/* Constructor */
	public Search(boolean liteVersion)
	{
		this.results = new Vector();
		_this = this;
		this.liteVersion = liteVersion;
	}	
	public static void addContact()
		{
				if (ContactList.getGroupItems().length == 0)
				{
					Alert errorMsg = new Alert(ResourceBundle.getString("warning"), mipException.getErrDesc(161, 0), null, AlertType.WARNING);
					errorMsg.setTimeout(Alert.FOREVER);
					ContactList.activate(errorMsg);
				}
				else
				{
					/* Show list of groups to select which group to add to */
					getSearchForm().groupList = new List(ResourceBundle.getString("whichgroup"), List.EXCLUSIVE);
					for (int i = 0; i < ContactList.getGroupItems().length; i++)
					{
						getSearchForm().groupList.append(ContactList.getGroupItems()[i].getName(), null);
					}
					getSearchForm().groupList.addCommand(getSearchForm().backCommand);
					getSearchForm().groupList.addCommand(getSearchForm().addCommand);
					getSearchForm().groupList.setCommandListener(getSearchForm());
					MIP.display.setCurrent(getSearchForm().groupList);
				}
		}
		
		public static void sendMsg()
		{
			String[] resultData = getResult(getSearchForm().selectedIndex);
			
				ContactListContactItem cItem = ContactList.createTempContact(resultData[mipUI.UI_UIN_LIST]);
				cItem.setStringValue(ContactListContactItem.CONTACTITEM_NAME, resultData[mipUI.UI_NICK]);
				mipUI.writeMessage(cItem, null);
		}
		
		public static void viewProfile()
		{
			String[] resultData = getResult(getSearchForm().selectedIndex);
				mipUI.requiestUserInfo(resultData[mipUI.UI_UIN_LIST], resultData[mipUI.UI_NICK]);
		}

	/* Add a result to the results vector */
	public void addResult(String uin, String nick, String name, String email, String auth, int status, String gender, int age)
	{
		String[] resultData = new String[mipUI.UI_LAST_ID];

		resultData[mipUI.UI_UIN_LIST] = uin;
		resultData[mipUI.UI_NICK] = nick;
		resultData[mipUI.UI_NAME] = name;
		resultData[mipUI.UI_EMAIL] = email;
		resultData[mipUI.UI_AUTH] = auth;
		resultData[mipUI.UI_STATUS] = Integer.toString(status);
		resultData[mipUI.UI_GENDER] = gender;
		resultData[mipUI.UI_AGE] = Integer.toString(age);

		this.results.addElement(resultData);
	}

	/* Return a result object by given Nr */
	public static String[] getResult(int nr)
	{
		return (String[]) results.elementAt(nr);
	}

	/* Return size of search results */
	public static int size()
	{
		return results.size();
	}

	/* Return the SearchForm object */
	public static SearchForm getSearchForm()
	{
		if (searchForm == null) searchForm = new SearchForm();
		return searchForm;
	}

	/** ************************************************************************* */
	/** ************************************************************************* */
	/** ************************************************************************* */

}
