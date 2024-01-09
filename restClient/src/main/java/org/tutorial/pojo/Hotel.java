package org.tutorial.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tb_hotel")
@Data
public class Hotel {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "price")
    private Integer price;

    @Column(name = "score")
    private Integer score;

    @Column(name = "brand")
    private String brand;

    @Column(name = "city")
    private String city;

    @Column(name = "star_name")
    private String starName;

    @Column(name = "business")
    private String business;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "pic")
    private String pic;

}
