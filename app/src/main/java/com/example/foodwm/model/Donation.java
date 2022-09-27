package com.example.foodwm.model;



public class Donation
{
    String id,address,donarId, distributerId,city, vehicle, additionalContact, foodItems, status,map,time;
    double weight;
    Boolean  isHuman, isAnimal;


    public Donation()
    {

    }

    public Donation(String id,String address, String donarId, String distributerId, String city, String vehicle, String additionalContact, String foodItems, String status, String map, String time, double weight, Boolean isHuman, Boolean isAnimal) {
        this.id=id;
        this.address = address;
        this.donarId = donarId;
        this.distributerId = distributerId;
        this.city = city;
        this.vehicle = vehicle;
        this.additionalContact = additionalContact;
        this.foodItems = foodItems;
        this.status = status;
        this.map = map;
        this.time = time;
        this.weight = weight;
        this.isHuman = isHuman;
        this.isAnimal = isAnimal;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDonarId() {
        return donarId;
    }

    public void setDonarId(String donarId) {
        this.donarId = donarId;
    }

    public String getDistributerId() {
        return distributerId;
    }

    public void setDistributerId(String distributerId) {
        this.distributerId = distributerId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getAdditionalContact() {
        return additionalContact;
    }

    public void setAdditionalContact(String additionalContact) {
        this.additionalContact = additionalContact;
    }

    public String getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(String foodItems) {
        this.foodItems = foodItems;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Boolean getHuman() {
        return isHuman;
    }

    public void setHuman(Boolean human) {
        isHuman = human;
    }

    public Boolean getAnimal() {
        return isAnimal;
    }

    public void setAnimal(Boolean animal) {
        isAnimal = animal;
    }
}
