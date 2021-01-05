package com.example.projektet;

/**
 * Denna klass hanterar medlemsdata.
 * @author Andreas Roghe, Sofia Ågren
 * @version 2020-01-05
 */
class MemberData {

    private final String nickName;
    private final String email;
    private final String password;

    public MemberData(String name) {
        this(name, null, null);
    }

    public MemberData(String name, String email) {
        this(name, email, null);
    }

    public MemberData(String name, String email, String password) {
        this.nickName = name;
        this.email = email;
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}