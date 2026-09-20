package com.concerttracker.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class VenueCreateRequest {

    @NotBlank(message = "El nombre del venue es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
    private String city;

    @NotBlank(message = "El país es obligatorio")
    @Size(max = 100, message = "El país no puede superar los 100 caracteres")
    private String country;

    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String address;

    @Positive(message = "La capacidad debe ser un número positivo")
    private Integer capacity;

    public VenueCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
