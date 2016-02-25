package mip;

import javax.microedition.lcdui.*;


import mip.util.ResourceBundle;
import DrawControls.*;
import mip.comm.Icq;

public class StatusIcons implements VirtualListCommands {

    private StatusIcons _this;
    final public static ImageList images = new ImageList();
    final public static ImageList images2 = new ImageList();
    final public static ImageList images3 = new ImageList();
    final public static ImageList images4 = new ImageList();
    private int statusID;
    static public boolean haveToRestoreStatus;

    public StatusIcons() {
        _this = this;

        // Read images
        try {
            images.load("/icons.png", -1, -1, -1);
            images2.load("/xstatus.png", -1, -1, -1);
            images3.load("/cicons.png", -1, -1, -1);
            images4.load("/pstatus.png", -1, -1, -1);
        } catch (Exception e) {
            return;
        }
    }
    ///////////////////////////////////
    //                               // 
    //   UI for emotion selection    //
    //                               //
    ///////////////////////////////////
    private StatusSelector StatusSelector;
    private int currentIL;

    public void selectIcon(int i) {
        if (i == 1) {
            StatusSelector = new StatusSelector(images2);
        } else if (i == 0) {
            StatusSelector = new StatusSelector(images);
            haveToRestoreStatus = false;
        } else if (i == 2) {
            StatusSelector = new StatusSelector(images3);
        } else if (i == 3) {
            StatusSelector = new StatusSelector(images4);
        }

        currentIL = i;
        mipUI.setColorScheme(StatusSelector);
        StatusSelector.activate(MIP.display);
    }

    public void onKeyPress(VirtualList sender, int keyCode, int type) {
    }

    public void onCursorMove(VirtualList sender) {
    }

    public void onItemSelected(VirtualList sender) {
        select();
    }
    
    public void selectMainStatus(int statusID) {
        int onlineStatus = ContactList.STATUS_ONLINE;
        boolean prStatusChanged = false;
        switch (statusID) {
            case VMenuItem.VMI_STATUS_CHAT:
                onlineStatus = ContactList.STATUS_CHAT;
                break;
            case VMenuItem.VMI_STATUS_OCCUPIED:
                onlineStatus = ContactList.STATUS_OCCUPIED;
                break;
            case VMenuItem.VMI_STATUS_DND:
                onlineStatus = ContactList.STATUS_DND;
                break;
            case VMenuItem.VMI_STATUS_AWAY:
                onlineStatus = ContactList.STATUS_AWAY;
                break;
            case VMenuItem.VMI_STATUS_NA:
                onlineStatus = ContactList.STATUS_NA;
                break;
            case VMenuItem.VMI_STATUS_EVIL:
                onlineStatus = ContactList.STATUS_EVIL;
                break;
            case VMenuItem.VMI_STATUS_DEPRESS:
                onlineStatus = ContactList.STATUS_DEPRESS;
                break;
            case VMenuItem.VMI_STATUS_HOME:
                onlineStatus = ContactList.STATUS_HOME;
                break;
            case VMenuItem.VMI_STATUS_WORK:
                onlineStatus = ContactList.STATUS_WORK;
                break;
            case VMenuItem.VMI_STATUS_LUNCH:
                onlineStatus = ContactList.STATUS_LUNCH;
                break; 
            case VMenuItem.VMI_STATUS_INVISIBLE:
                onlineStatus = ContactList.STATUS_INVISIBLE;
                Options.setInt(Options.OPTION_PRIVATESTATUS, 3);
                prStatusChanged = true;
                break;
            case VMenuItem.VMI_STATUS_INVIS_ALL:
                onlineStatus = ContactList.STATUS_INVIS_ALL;
                Options.setInt(Options.OPTION_PRIVATESTATUS, 2);
                prStatusChanged = true;
                break;
        }
        Options.setLong(Options.OPTION_ONLINE_STATUS, onlineStatus);
        Options.safe_save();
        
        if (Icq.isConnected()) {
            try {
                Icq.setCliStatus(prStatusChanged, false);
            } catch (mipException e) {
                mipException.handleException(e);
                if (e.isCritical()) {
                    return;
                }
            }
        }
    }
    
