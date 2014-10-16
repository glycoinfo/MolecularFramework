package org.eurocarbdb.MolecularFramework.util.analytical.misc;

import java.util.Comparator;

import org.eurocarbdb.MolecularFramework.sugar.Modification;


public class ComparatorModification  implements Comparator<Modification>
{
	public int compare(Modification arg0, Modification arg1) 
	{
		if (arg0.getPositionOne() < arg1.getPositionOne())
		{
			return -1;
		} 
		else if (arg0.getPositionOne() > arg1.getPositionOne())
		{
			return 1;
		}
		else
		{
			if ( arg0.getPositionTwo() == null )
			{
				if ( arg1.getPositionTwo() == null )
				{
					return arg0.getModificationType().compareTo(arg1.getModificationType());
				}
				else
				{
					return 1;
				}				
			}
			else
			{
				if ( arg1.getPositionTwo() == null )
				{
					return -1;
				}
				else
				{
					if (arg0.getPositionTwo() < arg1.getPositionTwo())
					{
						return -1;
					}
					if (arg0.getPositionTwo() > arg1.getPositionTwo())
					{
						return 1;
					}			
					return arg0.getModificationType().compareTo(arg1.getModificationType());		
				}				
			}			
		}
	}
}
