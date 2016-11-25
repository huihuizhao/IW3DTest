package interpolation;

/**
 * @author        Wolfram Ruehaak
 * @version       2.0
 * @Copyright:    GPL Copyright (c) 2003
 * @e-mail:       w.ruehaak@online.de,
 * @homepage:     http://www.geomath.onlinehome.de
 * @date:         17.06.2004
 *
 * iw3d: A programm for gridding 3-dimensional scattered data with the
 * "inverse-distances" method.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.awt.*;
import javax.swing.*;

/**
 * Calculation of a 3D-variogram
 * 
 * The variogram has 3 directions - according to the cartesian directions, this
 * means from E-W, N-S, Top-Down The result is the range. (Schlueter, 1996: The
 * range is the distance, for which a a statistical connection between to
 * measurement points exists.) The Semi-Variogramm gives the following three
 * parameter: 1. Sill 2. Range (or Correlationlength) 3. Nugget-Effekt
 * 
 * Again after @Schlueter: For example semi-variogram along the x-axis: sort
 * data ascending in x-direction Then for 10 distance-intervals (lags): h =
 * (XMAX-XMIN)/10 h1 = 1*h h2 = 2*h ... h10 = 10*h
 * 
 * Starting with h1, take the first X-Value X1 (take smallest X) and calculate
 * the difference of this value to the value in h1 distance (X2), then square
 * this value plus (X2 - X3)^2 [X3 iis in the distance h1 from X2] plus ...
 * [repeat this N-1 times - until XMAX is reached] (XN-1 - XN)^2 divided with
 * (2*N) [N sshould be in this case also = 10] = GAMMA(h1) repeat this for all
 * the other hN, results in GAMMA(hN), which is the
 * Semi-/Experimental-Variogram.
 * 
 * Following @Kitanidis: consider the case of /i{n} measurements z(/b{x_1}),
 * z(/b{x_2}), ..., z(/b{x_n}),. The bold letter /b{x} stands for the array of
 * coordinates of the point where these measurements were taken. Plot the square
 * difference 1/2[z(/b{x_i})-z(/b{x_i'}),]^2 against the seperation distance
 * ||/b{x_i}-/b{x_i'}|| for all measurement pairs (where || || means the length
 * of a vector). For /i{n} measurements, there are n(n-1)/2 such pairs that form
 * a scatter plot known as the /i{raw variogram}. The experimental variogram is
 * a smooth line through this scatter plot. In the common method of plotting the
 * experimental variogram, the axis of seperation distance is divíded into
 * consectutive intervals, similary as for the histogram. The /i{k}-th interval
 * is [h^l_k,h^u_k] and contains N_k pairs of measurements
 * [z(/b{x_i}),z(/b{x_i'})]. Thenn compute: gamma(h_k) =
 * (1/(2N_k))*SUM[i=1,N_k][z(/b{x_i})-z(/b{x_i'})]^2, where index i refers to
 * each pair of measurements z(/b{x_i}) and z(/b{x_i'}) for which h^l_k <= ||
 * /b{x_i}-/b{x_i'} || < h^u_k. This interval is represented by a single point
 * h_k. Take h_k equal to the average value. h_k =
 * 1/N_k*SUM[i=1,N_k]||/b{x_i}-/b{x_i'}||
 * 
 * Next, these points [h_k,gamma(h_k)] are connected to form the experimental
 * variogram. (...) It is unprofitable to spend too much time at this stage
 * fiddling with the intervals because there is really no "best" experimental
 * variogram. Some useful guidelines to obtain a reasonable experimental
 * variogram are: 1. Use three to six intervals. 2. Include more pairs (use
 * longer intervals) at distances where the raw varigram is spread out.
 */

public class variogram3d extends iw3d {
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	iw3d parent;
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	static final boolean NEAR_OK = false;
	public double min_Temp, max_Temp, min_X, max_X, min_Y, max_Y, min_Z, max_Z;
	public double[] normTemp;
	public double[] normX;
	public double[] normY;
	public double[] normZ;
	public double[] alpha;
	public double[] beta;
	public double[] X;
	public double[] Y;
	public double[] Z;
	public double[] Temp;
	public static double correlation_length_x;
	public static double correlation_length_y;
	public static double correlation_length_z;
	public static double[][] allData; // = new double[10][6];
	// ---------------------------------------------------------------------------------------

