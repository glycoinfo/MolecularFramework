package org.eurocarbdb.MolecularFramework.sugar;


/**
 * @author rene
 *
 */
public enum BaseType
{
    DGRO("dgro","2"),
    LGRO("lgro","1"),
    DERY("dery","22"),
    LERY("lery","11"),      
    DRIB("drib","222"),
    LRIB("lrib","111"),     
    DARA("dara","122"),
    LARA("lara","211"),
    DALL("dall","2222"),
    LALL("lall","1111"),
    DALT("dalt","1222"),
    LALT("lalt","2111"),
    DGLC("dglc","2122"),
    LGLC("lglc","1211"),
    DMAN("dman","1122"),
    LMAN("lman","2211"),
    DTHR("dthr","12"),
    LTHR("lthr","21"),
    DXYL("dxyl","212"),
    LXYL("lxyl","121"),
    DLYX("dlyx","112"),
    LLYX("llyx","221"),
    DGUL("dgul","2212"),
    LGUL("lgul","1121"),
    DIDO("dido","1212"),
    LIDO("lido","2121"),
    DGAL("dgal","2112"),
    LGAL("lgal","1221"),
    DTAL("dtal","1112"),
    LTAL("ltal","2221"),

    // basetype jokers
    XGRO("xgro","*"),
    XTHR("xthr","**"),
    XERY("xery","**"),
    XARA("xara","***"),
    XRIB("xrib","***"),
    XLYX("xlyx","***"),
    XXYL("xxyl","***"),     
    XALL("xall","****"),
    XALT("xalt","****"),
    XMAN("xman","****"),
    XGLC("xglc","****"),
    XGUL("xgul","****"),
    XIDO("xido","****"),
    XTAL("xtal","****"),
    XGAL("xgal","****");
    
    private String m_strName;
    private String m_strStereo;
    
    private BaseType( String a_strName, String a_strStereo )
    {
        this.m_strName = a_strName;
        this.m_strStereo = a_strStereo;
    }
    
    public String getName() 
    {  
        return this.m_strName;  
    }
    
    public String getStereoCode() 
    {  
        return this.m_strStereo;  
    }
    
    public static BaseType forName( String a_strName ) throws GlycoconjugateException
    {
        String t_strName = a_strName.toUpperCase();
        for ( BaseType t_objBasetype : BaseType.values() )
        {
            if ( t_objBasetype.m_strName.equalsIgnoreCase(t_strName) )
            {
                return t_objBasetype;
            }
        }
        throw new GlycoconjugateException("Invalid value for basetype");
    }    

    public static BaseType forStereoCode( String a_strCode ) throws GlycoconjugateException
    {
        String t_strName = a_strCode.toUpperCase();
        for ( BaseType t_objBasetype : BaseType.values() )
        {
            if ( t_objBasetype.m_strStereo.equalsIgnoreCase(t_strName) )
            {
                return t_objBasetype;
            }
        }
        throw new GlycoconjugateException("Invalid value for basetype stereo code");
    }    
    
    public boolean absoluteConfigurationUnknown () {
    	
    	if (this.getStereoCode().contains("*")){
    		return true;
    	}
    	else {
    		return false;
    	}
    }

}
