package com.scbx.bookstore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Books {

    private Long id;
    private String name;
    private String author;
    private Float price;
    private Boolean is_recommended;



}
