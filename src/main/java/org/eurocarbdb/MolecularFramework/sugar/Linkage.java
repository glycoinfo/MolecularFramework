package org.eurocarbdb.MolecularFramework.sugar;

import java.util.ArrayList;
import java.util.Iterator;

public class Linkage  
{    
    private ArrayList<Integer> m_aChildLinkage = new ArrayList<Integer>();
    private ArrayList<Integer> m_aParentLinkage  = new ArrayList<Integer>();
    
    private LinkageType m_enumParentType = LinkageType.UNVALIDATED;
    private LinkageType m_enumChildType = LinkageType.UNVALIDATED;
    
    public static final int UNKNOWN_POSITION   = -1;
    
    public Linkage()
    {
        this.clear();
    }
    
    public void clear() 
    {
        this.m_aChildLinkage.clear();
        this.m_aParentLinkage.clear();
    }
    
    public boolean addChildLinkage(int a_iPosition) 
    {        
        if ( !this.m_aChildLinkage.contains(a_iPosition) )
        {
            return this.m_aChildLinkage.add(a_iPosition);
        }
        return false;
    }
    
    public boolean addParentLinkage(int a_iPosition) 
    {
        if ( !this.m_aParentLinkage.contains(a_iPosition) )
        {
            return this.m_aParentLinkage.add(a_iPosition);
        }
        return false;
    }
    
    public ArrayList<Integer> getChildLinkages() 
    {
        return this.m_aChildLinkage ;
    }
    
    public void setChildLinkages(ArrayList<Integer> a_aLinkages) throws GlycoconjugateException 
    {
        if ( a_aLinkages == null )
        {
            throw new GlycoconjugateException("null is not a valide set of linkage positions.");
        }
        this.m_aChildLinkage.clear();
        for (Iterator<Integer> t_iterPosition = a_aLinkages.iterator(); t_iterPosition.hasNext();)
        {
            this.addChildLinkage(t_iterPosition.next());            
        }
    }
    
    public void setParentLinkages(ArrayList<Integer> a_aLinkages) throws GlycoconjugateException 
    {
        if ( a_aLinkages == null )
        {
            throw new GlycoconjugateException("null is not a valide set of linkage positions.");
        }
        this.m_aParentLinkage.clear();
        for (Iterator<Integer> t_iterPosition = a_aLinkages.iterator(); t_iterPosition.hasNext();)
        {
            this.addParentLinkage(t_iterPosition.next());            
        }
    }

    public ArrayList<Integer> getParentLinkages() 
    {
        return this.m_aParentLinkage;
    }

    public void setParentLinkageType( LinkageType a_enumType) throws GlycoconjugateException
    { 
        if ( a_enumType == null )
        {
            throw new GlycoconjugateException("null is not allowed as linkage type");
        }
        this.m_enumParentType = a_enumType;
    }
    
    public void setChildLinkageType( LinkageType a_enumType ) throws GlycoconjugateException
    {
        if ( a_enumType == null )
        {
            throw new GlycoconjugateException("null is not allowed as linkage type");
        }
        this.m_enumChildType = a_enumType;
    }
    
    public LinkageType getParentLinkageType()
    {
        return this.m_enumParentType;
    }
    
    public LinkageType getChildLinkageType()
    {
        return this.m_enumChildType;
    }
    
    public Linkage copy() throws GlycoconjugateException
    {
        Linkage t_objLinkage = new Linkage();
        // copy childlinkages
        for (Iterator<Integer> t_iterPositions = this.m_aChildLinkage.iterator(); t_iterPositions.hasNext();)
        {
            int t_iPosition = t_iterPositions.next();
            t_objLinkage.addChildLinkage(t_iPosition);
        }
        //copy parentlinkages
        for (Iterator<Integer> t_iterPositions = this.m_aParentLinkage.iterator(); t_iterPositions.hasNext();)
        {
            int t_iPosition = t_iterPositions.next();
            t_objLinkage.addParentLinkage(t_iPosition);
        }
        t_objLinkage.setParentLinkageType(this.m_enumParentType);
        t_objLinkage.setChildLinkageType(this.m_enumChildType);
        return t_objLinkage;
    }
}