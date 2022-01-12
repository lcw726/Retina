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
public class Reflection {
  private short tempimage[][];
  public Reflection()
  {}
  public short[][] getimage(short[][] sourceimage,int tempH,int tempW,int blockmid)
  {
    tempimage=new short[tempH][tempW];
    for (int i = blockmid; i < tempH - blockmid; i++)
      for (int j = blockmid; j < tempW - blockmid; j++)
        tempimage[i][j] = sourceimage[i - blockmid][j - blockmid];
//i方向
    for (int i = 0; i < tempH; i++)
      for (int j = blockmid; j < tempW - blockmid; j++) {
        if (i < blockmid)
          tempimage[i][j] = tempimage[blockmid - i + blockmid - 1][j];
        else if (i >= tempH - blockmid)
          tempimage[i][j] = tempimage[tempH - blockmid -
              (i - (tempH - blockmid - 1))][j];
      }
//j方向
    for (int i = 0; i < tempH; i++)
      for (int j = 0; j < tempW; j++) {
        if (j < blockmid)
          tempimage[i][j] = tempimage[i][blockmid - j + blockmid - 1];
        else if (j >= tempW - blockmid)
          tempimage[i][j] = tempimage[i][tempW - blockmid -
              (j - (tempW - blockmid - 1))];
      }

    return tempimage;
  }
}
