/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;

/**
 *
 * @author joe.butikofe
 */
@Named(value = "countryBean")
@ApplicationScoped
public class CountryBean  implements Serializable{
    
    private static Map<String, Integer> countries;
    private String country = "Switzerland";
    static{
        countries = new LinkedHashMap<String,Integer>();
        countries.put("Switzerland", 0);
        countries.put("USA", 1);
        countries.put("England", 2);
        countries.put("China", 3);   
    }
    
    public void countryChanged(ValueChangeEvent e)
    {
        country = e.getNewValue().toString();
    }
    
    public Map<String, Integer> getCountriesInMap()
    {
        return this.countries;
    }
    
    public String getCountry()
    {
        return country;
    }
    
    public void setCountry(String country)
    {
        this.country = country;
    }
    
}
