package com.example.examconsult;

import java.io.Serializable;

public class User implements Serializable {
    String login;
    String password;
    byte[] salt;
}
