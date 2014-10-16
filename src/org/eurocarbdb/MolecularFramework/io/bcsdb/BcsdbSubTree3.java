/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.bcsdb;

import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;

/**
 * @author rene
 *
 */
public class BcsdbSubTree3
{
    public String m_strResidue = null;
    public GlycoEdge m_objEdge = null;
    public ArrayList<BcsdbSubTree3> m_aSubresidue = new ArrayList<BcsdbSubTree3>();
    public GlycoNode m_objNode = null;
}
