/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import beans.*;
import java.util.Date;
import java.util.Enumeration;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author joe.butikofe
 */
@ManagedBean
@SessionScoped
public class UserController {
    
    /**
     * Creates a new instance of userBean
     */
    public UserController() {
        
    }

    /**
     * @return the nickname
     */
    public String newUser(HttpServletRequest request) {
    
    String nickname = request.getParameter("userForm:nickname");
    String email = request.getParameter("userForm:email");
    String birthday = request.getParameter("userForm:nickname");
    String country = request.getParameter("userForm:country");
    String city = request.getParameter("userForm:city");
    String picture = request.getParameter("userForm:picture");
    
    String password = null;
    
    int score = 0;
   
    return "/homePage";
    
    }
    
}