	public variogram3d(Frame fr, boolean debug, double[] X0, double[] Y0, double[] Z0, double[] Temp0, double min_X0,
			double max_X0, double min_Y0, double max_Y0, double min_Z0, double max_Z0, int ni0) {

		// restore
		ni = ni0;
		min_X = min_X0;
		max_X = max_X0;
		min_Y = min_Y0;
		max_Y = max_Y0;
		min_Z = min_Z0;
		max_Z = max_Z0;

		X = new double[ni + 1];
		Y = new double[ni + 1];
		Z = new double[ni + 1];
		Temp = new double[ni + 1];

		for (int i = 0; i <= ni0; i++) {
			X[i] = X0[i];
			Y[i] = Y0[i];
			Z[i] = Z0[i];
			Temp[i] = Temp0[i];
		}
		// end restore

		parent = (iw3d) fr;
//		Progress prg = new Progress();
//		prg.start(); // start Thread
		
		semiVariogram sv = new semiVariogram(debug, X, Y, Z, Temp, min_X, max_X, min_Y, max_Y, min_Z, max_Z, ni);
		
	}

	class Progress extends Thread {
		public void run() {

			if (debug)
				System.out.println("semi variogram 1a " + X[1] + " " + Y[1] + " " + Z[1] + " " + Temp[1] + " " + min_X
						+ " " + max_X + " " + min_Y + " " + max_Y + " " + min_Z + " " + max_Z + " " + ni);

			semiVariogram sv = new semiVariogram(debug, X, Y, Z, Temp, min_X, max_X, min_Y, max_Y, min_Z, max_Z, ni);
		}
	}

	class semiVariogram extends JFrame {

		private JProgressBar progressbar;

