package com.sportlink.sportlink.media.voucher;

import com.sportlink.sportlink.voucher.Voucher;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class VoucherMedia {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ElementCollection
    private List<String> imgNames;

}
