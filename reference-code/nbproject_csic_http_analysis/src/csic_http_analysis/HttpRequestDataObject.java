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


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author Peter Scully
 */
class HttpRequestDataObject {

    // =========================================================================
    // ---- Data
    // =========================================================================
    protected String method;
    protected String url;
    protected String protocol;
    protected String userAgent;
    protected String pragma;
    protected String cacheControl;
    protected String accept;
    protected String acceptEncoding;
    protected String acceptCharset;
    protected String acceptLanguage;
    protected String host;
    protected String connection;
    
    protected String contentLength;
    protected String contentType;
    
    protected String cookieString;
    protected String payloadString;
    
    protected String[][] cookie;
    protected String[][] payload;
    
    
    private final static int COLUMN_PAIR                  = -1;
    private final static int COLUMN_PAYLOAD_KEY_UTF8      = 0;
    private final static int COLUMN_PAYLOAD_VALUE_UTF8    = 1;
    private final static int COLUMN_PAYLOAD_KEY_RAW       = 2;
    private final static int COLUMN_PAYLOAD_VALUE_RAW     = 3;
    
    
    // =========================================================================
    // ---- Initialisation
    // =========================================================================
    protected HttpRequestDataObject(){
        
    }
    
    
    
    
    
    
    
    // =========================================================================
    // ---- Getters
    // =========================================================================
    protected String[][] getCookie(){
        if(cookie == null){
            cookie = parseHTTPPayloadContentTo2DArray( parseAttributeName(cookieString) );
        }
        return cookie;
    }
    protected String[][] getPayload(){
        if(payload == null){
            payload = parseHTTPPayloadContentTo2DArray(payloadString);
        }
        return payload;
    }
    
    
    
    
    // =========================================================================
    // ---- Helpers
    // =========================================================================    
    protected String[][] parseHTTPPayloadContentTo2DArray(String content){
        if(content == null){
            content = "";
        }
        String[] pairs              = content.split("&");
        String[][] keyValuePairs    = new String[pairs.length][2];
        int c = 0;
        for(int i=0; i < pairs.length; i++){
            String s            = pairs[i];
            if(s.contains("=")){
                String[] temp       = s.split("=");
                keyValuePairs[c][0] = temp[0];
                keyValuePairs[c][1] = temp[1];
                c++;
            }
        }
        return keyValuePairs;
    }
    
    protected static String parseAttributeName(String line){
        String result = line;
        if(line != null){
            int i = line.indexOf(":");
            if(i != -1){
                String[] t = line.split(":", 2);
                if(t.length >= 2){
                    result = t[1].trim();
                }
            }
        }
        return result;
    }

    public int getLargestArrayLength() {
        int result = 0;
        if(cookie != null){
            int largest = cookie.length;
            result = (payload.length > largest) ? payload.length : largest;
        }
        else if(payload != null){
            result = payload.length;
        }
        else{
            result = 0;
        }
        
        return result;
    }

    /**
     * Optimistic. Assumes elements in array[index][0] and array[index][1] are not null.
     * 
     * @param array
     * @param i
     * @return 
     */
    private String getKeyValue(String[][] array, int i, int COLUMN) {
        String result = "";
        if(array != null){
            int finalIndex = array.length-1;
            if(i >= finalIndex){                    //On or greater than last index.
                if(array[finalIndex] != null){
                    if(array[finalIndex][0] != null && array[finalIndex][1] != null){
                        result = selectParsedText(array, finalIndex, COLUMN);
                    }
                }
            }
            else if(i < finalIndex && i >= 0){      //Valid, non-last index:
                if(array[i] != null){
                    if(array[finalIndex][0] != null && array[finalIndex][1] != null){
                        result = selectParsedText(array, i, COLUMN);
                    }
                }
            }
        }
        return result;
    }
    private String selectParsedText(String[][] array, int finalIndex, int COLUMN){
        String result = "";
        if(COLUMN == COLUMN_PAIR){
            result = getPairParsed(array, finalIndex);
        }
        else if(COLUMN == COLUMN_PAYLOAD_KEY_UTF8){
            result = getKeyParsed_UTF8(array, finalIndex);
        }
        else if(COLUMN == COLUMN_PAYLOAD_VALUE_UTF8){
            result = getValueParsed_UTF8(array, finalIndex);
        }
        else if(COLUMN == COLUMN_PAYLOAD_KEY_RAW){
            result = getKeyParsed_RAW(array, finalIndex);
        }
        else if(COLUMN == COLUMN_PAYLOAD_VALUE_RAW){
            result = getValueParsed_RAW(array, finalIndex);
        }
        return result;
    }    
    private String getPairParsed(String[][] array, int finalIndex){
        String key = parse_UTF8(array[finalIndex][0]);
        String val = parse_UTF8(array[finalIndex][1]);
        return joinPair(key, val);
    }
    private String getKeyParsed_UTF8(String[][] array, int finalIndex){
        return parse_UTF8(array[finalIndex][0]);
    }
    private String getValueParsed_UTF8(String[][] array, int finalIndex){
        return parse_UTF8(array[finalIndex][1]);
    }    
    private String getKeyParsed_RAW(String[][] array, int finalIndex){
        return escapeChars(array[finalIndex][0]);
    }
    private String getValueParsed_RAW(String[][] array, int finalIndex){
        return escapeChars(array[finalIndex][1]);
    }    
    private String joinPair(String key, String val){
        return new StringBuilder(key).append("=").append(val).toString();
    }
    private String escapeChars(String text){
        return text.replace("\"", "\\\"");                 //Added to escape any double-quotes found in the payload. To aid parsing CSV lines as input.
    }
    private String parse_UTF8(String text){
        String result = "";
        try {
            String temp     = URLDecoder.decode(text, "UTF-8");
            temp            = escapeChars(temp);
            result          = temp;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger("").log(Level.SEVERE, new StringBuilder("Failed to decode text to UTF8. Input:").append(text).toString(), ex);
        }
        return result;
    }
    
    public String getCookiePairUTF8(int i) {
        return getKeyValue(cookie, i, COLUMN_PAIR);
    }
    public String getPayloadPairUTF8(int i) {
        return getKeyValue(payload, i, COLUMN_PAIR);
    }
    
    public String getPayloadKeyUTF8(int i) {
        return getKeyValue(payload, i, COLUMN_PAYLOAD_KEY_UTF8);
    }
    public String getPayloadValueUTF8(int i) {
        return getKeyValue(payload, i, COLUMN_PAYLOAD_VALUE_UTF8);
    }
    public String getPayloadKeyRAW(int i) {
        return getKeyValue(payload, i, COLUMN_PAYLOAD_KEY_RAW);
    }
    public String getPayloadValueRAW(int i) {
        return getKeyValue(payload, i, COLUMN_PAYLOAD_VALUE_RAW);
    }

    
    
    // =========================================================================
    // ---- Overriden
    // =========================================================================
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,  ToStringStyle.MULTI_LINE_STYLE);
    }
}
