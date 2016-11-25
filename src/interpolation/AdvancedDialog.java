package interpolation;

/**
 * @author        Wolfram Ruehaak
 * @version       1.0
 * @Copyright:    GPL Copyright (c) 2001
 * @e-mail:       w.ruehaak@online.de,
 * @homepage:     http://www.geomath.onlinehome.de
 *
 * iw3d: A programm for gridding 3-dimensional scattered data with the "inverse distances" method.
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
 ******************************************************************************
 */

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.*;

public class AdvancedDialog extends JFrame implements ActionListener {
	public String s_beta;
	public String s_delta;
	public String s_missingvalue;
	protected Button okay;
	protected Button cancel;

	protected Button b_showVariogram_x;
	protected Button b_showVariogram_y;
	protected Button b_showVariogram_z;
	protected Button b_showHistogram;
	protected Button b_infoVariogram;

	TextField T_beta;
	TextField T_delta;
	TextField T_missingvalue;
	TextField T_nr_assign_missingvalue;
	Checkbox checkbox_radius;
	boolean b_octant_search;
	boolean b_trend, b_blank2, b_modify_CL;
	TextField T_X1, T_X2;
	Checkbox cb_blank2;
	Checkbox cb_trend;
	Checkbox cb_modify_CL;
	TextField T_corrl_x, T_corrl_y, T_corrl_xy, T_corrl_z;
	TextField T_empty_quad, T_data_in_quad;
	boolean debug;
	double search_x_new, search_y_new, search_z_new;

	BoundarysDialog parent;

	double beta, delta, missingvalue, X1, X2;
	int NR_EMPTY_QUAD, NR_DATA_QUAD;

	static final NumberFormat nf = NumberFormat.getNumberInstance();

