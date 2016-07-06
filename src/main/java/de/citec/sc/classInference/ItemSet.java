/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.sc.classInference;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author sherzod
 */
public class ItemSet {
    private Set<String> domainClasses;
    private Set<String> rangeClasses;

    public ItemSet(Set<String> domainClasses, Set<String> rangeClasses) {
        this.domainClasses = new HashSet<>();
        for(String s : domainClasses) {
            this.domainClasses.add(s);
        }
        this.rangeClasses = new HashSet<>();
        for(String s : rangeClasses) {
            this.rangeClasses.add(s);
        }
    }

    public Set<String> getDomainClasses() {
        return domainClasses;
    }

    public void setDomainClasses(Set<String> domainClasses) {
        this.domainClasses = domainClasses;
    }

    public Set<String> getRangeClasses() {
        return rangeClasses;
    }

    public void setRangeClasses(Set<String> rangeClasses) {
        this.rangeClasses = rangeClasses;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.domainClasses);
        hash = 13 * hash + Objects.hashCode(this.rangeClasses);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemSet other = (ItemSet) obj;
        if (!Objects.equals(this.domainClasses, other.domainClasses)) {
            return false;
        }
        if (!Objects.equals(this.rangeClasses, other.rangeClasses)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "domainClasses= " + domainClasses + ", rangeClasses=" + rangeClasses;
    }
    
    
}
