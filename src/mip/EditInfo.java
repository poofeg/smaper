package mip;

import java.io.ByteArrayOutputStream;
import javax.microedition.lcdui.*;

import mip.comm.Icq;
import mip.comm.SaveInfoAction;
import mip.comm.ToIcqSrvPacket;
import mip.comm.Util;
import mip.util.ResourceBundle;

public class EditInfo extends Form implements CommandListener
{
	private TextField _NickNameItem = new TextField( ResourceBundle.getString("nick"), null, 15, TextField.ANY );
	private TextField _FirstNameItem = new TextField( ResourceBundle.getString("firstname"), null, 15, TextField.ANY );
	private TextField _LastNameItem = new TextField( ResourceBundle.getString("lastname"), null, 15, TextField.ANY );
	private TextField _EmailItem = new TextField( ResourceBundle.getString("email"), null, 50, TextField.EMAILADDR );
	private TextField _BdayItem = new TextField( ResourceBundle.getString("birth_day"), null, 15, TextField.ANY );
	private TextField _CityItem = new TextField( ResourceBundle.getString("city"), null, 15, TextField.ANY );
	private ChoiceGroup _SexItem = new ChoiceGroup(ResourceBundle.getString("gender"), ChoiceGroup.EXCLUSIVE);
	private TextField _NotesItem = new TextField(ResourceBundle.getString("notes"), null, 1000, TextField.ANY);
	
	private Command _CmdCancel = new Command(ResourceBundle.getString("cancel"), Command.CANCEL, 0);
	private Command _CmdSave = new Command(ResourceBundle.getString("save"), Command.OK, 1);
	private Displayable _PreviousForm;
	
	public EditInfo(Displayable currentForm) 
	{
		super(ResourceBundle.getString("editform"));
		_PreviousForm = currentForm;
		_SexItem.append(ResourceBundle.getString("female"), null);
		_SexItem.append(ResourceBundle.getString("male"), null);
		append(_NickNameItem);
		append(_FirstNameItem);
		append(_LastNameItem);
		append(_SexItem);
		append(_EmailItem);
		append(_BdayItem);
		append(_CityItem);
		append(_NotesItem);
		addCommand(_CmdSave);
		addCommand(_CmdCancel);
		setCommandListener(this);
	}
	
	public static void showEditForm(String[] userInfo, Displayable previousForm)
	{
		EditInfo editInfoForm = new EditInfo(previousForm);
		editInfoForm._SexItem.setSelectedIndex( Util.stringToGender(userInfo[mipUI.UI_GENDER])-1, true );
		editInfoForm._NickNameItem.setString(userInfo[mipUI.UI_NICK]);
		editInfoForm._EmailItem.setString(userInfo[mipUI.UI_EMAIL]);
		editInfoForm._BdayItem.setString(userInfo[mipUI.UI_BDAY]);
		editInfoForm._FirstNameItem.setString(userInfo[mipUI.UI_FIRST_NAME]);
		editInfoForm._LastNameItem.setString(userInfo[mipUI.UI_LAST_NAME]);
		editInfoForm._CityItem.setString( userInfo[mipUI.UI_CITY] );
		editInfoForm._NotesItem.setString(userInfo[mipUI.UI_ABOUT]);
		
		MIP.display.setCurrent( editInfoForm );
	}

	public void commandAction(Command c, Displayable d) 
	{
		if( c == _CmdCancel )
			MIP.display.setCurrent(_PreviousForm);
		
		if( c == _CmdSave )
		{
			String[] lastInfo = Util.getLastUserInfo();
			lastInfo[mipUI.UI_NICK]       = _NickNameItem.getString();
			lastInfo[mipUI.UI_EMAIL]      = _EmailItem.getString();
			lastInfo[mipUI.UI_BDAY]       = _BdayItem.getString();
			lastInfo[mipUI.UI_FIRST_NAME] = _FirstNameItem.getString();
			lastInfo[mipUI.UI_LAST_NAME]  = _LastNameItem.getString();
			lastInfo[mipUI.UI_CITY]       = _CityItem.getString();
			lastInfo[mipUI.UI_GENDER]     = Util.genderToString(_SexItem.getSelectedIndex()+1);
			lastInfo[mipUI.UI_ABOUT]	   = _NotesItem.getString(); 
			
			//
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Util.writeWord(stream ,ToIcqSrvPacket.CLI_SET_FULLINFO, false);

			Util.writeAsciizTLV(SaveInfoAction.FIRSTNAME_TLV_ID, stream, lastInfo[mipUI.UI_FIRST_NAME], false);
		
			SaveInfoAction action = new SaveInfoAction(lastInfo);
			try
			{
				Icq.requestAction(action);
			}
			catch (mipException e)
			{
				mipException.handleException(e);
				if (e.isCritical()) return;
			}
			
			SplashCanvas.addTimerTask("saveinfo", action, false);
		}
	}
}