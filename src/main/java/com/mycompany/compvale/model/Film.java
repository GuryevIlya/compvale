package com.mycompany.compvale.model;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author delet
 */
public class Film extends Cinema{
    private Set<String>  leadRoles = new HashSet<String>();
    private int duration;

    public void addLeadRole(String role){
        leadRoles.add(role);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