    public void select() {
        switch (currentIL) {
            case 1:
                if (getSelectedIndex() == 0) {
                    Options.setInt(Options.OPTION_XSTATUS, -1);
                } else {
                    Options.setInt(Options.OPTION_XSTATUS, getSelectedIndex());
                }
                Options.safe_save();
                if (getSelectedIndex() == 0) {
                    ContactList.activate();
                    if (Icq.isConnected()) {
                        try {
                            Icq.setCLI_USERINFO(Options.getInt(Options.OPTION_CLI_ID));
                        } catch (mipException e) {
                            mipException.handleException(e);
                            if (e.isCritical()) {
                                return;
                            }
                        }
                    }
                    break;
                }
                ContextMenu.xTextField.setString(Options.getString(Options.OPTION_STATUS_TITLE));
                ContextMenu.xTextField2.setString(Options.getString(Options.OPTION_STATUS_MESSAGE));
                ContextMenu.xOptionsChoiceGroup.setSelectedIndex(0, Options.getBoolean(Options.OPTION_SHOWXTRAZMSG));
                if (Options.getInt(Options.OPTION_BFLAG) == 1) {
                    ContextMenu.xOptionsChoiceGroup.setSelectedIndex(1, true);
                }
                MIP.display.setCurrent(ContextMenu.xForm);
                break;

            case 2:
                Options.setInt(Options.OPTION_CLI_ID, getSelectedIndex());
                Options.safe_save();
                if (Icq.isConnected()) {
                    try {
                        Icq.setCLI_USERINFO(getSelectedIndex());
                    } catch (mipException e) {
                        mipException.handleException(e);
                        if (e.isCritical()) {
                            return;
                        }
                    }
                }
                break;

            case 3:
                if (Options.getInt(Options.OPTION_PRIVATESTATUS) == getSelectedIndex() + 1) {
                    break;
                }
                Options.setInt(Options.OPTION_PRIVATESTATUS, getSelectedIndex() + 1);
                Options.safe_save();
                if (Icq.isConnected()) {
                    try {
                        Icq.setCliStatus(true, false);
                    } catch (mipException e) {
                        mipException.handleException(e);
                        if (e.isCritical()) {
                            return;
                        }
                    }
                }
                break;
        }
        StatusSelector = null;
        switch (currentIL) {
            case 0:
            case 3:
                ContextMenu.build(6, ContactList.getVisibleContactListRef());
                break;
            case 2:
                ContextMenu.build(0, ContactList.getVisibleContactListRef());
                break;
        }
    }

    public int getSelectedIndex() {
        return statusID;
    }
    /////////////////////////
    //                     //
    //    class StatusSelector   //
    //                     //
    /////////////////////////
    class StatusSelector extends VirtualList implements VirtualListCommands {

        private int cols,  rows,  itemHeight,  curCol,  num;
        private ImageList imageL;
        private StatusSelector _this;

