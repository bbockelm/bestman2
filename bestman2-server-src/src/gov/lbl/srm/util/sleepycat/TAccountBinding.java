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

package gov.lbl.srm.util.sleepycat;

import gov.lbl.srm.server.*;
import gov.lbl.srm.util.TSRMUtil;

import com.sleepycat.bind.tuple.*;
import com.sleepycat.je.*;

public class TAccountBinding extends TSRMTupleBinding {
    public Object entryToObject(TupleInput ti) {
	String accountName = ti.readString();

	TSRMUtil.startUpInfo("@@ Hello, I read: "+accountName+"!!");
	if (accountName.equals(TAccountManager._SUPER_USER.getID())) {
	    return TAccountManager._SUPER_USER;
	} else {
	    TAccount result = TAccountManager.getAccount(accountName);
	    if (result != null) {
		return result;
	    } else {
		return TAccountManager.createAccount(accountName);
	    }
	}
    }

    public void objectToEntry(Object object, TupleOutput to) {
	TAccount account = (TAccount)object;
	to.writeString(account.getID());
    }

    public static void writeMe(TAccount acct, Database db) {
	TupleBinding accountBinding = new TAccountBinding();
	
	setKey(acct.getID());
	accountBinding.objectToEntry(acct, _data);

	writeData(db);	
    }
}