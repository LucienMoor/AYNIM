/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lea.vonmural
 */
@ManagedBean
@RequestScoped
public class AuthenticationController implements Serializable{

    /**
     * Creates a new instance of AuthenticationController
     */
    public AuthenticationController() {
    }
    
    public String logout() {
        String page="/login.xhtml";
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
          request.logout();
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage("Logout failed!"));
            page="/login?logout=false&faces-redirect=true";
        }
        return page;
    }
    
}
