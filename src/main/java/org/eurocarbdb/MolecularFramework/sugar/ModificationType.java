package org.eurocarbdb.MolecularFramework.sugar;

/**
 * @author Logan
 *
 */
public enum ModificationType 
{
    DEOXY("d"),
    ACID("a"),
    KETO("keto"),
    ALDI("aldi"),
    DOUBLEBOND("en"),
    UNKNOWN_DOUBLEBOND("enx"),
    SP2_HYBRID("sp2"),
    TRIPLEBOND("sp"),
    GEMINAL("geminal");
    
    private String m_strSymbol = "";
    
    /** Private constructor, see the forName methods for external use. */
    private ModificationType( String symbol )
    {
        this.m_strSymbol = symbol;
    }

    /** Returns the appropriate Anomer instance for the given character/symbol.  
     * @throws GlycoconjugateException */
    public static ModificationType forName( String a_strModi ) throws GlycoconjugateException
    {
        for ( ModificationType a : ModificationType.values() )
        {
            if ( a_strModi.equalsIgnoreCase( a.m_strSymbol) )
            {
                return a;
            }
        }
        throw new GlycoconjugateException("Invalid value for modification");
    }

    public String getName()
    {
    	return this.m_strSymbol;
    }
    
}
