import java.util.Arrays;
public class Kmeans {
    private double[][] center;
    public Kmeans(int group,int dataNumber,int dimension,double[][] dataset){
        double[] gNum=new double[group];
        center=new double[group][dimension];
        double tempd[]=new double[group];
        int[] num=new int[group];
        double[][] newCenter=new double[group][dimension];
        double[][] centerSum=new double[group][dimension];
        double[] difference=new double[10];
        for(int iteration=1;iteration<100;iteration++){
            for(int k=0;k<group;k++){
                gNum[k]=0;
                difference[k]=0;
                for(int m=0;m<dimension;m++){
                    centerSum[k][m]=0;
                }
            }
            if(iteration==1){
                //�航��銴�
            	for(int i=0;i<group;i++){
                    num[i]=(int)(Math.random()*dataNumber);//�冽�賢��嗾蝑�
                    for(int j=0;j<dimension;j++){
                        center[i][j]=dataset[num[i]][j];//蝢支葉敹� 
                        }
                }
            }else{
                //閮�頝
                for(int i=0;i<dataNumber;i++){
                    double d[]=new double[group];
                    for(int k=0;k<group;k++){
                        for(int j=0;j<dimension;j++){
                            d[k]+=Math.pow((dataset[i][j]-center[k][j]),2);//閮�頝
                            tempd[k]=d[k];
                        }
                    }
                    Arrays.sort(d);
                    //閮�瘥黎��
                    for(int m=0;m<group;m++){
                        if(d[0]==tempd[m]){
                           gNum[m]++;
                           for(int q=0;q<dimension;q++){
                               centerSum[m][q]=centerSum[m][q]+dataset[i][q];
                           }
                        }
                    }
                }
                //�湔蝢支葉敹�                
                for(int i=0;i<group;i++){
                    for(int j=0;j<dimension;j++){
                        newCenter[i][j]=centerSum[i][j]/gNum[i];
                        difference[i]+=Math.abs(100*newCenter[i][j]-100*center[i][j]);
                    }
                }
                for(int i=0;i<group;i++){
                    for(int j=0;j<dimension;j++){
                        center[i][j]=newCenter[i][j];
                    }
                }
                if(difference[0]==0 && difference[1]==0 && difference[2]==0 &&
                   difference[3]==0 && difference[4]==0 && difference[5]==0 &&
                   difference[6]==0 && difference[7]==0 && difference[8]==0 &&
                   difference[9]==0){
                   break;
                }
            }
        }
    }
    public double[][] getCenter(){
        return center;
    }
}