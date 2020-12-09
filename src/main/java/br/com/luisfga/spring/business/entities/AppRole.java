package br.com.luisfga.spring.business.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "app_role")
@NamedQueries({
        @NamedQuery(name = "AppRole.findRolesForNewUser", query = "SELECT ar FROM AppRole ar WHERE ar.roleName IN ('USER')"),
        @NamedQuery(name = "AppRole.findAllRoles", query = "SELECT ar FROM AppRole ar")
})
public class AppRole implements Serializable {
    
    @Id
    @Column(name = "role_name")
    private String roleName;
    
    @ManyToMany(mappedBy = "roles")
    private Set<AppUser> users;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<AppUser> getUsers() {
        return users;
    }

    public void setUsers(Set<AppUser> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return this.roleName;
    }
}