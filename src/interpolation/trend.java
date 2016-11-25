package interpolation;

public class trend extends iw3d {

/** remove a spatial trend before interpolation */
   public static double[] removeTrend(   double[] temp, double[] de,
                                       double X1, double X2, int ni) {
//      System.out.println("removeTrend: X1 = " + X1 + " X2 = " + X2);
      for(int i= 0; i<=ni;i++) {
      temp[i] = (temp[i] - X1) / (X2 * de[i]);
      }
   return temp;
   }
/** add the before removed spatial trend after the interpolation */
   public static double[][][] addTrend(double[][][] newt, double[] zv,
                                       double X1, double X2, int nx, int ny, int nz,
                                       double missingvalue) {
//      System.out.println("addTrend: X1 = " + X1 + " X2 = " + X2);
      for(int x= 0; x<=nx;x++) {
      for(int y= 0; y<=ny;y++) {
      for(int z= 0; z<=nz;z++) {
         if(newt[x][y][z]!=missingvalue) {
         newt[x][y][z] = (newt[x][y][z] * (X2* zv[z]))+ X1;
         }
      }}}
      return newt;
   }
}
