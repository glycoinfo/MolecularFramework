package org.eurocarbdb.MolecularFramework.util.validation.data;

import java.util.ArrayList;
import java.util.List;

public class GlycoEdgeInformation
{
    private List<LinkagePosition> m_child = new ArrayList<LinkagePosition>();
    private List<LinkagePosition> m_parent = new ArrayList<LinkagePosition>();
    public List<LinkagePosition> getChild()
    {
        return m_child;
    }
    public void setChild(List<LinkagePosition> a_child)
    {
        m_child = a_child;
    }
    public List<LinkagePosition> getParent()
    {
        return m_parent;
    }
    public void setParent(List<LinkagePosition> a_parent)
    {
        m_parent = a_parent;
    }
}
