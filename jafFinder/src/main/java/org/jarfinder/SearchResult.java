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


/**
 * The Class SearchResult.
 *
 * @author Pradeep Pejaver
 */
public class SearchResult {

    /** The row no. */
    private Integer rowNo;

    /** The file name. */
    private String fileName;

    /** The file path. */
    private String filePath;

    /**
     * Gets the row no.
     *
     * @return the rowNo
     */
    public Integer getRowNo() {

        return rowNo;
    }

    /**
     * Sets the row no.
     *
     * @param rowNo the rowNo to set
     */
    public void setRowNo(Integer rowNo) {
        this.rowNo = rowNo;
    }

    /**
     * Gets the file name.
     *
     * @return the fileName
     */
    public String getFileName() {

        return fileName;
    }

    /**
     * Sets the file name.
     *
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the file path.
     *
     * @return the filePath
     */
    public String getFilePath() {

        return filePath;
    }

    /**
     * Sets the file path.
     *
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

	@Override
	public String toString() {
		return "SearchResult [rowNo=" + rowNo + ", fileName=" + fileName + ", filePath=" + filePath + "]";
	}
    
    
}
