package com.sportlink.sportlink.media.voucher;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VoucherMediaService {

    public static final String BASE_URL = "https://voucher.voucher.com/";
    public static final String PATH = "fdsfd";

    I_VoucherMediaRep voucherMediaRep;
    
    public VoucherMedia saveVoucherImage(Long voucherId, String img) {
        Optional<VoucherMedia> opt = voucherMediaRep.findById(voucherId);
        VoucherMedia voucherMedia;
        voucherMedia = opt.orElseGet(VoucherMedia::new);
        voucherMedia.setId(voucherId);
        voucherMedia.getImgNames().add(img);
        return voucherMediaRep.save(voucherMedia);
    }


    public List<String> getImages(Long voucherId) {
        Optional<VoucherMedia> opt = voucherMediaRep.findById(voucherId);
        if(opt.isEmpty()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        opt.get().getImgNames().stream().forEach(image -> {
            String url = BASE_URL + PATH + image;
            result.add(url);
        });
        return result;
    }

    public void deleteVoucherImage(Long voucherId, String imageUUID) {
        Optional<VoucherMedia> opt = voucherMediaRep.findById(voucherId);
        if(opt.isEmpty()) {
            return;
        }
        opt.get().getImgNames().remove(imageUUID);
        voucherMediaRep.save(opt.get());
    }
}
