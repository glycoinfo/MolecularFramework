/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.analytical.disaccharide;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;

/**
 * @author Logan
 *
 */
public class Disaccharide 
{
	private Boolean touched = false;
	private Monosaccharide m_objParent = null;
	private Monosaccharide m_objChild = null;
	private GlycoEdge m_objLinkage = null;
	
	public void setParent(Monosaccharide a_objParent)
	{
		this.m_objParent = a_objParent;
	}
	
	public void setChild(Monosaccharide a_objChild)
	{
		this.m_objChild = a_objChild;
	}
	
	public void setLinkage(GlycoEdge a_objEdge)
	{
		this.m_objLinkage = a_objEdge;		
	}
	
	public Monosaccharide getParent()
	{
		return this.m_objParent;
	}
	
	public Monosaccharide getChild()
	{
		return this.m_objChild;
	}
	
	public GlycoEdge getLinkage()
	{
		return this.m_objLinkage;
	}

	public Boolean getTouched() {
		return touched;
	}

	public void setTouched(Boolean touched) {
		this.touched = touched;
	}
}
