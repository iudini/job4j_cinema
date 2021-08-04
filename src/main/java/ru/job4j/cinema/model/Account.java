package ru.job4j.cinema.model;

public class Account {
    private int id;
    private String username;
    private String email;
    private String phone;

    public static Account of(String username, String email, String phone) {
        Account account = new Account();
        account.username = username;
        account.email = email;
        account.phone = phone;
        return account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
