/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.OGBI;

import org.eurocarbdb.MolecularFramework.sugar.GlycoGraph;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;

/**
 * @author Logan
 *
 */
public class OgbiResidue 
{
	public Monosaccharide m_objMS;
	public GlycoGraph m_objGraph;
	public boolean m_bUnderdeterminded;
	
	public OgbiResidue(Monosaccharide a_objMS,GlycoGraph a_objGraph,boolean a_bUnderdeterminded)
	{
		this.m_bUnderdeterminded = a_bUnderdeterminded;
		this.m_objMS = a_objMS;
		this.m_objGraph = a_objGraph;
	}	
}
