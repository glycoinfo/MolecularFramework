package org.eurocarbdb.MolecularFramework.sugar;


/**
 * @author rene
 *
 */
public enum LinkageType
{
    H_LOSE('h'),
    DEOXY('d'),
    H_AT_OH('o'),
    UNKNOWN('x'),
    NONMONOSACCHARID('n'),
    S_CONFIG('s'),
    R_CONFIG('r'),
    UNVALIDATED('u');

    private char m_cSymbol;

    private LinkageType( char a_cSymbol )
    {
        this.m_cSymbol = a_cSymbol;
    }
    
    public char getType() 
    {  
        return this.m_cSymbol;  
    }

    public static LinkageType forName( char a_cName ) throws GlycoconjugateException
    {
        for ( LinkageType a : LinkageType.values() )
        {
            if ( a_cName == a.m_cSymbol )
            {
                return a;
            }
        }
        throw new GlycoconjugateException("Invalid value for a linkagetype");
    }

   
}
