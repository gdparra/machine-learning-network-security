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


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author Peter Scully
 */
public class WekaOutputFormat_HttpObjects {

    // =========================================================================
    // ---- Data
    // =========================================================================
    
    protected FileAccess f;
    protected String filename;
    
    // =========================================================================
    // ---- Initialisation
    // =========================================================================
    
    public WekaOutputFormat_HttpObjects(FileAccess f) {
        this.f = f;
        filename = f.getFilename();
    }

    public void Initialise(){
        CreateDuplicationDatasetFile();
    }
    
    
    
    // =========================================================================
    // ---- Output / Formatting : Duplication
    // =========================================================================
    
    
    private void CreateDuplicationDatasetFile(){
        int c=0;
        f.writeToDisk(addHeader());
        
        for(HttpRequestDataObject o : f.getListGET()){
            addDuplicationLine(o,c++);
        }
        for(HttpRequestDataObject o : f.getListPOST()){
            addDuplicationLine(o,c++);
        }
        for(HttpRequestDataObject o : f.getListPUT()){
            addDuplicationLine(o,c++);
        }
    }
    
    
    // =========================================================================
    // ---- Helpers
    // =========================================================================
    
    private String addHeader(){
        
        return new StringBuilder(addColumn("index"))
                .append(addColumn("method"))
                .append(addColumn("url"))
                .append(addColumn("protocol"))
                .append(addColumn("userAgent"))
                .append(addColumn("pragma"))
                .append(addColumn("cacheControl"))
                .append(addColumn("accept"))
                .append(addColumn("acceptEncoding"))
                .append(addColumn("acceptCharset"))
                .append(addColumn("acceptLanguage"))
                .append(addColumn("host"))
                .append(addColumn("connection"))
                .append(addColumn("contentLength"))
                .append(addColumn("contentType"))
                .append(addColumn("cookie"))
                .append(addColumn("payload"))
                .append(addFinalColumn("label"))
                .toString();
    }
    private void addDuplicationLine(HttpRequestDataObject o, int index){
        
        String base = new StringBuilder(addColumn(""+index))
                .append(addColumn(o.method))
                .append(addColumn(o.url))
                .append(addColumn(o.protocol))
                .append(addColumn(HttpRequestDataObject.parseAttributeName(o.userAgent)))
                .append(addColumn(HttpRequestDataObject.parseAttributeName(o.pragma)))
                .append(addColumn(HttpRequestDataObject.parseAttributeName(o.cacheControl)))
                .append(addColumn(HttpRequestDataObject.parseAttributeName(o.accept)))
                .append(addColumn(HttpRequestDataObject.parseAttributeName(o.acceptEncoding)))
                .append(addColumn(HttpRequestDataObject.parseAttributeName(o.acceptCharset)))
                .append(addColumn(HttpRequestDataObject.parseAttributeName(o.acceptLanguage)))
                .append(addColumn(HttpRequestDataObject.parseAttributeName(o.host)))
                .append(addColumn(HttpRequestDataObject.parseAttributeName(o.connection)))
                .append(addColumn(HttpRequestDataObject.parseAttributeName(o.contentLength)))
                .append(addColumn(HttpRequestDataObject.parseAttributeName(o.contentType)))
                 .toString();
        o.getCookie();
        o.getPayload();
        
        int count = o.getLargestArrayLength();
         
        int i=0;
        do{
            //System.out.println(i+") here count = "+count);
            StringBuilder sb = new StringBuilder(base);
            
            sb.append(addColumn(o.getCookiePairUTF8(i)));
            sb.append(addColumn(o.getPayloadPairUTF8(i)));
            sb.append(addFinalColumn(filename.substring(0, 4)));
            f.writeToDisk(sb.toString());
            i++;
        }while(i<count);
    }
    
    protected String addColumn(String s){
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(s).append("\",");
        return sb.toString();
    }
    
    protected String addFinalColumn(String s){
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(s).append("\"\n");
        return sb.toString();
    }
    
    
    // =========================================================================
    // ---- Overriden
    // =========================================================================
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,  ToStringStyle.MULTI_LINE_STYLE);
    }
    
    
    
    
    
    
    
    
}
