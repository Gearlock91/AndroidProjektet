package com.example.projektet;

class MemberData {
    private String nickName;
    private String email;
    private String password;


    public MemberData(String name, String email, String password) {
        this.nickName = name;
        this.email = email;
        this.password = password;

    }

    // Add an empty constructor so we can later parse JSON into MemberData using Jackson
    public MemberData() {
    }

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword(){return password;}
}