/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author joe.butikofe
 */
@Entity
@Table(name = "role_group")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RoleGroup.findAll", query = "SELECT r FROM RoleGroup r"),
    @NamedQuery(name = "RoleGroup.findById", query = "SELECT r FROM RoleGroup r WHERE r.id = :id"),
    @NamedQuery(name = "RoleGroup.findByGroupid", query = "SELECT r FROM RoleGroup r WHERE r.groupid = :groupid"),
    @NamedQuery(name = "RoleGroup.findByRoleid", query = "SELECT r FROM RoleGroup r WHERE r.roleid = :roleid")})
public class RoleGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "groupid")
    private int groupid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "roleid")
    private int roleid;
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Role role;
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Group1 group1;

    public RoleGroup() {
    }

    public RoleGroup(Integer id) {
        this.id = id;
    }

    public RoleGroup(Integer id, int groupid, int roleid) {
        this.id = id;
        this.groupid = groupid;
        this.roleid = roleid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public int getRoleid() {
        return roleid;
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Group1 getGroup1() {
        return group1;
    }

    public void setGroup1(Group1 group1) {
        this.group1 = group1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoleGroup)) {
            return false;
        }
        RoleGroup other = (RoleGroup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.RoleGroup[ id=" + id + " ]";
    }
    
}
