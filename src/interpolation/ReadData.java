package interpolation;
import java.io.*;
import java.util.*;
import java.awt.*;

/**
 * @author Wolfram Ruehaak
 * @version 1.2
 * @Copyright: GPL Copyright (c) 2001
 * @e-mail: w.ruehaak@online.de,
 * @homepage: http://www.geomath.onlinehome.de
 *
 *            iw3d: A programm for gridding 3-dimensional scattered data with
 *            the "inverse distances" method.
 *
 *            This program is free software; you can redistribute it and/or
 *            modify it under the terms of the GNU General Public License as
 *            published by the Free Software Foundation; either version 2 of the
 *            License, or (at your option) any later version.
 *
 *            This program is distributed in the hope that it will be useful,
 *            but WITHOUT ANY WARRANTY; without even the implied warranty of
 *            MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *            General Public License for more details.
 *
 *            You should have received a copy of the GNU General Public License
 *            along with this program; if not, write to the Free Software
 *            Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *            02111-1307 USA
 ******************************************************************************
 *            This Dialog Subroutine returns the following parameters: min x,
 *            max x, nr of x, search radius in x direction min y, max y, nr of
 *            y, search radius in y direction min z, max z, nr of z, search
 *            radius in z direction
 *
 *            furthermore it returns the depth were a sclice shall be extracted,
 *            sclice-depth and the filename this sclice shall have.
 *            slice-filename
 ******************************************************************************
 */
public class ReadData extends iw3d {

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	iw3d parent;

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * the main method for reading the data file (has to be in ASCII column
	 * format) uses vector and sets the size of the data arrays
	 */
	public ReadData(Frame fr, String Directory, String Filename, boolean skip_first_line, boolean debug) {

		parent = (iw3d) fr;

		int i = 0, j = 0, ni, nj, number_of_rows;
		double[][] dAllValues;

		/**
		 * AFAIK Vector doubles its size if it becomes to small, so it might be
		 * better to define the increasing size, which is the second value:
		 * Vector v = new Vector(Size,IncreasingsSize);
		 */
		Vector v2 = new Vector(); // 4096000,1024
		try {
			File f = new File(Directory + Filename);
			if (f.length() == 0) {
				System.out.println("Sorry the file you selected has zero length!");
			}
		} catch (NullPointerException exc) {
			System.out.println(exc);
			return;
		}

		try {
			FileInputStream fis = new FileInputStream(Directory + Filename);
			BufferedReader r = new BufferedReader(new InputStreamReader(fis));
			StreamTokenizer st = new StreamTokenizer(r);

			String s1;
			st.resetSyntax();
			st.whitespaceChars(' ', ' ');
			st.whitespaceChars('\n', '\n');
			st.whitespaceChars('\t', '\t');
			st.whitespaceChars(',', ',');
			st.whitespaceChars(';', ';');
			st.wordChars('0', '9');
			st.wordChars('e', 'e');
			st.wordChars('E', 'E');
			st.wordChars('.', '.');
			st.wordChars('+', '+');
			st.wordChars('-', '-');
			st.eolIsSignificant(true);

			if (skip_first_line)
				r.readLine();

			j = 0;
			i = 0;
			while (st.nextToken() != st.TT_EOF) {
				s1 = st.sval;
				if (s1 != null) {
					v2.addElement(s1);
					i = st.lineno();
				}
			}
		} catch (IOException ioe) {
		}
		ni = i - 1;
		nj = ((v2.size()) / (ni + 1)) - 1;

		if (debug)
			System.out.println(
					"v2.size() = " + v2.size() + " number_of_rows = " + (ni + 1) + " number_of_columns = " + (nj + 1));

		dAllValues = new double[ni + 1][nj + 1];
		int k = 0;
		for (i = 0; i <= ni; i++) {
			for (j = 0; j <= nj; j++) {
				Object f = v2.elementAt(k);
				String s = f.toString();
				dAllValues[i][j] = Double.valueOf(s).doubleValue();
				k++;
			}
		}
		if (debug)
			System.out.println("finished reading" + " " + ni + " " + nj);
		parent.getReadData(ni, nj, dAllValues); // !!!
	}
}
