/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.faces.bean.ApplicationScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;

/**
 *
 * @author joe.butikofe
 */
@Named(value = "countryBean")
@ApplicationScoped
public class CountryBean  implements Serializable{
    
    private static Map<String, String> countries;
    private String country = "Thing 1";
    static{
        countries = new LinkedHashMap<String,String>();
        countries.put("First Thing", "Thing 1");
        countries.put("Second Thing", "Thing 2");
        countries.put("Third Thing", "Thing 3");
        countries.put("Fourth Thing", "Thing 4");   
    }
    
    public void countryChanged(ValueChangeEvent e)
    {
        country = e.getNewValue().toString();
    }
    
    public Map<String, String> getCountriesInMap()
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
