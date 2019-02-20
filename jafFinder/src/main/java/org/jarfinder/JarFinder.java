/*
 * Copyright 2008 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jarfinder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * The Class JarFinder.
 * 
 * @author Pradeep Pejaver
 */
public class JarFinder extends JFrame {

	/** The heading label. */
	private JLabel headingLabel = null;

	/** The jar file label. */
	private JLabel jarFileLabel = null;

	/** The browse button. */
	private JButton browseButton = null;

	/** The class name label. */
	private JLabel classNameLabel = null;

	private JCheckBox isCaseSensitive = null;

	/** The class name text. */
	public JTextField classNameText = null;

	/** The search button. */
	public JButton searchButton = null;

	/** The stop search button. */
	public JButton stopSearchButton = null;

	/** The jar folder text. */
	public JTextField jarFolderText = null;

	/** The results table. */
	public JTable resultsTable;

	/** The results table model. */
	public JarFinderTableModel resultsTableModel;

	/** The scroll pane. */
	private JScrollPane scrollPane = null;

	/** The developed by. */
	private JLabel developedBy = null;

	/** The progress bar. */
	public JProgressBar progressBar = null;

	/** The status label. */
	public JLabel statusLabel = null;

	/** The constant used to indicate started status. */
	public final int STATUS_STARTED = 1;

	/** The constant used to indicate completed status. */
	public final int STATUS_COMPLETED = 5;

	/** The constant used to indicate error status. */
	public final int STATUS_ERROR = 10;

	/** The constant used to indicate stopped status. */
	public final int STATUS_STOPPED = 15;

	private Thread searchThread = null;

	/**
	 * Instantiates a new jar finder.
	 * 
	 * @throws HeadlessException
	 *             the headless exception
	 */
	public JarFinder() throws HeadlessException {
		super();
		createComponents();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("JAR Finder ~ 1.0");
		this.setIconImage(new ImageIcon(getClass().getResource("/find.gif"))
				.getImage());
		this.setVisible(true);
	}

	/**
	 * @return the resultsTableModel
	 */
	public JarFinderTableModel getResultsTableModel() {
		return resultsTableModel;
	}

