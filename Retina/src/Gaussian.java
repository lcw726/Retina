
public class Gaussian {

	private int height;
	private int width;

	public double[][] gg(double[][] image,int h,int w,int r,double s)
	{
		height=h;
		width=w;

		double[][] output = new double[height][width];

		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){

					double sum=0, aa=0;
					int shift=(int)r/2;
					int x_up,x_down,y_left,y_right,u,v;
					x_up=i-shift;
					x_down=i+shift;
					y_left=j-shift;
					y_right=j+shift;

					for(int x=x_up;x<=x_down;x++){
						for(int y=y_left;y<=y_right;y++){

							if(x>=0 && y>=0 && x<height	&& y<width){

								u=(int)Math.pow((i-x),2);
								v=(int)Math.pow((j-y),2);

								double temp = 2*3.1416*Math.pow(s,2);

								double temp2 = -(u+v)/(2*Math.pow(s,2));

								double temp3 = Math.pow(2.71828,temp2);

								double temp4 = (1/temp)*temp3;

								sum = sum+temp4;

								aa = aa+image[x][y]*temp4;

							}
						}
					}

					output[i][j]=aa/sum;

			}
		}
		return output;
	 }

}
