package org.eurocarbdb.MolecularFramework.sugar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.util.analytical.misc.ComparatorModification;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

/**
 * 
 * @author rene
 */
public class Monosaccharide extends GlycoNode 
{
	private     Anomer      m_enumAnomer;
	private     ArrayList<BaseType> m_aBaseType = new ArrayList<BaseType>();
	private     Superclass  m_enumSuperclass;
	// both -1 for unknown ; both 0 for open ring 
	public static final int UNKNOWN_RING   = -1;
	public static final int OPEN_CHAIN   = 0;
	private     int         m_iRingStart;
	private     int         m_iRingEnd;
	private 	ArrayList<Modification> m_aModifications = new ArrayList<Modification>();

	private Monosaccharide()
	{
		super();
	}

	public Monosaccharide(Anomer a_enumAnomer, Superclass a_enumSuperclass) throws GlycoconjugateException
	{
		if ( a_enumAnomer == null )
		{
			throw new GlycoconjugateException("Anomer can't be null");
		}
		this.m_enumAnomer = a_enumAnomer;
		if ( a_enumSuperclass == null )
		{
			throw new GlycoconjugateException("Superclass can't be null");
		}
		this.m_enumSuperclass = a_enumSuperclass;
		this.m_iRingEnd = -1;
		this.m_iRingStart = -1;
		this.m_aModifications.clear();
		this.m_aBaseType.clear();
	}

	public void setAnomer(Anomer a_enumAnomer) throws GlycoconjugateException
	{
		if ( a_enumAnomer == null )
		{
			throw new GlycoconjugateException("Anomer can't be null");
		}
		this.m_enumAnomer = a_enumAnomer;
	}

	public void setSuperclass( Superclass a_enumSuperclass ) throws GlycoconjugateException
	{
		if ( a_enumSuperclass == null )
		{
			throw new GlycoconjugateException("Superclass can't be null");
		}
		this.m_enumSuperclass = a_enumSuperclass;
	}

	public Superclass getSuperclass()
	{
		return this.m_enumSuperclass;
	}

	/**
	 * 
	 * @return Anomer or null if not validated
	 */


	public Anomer getAnomer() 
	{
		return this.m_enumAnomer;
	}

	/**
	 * -1 ; -1 for unknown
	 *  0 ;  0 for open chain
	 * @param a_iStart
	 * @param a_iEnd
	 * @return
	 * @throws GlycoconjugateException 
	 */
	public void setRing(int a_iStart, int a_iEnd) throws GlycoconjugateException
	{
		if ( (a_iEnd==0 || a_iStart == 0) && ((a_iEnd+a_iStart) > 0) )
		{
			throw new GlycoconjugateException("For open chain both ring positions must be 0.");			
		}	
		if ( a_iStart > a_iEnd )
		{
			throw new GlycoconjugateException("Endpoint must be larger than startpoint");
		}
		if ( a_iStart < Monosaccharide.UNKNOWN_RING )
		{
			throw new GlycoconjugateException("Startpoint must be equal or larger than -1");
		}
		this.m_iRingStart = a_iStart;
		if ( a_iEnd < Monosaccharide.UNKNOWN_RING )
		{
			throw new GlycoconjugateException("Endpoint must be equal or larger than -1");
		}
		this.m_iRingEnd = a_iEnd;
	}

	/**
	 * 
	 * @return Positive Startposition of the ring or -1 if not validated
	 */
	public int getRingStart() 
	{
		return this.m_iRingStart;
	}

	/**
	 * 
	 * @return Positive endposition of the ring or -1 if not validated
	 */
	public int getRingEnd() 
	{
		return this.m_iRingEnd;
	}

	/**
	 * 
	 * @see org.eurocarbdb.util.Visitable#accept(org.eurocarbdb.util.GlycoVisitor)
	 */
	public void accept(GlycoVisitor a_objVisitor) throws GlycoVisitorException 
	{
		a_objVisitor.visit(this);		
	}

	public void setModification(ArrayList<Modification> a_aModi) throws GlycoconjugateException
	{
		if ( a_aModi == null )
		{
			throw new GlycoconjugateException("null is not a valide set of modifications.");
		}
		this.m_aModifications.clear();
		for (Iterator<Modification> t_iterModi = a_aModi.iterator(); t_iterModi.hasNext();)
		{
			this.addModification(t_iterModi.next());            
		}
	}

	public ArrayList<Modification> getModification()
	{
		return this.m_aModifications;
	}

	public boolean addModification( Modification a_objModification )
	{
		if ( a_objModification == null )
		{
			return false;
		}
		if ( !this.m_aModifications.contains(a_objModification) )
		{
			return this.m_aModifications.add(a_objModification);
		}		
		return false;
	}

	public boolean removeModification( Modification a_objModification )
	{
		return this.m_aModifications.remove(a_objModification);
	}

	public void setBaseType(ArrayList<BaseType> a_aBastType) throws GlycoconjugateException
	{
		if ( a_aBastType == null )
		{
			throw new GlycoconjugateException("null is not a valide set of basetypes.");
		}
		this.m_aBaseType.clear();
		for (Iterator<BaseType> t_iterBase = a_aBastType.iterator(); t_iterBase.hasNext();)
		{
			this.addBaseType(t_iterBase.next());            
		}
		this.m_aBaseType = a_aBastType;
	}

