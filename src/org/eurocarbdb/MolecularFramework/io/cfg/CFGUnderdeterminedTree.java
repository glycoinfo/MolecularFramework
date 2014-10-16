/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.cfg;

import org.eurocarbdb.MolecularFramework.sugar.UnderdeterminedSubTree;

/**
 * @author rene
 *
 */
public class CFGUnderdeterminedTree
{
    private UnderdeterminedSubTree m_objTree = null;
    private Integer m_iID = 0;
    
    public void setId(Integer a_iID)
    {
        this.m_iID = a_iID;
    }
    
    public Integer getId()
    {
        return this.m_iID;
    }
    
    public void setTree(UnderdeterminedSubTree a_objNode)
    {
        this.m_objTree = a_objNode;
    }
    
    public UnderdeterminedSubTree getTree()
    {
        return this.m_objTree;
    }
}
