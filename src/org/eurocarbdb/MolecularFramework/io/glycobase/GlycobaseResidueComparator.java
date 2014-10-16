/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.glycobase;

import java.util.Comparator;


/**
 * @author rene
 *
 */
public class GlycobaseResidueComparator implements Comparator<GlycobaseResidue> 
{
	public int compare(GlycobaseResidue arg0, GlycobaseResidue arg1) 
	{
	    int t_iLength0 = arg0.m_strPosition.split(",").length;
	    int t_iLength1 = arg1.m_strPosition.split(",").length;
	    if ( t_iLength0 > t_iLength1 )
	    {
	        return 1;
	    }
	    if ( t_iLength0 < t_iLength1 )
        {
            return -1;
        }
	    return 0;
	}
}