		/**
		 * Prepare data for Statistics sub class We want to do basic statistics
		 * (median, linear regression and so on)
		 */
		public semiVariogram(boolean debug, double[] X, double[] Y, double[] Z, double[] Temp, double min_X,
				double max_X, double min_Y, double max_Y, double min_Z, double max_Z, int ni) {
			super("please wait - calculating semivariogram");
			int ni2 = ni;
			int nj2 = ni;
			j = nj2;
			int maxl = 20;
			allData = new double[maxl][6];
			getContentPane().setLayout(new FlowLayout());
			progressbar = new JProgressBar(0, 100);
			progressbar.setValue(progressbar.getMinimum());
			progressbar.setStringPainted(true);
			getContentPane().add(progressbar);
			setSize(500, 100);
			setLocationRelativeTo(null);
//			 this.setVisible(true);
//			 this.requestFocus();

			double nr_of_indizes = 1.0 / (double) (maxl) / 100.;
			int current;

			if (debug)
				System.out.println("semi variogram " + min_X + " " + max_X + " " + min_Y + " " + max_Y + " " + min_Z
						+ " " + max_Z + " " + ni);

			double[] gamma_X = new double[ni + 1];
			double[] h_X = new double[ni + 1];
			double[] gamma_Y = new double[ni + 1];
			double[] h_Y = new double[ni + 1];
			double[] gamma_Z = new double[ni + 1];
			double[] h_Z = new double[ni + 1];
			double dist_X = (max_X - min_X) / maxl;
			double dist_Y = (max_Y - min_Y) / maxl;
			double dist_Z = (max_Z - min_Z) / maxl;
			// reducing of the search distance !
			// X_BANDWIDTH - Tolerance of the search area in X-direction
			double X_TOL = Math.abs((max_X - min_X) / maxl);
			// Y_BANDWIDTH - Tolerance of the search area in Y-direction
			double Y_TOL = Math.abs((max_Y - min_Y) / maxl);
			// Z_BANDWIDTH - Tolerance of the search area in Z-direction
			double Z_TOL = Math.abs((max_Z - min_Z) / maxl);

			if (debug)
				System.out.println("X_TOL = " + X_TOL + "Y_TOL = " + Y_TOL + " Z_TOL = " + Z_TOL);

			double hk_X[] = new double[maxl + 1];
			double hk_Y[] = new double[maxl + 1];
			double hk_Z[] = new double[maxl + 1];

			for (int l = 1; l <= maxl; l++) {
				double gammasum_X = 0.0;
				double hsum_X = 0.0;
				double gammasum_Y = 0.0;
				double hsum_Y = 0.0;
				double gammasum_Z = 0.0;
				double hsum_Z = 0.0;
				int k_X = 0;
				h_X[0] = 0.0;
				h_X[l] = dist_X + h_X[l - 1];
				int k_Y = 0;
				h_Y[0] = 0.0;
				h_Y[l] = dist_Y + h_Y[l - 1];
				int k_Z = 0;
				h_Z[0] = 0.0;
				h_Z[l] = dist_Z + h_Z[l - 1];
				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				// Progress Bar
				current = (int) (Math.round((l) * nr_of_indizes));
				updateProgress(l * (100 / maxl));
				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				j = nj2;
				for (j = nj2; j >= 0; j--) {
					for (i = 0; i <= ni2; i++) {
						if (j != i) {

							// TMP041109 double deltax = Math.abs(X[i]-X[j]);
							// TMP041109 double deltay = Math.abs(Y[i]-Y[j]);
							// TMP041109 double deltaz = Math.abs(Z[i]-Z[j]);

							double deltax = (X[i] - X[j]);
							double deltay = (Y[i] - Y[j]);
							double deltaz = (Z[i] - Z[j]);

							// defining a specific Radius, for the
							// residence of a data-point within a specific
							// angle.
							// To ensure that the sector of the angle defined
							// residence triangle
							// doesn't become to large, the angle-tolerance
							// merges into the bandwidth,
							// as soon as the perpendicular on the
							// direction-(normal?) vector is bigger,
							// athan half of the bandwidth.
							double theta = 20.0 * Math.PI / 180.0;
							// ---------------------------------------------------------------------
							// X-DIRECTION
							//
							// testing of a LAG-TOLERANCE like the one used in
							// GAMV

							double XLTOL = dist_X / 2.0;

							// 041109 Testing the new approach that only values
							// nearer than half of
							// the total distance should be used.
							// This has been quite simple been solved: we use 20
							// Values here and only 10 for displaying!
							// Taking this into account below also for
							// calculating the correlation length only the
							// first 10 value where used.
							// Further the total number of pairs has to be
							// counted (should be not less than 30 (50)).

							if (h_X[l - 1] - XLTOL <= deltax && deltax <= h_X[l] + XLTOL) {
								if (deltay < Y_TOL && deltaz < Z_TOL) {
									// check if the point is in the
									// near-distance area
									if (deltax < 0.5 * X_TOL) {
										// check if this nearby point in the
										// XY-Layer and in the XZ-Layer
										// has an angle < theta to the x-axis.
										// point_angle is the angle which X[i]
										// and X[j] have to the x-axis.
										double point_angle_XY = Math.atan(deltax / deltay);
										// point_angle is the angle which Z[i]
										// and Z[j] have to the z-axis
										double point_angle_XZ = Math.atan(deltax / deltaz);
										if ((point_angle_XY < theta && point_angle_XZ < theta) | NEAR_OK) {
											hsum_X = hsum_X + deltax;
											gammasum_X = gammasum_X + Math.pow((Temp[i] - Temp[j]), 2);
											k_X++;
										}
									}
									// point is within the nearby area
									else {
										hsum_X = hsum_X + deltax;
										gammasum_X = gammasum_X + Math.pow((Temp[i] - Temp[j]), 2);
										k_X++;
									}
								}
							}
							// ---------------------------------------------------------------------
							// Y-DIRECTION

							double YLTOL = dist_Y / 2.0;
							if (h_Y[l - 1] - YLTOL <= deltay && deltay <= h_Y[l] + YLTOL) {
								if (deltax < X_TOL && deltaz < Z_TOL) {
									// nearby area ?
									if (deltay < 0.5 * Y_TOL) {
										// Angle within XY-layer and XZ-layer to
										// x-axis < theta ?
										// point_angle is the angle of X[i] and
										// X[j] to the x-axis
										double point_angle_YX = Math.atan(deltay / deltax);
										// point_angle is the angle of Y[i] and
										// Y[j] to the y-axis
										double point_angle_YZ = Math.atan(deltay / deltaz);
										if ((point_angle_YX < theta && point_angle_YZ < theta) | NEAR_OK) {
											if (h_Y[l - 1] <= deltay && deltay < h_Y[l]) {
												hsum_Y = hsum_Y + deltay;
												gammasum_Y = gammasum_Y + Math.pow((Temp[i] - Temp[j]), 2);
												k_Y++;
											}
										}
									}
									// Point is within the nearby area
									else if (deltay >= 0.5 * Y_TOL) {
										if (h_Y[l - 1] <= deltay && deltay < h_Y[l]) {
											hsum_Y = hsum_Y + deltay;
											gammasum_Y = gammasum_Y + Math.pow((Temp[i] - Temp[j]), 2);
											k_Y++;
										}
									}
								}
							}
							// ---------------------------------------------------------------------
							// Z-DIRECTION

							double ZLTOL = dist_Z / 2.0;
							if (h_Z[l - 1] - ZLTOL <= deltaz && deltaz <= h_Z[l] + ZLTOL) {
								if (deltax < X_TOL && deltay < Y_TOL) {
									// check if the point is in the
									// near-distance area
									if (deltaz < 0.5 * Z_TOL) {
										// check if this nearby point in the
										// XY-Layer and in the XZ-Layer
										// has an angle < theta to the x-axis.
										// point_angle is the angle which X[i]
										// and X[j] have to the x-axis.
										double point_angle_ZY = Math.atan(deltaz / deltay);
										// point_angle is the angle which Z[i]
										// and Z[j] have to the z-axis
										double point_angle_ZX = Math.atan(deltaz / deltax);
										if ((point_angle_ZX < theta && point_angle_ZY < theta) | NEAR_OK) {
											if (h_Z[l - 1] <= deltaz && delta < h_Z[l]) {
												hsum_Z = hsum_Z + deltaz;
												gammasum_Z = gammasum_Z + Math.pow((Temp[i] - Temp[j]), 2);
												k_Z++;
											}
										}
									}
									// Point is within the nearby area
									else if (deltaz >= 0.5 * Z_TOL) {
										if (h_Z[l - 1] <= deltaz && deltaz < h_Z[l]) {
											hsum_Z = hsum_Z + deltaz;
											gammasum_Z = gammasum_Z + Math.pow((Temp[i] - Temp[j]), 2);
											k_Z++;
										}
									}
								}
							}
							// ---------------------------------------------------------------------
						}
					}
				}
				if (k_X >= 30) {
					hk_X[l] = hsum_X / (k_X); // average h for which this gamma
												// results
					gamma_X[l] = 1.0 / (2.0 * (k_X)) * gammasum_X;
				} else {
					hk_X[l] = 0.0;
					gamma_X[l] = 0.0;
				}
				if (k_Y >= 30) {
					hk_Y[l] = hsum_Y / (k_Y); // average h for which this gamma
												// results
					gamma_Y[l] = 1.0 / (2.0 * (k_Y)) * gammasum_Y;
				} else {
					hk_Y[l] = 0.0;
					gamma_Y[l] = 0.0;
				}
				if (k_Z >= 30) {
					hk_Z[l] = hsum_Z / (k_Z); // average h for which this gamma
												// results
					gamma_Z[l] = 1.0 / (2.0 * (k_Z)) * gammasum_Z;
				} else {
					hk_Z[l] = 0.0;
					gamma_Z[l] = 0.0;
				}
				if (debug)
					System.out.println(l + " " + hk_X[l] + " " + gamma_X[l] + " " + " Pairs(X): " + k_X + " " + hk_Y[l]
							+ " " + gamma_Y[l] + " " + " Pairs(Y): " + k_Y + " " + hk_Z[l] + " " + gamma_Z[l] + " "
							+ " Pairs(Z): " + k_Z);
			}
			// save into an array for visualisation
			for (int l = 1; l <= maxl; l++) {
				allData[l - 1][0] = hk_X[l];
				allData[l - 1][1] = gamma_X[l];
				allData[l - 1][2] = hk_Y[l];
				allData[l - 1][3] = gamma_Y[l];
				allData[l - 1][4] = hk_Z[l];
				allData[l - 1][5] = gamma_Z[l];
			}
			//
			// Calculate the correlation-length from the semi-variogram
			// This shall be the value for which the gamma stays equal or
			// decreases
			//
			double max_hk_X = -1.E64, max_hk_Y = -1.E-64, max_hk_Z = -1.E-64;
			for (int l = 1; l <= maxl / 2.; l++) {
				max_hk_X = Math.max(max_hk_X, hk_X[l]);
				max_hk_Y = Math.max(max_hk_Y, hk_Y[l]);
				max_hk_Z = Math.max(max_hk_Z, hk_Z[l]);
			}
			int xc = 0, yc = 0, zc = 0;
			for (int l = 1; l < maxl / 2.; l++) {
				if (gamma_X[l] >= gamma_X[l + 1]) {
					correlation_length_x = hk_X[l];
					xc++;
					break;
				}
			}
			for (int l = 1; l < maxl / 2.; l++) {
				if (gamma_Y[l] >= gamma_Y[l + 1]) {
					correlation_length_y = hk_Y[l];
					yc++;
					break;
				}
			}
			for (int l = 1; l < maxl / 2.; l++) {
				if (gamma_Z[l] >= gamma_Z[l + 1]) {
					correlation_length_z = hk_Z[l];
					zc++;
					break;
				}
			}
			if (xc == 0)
				correlation_length_x = max_hk_X;
			if (yc == 0)
				correlation_length_y = max_hk_Y;
			if (zc == 0)
				correlation_length_z = max_hk_Z;
			String clx = correlation_length_x + "";
			String cly = correlation_length_y + "";
			String clz = correlation_length_z + "";
			if (clx.equals("NaN"))
				correlation_length_x = Math.abs(max_X - min_X);
			if (cly.equals("NaN"))
				correlation_length_y = Math.abs(max_Y - min_Y);
			if (clz.equals("NaN"))
				correlation_length_z = Math.abs(max_Z - min_Z);

			/*
			 * Hier sollte ein InfoDialog kommen, wenn die Korrelationslaenge zu
			 * klein ist; mit Verweis auf den Advanced Dialog!
			 */
			parent.getCorrelationLength();
			 this.setVisible(false);
		}

		/** Update the progress */
		private void updateProgress(int i) {
			progressbar.setValue(i);
		}
	}
}
