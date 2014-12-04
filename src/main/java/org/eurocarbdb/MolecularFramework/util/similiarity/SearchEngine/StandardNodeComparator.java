package org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine;

import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.sugar.Anomer;
import org.eurocarbdb.MolecularFramework.sugar.BaseType;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Superclass;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorNodeType;

public class StandardNodeComparator implements NodeComparator {

	private GlycoVisitorNodeType m_visNodeType = new GlycoVisitorNodeType();

	public int compare(GlycoNode o1, GlycoNode o2) {

		SimpleGetNameVisitor v = new SimpleGetNameVisitor();

		try {	
			o1.accept(v);
			String r1Name=v.getName();
			v.clear();
			o2.accept(v);
			String r2Name=v.getName();
			v.clear();			

			if (m_visNodeType.isMonosaccharide(o1) &&
					m_visNodeType.isMonosaccharide(o2)){

				// equal names
				if (r2Name.equals(r1Name)){	
					return 0;
				}

				Monosaccharide m1 = (Monosaccharide) o1.copy();
				Monosaccharide m2 = (Monosaccharide) o2.copy();
				harmonizeMS(m1, m2);

				m1.accept(v);
				String m1name=v.getName();
				v.clear();
				m2.accept(v);
				String m2name=v.getName();
				v.clear();			

				if (m2name.equals(m1name)){				
					return 0;	
				}
			}

			else if (r2Name.equals(r1Name)){				
				return 0;				
			}


		} catch (GlycoVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GlycoconjugateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		return 1;
	}



	private void harmonizeMS(Monosaccharide m1, Monosaccharide m2) {
		//Anomer unc
		if (m1.getAnomer()==Anomer.Unknown ||
				m2.getAnomer()==Anomer.Unknown	){
			try {
				m1.setAnomer(Anomer.Unknown);
				m2.setAnomer(Anomer.Unknown);
			} catch (GlycoconjugateException e) {

				e.printStackTrace();
			}
		}

		// case XGro-DGal etc
		for (int i = 0; i < m1.getBaseType().size(); i++) {
			BaseType b1 = m1.getBaseType().get(i);

			if (b1.absoluteConfigurationUnknown()&&
					m2.getBaseType().size()>i	){

				BaseType b2 = m2.getBaseType().get(i);
				String s1 = b1.getName().subSequence(1,3).toString();
				String s2 = b2.getName().subSequence(1,3).toString();
				if (s1.equalsIgnoreCase(s2)){
					ArrayList <BaseType> a_basetypes = m2.getBaseType();
					a_basetypes.set(i, b1);
					try {
						m2.setBaseType(a_basetypes);
					} catch (GlycoconjugateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}		
		}
		for (int i = 0; i < m2.getBaseType().size(); i++) {
			BaseType b1 = m2.getBaseType().get(i);

			if (b1.absoluteConfigurationUnknown() &&
				m1.getBaseType().size()>i	){

				BaseType b2 = m1.getBaseType().get(i);
				String s1 = b1.getName().subSequence(1,3).toString();
				String s2 = b2.getName().subSequence(1,3).toString();
				if (s1.equalsIgnoreCase(s2)){
					ArrayList <BaseType> a_basetypes = m1.getBaseType();
					a_basetypes.set(i, b1);
					try {
						m1.setBaseType(a_basetypes);
					} catch (GlycoconjugateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}		
		}


		//Only superclass given	
		if (m1.getBaseType().size()==0 ||
				m2.getBaseType().size()==0){

			try {
				//empty array
				ArrayList <BaseType> BaseTypeTemp = new ArrayList<BaseType> ();
				m1.setBaseType(BaseTypeTemp);				
				m2.setBaseType(BaseTypeTemp);
			} catch (GlycoconjugateException e) {

				e.printStackTrace();
			}		
		}

		// SUG absolute superclass given
		if (m1.getSuperclass()==Superclass.SUG ||
				m1.getSuperclass()==Superclass.SUG){

			try {
				//empty array
				ArrayList <BaseType> BaseTypeTemp = new ArrayList<BaseType> ();
				m1.setBaseType(BaseTypeTemp);				
				m2.setBaseType(BaseTypeTemp);
				m1.setSuperclass(Superclass.SUG);
				m2.setSuperclass(Superclass.SUG);

			} catch (GlycoconjugateException e) {

				e.printStackTrace();
			}		
		}


		// Ringsize start unc
		if (m1.getRingStart()==Monosaccharide.UNKNOWN_RING ||
				m2.getRingStart()==Monosaccharide.UNKNOWN_RING	){
			try {
				m1.setRingStart(Monosaccharide.UNKNOWN_RING);
				m2.setRingStart(Monosaccharide.UNKNOWN_RING);
			} catch (GlycoconjugateException e) {

				e.printStackTrace();
			}
		}

		// Ringsize end unc
		if (m1.getRingEnd()==Monosaccharide.UNKNOWN_RING ||
				m2.getRingEnd()==Monosaccharide.UNKNOWN_RING	){
			try {
				m1.setRingEnd(Monosaccharide.UNKNOWN_RING);
				m2.setRingEnd(Monosaccharide.UNKNOWN_RING);
			} catch (GlycoconjugateException e) {

				e.printStackTrace();
			}
		}


		
	}
}




