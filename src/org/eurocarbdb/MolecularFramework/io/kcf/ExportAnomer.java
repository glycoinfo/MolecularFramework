package org.eurocarbdb.MolecularFramework.io.kcf;

import org.eurocarbdb.MolecularFramework.sugar.Anomer;

public class ExportAnomer 
{
    private Integer m_position = 1;
    private Anomer m_anomer = Anomer.Alpha;
    public Integer getPosition()
    {
        return this.m_position;
    }
    public void setPosition(Integer a_position)
    {
        this.m_position = a_position;
    }
    public Anomer getAnomer()
    {
        return this.m_anomer;
    }
    public void setAnomer(Anomer a_anomer)
    {
        this.m_anomer = a_anomer;
    }
}
