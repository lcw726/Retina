import java.io.*;
import java.lang.Math;
import java.lang.Object;
import java.text.DecimalFormat;
import java.util.Arrays;

class  Test
{
	static double Y[][];
	static double YY[][];
	static double YYY[][];
	static double ZZ[][];
	static double XX[][];
	static double gray[][];
	static int range=81; //LocalThreshold's window size
	static int av_range=3;
	static double r=1.0;
	static int LT=0;
	static int HT=150;
	static int imgWidth,imgHeight;

	public static void main(String[] args)
	{
		String[] f =new File("img\\").list();
		for(int i=0;i<f.length;i++){		
		readjpg_marker("img\\"+f[i]);
			Y=nor(Y,imgHeight,imgWidth);
			//outputjpg(Y,"out.jpg",imgHeight,imgWidth);//輸出灰階影像, (參數：輸出路徑,影像高,影像寬)
			outputRGB("img\\"+f[i],"img2\\"+f[i]); //輸出RGB影像, (參數：原始影像,輸出路徑)
		
		readjpg("img2\\"+f[i]);
		YY=nor(YY,imgHeight,imgWidth);
		outputjpg(YY,"outG_grey_outRGB"+i+".bmp",imgHeight,imgWidth);

        double totalArray[][] = new double[4][2];//[0~1][0~1]
        double cross[][] = new double [imgHeight][imgWidth]; //bright
        double cross2[][] = new double [imgHeight][imgWidth]; //dark
        double mode_1[][] = new double [imgHeight][imgWidth];
        int t=0;

		for(int a=0;a<imgHeight;a++){    //0為Background
			for(int b=0;b<imgWidth;b++){

				if(gray[a][b]<=20){
					gray[a][b]=0;
					cross[a][b]=0;
					cross2[a][b]=0;
				}else{
					gray[a][b]=255;
					cross[a][b]=255;
					cross2[a][b]=255;
					t++;
				}
			}
		}

        for(int a=0;a<imgHeight;a++){
			for(int b=0;b<imgWidth;b++){

				if(gray[a][b]!=0){

				int countH=0;
				int countV=0;

				totalArray[0][0]=0.0;
				totalArray[0][1]=0.0;
				totalArray[1][0]=0.0;
				totalArray[1][1]=0.0;
				totalArray[2][0]=0.0;
				totalArray[2][1]=0.0;
				totalArray[3][0]=0.0;
				totalArray[3][1]=0.0;

				for(int n=0;n<range;n++){  //horizental
						if((b-(range/2)+n<0)||(b-(range/2)+n>=imgWidth)||(gray[a][b-(range/2)+n]==0)){
							totalArray[0][0]=totalArray[0][0]+0.0;
						}else{
							totalArray[0][0]=totalArray[0][0]+YY[a][b-(range/2)+n];
							countH++;
						}
				}

				for(int m=0;m<range;m++){ //vertical
						if((a-(range/2)+m<0)||(a-(range/2)+m>=imgHeight)||(gray[a-(range/2)+m][b]==0)){
							totalArray[0][1]=totalArray[0][1]+0.0;
						}else{
							totalArray[0][1]=totalArray[0][1]+YY[a-(range/2)+m][b];
							countV++;
						}
				}

        		//difference
        		totalArray[1][0]=(totalArray[0][0]+totalArray[0][1]-YY[a][b])/(countH+countV-1);//average
        		totalArray[1][1]=YY[a][b];

        		//cross[a][b]=Math.pow(Math.abs(totalArray[1][0]-totalArray[1][1]),r);
        		//cross[a][b]=Math.abs(totalArray[1][0]-totalArray[1][1]);
        		if(totalArray[1][1]>totalArray[1][0])
        			cross[a][b]=Math.pow(totalArray[1][1]-totalArray[1][0],r);
        		else
        			cross[a][b]=0;

				//standard error
				totalArray[2][0]=totalArray[2][0]+cross[a][b];//total

				}
			}
        }

        cross=nor3(cross,imgHeight,imgWidth);
		outputjpg(cross,"outG_crossOrigin_"+i+".bmp",imgHeight,imgWidth);

        totalArray[2][1]=totalArray[2][0]/t; //average

        for(int a=0;a<imgHeight;a++){
			for(int b=0;b<imgWidth;b++){
				if(gray[a][b]!=0){
					totalArray[3][0]=totalArray[3][0]+Math.pow(totalArray[2][1]-cross[a][b],2);
				}
			}
        }

        totalArray[3][1]=Math.sqrt(totalArray[3][0]/t);//std. error

        double totalLight=0.0; //light spot's luminance
        int v=0;

        for(int a=0;a<imgHeight;a++){
			for(int b=0;b<imgWidth;b++){

				if(cross[a][b]>totalArray[2][1]+2*totalArray[3][1]){
        			cross2[a][b]=255;
        			totalLight=totalLight+cross[a][b];
        			v++;
				}else{
        			cross2[a][b]=0;
				}

			}
        }

		outputjpg(cross2,"outG_cross_"+i+".bmp",imgHeight,imgWidth);

        double avgLight=totalLight/v; //global avg. luminance
        System.out.println(avgLight);

        //area label******************************************************************/
        //double>>short
	    short objectimg[][] = new short[imgHeight][imgWidth];
		short Z[][] = new short[imgHeight][imgWidth];

		for(int a=0;a<imgHeight;a++)
			for(int b=0;b<imgWidth;b++)
				Z[a][b]=(short)cross2[a][b];

		//label
        Marker m = new Marker(Z,imgHeight,imgWidth);

        for(int a=0;a<imgHeight;a++)
			for(int b=0;b<imgWidth;b++)
            	objectimg[a][b]=m.numberimage[a][b];//number map

		int x[] = new int[m.objectamount];//抓編碼個數(物件)(編到幾號去了)
		double y[] = new double[m.objectamount];//local label's total luminance

	    //算各編碼的個數(面積)
		for(int a=0;a<m.objectamount;a++){
			x[a]=0; //初始值
			y[a]=0;
			for(int uu=0;uu<imgHeight;uu++){
				for(int vv=0;vv<imgWidth;vv++){

	            if(objectimg[uu][vv]==a){
	              	x[a]++;
	              	y[a]=y[a]+cross[uu][vv];
	            }

				}
			}
		}

		//local label's avg. luminance
		double labelight[] = new double[m.objectamount];

		for(int a=0;a<m.objectamount;a++){
			labelight[a]=0;
			labelight[a]=y[a]/x[a];
		}

		for(int a=0;a<imgHeight;a++){
			for(int b=0;b<imgWidth;b++){
				if(cross2[a][b]==255){
					if(x[objectimg[a][b]]<=300 || labelight[objectimg[a][b]]<avgLight-30)
						cross2[a][b]=0;
				}
			}
		}



		//cross=nor4(cross,imgHeight,imgWidth,0.7);
		//cross=nor3(cross,imgHeight,imgWidth);
		outputjpg(cross2,"outG_avg_"+i+".bmp",imgHeight,imgWidth);

/*******dark**************************************************************************/
//cross < average
//subtract light area (cross2)

		//YY average filter
		double B[][] = new double[av_range][av_range];
		double AVYY[][] = new double[imgHeight][imgWidth];

		for(int a=0;a<imgHeight;a++)
			for(int b=0;b<imgWidth;b++)
				AVYY[a][b]=0;

        for(int a=0;a<imgHeight;a++){
			for(int b=0;b<imgWidth;b++){
			  if(gray[a][b]!=0){
        		for(int mm=0;mm<av_range;mm++){ //將原始影像(original[][])，以(a,b)為主，往外擴充r*r的pixel
					for(int n=0;n<av_range;n++){
						if((a-(av_range/2)+mm<0)||(a-(av_range/2)+mm>=imgHeight)||(b-(av_range/2)+n<0)||(b-(av_range/2)+n>=imgWidth))
							B[mm][n]=0;
						else
							B[mm][n]=YY[a-(av_range/2)+mm][b-(av_range/2)+n];
					}

        		}

        		double total=0.0;

        		for(int mm=0;mm<av_range;mm++)
					for(int n=0;n<av_range;n++)
						total=total+B[mm][n];

				AVYY[a][b]=total/(av_range*av_range);

			  }
	    	}
	    }
        		outputjpg(AVYY,"outG_3x3avg_"+i+".bmp",imgHeight,imgWidth);

        for(int a=0;a<imgHeight;a++){
			for(int b=0;b<imgWidth;b++){

				if(gray[a][b]!=0){

				int countH=0;
				int countV=0;

				totalArray[0][0]=0.0;
				totalArray[0][1]=0.0;
				totalArray[1][0]=0.0;
				totalArray[1][1]=0.0;
				totalArray[2][0]=0.0;
				totalArray[2][1]=0.0;
				totalArray[3][0]=0.0;
				totalArray[3][1]=0.0;

				for(int n=0;n<range;n++){  //horizental
						if((b-(range/2)+n<0)||(b-(range/2)+n>=imgWidth)||(gray[a][b-(range/2)+n]==0)||(cross2[a][b-(range/2)+n]==255)){
							totalArray[0][0]=totalArray[0][0]+0.0;
						}else{
							totalArray[0][0]=totalArray[0][0]+AVYY[a][b-(range/2)+n];
							countH++;
						}
				}

				for(int mm=0;mm<range;mm++){ //vertical
						if((a-(range/2)+mm<0)||(a-(range/2)+mm>=imgHeight)||(gray[a-(range/2)+mm][b]==0)||(cross2[a-(range/2)+mm][b]==255)){
							totalArray[0][1]=totalArray[0][1]+0.0;
						}else{
							totalArray[0][1]=totalArray[0][1]+AVYY[a-(range/2)+mm][b];
							countV++;
						}
				}

        		//difference
        		totalArray[1][0]=(totalArray[0][0]+totalArray[0][1]-AVYY[a][b])/(countH+countV-1);//average
        		totalArray[1][1]=AVYY[a][b];

        		if(totalArray[1][1]<totalArray[1][0])
        			cross[a][b]=Math.pow(totalArray[1][0]-totalArray[1][1],r);
        		else
        			cross[a][b]=0;

				//standard error
				totalArray[2][0]=totalArray[2][0]+cross[a][b];//total

				}
			}
        }
                cross=nor3(cross,imgHeight,imgWidth);
        		outputjpg(cross,"outG_dark_"+i+".bmp",imgHeight,imgWidth);

                //runlength
                short ZZ[][] = new short[imgHeight][imgWidth];
				double XX[][] = new double[imgHeight][imgWidth];

				for(int a=0;a<imgHeight;a++)
					for(int b=0;b<imgWidth;b++)
						ZZ[a][b]=(short)cross[a][b];

				Runlength run = new Runlength();
				run.enhance(imgHeight,imgWidth,ZZ,9);

				for(int a=0;a<imgHeight;a++){
					for(int b=0;b<imgWidth;b++){
						ZZ[a][b]=run.Runlengthimage[a][b];
						XX[a][b]=(double)ZZ[a][b];
					}
				}

				XX=nor3(XX,imgHeight,imgWidth);

        		outputjpg(XX,"outG_dark_runlength"+i+".bmp",imgHeight,imgWidth);

        		//cross=nor4(cross,imgHeight,imgWidth,0.8);

        		//runlength
        		for(int a=0;a<imgHeight;a++)
					for(int b=0;b<imgWidth;b++)
						ZZ[a][b]=(short)cross[a][b];

        		run.enhance(imgHeight,imgWidth,ZZ,9);

				for(int a=0;a<imgHeight;a++){
					for(int b=0;b<imgWidth;b++){
						ZZ[a][b]=run.Runlengthimage[a][b];
						XX[a][b]=(double)ZZ[a][b];
					}
				}

				XX=nor2(XX,imgHeight,imgWidth,1.0);/////////T=150
        		outputjpg(XX,"outG_dark_gamma"+i+".bmp",imgHeight,imgWidth);

        		/*//gaussain
   				Gaussian g = new Gaussian();
				XX=g.gg(XX,imgHeight,imgWidth,3,0.5);
				outputjpg(XX,"out_gaussian.jpg",imgHeight,imgWidth);//輸出高斯濾波影像*/

				////////////////////////////////////////
				totalArray[2][1]=totalArray[2][0]/t; //average

        for(int a=0;a<imgHeight;a++){
			for(int b=0;b<imgWidth;b++){
				if(gray[a][b]!=0){
					totalArray[3][0]=totalArray[3][0]+Math.pow(totalArray[2][1]-XX[a][b],2);
				}
			}
        }

        totalArray[3][1]=Math.sqrt(totalArray[3][0]/t);//dark spot std. error

        totalLight=0.0; //dark spot's luminance
        v=0;

        for(int a=0;a<imgHeight;a++){
			for(int b=0;b<imgWidth;b++){

				if(XX[a][b]>totalArray[2][1]+2*totalArray[3][1]){
        			cross2[a][b]=255;
        			totalLight=totalLight+XX[a][b];
        			v++;
				}else{
        			cross2[a][b]=0;
				}

			}
        }

		outputjpg(cross2,"outG_cross_dark"+i+".bmp",imgHeight,imgWidth);

        avgLight=totalLight/v; //global avg. luminance
        System.out.println(avgLight);

        //area label******************************************************************/
        //double>>short
	    //short objectimg[][] = new short[imgHeight][imgWidth];
		//short Z[][] = new short[imgHeight][imgWidth];

		for(int a=0;a<imgHeight;a++)
			for(int b=0;b<imgWidth;b++)
				Z[a][b]=(short)cross2[a][b];

		//label
        Marker ma = new Marker(Z,imgHeight,imgWidth);

        for(int a=0;a<imgHeight;a++)
			for(int b=0;b<imgWidth;b++)
            	objectimg[a][b]=ma.numberimage[a][b];//number map

		int xd[] = new int[ma.objectamount];//抓編碼個數(物件)(編到幾號去了)
		double yd[] = new double[ma.objectamount];//local label's total luminance

	    //算各編碼的個數(面積)
		for(int a=0;a<ma.objectamount;a++){
			xd[a]=0; //初始值
			yd[a]=0;
			for(int uu=0;uu<imgHeight;uu++){
				for(int vv=0;vv<imgWidth;vv++){

	            if(objectimg[uu][vv]==a){
	              	xd[a]++;
	              	yd[a]=yd[a]+XX[uu][vv];
	            }

				}
			}
		}

		//local label's avg. luminance
		double labedark[] = new double[ma.objectamount];

		for(int a=0;a<ma.objectamount;a++){
			labedark[a]=0;
			labedark[a]=yd[a]/xd[a];
		}

		for(int a=0;a<imgHeight;a++){
			for(int b=0;b<imgWidth;b++){
				if(cross2[a][b]==255){
					if((xd[objectimg[a][b]]<=10))// && labedark[objectimg[a][b]]>avgLight)
						cross2[a][b]=0;
				}
			}
		}

        outputjpg(cross2,"outG_dark_binary"+i+".bmp",imgHeight,imgWidth);

        cross2=shrink(cross2,gray,imgHeight,imgWidth,5);
		outputjpg(cross2,"outG_dark_shrink"+i+".bmp",imgHeight,imgWidth);

		//抓邊緣
		//outputRGB_R("outG_dark_shrink"+i+".bmp","out"+i+".bmp",cross2);

		//label/////////////////////////////////////////////////////
		for(int a=0;a<imgHeight;a++)
			for(int b=0;b<imgWidth;b++)
				Z[a][b]=(short)cross2[a][b];

       ma = new Marker(Z,imgHeight,imgWidth);

        for(int a=0;a<imgHeight;a++)
			for(int b=0;b<imgWidth;b++)
            	objectimg[a][b]=ma.numberimage[a][b];//number map

		xd = new int[ma.objectamount];//抓編碼個數(物件)(編到幾號去了)
		int circum[] = new int[ma.objectamount];
		double check[] = new double[ma.objectamount];
		double check_d[] = new double[ma.objectamount];
		int[][] loc_x=new int[ma.objectamount][2000];
		int[][] loc_y=new int[ma.objectamount][2000];

	    //算各編碼的個數(面積)
		for(int a=1;a<ma.objectamount;a++){
			xd[a]=0; //初始值
			circum[a]=0;

			for(int uu=1;uu<imgHeight-1;uu++){
				for(int vv=1;vv<imgWidth-1;vv++){

	            if(objectimg[uu][vv]==a)
	              	xd[a]++;

	            if(objectimg[uu-1][vv]==0||objectimg[uu+1][vv]==0||
					objectimg[uu][vv+1]==0||objectimg[uu][vv-1]==0){

						if(objectimg[uu][vv]==a)
						circum[a]++; //circumference
//						if(objectimg[i][j]!=0)
//						G1[i][j]=(0xff000000 | (0 << 16) | (255 << 8) | 0);
                        loc_x[a][circum[a]]=uu;
        			    loc_y[a][circum[a]]=vv;
				}

				}

			double diameter=0,max_diameter=0;

	     	for (int r=0; r<circum[a]-1; r++) {
        		for (int s=r+1; s<circum[a];s++) {
        			double temp=0;
        			temp=Math.pow((loc_x[a][r]-loc_x[a][s]),2)+Math.pow((loc_y[a][r]-loc_y[a][s]),2);
        			diameter=Math.pow(temp,(0.5));//計算最長直徑推論是否和圓接近
        			//System.out.println("dis"+dis);
        			if(diameter>max_diameter){
        				max_diameter=diameter;
        			}
        		}
           	}

			double temp_area_d=0;
        	temp_area_d=Math.pow((max_diameter/2),2)*Math.PI;
        	check_d[a]=xd[a]/temp_area_d;//計算是否和圓接近

			double temp_area=0;//計算面積是否和圓接近
        	temp_area=Math.pow((circum[a]/2/Math.PI),2)*Math.PI;
        	//System.out.println("temp="+temp_area);
			check[a]=xd[a]/temp_area;
			//System.out.println("circum_"+a+"="+circum[a]+" x="+x[a]+" check="+check[a]);

        	    	for(int h=1;h<imgHeight-1;h++){
						for(int w=1;w<imgWidth-1;w++){
						if(objectimg[h][w]==a){
						 if(check_d[a]<0.2)
						 	cross2[h][w]=0;
						 else
						 	cross2[h][w]=255;
						}
						}
        	    	}

			}

			outputjpg(cross2,"outG_dark_label"+i+".bmp",imgHeight,imgWidth);
			}
		System.out.println("finish");		
		}
	}//main

