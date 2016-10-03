/*   ========================================================================
 *   Copyright 2010-2015 Peter M. D. Scully
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   ========================================================================
 */

package csic_http_analysis;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;

/*
 * This is the FileAccess class
 * It controls access to file storage of database connectivity data.
 * @author lll6, pds7
 * @version 20/11/2008
 */
public class FileAccess {

    // ====================================================================================================
    // --- DATA
    // ====================================================================================================
    
    /**
     * Stored file location for the database connection data.
     */
    private String filename; //test_header_file //normalTrafficTraining.txt //anomalousTrafficTest.txt
    private String output;
    
    
    private ArrayList<HttpRequestDataObject> listPOST;
    private ArrayList<HttpRequestDataObject> listPUT;
    private ArrayList<HttpRequestDataObject> listGET;

    
    public FileAccess(String filename, String output){
        this.filename   = filename;
        this.output     = output;
        listPOST    = new ArrayList<HttpRequestDataObject>();
        listPUT     = new ArrayList<HttpRequestDataObject>();
        listGET     = new ArrayList<HttpRequestDataObject>();
    }
    
    
    
    
    // ====================================================================================================
    // --- Read File
    // ====================================================================================================
    
    /**
     * read in from "datafile" in current directory, make a new DBAccessObject
     * and return it, if not exist display error.
     *
     * @return Returns a DBAccessObject or null.
     */
    public void read() {
        BufferedReader br = createBufferedReader(new File(filename));
        if (br != null) {
            while(readHeader(br) != null);					//Attempt to read the file.
        }
        closeBufferedReader(br);
    }


    /**
     * Method to read lines from the file 
     *
     * @param br
     * @return DBAccessObject
     */
    private String readHeader(BufferedReader br) {
        String result = null;
        try {
            String string = br.readLine();
            if(string != null){
                if(string.startsWith("GET")){
                    //do GET
                    listGET.add(new HttpObject_GET(string, br));
                    result = string;
                }
                else if(string.startsWith("POST")){
                    //do POST
                    listPOST.add(new HttpObject_POST(string, br));
                    result = string;
                }
                else if(string.startsWith("PUT")){
                    //do PUT
                    listPUT.add(new HttpObject_PUT(string, br));
                    result = string;
                }
                else{
                    System.err.println("FATAL Header File Format Error. Failed to find GET or POST in: "+string);
                }
            }
            else{
                System.out.println("EOF / NULL STRING");
            }
            

        } catch (IOException e) {
            System.err.println("IOException: while counting file line numbers.");
            JOptionPane.showMessageDialog(null, "Error while reading file.\nPlease contact your developer.");
        }
        return result;
    }
    
    
    
    
    
    
    
    
    
    // ====================================================================================================
    // --- Write File
    // ====================================================================================================
    
    /**
     * write these variables to a file called "datafile" so that you can get
     * them in the same way from the read() method
     *
     * @return 0=fail, 1= success, 2= successful overwrite
     */
    public int writeToDisk(String line) {
        int result = 0;
        boolean exists = fileExists(output);

        //TODO: Call encrypt method. 

        try {
            PrintWriter out = new PrintWriter(new DataOutputStream(new FileOutputStream(new File(output),true)));
            out.print(line);

            out.close();
            if (exists) {
                result = 2;
            } else {
                result = 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
    
    
    
    
    
    
    
    
    // ====================================================================================================
    // --- Helper Functions
    // ====================================================================================================
    
    

    /**
     * Returns true if the datafile exists.
     *
     * @return
     */
    boolean fileExists(String file) {
        File temp = new File(file);
        return temp.exists();
    }

    /**
     * Initialise BufferedReader with filename. Static class method.
     *
     * @param fileName
     * @return BufferedReader
     */
    private BufferedReader createBufferedReader(File fileName) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new ProgressMonitorInputStream(null, "Reading " + fileName,
                    new FileInputStream(fileName))));
        } catch (FileNotFoundException e) {									//catch file not found exception
            System.err.println("File not found! " + fileName.getName());
            JOptionPane.showMessageDialog(null, "File not found: " + fileName.getName());
        }

        return br;
    }

    /**
     * Method to close a BufferedReader.
     *
     * @param br
     */
    private void closeBufferedReader(BufferedReader br) {
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error while closing br with file.");
        }
    }

    /**
     * Count number of lines in a file.
     *
     * @return int number of lines
     * @param BufferedReader br
     */
    private int performLineCount(BufferedReader br) {
        //Skip through the file, return the total number of lines. TC=O(L), SC=O(1)
        int counter = 0;
        try {
            while (br.readLine() != null) {
                counter++;
            }
        } catch (IOException e) {
            System.err.println("IOException: while counting file line numbers.");
            JOptionPane.showMessageDialog(null, "Error while reading file.\nPlease contact your developer.");
        }
        return counter;
    }
    
    
    
    
    
    
    
    
    
    
    // ====================================================================================================
    // --- Getters
    // ====================================================================================================
    public ArrayList<HttpRequestDataObject> getListGET() {
        return listGET;
    }
    public ArrayList<HttpRequestDataObject> getListPOST() {
        return listPOST;
    }
    public ArrayList<HttpRequestDataObject> getListPUT() {
        return listPUT;
    }
    public String getFilename(){
        return filename;
    }
}