	/**
	 * @return the isCaseSensitive
	 */
	public JCheckBox getIsCaseSensitive() {
		return isCaseSensitive;
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @see java.awt.Component#repaint(int, int, int, int)
	 */
	public void repaint(int x, int y, int width, int height) {
		super.repaint(x, y, width, height);
		this.validate();

		Rectangle rectangle = this.getContentPane().getBounds();

		this.setupScreen(rectangle);
	}

	/**
	 * Creates the components.
	 */
	private void createComponents() {
		headingLabel = new JLabel("JAR Finder");
		headingLabel.setFont(new Font("Verdana", 1, 16));

		jarFileLabel = new JLabel("Jar File/Folder : ");
		jarFolderText = new JTextField("");
		browseButton = new JButton("<html><u>B</u>rowse...</html>");
		browseButton.setMnemonic('b');
		browseButton.addActionListener(new BrowseListener(this));

		classNameLabel = new JLabel("Class Name : ");
		classNameText = new JTextField("");

		searchButton = new JButton("<html><u>S</u>earch</html>");
		searchButton.setMnemonic('s');
		searchButton.addActionListener(new SearchListener(this));

		stopSearchButton = new JButton("<html>S<u>t</u>op Search</html>");
		stopSearchButton.setMnemonic('t');
		stopSearchButton.setVisible(false);
		stopSearchButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (searchThread != null) {
					//have to kill the search thread. no other way
					searchThread.stop();
					handleSearchButtons(true);
					updateStatus(STATUS_STOPPED);
				}
			}

		});

		isCaseSensitive = new JCheckBox("Match Case");
		isCaseSensitive.setSelected(true);

		resultsTableModel = new JarFinderTableModel();
		resultsTable = new JTable(resultsTableModel);
		resultsTable.setToolTipText("Click column headers to sort");

		JTableHeader th = resultsTable.getTableHeader();
		th.addMouseListener(new SortingListener());
		th.setToolTipText("Click column headers to sort");

		resultsTable.setGridColor(new Color(0xF1EFE2));
		setTableColumnWidths();
		scrollPane = new JScrollPane(resultsTable);

		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		developedBy = new JLabel(
				"Developed By Pradeep Pejaver - pejaverpradeep@gmail.com");
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);

		statusLabel = new JLabel("");

		this.getContentPane().setLayout(null);

		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

		this.setSize(((screenDimension.width * 3) / 4),
				((screenDimension.height * 3) / 4));

		Rectangle rectangle = this.getBounds();

		setupScreen(rectangle);
	}

	/**
	 * Sets the up screen.
	 * 
	 * @param rectangle
	 *            the new up screen
	 */
	private void setupScreen(Rectangle rectangle) {

		int x = rectangle.x;
		int y = rectangle.y;
		int height = rectangle.height;
		int width = rectangle.width;

		addComponent(headingLabel, (x + (width / 2)) - 50, y + 10, 100, 25);

		addComponent(jarFileLabel, x + 50, y + 75, 80, 20);
		addComponent(jarFolderText, x + 135, y + 75, 300, 20);
		addComponent(browseButton, x + 440, y + 75, 80, 20);

		addComponent(classNameLabel, x + 50, y + 110, 80, 20);
		addComponent(classNameText, x + 135, y + 110, 300, 20);

		addComponent(searchButton, x + 50, y + 145, 80, 20);
		addComponent(isCaseSensitive, x + 175, y + 145, 100, 20);
		addComponent(stopSearchButton, x + 280, y + 145, 100, 20);

		addComponent(scrollPane, x + 50, y + 180, (x + width) - 100,
				(y + height) - 255);
		addComponent(developedBy, x + 50, (y + height) - 60, 350, 25);

		addComponent(progressBar, x + 405, (y + height) - 60, 100, 25);
		addComponent(statusLabel, (x + 575), (y + height) - 60, 150, 25);
	}

	/**
	 * Adds the component.
	 * 
	 * @param component
	 *            the component
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	private void addComponent(Component component, int x, int y, int width,
			int height) {
		component.setBounds(x, y, width, height);
		this.getContentPane().add(component);
	}

	/**
	 * Sets the table column widths.
	 */
	private void setTableColumnWidths() {
		resultsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		resultsTable.getColumnModel().getColumn(0).setMaxWidth(100);
		resultsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		resultsTable.getColumnModel().getColumn(1).setMaxWidth(500);
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new JarFinder();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the status label.
	 * 
	 * @param status
	 *            the status
	 */
	public void updateStatus(int status) {

		if (status == STATUS_STARTED) {
			statusLabel.setText("Search in progress...");
		} else if (status == STATUS_COMPLETED) {
			statusLabel.setText("Search Completed");
			progressBar.setVisible(false);
		} else if (status == STATUS_STOPPED) {
			statusLabel.setText("Search Stopped");
			progressBar.setVisible(false);
		} else if (status == STATUS_ERROR) {
			statusLabel.setText("");
			progressBar.setVisible(false);
		}
	}

	/**
	 * The listener interface for receiving browse events. The class that is
	 * interested in processing a browse event implements this interface, and
	 * the object created with that class is registered with a component using
	 * the component's <code>addBrowseListener<code> method. When
	 * the browse event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see BrowseEvent
	 */
	private class BrowseListener implements ActionListener {

		/** The parent frame. */
		private JFrame parentFrame = null;

		/**
		 * Instantiates a new browse listener.
		 * 
		 * @param parent
		 *            the parent
		 */
		public BrowseListener(JFrame parent) {
			super();
			parentFrame = parent;
		}

		/**
		 * @param e
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {

			JFileChooser chooser = new JFileChooser();

			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.showDialog(parentFrame, "Select");

			try {
				jarFolderText.setText(chooser.getSelectedFile().getPath());
			} catch (Exception ex) {
				jarFolderText.setText("");
			}
		}
	}

	/**
	 * The listener interface for receiving search events. The class that is
	 * interested in processing a search event implements this interface, and
	 * the object created with that class is registered with a component using
	 * the component's <code>addSearchListener<code> method. When
	 * the search event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see SearchEvent
	 */
	private class SearchListener implements ActionListener {

		/** The jar finder. */
		private JarFinder jarFinder;

		/**
		 * Instantiates a new search listener.
		 * 
		 * @param jarFinder
		 *            the jar finder
		 */
		public SearchListener(JarFinder jarFinder) {
			super();
			this.jarFinder = jarFinder;
		}

		/**
		 * @param e
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			resultsTableModel.clearExistingResults();
			setTableColumnWidths();
			jarFinder.progressBar.setVisible(false);
			searchThread = new Thread(new JarFinderThread(jarFinder));
			searchThread.start();
		}
	}

	/**
	 * The Class JarFinderThread.
	 */
	private class JarFinderThread implements Runnable {

		/** The jar finder. */
		private JarFinder jarFinder;

		/**
		 * Instantiates a new jar finder thread.
		 * 
		 * @param jarFinder
		 *            the jar finder
		 */
		public JarFinderThread(JarFinder jarFinder) {
			this.jarFinder = jarFinder;
		}

		/**
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {

			String jarFolder = jarFolderText.getText();
			String className = classNameText.getText();

			handleSearchButtons(false);

			JarFinderUtil finderUtil = new JarFinderUtil();
			finderUtil.findJar(jarFolder, className, jarFinder);

			handleSearchButtons(true);
		}
	}

	private void handleSearchButtons(boolean isSearchCompleted) {
		if (!isSearchCompleted) {
			isCaseSensitive.setEnabled(false);
			searchButton.setEnabled(false);
			browseButton.setEnabled(false);
			stopSearchButton.setVisible(true);
			updateStatus(STATUS_STARTED);
		} else {
			searchButton.setEnabled(true);
			browseButton.setEnabled(true);
			isCaseSensitive.setEnabled(true);
			stopSearchButton.setVisible(false);
			updateStatus(STATUS_COMPLETED);

		}
	}

	private class SortingListener extends MouseAdapter {

		/**
		 * @param e
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);

			TableColumnModel columnModel = resultsTable.getColumnModel();
			int viewColumn = columnModel.getColumnIndexAtX(e.getX());
			int column = resultsTable.convertColumnIndexToModel(viewColumn);
			if (e.getClickCount() == 1 && column != -1) {
				resultsTableModel.sortColumn(column);
			}
		}
	}

	class JarFinderTableModel extends DefaultTableModel {

		private List dataList = new ArrayList();

		private String[] columnNames = { "Sl No", "File Name", "Full Path" };

		private boolean[] sortFlags = { false, false, false };

		public JarFinderTableModel() {
			super();
		}

		/**
		 * @return the dataList
		 */
		public List getDataList() {
			return dataList;
		}

		public void updateData(SearchResult result) {
			dataList.add(result);
			fireTableDataChanged();
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public void setValueAt(Object value, int row, int column) {
			// user cannot change any value
		}

		public int getRowCount() {
			if (dataList != null && dataList.size() > 0) {
				return dataList.size();
			} else {
				return 0;
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Object retVal = null;
			if (dataList != null) {
				SearchResult result = (SearchResult) dataList.get(rowIndex);
				switch (columnIndex) {
				case 0:
					retVal = result.getRowNo();
					break;

				case 1:
					retVal = result.getFileName();
					break;

				default:
					retVal = result.getFilePath();
					break;
				}
			}
			return retVal;
		}

		public void sortColumn(int column) {
			if (dataList != null && dataList.size() > 1) {
				boolean isColSortAsc = sortFlags[column];
				Comparator comparator = getComparator(column, isColSortAsc);
				Collections.sort(dataList, comparator);
				sortFlags[column] = !isColSortAsc;
				fireTableDataChanged();
			}
		}

		public void clearExistingResults() {
			dataList = new ArrayList();
			sortFlags[0] = false;
			sortFlags[1] = false;
			sortFlags[2] = false;
			fireTableDataChanged();
		}

	}

	private Comparator getComparator(final int column,
			final boolean isColSortAsc) {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				int retVal = 0;
				SearchResult searchResult1 = (SearchResult) o1;
				SearchResult searchResult2 = (SearchResult) o2;
				switch (column) {
				case 0:
					Integer row1 = searchResult1.getRowNo();
					Integer row2 = searchResult2.getRowNo();
					if (isColSortAsc) {
						retVal = row1.compareTo(row2);
					} else {
						retVal = row2.compareTo(row1);
					}
					break;

				case 1:
					String fileName1 = searchResult1.getFileName();
					String fileName2 = searchResult2.getFileName();
					if (isColSortAsc) {
						retVal = fileName1.compareTo(fileName2);
					} else {
						retVal = fileName2.compareTo(fileName1);
					}
					break;

				default:
					String filePath1 = searchResult1.getFilePath();
					String filePath2 = searchResult2.getFilePath();
					if (isColSortAsc) {
						retVal = filePath1.compareTo(filePath2);
					} else {
						retVal = filePath2.compareTo(filePath1);
					}
					break;
				}

				return retVal;
			}
		};
	}

}
