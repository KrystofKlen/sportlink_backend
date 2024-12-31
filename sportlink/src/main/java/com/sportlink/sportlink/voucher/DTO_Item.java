package com.sportlink.sportlink.voucher;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DTO_Item {
    private Long id;
    private String name;
    private String description;
    private List<String> images;
}
