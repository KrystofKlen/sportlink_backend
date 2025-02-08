package com.sportlink.sportlink.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocketPasswordRequest {
    Long locationId;
    String password;
}
