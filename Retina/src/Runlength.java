/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.awt.event.*;
public class Runlength {
  public short Runlengthimage[][];
  public Runlength() {}

  public short[][] enhance(int height, int width, short sourceimage[][],
                           int blocksize) { //7*7 or 9*9
    Runlengthimage = new short[height][width];
    //image aa=new image();
    //aa.jMenuItem2_actionPerformed(e);
    //System.out.println("123");
    int blockmid = blocksize / 2;
    int tempH = height + blockmid * 2;
    int tempW = width + blockmid * 2;
    int blockx, blocky, amount0, amount45, amount90, amount135,
        directionx, directiony;
    int coordnatedirction4[][][] = new int[4][blocksize][2]; //紀錄0 45 90 135 各點的XY座標
    short tempimage[][] = new short[tempH][tempW];
    double blockmean = 0;
    double avgdirection[] = new double[8];
    //作映射
    Reflection refimage=new Reflection();
    tempimage=refimage.getimage(sourceimage,tempH,tempW,blockmid);
    for (int i = blockmid; i < tempH - blockmid; i++)
      for (int j = blockmid; j < tempW - blockmid; j++) {
        blockx = -1;
        blocky = -1;
        amount0 = -1;
        amount45 = -1;
        amount90 = -1;
        amount135 = -1;
        int count=0;
        for (int index = 0; index < 8; index++)
          avgdirection[index] = 0;
        blockmean = 0;
        for (int blocki = i - blockmid; blocki <= i + blockmid; blocki++) {
          blockx++;
          for (int blockj = j - blockmid; blockj <= j + blockmid; blockj++) {
            blocky++;
            blockmean += tempimage[blocki][blockj];
            if (blockx == blockmid) { //processed dirction0
              amount0++;
              coordnatedirction4[0][amount0][0] = blocki;
              coordnatedirction4[0][amount0][1] = blockj;
              avgdirection[0] += tempimage[blocki][blockj];
              //out[u-bound][v-bound]=90;
            }
            if (blocksize - 1 - blockx == blocky) { //processed dirction45
              amount45++;
              coordnatedirction4[1][amount45][0] = blocki;
              coordnatedirction4[1][amount45][1] = blockj;
              avgdirection[1] += tempimage[blocki][blockj];
              //out[u-bound][v-bound]=90;
            }
           if (blocky == blockmid) { //processed dirction90
              amount90++;
              coordnatedirction4[2][amount90][0] = blocki;
              coordnatedirction4[2][amount90][1] = blockj;
              avgdirection[2] += tempimage[blocki][blockj];
              //out[u-bound][v-bound]=90;
            }
            if (blockx == blocky) { //processed dirction135
              amount135++;
              coordnatedirction4[3][amount135][0] = blocki;
              coordnatedirction4[3][amount135][1] = blockj;
              avgdirection[3] += tempimage[blocki][blockj]; ;
            }
          }
           blocky = -1;
        }
        //System.out.println(" amount135="+ amount135+" amount90="+amount90);
        blockmean=blockmean/(blocksize*blocksize);
        for (int index = 0; index < blocksize; index++) {
          // processed dirction 22.5
          directionx = (coordnatedirction4[0][index][0] + coordnatedirction4[1][blocksize - 1 - index][0]) / 2;
          directiony = (coordnatedirction4[0][index][1] + coordnatedirction4[1][blocksize - 1 - index][1]) / 2;
          avgdirection[4] += tempimage[directionx][directiony];
          //out[tu-bound][tv-bound]=255;

          //processed dirction77.5
          directionx = (coordnatedirction4[2][index][0] + coordnatedirction4[1][index][0]) / 2;
          directiony = (coordnatedirction4[2][index][1] + coordnatedirction4[1][index][1]) / 2;
          avgdirection[5] += tempimage[directionx][directiony];
          //out[tu-bound][tv-bound]=255;

          //processed dirction112.5
          directionx = (coordnatedirction4[2][index][0] + coordnatedirction4[3][index][0]) / 2;
          directiony = (coordnatedirction4[2][index][1] + coordnatedirction4[3][index][1]) / 2;
          avgdirection[6] += tempimage[directionx][directiony];
          //out[tu-bound][tv-bound]=255;

          //processed dirction157.5
          directionx = (coordnatedirction4[0][index][0] + coordnatedirction4[3][index][0]) / 2;
          directiony = (coordnatedirction4[0][index][1] + coordnatedirction4[3][index][1]) / 2;
          avgdirection[7] += tempimage[directionx][directiony];
          //out[tu-bound][tv-bound]=255;
        }
        double max = 0, min = 500;
        for (int index = 0; index < 8; index++) {
          avgdirection[index] /= (double) blocksize;
          if (avgdirection[index] > max)
            max = avgdirection[index];
          if (avgdirection[index] <= min)
            min = avgdirection[index];
        }
       // System.out.println("i="+i+" j="+j);
        if (tempimage[i][j] >= blockmean)
           Runlengthimage[i-blockmid][j-blockmid] = (short)Math.round(max);
        else if (tempimage[i][j] < blockmean)
           Runlengthimage[i-blockmid][j-blockmid] = (short)Math.round(min);
      }
    return Runlengthimage;
  }
}
