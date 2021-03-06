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

package gov.lbl.srm.client.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Properties;
import java.util.Enumeration;
import java.io.*;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

import javax.swing.JFrame;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.gridforum.jgss.ExtendedGSSManager;
import org.gridforum.jgss.ExtendedGSSCredential;

import org.globus.util.Util;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.bc.*;
import java.security.interfaces.*;
import java.security.PrivateKey;
import java.security.cert.*;

import gov.lbl.srm.client.intf.*;
import gov.lbl.srm.client.util.*;
import gov.lbl.srm.client.exception.*;
import gov.lbl.srm.client.wsdl.*;

//import srm.common.StorageResourceManager.*;
import gov.lbl.srm.StorageResourceManager.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.*;


//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// Class SRMClientSpaceReleaseFile
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public class SRMClientSpaceReleaseFile
{

private GSSCredential mycred;
private XMLParseConfig pConfig = new XMLParseConfig();

private Properties properties = new Properties();
private String configFileLocation = "";
private String _password ="";
private String userDesc="";
private String userKey="";
private String userCert="";
private String proxyFile="";
private boolean isRenew = false;
private boolean textReport=true;
private boolean serviceURLGiven=false;
private String serviceURL="";
private int servicePortNumber=0;
private String serviceHandle="";
private int proxyType;
private boolean forceFileRelease=false;
private String serviceUrl="";
private String storageInfo="";
private String uid="";
private int tokenLifetime = 0; 
private long tokenSize = 0;
private long gTokenSize = 0;
private String fileToken;
private String spaceType="replica";
private String accessLatencyType="online";
private String requestToken;
private Request request;
private String requestType;
private boolean overrideserviceurl=false;
private boolean keepSpace=false;
private String log4jlocation="";
private String logPath="";
private String eventLogPath="";
private String sourceUrl="";
private String inputFile="";
private boolean updateSpace=false;
private boolean getSpaceToken=false;
private boolean getSpaceTokenMeta=false;
private String spaceTokenDesc="";

private static Log logger;
private static PrintIntf pIntf;

private SRMWSDLIntf srmCopyClient;

private boolean _debug=false;
private boolean silent = false;
private boolean useLog=false;
//private int statusMaxTimeAllowed=600;
private int statusMaxTimeAllowed=-1;
private int statusWaitTime=30;
private int connectionTimeOutAllowed=600;
private int setHTTPConnectionTimeOutAllowed=600;
private boolean gotConnectionTimeOut;
private boolean gotHTTPConnectionTimeOut;

private java.util.logging.Logger _theLogger =
        java.util.logging.Logger.getLogger
            (gov.lbl.srm.client.main.SRMClientN.class.getName());
private java.util.logging.FileHandler _fh;

private Vector inputVec = new Vector ();
private String delegationNeeded="";


//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// SRMClientSpaceReleaseFile
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::


public SRMClientSpaceReleaseFile(String[] args, PrintIntf pIntf) {

  this.pIntf = pIntf;

  /*
  if(uid.equals("")) {
    //extract username from env.
    Properties props = System.getProperties();
    String tuid =props.getProperty("user.name");
    if(tuid != null) {
      uid = tuid; 
    }
  }
  */

  if(args.length == 0) {
    showUsage(true);
  }


  for(int i = 0; i < args.length; i++) {
    boolean ok = false;
     if(i == 0 && !args[i].startsWith("-")) {
      sourceUrl=args[i];
      ok = true;
    }
    else if(args[i].equalsIgnoreCase("-conf") && i+1 < args.length) {
      configFileLocation = args[i+1];
      i++;
    }
     else if(args[i].equalsIgnoreCase("-sethttptimeout") && i+1 < args.length) {
     try {
        setHTTPConnectionTimeOutAllowed = Integer.parseInt(args[i+1]);
        i++;
        gotHTTPConnectionTimeOut=true;
     }catch(NumberFormatException nfe) {
        setHTTPConnectionTimeOutAllowed = 600; //using the default value
     }
    }
     else if(args[i].equalsIgnoreCase("-connectiontimeout") && i+1 < args.length) {
     try {
        connectionTimeOutAllowed = Integer.parseInt(args[i+1]);
        i++;
        gotConnectionTimeOut=true;
     }catch(NumberFormatException nfe) {
        connectionTimeOutAllowed = 1800; //using the default value
     }
    }

    else if(args[i].equalsIgnoreCase("-lite")) {
       ;
    }
    else if(args[i].equalsIgnoreCase("-s") && i+1 < args.length) {
      sourceUrl=args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-f") && i+1 < args.length) {
      inputFile=args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-remove")) {
      keepSpace=true;
    }
    else if(args[i].equalsIgnoreCase("-storageinfo")) {
      if(i+1 < args.length) {
         if(args[i+1].startsWith("for")) {
           storageInfo = args[i+1];
           i++;
         }
         else {
           storageInfo = ""+true;
         }
      }
      else {
        storageInfo=""+true;
      }
    }
    /*
    else if(args[i].equalsIgnoreCase("-forcerelease")) {
      forceFileRelease=true;
    }
    */
    else if(args[i].equalsIgnoreCase("-spacedesc") && i+1 < args.length) {
      spaceTokenDesc=args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-requesttoken") && i+1 < args.length) {
      requestToken=args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-serviceurl") && i+1 < args.length) {
      serviceUrl = args[i+1];
      serviceURLGiven=true;
      i++;
    }
    else if(args[i].equalsIgnoreCase("-log") && i+1 < args.length) {
      useLog=true;
      eventLogPath = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-authid") && i+1 < args.length) {
      uid = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-accesslatency") 
		&& i+1 < args.length) {
      accessLatencyType = args[i+1].toUpperCase();
      i++;
      if((!accessLatencyType.equalsIgnoreCase("ONLINE"))
          && (!accessLatencyType.equalsIgnoreCase("NEARLINE"))) {
        System.out.println("\nGiven accesslatency type ignored and using "+
            "default space type " + accessLatencyType);
      }
    }
    else if(args[i].equalsIgnoreCase("-retentionpolicy") 
		&& i+1 < args.length) {
      spaceType = args[i+1].toUpperCase();
      i++;
      if((!spaceType.equalsIgnoreCase("CUSTODIAL"))
          && (!spaceType.equalsIgnoreCase("OUTPUT"))
          && (!spaceType.equalsIgnoreCase("REPLICA"))) {
        System.out.println("\nGiven space type ignored and using "+
            "default space type " + spaceType);
      }
    }
    /*
    else if(args[i].equalsIgnoreCase("-log4jlocation") && i+1 < args.length) {
      log4jlocation = args[i+1];
      i++;
    }
    */
    else if(args[i].equalsIgnoreCase("-userdesc") && i+1 < args.length) {
      userDesc = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-userkey") && i+1 < args.length) {
      userKey = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-usercert") && i+1 < args.length) {
      userCert = args[i+1];
      i++;
    }
    /*
    else if(args[i].equalsIgnoreCase("-textreport")) {
      textReport = true;
    }
    */
    else if(args[i].equalsIgnoreCase("-proxyfile") && i+1 < args.length) {
      proxyFile = args[i+1];
      i++;
    }
    else if(args[i].equalsIgnoreCase("-delegation")) {
       if(i+1 < args.length) {
          delegationNeeded = ""+true;
          i++;
       }
       else {
          delegationNeeded = ""+true;
       }
    }
    else if(args[i].equalsIgnoreCase("-debug")) {
      _debug=true;
    }
    else if(args[i].equalsIgnoreCase("-quiet")) {
      silent=true;
    }
    else if(args[i].equalsIgnoreCase("-renewproxy")) {
      isRenew = true;
    }
    else if(args[i].equalsIgnoreCase("-v2")) {
      ;
    }
    else if(args[i].equalsIgnoreCase("-version")) {
      SRMClientN.printVersion();
    } 
    else if(args[i].equalsIgnoreCase("-help")) {
      showUsage(true);
    } 
    else {
      boolean b = gov.lbl.srm.client.util.Util.parseSrmCpCommands(args[i],0);
      if(b) ;
      else {
        if(!ok) {
         showUsage (true);
        }
      }
    }
  }

  Properties sys_config = new Properties ();

  try {
    if(!configFileLocation.equals("")) {
      File f = new File(configFileLocation);
      if(!f.exists()) {
        System.out.println("\nConfig file does not exists " +
            configFileLocation);
        showUsage(false);
      }
      if(_debug) {
        System.out.println("\nParsing config file " + configFileLocation);
      }
      sys_config = gov.lbl.srm.client.util.Util.parsefile(
			configFileLocation,"SRM-CLIENT",silent,useLog, _theLogger);
    }
  }catch(Exception e) {
    System.out.println("\nSRM-CLIENT: Exception from client="+e.getMessage());
         util.printEventLogException(_theLogger,"",e);
    showUsage(false);
  }

    String stemp = System.getProperty("SRM.HOME");
  if(stemp != null && !stemp.equals("")) {
     configFileLocation = stemp+"/conf/srmclient.conf";
     try {
       File f = new File(configFileLocation);
       if(f.exists()) {
        if(_debug) {
          System.out.println("\nParsing config file " + configFileLocation);
        }
        sys_config = gov.lbl.srm.client.util.Util.parsefile(
			configFileLocation,"SRM-CLIENT",silent,useLog, _theLogger);
       }
     } catch(Exception e) {
        System.out.println("\nSRM-CLIENT: Exception from client="+e.getMessage());
         util.printEventLogException(_theLogger,"",e);
        showUsage(false);
     }
  }


  if(proxyFile.equals("")) {
    Properties props = System.getProperties();
    Enumeration ee = props.propertyNames();
    while (ee.hasMoreElements()) {
     String str = (String) ee.nextElement();
     if(str.trim().equals("X509_USER_PROXY")) {
       String ttemp = props.getProperty(str.trim());
       if(ttemp != null) {
         proxyFile=ttemp;
       }
     }
     //System.out.println(str);
    }
  }

  String detailedLogDate = util.getDetailedDate();


  /*
  if(logPath.equals("")) {
    String temp = (String) sys_config.get("logpath");
    if(temp != null) {
      logPath = temp;
    }
    else {
      logPath ="./srm-client-detailed-"+detailedLogDate+".log";
    }
  }
  */

  if(silent || useLog) {
  if(eventLogPath.equals("")) {
    String temp = (String) sys_config.get("eventlogpath");
    if(temp != null) {
      eventLogPath = temp;
    }
    else {
      eventLogPath = "./srmclient-event-"+detailedLogDate+".log";
    }
  }

  try {
      _fh = new java.util.logging.FileHandler(eventLogPath);
      _fh.setFormatter(new NetLoggerFormatter());
      _theLogger.addHandler(_fh);
      _theLogger.setLevel(java.util.logging.Level.ALL);

      File f = new File(eventLogPath+".lck");
      if(f.exists()) {
        f.delete();
      }
  }catch(Exception e) {
     System.out.println("\nSRM-CLIENT: Exception from client="+e.getMessage());
         util.printEventLogException(_theLogger,"",e);
  }
  }

  /*
  if(silent) {
    if(logPath.equals("")) {
      System.out.println("\nFor the option quiet, -log is needed to " + 
        " forward to output to the logfile");
      System.out.println("Please provide -log <path to logfile> ");
      showUsage(false); 
    }
  }
  */

  String ttemp = System.getProperty("log4j.configuration");
  if(ttemp != null && !ttemp.equals("")) {
     log4jlocation = ttemp;
  }

  //setup log4j configuration
  /*
  String ttemp = System.getProperty("log4j.configuration");
  if(ttemp != null && !ttemp.equals("")) {
     log4jlocation = ttemp;
  }else {
     if(log4jlocation.equals("")) {
       String temp = (String) sys_config.get("log4jlocation");
       if(temp != null) {
         log4jlocation = temp;
       }
       else {
         log4jlocation = "logs/log4j_srmclient.properties";
       }
     }
  }
  */

  /*
  if(!logPath.equals("")) {
    //check existence of logfile.
    if(logPath.endsWith("/")) {
       logPath = logPath.substring(0,logPath.length()-1);
    }
    
    int idx = logPath.lastIndexOf("/");
    if(idx != -1) {
      String logPathDir = logPath.substring(0,idx);
      if(idx == 0) {
        logPathDir = logPath;
      }

      try { 
      File f = new File(logPath);
      if(f.isDirectory()) { 
         System.out.println("Given logpath is a directory, please give " +
			"full path name with desired log file name " + logPath);
         showUsage(false);
      }
      }catch(Exception e) {
         System.out.println("\nSRM-CLIENT: Exception from client="+e.getMessage());
         util.printEventLogException(_theLogger,"",e);
         showUsage(false);
      }

      //rewrite the log4j conf file with the new log path
      try {
         String ref;
         FileInputStream file = new FileInputStream(log4jlocation);
         BufferedReader in = new BufferedReader(new InputStreamReader(file));
         FileOutputStream outFile =
             new FileOutputStream(logPathDir+"/log4j_srmclient.properties");
         BufferedWriter out =
             new BufferedWriter(new OutputStreamWriter(outFile));

         while((ref= in.readLine()) != null) {
            if(ref.startsWith("log4j.appender.SRMCOPY.File")) {
              out.write("log4j.appender.SRMCOPY.File="+logPath+"\n");
            }
            else {
              out.write(ref+"\n");
            }
         }
         in.close();
         if(file != null) file.close();
         out.close();
         if(outFile != null) outFile.close();

      }catch(IOException ex) {
         System.out.println("\nSRM-CLIENT: Exception from client="+ex.getMessage());
         util.printEventLogException(_theLogger,"",ex);
         showUsage(false);
      }
      log4jlocation=logPathDir+"/log4j_srmclient.properties";
      PropertyConfigurator.configure(log4jlocation);
      ClassLoader cl = this.getClass().getClassLoader();
      try {
         Class c = cl.loadClass("gov.lbl.srm.client.main.SRMClientSpace");
         logger = LogFactory.getLog(c.getName());
      }catch(ClassNotFoundException cnfe) {
         System.out.println("ClassNotFoundException " + cnfe.getMessage());
         util.printEventLogException(_theLogger,"",cnfe);
      }
    }
  }
  else {
     logPath ="./srm-client-detailed.log";
  }
  */

  try {
  PropertyConfigurator.configure(log4jlocation);
  }catch(Exception ee){;}

  /*
  if(_debug) {
    util.printMessage("Log4jlocation " + log4jlocation, logger,silent);
  }
  */

  //default values such as "Enter a Value"
  properties.put("user-cert", pConfig.getUserCert());
  properties.put("user-key", pConfig.getUserKey());
  properties.put("proxy-file", pConfig.getProxyFile());


  try {

    if(!userKey.equals("")) {
      pConfig.setUserKey(userKey);
      properties.put("user-key",userKey);
    }
    else {
      String temp =  (String) sys_config.get("UserKey");
      if(temp != null) {
        userKey = temp;
        pConfig.setUserKey(userKey);
        properties.put("user-key",userKey);
      }
    }
    if(!userCert.equals("")) {
      pConfig.setUserCert(userCert);
      properties.put("user-cert", userCert);
    }
    else {
      String temp =  (String) sys_config.get("UserCert");
      if(temp != null) {
        userCert = temp;
        pConfig.setUserKey(userCert);
        properties.put("user-cert",userCert);
      }
    }
    if(!proxyFile.equals("")) {
      pConfig.setProxyFile(proxyFile);
      properties.put("proxy-file", proxyFile);
    }
    else {
      String temp =  (String) sys_config.get("ProxyFile");
      if(temp != null) {
        proxyFile = temp;
        pConfig.setUserKey(proxyFile);
        properties.put("proxy-file",proxyFile);
      }
    }

    if(!gotHTTPConnectionTimeOut) {
      String temp = (String) sys_config.get("SetHTTPConnectionTimeOut");
      if(temp != null) {
        int x = 0;
        try {
           x = Integer.parseInt(temp);
           setHTTPConnectionTimeOutAllowed = x;
        }catch(NumberFormatException nfe) {
           setHTTPConnectionTimeOutAllowed=600;
        }
      }
     }

    if(!gotConnectionTimeOut) {
      String temp = (String) sys_config.get("ConnectionTimeOut");
      if(temp != null) {
        int x = 0;
        try {
           x = Integer.parseInt(temp);
           connectionTimeOutAllowed = x;
        }catch(NumberFormatException nfe) {
           connectionTimeOutAllowed=1800;
        }
      }
     }


    String xtemp = (String) sys_config.get("ServicePortNumber");
    if(xtemp != null) {
        int x = 0;
        try {
           x = Integer.parseInt(xtemp);
           servicePortNumber = x;
        }catch(NumberFormatException nfe) { }
    }

    String xxtemp = (String) sys_config.get("ServiceHandle");
    if(xxtemp != null) {
         serviceHandle = xxtemp;
    }


     if(!serviceURLGiven) {
        String temp = (String) sys_config.get("ServiceURL");
        if(temp != null) {
           serviceURL = temp;
        }
     }


     //if all three not provided, using default proxy.
     //if one of userCert or userKey is provided, the other is needed
     if(proxyFile.equals("")) {
       if(userCert.equals("") && userKey.equals("")) {
         //System.out.println
		   //("\nProxyFile or UserCert and UserKey is not provided");
         try {
           //proxyFile ="/tmp/x509up_u"+MyConfigUtil.getUID();
           proxyFile ="/tmp/x509up_u"+MyConfigUtil.getUID2();
         }catch(Exception e) {
		   System.out.println("\nSRM-CLIENT: Exception from client="+e.getMessage());
         util.printEventLogException(_theLogger,"",e);
		   proxyFile ="/tmp/x509up_u"+MyConfigUtil.getUID();
         }
         pConfig.setProxyFile(proxyFile);
         properties.put("proxy-file", proxyFile);
         //System.out.println("\nUsing default user proxy " + proxyFile);
       }
       else {
         if(userCert.equals("") || userKey.equals("")) {
           inputVec.clear();
           inputVec.addElement("UserCert and UserKey both should be provided");
           util.printEventLog(_theLogger,"SRMClientSpace",inputVec,silent,useLog); 
           System.out.println
		     ("\nSRM-CLIENT: UserCert and UserKey both should be provided");
           showUsage(false);
         }
       }
     }

     if(isRenew) {
      String v1 = (String) properties.get("user-cert");
      String v2 = (String) properties.get("user-key");
      if(v1.startsWith("Enter") || v2.startsWith("Enter")) {
        inputVec.clear();
        inputVec.addElement("If you want to renew proxy automatically, " +
         "you need to enter user-cert location and user-key location.");
        util.printEventLog(_theLogger,"SRMClientSpace",inputVec,silent,useLog); 
        System.out.println("\nSRM-CLIENT: If you want to renew proxy automatically,\n "+
         "you need to enter user-cert location and user-key location.");
         inputVec.clear();
		 inputVec.addElement("StatusCode=93");
		 util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
        System.exit(93); 
      }
      String v3 = (String)properties.get("proxy-file");
      if(v3.startsWith("Enter")) {
        inputVec.clear();
        inputVec.addElement("If you want to renew proxy automatically, "+
          "please enter your proxy file location.");
        util.printEventLog(_theLogger,"SRMClientSpace",inputVec,silent,useLog); 
        System.out.println("\nSRM-CLIENT: If you want to renew proxy automatically,\n "+
          "please enter your proxy file location.");
         inputVec.clear();
		 inputVec.addElement("StatusCode=93");
		 util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
        System.exit(93); 
      }
      else {
        inputVec.clear();
        inputVec.addElement("Enter GRID passphrase");
        util.printEventLog(_theLogger,"SRMClientSpace",inputVec,silent,useLog); 
        String line = PasswordField.readPassword("Enter GRID passphrase: ");
        _password = line;
      }
      //else there is no need to renew proxy.
     }

     String[] surl=null;

     Vector fileInfo = new Vector();
     String temp = "";

     if(inputFile.equals("")) {
          surl = new String[1];
          if(sourceUrl.equals("") && (requestToken == null || requestToken.equals(""))) {
              util.printMessage("\nSRM-CLIENT: Please provide either -s <sourceUrl>", logger,silent);
              util.printMessage("or -f <inputFile> or -requesttoken",logger,silent);
              inputVec.clear();
              inputVec.addElement("Please provide either -s <sourceUrl> " +
                 "or -f <inputFile> or -requesttoken");
              util.printEventLog(_theLogger,"SRMClientSpace",inputVec,silent,useLog); 
          }
          surl[0] = sourceUrl;
          request = gov.lbl.srm.client.util.Util.createRequest(surl[0],"","",_debug,silent,useLog,
            "SRM-CLIENT",false,_theLogger,logger);
       }
       else {
          request = gov.lbl.srm.client.util.Util.parseXML(
			inputFile,"SRM-CLIENT",silent,useLog,_theLogger);
       }

       request.setModeType("releaseFile");

       requestType = request.getModeType();
       fileInfo = validateURL(request);

       String tempType="surlType";

       if(fileInfo.size() > 0) {
          FileInfo file = (FileInfo) fileInfo.elementAt(0);
          if(requestType.equalsIgnoreCase("put")) {
            temp = file.getTURL();  
            tempType="turlType";
          }
          else {
            temp = file.getSURL();  
          }
          if(!serviceURLGiven) {
            overrideserviceurl = true;
          }
       }
       else {  
            util.printMessage("\nSRM-CLIENT: No valid files in this request.",
				logger,silent);
            util.printMessage("Please check your input.",logger,silent);
            inputVec.clear();
            inputVec.addElement("No valid files in this request " +
               "Please check your input.");
            util.printEventLog(_theLogger,"SRMClientSpace",inputVec,silent,useLog);
            showUsage(false);
       }

     if(overrideserviceurl) {
       serviceUrl = gov.lbl.srm.client.util.Util.getServiceUrl(
			temp,serviceURL,serviceHandle,servicePortNumber,1,silent,useLog,_theLogger,logger);
       if(serviceUrl == null) showUsage(false);

       for(int i = 0; i < fileInfo.size(); i++) {
         FileIntf fIntf = (FileIntf) fileInfo.elementAt(i);
         if(tempType.equals("surlType")) {
           String temp1 = fIntf.getSURL();
           String sfn = gov.lbl.srm.client.util.Util.getSFN(temp1);
           fIntf.setSURL(serviceUrl.replace("httpg","srm")+sfn);
         }
         else {
           String temp1 = fIntf.getTURL();
           String sfn = gov.lbl.srm.client.util.Util.getSFN(temp1);
           fIntf.setTURL(serviceUrl.replace("httpg","srm")+sfn);
         }
       }
     }
     else {
       if(serviceUrl.equals("")) {
          String tt = (String) sys_config.get("ServiceUrl");
          if(tt != null) {
             serviceUrl = tt;
          }
          else {
           inputVec.clear();
		   inputVec.addElement("Please provide the -serviceurl full SRM service url");
           inputVec.addElement (" example:srm://<hostname>:<port>//wsdlpath");
           util.printEventLog(_theLogger,"SRMClientSpace",inputVec,silent,useLog);
           util.printMessage 
		      ("\nSRM-CLIENT: Please provide the -serviceurl full SRM service url",logger,silent);
           util.printMessage 
		      ("  example:srm://<hostname>:<port>//wsdlpath",logger,silent);
                showUsage(false);
          }
       }
      }

       GSSCredential credential=null;
     try {
       credential = gov.lbl.srm.client.util.Util.checkTimeLeft
            (pConfig,properties,_password, _theLogger,silent,useLog,logger,pIntf,_debug);
       proxyType=gov.lbl.srm.client.util.Util.getProxyType(properties);
     }catch(Exception ee) {
         System.out.println("\nSRM-CLIENT: Exception from client="+ee.getMessage());
         util.printEventLogException(_theLogger,"",ee);
         inputVec.clear();
		 inputVec.addElement("StatusCode=92");
		 util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
         util.printHException(ee,pIntf);
        System.exit(92);
     }

      serviceUrl = gov.lbl.srm.client.util.Util.getServiceUrl(
			serviceUrl,serviceURL,serviceHandle,servicePortNumber,0,silent,useLog,_theLogger,logger);
      if(serviceUrl == null) showUsage(false);

      //util.printMessage("ServiceUrl " + serviceUrl,logger,silent);
      inputVec.clear();
      inputVec.addElement("ServiceUrl="+serviceUrl);
      util.printEventLog(_theLogger,"SRMClientSpace",inputVec,silent,useLog);

      if(_debug) {
      util.printMessage("\n===================================",logger,silent);
      util.printMessage("SRM-CLIENT configuration",logger,silent);
      util.printMessage("\nserviceUrl     : " + serviceUrl,logger,silent);
      inputVec.clear();
      inputVec.addElement("ServiceUrl="+serviceUrl);
      util.printMessage("userid         : " + uid,logger,silent);
      inputVec.addElement("userid="+uid);
      util.printMessage("request token : " + requestToken, logger,silent);   
      inputVec.addElement("RequestToken="+requestToken);
      util.printMessage("Debug ON       : " + _debug,logger,silent);
      util.printMessage("Quiet ON       : " + silent,logger,silent);
      inputVec.addElement("Debug="+_debug);
      inputVec.addElement("Quiet="+silent);

      util.printEventLog(_theLogger,"Initialization",inputVec,silent,useLog);
      }

        util.printMessage("SRM-CLIENT: " + "Connecting to serviceurl " +
            serviceUrl,logger,silent);

      String sCode = null;

      //if(requestToken != null) {
          if(_debug) {
             util.printMessage("\nSRM-CLIENT: Releasing file now.", logger,silent);
          }
          inputVec.clear();
          util.printEventLog(_theLogger,"Releasing file now",inputVec,silent,useLog);
          SRMUtilClient utilClient = new SRMUtilClient
               (serviceUrl,uid, userDesc, credential, _theLogger, logger,
					pIntf, _debug,silent,useLog,false, false, 
				    statusMaxTimeAllowed,
				    statusWaitTime,storageInfo,proxyType,
					connectionTimeOutAllowed,setHTTPConnectionTimeOutAllowed,
			        delegationNeeded,3,60);
          utilClient.setRequestToken(requestToken);
          sCode = utilClient.releaseFile(keepSpace, requestType, fileInfo);
      //}
      /*
      else {
          inputVec.clear();
          inputVec.addElement("No request token provided. Cannot release file");
          util.printEventLog(_theLogger,"Releasing file now",inputVec,silent,useLog);
          util.printMessage
            ("\nSRM-CLIENT: No request token provided. Cannot release file", logger,silent);
          showUsage(false);
          
      }
      */
      if(pIntf == null) {
         int exitValue = util.mapStatusCode(sCode);
         inputVec.clear();
		 inputVec.addElement("StatusCode="+exitValue);
		 util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
         System.exit(exitValue);
      }
   }catch(Exception e) {
      System.out.println("\nSRM-CLIENT: Exception from client="+e.getMessage());
         util.printEventLogException(_theLogger,"",e);
      if(pIntf == null) {
         inputVec.clear();
		 inputVec.addElement("StatusCode=92");
		 util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
         System.exit(92);
      }
      util.printHException(e,pIntf);
   }
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// validateURL
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

private Vector  validateURL(Request request) {
  Vector result = new Vector ();
  Vector fInfo = request.getFiles();
  int size = fInfo.size(); 
  for(int i = 0; i < size; i++) {
    boolean skip = false;
    FileInfo f = (FileInfo) fInfo.elementAt(i);
    String surl = f.getSURL();
    String turl = f.getTURL();
    if(request.getModeType().equalsIgnoreCase("dir")) {
       if(!surl.startsWith("srm://")) {
         inputVec.clear();
         inputVec.addElement("source url is not valid " + surl);
         inputVec.addElement("skipping this url in the request");
         util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
         util.printMessage("\nSRM-CLIENT: source url is not valid " + surl, logger,silent);
         util.printMessage("\nskipping this url in the request", logger,silent);
         skip = true;
       }
   }
   else if(request.getModeType().equalsIgnoreCase("releaseFile")) {
       if(surl.equals("")) { ; }
       else {
       if(!surl.startsWith("srm://")) {
         inputVec.clear();
         inputVec.addElement("source url is not valid " + surl);
         inputVec.addElement("skipping this url in the request");
         util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
         util.printMessage("\nSRM-CLIENT: source url is not valid " + surl, logger,silent);
         util.printMessage("\nskipping this url in the request", logger,silent);
         skip = true;
       }
      }
   }
   else if((request.getModeType().equalsIgnoreCase("extendlifetime")) || 
           (request.getModeType().equalsIgnoreCase("purgefromspace")) ||
           (request.getModeType().equalsIgnoreCase("changespace"))) {
       if(!surl.startsWith("srm://") && !surl.equals("")) {
         inputVec.clear();
         inputVec.addElement("source url is not valid " + surl);
         inputVec.addElement("skipping this url in the request");
         util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
         util.printMessage("\nSRM-CLIENT: source url is not valid " + surl, logger,silent);
         util.printMessage("\nskipping this url in the request", logger,silent);
         skip = true;
       }
   }
   else {
      if((surl.startsWith("gsiftp:")) || (surl.startsWith("srm:")) ||
         (surl.startsWith("ftp:")) || (surl.startsWith("http:"))) {
         if((!turl.startsWith("file:")) && (!turl.startsWith("srm:"))) {
           inputVec.clear();
           inputVec.addElement("source turl is not valid " + surl);
           inputVec.addElement("skipping this turl in the request");
           util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
           util.printMessage("\nSRM-CLIENT: turl is not valid " + turl,logger,silent);
           //util.printMessage("\nfor the given surl " + surl,logger);
           util.printMessage("SRM-CLIENT: skipping this file in the request",logger,silent);
           skip = true;
         }
      }
      else if(surl.startsWith("file:")) {
        if(!turl.startsWith("srm:")) {
          inputVec.clear();
          inputVec.addElement("source turl is not valid " + surl);
          inputVec.addElement("skipping this file in the request");
          util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
          util.printMessage("\nSRM-CLIENT: turl is not valid " + turl,logger,silent);
          //util.printMessage("\nfor the given surl " + surl,logger,silent);
          util.printMessage("SRM-CLIENT: skipping this file in the request",logger,silent);
          skip = true;
        }
      }
      else {
        inputVec.clear();
        inputVec.addElement("Given surl is not valid " + surl);
        inputVec.addElement ("surl should start with gsiftp,ftp,http,srm,file");
        inputVec.addElement("skipping this file in the request");
        util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
        util.printMessage("\nSRM-CLIENT: Given surl is not valid " + surl, logger,silent);
        util.printMessage
          ("\nSRM-CLIENT: surl should start with gsiftp,ftp,http,srm,file",logger,silent);
        util.printMessage("SRM-CLIENT: skipping this file in the request.",logger,silent);
        skip = true;
      }
    }
    if(!skip) {
        if(requestType.equalsIgnoreCase("Put")) {
          //check source file exists
          try {
            MyGlobusURL gurl = new MyGlobusURL(surl,1);
            String path = gurl.getFilePath();
            File ff = new File(path);
            if(ff.exists()) {
              f.setExpectedSize(""+ff.length());
              result.add(f);
            }
            else {
               util.printMessage
                    ("\nSRM-CLIENT: Source file does not exists " + surl,logger,silent);
               inputVec.clear();
               inputVec.addElement ("Source file does not exists " + surl);
               util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
            }
          }catch(Exception e) {
            System.out.println("\nSRM-CLIENT: Exception from client="+e.getMessage());
         util.printEventLogException(_theLogger,"",e);
            inputVec.clear();
            inputVec.addElement("Exception="+e.getMessage());
            util.printEventLog(_theLogger,"ValidateURL",inputVec,silent,useLog);
          }
        }
        else {
          result.add(f);
        }
    }//end if(!skip)
   }//end for
   size = result.size();
   for(int i =0; i < result.size(); i++) {
      FileInfo f = (FileInfo) result.elementAt(i);
      f.setLabel(i);
   }
   return result;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// showUsage
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void showUsage (boolean b) {
 if(b) { 
    System.out.println("Usage :\n"+
            "\tsrm-release -s <sourceurl> [command line options] \n" +
            "\tor srm-release <sourceurl> [command line options] \n" +
            "\tcommand line options will override the options from conf file\n"+
            "\n"+
            "\t-conf              <path>\n"+
            "\t-s                 <source_url> \n"+
            "\t-f                 <inputFile> \n"+
		    "\t-serviceurl        <full wsdl service url> \n" +
            "\t                      example srm://host:port/wsdlpath \n"+
            "\t                      (required for requests when surl\n" +
            "\t                       does not have wsdl information)\n"+
            "\t-authid            (user authorization id used in SRM)\n"+
            "\t-remove             default:false\n"+
            "\t-requesttoken      <request_token >(valid request token returned by server)\n" +
            //"\t-forcerelease      (release file forcefully default:false\n" +
			"\t-delegation        uses delegation (default: no delegation)\n"+ 
            "\t-proxyfile         <path to proxyfile>default:from user default proxy location\n"+
            "\t-usercert          <path to usercert> \n" +
            "\t-userkey           <path to userkey> \n" +
            //"\t-renewproxy             (renews proxy automatically) \n" +
            "\t-storageinfo       <true|false|string> extra storage access information\n"+ 
            "\t                    when needed. a formatted input separated by comma \n"+
            "\t                    is used with following keywords: \n"+
            "\t                    for:<source|target|sourcetarget>,login:<string>,\n"+
            "\t                    passwd:<string>,projectid:<string>,readpasswd:<string>,\n"+  
            "\t                    writepasswd<string> (default:false)\n"+ 
            //"\t-connectiontimeout <integer in seconds> (enforce http connection timeout in the given seconds)default:600 \n"+
            "\t-sethttptimeout    <integer in seconds> (enforce SRM/httpg connection timeout and sets client-side http connection timeout in the given seconds)default:600 \n"+
	        "\t-quiet             default false\n" +
            "\t                      suppress output in the console.\n" +
            "\t                      this option puts the output to the logfile default:srmclient-event-date-random.log\n" +
            "\t-log               <path to logfile>\n" +
            "\t-debug             (true | false) default false\n" +
            "\t-help              show this message.");
 } 
 if(pIntf == null) {
         inputVec.clear();
		 inputVec.addElement("StatusCode=93");
		 util.printEventLog(_theLogger,"StatusExit",inputVec,silent,useLog);
         System.exit(93);
 }
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getConfigFileLocation
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public String getConfigFileLocation () {
  return configFileLocation;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getProperties
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public Properties getProperties() {
  return properties;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// setConfig
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void setConfig (XMLParseConfig config) {
  pConfig = config;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// setPassword
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public void setPassword(String str) {
  _password = str;
  boolean b = false;
  if(_password != null && _password.trim().length() > 0) {
    b = true;
  }
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
// getPassword
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public String getPassword() {
  return _password;
}

//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//  main driver method
//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

public static void main(String[] args) {
   new SRMClientSpaceReleaseFile(args,null);
}

}

