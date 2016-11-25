package interpolation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.geom.*;
import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.text.*;
import java.util.Properties;
import java.net.URL;
import java.awt.image.*;

/**
 * @author Wolfram Ruehaak
 * @version 2.1
 * @Copyright: GPL Copyright (c) 2003
 * @e-mail: w.ruehaak@online.de,
 * @homepage: http://www.geomath.onlinehome.de
 * @date: 14.12.2004
 *
 *        iw3d: A programm for gridding 3-dimensional scattered data with the
 *        "inverse-distances" method.
 *
 *        This program is free software; you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation; either version 2 of the License, or (at
 *        your option) any later version.
 *
 *        This program is distributed in the hope that it will be useful, but
 *        WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *        General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program; if not, write to the Free Software
 *        Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *        USA
 *
 *        ----------------------------------------------------------------------------
 *        This program reads one data file, then the user has to select the
 *        gridding options, afterwards the calculated data are writen in an
 *        outputfile, further a slice at a selectable depth is written.
 *        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *        New feature in version 1.1: 1) Remove spatial trend before
 *        interpolataion and add it afterward: T(c) = (T(i) - 10)/z[i] T(o) =
 *        (T(c)*depth) + 10 in General for any linear trend: T(c) = (T(i) +
 *        X1)/(X2*Depth) because T is a function of the depth z and begins with
 *        the surface temperature T0 = 10 grade Celsius 2) add a menu option for
 *        blanking for data values with a distance xy / z greater than the mean
 *        xy / z distance (b_blank2)
 *        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *        New feature in version 2.0: 1) Added the abbility to use the 'Octant
 *        Search Method' for compensating clustering effects 2) Calculating &
 *        showing semi-variograms
 */

public class iw3d extends JFrame implements ItemListener {

