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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;

/**
 * The Class JarFinder.
 */
public class JarFinderUtil {

	/** The jar finder. */
	JarFinder jarFinder = null;

	/** The jar set. */
	Set jarSet = new HashSet();

	/** The index. */
	int index = 0;

	Vector dataVector = null;

	/**
	 * Check file exists.
	 * 
	 * @param jarFileStr
	 *            the jar file str
	 * @param className
	 *            the class name
	 * @param jarList
	 *            the jar list
	 */
	private void checkFileExists(String jarFileStr, String className,
			List jarList) {

		JarFile jarFile;

		try {
			jarFile = new JarFile(jarFileStr);

			String jarEntryName;
			Enumeration en = jarFile.entries();
			System.out.println("jarFile.entries()"+jarFile.entries());
			String jarName = "";

			boolean matchCase = jarFinder.getIsCaseSensitive().isSelected();
			while (en.hasMoreElements()) {

				JarEntry jarEntry = (JarEntry) en.nextElement();

				jarEntryName = jarEntry.getName();
				
				if (!matchCase) {
					jarEntryName = jarEntryName.toUpperCase();
					className = className.toUpperCase();
				}

				if (jarEntryName.indexOf(className) != -1) {

					if (!jarSet.contains(jarFileStr)) {
						index++;
						jarList.add(jarFileStr);
						jarSet.add(jarFileStr);

						try {

							if (jarFileStr.indexOf("/") > -1) {
								jarName = jarFileStr.substring(jarFileStr
										.lastIndexOf("/") + 1);
							} else if (jarFileStr.indexOf("\\") > -1) {
								jarName = jarFileStr.substring(jarFileStr
										.lastIndexOf("\\") + 1);
							} else {
								jarName = jarFileStr;
							}
						} catch (Exception e) {
							jarName = jarFileStr;
						}

						SearchResult result = new SearchResult();
						result.setRowNo(new Integer(index));
						result.setFileName(jarName);
						result.setFilePath(jarFileStr);
						jarFinder.getResultsTableModel().updateData(result);

					}
				}
			}
		} catch (IOException e) {
			//System.out.println("Exception: " + e);
		}
	}
	
	public static List<SearchResult> checkFileExistsNonUi(String jarFileStr, String className,
			List jarList, boolean caseSensitive) {
		//System.out.println("checkFileExistsNonUi jarFileStr:"+jarFileStr+" className:"+ className+" jarList:"+jarList+" caseSensitive:"+caseSensitive);
		List<SearchResult> searchResult = new ArrayList<SearchResult>();
		JarFile jarFile;
		Set jarSet = new HashSet();

		try {
			jarFile = new JarFile(jarFileStr);

			String jarEntryName;
			Enumeration en = jarFile.entries();
			String jarName = "";

			boolean matchCase = caseSensitive;
			while (en.hasMoreElements()) {

				JarEntry jarEntry = (JarEntry) en.nextElement();

				jarEntryName = jarEntry.getName();
//				System.out.println("jarEntryName:_"+jarEntryName);
				if (!matchCase) {
					jarEntryName = jarEntryName.toUpperCase();
					className = className.toUpperCase();
				}
//				System.out.println(" className"+className );
//				if (jarEntryName.endsWith("XML")) {
//					System.out.println("jarname:"+jarEntryName+" classname:"+className+" contains?"+(jarEntryName.indexOf(className)>-1)+"");
//				}
				if (jarEntryName.indexOf(className) != -1) {
					//System.out.println("jarEntryName:_"+jarEntryName+" className"+className );	
					if (!jarSet.contains(jarFileStr)) {
//						System.out.println("jarEntryName:_"+jarEntryName+" className"+className );
//						System.out.println("indexof classname:" +jarEntryName.indexOf(className));
//						System.out.println("indexof classname .xml:" +jarEntryName.indexOf(className+".XML"));
//						System.out.println("indexof classname /xml:" +jarEntryName.indexOf(className+"/XML"));
						
						//index++;
						jarList.add(jarFileStr);
						jarSet.add(jarFileStr);

						try {

							if (jarFileStr.indexOf("/") > -1) {
								jarName = jarFileStr.substring(jarFileStr
										.lastIndexOf("/") + 1);
							} else if (jarFileStr.indexOf("\\") > -1) {
								jarName = jarFileStr.substring(jarFileStr
										.lastIndexOf("\\") + 1);
							} else {
								jarName = jarFileStr;
							}
						} catch (Exception e) {
							jarName = jarFileStr;
						}

						SearchResult result = new SearchResult();
//						result.setRowNo(new Integer(index));
						result.setFileName(jarName);
						result.setFilePath(jarFileStr);
//						jarFinder.getResultsTableModel().updateData(result);
						searchResult.add(result);

					}
				}
			}
		} catch (IOException e) {
			//System.out.println("Exception: " + e);
		}
		return searchResult;
	}

	
	public static List<SearchResult> findJar (String directory, String fileName, boolean caseSensitive, boolean plainSearch) {
	
		JarFinderUtil jarFinderUtil = new JarFinderUtil();
		List jarFileList = new ArrayList();
		
		System.out.println("Getting directory Jar file list...");
		JarFinderUtil.getJarList(directory, jarFileList);

		List<SearchResult> retList = JarFinderUtil.checkFileExistsNonUi(jarFileList, fileName, caseSensitive, plainSearch);
		return retList;
	}
	
	
	/**
	 * Update progress.
	 * 
	 * @param index
	 *            the index
	 * @param maxSize
	 *            the max size
	 */
	private void updateProgress(int index, int maxSize) {

		if (maxSize > 0) {

			int value = (index * 100) / maxSize;

			jarFinder.progressBar.setValue(value);
			jarFinder.progressBar.repaint();
		}
	}

