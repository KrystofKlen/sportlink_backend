package com.sportlink.sportlink.utils;

import com.sportlink.sportlink.account.account.Account;
import com.sportlink.sportlink.account.account.DTO_Account;
import com.sportlink.sportlink.account.company.CompanyAccount;
import com.sportlink.sportlink.account.company.DTO_CompanyAccount;
import com.sportlink.sportlink.account.company.DTO_CompanyAccountDetails;
import com.sportlink.sportlink.account.user.DTO_UserAccount;
import com.sportlink.sportlink.account.user.DTO_UserAccountDetails;
import com.sportlink.sportlink.account.user.UserAccount;
import com.sportlink.sportlink.comment.DTO_Review;
import com.sportlink.sportlink.comment.Review;
import com.sportlink.sportlink.consent.Consent;
import com.sportlink.sportlink.consent.DTO_Consent;
import com.sportlink.sportlink.currency.Currency;
import com.sportlink.sportlink.location.DTO_Location;
import com.sportlink.sportlink.location.Location;
import com.sportlink.sportlink.reward.DTO_Reward;
import com.sportlink.sportlink.reward.Reward;
import com.sportlink.sportlink.transfer.DTO_Transfer;
import com.sportlink.sportlink.transfer.Transfer;
import com.sportlink.sportlink.visit.DTO_Visit;
import com.sportlink.sportlink.visit.Visit;
import com.sportlink.sportlink.voucher.DTO_Voucher;
import com.sportlink.sportlink.voucher.Voucher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DTO_Adapter {

    public DTO_UserAccount getDTO_UserAccount(UserAccount user) {
        return new DTO_UserAccount(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getDateOfBirth(),
                user.getProfilePicUUID()
        );
    }

    public Map<String, Integer> getDTO_Balance(Map<Currency, Integer> balance) {
        Map<String, Integer> dto = new HashMap<>();

        balance.forEach((k, v) -> {
            dto.put(k.getName(), v);
        });
        return dto;
    }

    public DTO_CompanyAccount getDTO_CompanyAccount(CompanyAccount companyAccount) {
        return new DTO_CompanyAccount(
                companyAccount.getName(),
                companyAccount.getAddress(),
                companyAccount.getPhone(),
                companyAccount.getContactEmail(),
                companyAccount.getWebsiteUrl(),
                companyAccount.getProfilePicUUID()
        );
    }

    public DTO_Location getDTO_Location(Location location) {
        return new DTO_Location(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getDescription(),
                location.getActivities(),
                location.getLongitude(),
                location.getLatitude(),
                location.getVerificationStrategies(),
                location.getImagesUUID()
        );
    }

    public Location getLocationFromDTO(DTO_Location dtoLocation) {
        return new Location(
                dtoLocation.getId(),
                dtoLocation.getName(),
                dtoLocation.getAddress(),
                dtoLocation.getDescription(),
                dtoLocation.getActivities(),
                dtoLocation.getLongitude(),
                dtoLocation.getLatitude(),
                dtoLocation.getVerificationStrategies(),
                dtoLocation.getImagesUUIDs()
        );
    }

    public DTO_Voucher getDTO_Voucher(Voucher voucher) {
        return new DTO_Voucher(
                voucher.getId(),
                voucher.getTitle(),
                voucher.getDescription(),
                voucher.getCurrency().getName(),
                voucher.getPrice(),
                voucher.getExpirationDate(),
                voucher.getState(),
                "",
                voucher.getImagesUUID()
        );
    }

    public DTO_Visit getDTO_Visit(Visit visit) {
        return new DTO_Visit(
                visit.getVisitId(),
                getDTO_Location(visit.getLocation()),
                visit.getTimestampStart(),
                visit.getTimestampStop(),
                visit.getState()
        );
    }

    public DTO_Transfer getDTO_Transfer(Transfer transfer) {
        return new DTO_Transfer(
                transfer.getId(),
                getDTO_UserAccount(transfer.getUser()),
                transfer.getTimestamp(),
                transfer.getCurrency().getName(),
                transfer.getAmount()
        );
    }

    public DTO_Reward getDTO_Reward(Reward reward) {
        return new DTO_Reward(
                reward.getId(),
                reward.getCurrency().getName(),
                reward.getAmount(),
                reward.getRewardConditions(),
                reward.getTotalClaimsLimit(),
                reward.getTotalClaimsCount(),
                reward.getMonthClaimsLimit(),
                reward.getMonthClaimsCount(),
                reward.getIntervals(),
                reward.getMinMinuteSpend()
        );
    }

    public DTO_Review getDTO_Review(Review review) {
        return new DTO_Review(
                review.getId(),
                review.getLocation().getId(),
                review.getUserPosting().getUsername(),
                review.getContent()
        );
    }

    public DTO_Account getDTO_Account(Account account) {
        return new DTO_Account(
                account.getId(),
                account.getLoginEmail(),
                account.getUsername(),
                account.getPassword(),
                account.getRole(),
                account.getProfilePicUUID(),
                account.getStatus()
        );
    }

    public DTO_Consent getDTO_Consent(Consent consent) {
        return new DTO_Consent(
                consent.getAccount().getId(),
                consent.getAgreement().getAgreement(),
                consent.getConsentGivenAt(),
                consent.getConsentExpiredAt()
        );
    }

    public DTO_UserAccountDetails getDTO_UserAccountDetails(UserAccount userAccount) {
        return new DTO_UserAccountDetails(
                userAccount.getId(),
                userAccount.getLoginEmail(),
                userAccount.getUsername(),
                userAccount.getRole(),
                userAccount.getProfilePicUUID(),
                userAccount.getStatus(),
                userAccount.getFirstName(),
                userAccount.getLastName(),
                userAccount.getDateOfBirth()
        );
    }

    public DTO_CompanyAccountDetails getDTO_CompanyAccountDetails(CompanyAccount companyAccount) {
        return new DTO_CompanyAccountDetails(
                companyAccount.getId(),
                companyAccount.getLoginEmail(),
                companyAccount.getUsername(),
                companyAccount.getRole(),
                companyAccount.getProfilePicUUID(),
                companyAccount.getStatus(),
                companyAccount.getName(),
                companyAccount.getAddress(),
                companyAccount.getPhone(),
                companyAccount.getContactEmail(),
                companyAccount.getWebsiteUrl()
        );
    }
}
