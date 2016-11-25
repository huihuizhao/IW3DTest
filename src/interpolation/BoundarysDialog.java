package interpolation;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.*;

/**
 * @author Wolfram Ruehaak
 * @version 2.0
 * @Copyright: GPL Copyright (c) 2004
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
 *            furthermore it returns if sclices shall be extracted for every
 *            depth and the filename this sclice shall have.
 */

public class BoundarysDialog extends JFrame implements ActionListener {
	boolean debug;

	String min_x_new, max_x_new, min_y_new, max_y_new, min_z_new, max_z_new;
	String s_df = null;

	/** String containing the number of grid point in x-direction */
	String nr_x_new = null;
	/** String containing the number of grid points in y-direction */
	String nr_y_new = null;
	/** String containing the number of grid points in z-direction */
	String nr_z_new = null;
	/**
	 * String containing the ending filename of the extracted slices
	 * #depth#filename
	 */
	String slice_filename_new;
	/** the okay button */
	protected Button okay;
	/** the cancel button */
	protected Button cancel;
	/** the help button */
	protected Button help;

	TextField T_x_min = new TextField();
	TextField T_y_min = new TextField();
	TextField T_z_min = new TextField();
	TextField T_x_max = new TextField();
	TextField T_y_max = new TextField();
	TextField T_z_max = new TextField();
	TextField T_x_nr = new TextField();
	TextField T_y_nr = new TextField();
	TextField T_z_nr = new TextField();

	TextArea T_dx = new TextArea();
	TextArea T_dy = new TextArea();
	TextArea T_dz = new TextArea();
	TextArea TA_wx = new TextArea();
	TextArea TA_wy = new TextArea();
	TextArea TA_wz = new TextArea();

	TextField T_slice_filename = new TextField();

	CheckboxGroup checkboxgroup = new CheckboxGroup();
	Checkbox[] checkboxes = new Checkbox[4];
	Checkbox checkbox_z = new Checkbox();
	CheckboxGroup grd_checkbox_group = new CheckboxGroup();
	Checkbox checkbox_grd_srf = new Checkbox();
	Checkbox checkbox_grd_dat = new Checkbox();
	Checkbox checkbox_depth_grd = new Checkbox();
	boolean normalize_only_z = false;
	boolean b_octant_search;
	boolean b_extract_slices = true;
	int i;
	int my_ni;
	double min_x, min_y, min_z;
	double max_x, max_y, max_z;
	int nr_x, nr_y, nr_z;
	double dx, dy, dz;
	double new_beta, new_delta, new_missingvalue;
	double wx, wy, wz;
	int new_normalize;
	int nr_assign_missingvalue;
	public static final int MAX_ENTRY = 1500;
	private String s_fileinfo;
	long YourNormjobTime = 0;
	long YourWorkTime = 0;
	boolean write_surfer_grd, b_blank2, b_trend;
	double X1, X2;
	double correlation_length_x, correlation_length_y, correlation_length_z;
	public static double search_x_new, search_y_new, search_z_new;
	int new_NR_EMPTY_OCT, new_NR_DATA_OCT;

	DecimalFormat df = new DecimalFormat();
	static final DecimalFormat df_E = new DecimalFormat("0.00E00");
	static final DecimalFormat df_F = new DecimalFormat("0.0####");
	static final NumberFormat nf = NumberFormat.getNumberInstance();

	iw3d parent;