	public ArrayList<BaseType> getBaseType()
	{
		return this.m_aBaseType;
	}

	public boolean addBaseType( BaseType a_objBaseType ) throws GlycoconjugateException
	{
		if ( a_objBaseType == null )
		{
			throw new GlycoconjugateException("Basetype can't be null");
		}
		return this.m_aBaseType.add(a_objBaseType);
	}

	public boolean removeBaseType( BaseType a_objBaseType)
	{
		return this.m_aBaseType.remove(a_objBaseType);
	}

	public String getGlycoCTName()
	{
		String anomer=null;
		String name=null;
		String basetypes="";
		String t_Modifications="";
		String ringstart=null;
		String ringend=null;


		anomer=this.getAnomer().getSymbol();


		for (Iterator<BaseType> iter = this.getBaseType().iterator(); iter.hasNext();) {
			BaseType element = iter.next();			
			basetypes= basetypes+"-"+element.getName();			
		}

		if (this.getRingStart()==-1){
			ringstart="x";
		}
		else {
			ringstart=String.valueOf(this.getRingStart());
		}

		if (this.getRingEnd()==-1){
			ringend="x";
		}
		else {
			ringend=String.valueOf(this.getRingEnd());
		}

		ComparatorModification cf = new ComparatorModification();
		Collections.sort( this.m_aModifications , cf );

		for (Iterator<Modification> iter = this.getModification().iterator(); iter.hasNext();) {
			Modification element = iter.next();
			if (element.hasPositionTwo()){
				t_Modifications=t_Modifications+
				"|"+
				element.getPositionOne()+
				","+
				element.getPositionTwo()+
				":"+element.getName();	
			}
			else {
				t_Modifications=t_Modifications+
				"|"+
				element.getPositionOne()+
				":"+element.getName();	
			}
		}
		//remove trailing ","
		if (t_Modifications!="") t_Modifications=t_Modifications.substring(1,t_Modifications.length());

		name=	anomer+
		basetypes +    			
		"-"+    			
		this.getSuperclass()+    			
		"-"+    			
		ringstart+
		":"+ 
		ringend;

		if (t_Modifications!=""){
			name+="|"+t_Modifications;
		}	

		return name;
	}

	/**	 
	 * Check if Modification exists in Monosaccharide
	 * @returns Boolean 
	 * @throws GlycoconjugateException 
	 * @see java.lang.Object#clone()
	 **/

	public boolean hasModification (Modification a_objModification)
	{
		if (this.m_aModifications.contains(a_objModification)) 
		{			
			return true;
		}
		else 
		{
			return false;
		}
	}
	/**	 
	 * Check if Modification exists in Monosaccharide
	 * @returns Boolean 
	 * @throws GlycoconjugateException 
	 * @see java.lang.Object#clone()
	 **/
	public boolean hasModification(ModificationType a_objModiType, Integer positionOne)
	{
		for (Modification m: this.m_aModifications)
		{
			if (m.getName()==a_objModiType.getName() && m.getPositionOne()==positionOne	)
			{
				return true;
			}
		}		
		return false;
	}
	/**	 
	 * Check if Modification exists in Monosaccharide
	 * @returns Boolean 
	 * @throws GlycoconjugateException 
	 * @see java.lang.Object#clone()
	 **/
	public boolean hasModification(ModificationType a_objModiType, Integer positionOne, Integer positionTwo){

		for (Modification m: this.m_aModifications)
		{
			if (m.getName().equals(a_objModiType.getName()) && 
					m.getPositionOne()==positionOne	&&
					m.getPositionTwo()==positionTwo)
			{
				return true;
			}
		}		
		return false;
	}

	/**
	 * Create a clone of the Monosaccharide. Doesn't clone the linkages.
	 * @throws GlycoconjugateException 
	 * @see java.lang.Object#clone()
	 */
	public Monosaccharide copy() throws GlycoconjugateException
	{
		Monosaccharide t_objMS = null;
		// create new MS with Anomer and Superclass
		t_objMS = new Monosaccharide( this.m_enumAnomer , this.m_enumSuperclass );
		// ring
		t_objMS.setRing( this.m_iRingStart , this.m_iRingEnd );
		// basetype
		for (Iterator<BaseType> t_iterBase = this.m_aBaseType.iterator(); t_iterBase.hasNext();)
		{
			t_objMS.addBaseType(t_iterBase.next());
		}
		// modification
		for (Iterator<Modification> t_iterModi = this.m_aModifications.iterator(); t_iterModi.hasNext();)
		{
			t_objMS.addModification(t_iterModi.next().copy());
		}
		return t_objMS;
	}

	/**
	 * @param unknown_ring2
	 * @throws GlycoconjugateException 
	 */
	public void setRingStart(int a_iStart) throws GlycoconjugateException 
	{
		if ( a_iStart > this.m_iRingEnd )
		{
			throw new GlycoconjugateException("Endpoint must be larger than startpoint");
		}
		if ( a_iStart < Monosaccharide.UNKNOWN_RING )
		{
			throw new GlycoconjugateException("Startpoint must be equal or larger than -1");
		}
		this.m_iRingStart = a_iStart;
	}

