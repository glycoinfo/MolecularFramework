package org.eurocarbdb.MolecularFramework.io.kcf;

public class ExportResidueKCF 
{
    private double m_x = 0;
    private double m_y = 0;
    private Integer m_id = 0;
    private String m_name = null;
    
    public double getX() {
        return m_x;
    }
    public void setX(double a_x) {
        m_x = a_x;
    }
    public double getY() {
        return m_y;
    }
    public void setY(double a_y) {
        m_y = a_y;
    }
    public Integer getId() {
        return m_id;
    }
    public void setId(Integer a_id) {
        m_id = a_id;
    }
    public String getName() {
        return m_name;
    }
    public void setName(String a_name) {
        m_name = a_name;
    }
}
