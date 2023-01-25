package com.pfa.pfasecurity.material;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import com.pfa.pfasecurity.reservation.Reservation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "material")
public class Material {
    @Id
    @GeneratedValue
    private Integer id;
    private String sku;
    private String titre;
    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "material_id",referencedColumnName = "id")
    private List<Image> images;
    private String departement;
    private boolean disponible;
    private String tags;
    //private Date dueDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "material")
    private List<Reservation> reservations;
    private int quantite;
    

}