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
@Named(value = "cityBean")
@ApplicationScoped
public class CityBean  implements Serializable{
    
    private static Map<String, Integer> cities;
    private String city = "Berne";
    static{
        cities = new LinkedHashMap<String,Integer>();
        cities.put("Berne", 0);
        cities.put("New York", 1);
        cities.put("London", 2);
        cities.put("Beijing", 3);   
    }
    
    public void cityChanged(ValueChangeEvent e)
    {
        city = e.getNewValue().toString();
    }
    
    public Map<String, Integer> getCitiesInMap()
    {
        return this.cities;
    }
    
    public String getCity()
    {
        return city;
    }
    
    public void setCity(String city)
    {
        this.city = city;
    }
    
}
