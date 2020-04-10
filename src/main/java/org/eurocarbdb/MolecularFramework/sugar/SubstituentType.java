/**
 * 
 */
package org.eurocarbdb.MolecularFramework.sugar;

/**
 * @author rene
 * complex means, only linkage position 1 is allowed for non complex substitutents
 */
public enum SubstituentType
{
    ACETYL("acetyl",1,1,false),
    AMINO("amino",1,2, false),
    ANHYDRO("anhydro",2,2, false),
    BROMO("bromo",1,1, false),
    CHLORO("chloro",1,1, false),
    EPOXY("epoxy",2,2, false), // 0
    ETHANOLAMINE("ethanolamine",1,0, false), 
    ETHYL("ethyl",1,1, false),
    FLOURO("fluoro",1,1, false),
    FORMYL("formyl",1,1, false),
    GLYCOLYL("glycolyl",1,1, false),
    HYDROXYMETHYL("hydroxymethyl",1,1, false),
    IMINO("imino",1,2, false),
    IODO("iodo",1,1, false),
    LACTONE("lactone",2,2, false), // 0
    METHYL("methyl",1,1, false),
    N_ACETYL("n-acetyl",1,1, false),
    N_ALANINE("n-alanine",1,1, false),
    N_DIMETHYL("n-dimethyl",1,1, false),
    N_FORMYL("n-formyl",1,1, false),
    N_GLYCOLYL("n-glycolyl",1,1, false),
    N_METHYL("n-methyl",1,1, false),
    N_SUCCINATE("n-succinate",1,0, false),
    SUCCINATE("succinate",1,0, false),
    N_SULFATE("n-sulfate",1,2, false),
    N_TRIFLOUROACETYL("n-triflouroacetyl",1,1, false),
    NITRATE("nitrate",1,1, false),
    PHOSPHATE("phosphate",1,3, false),
    PYRUVATE("pyruvate",2,2, false), // 0
    PYROPHOSPHATE("pyrophosphate",1,3, false),
    TRIPHOSPHATE("triphosphate",1,3, false),
    R_LACTATE("(r)-lactate",1,2, false),
    R_PYRUVATE("(r)-pyruvate",2,2, false),
    S_LACTATE("(s)-lactate",1,2, false),
    S_PYRUVATE("(s)-pyruvate",2,2, false),
    SULFATE("sulfate",1,2, false),
//    THIO("thio",1,1, false),
    THIO("thio",1,2, false),  // changed by Masaaki Matsubara 04/10/2020
    AMIDINO("amidino",1,1, false),
    N_AMIDINO("n-amidino",1,1, false),
    R_CARBOXYMETHYL("(r)-carboxymethyl",1,1, false),
    CARBOXYMETHYL("carboxymethyl",1,1, false),
    S_CARBOXYMETHYL("(s)-carboxymethyl",1,1, false),
    R_CARBOXYETHYL("(r)-carboxyethyl",1,1, false),
    S_CARBOXYETHYL("(s)-carboxyethyl",1,1, false),
    N_METHYLCARBAMOYL("n-methyl-carbamoyl",1,1, false),
    PHOSPHO_ETHANOLAMINE("phospho-ethanolamine",1,2, false),    
    DIPHOSPHO_ETHANOLAMINE("diphospho-ethanolamine",1,2, false),
    PHOSPHO_CHOLINE("phospho-choline",1,1, false),
    X_LACTATE("(x)-lactate",1,2, false),
//    X_PYRUVATE("(x)-pyruvate",2,2, false),  
    R_1_HYDROXYMETHYL("(r)-1-hydroxymethyl",1,1, false),
    S_1_HYDROXYMETHYL("(s)-1-hydroxymethyl",1,1, false);  
    
    private String m_strName;
    private Integer minValence;
    private Integer maxValence;
    private Boolean complex;
    
    private SubstituentType( String a_strName, Integer a_minValence, Integer a_maxValence, Boolean a_complex )
    {
        this.m_strName = a_strName;
        this.minValence = a_minValence;
        this.complex = a_complex;
        this.maxValence = a_maxValence;
        
    }

    public static SubstituentType forName( String a_strName )
    {
        String t_strName = a_strName.toUpperCase();
        for ( SubstituentType t_objType : SubstituentType.values() )
        {
            if ( t_objType.m_strName.equalsIgnoreCase(t_strName) )
            {
                return t_objType;
            }
        }
        return null;
    }       
    
    public String getName()
    {
        return this.m_strName;
    }
    
    public Integer getMinValence()
    {
        return this.minValence;
    }
    
    public Integer getMaxValence()
    {
        return this.maxValence;
    }
    
    public Boolean getComplexType (){
    	return this.complex;
    }
}



