/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package csic_http_analysis;

/**
 *
 * @author Peter Scully
 */
public class WekaOutputFormat_PayloadLabel extends WekaOutputFormat_HttpObjects {

    
    
    
    // =========================================================================
    // ---- Initialisation
    // =========================================================================
    
    public WekaOutputFormat_PayloadLabel(FileAccess f) {
        super(f);
    }

    @Override
    public void Initialise() {
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
        
        return new StringBuilder(addColumn("url"))
                .append(addColumn("payload_key_raw"))
                .append(addColumn("payload_value_raw"))
                .append(addColumn("payload_key_utf8"))
                .append(addColumn("payload_value_utf8"))
                .append(addFinalColumn("label"))
                .toString();
    }
    private void addDuplicationLine(HttpRequestDataObject o, int index){
        
        String base = new StringBuilder(addColumn(o.url))
                 .toString();
        o.getPayload();
        
        int count = o.getLargestArrayLength();
         
        int i=0;
        do{
            //System.out.println(i+") here count = "+count);
            StringBuilder sb = new StringBuilder(base);
            
            sb.append(addColumn(o.getPayloadKeyRAW(i)));
            sb.append(addColumn(o.getPayloadValueRAW(i)));
            sb.append(addColumn(o.getPayloadKeyUTF8(i)));
            sb.append(addColumn(o.getPayloadValueUTF8(i)));
            sb.append(addFinalColumn(super.filename.substring(0, 4)));
            f.writeToDisk(sb.toString());
            i++;
        }while(i<count);
    }
    
}