	/**
	 * Check file exists.
	 * 
	 * @param jarFileList
	 *            the jar file list
	 * @param className
	 *            the class name
	 * 
	 * @return the list
	 */
	private List checkFileExists(List jarFileList, String className) {

		List jarList = new ArrayList();

		className = className.replaceAll("\\.", "/");

		int maxSize = jarFileList.size();

		for (int i = 0; i < jarFileList.size(); i++) {

			String s = (String) jarFileList.get(i);

			checkFileExists(s, className, jarList);
			updateProgress(i, maxSize);
		}

		updateProgress(maxSize, maxSize);
		jarFinder.updateStatus(jarFinder.STATUS_COMPLETED);

		return jarList;
	}
	
	public static List checkFileExistsNonUi(List jarFileList, String className, boolean caseSensitive, boolean plainSearch) {

		List jarList = new ArrayList();
		
		if (!plainSearch) {
			className = className.replaceAll("\\.", "/");
		}
		int maxSize = jarFileList.size();

		for (int i = 0; i < jarFileList.size(); i++) {

			String s = (String) jarFileList.get(i);

			checkFileExistsNonUi(s, className, jarList, caseSensitive);
		}

		return jarList;
	}

	/**
	 * Gets the jar list.
	 * 
	 * @param jarDirectory
	 *            the jar directory
	 * @param jarFileList
	 *            the jar file list
	 * 
	 * @return the jar list
	 */
	public static  void getJarList(String jarDirectory, List jarFileList) {
//		System.out.println("getJarList"+jarDirectory+" jarFileList:"+jarFileList);
		File file = new File(jarDirectory);

//		System.out.println(""+file.getAbsolutePath()+" isDir?"+file.isDirectory());
		if (file.isDirectory()) {

			String[] files = file.list();

			if ((files != null) && (files.length > 0)) {

				for (int i = 0; i < files.length; i++) {
					getJarList(jarDirectory + "/" + files[i], jarFileList);
				}
			}
		} else if (file.isFile() && (file.getName().indexOf(".jar") != -1)) {
			jarFileList.add(file.getPath());
		}
	}

	/**
	 * Find jar.
	 * 
	 * @param jarDirectory
	 *            the jar directory
	 * @param className
	 *            the class name
	 * @param jarFinder
	 *            the jar finder
	 * 
	 * @return the sets the
	 */
	public void findJar(String jarDirectory, String className,
			JarFinder jarFinder) {
		this.jarFinder = jarFinder;

		if ((jarDirectory != null) && (className != null)
				&& (className.trim().length() > 0)) {
			jarFinder.updateStatus(jarFinder.STATUS_STARTED);

			List jarFileList = new ArrayList();

			getJarList(jarDirectory, jarFileList);

			jarFinder.progressBar.setValue(0);
			jarFinder.progressBar.setVisible(true);

			index = 0;
			dataVector = new Vector();

			List retList = checkFileExists(jarFileList, className);

			if (retList.size() == 0) {
				jarFinder.updateStatus(jarFinder.STATUS_COMPLETED);
				JOptionPane.showMessageDialog(jarFinder,
						"Couldn't find the specified class", "Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(jarFinder, "Please specify the "
					+ "directory and fully qualified class name.", "Error",
					JOptionPane.ERROR_MESSAGE);
			jarFinder.updateStatus(jarFinder.STATUS_ERROR);
		}
	}
}
