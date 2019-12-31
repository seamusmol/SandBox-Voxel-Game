package Util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;


public class FileChooserUtil {
	
	public static File getFile(String PathStart, String fileDescription, String FileExtension)
	{
		//new File(System.getProperty("user.home") + "/Desktop")
		File newFile = null;
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.grabFocus();
		fileChooser.setDialogTitle("Import File");
		
		if(PathStart.length() == 0)
		{
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		}
		else
		{
			fileChooser.setCurrentDirectory(new File(PathStart));
		}
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setAcceptAllFileFilterUsed(false);
//		fileChooser.setFileFilter(new FileNameExtensionFilter(fileDescription, FileExtension));
		
		int result = fileChooser.showOpenDialog(fileChooser);
		if (result == JFileChooser.APPROVE_OPTION) 
		{
		    newFile = fileChooser.getSelectedFile();
		}
		return newFile;
	}
	
	public static Object[] getImageTexture(String PathStart, String fileDescription, String FileExtension)
	{
		File newFile = null;
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.grabFocus();
		fileChooser.setDialogTitle("Import Texture");
		
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
//		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new FileNameExtensionFilter("PNG(.png)", "png"));
		
		int result = fileChooser.showOpenDialog(fileChooser);
		if (result == JFileChooser.APPROVE_OPTION) 
		{
		    newFile = fileChooser.getSelectedFile();
		    BufferedImage bm = null;
			try 
			{
				AWTLoader loader = new AWTLoader();
				bm = ImageIO.read(newFile);
			    Image load = loader.load(bm, true);
			    Texture tex = new Texture2D();
			    tex.setImage(load);
			    return new Object[]{tex, newFile.getPath()};
			} 
			catch (IOException e)
			{
			}
		}
		return null;
	}
	
}
