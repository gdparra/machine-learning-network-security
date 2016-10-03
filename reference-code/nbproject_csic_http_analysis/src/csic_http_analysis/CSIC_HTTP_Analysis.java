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

/**
 *
 * @author pds7
 */
public class CSIC_HTTP_Analysis {

    
    
    
    public static void main(String[] args) {
        //test_header_file //normalTrafficTraining.txt //normalTrafficTest.txt //anomalousTrafficTest.txt
        FileAccess f;
        //FrequencyAnalysis_HttpObjects fa = new FrequencyAnalysis_HttpObjects(f);
        //WekaOutputFormat_HttpObjects wa = new WekaOutputFormat_HttpObjects(f);
        WekaOutputFormat_PayloadLabel wa;   //new WekaOutputFormat_PayloadLabel(f);
        
        
        f = new FileAccess("/Users/Eudora/Downloads/normalTrafficTraining.txt", "payload_ONLY_output_http_csic_2010_weka_with_duplications_utf8_escd_norm.csv");
        f.read();
        wa = new WekaOutputFormat_PayloadLabel(f);
        wa.Initialise();
        
        
        f = new FileAccess("/Users/Eudora/Downloads/normalTrafficTest.txt", "payload_ONLY_output_http_csic_2010_weka_with_duplications_utf8_escd_norm_test.csv");
        f.read();
        wa = new WekaOutputFormat_PayloadLabel(f);
        wa.Initialise();
    }
    

    
    
    
}
