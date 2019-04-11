package com.atguigu.gmall.search.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    /**
     *
     * "account_number": 44,
     "balance": 34487,
     "firstname": "Aurelia",
     "lastname": "Harding",
     "age": 37,
     "gender": "M",
     "address": "502 Baycliff Terrace",
     "employer": "Orbalix",
     "email": "aureliaharding@orbalix.com",
     "city": "Yardville",
     "state": "DE"
     */
    //es中不能够驼峰命名
    private  Long account_number;
    private  Long balance;
    private  String firstname;
    private  String lastname;
    private  Integer age;
    private  String gender;
    private  String address;
    private  String employer;
    private  String email;
    private  String city;
    private  String state;
}
