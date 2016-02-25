/*
 * Select.java
 *
 * Created on 18 Январь 2007 г., 21:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package mip.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import mip.MIP;
import mip.util.ResourceBundle;

/**
 *
 * @author vladimir
 */
public class Select implements CommandListener {
    
    /** Creates a new instance of Select */
    public Select(String title) {
        items = new List(ResourceBundle.getString(title), List.IMPLICIT);
    }

    public void add(String promt, Image icon) {
        items.append(promt, icon);
    }
        
    public void setSelectedIndex(int index) {
        items.setSelectedIndex(index, true);
    }
    
    private ActionListener listener;
    public void addActionListener(ActionListener listener) {
        this.listener = listener;
    }
    
	private static Command backCommand = new Command(ResourceBundle.getString("back"), Command.BACK, 1);
	private static Command selectCommand = new Command(ResourceBundle.getString("select"), Command.OK, 1);

    public void show() {
        items.setCommandListener(this);
        items.addCommand(backCommand);
        items.addCommand(selectCommand);
        MIP.display.setCurrent(items);
    }

    private List items;
    private int index;
    public int getSelectedIndex() {
        return index;
    }

    public void commandAction(Command command, Displayable displayable) {
        index = -1;
        if (command == selectCommand || command == List.SELECT_COMMAND) {
            index = items.getSelectedIndex();;
			listener.actionPerformed(this);
        }
		if (command == backCommand) {
			listener.actionMMCLAct();
        }
    }
    
}