        StatusSelector(ImageList imgs) {
            super(null);
            setSoftNames("cancel", "select");
            _this = this;
            setVLCommands(this);
            imageL = imgs;
            num = imageL.size();
            if ((num == 20) && (imgs.getName() != "/cicons.png")) {
                num = 13;
            } else if (num == 20) {
                num = 12;
            }
            int drawWidth = getWidth() - 8;

            setCursorMode(SEL_NONE);

            int imgHeight = imageL.getHeight();

            itemHeight = imgHeight + 2;

            cols = drawWidth / itemHeight;
            rows = (num + cols - 1) / cols;
            curCol = 0;
            this.setSoftNames(ResourceBundle.getString("cancel"), ResourceBundle.getString("select"));

            showCurrSmileName();
        }
        //#sijapp cond.if target is "MIDP2"#
        protected boolean pointerPressedOnUtem(int index, int x, int y) {
            int lastCol = curCol;
            curCol = x / itemHeight;
            if (curCol < 0) {
                curCol = 0;
            }
            if (curCol >= cols) {
                curCol = cols - 1;
            }
            if (lastCol != curCol) {
                showCurrSmileName();
                invalidate();
            }
            return false;
        }
        //#sijapp cond.end#
        protected void drawItemData(
                Graphics g,
                int index,
                int x1, int y1, int x2, int y2,
                int fontHeight) {
            int xa, xb;
            int startIdx = cols * index;
            int imagesCount = imageL.size();
            boolean isSelected = (index == getCurrIndex());
            xa = x1;
            for (int i = 0; i < cols; i++, startIdx++) {
                if (startIdx >= num) {
                    break;
                }
                xb = xa + itemHeight;

                if (isSelected && (i == curCol)) {
                    drawGradient(g, transformColorLight(Options.getInt(Options.OPTION_COLOR10), -16), transformColorLight(Options.getInt(Options.OPTION_COLOR10), 16), xa, y1, xb, y2);
                    g.setColor(Options.getInt(Options.OPTION_COLOR2));
                    g.setStrokeStyle(Graphics.DOTTED);
                    g.drawRect(xa, y1, itemHeight, itemHeight - 1);
                }
                if (startIdx < imagesCount) {
                    g.drawImage(imageL.elementAt(startIdx), xa + 1, y1 + 1, Graphics.TOP | Graphics.LEFT);
                }
                xa = xb;
            }
        }

        private void showCurrSmileName() {
            int selIdx = _this.getCurrIndex() * cols + curCol;
            if (selIdx >= num) {
                return;
            }
            statusID = selIdx;
            if (num == 38) {
                _this.setCaption(mipUI.getXStatusStringByImage(selIdx));
            } else if (num == 13) {
                _this.setCaption(mipUI.getStatusStringByImage(selIdx));
            } else if (num == 5) {
                _this.setCaption(mipUI.getPStatusStringByImage(selIdx));
            } else {
                _this.setCaption(mipUI.getCLIndex(selIdx));
            }
        }

        public int getItemHeight(int itemIndex) {
            return itemHeight;
        }

        protected int getSize() {
            return rows;
        }

        protected void get(int index, ListItem item) {
        }

        public void onKeyPress(VirtualList sender, int keyCode, int type) {
            if ((type == VirtualList.KEY_PRESSED) || (type == VirtualList.KEY_REPEATED)) {
                int lastCol = curCol;
                int curRow = getCurrIndex();
                int rowCount = getSize();
                try {
                    switch (getGameAction(keyCode)) {
                        case Canvas.LEFT:
                            if ((curRow == 0) && (curCol == 0)) {
                                curCol = cols - 1;
                                curRow = rowCount - 1;
                            } else if (curCol != 0) {
                                curCol--;
                            } else if (curRow != 0) {
                                curCol = cols - 1;
                                curRow--;
                            }
                            break;

                        case Canvas.RIGHT:
                            if ((curRow == rowCount - 1) && (curCol == (num - 1) % cols)) {
                                curCol = 0;
                                curRow = 0;
                            } else if (curCol < (cols - 1)) {
                                curCol++;
                            } else if (curRow <= rowCount) {
                                curCol = 0;
                                curRow++;
                            }
                            break;

                        case Canvas.UP:
                            if (curRow == 0) {
                                curRow = rowCount;
                                if (curCol > (num - 1) % cols) {
                                    curCol = (num - 1) % cols;
                                }
                            }
                            break;

                        case Canvas.DOWN:
                            if ((curCol > (num - 1) % cols) && (curRow == rowCount - 2)) {
                                curCol = (num - 1) % cols;
                            }
                            break;
                    }
                } catch (Exception e) {
                }

                setCurrentItem(curRow);

                int index = curCol + getCurrIndex() * cols;
                if (index >= num) {
                    curCol = (num - 1) % cols;
                }
                if (lastCol != curCol) {
                    invalidate();
                    showCurrSmileName();
                }
            }
        }

        public void onCursorMove(VirtualList sender) {
            showCurrSmileName();
        }

        public void onItemSelected(VirtualList sender) {
            select();
        }
    }
}
