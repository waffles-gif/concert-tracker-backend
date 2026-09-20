package com.concerttracker.backend.dto.response;

public class VenueResponse {

    private Long id;
    private String name;
    private String city;
    private String country;
    private String address;
    private Integer capacity;

    public VenueResponse() {
    }

    public VenueResponse(Long id, String name, String city, String country, String address, Integer capacity) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.address = address;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getAddress() {
        return address;
    }

    public Integer getCapacity() {
        return capacity;
    }
}
