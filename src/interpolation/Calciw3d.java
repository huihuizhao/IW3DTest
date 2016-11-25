package interpolation;

/**
 *
 * @author        Wolfram Ruehaak
 * @version       2.0
 * @Copyright:    GPL Copyright (c) 2003
 * @e-mail:       w.ruehaak@online.de,
 * @homepage:     http://www.geomath.onlinehome.de
 * @date:             17.06.2003
 *
 * iw3d: A programm for gridding 3-dimensional scattered data with the
 * "inverse distances" method.
 *
 * ---------------------------------------------------------------------
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * ---------------------------------------------------------------------
 *  This is the main interpolation routine<br>
 *  using the inverse distance with a power algorithm
 *  X is the Rechts-Value of the measured data<br>
 *  YC is the Hoch-Value ''<br>
 *  ZC is the depth (NN) ''<br>
 *  temp is the value we want to interpolate.<br>
 *
 *  Please look in the attached documentation for details.
 */
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.image.*;
import java.io.*;

public class Calciw3d extends JFrame {

	private JProgressBar progressbar;

	iw3d parent;

	long current;
	double deltax = 0.0, deltay = 0.0, deltaz = 0.0;
	double omega = 0.0, tau = 0.0, vecdist = 0.0;
	long scr11 = 0, scr12 = 0;
	double wx, wy, wz;
	double[][][] newt;
	double D_TOL;

