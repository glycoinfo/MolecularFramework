package org.eurocarbdb.MolecularFramework.io.kcf;

public class ExportLinkageKCF 
{
    private Integer m_id = 0;
    private String m_childString = null;
    private String m_parentString = null;
    public Integer getId() {
        return m_id;
    }
    public void setId(Integer a_id) {
        m_id = a_id;
    }
    public String getChildString() {
        return m_childString;
    }
    public void setChildString(String a_childString) {
        m_childString = a_childString;
    }
    public String getParentString() {
        return m_parentString;
    }
    public void setParentString(String a_parentString) {
        m_parentString = a_parentString;
    }
}