	public static void readjpg_marker(String InputFileName)
	{
		int R[][],G[][],B[][],couR=0,couG=0,couB=0;
		JPGPixel p=new JPGPixel();//抓jpg影像，所需的物件
		p.ToGetJPGPixel(InputFileName);
		imgHeight=p.imgHeight;
		imgWidth=p.imgWidth;
		R=new int[imgHeight][imgWidth];
		G=new int[imgHeight][imgWidth];
		B=new int[imgHeight][imgWidth];
		Y=new double[imgHeight][imgWidth];
		YY=new double[imgHeight][imgWidth];
		ZZ=new double[imgHeight][imgWidth];
		XX=new double[imgHeight][imgWidth];
		R=p.getR();
		G=p.getG();
		B=p.getB();
		for(int i=0;i<imgHeight;i++){//灰階化
			for(int j=0;j<imgWidth;j++){
				Y[i][j]=(double)(0.299*R[i][j]+0.587*G[i][j]+0.114*B[i][j]);

                YY[i][j]=Y[i][j];//複製並黑白化(retinal為白)

				if(YY[i][j]<=30)
					YY[i][j]=0;
				else
					YY[i][j]=255;

		    }
	    }
        //矩陣強制轉型,因為Marker矩陣型態為為short
	    short objectimg[][] = new short[imgHeight][imgWidth];
		short Z[][] = new short[imgHeight][imgWidth];
		for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){
				Z[i][j]=(short)YY[i][j]; //double強制轉short
		    }
		}
		//標記
        Marker m = new Marker(Z,imgHeight,imgWidth);

        for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){
            objectimg[i][j]=m.numberimage[i][j];//抓編碼後的矩陣(number map)
			}
		}

		int x[] = new int[m.objectamount];//抓編碼個數(物件)(編到幾號去了)
		int temp = x[1];
		int MAX_maker=0;

    //算各編碼的個數(面積)
	for(int a=1;a<m.objectamount;a++){
		x[a]=0; //初始值
		for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){

            if(objectimg[i][j]==a)
              x[a]++;

			}
		}
			if(temp<x[a]){//比大小
			temp=x[a];
	        MAX_maker=a;//個數最多的號碼
			}
	}

	 	for(int i=0;i<imgHeight;i++){//周圍設成0(BACKGROUND)
			for(int j=0;j<imgWidth;j++){
			 if(objectimg[i][j]!=MAX_maker)
			 	Y[i][j]=0;

             ZZ[i][j]=Y[i][j];       //但是retinal邊邊有黏碎屑,所以再做一次label 
			 if(ZZ[i][j]>=230){      //取threshold
			 	ZZ[i][j]=255;
			 }else if(ZZ[i][j]>0&&ZZ[i][j]<230){
			 	ZZ[i][j]=50;
			 }
		    }
	    }

	    //矩陣強制轉型2
	    short objectimg2[][] = new short[imgHeight][imgWidth];
		short X[][] = new short[imgHeight][imgWidth];
		for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){
				X[i][j]=(short)ZZ[i][j];
		    }
		}
		//標記2
        Marker mm = new Marker(X,imgHeight,imgWidth);

        for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){
            objectimg2[i][j]=mm.numberimage[i][j];//抓編碼後的矩陣2
			}
		}

		int y[] = new int[mm.objectamount];//抓編碼個數(物件)2
		int temp2 = y[1];
		int MAX_maker2=0;
    //算各編碼的個數(面積)2
	for(int a=1;a<mm.objectamount;a++){
		y[a]=0;
		for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){

            if(objectimg2[i][j]==a)
              y[a]++;

			}
		}
			if(temp2<y[a]){//比大小
			temp2=y[a];
	        MAX_maker2=a;//個數最多的號碼
			}
	}

       int new_num=0;

	    for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){  //pixel不屬於最大塊且八個方向有pixel為黑,設此pixel為黑
			 if((objectimg2[i][j]!=0)&&(objectimg2[i][j]!=MAX_maker2)&&(objectimg2[i-1][j-1]==0
			 	               ||objectimg2[i-1][j]==0
			 	               ||objectimg2[i-1][j+1]==0
			 	               ||objectimg2[i][j-1]==0
			 	               ||objectimg2[i][j+1]==0
			 	               ||objectimg2[i+1][j-1]==0
			 	               ||objectimg2[i+1][j]==0
			 	               ||objectimg2[i+1][j+1]==0))
			 	            objectimg2[i][j]=0;
			}
	    }

	    for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){

			 if(objectimg2[i][j]==0)  //pixel為0的覆蓋到原圖
			 	Y[i][j]=0;

			}
	    }
	   /*
	   for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){

			 if((X[i][j]==255)&&(Y[i-1][j-1]==0
			 	               ||Y[i-1][j]==0
			 	               ||Y[i-1][j+1]==0
			 	               ||Y[i][j-1]==0
			 	               ||Y[i][j+1]==0
			 	               ||Y[i+1][j-1]==0
			 	               ||Y[i+1][j]==0
			 	               ||Y[i+1][j+1]==0))
			 	            Y[i][j]=0;
			}
	   }*/
	}

	public static void outputRGB(String InputFileName,String outpath)
	{
		int R[][],G[][],B[][];
		JPGPixel p=new JPGPixel();//抓jpg影像，所需的物件
		p.ToGetJPGPixel(InputFileName);
		imgHeight=p.imgHeight;
		imgWidth=p.imgWidth;
		R=new int[imgHeight][imgWidth];
		G=new int[imgHeight][imgWidth];
		B=new int[imgHeight][imgWidth];
		//Y=new double[imgHeight][imgWidth];
		R=p.getR();
		G=p.getG();
		B=p.getB();
		for(int i=0;i<imgHeight;i++)
			for(int j=0;j<imgWidth;j++)
				if(Y[i][j]==0) //RGB圖
				{
					R[i][j]=0;
					G[i][j]=0;
					B[i][j]=0;
				}
		p.setR(R,imgHeight,imgWidth);
		p.setG(G,imgHeight,imgWidth);
		p.setB(B,imgHeight,imgWidth);
		p.saveRGBPixel(outpath,imgHeight,imgWidth);
	}

	public static double[][] nor(double input[][],int Hei,int Wid)//正規化
	{
		double max=0,min=0;
		double out[][]=new double[Hei][Wid];
		for(int i=0;i<Hei;i++)
			for(int j=0;j<Wid;j++)
			{
				if(i==0 && j==0)
				{
					max=input[i][j];
					min=input[i][j];
				}
				else
				{
					if(input[i][j]>=max)
						max=input[i][j];
					else if(input[i][j]<min)
						min=input[i][j];
				}
			}

		for(int i=0;i<Hei;i++)
			for(int j=0;j<Wid;j++)
				out[i][j]=(double)255*((input[i][j]-min)/(double)(max-min+0.1));

		return out;
	}

	public static double[][] nor2(double input[][],int Hei,int Wid,double r)//gamma
	{

		double out[][]=new double[Hei][Wid];

		for(int i=0;i<Hei;i++)
			for(int j=0;j<Wid;j++)
				out[i][j]=input[i][j];

		for(int i=0;i<Hei;i++){
			for(int j=0;j<Wid;j++){

				if(gray[i][j]!=0){
					if(input[i][j]>=HT)
						out[i][j]=255;
				    else
				    	out[i][j]=(double)255*Math.pow(((input[i][j]-LT)/(double)(HT-LT)),r); //gamma
				}

			}
		}
		return out;
	}

	public static double[][] nor3(double input[][],int Hei,int Wid)//NEW正規化(background不算)
	{

		double out[][]=new double[Hei][Wid];

        double temp=input[Hei/2][Wid/2];
        double temp2=input[Hei/2][Wid/2];

		for(int i=0;i<Hei;i++){
			for(int j=0;j<Wid;j++){
        		if(gray[i][j]!=0){

       	   		temp = Math.max(temp, input[i][j]);
       	   		temp2 = Math.min(temp2, input[i][j]);
				}
			}
		}

       	double max = temp;
       	double min = temp2;

		for(int i=0;i<Hei;i++){
			for(int j=0;j<Wid;j++){
				if(gray[i][j]!=0)
					out[i][j]=(double)255*((input[i][j]-min)/(double)(max-min));
				else
					out[i][j]=0;
			}
		}
		return out;
	}

	public static double[][] nor4(double input[][],int Hei,int Wid,double r)//gamma
	{

		double out[][]=new double[Hei][Wid];

        double temp=input[Hei/2][Wid/2];
        double temp2=input[Hei/2][Wid/2];

		for(int i=0;i<Hei;i++){
			for(int j=0;j<Wid;j++){
        		if(gray[i][j]!=0){

       	   		temp = Math.max(temp, input[i][j]);
       	   		temp2 = Math.min(temp2, input[i][j]);
				}
			}
		}

       	double max = temp;
       	double min = temp2;

		for(int i=0;i<Hei;i++){
			for(int j=0;j<Wid;j++){
				if(gray[i][j]!=0)
					out[i][j]=(double)255*Math.pow(((input[i][j]-min)/(double)(max-min)),r);//gamma
				else
					out[i][j]=0;
			}
		}
		return out;
	}

	public static double[][] nor5(double input[][],int Hei,int Wid)//equivalent area
	{
		int length=0;

		for(int i=0;i<Hei;i++){
			for(int j=0;j<Wid;j++){
				if(gray[i][j]!=0){
       	   			length++;
				}
			}
		}
		System.out.println(length);

		double origin[]=new double[length];
		double unSort[]=new double[length];
		double Sort[]=new double[length];
		int position[]=new int[length];
		double out[][]=new double[Hei][Wid];
		int a=0;
		int value=0;

		/*for(int i=0;i<Hei;i++)
			for(int j=0;j<Wid;j++)
				out[i][j]=input[i][j];*/

		for(int i=0;i<Hei;i++){
			for(int j=0;j<Wid;j++){
				if(gray[i][j]!=0){
       	   			origin[a] = input[i][j];
       	   			unSort[a] = input[i][j];
       	   			Sort[a] = input[i][j];
       	   			a++;
				}
			}
		}

		//int dis=(int)length/255;
		//System.out.println(dis);

        Arrays.sort(Sort);

/*		for(int x=0;x<length;x++){
			for(int y=0;y<length;y++){
					if(Sort[x]==unSort[y]){
						position[x]=y;
						unSort[y]=-1;
						break;
					}

			}
		}


		for(int x=0;x<length;x++){
			if((x%dis==0) &&(x!=0)){
				origin[position[x]]=value;
				value++;
				System.out.print(value+" ");
			}else{
				origin[position[x]]=value;
			}

		}

		int b=0;

		for(int i=0;i<Hei;i++){
			for(int j=0;j<Wid;j++){
				if(gray[i][j]!=0){
					out[i][j]=origin[b];
					b++;
				}
			}
		}*/

		int pos=(int)0.9*length;
		double thres=Sort[pos];

		for(int i=0;i<Hei;i++){
			for(int j=0;j<Wid;j++){
				if(gray[i][j]!=0){
					if(input[i][j]>thres)
						out[i][j]=255;

				}
			}
		}

        double temp=input[Hei/2][Wid/2];
        double temp2=input[Hei/2][Wid/2];

		for(int i=0;i<Hei;i++){
			for(int j=0;j<Wid;j++){
        		if(gray[i][j]!=0 &&out[i][j]!=255){

       	   		temp = Math.max(temp, input[i][j]);
       	   		temp2 = Math.min(temp2, input[i][j]);
				}
			}
		}

       	double max = temp;
       	double min = temp2;

		for(int i=0;i<Hei;i++){
			for(int j=0;j<Wid;j++){
				if(gray[i][j]!=0  &&out[i][j]!=255)
					out[i][j]=(double)255*((input[i][j]-min)/(double)(max-min));
				else
					out[i][j]=0;
			}
		}

		return out;
	}

	public static void sd(double input[][],int Hei,int Wid)//標準差
	{
		double s=0,t=0;
		double ii=(double)1/2;

		for(int i=0;i<Hei;i++)
			for(int j=0;j<Wid;j++)
				s=s+input[i][j];

		double u=s/(Hei*Wid);

		for(int i=0;i<Hei;i++)
			for(int j=0;j<Wid;j++)
				t=t+(input[i][j]-u)*(input[i][j]-u);

		double v=Math.sqrt(t/(Hei*Wid));

        System.out.println(u);
        System.out.println(v);
	}

	public static double[][] shrink(double input[][],double mask[][],int Hei,int Wid,int pixel)//正規化
	{

		double out[][]=new double[Hei][Wid];

        //內縮,四個方向
		//→
		for(int a=0;a<imgHeight;a++){
			for(int b=0;b<imgWidth;b++){
				if(mask[a][b]==255){
					for(int xx=0;xx<pixel;xx++)
						mask[a][b+xx]=0;
				break;
				}
			}
		}

		//←
		for(int a=0;a<imgHeight;a++){
			for(int b=imgWidth-1;b>=0;b--){
				if(mask[a][b]==255){
					for(int xx=0;xx<pixel;xx++)
						mask[a][b-xx]=0;
				break;
				}
			}
		}

		//↓
		for(int b=0;b<imgWidth;b++){
			for(int a=0;a<imgHeight;a++){
				if(mask[a][b]==255){
					for(int xx=0;xx<pixel;xx++)
						mask[a+xx][b]=0;
				break;
				}
			}
		}

		//↑
		for(int b=0;b<imgWidth;b++){
			for(int a=imgHeight-1;a>=0;a--){
				if(mask[a][b]==255){
					for(int xx=0;xx<pixel;xx++)
						mask[a-xx][b]=0;
				break;
				}
			}
		}

		for(int a=0;a<imgHeight;a++)
			for(int b=0;b<imgWidth;b++)
				if(mask[a][b]==0)
				  	input[a][b]=0;

		for(int a=0;a<imgHeight;a++)
			for(int b=0;b<imgWidth;b++)
				out[a][b]=input[a][b];

		return out;
	}

	public static void readjpg(String InputFileName)
	{
		int R[][],G[][],B[][],couR=0,couG=0,couB=0;
		JPGPixel p=new JPGPixel();//抓jpg影像，所需的物件
		p.ToGetJPGPixel(InputFileName);
		imgHeight=p.imgHeight;
		imgWidth=p.imgWidth;
		R=new int[imgHeight][imgWidth];
		G=new int[imgHeight][imgWidth];
		B=new int[imgHeight][imgWidth];
		Y=new double[imgHeight][imgWidth];
		YY=new double[imgHeight][imgWidth];
		YYY=new double[imgHeight][imgWidth];
		gray=new double[imgHeight][imgWidth];


		R=p.getR();
		G=p.getG();
		B=p.getB();

		for(int i=0;i<imgHeight;i++)//灰階化
			for(int j=0;j<imgWidth;j++)
				gray[i][j]=(double)(0.299*R[i][j]+0.587*G[i][j]+0.114*B[i][j]);

		for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){
				Y[i][j]=(double)(0.299*R[i][j]);//Red灰階
				YY[i][j]=(double)(0.587*G[i][j]);//Green灰階
			}
		}
	}

	public static void outputRGB_R(String InputFileName,String outpath,double input[][])
	{
		int R[][],G[][],B[][];
		JPGPixel p=new JPGPixel();//抓jpg影像，所需的物件
		p.ToGetJPGPixel(InputFileName);
		imgHeight=p.imgHeight;
		imgWidth=p.imgWidth;
		R=new int[imgHeight][imgWidth];
		G=new int[imgHeight][imgWidth];
		B=new int[imgHeight][imgWidth];
		Y=new double[imgHeight][imgWidth];
		R=p.getR();
		G=p.getG();
		B=p.getB();

		for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){
				if((input[i][j]!=0)&&(input[i-1][j-1]==0
			 	               ||input[i-1][j]==0
			 	               ||input[i-1][j+1]==0
			 	               ||input[i][j-1]==0
			 	               ||input[i][j+1]==0
			 	               ||input[i+1][j-1]==0
			 	               ||input[i+1][j]==0
			 	               ||input[i+1][j+1]==0))
				{
					R[i][j]=255;
					G[i][j]=0;
					B[i][j]=0;
				}
			}
		}

		p.setR(R,imgHeight,imgWidth);
		p.setG(G,imgHeight,imgWidth);
		p.setB(B,imgHeight,imgWidth);
		p.saveRGBPixel(outpath,imgHeight,imgWidth);
	}
		public static void outputRGB_G(String InputFileName,String outpath)
	{
		int R[][],G[][],B[][];
		JPGPixel p=new JPGPixel();//抓jpg影像，所需的物件
		p.ToGetJPGPixel(InputFileName);
		imgHeight=p.imgHeight;
		imgWidth=p.imgWidth;
		R=new int[imgHeight][imgWidth];
		G=new int[imgHeight][imgWidth];
		B=new int[imgHeight][imgWidth];
		Y=new double[imgHeight][imgWidth];
		R=p.getR();
		G=p.getG();
		B=p.getB();
		for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){
				R[i][j]=0;
				B[i][j]=0;
			}
		}
		p.setR(R,imgHeight,imgWidth);
		p.setG(G,imgHeight,imgWidth);
		p.setB(B,imgHeight,imgWidth);
		p.saveRGBPixel(outpath,imgHeight,imgWidth);
	}
		public static void outputRGB_B(String InputFileName,String outpath)
	{
		int R[][],G[][],B[][];
		JPGPixel p=new JPGPixel();//抓jpg影像，所需的物件
		p.ToGetJPGPixel(InputFileName);
		imgHeight=p.imgHeight;
		imgWidth=p.imgWidth;
		R=new int[imgHeight][imgWidth];
		G=new int[imgHeight][imgWidth];
		B=new int[imgHeight][imgWidth];
		Y=new double[imgHeight][imgWidth];
		R=p.getR();
		G=p.getG();
		B=p.getB();
		for(int i=0;i<imgHeight;i++){
			for(int j=0;j<imgWidth;j++){
				R[i][j]=0;
				G[i][j]=0;
			}
		}
		p.setR(R,imgHeight,imgWidth);
		p.setG(G,imgHeight,imgWidth);
		p.setB(B,imgHeight,imgWidth);
		p.saveRGBPixel(outpath,imgHeight,imgWidth);
	}

	public static void outputRGB(String InputFileName,String outpath,double[][] edge) // 將edge標回原圖顯示
	{
		int R[][],G[][],B[][];
		JPGPixel p=new JPGPixel();//抓jpg影像，所需的物件
		p.ToGetJPGPixel(InputFileName);
		imgHeight=p.imgHeight;
		imgWidth=p.imgWidth;
		R=new int[imgHeight][imgWidth];
		G=new int[imgHeight][imgWidth];
		B=new int[imgHeight][imgWidth];
		Y=new double[imgHeight][imgWidth];
		R=p.getR();
		G=p.getG();
		B=p.getB();
		for(int i=0;i<imgHeight;i++)
			for(int j=0;j<imgWidth;j++)
				if(edge[i][j]==255)
				{
					R[i][j]=255;
					G[i][j]=0;
					B[i][j]=0;
				}
		p.setR(R,imgHeight,imgWidth);
		p.setG(G,imgHeight,imgWidth);
		p.setB(B,imgHeight,imgWidth);
		p.saveRGBPixel(outpath,imgHeight,imgWidth);
	}

	public static void outputjpg(int out[][],String outpath,int Hei,int Wid)
	{
		JPGPixel p=new JPGPixel();
		p.setL(out,Hei,Wid);
		p.saveLPixel(outpath,Hei,Wid);
	}

	public static void outputjpg(double out[][],String outpath,int Hei,int Wid)
	{
		int temp[][]=new int [Hei][Wid];
		for(int i=0;i<Hei;i++)
			for(int j=0;j<Wid;j++)
				temp[i][j]=(int)out[i][j];
		JPGPixel p=new JPGPixel();
		p.setL(temp,Hei,Wid);
		p.saveLPixel(outpath,Hei,Wid);
	}
}//class  test