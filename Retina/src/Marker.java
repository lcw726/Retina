import javax.swing.*;

class Marker {
  private int savecode[][] = new int[2][50000];
  short numberimage[][];
  int index=1;
  int objectamount;
   public Marker(short original[][], int imgheighth, int imgwidth) {
    int small = 3;
    int count = 1;
    int lot;
    int x;
    int min;
    int value;
    int code[][] = new int[imgheighth][imgwidth];
    numberimage=new short[imgheighth][imgwidth];
    short temparray[]=new short[50000];
    temparray[0]=1;
    for (int i3 = 0; i3 < imgwidth; i3++) {
      for (int i4 = 0; i4 < imgheighth; i4++) {
        code[0][i3] = 1;
        code[imgheighth - 1][i3] = 1;
        code[i4][0] = 1;
        code[i4][imgwidth - 1] = 1;
      /*  if(original[i3][i4]!=255)
         original[i3][i4]=255;
        else
         original[i3][i4]=0;*/
      }
    }
    savecode[0][1] = 1;
    for (int i4 = 0; i4 < 50000; i4++) {
      savecode[1][i4] = 0;
    }

    for (int i = small / 2; i < imgheighth - small / 2; i++) {
      for (int j = small / 2; j < imgwidth - small / 2; j++) {
        if (original[i][j] != original[i][j - 1]
            && original[i][j] != original[i - 1][j + 1]
            && original[i][j] != original[i - 1][j]
            && original[i][j] != original[i - 1][j - 1]) {
          count = count + 1;
          code[i][j] = count;
          savecode[0][count] = code[i][j];
        }

        else if (original[i][j] == original[i][j - 1]) {
          value = code[i][j - 1];
          if (savecode[1][value] == 0) {
            min = code[i][j - 1];
          }
          else {
            min = savecode[1][value];
          }
          code[i][j] = min;
          for (int i2 = j - 1; i2 <= j + 1; i2++) {
            if (original[i][j] == original[i - 1][i2]) {
              x = code[i - 1][i2];
              if (savecode[1][x] == 0) {
                if (min > code[i - 1][i2]) {
                  min = code[i - 1][i2];
                  code[i][j] = min;
                }
                else {
                  code[i][j] = min;
                }
              }
              else {
                if (min > savecode[1][x]) {
                  min = savecode[1][x];
                  code[i][j] = min;
                }
                else {
                  code[i][j] = min;
                }
              }
            }
          }
          for (int i2 = j - 1; i2 <= j + 1; i2++) {
            if (original[i][j] == original[i - 1][i2]) {

              lot = code[i - 1][i2];
              savecode[1][lot] = min;
              code[i - 1][i2] = min;
            }
          }
          value = code[i][j - 1];
          savecode[1][value] = min;
          code[i][j - 1] = min;
        } //比1次

        else if (original[i][j] == original[i - 1][j + 1]) {
          value = code[i - 1][j + 1];
          if (savecode[1][value] == 0) {
            min = code[i - 1][j + 1];
          }
          else {
            min = savecode[1][value];
          }
          code[i][j] = min;

          for (int i2 = j - 1; i2 <= j; i2++) {
            if (original[i][j] == original[i - 1][i2]) {
              x = code[i - 1][i2];
              if (savecode[1][x] == 0) {
                if (min > code[i - 1][i2]) {
                  min = code[i - 1][i2];
                  code[i][j] = min;
                }
                else {
                  code[i][j] = min;
                }
              }
              else {
                if (min > savecode[1][x]) {
                  min = savecode[1][x];
                  code[i][j] = min;
                }
                else {
                  code[i][j] = min;
                }
              }
            }
          }
          for (int i2 = j - 1; i2 <= j + 1; i2++) {
            if (original[i][j] == original[i - 1][i2]) {

              lot = code[i - 1][i2];
              savecode[1][lot] = min;
              code[i - 1][i2] = min;
            }
          }
          value = code[i - 1][j + 1];
          savecode[1][value] = min;
          code[i - 1][j + 1] = min;
        } //比2次

        else if (original[i][j] == original[i - 1][j]) {
          value = code[i - 1][j];
          if (savecode[1][value] == 0) {
            min = code[i - 1][j];
          }
          else {
            min = savecode[1][value];
          }
          code[i][j] = min;

          for (int i2 = j - 1; i2 < j; i2++) {
            if (original[i][j] == original[i - 1][i2]) {
              x = code[i - 1][i2];
              if (savecode[1][x] == 0) {
                if (min > code[i - 1][i2]) {
                  min = code[i - 1][i2];
                  code[i][j] = min;
                }
                else {
                  code[i][j] = min;
                }
              }
              else {
                if (min > savecode[1][x]) {
                  min = savecode[1][x];
                  code[i][j] = min;
                }
                else {
                  code[i][j] = min;
                }
              }
            }
          }
          for (int i2 = j - 1; i2 <= j + 1; i2++) {
            if (original[i][j] == original[i - 1][i2]) {

              lot = code[i - 1][i2];
              savecode[1][lot] = min;
              code[i - 1][i2] = min;
            }
          }
          value = code[i - 1][j];
          savecode[1][value] = min;
          code[i - 1][j] = min;
        } //比3次

        else if (original[i][j] == original[i - 1][j - 1]) {
          value = code[i - 1][j - 1];
          if (savecode[1][value] == 0) {
            min = code[i - 1][j - 1];
          }
          else {
            min = savecode[1][value];
          }
          code[i][j] = min;
          //savecode[0][min]=min;
        }
      }
    }



    for (int cc = 0; cc < 50000; cc++) { //最後的檢查
      boolean add=false;
      if (savecode[1][cc] != 0) {
        int right = savecode[1][cc];
        if (savecode[1][right] != 0) {
          savecode[1][cc] = savecode[1][right];
          for(int i=0;i<index;i++)
          {
            if(temparray[i]!=savecode[1][cc])
              add=true;
            else
            {
              add=false;
              break;
            }
          }
          if(add)
          {
            temparray[index]=(short)savecode[1][cc];
            index++;
          }
        }
      }
    }
    int tempindex = index;
   /* for (int ai = 0; ai < index; ai++)
      System.out.println(" temparray[index]=" + temparray[ai]);
    System.out.println("index=" + index);*/

    for (int i = 0; i < imgheighth; i++) {
      for (int j = 0; j < imgwidth; j++) {
        boolean add = false;
        for (int i1 = 0; i1 < index; i1++) {
          if (temparray[i1] != code[i][j])
            add = true;
          else {
            add = false;
            break;
          }
        }
        if (add) {
          temparray[index] = (short) code[i][j];
          index++;
        }
      }
    }
    int number[]=new int[index];
    for (int i = 0; i < imgheighth; i++) { //編碼
      for (int j = 0; j < imgwidth; j++) {
        for (int i1 = 0; i1 < index; i1++) {
          if ( (code[i][j] == temparray[i1])) {
            number[i1]++;
            break;
          }
        }
      }
    }
    for (int i1 = tempindex; i1 < index; i1++) {
      if (number[i1] == 1) {
        temparray[tempindex] = temparray[i1];
        number[tempindex] = number[i1];
        tempindex++;
      }
    }
    index=tempindex;
 //   System.out.println("index=" + index);
    /*for (int ai = 0; ai < index; ai++)
      System.out.println(index+" temparray=" + temparray[ai]+ " number="+number[ai]);*/
    for (int i = 0; i < imgheighth; i++) { //編碼
      for (int j = 0; j < imgwidth; j++) {
        for (int cc = 0; cc < 50000; cc++) {
          if (savecode[1][cc] != 0) {
            if (savecode[0][cc] == code[i][j]) {
              for(int ai=0; ai<index;ai++)
              {
                if(savecode[1][cc]==temparray[ai])
                {
                  //code[i][j]=ai;
                  numberimage[i][j] = (short) (ai*1);
                  break;
                }
               // code[i][j] = savecode[1][cc];
              }
            }
          }
          //original[i][j]=(short)(code[i][j]*5);
          //System.out.print(code[i][j]+"\t");
        }
       for (int ai = 0; ai < index; ai++) {
          if (code[i][j] == temparray[ai] && number[ai]==1) {
            numberimage[i][j] = (short) (ai*1);
            break;
          }
        }
        objectamount=index;
        // numberimage[i][j] = (short) ((code[i][j])*5);
      }
    }

  }
}
