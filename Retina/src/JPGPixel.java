import java.awt.*;
import java.awt.event.*;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.awt.image.*;
import com.sun.image.codec.jpeg.*;

class JPGPixel extends Frame
{
	private int R[][];
	private int G[][];
	private int B[][];
	private int L[][];
	private int pixel[];
	public  int imgWidth;
	public  int imgHeight;

	public JPGPixel(){}

	public void ToGetJPGPixel(String jpgPath)
	{
		Image image=Toolkit.getDefaultToolkit().getImage(jpgPath);
		MediaTracker mt=new MediaTracker(this);
		mt.addImage(image,1);
		try
		{	
			if(!mt.waitForAll(10000)){
			System.out.println("Load error!!");}
		}catch(InterruptedException e){System.err.println(e);}

		imgWidth=image.getWidth(JPGPixel.this);
		imgHeight=image.getHeight(JPGPixel.this);     

		pixel=new int[imgWidth*imgHeight];
		R=new int[imgHeight][imgWidth];
		G=new int[imgHeight][imgWidth];
		B=new int[imgHeight][imgWidth];
		L=new int[imgHeight][imgWidth];
		try
		{
			PixelGrabber pg=new PixelGrabber(image,0,0,imgWidth,imgHeight,pixel,0,imgWidth);
			pg.grabPixels();
		}catch(InterruptedException e){}

		for(int i=0;i<imgHeight;i++)
			for(int j=0;j<imgWidth;j++)
			{
				L[i][j]=(0xff000000 & pixel[(i*imgWidth)+j]) >>24;
				R[i][j]=(0xff0000 & pixel[(i*imgWidth)+j]) >>16;
				G[i][j]=(0xff00 & pixel[(i*imgWidth)+j]) >>8;
				B[i][j]=(0xff & pixel[(i*imgWidth)+j]);
			}	
	}//ToGetJPGPixel

	public int[][] getR(){return R;}
	public int[][] getG(){return G;}
	public int[][] getB(){return B;}
	public int[][] getL(){return L;}

	public void setR(int[][] srR,int Height,int Width)
	{
		R =new int[Height][Width];
		for(int i=0;i<Height;i++)
			for(int j=0;j<Width;j++)
				R[i][j]=srR[i][j];  	
	}//setR

	public void setG(int[][] srG,int Height,int Width)
	{
		G =new int[Height][Width];
		for(int i=0;i<Height;i++)
			for(int j=0;j<Width;j++)
				G[i][j]=srG[i][j];  
	}//setG

	public void setB(int[][] srB,int Height,int Width)
	{
		B =new int[Height][Width];
		for(int i=0;i<Height;i++)
			for(int j=0;j<Width;j++)
				B[i][j]=srB[i][j]; 
	}//setB

	public void setL(int[][] srL,int Height,int Width)
	{
		L =new int[Height][Width];
		for(int i=0;i<Height;i++)
			for(int j=0;j<Width;j++)
			L[i][j]=srL[i][j];  
	}//setL

	public void saveRGBPixel(String savePath,int Height,int Width)
	{
		FileOutputStream fileOut;
		pixel=new int[Width*Height];
		File file = new File(savePath);  
		BufferedImage bf=new BufferedImage(Width, Height,BufferedImage.TYPE_INT_RGB); 

		for(int i=0;i<Height;i++)
			for(int j=0;j<Width;j++)
				pixel[(i*Width)+j]=(L[i][j]<<24) | (R[i][j]<<16) | (G[i][j]<<8) | B[i][j];

		bf.setRGB(0,0,Width, Height,pixel,0,Width);  

		try 
		{
			fileOut = new FileOutputStream(file);
			//將檔案存成JPEG格式
			JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(fileOut);
			jpeg.encode(bf);
			fileOut.close();
		}catch (Exception e){}
	}//saveRGBPixel

	public void saveGrayPixel(String savePath,int Height ,int Width)
	{
		FileOutputStream fileOut;
		pixel=new int[Width*Height];
		int temp;
		File file = new File(savePath);  
		BufferedImage bf=new BufferedImage(Width, Height,BufferedImage.TYPE_INT_RGB); 

		for(int i=0;i<Height;i++)
			for(int j=0;j<Width;j++)
			{
				temp=(R[i][j]+G[i][j]+B[i][j])/3;
				pixel[(i*Width)+j]=0xff000000| (temp<<16) | (temp<<8) | temp;
			}	

		bf.setRGB(0,0,Width, Height,pixel,0,Width); 
		try 
		{
			fileOut = new FileOutputStream(file);
			//將檔案存成JPEG格式
			JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(fileOut);
			jpeg.encode(bf);
			fileOut.close();
		}catch (Exception e){}	   
	}//saveGrayPixel	

	public void saveLPixel(String savePath,int Height ,int Width)
	{
		FileOutputStream fileOut;
		int temp;
		pixel=new int[Width*Height];
		File file = new File(savePath);  
		BufferedImage bf=new BufferedImage(Width, Height,BufferedImage.TYPE_INT_RGB); 

		for(int i=0;i<Height;i++)
			for(int j=0;j<Width;j++)
				pixel[(i*Width)+j]= 0xff000000 | L[i][j]<<16 | L[i][j]<<8 | L[i][j]; 
		bf.setRGB(0,0,Width, Height,pixel,0,Width);  

		try 
		{
			fileOut = new FileOutputStream(file);
			//將檔案存成JPEG格式
			JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(fileOut);
			jpeg.encode(bf);
			fileOut.close();
		}catch (Exception e){}	   
	}//saveLPixel
}//JPGPixel