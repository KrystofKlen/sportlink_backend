package com.sportlink.sportlink.utils;

import com.sportlink.sportlink.voucher.DTO_Item;
import com.sportlink.sportlink.voucher.Item;
import com.sportlink.sportlink.account.CompanyAccount;
import com.sportlink.sportlink.account.DTO_CompanyAccount;
import com.sportlink.sportlink.account.DTO_UserAccount;
import com.sportlink.sportlink.account.UserAccount;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.currency.DTO_Currency;
import com.sportlink.sportlink.currency.DTO_MultiCurrencyAmmount;
import com.sportlink.sportlink.currency.MultiCurrencyAmount;
import com.sportlink.sportlink.location.DTO_Location;
import com.sportlink.sportlink.location.Location;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DTO_Adapter {

    public DTO_UserAccount getDTO_UserAccount(UserAccount user) {
        return new DTO_UserAccount(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getDateOfBirth(),
                user.getProfilePic()
        );
    }

    public DTO_Currency getDTO_Currency(Currency currency) {
        return new DTO_Currency(
                currency.getName(),
                currency.getImage()
        );
    }

    public DTO_Item getDTO_Item(Item item) {
        return new DTO_Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getImages()
        );
    }

    public DTO_MultiCurrencyAmmount getDTO_MultiCurrencyAmmount(MultiCurrencyAmount multiCurrencyAmount) {
        HashMap<DTO_Currency, Integer> amounts = new HashMap<>();

        multiCurrencyAmount.getAmounts().forEach((currency, amount) -> {
            amounts.put(getDTO_Currency(currency), amount.intValue());
        });
        return new DTO_MultiCurrencyAmmount(amounts);
    }

    public DTO_CompanyAccount getDTO_CompanyAccount(CompanyAccount companyAccount) {
        return new DTO_CompanyAccount(
                companyAccount.getName(),
                companyAccount.getAddress(),
                companyAccount.getPhone(),
                companyAccount.getContactEmail(),
                companyAccount.getWebsiteUrl(),
                companyAccount.getImage()
        );
    }

    public DTO_Location getDTO_Location(Location location) {
        return new DTO_Location(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getDescription(),
                location.getImages()
        );
    }

}
