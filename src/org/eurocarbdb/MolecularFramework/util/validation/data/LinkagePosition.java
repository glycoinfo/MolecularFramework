package org.eurocarbdb.MolecularFramework.util.validation.data;

import org.eurocarbdb.MolecularFramework.sugar.LinkageType;

public class LinkagePosition
{
    private Integer m_position = null;
    private LinkageType m_linkageType = null;
    public LinkageType getLinkageType()
    {
        return m_linkageType;
    }
    public void setLinkageType(LinkageType linkageType)
    {
        m_linkageType = linkageType;
    }
    public Integer getPosition()
    {
        return m_position;
    }
    public void setPosition(Integer position)
    {
        m_position = position;
    }
}
