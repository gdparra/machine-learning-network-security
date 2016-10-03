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
class HttpObject_GET extends HttpRequestDataObject {

    
    
    
    
    
    // =========================================================================
    // ---- Initialisation
    // =========================================================================
    public HttpObject_GET(String line0, BufferedReader br) throws IOException {
        //System.out.println("DO  GET:\t" + Arrays.toString(line0.split(" ")));
        
        String method = parseLine0(line0);
        if( method.compareTo("GET") != 0){
            System.err.println("Header File Format Error. GET expected, but was missing in line: " + line0);
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
        super.connection        = br.readLine();
        
        
//        super.contentType       = "";
//        super.contentLength     = "";
        
        
        if(! connection.startsWith("Connection: close")){
            System.err.println("FATAL Header File Format Error. GET failed to finish correctly: "+line0+"("+connection+")");
            throw new IOException();
        }
        
        //Separator: from end of GET to (exclusive) next header. (2 lines)
        br.readLine();
        br.readLine();
    }

    
    
    // =========================================================================
    // ---- Parsers
    // =========================================================================    
    /** Return method (e.g. "GET"). Should always be "GET". If not there is some problem.
     * @param line
     * @return 
     */
    private String parseLine0(String line) {
        String[] s      = line.split(" ");
        super.method    = s[0];
        super.protocol  = s[2];
        String[] urlArr = s[1].split("\\?");
        super.url           = urlArr[0];
        if(urlArr.length > 1){
            super.payloadString = urlArr[1];
        }
        return super.method;
    }
    
}
