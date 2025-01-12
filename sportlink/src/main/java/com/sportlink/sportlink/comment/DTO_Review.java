package com.sportlink.sportlink.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTO_Review {
    long reviewId;
    long locationId;
    String username;
    String content;
}
