package DrawControls;

import java.util.Vector;
import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;
import java.io.InputStream;

public class ImageList {

    private Vector items = new Vector();
    private int width = 0,  height = 0;

    //! Return image by index
    public Image elementAt(int index) {
        return ((index < 0) || (index >= items.size())) ? null : (Image) items.elementAt(index);
    }

    public void setImage(Image image, int index) {
        items.setElementAt(Image.createImage(image), index);
    }
    private String rsname;

    public String getName() {
        return rsname;
    }
    //! Return number of stored images
    public int size() {
        return items.size();
    }

    //! Return width of each image
    public int getWidth() {
        return width;
    }

    //! Return hright of each image
    public int getHeight() {
        return height;
    }

    //! Remove all images from list
    public void removeAllElements() {
        items.removeAllElements();
    }

    //! Load and divide big image to several small and store it in object
    public void load(
            String resName, //!< Name of image in resouce
            int width, //!< Width of result images
            int height, //!< Height of result images
            int count) throws IOException {
        rsname = resName;
        Image resImage = Image.createImage(resName);
        int imgHeight = resImage.getHeight();
        int imgWidth = resImage.getWidth();

        if (width == -1) {
            width = imgHeight;
        }
        if (height == -1) {
            height = imgHeight;
        }
        this.width = width;
        this.height = height;

        for (int y = 0; y < imgHeight; y += height) {
            for (int x = 0; x < imgWidth; x += width) {
                Image newImage;
                //#sijapp cond.if target is "MIDP2" | target is "MOTOROLA" | target is "SIEMENS2"#
                newImage = Image.createImage(Image.createImage(resImage, x, y, width, height, Sprite.TRANS_NONE));
                //#sijapp cond.else#
                newImage = Image.createImage(width, height);
                newImage.getGraphics().drawImage(resImage, -x, -y, Graphics.TOP | Graphics.LEFT);
                //#sijapp cond.end#
                Image imImage = Image.createImage(newImage);
                items.addElement(imImage);
            }
        }
    }

    public void load(
            InputStream stream, //!< Name of image in resouce
            int width, //!< Width of result images
            int height, //!< Height of result images
            int count) throws IOException {
        Image resImage = Image.createImage(stream);
        int imgHeight = resImage.getHeight();
        int imgWidth = resImage.getWidth();

        if (width == -1) {
            width = imgHeight;
        }
        if (height == -1) {
            height = imgHeight;
        }
        this.width = width;
        this.height = height;

        for (int y = 0; y < imgHeight; y += height) {
            for (int x = 0; x < imgWidth; x += width) {
                Image newImage;
                newImage = Image.createImage(Image.createImage(resImage, x, y, width, height, Sprite.TRANS_NONE));
                Image imImage = Image.createImage(newImage);
                items.addElement(imImage);
            }
        }
    }

    public void load(String firstLine, String extention, int from, int to)
            throws IOException {
        Image image = null;

        for (int i = from; i <= to; i++) {
            image = Image.createImage(firstLine + Integer.toString(i) + "." + extention);
            items.addElement(image);
        }
        if (image != null) {
            height = image.getHeight();
            width = image.getWidth();
        } else {
            height = width = 0;
        }
    }
}