package br.com.luisfga.spring.business.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "app_role")
@NamedQueries({
        @NamedQuery(name = "AppRole.findRolesForNewUser", query = "SELECT ar FROM AppRole ar WHERE ar.roleName IN ('Normal User')"),
        @NamedQuery(name = "AppRole.findAllRoles", query = "SELECT ar FROM AppRole ar")
})
public class AppRole implements Serializable {
    
    @Id
    @Column(name = "role_name")
    private String roleName;
    
    @ManyToMany(mappedBy = "roles")
    private List<AppUser> users;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<AppUser> getUsers() {
        return users;
    }

    public void setUsers(List<AppUser> users) {
        this.users = users;
    }

}