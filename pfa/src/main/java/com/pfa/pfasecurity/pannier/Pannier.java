package com.pfa.pfasecurity.pannier;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

import com.pfa.pfasecurity.user.User;
import com.pfa.pfasecurity.material.Material;

@Data
@Entity
@Table(name = "pannier")
public class Pannier {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "pannier_material", 
        joinColumns = @JoinColumn(name = "pannier_id"), 
        inverseJoinColumns = @JoinColumn(name = "material_id"))
    private List<Material> materials;
}
