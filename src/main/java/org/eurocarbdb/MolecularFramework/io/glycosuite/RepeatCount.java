package org.eurocarbdb.MolecularFramework.io.glycosuite;

public class RepeatCount
{
    private int m_min = -1;
    private int m_max = -1;
    public RepeatCount(int a_min, int a_max)
    {
        this.m_min = a_min;
        this.m_max = a_max;
    }
    public RepeatCount(int a_value)
    {
        this.m_min = a_value;
        this.m_max = a_value;
    }
    public int getMin()
    {
        return m_min;
    }
    public void setMin(int min)
    {
        m_min = min;
    }
    public int getMax()
    {
        return m_max;
    }
    public void setMax(int max)
    {
        m_max = max;
    }
}
