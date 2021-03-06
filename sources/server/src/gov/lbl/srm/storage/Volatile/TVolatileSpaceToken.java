/**
 *
 * *** Copyright Notice ***
 *
 * BeStMan Copyright (c) 2010, The Regents of the University of California, 
 * through Lawrence Berkeley National Laboratory (subject to receipt of any 
 * required approvals from the U.S. Dept. of Energy).  This software was 
 * developed under funding from the U.S. Department of Energy and is 
 * associated with the Berkeley Lab Scientific Data Management Group projects.
 * All rights reserved.
 * 
 * If you have questions about your rights to use or distribute this software, 
 * please contact Berkeley Lab's Technology Transfer Department at TTD@lbl.gov.
 * 
 * NOTICE.  This software was developed under funding from the 
 * U.S. Department of Energy.  As such, the U.S. Government has been granted 
 * for itself and others acting on its behalf a paid-up, nonexclusive, 
 * irrevocable, worldwide license in the Software to reproduce, prepare 
 * derivative works, and perform publicly and display publicly.  
 * Beginning five (5) years after the date permission to assert copyright is 
 * obtained from the U.S. Department of Energy, and subject to any subsequent 
 * five (5) year renewals, the U.S. Government is granted for itself and others
 * acting on its behalf a paid-up, nonexclusive, irrevocable, worldwide license
 * in the Software to reproduce, prepare derivative works, distribute copies to
 * the public, perform publicly and display publicly, and to permit others to
 * do so.
 *
*/

/**
 *
 * Email questions to SRM@LBL.GOV
 * Scientific Data Management Research Group
 * Lawrence Berkeley National Laboratory
 * http://sdm.lbl.gov/bestman
 *
*/

package gov.lbl.srm.storage.Volatile;

//import srm.common.StorageResourceManager.*;
import gov.lbl.srm.StorageResourceManager.*;
import gov.lbl.srm.storage.*;
import gov.lbl.srm.util.*;
import gov.lbl.srm.server.*;

import java.util.*;

public class TVolatileSpaceToken extends TSpaceTokenSkeleton {

        public TVolatileSpaceToken(TTokenAttributes desc, TBasicDevice device) {
	    super(desc, device);
	    //super.setID(TSRMStorage._DefVolatileTokenPrefix+TSRMUtil.getTokenID());
	    
	    /*
	    // this is need to make correct accounting of tokens issued.
	    String defaultID = TSRMUtil.getTokenID(); 

	    if ((desc.getTokenID() != null) && (desc.getTokenID().length() > 0)) {
		super.setID(desc.getTokenID()); // needed to read back cache log
	    } else {
		super.setID(TSRMStorage._DefVolatileTokenPrefix+defaultID);
	    }
	    */
	    super.setID(TSRMSpaceType._DefVolatileTokenPrefix, desc.getTokenID());
	}

	public TSRMSpaceType getType(){
	    return TSRMSpaceType.Volatile;
	}

        public TFileStorageType getDefaultFileType() {
	    return TFileStorageType.VOLATILE;
	}	      

        public void actionOnReleasedFiles(Set files) {
	    // volatile space has no specific reaction to released files
	}

       
}



