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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author Peter Scully
 */
public class FrequencyAnalysis_HttpObjects {

    // =========================================================================
    // ---- Data
    // =========================================================================
    
    private FileAccess f;
    private String filename;
    private int count_GET;
    private int count_POST;
    private int count_PUT;
    private HashMap<String, Integer> count_userAgent;
    private HashMap<String, Integer> count_pragma;
    private HashMap<String, Integer> count_cacheControl;
    private HashMap<String, Integer> count_accept;
    private HashMap<String, Integer> count_acceptEncoding;
    private HashMap<String, Integer> count_acceptCharset;
    private HashMap<String, Integer> count_acceptLanguage;
    private HashMap<String, Integer> count_host;
    private HashMap<String, Integer> count_connection;
    private HashMap<String, Integer> count_contentType;
    private HashMap<String, Integer> count_contentLength;
    
    private HashMap<String, HashMap<String, Integer>> count_payload;
    private HashMap<String, HashMap<String, Integer>> count_cookie;
    
    // =========================================================================
    // ---- Initialisation
    // =========================================================================
    
    public FrequencyAnalysis_HttpObjects(FileAccess f) {
        this.f = f;
        filename = f.getFilename();
        init();
        doAnalysis();
        //System.out.println(toString());
        CreateFrequencyDatasetFile();
    }

    private void init(){
        count_userAgent         = new HashMap<String,Integer>();
        count_pragma            = new HashMap<String,Integer>();
        count_cacheControl 	= new HashMap<String,Integer>();
        count_accept 		= new HashMap<String,Integer>();
        count_acceptEncoding    = new HashMap<String,Integer>();
        count_acceptCharset     = new HashMap<String,Integer>();
        count_acceptLanguage    = new HashMap<String,Integer>();
        count_host 		= new HashMap<String,Integer>();
        count_connection 	= new HashMap<String,Integer>();
        count_contentType 	= new HashMap<String,Integer>();
        count_contentLength     = new HashMap<String,Integer>();
        
        count_payload           = new HashMap<String,HashMap<String,Integer>>();
        count_cookie            = new HashMap<String,HashMap<String,Integer>>();
    }
    
    
    
    //Order of get and posts -- e.g. G=2,P=1,G=2,P=1,G=1,P=1,G=1
    //Total count of get and post -- e.g. G=6,P=3
    
    
    
    // =========================================================================
    // ---- Methods
    // =========================================================================
    
    private void doAnalysis(){
        countMethods();
        
        for(HttpRequestDataObject o : f.getListGET()){
            countHeaders(o);
            countCookie(o);
            countPayload(o);
        }
        for(HttpRequestDataObject o : f.getListPOST()){
            countHeaders(o);
            countCookie(o);
            countPayload(o);
        }
        for(HttpRequestDataObject o : f.getListPUT()){
            countHeaders(o);
            countCookie(o);
            countPayload(o);
        }
    }
    
    
    // =========================================================================
    // ---- Output / Formatting : Frequency
    // =========================================================================
    private void CreateFrequencyDatasetFile(){
        
        f.writeToDisk(addLine("count","GET", count_GET, false));
        f.writeToDisk(addLine("count","POST", count_POST, false));
        f.writeToDisk(addLine("count","PUT", count_PUT, false));
        
        f.writeToDisk(addHashToLines(count_userAgent, "User-Agent", true));
        f.writeToDisk(addHashToLines(count_pragma, "Pragma", true));
        f.writeToDisk(addHashToLines(count_cacheControl, "Cache-control", true));
        f.writeToDisk(addHashToLines(count_accept, "Accept", true));
        f.writeToDisk(addHashToLines(count_acceptEncoding, "Accept-Encoding", true));
        f.writeToDisk(addHashToLines(count_acceptCharset, "Accept-Charset", true));
        f.writeToDisk(addHashToLines(count_acceptLanguage, "Accept-Language", true));
        f.writeToDisk(addHashToLines(count_host, "Host", true));
        f.writeToDisk(addHashToLines(count_connection, "Connection", true));
        f.writeToDisk(addHashToLines(count_contentType, "Content-Type", true));
        f.writeToDisk(addHashToLines(count_contentLength, "Content-Length", true));
        
        addHashOfHashToLines(count_payload, "payload");
        addHashOfHashToLines(count_cookie, "cookie");
    }
    
    
    
    
    // =========================================================================
    // ---- Formatting Helpers
    // =========================================================================
    private String addLine(String title, String content, int frequency, boolean parseTitle){
        StringBuilder s = new StringBuilder();
        if(parseTitle){
            content = HttpRequestDataObject.parseAttributeName(content);
        }
        s.append("\"").append(title).append("\",");
        s.append("\"").append(content).append("\",");
        s.append("\"").append(frequency).append("\",");
        s.append("\"").append(filename.substring(0, 4)).append("\"\n");
        
        return s.toString();
    }
    
    
    private String addHashToLines(HashMap<String, Integer> hash, String title, boolean parseTitle){
        StringBuilder result        = new StringBuilder();
        Set<String> keys            = hash.keySet();
        for(String s : keys){
            Integer i = hash.get(s);
            result.append( addLine(title, s, i, parseTitle) );
        }
        return result.toString();
    }
    