	/**
	 * @param unknown_ring2
	 * @throws GlycoconjugateException 
	 */
	public void setRingEnd(int a_iEnd) throws GlycoconjugateException 
	{
		if ( this.m_iRingStart > a_iEnd )
		{
			throw new GlycoconjugateException("Endpoint must be larger than startpoint");
		}
		if ( a_iEnd < Monosaccharide.UNKNOWN_RING )
		{
			throw new GlycoconjugateException("Endpoint must be equal or larger than -1");
		}
		this.m_iRingEnd = a_iEnd;
	}

	public static Monosaccharide forGlycoCTName(String nameGlycoCT) throws GlycoconjugateException 
	{
		Monosaccharide t_monosaccharide = new Monosaccharide();
		String[] t_aPartsModi = nameGlycoCT.split("\\|");
		// 0 => ms ; 1 ... => modi
		String t_strMonosaccharide = t_aPartsModi[0];
		String[] t_aPartsMS = t_strMonosaccharide.split("\\-");
		// 0 => anomer n => Basetype n+1 => Superclass n+2 => ring 
		if ( t_aPartsMS.length < 3 )
		{
			throw new GlycoconjugateException("Unknown monosaccharide format: " + t_strMonosaccharide);
		}
		try
		{
			// anomer
			t_monosaccharide.setAnomer( Anomer.forSymbol(t_aPartsMS[0].charAt(0)) );
			// basetype 
			int t_iPosition = 1;
			ArrayList<BaseType> t_aBasetypes = new ArrayList<BaseType>();
			while ( t_aPartsMS[t_iPosition].length() == 4 )
			{
				t_aBasetypes.add(BaseType.forName(t_aPartsMS[t_iPosition++]));
			}
			t_monosaccharide.setBaseType( t_aBasetypes );
			// superclass
			Superclass t_superclass = Superclass.forName( t_aPartsMS[t_iPosition++] );
			if ( t_superclass == null )
			{
				throw new GlycoconjugateException("Unknown superclass name: " + t_strMonosaccharide);
			}
			t_monosaccharide.setSuperclass( t_superclass );
			// ring size
			String[] t_aPartsRing = t_aPartsMS[t_iPosition].split(":");
			if ( t_aPartsRing.length != 2 )
			{
				throw new GlycoconjugateException("Unknown ring size format: " + t_strMonosaccharide);
			}
			Integer t_iStart = null;
			Integer t_iEnd = null;
			if ( t_aPartsRing[1].equals("x") )
			{
				t_iEnd = Monosaccharide.UNKNOWN_RING;
			}
			else
			{
				t_iEnd = Integer.parseInt(t_aPartsRing[1]);
			}
			if ( t_aPartsRing[0].equals("x") )
			{
				t_iStart = Monosaccharide.UNKNOWN_RING ;
			}
			else
			{
				t_iStart = Integer.parseInt(t_aPartsRing[0]);
			}			
			t_monosaccharide.setRing(t_iStart, t_iEnd);
			// modification 3,4:en  4:d 
			ArrayList<Modification> t_aModifications = new ArrayList<Modification>();
			for (int i = 1; i < t_aPartsModi.length; i++) 
			{
				t_aModifications.add( Monosaccharide.parseModification(t_aPartsModi[i]));
			}
			t_monosaccharide.setModification( t_aModifications );
		} 
		catch (NumberFormatException e) 
		{
			throw new GlycoconjugateException(t_strMonosaccharide + ":" + e.getMessage(),e);
		}
		return t_monosaccharide;
	}

	private static Modification parseModification(String a_strModi) throws GlycoconjugateException  
	{
		String[] t_aParts = a_strModi.split(":");
		if ( t_aParts.length != 2 )
		{
			throw new GlycoconjugateException("Unknown modification format: " + a_strModi);
		}
		ModificationType t_objModi;
		t_objModi = ModificationType.forName(t_aParts[1]);
		String[] t_aPositions = t_aParts[0].split(",");
		int t_iPosition1 = 0;
		if ( t_aPositions[0].equals("x") )
		{
			t_iPosition1 = Modification.UNKNOWN_POSITION;
		}
		else
		{
			t_iPosition1 = Integer.parseInt(t_aPositions[0]);
		}
		if ( t_aPositions.length >2 )
		{
			throw new GlycoconjugateException("Unknown modification format: " + a_strModi);
		}
		else if ( t_aPositions.length == 2 )
		{
			int t_iPosition2 = 0;
			if ( t_aPositions[1].equals("x") )
			{
				t_iPosition2 = Modification.UNKNOWN_POSITION;
			}
			else
			{
				t_iPosition2 = Integer.parseInt(t_aPositions[1]);
			}
			return new Modification(t_objModi,t_iPosition1,t_iPosition2);
		}
		else
		{
			return new Modification(t_objModi,t_iPosition1);
		}
	}
}