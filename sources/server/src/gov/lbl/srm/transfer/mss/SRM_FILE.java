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

package gov.lbl.srm.transfer.mss;

import java.util.Vector;
import java.util.Hashtable;

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// SRM_FILE
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public class SRM_FILE {

 private String requestToken="";
 private String file="";
 private long size;
 private String timeStamp="";
 private boolean alreadyReported=false;
 private MSS_MESSAGE status; 
 private String explanation="";
 private ExecScript currentProcess;
 private FileObj fileObj;
 private int numTimesStatusCalled;
 private long startTimeStamp = System.currentTimeMillis();

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// SRM_FILE
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void clean() {
	if (fileObj != null) {
	    fileObj.clean();
	    fileObj = null;
	}
	currentProcess = null;
}

public SRM_FILE () { 
  status = MSS_MESSAGE.SRM_MSS_REQUEST_QUEUED;
  explanation="Request queued";
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getStartTimeStamp
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public long getStartTimeStamp() {
  return startTimeStamp;
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// setStartTimeStamp
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void setStartTimeStamp (long startTimeStamp) {
  this.startTimeStamp = startTimeStamp;
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//setRequestToken
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void setRequestToken(String rid) {
  requestToken = rid;
}

//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//getRequestToken
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public String getRequestToken() {
  return requestToken;
}

public void setStatus(MSS_MESSAGE status) {
  this.status = status;
}

public ExecScript getCurrentProcess() {
  return currentProcess;
}
 
public void setCurrentProcess(ExecScript process) {
  currentProcess = process;
}

public void setFileObj (FileObj fileObj) {
   this.fileObj = fileObj;
}
 
public FileObj getFileObj () {
   return fileObj;
}


public void setAlreadyReported(boolean b) {
  alreadyReported = b;
}

public boolean getAlreadyReported() {
  return alreadyReported;
}

public int getAlreadyReportedCount() {
  return numTimesStatusCalled;
}

public void increaseAlreadyReportedCount() {
  numTimesStatusCalled++;
}

public MSS_MESSAGE getStatus() {
  return status;
}

public void setFile(String file) {
  this.file = file;
}

public void setExplanation(String msg) {
  explanation = msg;
}

public String getExplanation() {
  return explanation;
}

public void setTimeStamp(String time) {
  timeStamp = time;
}

public String getTimeStamp() {
  return timeStamp;
}

public void setSize (long size) {
  this.size = size;
}

public String getFile() {
  return this.file;
}

public long getSize() {
  return this.size;
}

}