	/** the static variable for the debug-mode */
	static final boolean debug = false;
	/** Button of the Swing start-frame */
	private JButton startButton;
	/** Button of the Swing start-frame */
	private JButton loadButton;
	/** Button of the Swing start-frame */
	private JButton infoButton;
	/** Checkbox data-file has headingline of the Swing start-frame */
	private JCheckBox headingLine;
	/** Checkbox weighting of the Swing start-frame */
	private JCheckBox check_weighting;
	/** TextArea of the Swing start-frame */
	private JTextArea taskOutput;
	/** the nice self made icon */
	ImageIcon icon;
	URL url;
	URL url2;
	/** the maximum number (+1) of columns and rows of the data-file */
	int ni, nj;
	/** Rechts --> measured data */
	double[] re;
	/** Hoch --> measured data */
	double[] ho;
	/** Depth --> measured data */
	double[] de;
	/** Temperature --> measured data */
	double[] temp;
	/** weighting factor - if we have measured data with different qualitys */
	public static double[] phi;
	/** shall we weight or not */
	public static boolean b_weighting = false;
	/** Interpolated model-temperature */
	double[][][] newt;
	/** loop indizes */
	int x, y, z, i, j, k;;
	/** variable: the total number of grid nodes in x/y/z-direction */
	int nx, ny, nz;
	/** variable the (in every direction) equal sized grid */
	double[] xv, yv, zv;
	/** Name of the Working-Directory */
	public static String Directory;
	/** File to read */
	public static String Filename;
	/** File to write with dat format */
	public static String SaveFilename;
	/** Name of the data file which contains the measured data */
	public static String measureddatafile;
	/** title of the 'open-data' dialog */
	public static String opentitle;
	// *****************************************************************************
	/** variable of <code>opendata</code> */
	double[][] data1;
	/**
	 * variable of <code>opendata</code> the main array holding all the data of
	 * the file we have read
	 */
	double[][] dAllValues;
	// *****************************************************************************
	/** scratch variable - currently global should change soon */
	double[] scr0;
	/** scratch variable - currently global should change soon */
	double[] scr1;
	/** scratch variable - currently global should change soon */
	double[] scr2;
	/** scratch variable - currently global should change soon */
	double[] scr3;
	/** scratch variable - currently global should change soon */
	double[] scr4;
	/**
	 * scratch variable, nr of columns we work with (0,1,2,3 = 3) - currently
	 * global should change soon
	 */
	int i_dummy = 3;
	/** scratch variable - currently global should change soon */
	int count = 0;
	/** scratch variable - currently global should change soon */
	int count2 = 0;
	/** scratch variables */
	public int nrofmeasureddata, nrofmodeldata, nrofnewt;
	/** min/max in x/y/z direction */
	public double minx = 0.F, maxx = 0.F, miny = 0.F, maxy = 0.F, minz = 0.F, maxz = 0.F;
	/** Title of the mainframe */
	String Title;
	/** the grid spacing in x/y/z-direction */
	double dx = 0.F, dy = 0.F, dz = 0.F;
	/**
	 * ------------------ the variables for the BoundarysDialog
	 * --------------------
	 */
	/** variable coming from the BoundarysDialog */
	double min_x, min_y, min_z, max_x, max_y, max_z, dSumT, dMeanT;
	/** variable coming from the BoundarysDialog */
	int nr_x, nr_y, nr_z;
	/** variable coming from the BoundarysDialog */
	double search_x, search_y, search_z;
	/** variable coming from the BoundarysDialog */
	public boolean b_extract_slices;
	/**
	 * variable coming from the BoundarysDialog : file to write with dat format
	 */
	public String slice_filename;
	/** variable coming from the BoundarysDialog */
	int nr_of_slices;
	/** ---------------------------------------------------------------------------- */
	/** normalization variables */
	double relation_y, relation_z;
	/** how less values when to assign a missingvalue */
	static int assign_missing_value_when_lower_than = 0;
	/** marker for missingvalue */
	// static double missingvalue = -9999.;
	static double missingvalue = 1.70141E38; // The Surfer value
	/**
	 * variables for the Inverse-Distance calculation - beta is the power
	 * parameter
	 */
	double beta = 3.5;
	/**
	 * variables for the Inverse-Distance calculation - delta is the smoothing
	 * parameter
	 */
	double delta = 0.;
	/** write the slices as surfer or dat output */
	static boolean surfer_grd = true;
	/** skip first line of the data input file (yes/no) */
	static boolean skip_first_line = false;
	/** Inverse-Distances parameter - the way for searchin: */
	static boolean b_Octant_search = true;
	/**
	 * normalize ? 0 = no, 1 = yes, using grid, 2. = yes, using extension, 3. =
	 * using 1&2
	 */
	int i_normalize = 1;
	/** Normalize only in z-direction ? */
	static boolean normalize_only_z;
	/** Remove a spatial Trend */
	static boolean b_trend = false;
	boolean b_blank2 = false;
	/** function values for trend correction */
	double X1 = 1.0, X2 = 1.0;
	/** variables for Octant search */
	public int NR_EMPTY_OCT = 7, NR_DATA_OCT = 12;
	/** min/max of the data values */
	double min_re, min_ho, min_de, min_temp, max_re, max_ho, max_de, max_temp;

	/** correlation length from the semi-variogram */