	public BoundarysDialog(JFrame f, String title, boolean debug, int ni, double min_re, double min_ho, double min_de,
			double min_temp, double max_re, double max_ho, double max_de, double max_temp, double beta, double delta,
			double missingvalue, int assign_missing_value_when_lower_than, int normalize, boolean surfer_grd,
			int NR_EMPTY_OCT, int NR_DATA_OCT, boolean b_octant_search_old) {

		super("Boundarys Dialog");

		parent = (iw3d) f;

		b_octant_search = b_octant_search_old;
		write_surfer_grd = surfer_grd;
		nr_assign_missingvalue = assign_missing_value_when_lower_than;
		new_normalize = normalize;
		search_x_new = variogram3d.correlation_length_x;
		search_y_new = variogram3d.correlation_length_y;
		search_z_new = variogram3d.correlation_length_z;

		this.getContentPane().setLayout(new BorderLayout(15, 15));
		my_ni = ni;
		new_beta = beta;
		new_delta = delta;
		new_missingvalue = missingvalue;
		new_NR_EMPTY_OCT = NR_EMPTY_OCT;
		new_NR_DATA_OCT = NR_DATA_OCT;

		s_fileinfo = "nr of values = " + (ni + 1) + "\n" + "Min(x) = " + min_re + "\n" + "Max(x) = " + max_re + "\n"
				+ "Width(x) = " + (max_re - min_re) + "\n\n" + "Min(y) = " + min_ho + "\n" + "Max(y) = " + max_ho + "\n"
				+ "Width(y) = " + (max_ho - min_ho) + "\n\n" + "Min(z) = " + min_de + "\n" + "Max(z) = " + max_de + "\n"
				+ "Width(z) = " + (max_de - min_de) + "\n\n" + "Min(f) = " + min_temp + "\n" + "Max(f) = " + max_temp
				+ "\n" + "Width(f) = " + (max_temp - min_temp);
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		Panel p0 = new Panel();
		p0.setLayout(new GridLayout(5, 6));
		this.getContentPane().add(p0, "North");

		Button okay = new Button("Okay");
		okay.setActionCommand("okay");
		okay.addActionListener(this);

		Button cancel = new Button("Cancel");
		cancel.setActionCommand("cancel");
		cancel.addActionListener(this);

		Button FileInfo = new Button("File Info");
		FileInfo.setActionCommand("FileInfo");
		FileInfo.addActionListener(this);

		Button advanced = new Button("Advanced");
		advanced.setActionCommand("advanced");
		advanced.addActionListener(this);

		Button help = new Button("Help");
		help.setActionCommand("help");
		help.addActionListener(this);
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		int a;
		double deziDimX = 10., deziDimY = 10., deziDimZ = 10.;

		for (a = 0; a <= 20; a++) {
			if (Math.pow(10., a) >= (max_re - min_re)) {
				deziDimX = (double) Math.pow(10, a);
				deziDimX = (double) (deziDimX / 1000.);
				break;
			}
		}
		for (a = 0; a <= 20; a++) {
			if (Math.pow(10., a) >= (max_ho - min_ho)) {
				deziDimY = (double) Math.pow(10, a);
				deziDimY = (double) (deziDimY / 1000.);
				break;
			}
		}
		for (a = 0; a <= 20; a++) {
			if (Math.pow(10., a) >= (max_de - min_de)) {
				deziDimZ = (double) Math.pow(10, a);
				deziDimZ = (double) (deziDimZ / 1000.);
				break;
			}
		}
		min_x = deziDimX * Math.floor(min_re / deziDimX);
		max_x = deziDimX * Math.ceil(max_re / deziDimX);
		nr_x_new = "" + 20;
		min_y = deziDimY * Math.floor(min_ho / deziDimY);
		max_y = deziDimY * Math.ceil(max_ho / deziDimY);
		double relxy = (max_ho - min_ho) / (max_re - min_re);
		int scr = (int) Math.round(20.F * relxy);
		nr_y_new = "" + scr;
		min_z = deziDimZ * Math.floor(min_de / deziDimZ);
		max_z = deziDimZ * Math.ceil(max_de / deziDimZ);
		nr_z_new = "" + 20;

		// *********************************************************************
		if (Math.abs(min_x) > 1.E+08 || Math.abs(min_x) < 1.E-08 && min_x != 0.0) {
			s_df = (df_E.format(min_x));
		} else {
			s_df = (df_F.format(min_x));
		}
		T_x_min = new TextField(s_df, 10);
		min_x_new = Double.toString(min_x);
		// *********************************************************************
		if (Math.abs(max_x) > 1.E+08 || Math.abs(max_x) < 1.E-08 && max_x != 0.0) {
			s_df = (df_E.format(max_x));
		} else {
			s_df = (df_F.format(max_x));
		}
		T_x_max = new TextField(s_df, 10);
		max_x_new = Double.toString(max_x);
		// *********************************************************************
		wx = max_x - min_x;
		if (Math.abs(wx) > 1.E+08 || Math.abs(wx) < 1.E-08) {
			s_df = (df_E.format(wx));
		} else {
			s_df = (df_F.format(wx));
		}
		TA_wx = new TextArea(s_df, 1, 1, 3);
		TA_wx.setEditable(false);
		T_x_nr = new TextField(nr_x_new, 10);
		try {
			nr_x = nf.parse(nr_x_new).intValue();
		} catch (ParseException pe) {
			System.out.println(pe);
		}
		dx = (max_x - min_x) / nr_x;
		if (Math.abs(dx) > 1.E+08 || Math.abs(dx) < 1.E-08) {
			s_df = (df_E.format(dx));
		} else {
			s_df = (df_F.format(dx));
		}
		T_dx = new TextArea(s_df, 1, 1, 3);
		T_dx.setEditable(false);
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		if (Math.abs(min_y) > 1.E+08 || Math.abs(min_y) < 1.E-08 && min_y != 0.0) {
			s_df = (df_E.format(min_y));
		} else {
			s_df = (df_F.format(min_y));
		}
		T_y_min = new TextField(s_df, 10);
		min_y_new = Double.toString(min_y);

		if (Math.abs(max_y) > 1.E+08 || Math.abs(max_y) < 1.E-08 && max_y != 0.0) {
			s_df = (df_E.format(max_y));
		} else {
			s_df = (df_F.format(max_y));
		}
		T_y_max = new TextField(s_df, 10);
		max_y_new = Double.toString(max_y);

		wy = max_y - min_y;
		if (Math.abs(wy) > 1.E+08 || Math.abs(wy) < 1.E-08) {
			s_df = (df_E.format(wy));
		} else {
			s_df = (df_F.format(wy));
		}
		TA_wy = new TextArea(s_df, 1, 1, 3);
		TA_wy.setEditable(false);

		T_y_nr = new TextField(nr_y_new, 10);
		try {
			nr_y = nf.parse(nr_y_new).intValue();
		} catch (ParseException pe) {
			System.out.println(pe);
		}
		dy = (max_y - min_y) / nr_y;
		if (Math.abs(dy) > 1.E+08 || Math.abs(dy) < 1.E-08) {
			s_df = (df_E.format(dy));
		} else {
			s_df = (df_F.format(dy));
		}
		T_dy = new TextArea(s_df, 1, 1, 3);
		T_dy.setEditable(false);
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		if (Math.abs(min_z) > 1.E+08 || Math.abs(min_z) < 1.E-08 && min_z != 0.0) {
			s_df = (df_E.format(min_z));
		} else {
			s_df = (df_F.format(min_z));
		}
		T_z_min = new TextField(s_df, 10);
		min_z_new = Double.toString(min_z);

		if (Math.abs(max_z) > 1.E+08 || Math.abs(max_z) < 1.E-08 && max_z != 0.0) {
			s_df = (df_E.format(max_z));
		} else {
			s_df = (df_F.format(max_z));
		}
		T_z_max = new TextField(s_df, 10);
		max_z_new = Double.toString(max_z);

		wz = max_z - min_z;
		if (Math.abs(wz) > 1.E+08 || Math.abs(wz) < 1.E-08) {
			s_df = (df_E.format(wz));
		} else {
			s_df = (df_F.format(wz));
		}
		TA_wz = new TextArea(s_df, 1, 1, 3);
		TA_wz.setEditable(false);

		T_z_nr = new TextField(nr_z_new, 10);
		try {
			nr_z = nf.parse(nr_z_new).intValue();
		} catch (ParseException pe) {
			System.out.println(pe);
		}
		dz = (max_z - min_z) / nr_z;

		if (Math.abs(dz) > 1.E+08 || Math.abs(dz) < 1.E-08) {
			s_df = (df_E.format(dz));
		} else {
			s_df = (df_F.format(dz));
		}
		T_dz = new TextArea(s_df, 1, 1, 3);
		T_dz.setEditable(false);
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		p0.add(okay);
		p0.add(cancel);
		p0.add(new Label(""));
		p0.add(advanced);
		p0.add(FileInfo);
		p0.add(help);
		p0.add(new Label(""));
		p0.add(new Label("min"));
		p0.add(new Label("max"));
		p0.add(new Label("width"));
		p0.add(new Label("# of lines"));
		p0.add(new Label("spacing"));
		p0.add(new Label("X-direction"));
		p0.add(T_x_min);
		p0.add(T_x_max);
		p0.add(TA_wx);
		p0.add(T_x_nr);
		p0.add(T_dx);
		p0.add(new Label("Y-direction"));
		p0.add(T_y_min);
		p0.add(T_y_max);
		p0.add(TA_wy);
		p0.add(T_y_nr);
		p0.add(T_dy);
		p0.add(new Label("Z-direction"));
		p0.add(T_z_min);
		p0.add(T_z_max);
		p0.add(TA_wz);
		p0.add(T_z_nr);
		p0.add(T_dz);
		Panel p1 = new Panel();
		Panel p2 = new Panel();
		p2.setLayout(new GridLayout(6, 5));
		this.getContentPane().add(p2, "Center");
		Panel p3 = new Panel();
		p3.setLayout(new GridLayout(6, 5));
		this.getContentPane().add(p3, "East");
		Panel p4 = new Panel();
		p4.setLayout(new GridLayout(6, 5));
		this.getContentPane().add(p4, "West");

		checkbox_depth_grd = new Checkbox("extract slice for all depths?", b_extract_slices);
		p4.add(checkbox_depth_grd);
		ItemListener checkbox_depth_grd_listener = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				b_extract_slices = ((Checkbox) ie.getItemSelectable()).getState();
				T_slice_filename.setEditable(b_extract_slices);
			}
		};
		checkbox_depth_grd.addItemListener(checkbox_depth_grd_listener);

		p2.add(new Label(""));

		p4.add(new Label("slice filename(s) ending with:"));
		T_slice_filename = new TextField("-slice.grd", 10);
		T_slice_filename.setEditable(b_extract_slices);
		p2.add(T_slice_filename);

		checkbox_grd_srf = new Checkbox("Surfer(tm) GS ASCII grid-file", grd_checkbox_group, write_surfer_grd);
		boolean b_scr;
		if (write_surfer_grd) {
			b_scr = false;
		} else {
			b_scr = true;
		}
		checkbox_grd_dat = new Checkbox("3-column ASCII dat file", grd_checkbox_group, b_scr);
		p4.add(new Label("format for the slices?"));
		p2.add(checkbox_grd_srf);
		p2.add(checkbox_grd_dat);

		ItemListener checkbox_grd_srf_listener = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				write_surfer_grd = ((Checkbox) ie.getItemSelectable()).getState();
			}
		};
		ItemListener checkbox_grd_dat_listener = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				boolean b_scr = ((Checkbox) ie.getItemSelectable()).getState();
				if (b_scr) {
					write_surfer_grd = false;
				} else {
					write_surfer_grd = true;
				}
			}
		};
		checkbox_grd_srf.addItemListener(checkbox_grd_srf_listener);
		checkbox_grd_dat.addItemListener(checkbox_grd_dat_listener);
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		boolean b_scr0 = false, b_scr1 = true, b_scr2 = false, b_scr3 = false;
		if (new_normalize == 0) {
			b_scr0 = true;
			b_scr1 = false;
			b_scr2 = false;
			b_scr3 = false;
		} else if (new_normalize == 1) {
			b_scr0 = false;
			b_scr1 = true;
			b_scr2 = false;
			b_scr3 = false;
		} else if (new_normalize == 2) {
			b_scr0 = false;
			b_scr1 = false;
			b_scr2 = true;
			b_scr3 = false;
		} else if (new_normalize == 3) {
			b_scr0 = false;
			b_scr1 = false;
			b_scr2 = false;
			b_scr3 = true;
		}

		checkboxes[0] = new Checkbox("1 - none", checkboxgroup, b_scr0);
		checkboxes[1] = new Checkbox("2 - grid", checkboxgroup, b_scr1);
		checkboxes[2] = new Checkbox("3 - data-extensions", checkboxgroup, b_scr2);
		checkboxes[3] = new Checkbox("4 - grid & extension", checkboxgroup, b_scr3);
		p3.add(new Label("standardization:"));
		for (i = 0; i < checkboxes.length; i++)
			p3.add(checkboxes[i]);
		ItemListener checkbox_listener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String scr = new String();
				scr = ((Checkbox) e.getItemSelectable()).getLabel();
				StringTokenizer st = new StringTokenizer(scr);
				new_normalize = Integer.parseInt(st.nextToken()) - 1;
			}
		};

		for (i = 0; i < checkboxes.length; i++)
			checkboxes[i].addItemListener(checkbox_listener);
		checkbox_z = new Checkbox("standardize only z", false);
		p3.add(checkbox_z);
		ItemListener checkbox_z_listener = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				normalize_only_z = ((Checkbox) ie.getItemSelectable()).getState();
			}
		};
		checkbox_z.addItemListener(checkbox_z_listener);
		// ---------------------------------------------------------------------
		// ++++++++++++++++++++ SOME EVENTS ... ********************************
		T_x_nr.addTextListener(new TextListener() {
			public void textValueChanged(TextEvent e) {
				min_x_new = (T_x_min.getText());
				max_x_new = (T_x_max.getText());
				try {
					min_x = nf.parse(min_x_new).doubleValue();
					max_x = nf.parse(max_x_new).doubleValue();
					nr_x = nf.parse((T_x_nr).getText()).intValue();
				} catch (ParseException pe) {
					System.out.println(pe);
				}
				dx = (max_x - min_x) / nr_x;
				if (Math.abs(dx) > 1.E+08 || Math.abs(dx) < 1.E-08) {
					s_df = (df_E.format(dx));
				} else {
					s_df = (df_F.format(dx));
				}
				T_dx.setText(s_df);
			}
		});

		T_y_nr.addTextListener(new TextListener() {
			public void textValueChanged(TextEvent e) {
				min_y_new = (T_y_min.getText());
				max_y_new = (T_y_max.getText());
				try {
					min_y = nf.parse(min_y_new).doubleValue();
					max_y = nf.parse(max_y_new).doubleValue();
					nr_y = nf.parse(((TextField) e.getSource()).getText()).intValue();
				} catch (ParseException pe) {
					System.out.println(pe);
				}
				dy = (max_y - min_y) / nr_y;
				if (Math.abs(dy) > 1.E+08 || Math.abs(dy) < 1.E-08) {
					s_df = (df_E.format(dy));
				} else {
					s_df = (df_F.format(dy));
				}
				T_dy.setText(s_df);
			}
		});

		T_z_nr.addTextListener(new TextListener() {
			public void textValueChanged(TextEvent e) {
				min_z_new = (T_z_min.getText());
				max_z_new = (T_z_max.getText());
				try {
					min_z = nf.parse(min_z_new).doubleValue();
					max_z = nf.parse(max_z_new).doubleValue();
					nr_z = nf.parse(((TextField) e.getSource()).getText()).intValue();
				} catch (ParseException pe) {
					System.out.println(pe);
				}
				dz = (max_z - min_z) / nr_z;
				if (Math.abs(dz) > 1.E+08 || Math.abs(dz) < 1.E-08) {
					s_df = (df_E.format(dz));
				} else {
					s_df = (df_F.format(dz));
				}
				T_dz.setText(s_df);
			}
		});
		T_x_min.addTextListener(new TextListener() {
			public void textValueChanged(TextEvent e) {
				try {
					max_x = nf.parse(T_x_max.getText()).doubleValue();
					min_x = nf.parse(T_x_min.getText()).doubleValue();
				} catch (ParseException pe) {
					System.out.println(pe);
				}
				wx = (max_x - min_x);
				if (Math.abs(wx) > 1.E+08 || Math.abs(wx) < 1.E-08) {
					s_df = (df_E.format(wx));
				} else {
					s_df = (df_F.format(wx));
				}
				TA_wx.setText(s_df);
				dx = (max_x - min_x) / nr_x;
				if (Math.abs(dx) > 1.E+08 || Math.abs(dx) < 1.E-08) {
					s_df = (df_E.format(dx));
				} else {
					s_df = (df_F.format(dx));
				}
				T_dx.setText(s_df);
			}
		});

		T_y_min.addTextListener(new TextListener() {
			public void textValueChanged(TextEvent e) {
				try {
					max_y = nf.parse(T_y_max.getText()).doubleValue();
					min_y = nf.parse(T_y_min.getText()).doubleValue();
				} catch (ParseException pe) {
					System.out.println(pe);
				}
				wy = (max_y - min_y);
				if (Math.abs(wy) > 1.E+08 || Math.abs(wy) < 1.E-08) {
					s_df = (df_E.format(wy));
				} else {
					s_df = (df_F.format(wy));
				}
				TA_wy.setText(s_df);
				dy = (max_y - min_y) / nr_y;
				if (Math.abs(dy) > 1.E+08 || Math.abs(dy) < 1.E-08) {
					s_df = (df_E.format(dy));
				} else {
					s_df = (df_F.format(dy));
				}
				T_dy.setText(s_df);
			}
		});

		T_z_min.addTextListener(new TextListener() {
			public void textValueChanged(TextEvent e) {

				try {
					max_z = nf.parse(T_z_max.getText()).doubleValue();
					min_z = nf.parse(T_z_min.getText()).doubleValue();
				} catch (ParseException pe) {
					System.out.println(pe);
				}
				wz = (max_z - min_z);
				if (Math.abs(wz) > 1.E+08 || Math.abs(wz) < 1.E-08) {
					s_df = (df_E.format(wz));
				} else {
					s_df = (df_F.format(wz));
				}
				TA_wz.setText(s_df);
				dz = (max_z - min_z) / nr_z;
				if (Math.abs(dz) > 1.E+08 || Math.abs(dz) < 1.E-08) {
					s_df = (df_E.format(dz));
				} else {
					s_df = (df_F.format(dz));
				}
				T_dz.setText(s_df);
			}
		});
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		T_x_max.addTextListener(new TextListener() {
			public void textValueChanged(TextEvent e) {
				try {
					min_x = nf.parse(T_x_min.getText()).doubleValue();
					max_x = nf.parse(T_x_max.getText()).doubleValue();
				} catch (ParseException pe) {
					System.out.println(pe);
				}
				wx = (max_x - min_x);
				if (Math.abs(wx) > 1.E+08 || Math.abs(wx) < 1.E-08) {
					s_df = (df_E.format(wx));
				} else {
					s_df = (df_F.format(wx));
				}
				TA_wx.setText(s_df);
				dx = (max_x - min_x) / nr_x;
				if (Math.abs(dx) > 1.E+08 || Math.abs(dx) < 1.E-08) {
					s_df = (df_E.format(dx));
				} else {
					s_df = (df_F.format(dx));
				}
				T_dx.setText(s_df);
			}
		});

		T_y_max.addTextListener(new TextListener() {
			public void textValueChanged(TextEvent e) {
				try {
					min_y = nf.parse(T_y_min.getText()).doubleValue();
					max_y = nf.parse(T_y_max.getText()).doubleValue();
				} catch (ParseException pe) {
					System.out.println(pe);
				}
				wy = (max_y - min_y);
				if (Math.abs(wy) > 1.E+08 || Math.abs(wy) < 1.E-08) {
					s_df = (df_E.format(wy));
				} else {
					s_df = (df_F.format(wy));
				}
				TA_wy.setText(s_df);
				dy = (max_y - min_y) / nr_y;
				if (Math.abs(dy) > 1.E+08 || Math.abs(dy) < 1.E-08) {
					s_df = (df_E.format(dy));
				} else {
					s_df = (df_E.format(dy));
				}
				T_dy.setText(s_df);
			}
		});

		T_z_max.addTextListener(new TextListener() {
			public void textValueChanged(TextEvent e) {
				try {
					min_z = nf.parse(T_z_min.getText()).doubleValue();
					max_z = nf.parse(T_z_max.getText()).doubleValue();
				} catch (ParseException pe) {
					System.out.println(pe);
				}
				wz = (max_z - min_z);
				if (Math.abs(wz) > 1.E+08 || Math.abs(wz) < 1.E-08) {
					s_df = (df_E.format(wz));
				} else {
					s_df = (df_F.format(wz));
				}
				TA_wz.setText(s_df);
				dz = (max_z - min_z) / nr_z;
				if (Math.abs(dz) > 1.E+08 || Math.abs(dz) < 1.E-08) {
					s_df = (df_E.format(dz));
				} else {
					s_df = (df_F.format(dz));
				}
				T_dz.setText(s_df);
			}
		});
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// Set the dialog size to the preferred size of its components
		this.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int xPos, yPos;
		xPos = (int) (screenSize.getWidth() / 2.0) - this.getWidth() / 2;
		yPos = (int) (screenSize.getHeight() / 2.0) - this.getHeight() / 2;
		this.setLocation(xPos, yPos);
		// *****************************************************************************
		
		//代替 okey 按钮单击事件
		min_x_new = (T_x_min.getText());
		max_x_new = (T_x_max.getText());
		nr_x_new = (T_x_nr.getText());
		min_y_new = (T_y_min.getText());
		max_y_new = (T_y_max.getText());
		nr_y_new = (T_y_nr.getText());
		min_z_new = (T_z_min.getText());
		max_z_new = (T_z_max.getText());
		nr_z_new = (T_z_nr.getText());

		slice_filename_new = (T_slice_filename.getText());
		// Checkbox Abfrage fehlt !
		try {
			if (nf.parse(nr_x_new).intValue() > MAX_ENTRY | nf.parse(nr_y_new).intValue() > MAX_ENTRY
					| nf.parse(nr_z_new).intValue() > MAX_ENTRY) {

				InfoDialog id = new InfoDialog(parent, "Error#1",
						"Sorry,\n " + "maximum number of grid-points is curently " + MAX_ENTRY + "\n"
								+ "you will have to change the variable MAX_ENTRY\n" + "in BoundarysDialog.java\n"
								+ "if you want to have a bigger grid!");
				id.setVisible(true);
			}
		} catch (ParseException pe) {
			System.out.println(pe);
		}
		
		//00
