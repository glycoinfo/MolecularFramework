package org.eurocarbdb.MolecularFramework.sugar;

public enum Anomer
{
    Alpha("alpha", "a"),
    Beta("beta", "b"),
    OpenChain("open-chain", "o"),
    Unknown("unknown", "x");

    /** Anomer verbose name */
    private String m_strFullname;

    /** Anomer short name. */
    private String m_strSymbol;

    /** Private constructor, see the forName methods for external use. */
    private Anomer( String fullname, String symbol )
    {
        this.m_strFullname = fullname;
        this.m_strSymbol = symbol;
    }

    /** Returns this anomer's full name  */
    public String getName() 
    {  
        return this.m_strFullname;  
    }

    /** Returns the abbreviated name (symbol) of this anomer.  */
    public String getSymbol() 
    {  
        return this.m_strSymbol;  
    }

    /** Returns the appropriate Anomer instance for the given character/symbol.  
     * @throws GlycoconjugateException */
    public static Anomer forName( String anomer ) throws GlycoconjugateException
    {
        for ( Anomer a : Anomer.values() )
        {
            if ( anomer.equals(a.m_strFullname) )
            {
                return a;
            }
        }
        throw new GlycoconjugateException("Invalid value for anomer");
    }

    /** Returns the appropriate Anomer instance for the given character/symbol.  
     * @throws GlycoconjugateException */
    public static Anomer forSymbol( char anomer ) throws GlycoconjugateException
    {
        for ( Anomer a : Anomer.values() )
        {
            if ( anomer == a.m_strSymbol.charAt(0) )
            {
                return a;
            }
        }
        throw new GlycoconjugateException("Invalid value for anomer");
    }
    
    public static Anomer forSymbol( String anomer ) throws GlycoconjugateException
    {
        for ( Anomer a : Anomer.values() )
        {
            if ( a.m_strSymbol.equals(anomer) )
            {
                return a;
            }
        }
        throw new GlycoconjugateException("Invalid value for anomer");
    }
}
