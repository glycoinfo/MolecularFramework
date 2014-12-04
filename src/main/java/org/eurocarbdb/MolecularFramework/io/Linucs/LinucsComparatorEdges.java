package org.eurocarbdb.MolecularFramework.io.Linucs;

import java.util.ArrayList;
import java.util.Comparator;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;

public class LinucsComparatorEdges  implements Comparator<GlycoEdge>
{
	public int compare(GlycoEdge arg0, GlycoEdge arg1) 
	{
		Linkage t_objLinkages0 = arg0.getGlycosidicLinkages().get(0);
		Linkage t_objLinkages1 = arg1.getGlycosidicLinkages().get(0);
		ArrayList <Integer> t_ag0 = t_objLinkages0.getParentLinkages();
		ArrayList <Integer> t_ag1 = t_objLinkages1.getParentLinkages();
		
		// first Parent Linkage Array bigger
		if ( t_ag0.size() > t_ag1.size() )
		{
			for (int i = 0; i < t_ag1.size(); i++) 
			{
				if (t_ag0.get(i) < t_ag1.get(i))
				{
					return 1;
				}
				else if ( t_ag0.get(i) > t_ag1.get(i))
				{
					return -1;
				}
			}	
			return -1;
		}
		// second Parent Linkage Array bigger
		else if (t_ag0.size() < t_ag1.size())
		{
			for (int i = 0; i < t_ag0.size(); i++) 
			{
				if ( t_ag0.get(i) < t_ag1.get(i) )
				{
					return 1;
				}
				else if ( t_ag0.get(i) > t_ag1.get(i))
				{
					return -1;
				}
			}
			return 1;
		}
		// same length of Parent Linkage Array
		else 
		{ 		
			for (int i = 0; i < t_ag0.size(); i++) 
			{
				if ( t_ag0.get(i) < t_ag1.get(i))
				{
					return -1;
				}
				else if ( t_ag0.get(i) > t_ag1.get(i))
				{
					return 1;
				}
			}
		}		

		t_ag0 = t_objLinkages0.getChildLinkages();
		t_ag1 = t_objLinkages1.getChildLinkages();
		// first Child Linkage Array bigger
		if ( t_ag0.size() > t_ag1.size() )
		{
			for (int i = 0; i < t_ag1.size(); i++) 
			{
				if (t_ag0.get(i) < t_ag1.get(i))
				{
					return 1;
				}
				else if ( t_ag0.get(i) > t_ag1.get(i))
				{
					return -1;
				}
			}	
			return -1;
		}
		// second Child Linkage Array bigger
		else if (t_ag0.size() < t_ag1.size())
		{
			for (int i = 0; i < t_ag0.size(); i++) 
			{
				if ( t_ag0.get(i) < t_ag1.get(i) )
				{
					return 1;
				}
				else if ( t_ag0.get(i) > t_ag1.get(i))
				{
					return -1;
				}
			}
			return 1;
		}
		// same length of Child Linkage Array
		else 
		{ 		
			for (int i = 0; i < t_ag0.size(); i++) 
			{
				if ( t_ag0.get(i) < t_ag1.get(i))
				{
					return -1;
				}
				else if ( t_ag0.get(i) > t_ag1.get(i))
				{
					return 1;
				}
			}
		}		
		return 0;
	}
}