	/**
	 * Begin of the main method
	 */
	public iw3d() {

		super("iw3d - 3D Inverse-Distance Interpolation");

		Image image; // the Image is an icon and vice versa ...
		image = null;
		URL url2 = getClass().getResource("iw3d.png");
		// try {
		// image = createImage((ImageProducer) url2.getContent());
		// } catch (IOException ioe) {
		// }
		this.setIconImage(image);

		startButton = new JButton("Start");
		startButton.setActionCommand("start");
		startButton.addActionListener(new ButtonListener());
		startButton.setEnabled(false);

		loadButton = new JButton("Load Data");
		loadButton.setActionCommand("load");
		loadButton.addActionListener(new ButtonListener());

		infoButton = new JButton("Info");
		infoButton.setActionCommand("info");
		infoButton.addActionListener(new ButtonListener());

		taskOutput = new JTextArea(19, 40);
		taskOutput.setLineWrap(true);
		taskOutput.setWrapStyleWord(true);
		taskOutput.setMargin(new Insets(5, 5, 5, 5));
		taskOutput.setEditable(false);

		taskOutput.append(
				"iw3d, Version 2.1, December 14 2004\n" + "This is a program for quality-weighted 3-d interpolation\n"
						+ "of scattered data with the 'inverse-distance weighting' algorithm\n"
						+ "Press 'Info' button for more information.\n" + "Contact:\n"
						+ "e-mail: w.ruehaak@online.de, http://www.geomath.onlinehome.de\n");

		JPanel panel = new JPanel();
		panel.add(loadButton);
		panel.add(startButton);
		panel.add(infoButton);

		url = ClassLoader.getSystemResource("iw3d.png");
		icon = (url != null) ? new ImageIcon(url) : null;
		JLabel label = new JLabel(icon);
		panel.add(label);

		headingLine = new JCheckBox("Data file has heading line?", false);
		check_weighting = new JCheckBox("5th column contains weighting values?", false);
		JPanel panel2 = new JPanel(new GridLayout(2, 1));
		JPanel panel3 = new JPanel(new GridLayout(2, 1));
		panel2.add(headingLine);
		panel2.add(check_weighting);

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(panel, BorderLayout.NORTH);
		contentPane.add(panel2);
		contentPane.add(panel3, BorderLayout.EAST);
		contentPane.add(new JScrollPane(taskOutput), BorderLayout.SOUTH);
		contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setContentPane(contentPane);

		headingLine.addItemListener(this);
		check_weighting.addItemListener(this);
		
		Progress prg = new Progress();
		// start Thread
		prg.start();
	}

	/**
	 * ItemStateChanged is an overwritten (?) method for handling action-events
	 * at the start-frame (buttons, checkboxes)
	 */
	public void itemStateChanged(ItemEvent e) {
		if ((JCheckBox) e.getSource() == headingLine) {
			JCheckBox headingLine = (JCheckBox) e.getSource();
			int change = e.getStateChange();
			if (change == ItemEvent.SELECTED) {
				skip_first_line = true;
				if (debug) {
					System.out.println(headingLine.getText() + ": SELECTED");
					System.out.println("skip_first_line = " + skip_first_line);
				}
			} else if (change == ItemEvent.DESELECTED) {
				skip_first_line = false;
				if (debug) {
					System.out.println(headingLine.getText() + ": DESELECTED");
					System.out.println("skip_first_line = " + skip_first_line);
				}
			}
		} else if ((JCheckBox) e.getSource() == check_weighting) {
			JCheckBox check_weighting = (JCheckBox) e.getSource();
			int change = e.getStateChange();
			if (change == ItemEvent.SELECTED) {
				b_weighting = true;
				if (debug) {
					System.out.println(check_weighting.getText() + ": SELECTED");
					System.out.println("weighting = " + b_weighting);
				}
			} else if (change == ItemEvent.DESELECTED) {
				b_weighting = false;
				if (debug) {
					System.out.println(check_weighting.getText() + ": DESELECTED");
					System.out.println("weighting = " + b_weighting);
				}
			}
		}
	}