	public Calciw3d(boolean debug, double[] XC, double[] YC, double[] ZC, double[] temp, double[] phi, int ni, int nx,
			int ny, int nz, boolean b_trend, boolean b_blank2, // b_blank2 is an
																// alternative
																// critera for
																// using a
																// search
																// distance, but
																// currently not
																// used
			double X1, double X2, double min_x, double max_x, double min_y, double max_y, double min_z, double max_z,
			boolean normalize_only_z, int i_normalize, double beta, double delta,
			int assign_missing_value_when_lower_than, double missingvalue, boolean b_Octant_search, boolean surfer_grd,
			double min_temp, double max_temp, boolean b_extract_slices, String slice_filename, double dSumT,
			double dMeanT, int NR_EMPTY_OCT, int NR_DATA_OCT) {

		super("Progress"); // super("Progress "+i);
		Image image; // the Image is an icon and vice versa ...
		image = null;
		// URL url2 = getClass().getResource("iw3d.png");
		// try {
		// image = createImage((ImageProducer)url2.getContent());
		// }catch (IOException ioe) {}
		// this.setIconImage(image);

		getContentPane().setLayout(new FlowLayout());

		progressbar = new JProgressBar(0, 100);
		progressbar.setValue(progressbar.getMinimum());
		progressbar.setStringPainted(true);
		getContentPane().add(progressbar);
		setSize(200, 100);
		setLocationRelativeTo(null);
		// this.setVisible(true);
		// this.requestFocus();

		D_TOL = Math
				.sqrt(Math.pow((max_x - min_x), 2.0) + Math.pow((max_y - min_y), 2.0) + Math.pow((max_z - min_z), 2.0));
		D_TOL = D_TOL * 1.E-12;

		if (debug)
			System.out.println("calciw3d 2 ni = " + ni + " D_TOL = " + D_TOL);

		double dx = ((max_x - min_x) / nx);
		double dy = ((max_y - min_y) / ny);
		double dz = ((max_z - min_z) / nz);

		/** normalize the measured data, if they have different extensions */
		double relation_y, relation_z;
		double width_XC = max_x - min_x;
		double width_YC = max_y - min_y;
		double width_ZC = max_z - min_z;
		/** normalize using grid-extensions */
		if (i_normalize == 1) {
			if (normalize_only_z == false) {
				relation_y = dx / dy;
			} else {
				relation_y = 1.;
			}
			relation_z = dx / dz;
		}
		/** normalize using data-extensions */
		else if (i_normalize == 2) {
			if (normalize_only_z == false) {
				relation_y = width_XC / width_YC;
			} else {
				relation_y = 1.;
			}
			relation_z = width_XC / width_ZC;
		} else if (i_normalize == 3) {
			/** normalize using grid & data-extensions */
			if (normalize_only_z == false) {
				relation_y = (dx / dy) * (width_XC / width_YC);
			} else {
				relation_y = 1.;
			}
			relation_z = (dx / dz) * (width_XC / width_ZC);
		}
		/** don't normalize */
		else {
			relation_y = 1.0;
			relation_z = 1.0;
		}
		/*
		 * ---------------------------------------------------------------------
		 * ----
		 */

		double[] xv = makegrid.xv(dx, nx, min_x);
		double[] yv = makegrid.yv(dy, ny, min_y);
		// double[] zv = makegrid.zv(dz, nz, min_z);
		double[] zv = new double[1];
		
		//深度剖面插值
//		zv[0] = 1000;
		
//		x和z互换，经度插值，重新设置剖面位置
//		zv[0]=135;
		
//		y和z互换，维度插值，重新设置剖面位置
		zv[0]=27;
		
		
		nz = 0;

		if (debug)
			System.out.println("search_x: " + BoundarysDialog.search_x_new + " search_y: "
					+ BoundarysDialog.search_y_new + " search_z: " + BoundarysDialog.search_z_new);

		newt = new double[nx + 1][ny + 1][nz + 1];

		wx = 1.;
		wy = 1.;
		wz = 1.;

		if (BoundarysDialog.search_x_new != 0.0) {
			wx = BoundarysDialog.search_x_new;
		} else {
			// 040619 wx = max_x - min_x;
			wx = 2.0 * (max_x - min_x);
		}
		if (BoundarysDialog.search_y_new != 0.0) {
			wy = BoundarysDialog.search_y_new;
		} else {
			// 040619 wy = max_y - min_y;
			wy = 2.0 * (max_y - min_y);
		}
		if (BoundarysDialog.search_z_new != 0.0) {
			wz = BoundarysDialog.search_z_new;
		} else {
			// 040619 wz = max_z - min_z;
			wz = 2.0 * (max_z - min_z);
		}

		if (debug) {
			System.out.println("start: Calciw3d - normalized wx,wy,wz:" + wx + " " + wy + " " + wz + "\n"
					+ "relation_y + relation_z: " + relation_y + " " + relation_z + "\n" + "dx,dy,dz:" + dx + " " + dy
					+ " " + dz + "\n" + "beta, delta: " + beta + " " + delta + "\n" + "b_Octant_search: "
					+ b_Octant_search + "\n" + "surfer_grd: " + surfer_grd + "\n" + "normalize_only_z: "
					+ normalize_only_z + "\n" + "b_blank2 (currently not used): " + b_blank2 + "\n" + "b_trend: "
					+ b_trend + " X1 = " + X1 + " X2 = " + X2 + "\nmissingvalue = " + missingvalue);
		}
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		double scattered_data_vecdist;
		long sum = 0;
		double nr_of_indizes = 1.0 / (double) (((ni + 1) * (nx + 1) * (ny + 1) * (nz + 1)) / 100.);
		long scr_progress = (long) (((ni + 1) * (nx + 1) * (ny + 1) * (nz + 1)) / 1000.);
		long begin = System.currentTimeMillis();
		// ##################################################################################
		if (debug) {
			double min_phi = +2.;
			double max_phi = +0.;
			for (int i = 0; i <= ni; i++) {
				min_phi = Math.min(phi[i], min_phi);
				max_phi = Math.max(phi[i], max_phi);
			}
			System.out.println("min_phi + max_phi " + min_phi + " " + max_phi);
		}
		// remove a spatial trend
		if (b_trend) {
			trend.removeTrend(temp, ZC, X1, X2, ni);
		}

		// ##################################################################################
		// Begin of the main calulation with 2 alternatives:
		// 1. using the Octant Search method
		// 2. search in an ellipsoid $ if(ellipsoid) {
		// a) using all values $ if(use all values) {...}
		// b) using search distance $ else {...}
		// to accelerate the algorithm I put the different parts in
		// if-questions out of the loops!
		// ##################################################################################
		if (b_Octant_search) {
			if (debug)
				System.out.println("Octant Search - begin of loops");
			for (int z = 0; z <= nz; z++) {
				for (int y = 0; y <= ny; y++) {
					for (int x = 0; x <= nx; x++) {
						omega = 0.0;
						tau = 0.0;
						OctantSearch.OctantSearch(debug, XC, YC, ZC, temp, phi, xv[x], yv[y], zv[z], ni, relation_y,
								relation_z, wx, wy, wz, delta, beta, NR_EMPTY_OCT, NR_DATA_OCT, D_TOL);
						sum = sum + (ni + 1);
						current = (long) (Math.round((sum) * nr_of_indizes));
						updateProgress(current);
						newt[x][y][z] = OctantSearch.newphi;
					} // x
				} // y
			} // z
		} else {
			if (debug)
				System.out.println("begin of loops - no Octant search!");
			if (BoundarysDialog.search_x_new == 0.0 && BoundarysDialog.search_y_new == 0.0
					&& BoundarysDialog.search_z_new == 0.0) { // the user has
																// selected use
																// all in every
																// direction !
				if (debug)
					System.out.println("use all in every direction is - true");
				for (int z = 0; z <= nz; z++) {
					for (int y = 0; y <= ny; y++) {
						for (int x = 0; x <= nx; x++) {
							omega = 0.0;
							tau = 0.0;
							boolean b_identic = false;
							identic: for (int i = 0; i <= ni; i++) {
								sum++;
								if (sum % scr_progress == 0) {
									current = (long) (Math.round(sum * nr_of_indizes));
									updateProgress(current);
								}
								deltax = Math.pow((XC[i] - xv[x]), 2.0);
								deltay = Math.pow(((YC[i] - yv[y]) * relation_y), 2.0);
								deltaz = Math.pow(((ZC[i] - zv[z]) * relation_z), 2.0);
								scattered_data_vecdist = Math.sqrt(deltax + deltay + deltaz);
								//
								// when a data point is very near to the grid
								// node, use this one and ignore all the others
								// D_TOL
								if (Math.abs(scattered_data_vecdist) <= 0.0) {
									b_identic = true;
									if (debug)
										System.out.println("identic " + Math.abs(scattered_data_vecdist));
									newt[x][y][z] = temp[i];
									break identic;
								}
								vecdist = Math.sqrt(Math.pow(scattered_data_vecdist, 2.0) + Math.pow(delta, 2.0));
								omega = omega + ((phi[i] * temp[i]) / Math.pow(vecdist, beta)); // if
																								// the
																								// distance
																								// ==
																								// zero
																								// this
																								// value
																								// becomes
																								// NaN
								tau = tau + ((phi[i] * 1.0) / Math.pow(vecdist, beta)); // if
																						// the
																						// distance
																						// ==
																						// zero
																						// this
																						// value
																						// becomes
																						// NaN
							} // i
							if (b_identic == false)
								newt[x][y][z] = omega / tau;
						} // x
					} // y
				} // z
			} // end of user selected use all in every direction
			else { // search in an ellipsoid but with search distance
				if (debug)
					System.out.println("use all in every direction is - false");
				double search_distance = Math
						.sqrt(Math.pow(wx, 2.0) + Math.pow((wy * relation_y), 2.0) + Math.pow((wz * relation_z), 2.0));
				for (int z = 0; z <= nz; z++) {
					for (int y = 0; y <= ny; y++) {
						for (int x = 0; x <= nx; x++) {
							omega = 0.0;
							tau = 0.0;
							scr11 = 0;
							boolean b_identic = false;
							identic: for (int i = 0; i <= ni; i++) {
								sum++;
								if (sum % scr_progress == 0) {
									current = (long) (Math.round(sum * nr_of_indizes));
									updateProgress(current);
								}
								deltax = Math.pow((XC[i] - xv[x]), 2.0);
								deltay = Math.pow(((YC[i] - yv[y]) * relation_y), 2.0);
								deltaz = Math.pow(((ZC[i] - zv[z]) * relation_z), 2.0);
								scattered_data_vecdist = Math.sqrt(deltax + deltay + deltaz);
								//
								// when a data point is very near to the grid
								// node, use this one and ignore all the others
								// D_TOL
								if (Math.abs(scattered_data_vecdist) <= 0.0) {
									b_identic = true;
									if (debug)
										System.out.println("identic " + Math.abs(scattered_data_vecdist));
									newt[x][y][z] = temp[i];
									break identic;
								}
								double A = Math.pow(wx, 2);
								double B = Math.pow((wy * relation_y), 2.0);
								double C = Math.pow((wz * relation_z), 2.0);
								double ellipsoid = deltax / A + deltay / B + deltaz / C;
								if (ellipsoid <= 1.0) { // this is the search
														// distance criteria
									vecdist = Math.sqrt(Math.pow(scattered_data_vecdist, 2.0) + Math.pow(delta, 2.0));
									omega = omega + ((phi[i] * temp[i]) / Math.pow(vecdist, beta)); // if
																									// the
																									// distance
																									// ==
																									// zero
																									// this
																									// value
																									// becomes
																									// NaN
									tau = tau + ((phi[i] * 1.0) / Math.pow(vecdist, beta)); // if
																							// the
																							// distance
																							// ==
																							// zero
																							// this
																							// value
																							// becomes
																							// NaN
									scr11++;
								}
							} // i
							if (b_identic == false) {
								newt[x][y][z] = omega / tau;
								// Assign the value for missing values if the
								// upper criteria was no time true
								if (scr11 <= assign_missing_value_when_lower_than) {
									newt[x][y][z] = missingvalue;
									scr12++;
								}
							}
						} // z
					} // y
				} // x
			}
		} // end of if(ellipsoid)
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		this.setVisible(false);
		long end = System.currentTimeMillis();
		if (debug)
			System.out.println("Calculation Time = " + (end - begin));
		if (debug)
			System.out.println("finished: Calciw3d - Nr of Missing Values = " + scr12);
		// add the before removed spatial trend
		if (b_trend) {
			trend.addTrend(newt, zv, X1, X2, nx, ny, nz, missingvalue);
			if (debug)
				System.out.println("Calciw3d: b_trend = " + b_trend + " addTrend");
		}
//		SaveData sd = new SaveData(debug, ni, nx, ny, nz, xv, yv, zv, b_extract_slices, newt, missingvalue,
//				slice_filename, surfer_grd, min_temp, max_temp, dSumT, dMeanT, b_trend, X1, X2, wx, wy, wz, relation_y,
//				relation_z, dx, dy, dz, beta, delta, b_Octant_search, NR_EMPTY_OCT, NR_DATA_OCT);
		
		double tempArray[][]=new double[newt.length][newt[0].length];
		for(int i=0;i<newt.length ;i++)
		{
			for(int j=0;j<newt[0].length ;j++)
			{
				tempArray[i][j]=newt[i][j][0];
			}
			
		}
//		String directory = "D:\\FocusMap\\程序代码\\IW3D_Java三维插值\\testdata\\";
		String directory = "src/interpolation/Data/testdata/";
		String saveFileName = "ProfileImage.jpg";
		try {
			SaveMatrixImage.createMatrixImage(tempArray, directory+saveFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/** Update the progress */
	private void updateProgress(long i) {
		progressbar.setValue((int) i);
	}
}
