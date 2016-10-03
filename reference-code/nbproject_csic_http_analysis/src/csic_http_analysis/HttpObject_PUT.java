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
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Peter Scully
 */
public class HttpObject_PUT extends HttpRequestDataObject  {

    
    private String line0;
    
    // =========================================================================
    // ---- Initialisation
    // =========================================================================
    public HttpObject_PUT(String line0, BufferedReader br) throws IOException {
        //System.out.println("DO  PUT:\t"+Arrays.toString(line0.split(" ")));
        
        this.line0      = line0;
        parseLine0(line0);
        
        if( method.compareTo("PUT") != 0){
            System.err.println("Header File Format Error. PUT expected, but was missing in line: " + line0);
            throw new IOException();
        }
        
        
        super.userAgent         = br.readLine();
        super.pragma            = br.readLine();
        super.cacheControl      = br.readLine();
        super.accept            = br.readLine();
        super.acceptEncoding    = br.readLine();
        super.acceptCharset     = br.readLine();
        super.acceptLanguage    = br.readLine();
        super.host              = br.readLine();
        super.cookieString      = br.readLine();
        super.contentType       = br.readLine();
        super.connection        = br.readLine();
        super.contentLength     = br.readLine();
        
                
        if(! connection.startsWith("Connection: close")){
            System.err.println("FATAL Header File Format Error. PUT failed to finish correctly: "+line0+"("+connection+")");
            throw new IOException();
        }
                
        //Separator: 1 line
        br.readLine();
        super.payloadString = br.readLine();
        br.readLine();
        parsePayload();
        
        
        
    }
    
    
    
    
    
    // =========================================================================
    // ---- Parsers
    // =========================================================================    
    /** Return method (e.g. "PUT"). Should always be "PUT". If not there is some problem.
     * @param line
     * @return 
     */
    private String parseLine0(String line) {
        String[] s      = line.split(" ");
        super.method    = s[0];
        super.url       = s[1];
        super.protocol  = s[2];
        return super.method;
    }

    private void parsePayload() {
        
        if(contentLength.startsWith("Content-Length: ")){
            String payloadLength = contentLength.substring(15, contentLength.length()).trim();
            int payloadLengthVal = Integer.parseInt(payloadLength);
            if( payloadLengthVal < 0 ){
                System.err.println("FATAL Header File Format Error. PUT Content-Length line invalid: "+line0+"("+contentLength+")");
            }
            else{
                //System.out.println("LEN PUT:\t"+payloadLengthVal);
                //if(payloadString.contains("=")){
                    //System.out.println("CON PUT:\t"+payloadString+"\t"+Arrays.toString(payloadString.split("&")));
                //}
            }
        }
    }

        
}
