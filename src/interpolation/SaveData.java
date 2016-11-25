package interpolation;

/**
 * @author        Wolfram Ruehaak
 * @version       2.0
 * @Copyright:    GPL Copyright (c) 2003
 * @e-mail:       w.ruehaak@online.de,
 * @homepage:     http://www.geomath.onlinehome.de
 * @date:             17.06.2004
 *
 * iw3d: A programm for gridding 3-dimensional scattered data with the
 * "inverse distances" method.
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
import java.io.*;
import java.text.*;

public class SaveData extends Frame {
	int x, y, z;
	private static final double MAX_VAL = +1.E+64;
	private static final double MIN_VAL = -1.E+64;
	boolean b_trend;
	double X1, X2;

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * The method SaveData opens a FileDialog, then it calls Save(Directory,
	 * Filename)
	 */
	public SaveData(boolean debug, int ni, int nx, int ny, int nz, double[] xv, double[] yv, double[] zv,
			boolean b_extract_slices, double[][][] newt, double missingvalue, String slice_filename, boolean surfer_grd,
			double min_temp, double max_temp, double dSumT, double dMeanT, boolean b_trend, double X1, double X2,
			double wx, double wy, double wz, double relation_y, double relation_z, double dx, double dy, double dz,
			double beta, double delta, boolean b_Octant_search, int NR_EMPTY_OCT, int NR_DATA_OCT) {

		String Directory = "D:\\FocusMap\\程序代码\\IW3D_Java三维插值\\testdata\\";
		String SaveFilename = "SaveFilename";

		// FileDialog d = new FileDialog(this, "Save File", FileDialog.SAVE);
		// d.setVisible(true);
		// Directory = d.getDirectory();
		// SaveFilename = d.getFile();
		Save(Directory, SaveFilename, debug, ni, nx, ny, nz, xv, yv, zv, b_extract_slices, newt, missingvalue,
				slice_filename, surfer_grd, min_temp, max_temp, dSumT, dMeanT, b_trend, X1, X2, wx, wy, wz, relation_y,
				relation_z, dx, dy, dz, beta, delta, b_Octant_search, NR_EMPTY_OCT, NR_DATA_OCT);
		// d.dispose();
	}

	/**
	 * The method Save is the method were all the data fiels we are interested
	 * are written:<br>
	 * - the grid or dat Files of the slices (if any) (x, y, value)<br>
	 * - the main-3D-data-file with 4-columns containing all interpolated values
	 */
	public void Save(String Directory, String SaveFilename, boolean debug, int ni, int nx, int ny, int nz, double[] xv,
			double[] yv, double[] zv, boolean b_extract_slices, double[][][] newt, double missingvalue,
			String slice_filename, boolean surfer_grd, double min_temp, double max_temp, double dSumT, double dMeanT,
			boolean b_trend, double X1, double X2, double wx, double wy, double wz, double relation_y,
			double relation_z, double dx, double dy, double dz, double beta, double delta, boolean b_Octant_search,
			int NR_EMPTY_OCT, int NR_DATA_OCT) {
//		nz = 0;

		double dMinNewT = MAX_VAL, dMaxNewT = MIN_VAL, dSumNewT = 0.0, dMeanNewT = 0.0;

		int k = 0;
		try {
			// ~~~~~~~~~~~~~~~ open the main data
			// file~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			FileOutputStream out = new FileOutputStream(Directory + SaveFilename);
			BufferedOutputStream bout = new BufferedOutputStream(out);
			PrintStream pout = new PrintStream(bout);
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if (debug)
				System.out.println("SaveData: nx,ny,nz: " + nx + " " + ny + " " + nz);

			double minx = xv[0];
			double maxx = xv[nx];
			double miny = yv[0];
			double maxy = yv[ny];
			double minz = zv[0];
			double maxz = zv[nz];
			long depth;
			double tmp_scale_x = 1.;
			double a, multi_x = 1.;
			for (a = -30.; a < 30.0; a++) {
				tmp_scale_x = Math.pow(10., a);
				if (Math.abs(maxz - minz) <= tmp_scale_x) {
					multi_x = tmp_scale_x * 10.;
					break;
				}
			}
			if (debug)
				System.out.println("multi_x = " + multi_x + " Math.abs(maxz-minz): " + Math.abs(maxz - minz)
						+ " tmp_scale_x: " + tmp_scale_x);

			if (b_extract_slices) {
				// ~~~~~~~~~~~~~~~ open the file for the extracted slice output
				// ~~~~~~~~~~~~~~~~
				for (z = 0; z <= nz; z++) {
					// if(Math.abs(minz) > 1. && Math.abs(maxz-minz) > 1.) {
					if (Math.abs(maxz - minz) > 10.0) {
						depth = (long) (zv[z]);
					} else {
						depth = (long) (zv[z] * multi_x);
					}
					if (debug)
						System.out.println("Slices: " + Directory + depth + slice_filename + " " + surfer_grd);
					FileOutputStream out2 = new FileOutputStream(Directory + depth + slice_filename);
					BufferedOutputStream bout2 = new BufferedOutputStream(out2);
					PrintStream pout2 = new PrintStream(bout2);
					if (surfer_grd) {
						double minf = MAX_VAL;
						double maxf = MIN_VAL;
						for (x = 0; x <= nx; x++) {
							for (y = 0; y <= ny; y++) {
								if (newt[x][y][z] != missingvalue) {
									minf = Math.min(minf, newt[x][y][z]);
									maxf = Math.max(maxf, newt[x][y][z]);
								}
							}
						}
						// write a datafile with the surfer ASCII grid-file
						// format
						pout2.println("DSAA");
						pout2.println((nx + 1) + " " + (ny + 1));
						pout2.println(minx + " " + maxx);
						pout2.println(miny + " " + maxy);
						pout2.println(minf + " " + maxf);

						for (y = 0; y <= ny; y++) {
							for (x = 0; x <= nx; x++) {
								pout2.print(newt[x][y][z] + " ");
							}
							pout2.println("");
							pout2.println("");
						}
					}
					// write a normal ascii 3 column dat file
					else {
						for (y = 0; y <= ny; y++) {
							for (x = 0; x <= nx; x++) {
								// pout2.println(xv[x]/1000.0 + "\t" +
								// yv[y]/1000.0 + "\t" + newt[x][y][z]);
								pout2.println(xv[x] + "\t" + yv[y] + "\t" + newt[x][y][z]);
								// pout2.println(x + "\t" + y + "\t" +
								// newt[x][y][z]);
							}
						}
					}
					pout2.close();
					bout2.close();
					out2.close();
				}
			}
			pout.println("TITLE = 'Interpolated Value'");
			pout.println("VARIABLES = 'X','Y','Z','Value'");
			String format = "00";
			DecimalFormat intf = new DecimalFormat(format);
			if (nx + 1 < 100 && ny + 1 < 100 && nz + 1 < 100) {
				format = "00";
			} else {
				format = "#00";
			}
			pout.print("ZONE I=");
			pout.print(intf.format(nx + 1));
			pout.print(", J=");
			pout.print(intf.format(ny + 1));
			pout.print(", K=");
			pout.print(intf.format(nz + 1));
			pout.print(", F=POINT\n");

			// write the main dat file
			for (z = 0; z <= nz; z++) {
				for (y = 0; y <= ny; y++) {
					for (x = 0; x <= nx; x++) {
						pout.println(xv[x] + "\t" + yv[y] + "\t" + zv[z] + "\t" + newt[x][y][z]);
						if (newt[x][y][z] != missingvalue) {
							dMinNewT = Math.min(dMinNewT, newt[x][y][z]);
							dMaxNewT = Math.max(dMaxNewT, newt[x][y][z]);
							dSumNewT = dSumNewT + newt[x][y][z];
						}
					}
				}
			}
			pout.close();
			bout.close();
			out.close();
		} catch (IOException ioe) {
			InfoDialog d2 = new InfoDialog(this, "Save Error", ioe.toString());
			d2.setVisible(true);
		}
		dMeanNewT = dSumNewT / ((nx + 1) * (ny + 1) * (nz + 1));

		String s_trend;
		if (b_trend) {
			s_trend = "\nTrend has been removed: " + "a = " + X1 + " b = " + X2;
		} else {
			s_trend = "\nNo trend has been removed.";
		}
		// optional: surfer_grd + "\nSearch distance after normalization -
		// wx,wy,wz: "+wx+"\t"+wy+"\t"+wz+
		// InfoDialog id = new InfoDialog(this, "Statistics", "Slices: " +
		// Directory + "depth" + slice_filename + "\n"
		// + "3-d datafile = " + Directory + SaveFilename + "\n" + "Min T = " +
		// dMinNewT + " original = "
		// + min_temp + "\n" + "Max T = " + dMaxNewT + " original = " + max_temp
		// + "\n" + "n = "
		// + ((nx + 1) * (ny + 1) * (nz + 1)) + " original = " + (ni + 1) + "\n"
		// + "Sum T = " + dSumNewT
		// + " original = " + dSumT + "\n" + "Mean T = " + dMeanNewT + "
		// original = " + dMeanT + "\n"
		// + "Correlation length (x) = " + (variogram3d.correlation_length_x) +
		// " Correlation length (y) = "
		// + (variogram3d.correlation_length_y) + " Correlation length (z) = " +
		// (variogram3d.correlation_length_z)
		// + "\n" + " search distance (x) = " + BoundarysDialog.search_x_new + "
		// search distance (y) = "
		// + BoundarysDialog.search_y_new + " search distance (z) = " +
		// BoundarysDialog.search_z_new + s_trend
		// + "\nNormalization y- / z-direction: " + relation_y + "\t" +
		// relation_z + "\ndx,dy,dz: " + dx + "\t"
		// + dy + "\t" + dz + "\nnx,ny,nz: " + (nx + 1) + "\t" + (ny + 1) + "\t"
		// + (nz + 1) + "\nbeta: " + beta
		// + " delta: " + delta + "\nquality-weighting is " + iw3d.b_weighting +
		// "\noctant serch is "
		// + b_Octant_search + "\nnumber of empty octants allowed: " +
		// NR_EMPTY_OCT
		// + " number of data used per octant: " + NR_DATA_OCT);
		// id.pack();
		// id.setVisible(true);

		// Jump back to iw3d.java and reset the program to start new again
		iw3d i = new iw3d();
		i.reset();
	}
}
