/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.validation;

import java.util.ArrayList;
import java.util.HashMap;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;

/**
 * @author rene
 *
 */
public class SugarGraphInformation
{
    private SugarGraphAglycon m_objAglyca = null;
    private Sugar m_objSugar = null;
    private ArrayList<SugarGraphAglycon> m_aTerminalAglyca = new ArrayList<SugarGraphAglycon>();
    private HashMap<GlycoNode,GlycoNode> m_hTopLevelNodes = new HashMap<GlycoNode,GlycoNode>(); 
    
    public SugarGraphInformation( Sugar a_objSugar, GlycoNode a_strAglyca , GlycoNode a_objReducing, GlycoEdge a_objAglycaEdge)
    {
        super();
        this.m_objSugar = a_objSugar;
        this.m_objAglyca = new SugarGraphAglycon(a_strAglyca,a_objReducing,a_objAglycaEdge); 
    }

    public SugarGraphAglycon getReducingInformation()
    {
        return this.m_objAglyca;
    }
    
    public void setReducingInformation(SugarGraphAglycon a_strAglyca)
    {
        this.m_objAglyca = a_strAglyca;
    }
    
    public void setSugar(Sugar a_objSugar)
    {
        this.m_objSugar = a_objSugar;
    }
    
    public Sugar getSugar()
    {
        return this.m_objSugar;
    }
    
    public ArrayList<SugarGraphAglycon> getTerminalInformation()
    {
    	return this.m_aTerminalAglyca;
    }
    
    public void addTerminalInformation(SugarGraphAglycon a_objAglyca)
    {
    	this.m_aTerminalAglyca.add(a_objAglyca);
    }
    
    public void setTerminalInformation(ArrayList<SugarGraphAglycon> t_aTerminal)
    {
    	this.m_aTerminalAglyca = t_aTerminal;
    }
    
    public HashMap<GlycoNode,GlycoNode> getTopLevelNodes()
    {
        return this.m_hTopLevelNodes;
    }
    
    public void addTopLevelNode(GlycoNode a_objOld,GlycoNode a_objNew)
    {
        this.m_hTopLevelNodes.put(a_objOld,a_objNew);
    }
}