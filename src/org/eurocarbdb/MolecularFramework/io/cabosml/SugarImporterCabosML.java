package org.eurocarbdb.MolecularFramework.io.cabosml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.GlycoCTLinkageComparator;
import org.eurocarbdb.MolecularFramework.sugar.Anomer;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.LinkageType;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Substituent;
import org.eurocarbdb.MolecularFramework.sugar.SubstituentType;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.Superclass;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
/*
<jcggdb:Glyco>
	<jcggdb:Carb_ID>JCGG-STR000001</jcggdb:Carb_ID>
	<jcggdb:Carb_structure>
		<jcggdb:MS SUBCLASS="HEX" ct_name="x-dglc-HEX-x:x" name="Glc">
			<jcggdb:MOD ct_name="n-acetyl" name="NAc" node="15" pos2="1"/>
			<jcggdb:MS SUBCLASS="HEX" anom="b" clink1="1" ct_name="b-dglc-HEX-1:5" name="Glc" node="2" plink6="1"> 			</jcggdb:MS>
 		</jcggdb:MS>
 	</jcggdb:Carb_structure>
 </jcggdb:Glyco> 
 */
public class SugarImporterCabosML extends SugarImporter 
{
	private Document m_objDocument = null;
	private String m_strID = null;
//	private Monosaccharide m_ms = null;
//	private Substituent m_subst = null;
	private GlycoEdge m_edge = null;
	private HashMap<SubstituentType, LinkageType> m_hSubstPositionOne = new HashMap<SubstituentType, LinkageType>();
	private HashMap<SubstituentType, LinkageType> m_hSubstPositionTwo = new HashMap<SubstituentType, LinkageType>();	