	public AdvancedDialog(JFrame f, String title, double beta, double delta, double missingvalue,
			int nr_assign_missingvalue, int NR_EMPTY_QUAD, int NR_DATA_QUAD, boolean debug) {

		super("Advanced Dialog");

		parent = (BoundarysDialog) f;

		Panel p = new Panel();
		p.setLayout(new GridLayout(15, 2));

		this.getContentPane().add(p, "West");

		Button okay = new Button("Okay");
		okay.setActionCommand("okay");
		okay.addActionListener(this);

		Button cancel = new Button("Cancel");
		cancel.setActionCommand("cancel");
		cancel.addActionListener(this);
		DecimalFormat df = new DecimalFormat("0.0####");
		T_beta = new TextField(df.format(beta), 10);
		T_delta = new TextField(df.format(delta), 10);

		String s_df;
		if (Math.abs(missingvalue) > 1.E+08 || Math.abs(missingvalue) < 1.E-08) {
			df = new DecimalFormat("0.00000E00");
		} else {
			df = new DecimalFormat("0.0#####");
		}
		s_df = (df.format(missingvalue));

		T_missingvalue = new TextField(s_df, 10);
		cb_trend = new Checkbox("remove a trend (T is a function of depth) ?", false);
		ItemListener cb1 = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				b_trend = ((Checkbox) ie.getItemSelectable()).getState();
				T_X1.setEditable(b_trend);
				T_X2.setEditable(b_trend);
			}
		};
		cb_trend.addItemListener(cb1);
		T_X1 = new TextField("0", 10);
		T_X2 = new TextField("1", 10);

		T_X1.setEditable(b_trend);
		T_X2.setEditable(b_trend);

		cb_blank2 = new Checkbox("use only data nearer than correlation length?", true);
		ItemListener cb2 = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				b_blank2 = ((Checkbox) ie.getItemSelectable()).getState();
			}
		};
		cb_blank2.addItemListener(cb2);
		cb_modify_CL = new Checkbox("modify correlation length?", false);
		ItemListener cb_mCL = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				b_modify_CL = ((Checkbox) ie.getItemSelectable()).getState();
				T_corrl_x.setEditable(b_modify_CL);
				T_corrl_y.setEditable(b_modify_CL);
				T_corrl_z.setEditable(b_modify_CL);
			}
		};
		cb_modify_CL.addItemListener(cb_mCL);
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		if (Math.abs(variogram3d.correlation_length_x) > 1.E+08
				|| Math.abs(variogram3d.correlation_length_x) < 1.E-08) {
			df = new DecimalFormat("0.00E00");
		} else {
			df = new DecimalFormat("0.0####");
		}
		s_df = (df.format(variogram3d.correlation_length_x));
		T_corrl_x = new TextField(s_df, 10);
		T_corrl_x.setEditable(b_modify_CL);

		Button b_showVariogram_x = new Button("correlation length x (show):");
		b_showVariogram_x.setActionCommand("show_variogram_x");
		b_showVariogram_x.addActionListener(this);

		if (Math.abs(variogram3d.correlation_length_y) > 1.E+08
				|| Math.abs(variogram3d.correlation_length_y) < 1.E-08) {
			df = new DecimalFormat("0.00E00");
		} else {
			df = new DecimalFormat("0.0####");
		}
		s_df = (df.format(variogram3d.correlation_length_y));
		T_corrl_y = new TextField(s_df, 10);
		T_corrl_y.setEditable(b_modify_CL);

		Button b_showVariogram_y = new Button("correlation length y (show):");
		b_showVariogram_y.setActionCommand("show_variogram_y");
		b_showVariogram_y.addActionListener(this);

		if (Math.abs(variogram3d.correlation_length_z) > 1.E+08
				|| Math.abs(variogram3d.correlation_length_z) < 1.E-08) {
			df = new DecimalFormat("0.00E00");
		} else {
			df = new DecimalFormat("0.0####");
		}
		s_df = (df.format(variogram3d.correlation_length_z));
		T_corrl_z = new TextField(s_df, 10);
		T_corrl_z.setEditable(b_modify_CL);

		Button b_showVariogram_z = new Button("correlation length z (show):");
		b_showVariogram_z.setActionCommand("show_variogram_z");
		b_showVariogram_z.addActionListener(this);

		// ------------------------------------------------------------------
		checkbox_radius = new Checkbox("use octant search(default)?", true);
		ItemListener cb3 = new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				b_octant_search = ((Checkbox) ie.getItemSelectable()).getState();
			}
		};
		checkbox_radius.addItemListener(cb3);
		T_empty_quad = new TextField(Integer.toString(NR_EMPTY_QUAD), 10);
		T_data_in_quad = new TextField(Integer.toString(NR_DATA_QUAD), 10);
		Button b_infoVariogram = new Button("info variogram");
		b_infoVariogram.setActionCommand("info_variogram");
		b_infoVariogram.addActionListener(this);

		Button b_showHistogram = new Button("show histogram of quality-classes");
		b_showHistogram.setActionCommand("show_histogram");
		b_showHistogram.addActionListener(this);

		// currently deactivated because it is not so important
		// and only necessary when the OctantSearch *isn't* used
		// be aware to activate also the otions below if you reactivate it.
		// p.add(new Label("Assign missing value when nr of values found
		// equal:"));
		// T_nr_assign_missingvalue = new
		// TextField(Integer.toString(nr_assign_missingvalue), 10);
		// p.add(T_nr_assign_missingvalue);
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		p.add(okay);
		p.add(cancel);
		p.add(new Label("weighting (power) parameter:"));
		p.add(T_beta);
		p.add(new Label("smoothing parameter (0 - 1):"));
		p.add(T_delta);
		p.add(new Label("number for missing value:"));
		p.add(T_missingvalue);
		p.add(cb_trend);
		p.add(new Label("T' = (T - a) / (b * depth)"));
		p.add(new Label("a ="));
		p.add(T_X1);
		p.add(new Label("b ="));
		p.add(T_X2);
		p.add(cb_blank2);
		p.add(cb_modify_CL);
		p.add(b_showVariogram_x);
		p.add(T_corrl_x);
		p.add(b_showVariogram_y);
		p.add(T_corrl_y);
		p.add(b_showVariogram_z);
		p.add(T_corrl_z);
		p.add(checkbox_radius);
		p.add(new Label(""));
		p.add(new Label("No of empty octants allowed:"));
		p.add(T_empty_quad);
		p.add(new Label("maximum of data to be used in octant:"));
		p.add(T_data_in_quad);
		p.add(b_infoVariogram);
		if (iw3d.b_weighting) {
			p.add(b_showHistogram);
		}
		// ------------------------------------------------------------------
		this.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int xPos, yPos;
		xPos = (int) (screenSize.getWidth() / 2.0) - this.getWidth() / 2;
		yPos = (int) (screenSize.getHeight() / 2.0) - this.getHeight() / 2;
		this.setLocation(xPos, yPos);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("okay")) {
			s_beta = (T_beta.getText());
			s_delta = (T_delta.getText());
			s_missingvalue = (T_missingvalue.getText());
			try {
				beta = nf.parse(s_beta).doubleValue();
				delta = nf.parse(s_delta).doubleValue();
				missingvalue = nf.parse(s_missingvalue).doubleValue();
			} catch (ParseException pe) {
				System.out.println(pe);
			}
			int nr_assign_missingvalue = 0;
			b_octant_search = checkbox_radius.getState();
			b_blank2 = cb_blank2.getState();
			b_trend = cb_trend.getState();

			try {
				X1 = nf.parse(T_X1.getText()).doubleValue();
				X2 = nf.parse(T_X2.getText()).doubleValue();
				NR_EMPTY_QUAD = nf.parse(T_empty_quad.getText()).intValue();
				NR_DATA_QUAD = nf.parse(T_data_in_quad.getText()).intValue();
			} catch (ParseException pe) {
				System.out.println(pe);
			}

			if (b_blank2 == false) {
				search_x_new = 0.0;
				search_y_new = 0.0;
				search_z_new = 0.0;
			} else if (b_modify_CL == true) {
				try {
					search_x_new = nf.parse(T_corrl_x.getText()).doubleValue();
					search_y_new = nf.parse(T_corrl_y.getText()).doubleValue();
					search_z_new = nf.parse(T_corrl_z.getText()).doubleValue();
				} catch (ParseException pe) {
					System.out.println(pe);
				}
			} else {
				search_x_new = variogram3d.correlation_length_x;
				search_y_new = variogram3d.correlation_length_y;
				search_z_new = variogram3d.correlation_length_z;

				if (debug)
					System.out.println("Case 3: " + search_x_new);
			}
			parent.getAdvanced(beta, delta, missingvalue, nr_assign_missingvalue, b_octant_search, b_blank2, b_trend,
					X1, X2, NR_EMPTY_QUAD, NR_DATA_QUAD, search_x_new, search_y_new, search_z_new);
			this.setVisible(false);
			this.dispose();
		} else if (command.equals("cancel")) {
			this.setVisible(false);
			this.dispose();
		} else if (command.equals("show_variogram_x")) {
			// showVariogram sv = new showVariogram("semivariogram x",0);
		} else if (command.equals("show_variogram_y")) {
			// showVariogram sv = new showVariogram("semivariogram y",1);
		} else if (command.equals("show_variogram_z")) {
			// showVariogram sv = new showVariogram("semivariogram z",2);
		} else if (command.equals("show_histogram")) {
			// showHistogram sh = new showHistogram("quality-classes
			// histogram");
		} else if (command.equals("info_variogram")) {
			String s_fileinfo = null;
			s_fileinfo = "x-direction:\n    lag     gamma  number of pairs\n";
			for (int i = 0; i < 10; i++) {
				s_fileinfo = s_fileinfo + variogram3d.allData[i][0] + "\t" + variogram3d.allData[i][1] + "\n";
			}

			s_fileinfo = s_fileinfo + "\ny-direction:\n    lag     gamma\n";
			for (int i = 0; i < 10; i++) {
				s_fileinfo = s_fileinfo + variogram3d.allData[i][2] + "\t" + variogram3d.allData[i][3] + "\n";
			}

			s_fileinfo = s_fileinfo + "\nz-direction:\n    lag     gamma\n";
			for (int i = 0; i < 10; i++) {
				s_fileinfo = s_fileinfo + variogram3d.allData[i][4] + "\t" + variogram3d.allData[i][5] + "\n";
			}
			InfoDialog fi = new InfoDialog(parent, "Variogram Info", s_fileinfo);
			fi.setVisible(true);

		}
	}
}