//		parent.getBoundarys(min_x_new, max_x_new, nr_x_new, search_x_new, min_y_new, max_y_new, nr_y_new,
//				search_y_new, min_z_new, max_z_new, "1", search_z_new, b_extract_slices, slice_filename_new,
//				new_beta, new_delta, new_missingvalue, nr_assign_missingvalue, new_normalize, normalize_only_z,
//				b_octant_search, write_surfer_grd, b_trend, X1, X2, b_blank2, new_NR_EMPTY_OCT, new_NR_DATA_OCT);
		
		//计算深度剖面
//		parent.getBoundarys("125", "140", "150", 25, "20", "30", "150",	25,
//              min_z_new, max_z_new, "1", 2000, b_extract_slices, slice_filename_new,
//				5, new_delta, new_missingvalue, nr_assign_missingvalue, new_normalize, normalize_only_z,
//				false, write_surfer_grd, b_trend, X1, X2, b_blank2, new_NR_EMPTY_OCT, new_NR_DATA_OCT);
		
		
//		//x和z互換，计算经度剖面的插值结果。更改交换经度和深度的小、大范围、生成图片的宽度、层数 参数
//		parent.getBoundarys("0", "1000", "150", 1000, "20", "30", "150",25, 
//				"125", "140", "1", 25, b_extract_slices, slice_filename_new,
//				5, new_delta, new_missingvalue, nr_assign_missingvalue, new_normalize, normalize_only_z,
//				false, write_surfer_grd, b_trend, X1, X2, b_blank2, new_NR_EMPTY_OCT, new_NR_DATA_OCT);
		
		//y和z互換，计算维度剖面的插值结果。更改交换经度和深度的小、大范围、生成图片的宽度、层数 参数
		parent.getBoundarys("125", "140", "150", 25, "0", "1000", "150", 1000,
				"20", "30", "150",25, b_extract_slices, slice_filename_new,
				5, new_delta, new_missingvalue, nr_assign_missingvalue, new_normalize, normalize_only_z,
				false, write_surfer_grd, b_trend, X1, X2, b_blank2, new_NR_EMPTY_OCT, new_NR_DATA_OCT);
		
		
		
		
		
		
		
		this.setVisible(false); // Pop the dialog down - deprecated
		this.dispose(); // Destroy it. Cannot be shown again after disposed
		
		
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("okay")) {

//			min_x_new = (T_x_min.getText());
//			max_x_new = (T_x_max.getText());
//			nr_x_new = (T_x_nr.getText());
//			min_y_new = (T_y_min.getText());
//			max_y_new = (T_y_max.getText());
//			nr_y_new = (T_y_nr.getText());
//			min_z_new = (T_z_min.getText());
//			max_z_new = (T_z_max.getText());
//			nr_z_new = (T_z_nr.getText());
//
//			slice_filename_new = (T_slice_filename.getText());
//			// Checkbox Abfrage fehlt !
//			try {
//				if (nf.parse(nr_x_new).intValue() > MAX_ENTRY | nf.parse(nr_y_new).intValue() > MAX_ENTRY
//						| nf.parse(nr_z_new).intValue() > MAX_ENTRY) {
//
//					InfoDialog id = new InfoDialog(parent, "Error#1",
//							"Sorry,\n " + "maximum number of grid-points is curently " + MAX_ENTRY + "\n"
//									+ "you will have to change the variable MAX_ENTRY\n" + "in BoundarysDialog.java\n"
//									+ "if you want to have a bigger grid!");
//					id.setVisible(true);
//				}
//			} catch (ParseException pe) {
//				System.out.println(pe);
//			}
//			parent.getBoundarys(min_x_new, max_x_new, nr_x_new, search_x_new, min_y_new, max_y_new, nr_y_new,
//					search_y_new, min_z_new, max_z_new, nr_z_new, search_z_new, b_extract_slices, slice_filename_new,
//					new_beta, new_delta, new_missingvalue, nr_assign_missingvalue, new_normalize, normalize_only_z,
//					b_octant_search, write_surfer_grd, b_trend, X1, X2, b_blank2, new_NR_EMPTY_OCT, new_NR_DATA_OCT);
//
//			this.setVisible(false); // Pop the dialog down - deprecated
//			this.dispose(); // Destroy it. Cannot be shown again after disposed
		} else if (command.equals("cancel")) {
			this.setVisible(false); // Pop the dialog down - deprecated
			this.dispose(); // Destroy it. Cannot be shown again after disposed
		} else if (command.equals("advanced")) {

			AdvancedDialog ad = new AdvancedDialog(this, "Advanced Settings", new_beta, new_delta, new_missingvalue,
					nr_assign_missingvalue, new_NR_EMPTY_OCT, new_NR_DATA_OCT, debug);
			ad.pack();
			ad.setVisible(true);
		} else if (command.equals("help")) {
			InfoDialog hd = new InfoDialog(parent, "Help", "Some informations concerning this dialog:\n"
					+ "In the min and max x/y/z field you can write a value\n"
					+ "for the minimum/maximum extension of the interpolated space\n"
					+ "(a floating point number, e.g. 1.1)\n" + "but in the field for the number of grid-points\n"
					+ "please only an integer value (e.g. 5, not 5.5)!\n"
					+ "After you have entered a new nr value the resulting spacing dx/dy/dz\n" + "is calculated.\n\n"
					+ "You can output slices for all depth of the grid.\n"
					+ "The slice-filenames start with the numerical value of the depth and\n"
					+ "end with the name which is written in the slice-filename-field." + '\n'
					+ "Please take care to use the correct digit-seperator (. or ,)\n"
					+ "depending on the country settings on your operating system.\n"
					+ "The 'Advanced Dialog' doesn't remembers the registered,\n"
					+ "so please take care if you re-enter this dialog!\n" + "\nWolfram Ruehaak, June 2004");
			hd.setVisible(true);
		} else if (command.equals("FileInfo")) {
			InfoDialog fi = new InfoDialog(parent, "Scattered Data File Info", s_fileinfo);
			fi.setVisible(true);
		}
	}

	protected void getAdvanced(double beta, double delta, double missingvalue, int nr_of_missingvalues,
			boolean b_octant_search2, boolean bblank2, boolean btrend, double a, double b, int NR_EMPTY_OCT2,
			int NR_DATA_OCT2, double search_x2, double search_y2, double search_z2) {

		b_octant_search = b_octant_search2;
		b_blank2 = bblank2;
		b_trend = btrend;
		X1 = a;
		X2 = b;
		new_NR_EMPTY_OCT = NR_EMPTY_OCT2;
		new_NR_DATA_OCT = NR_DATA_OCT2;

		search_x_new = search_x2;
		search_y_new = search_y2;
		search_z_new = search_z2;

		if (debug)
			System.out.println("b_blank2: " + b_blank2 + " b_trend: " + b_trend + " X1 = " + X1 + " X2 = " + X2
					+ " b_octantsearch = " + b_octant_search);
		new_beta = beta;
		new_delta = delta;
		new_missingvalue = missingvalue;
		nr_assign_missingvalue = nr_of_missingvalues;
		if (delta < 0.F || delta > 1.F) {
			InfoDialog id = new InfoDialog(parent, "wrong data", "delta has to be between zero and one !");
			id.setVisible(true);
		}
	}
}
