/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.namespace;

import java.util.HashMap;

import org.eurocarbdb.MolecularFramework.sugar.GlycoGraphAlternative;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative;

/**
 * @author Logan
 *
 */
public class AlternativeMapping 
{
	private SugarUnitAlternative m_objOriginal;
	private SugarUnitAlternative m_objCopy;
	private HashMap<GlycoNode,GlycoNode> m_hResidueMap;
	private GlycoGraphAlternative m_objGraphOriginal;
	private GlycoGraphAlternative m_objGraphCopy;
	
	public AlternativeMapping(SugarUnitAlternative a_objOriginal, GlycoGraphAlternative a_objGraphOrignal,
			SugarUnitAlternative a_objCopy, GlycoGraphAlternative a_objGraphCopy,
			HashMap<GlycoNode,GlycoNode> a_hResidueMap)
	{
		this.m_objOriginal = a_objOriginal;
		this.m_objCopy = a_objCopy;
		this.m_hResidueMap = a_hResidueMap;
		this.m_objGraphCopy = a_objGraphCopy;
		this.m_objGraphOriginal = a_objGraphOrignal;
	}
	
	public SugarUnitAlternative getOriginal()
	{
		return this.m_objOriginal;
	}
	
	public SugarUnitAlternative getCopy()
	{
		return this.m_objCopy;
	}
	
	public HashMap<GlycoNode,GlycoNode> getMapping()
	{
		return this.m_hResidueMap;
	}
	
	public GlycoGraphAlternative getGraphOriginal()
	{
		return this.m_objGraphOriginal;
	}
	
	public GlycoGraphAlternative getGraphCopy()
	{
		return this.m_objGraphCopy;
	}
}