	public SugarImporterCabosML()
	{
		super();
		this.m_hSubstPositionOne.put(SubstituentType.N_ACETYL, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.ETHYL, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.SULFATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.N_GLYCOLYL, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.AMINO, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.HYDROXYMETHYL, LinkageType.H_LOSE);
		this.m_hSubstPositionOne.put(SubstituentType.ACETYL, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.PHOSPHATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.N_SULFATE, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.ANHYDRO, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.N_FORMYL, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.PYRUVATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.FLOURO, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.CHLORO, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.N_AMIDINO, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.R_CARBOXYETHYL, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.DIPHOSPHO_ETHANOLAMINE, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.PHOSPHO_ETHANOLAMINE, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.ETHANOLAMINE, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.GLYCOLYL, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.THIO, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.S_PYRUVATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.N_METHYL, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.R_PYRUVATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.IODO, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.N_SUCCINATE, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.BROMO, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.N_DIMETHYL, LinkageType.DEOXY);
		this.m_hSubstPositionOne.put(SubstituentType.LACTONE, LinkageType.DEOXY);
//		this.m_hSubstPositionOne.put(SubstituentType.X_PYRUVATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.PYROPHOSPHATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.R_LACTATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.S_LACTATE, LinkageType.H_AT_OH);
        this.m_hSubstPositionOne.put(SubstituentType.X_LACTATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionOne.put(SubstituentType.TRIPHOSPHATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionTwo.put(SubstituentType.PHOSPHATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionTwo.put(SubstituentType.ANHYDRO, LinkageType.H_AT_OH);
		this.m_hSubstPositionTwo.put(SubstituentType.PYRUVATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionTwo.put(SubstituentType.S_PYRUVATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionTwo.put(SubstituentType.R_PYRUVATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionTwo.put(SubstituentType.LACTONE, LinkageType.H_AT_OH);
//		this.m_hSubstPositionTwo.put(SubstituentType.X_PYRUVATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionTwo.put(SubstituentType.AMINO, LinkageType.DEOXY);
		this.m_hSubstPositionTwo.put(SubstituentType.ETHANOLAMINE, LinkageType.H_AT_OH);
		this.m_hSubstPositionTwo.put(SubstituentType.PYROPHOSPHATE, LinkageType.H_AT_OH);
		this.m_hSubstPositionTwo.put(SubstituentType.TRIPHOSPHATE, LinkageType.H_AT_OH);
	}
	
	public String getID()
	{
		return this.m_strID;
	}

	@SuppressWarnings("unchecked")
	public Sugar parse(String a_strXML)  throws SugarImporterException
	{
		SAXBuilder builder = new SAXBuilder();
		this.m_objSugar = new Sugar();
		try 
		{
			this.m_objDocument = builder.build(new StringReader(a_strXML));
			if (builder.getValidation())
			{
				throw new SugarImporterException("XML Validation error");
			}
			// bis molecule gehen
			Element t_root = this.m_objDocument.getRootElement();
			if ( !t_root.getName().equals("Glyco") )
			{
				throw new SugarImporterException("Missing tag: Glyco.");
			}
			List<Element> t_lMainElements = this.m_objDocument.getRootElement().getChildren();
			for (Element t_element : t_lMainElements ) 
			{
				if ( t_element.getName().equals("Carb_ID") )
				{
					this.m_strID = t_element.getTextTrim();
				}
				else if ( t_element.getName().equals("Carb_structure") )
				{
					List<Element> t_aChilds = t_element.getChildren();
					if ( t_aChilds.size() != 1 ) 
					{
						throw new SugarImporterException("More than one sub-tag found in Carb_structure.");
					}
					for (Element element : t_aChilds) 
					{
						if ( !element.getName().equals("MS") )
						{
							throw new SugarImporterException("Subtag is not a MS tag: " + element.getName());
						}
						this.parseRoot(element);
					}
				}
				else if ( t_element.getName().equals("CategoryComposition") || t_element.getName().equals("Composition") )
				{
					// do nothing
				}
				else
				{
					throw new SugarImporterException("Found unknown tag: " + t_element.getName());
				}

			}            
			return this.m_objSugar;
		} 
		catch (JDOMException e) 
		{
			throw new SugarImporterException(e.getMessage(),e);
		}
		catch (IOException e) 
		{
			throw new SugarImporterException(e.getMessage(),e);
		} 
		catch (NumberFormatException e) 
		{
			throw new SugarImporterException(e.getMessage(),e);
		}
		catch (GlycoconjugateException e)
		{
			throw new SugarImporterException(e.getMessage(),e);
		}
	}

	@SuppressWarnings("unchecked")
	private void parseRoot(Element a_element) throws SugarImporterException, GlycoconjugateException 
	{		
		List<Attribute>  t_aAttributes = a_element.getAttributes();
		Monosaccharide t_ms = this.parserMSattributes(t_aAttributes,true);
		this.m_objSugar.addNode(t_ms);
		List<Element> t_aChilds = a_element.getChildren();
		for (Element element : t_aChilds) 
		{
			if ( element.getName().equals("MS") )
			{
				this.parserMS(element,t_ms);
			}
			else if ( element.getName().equals("MOD") )
			{
				this.parseSubst(element,t_ms);
			}
			else
			{
				throw new SugarImporterException("Found unknown tag in root MS: " + element.getName());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parserMS(Element a_element, Monosaccharide a_parent) throws GlycoconjugateException, SugarImporterException 
	{
		List<Attribute>  t_aAttributes = a_element.getAttributes();
		Monosaccharide t_ms = this.parserMSattributes(t_aAttributes,false);
		// order linkage
		ArrayList <Linkage> t_oLinkages = this.m_edge.getGlycosidicLinkages();
		GlycoCTLinkageComparator t_linComp = new GlycoCTLinkageComparator();
		Collections.sort(t_oLinkages,t_linComp);
		// add linkage types
		int t_iCount = 1;
		for (Linkage linkage : t_oLinkages) 
		{
			if ( t_iCount == 1 )
			{
				linkage.setParentLinkageType(LinkageType.H_AT_OH);
				linkage.setChildLinkageType(LinkageType.DEOXY);
			}
			else 
			{
				throw new SugarImporterException("MS with more then two parent linkages to other MS found : " + t_ms.getGlycoCTName());
			}
			t_iCount++;
		}
		this.m_objSugar.addNode(t_ms);
		this.m_objSugar.addEdge(a_parent, t_ms, this.m_edge);
		List<Element> t_aChilds = a_element.getChildren();
		for (Element element : t_aChilds) 
		{
			if ( element.getName().equals("MS") )
			{
				this.parserMS(element,t_ms);
			}
			else if ( element.getName().equals("MOD") )
			{
				this.parseSubst(element,t_ms);
			}
			else
			{
				throw new SugarImporterException("Found unknown tag in MS: " + element.getName());
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	private void parserMS(Element a_element, Substituent a_parent) throws GlycoconjugateException, SugarImporterException 
	{
		List<Attribute>  t_aAttributes = a_element.getAttributes();
		Monosaccharide t_ms = this.parserMSattributes(t_aAttributes,false);
		// order linkage
		ArrayList <Linkage> t_oLinkages = this.m_edge.getGlycosidicLinkages();
		GlycoCTLinkageComparator t_linComp = new GlycoCTLinkageComparator();
		Collections.sort(t_oLinkages,t_linComp);
		// add linkage types
		int t_iCount = 1;
		if ( a_parent.getParentEdge() != null )
		{
			t_iCount = a_parent.getParentEdge().getGlycosidicLinkages().size() + 1; 
		}
		for (Linkage linkage : t_oLinkages) 
		{
			if ( t_iCount == 1 )
			{
				linkage.setChildLinkageType(this.getLinkageTypeForSubst1(a_parent.getSubstituentType()));
				linkage.setParentLinkageType(LinkageType.NONMONOSACCHARID);
			}
			else if ( t_iCount == 2 )
			{
				linkage.setChildLinkageType(this.getLinkageTypeForSubst2(a_parent.getSubstituentType()));
				linkage.setParentLinkageType(LinkageType.NONMONOSACCHARID);
			}
			else
			{
				throw new SugarImporterException("MOD with more then two parent linkages found : " + a_parent.getSubstituentType());
			}
			t_iCount++;
		}
		this.m_objSugar.addNode(t_ms);
		this.m_objSugar.addEdge(a_parent, t_ms, this.m_edge);
		List<Element> t_aChilds = a_element.getChildren();
		for (Element element : t_aChilds) 
		{
			if ( element.getName().equals("MS") )
			{
				this.parserMS(element,t_ms);
			}
			else if ( element.getName().equals("MOD") )
			{
				this.parseSubst(element,t_ms);
			}
			else
			{
				throw new SugarImporterException("Found unknown tag in MS: " + element.getName());
			}
		}		
	}

	@SuppressWarnings("unchecked")
	private void parseSubst(Element a_element, Monosaccharide a_parent) throws SugarImporterException, GlycoconjugateException 
	{
		List<Attribute>  t_aAttributes = a_element.getAttributes();
		Substituent t_subst = this.parserSubstAttributes(t_aAttributes);
		// order Linkages
		ArrayList <Linkage> t_oLinkages = this.m_edge.getGlycosidicLinkages();
		GlycoCTLinkageComparator t_linComp = new GlycoCTLinkageComparator();
		Collections.sort(t_oLinkages,t_linComp);
		// add linkage types
		int t_iCount = 1;
		for (Linkage linkage : t_oLinkages) 
		{
			if ( t_iCount == 1 )
			{
				linkage.setParentLinkageType(this.getLinkageTypeForSubst1(t_subst.getSubstituentType()));
				linkage.setChildLinkageType(LinkageType.NONMONOSACCHARID);
			}
			else if ( t_iCount == 2 )
			{
				linkage.setParentLinkageType(this.getLinkageTypeForSubst2(t_subst.getSubstituentType()));
				linkage.setChildLinkageType(LinkageType.NONMONOSACCHARID);
			}
			else
			{
				throw new SugarImporterException("MOD with more then two parent linkages found : " + t_subst.getSubstituentType());
			}
			t_iCount++;
		}
		this.m_objSugar.addNode(t_subst);
		this.m_objSugar.addEdge(a_parent, t_subst, this.m_edge);
		List<Element> t_aChilds = a_element.getChildren();
		for (Element element : t_aChilds) 
		{
			if ( element.getName().equals("MS") )
			{
				this.parserMS(element,t_subst);
			}
			else
			{
				throw new SugarImporterException("Found unknown tag in MOD: " + element.getName());
			}
		}		
	}
	
	private Substituent parserSubstAttributes(List<Attribute> a_aAttributes) throws GlycoconjugateException, SugarImporterException 
	{
		String t_strNameGlycoCT = null;
		ArrayList<Integer> t_aParentPos = new ArrayList<Integer>();
		for (Attribute attribute : a_aAttributes) 
		{
			if ( attribute.getName().equals("ct_name") )
			{
				t_strNameGlycoCT = attribute.getValue();
			}
			else if ( attribute.getName().equals("name") || attribute.getName().equals("node") )
			{
				// ignore that
			}
			else if ( attribute.getName().startsWith("pos") )
			{
				String t_strValue = attribute.getName().replaceAll("pos", "").trim();
				if ( t_strValue.equals("X") )
				{
					t_aParentPos.add(Linkage.UNKNOWN_POSITION);	
				}
				else
				{
					t_aParentPos.add(Integer.parseInt(t_strValue));
				}
			}
			else
			{
				throw new SugarImporterException("Found unknown attribute in MOD: " + attribute.getName());
			}
		}	
		if ( t_strNameGlycoCT == null )
		{
			throw new SugarImporterException("Missing ct_name in MS.");
		}
		if ( t_aParentPos.size() == 0 )
		{
			throw new SugarImporterException("Missing child linkage position in MS: " + t_strNameGlycoCT );
		}
		this.m_edge = new GlycoEdge();
		Linkage t_linkage = new Linkage();
		t_linkage.addChildLinkage(1);
		t_linkage.setParentLinkages(t_aParentPos);
		t_linkage.setChildLinkageType(LinkageType.DEOXY);
		t_linkage.setParentLinkageType(LinkageType.H_AT_OH);
		this.m_edge.addGlycosidicLinkage(t_linkage);

		return new Substituent(SubstituentType.forName(t_strNameGlycoCT));
	}

	// <jcggdb:MS SUBCLASS="HEX" ct_name="x-dglc-HEX-x:x" name="Glc">
	private Monosaccharide parserMSattributes(List<Attribute> a_aAttributes,boolean a_bRoot) throws SugarImporterException, GlycoconjugateException 
	{
		Superclass t_super = null;
		String t_strNameGlycoCT = null;		
		ArrayList<Integer> t_aParentPos = new ArrayList<Integer>();
		ArrayList<Integer> t_aChildPos = new ArrayList<Integer>();
		Anomer t_anomer = null;
		for (Attribute attribute : a_aAttributes) 
		{
			if ( attribute.getName().equals("SUBCLASS") )
			{
				t_super = Superclass.forName(attribute.getValue());
			}
			else if ( attribute.getName().equals("ct_name") )
			{
				t_strNameGlycoCT = attribute.getValue();
			}
			else if ( attribute.getName().equals("name") || attribute.getName().equals("node") )
			{
				// ignore that
			}
			else if ( attribute.getName().startsWith("plink") )
			{
				String t_strValue = attribute.getName().replaceAll("plink", "").trim();
				if ( t_strValue.equals("X") )
				{
					t_aParentPos.add(Linkage.UNKNOWN_POSITION);
				}
				else
				{
					t_aParentPos.add(Integer.parseInt(t_strValue));	
				}				
			}
			else if ( attribute.getName().startsWith("clink") )
			{
				String t_strValue = attribute.getName().replaceAll("clink", "").trim();
				if ( t_strValue.equals("X") )
				{
					t_aChildPos.add(Linkage.UNKNOWN_POSITION);
				}
				else
				{
					t_aChildPos.add(Integer.parseInt(t_strValue));
				}
			}	
			else if ( attribute.getName().equals("anom") )
			{
				t_anomer = Anomer.forSymbol(attribute.getValue().toLowerCase());
			}
			else
			{
				throw new SugarImporterException("Found unknown attribute in MS: " + attribute.getName());
			}
		}		
		if ( t_strNameGlycoCT == null )
		{
			throw new SugarImporterException("Missing ct_name in MS.");
		}
		Monosaccharide t_ms = Monosaccharide.forGlycoCTName(t_strNameGlycoCT);
		if ( t_super != null )
		{
			if ( t_ms.getSuperclass() != t_super )
			{
				throw new SugarImporterException("Superclass does not match: " + t_strNameGlycoCT);
			}
		}
		if ( !a_bRoot )
		{
			if ( t_anomer == null )
			{
				throw new SugarImporterException("Missing anom in MS: " + t_strNameGlycoCT);
			}
			else
			{
				if ( t_ms.getAnomer() == Anomer.OpenChain )
				{
					if ( t_anomer != Anomer.Unknown )
					{
						throw new SugarImporterException("Wrong anom in MS: " + t_strNameGlycoCT);
					}
				}
				else
				{
					if ( t_anomer != t_ms.getAnomer() )
					{
						throw new SugarImporterException("Anomer does not match: " + t_strNameGlycoCT);
					}
				}
			}
			if ( t_aChildPos.size() == 0 )
			{
				throw new SugarImporterException("Missing child linkage position in MS: " + t_strNameGlycoCT );
			}
			if ( t_aParentPos.size() == 0 )
			{
				throw new SugarImporterException("Missing parent linkage position in MS: " + t_strNameGlycoCT );
			}
			this.m_edge = new GlycoEdge();
			Linkage t_linkage = new Linkage();
			t_linkage.setChildLinkages(t_aChildPos);
			t_linkage.setParentLinkages(t_aParentPos);
			this.m_edge.addGlycosidicLinkage(t_linkage);
		}
		return t_ms;
	}

	private LinkageType getLinkageTypeForSubst1(SubstituentType a_substType) throws SugarImporterException 
	{
		LinkageType t_result = this.m_hSubstPositionOne.get(a_substType);
		if ( t_result == null )
		{
			throw new SugarImporterException("Unable to set linkage type one for: " + a_substType );
		}
		return t_result;
	}

	private LinkageType getLinkageTypeForSubst2(SubstituentType a_substType) throws SugarImporterException
	{
		LinkageType t_result = this.m_hSubstPositionTwo.get(a_substType);
		if ( t_result == null )
		{
			throw new SugarImporterException("Unable to set linkage type two for: " + a_substType );
		}
		return t_result;
	}
}
