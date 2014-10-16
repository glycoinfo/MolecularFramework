package org.eurocarbdb.MolecularFramework.io;

public enum SequenceFormat
{
    BCSDB("bcsdb"),
    BCSDB3("bcsdb3"),
    LINUCS("linucs"),
    CARBBANK("carbbank"),
    GLYCOCT_CONDENSED("glycoct_condensed"),
    GLYOCCT_XML("glycoct_xml"),
    GLYDEII("glydeii"),
    KCF("kcf"),
    KCF_SOKA("kcf_soka"),
    GLYCOBASE_LILLE("glycobase_lille"),
    GLYCOBASE_DUBLIN("glycobase_dublin"),
    LINEARCODE("linearcode");

    private String m_strFullname;

    private SequenceFormat( String fullname)
    {
        this.m_strFullname = fullname;
    }

    public String getName() 
    {  
        return this.m_strFullname;  
    }

    public static SequenceFormat forName( String name ) throws SequenceFormatException
    {
        for ( SequenceFormat a : SequenceFormat.values() )
        {
            if ( name.equalsIgnoreCase(a.m_strFullname) )
            {
                return a;
            }
        }
        throw new SequenceFormatException("Invalid value for sequence format : " + name);
    }
}
