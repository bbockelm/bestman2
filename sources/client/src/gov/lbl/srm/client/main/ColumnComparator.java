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

import java.util.*;


public class ColumnComparator implements Comparator
{
  protected int index;
  protected boolean ascending;
  
  public ColumnComparator(int index, boolean ascending)
  {
    this.index = index;
    this.ascending = ascending;
  }
  
  public int compare(Object one, Object two) 
  {
    if (one instanceof Vector &&
        two instanceof Vector)
    {
      Vector vOne = (Vector)one;
      Vector vTwo = (Vector)two;
      Object oOne = vOne.elementAt(index);
      Object oTwo = vTwo.elementAt(index);
      if (oOne instanceof Comparable &&
          oTwo instanceof Comparable)
      {
        Comparable cOne = (Comparable)oOne;
        Comparable cTwo = (Comparable)oTwo;
        if (ascending)
        {
          return cOne.compareTo(cTwo);
        }
        else
        {
          return cTwo.compareTo(cOne);
        }
      }
    }
    return 1;
  }
}

