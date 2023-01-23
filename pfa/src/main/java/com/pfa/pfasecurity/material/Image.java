package com.pfa.pfasecurity.material;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "image")
public class Image {
    @Id
    @GeneratedValue
    private Integer id;
    private String image;


}