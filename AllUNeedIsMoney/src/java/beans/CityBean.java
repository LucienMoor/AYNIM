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
@Named(value = "cityBean")
@ApplicationScoped
public class CityBean  implements Serializable{
    
    private static Map<String, String> cities;
    private String city = "Thing 1";
    static{
        cities = new LinkedHashMap<String,String>();
        cities.put("First Thing", "Thing 1");
        cities.put("Second Thing", "Thing 2");
        cities.put("Third Thing", "Thing 3");
        cities.put("Fourth Thing", "Thing 4");   
    }
    
    public void cityChanged(ValueChangeEvent e)
    {
        city = e.getNewValue().toString();
    }
    
    public Map<String, String> getCitiesInMap()
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
