/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mas.common.util.helper;

import java.util.List;

/**
 *
 * @author winga_000
 */
public class PermissionHelper {
    private String name;
    private String url;
    private List<PermissionHelper> permissions;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<PermissionHelper> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionHelper> permissions) {
        this.permissions = permissions;
    }
    
    
}
