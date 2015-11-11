/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.util.helper;

/**
 *
 * @author winga_000
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapUtils {
  
  private MapUtils() {}
  
  public static List<Object> getKeysFromValue(Map<?, ?> hm, Object value){
    List<Object> list = new ArrayList<>();
    for(Object o :hm.keySet()){
        if(hm.get(o).equals(value)) {
            list.add(o);
        }
    }
    return list;
  }
}