    private void addHashOfHashToLines(HashMap<String, HashMap<String, Integer>> hashOfHash, String title){
        //StringBuilder result        = new StringBuilder();
        //Collection<HashMap<String, Integer>> hash = hashOfHash.values();
        Set<String> keys            = hashOfHash.keySet();
        for(String s : keys){
            HashMap<String, Integer> h = hashOfHash.get(s);
            String temp = addHashToLines(h, new StringBuilder(title).append(":").append(s).toString(), false);
            f.writeToDisk( temp );
        }
    }
    
    
    
    
    
    
    // =========================================================================
    // ---- Helpers
    // =========================================================================
    private void countHeaders(HttpRequestDataObject o){
        updateHashMap(count_userAgent, o.userAgent);
        updateHashMap(count_pragma, o.pragma);
        updateHashMap(count_cacheControl, o.cacheControl);
        updateHashMap(count_accept, o.accept);
        updateHashMap(count_acceptEncoding, o.acceptEncoding);
        updateHashMap(count_acceptCharset, o.acceptCharset);
        updateHashMap(count_acceptLanguage, o.acceptLanguage);
        updateHashMap(count_host, o.host);
        updateHashMap(count_connection, o.connection);
        updateHashMap(count_contentType, o.contentType);
        updateHashMap(count_contentLength, o.contentLength);
    }
    private void countCookie(HttpRequestDataObject o){
        String[][] s = o.getCookie();
        for(int i=0; i<s.length; i++){
            String key      = s[i][0];
            String value    = s[i][1];
            
            updateHashMapContent(count_cookie, key, value);
        }
    }
    private void countPayload(HttpRequestDataObject o){
        String[][] s = o.getPayload();
        
        for(int i=0; i<s.length; i++){
            String key      = s[i][0];
            String value    = s[i][1];
            
            updateHashMapContent(count_payload, key, value);
        }
    }
    
    
    
    // =========================================================================
    // ---- Counters
    // =========================================================================
    private HashMap<String,HashMap<String,Integer>> updateHashMapContent( HashMap<String,HashMap<String,Integer>> map, 
                                                                          String key, 
                                                                          String value){
        if(map.containsKey(key)){   //KEY exists
            HashMap<String,Integer> innerMap = map.get(key);
            if(innerMap.containsKey(value)){ //VALUE 
                Integer i = innerMap.get(value);
                i++;
                innerMap.put(value, i);
            }
            else{   //add new VALUE with count = 1.
                innerMap.put(value, 1);
            }
            //??? do we need to reassign innerMap to map ??? 
            //??? e.g. map.put(key, innerMap);
        }
        else{   //KEY and (therefore) VALUE do not exist:    
                //add new KEY, add new VALUE with count = 1.
            HashMap<String,Integer> innerMap = new HashMap<String,Integer>();
            innerMap.put(value, 1);
            map.put(key, innerMap);
        }
        return map;
    }
    private HashMap<String,Integer> updateHashMap(HashMap<String,Integer> map, String s){
        if(map.containsKey(s)){ //increment count
            Integer i = map.get(s);
            i++;
            map.put(s, i);
        }
        else{   //add new with count = 1.
            map.put(s, 1);
        }
        return map;
    }
    
    private void countMethods(){
        count_GET   = f.getListGET().size();
        count_POST  = f.getListPOST().size();
        count_PUT   = f.getListPUT().size();
    }
    
    
    
    
    // =========================================================================
    // ---- Overriden
    // =========================================================================
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,  ToStringStyle.MULTI_LINE_STYLE);
    }
    
}