	/**
	 * The actionPerformed method in this class is called when the user presses
	 * the start button.
	 */
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			String command = evt.getActionCommand();
			if (command.equals("start")) {
				startButton.setEnabled(false);
				loadButton.setEnabled(false);
				if (debug)
					System.out.println("calciw3d ni = " + " " + ni);
				Progress prg = new Progress();
				// start Thread
				prg.start();

				loadButton.setEnabled(false);
				startButton.setEnabled(false);
			} else if (command.equals("load")) {
				loadData();
				startButton.setEnabled(true);
			} else if (command.equals("info")) {
				taskOutput.replaceRange(null, 0, taskOutput.getDocument().getLength());
				taskOutput.insert(text1(), 0);
				taskOutput.setCaretPosition(0);
			}
		}
	}

	class Progress extends Thread {
//		public void run() {
//			Calciw3d calc = new Calciw3d(debug, re, ho, de, temp, phi, ni, nx, ny, nz, b_trend, b_blank2, X1, X2, min_x,
//					max_x, min_y, max_y, min_z, max_z, normalize_only_z, i_normalize, beta, delta,
//					assign_missing_value_when_lower_than, missingvalue, b_Octant_search, surfer_grd, min_temp, max_temp,
//					b_extract_slices, slice_filename, dSumT, dMeanT, NR_EMPTY_OCT, NR_DATA_OCT);
//		}
	}

	private void loadData() {
		OpenData("Open scattered data (ASCII space/tab/,/; seperated 4 columns !!!)");
		if (Directory == null && Filename == null) {
			loadButton.setEnabled(true); // funktioniert nicht !!!
			startButton.setEnabled(false);
			return;
		}
		Calciw3d calc = new Calciw3d(debug, re, ho, de, temp, phi, ni, nx, ny, nz, b_trend, b_blank2, X1, X2, min_x,
				max_x, min_y, max_y, min_z, max_z, normalize_only_z, i_normalize, beta, delta,
				assign_missing_value_when_lower_than, missingvalue, b_Octant_search, surfer_grd, min_temp, max_temp,
				b_extract_slices, slice_filename, dSumT, dMeanT, NR_EMPTY_OCT, NR_DATA_OCT);

	}



	/**
	 * The method getdata extracts the 4 (with weighting 5) columns we are
	 * interested in
	 */
	protected void getdata(int ni, int nj, double data[][]) {
		// ni = nr_of_columns; nj = nr_of_rows;
		if (debug)
			System.out.println("finished open data " + ni + " " + nj + " " + ((ni + 1) * (nj + 1)));
		int nip1 = ni + 1;
		scr0 = new double[nip1];
		scr1 = new double[nip1];
		scr2 = new double[nip1];
		scr3 = new double[nip1];
		if (b_weighting)
			scr4 = new double[nip1];

		for (i = 0; i <= ni; i++) {
			scr0[i] = data[i][0];
			scr1[i] = data[i][1];
			scr2[i] = data[i][2];
			scr3[i] = data[i][3];
			if (b_weighting)
				scr4[i] = data[i][4];
		}
		if (debug)
			System.out.println("Finished getdata");
	}

	/**
	 * get the data back from the BoundarysDiaolog routine
	 */
	public void getBoundarys(String min_x_new, String max_x_new, String nr_x_new, double search_x_new, String min_y_new,
			String max_y_new, String nr_y_new, double search_y_new, String min_z_new, String max_z_new, String nr_z_new,
			double search_z_new, boolean b_extract_slices_new, String slice_filename_new, double new_beta,
			double new_delta, double new_missingvalue, int assign_missing_value_when_lower_than2, int new_normalize,
			boolean normalize_only_z2, boolean b_Octantsearch2, boolean write_surfer_grd, boolean b_trend_dlg,
			double X1_dlg, double X2_dlg, boolean b_blank2_dlg, int NR_EMPTY_OCT2, int NR_DATA_OCT2) {

		NumberFormat nf = NumberFormat.getNumberInstance();
		try {
			min_x = nf.parse(min_x_new).doubleValue();
			min_y = nf.parse(min_y_new).doubleValue();
			min_z = nf.parse(min_z_new).doubleValue();

			max_x = nf.parse(max_x_new).doubleValue();
			max_y = nf.parse(max_y_new).doubleValue();
			max_z = nf.parse(max_z_new).doubleValue();

			nx = nf.parse(nr_x_new).intValue();
			ny = nf.parse(nr_y_new).intValue();
			nz = nf.parse(nr_z_new).intValue();
		} catch (ParseException pe) {
			System.out.println(pe);
		}
		beta = new_beta;
		delta = new_delta;
		missingvalue = new_missingvalue;
		assign_missing_value_when_lower_than = assign_missing_value_when_lower_than2;
		i_normalize = new_normalize;
		normalize_only_z = normalize_only_z2;
		b_Octant_search = b_Octantsearch2;
		surfer_grd = write_surfer_grd;
		b_blank2 = b_blank2_dlg;
		b_trend = b_trend_dlg;
		X1 = X1_dlg;
		X2 = X2_dlg;
		NR_EMPTY_OCT = NR_EMPTY_OCT2;
		NR_DATA_OCT = NR_DATA_OCT2;
		if (debug) {
			System.out.println("~~~~~~~~~~~~~~~~~~~~~ start getBoundarys ~~~~~~~~~~~~~~~~~~~~~" + "\n"
					+ "beta, delta, missingv.: " + beta + " " + delta + " " + missingvalue + "\n"
					+ "normalize_only_z, b_Octant_search, surfer_grd: " + normalize_only_z + " " + b_Octant_search + " "
					+ surfer_grd + "\n" + "trend, blank, X1, X2: " + b_trend + " " + b_blank2 + " " + X1 + " " + X2
					+ "\n" + "ni, nj: " + ni + " " + nj + "\n" + "NR_EMPTY_OCT: " + NR_EMPTY_OCT + " NR_DATA_OCT "
					+ NR_DATA_OCT + "\n"
					+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ end getBoundarys ~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		}
		b_extract_slices = b_extract_slices_new;
		slice_filename = slice_filename_new;

		if (debug)
			System.out.println(min_x + " " + max_x + " " + nx + " " + search_x + " " + '\n' + min_y + " " + max_y + " "
					+ ny + " " + search_y + " " + '\n' + min_z + " " + max_z + " " + nz + " " + search_z + " " + '\n'
					+ slice_filename_new + " " + b_extract_slices);
	}

	/**
	 * @return <code>text1</code> the text shown in the TextArea of the
	 *         mainFrame
	 */
	private static final String text1() {
		int i = 0;
		double[] nr1 = new double[80];
		String text0 = " - that is (maybe) not so good";
		BufferedReader r = new BufferedReader(new StringReader(System.getProperty("java.version")));
		StreamTokenizer st = new StreamTokenizer(r);
		try {
			while (st.nextToken() != st.TT_EOF) {
				if (debug == true)
					System.out.println("st.nval = " + st.nval);
				if (debug == true)
					System.out.println("st.sval = " + st.sval);
				nr1[i] = st.nval;
				st.nextToken();
				i++;
			}
		} catch (java.io.IOException exc) {
			System.out.println(exc);
		}
		if (debug)
			System.out.println(nr1[0]);
		if (nr1[0] >= 1.2) {
			text0 = " - that is good.";
		}

		String text1 = "Hello " + System.getProperty("user.name") + "\n" + "you're working on "
				+ System.getProperty("os.name") + "\nwith " + System.getProperty("sun.cpu.isalist") + ",\n"
				+ System.getProperty("java.runtime.name") + "," + System.getProperty("java.vm.version") + "\n" + text0
				+ "\n\n" + "iw3d, Version 2.0, 06/23/2004\n" + "A program for 3D-interpolation of scattered data\n"
				+ "Author: Wolfram Ruehaak, \n" + "e-mail: w.ruehaak@online.de, http://www.geomath.onlinehome.de\n"
				+ "To use this program please press first the load button, "
				+ "in the following open-dialog you should select the data file "
				+ "you want to interpolate. This data file has to be in ASCII format, "
				+ "space or tab or ',' or ';' seperated 4 columns "
				+ "(if you didn't select '5th column contains weighting values' "
				+ "the program ignores the columns after the 4th).\n"
				+ "It is possible to include an additional weight to the interpolation."
				+ "This value is read from the 5th column."
				+ "The amount of this weight is not important, but the relation. If the weight is zero the value "
				+ "isn't included at all.\n" + "After selecting the data file a dialog comes up "
				+ "where the dimensions & spacing of the "
				+ "new gridded file can be entered. Additionally it is possible to select a lot "
				+ "of special settings for the Inverse-Distance Weighting (IDW) algorithm. "
				+ "Please take a look at the readme-file for more information. \n" + "\n"
				+ "This program is free software; you can redistribute it and/or "
				+ "modify it under the terms of the GNU General Public "
				+ "License as published by the Free Software Foundation; either "
				+ "version 2 of the License, or (at your option) any later version.\n"
				+ "iw3d comes with ABSOLUTELY NO WARRANTY " + "without even the implied warranty of MERCHANTABILITY "
				+ "or FITNESS FOR A PARTICULAR PURPOSE.\n" + "See the GNU General Public License for more details\n"
				+ "You should have received a copy of the GNU Library General Public "
				+ "License along with this program; if not, write to the "
				+ "Free Software Foundation, Inc., 59 Temple Place - Suite 330, " + "Boston, MA  02111-1307, USA.\n\n";
		return text1;
	}

	/** the main method */
	public static void main(String[] args) {
		JFrame frame = new iw3d();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.setLocation(100, 100);
		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * The OpenData Method, opens a FileDialog - calls Open(Directory, Filename)
	 */
	public void OpenData(String opentitle) {
		// FileDialog d1 = new FileDialog(this, opentitle, FileDialog.LOAD);
		// d1.setDirectory(Directory);
		// d1.setModal(true);
		// d1.setVisible(true);
		// Directory = d1.getDirectory();
		// Filename = d1.getFile();
		Directory = "src/interpolation/Data/testdata/";
//		Directory = "D:\\FocusMap\\程序代码\\IW3D_Java三维插值\\testdata\\";
		Filename = "original-3D.dat";

		// d1.dispose();
		if (debug)
			System.out.println("next ...");
		if (Directory != null && Filename != null) {
			// ReadData rd = new ReadData(this, Directory, Filename,
			// skip_first_line, debug);
			GetValues gv = new GetValues(this);
		} else {
			loadButton.setEnabled(true); // not working !!!
			startButton.setEnabled(false);
			return;
		}
	}

	public void getReadData(int new_ni, int nj, double[][] dAllValues_new) {

		ni = new_ni;

		if (debug)
			System.out.println("ni,nj (1): " + ni + " " + nj);
		data1 = new double[ni + 1][nj + 1];
		for (i = 0; i <= ni; i++) {
			for (j = 0; j <= nj; j++) {
				// Better ==> ArrayCopy ... ?
				data1[i][j] = dAllValues_new[i][j];
			}
		}

		getdata(ni, nj, data1); // call getdata

		nrofmeasureddata = ni;
		re = new double[nrofmeasureddata + 1];
		ho = new double[nrofmeasureddata + 1];
		de = new double[nrofmeasureddata + 1];
		temp = new double[nrofmeasureddata + 1];
		phi = new double[nrofmeasureddata + 1];

		min_re = (double) (+1.E+64);
		min_ho = (double) (+1.E+64);
		min_de = (double) (+1.E+64);
		min_temp = (double) (+1.E+64);
		max_re = (double) (-1.E+64);
		max_ho = (double) (-1.E+64);
		max_de = (double) (-1.E+64);
		max_temp = (double) (-1.E+64);
		if (debug)
			System.out.println("weighting = " + b_weighting);

		dSumT = 0.0;

		for (k = 0; k <= ni; k++) {
			// Better ==> ArrayCopy ... ?
			re[k] = scr0[k];
			ho[k] = scr1[k];
			de[k] = scr2[k];
			temp[k] = scr3[k];
			if (b_weighting) {
				phi[k] = scr4[k];
			} else {
				phi[k] = 1.0;
			}
			min_re = Math.min(min_re, re[k]);
			min_ho = Math.min(min_ho, ho[k]);
			min_de = Math.min(min_de, de[k]);
			min_temp = Math.min(min_temp, temp[k]);

			max_re = Math.max(max_re, re[k]);
			max_ho = Math.max(max_ho, ho[k]);
			max_de = Math.max(max_de, de[k]);
			max_temp = Math.max(max_temp, temp[k]);
			dSumT = dSumT + temp[k];
		}
		dMeanT = dSumT / (ni + 1);

		/*
		 * if(b_weighting){ showHistogram sh = new
		 * showHistogram("Weighting Classes Histogram",ni,phi); }
		 */
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// Calling the Variogram routine
		variogram3d v = new variogram3d(this, debug, re, ho, de, temp, min_re, max_re, min_ho, max_ho, min_de, max_de,
				ni);
	}

	/** get Correlation length call Boundarys Dialog */
	public void getCorrelationLength() {

		// call BoundarysDialog ==> a dialog where the user cann select the
		// gridding options
		BoundarysDialog b = new BoundarysDialog(this, "grid boundarys", debug, ni, min_re, min_ho, min_de, min_temp,
				max_re, max_ho, max_de, max_temp, beta, delta, missingvalue, assign_missing_value_when_lower_than,
				i_normalize, surfer_grd, NR_EMPTY_OCT, NR_DATA_OCT, b_Octant_search);
		// b.setVisible(true);
	}

	/** reset */
	public void reset() {
		if (debug)
			System.out.println("reset 1");
		startButton.setEnabled(false);
		loadButton.setEnabled(true);
	}
}
