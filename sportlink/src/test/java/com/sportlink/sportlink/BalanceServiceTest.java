package com.sportlink.sportlink;

import com.sportlink.sportlink.balance.*;
import com.sportlink.sportlink.balance.Currency;
import com.sportlink.sportlink.utils.DTO_Adapter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BalanceServiceTest {

    @Mock
    private I_BalanceRepository balanceRepository;

    @Mock
    private DTO_Adapter dtoAdapter;

    @InjectMocks
    private BalanceService balanceService;

    private DTO_Balance balanceA;
    private DTO_Balance balanceB;

    // Define currencies based on the updated enum CURRENCY
    private DTO_Currency currencyA;
    private DTO_Currency currencyB;
    private DTO_Currency currencyC;
    private DTO_Currency currencyD;
    private DTO_Currency currencyE;
    private DTO_Currency currencyF;
    private DTO_Currency currencyG;
    private DTO_Currency currencyH;
    private DTO_Currency currencyI;

    private final Random random = new Random();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize currencies based on the CURRENCY enum
        currencyA = new DTO_Currency(CURRENCY.A, "pathA");
        currencyB = new DTO_Currency(CURRENCY.B, "pathB");
        currencyC = new DTO_Currency(CURRENCY.C, "pathC");
        currencyD = new DTO_Currency(CURRENCY.D, "pathD");
        currencyE = new DTO_Currency(CURRENCY.E, "pathE");
        currencyF = new DTO_Currency(CURRENCY.F, "pathF");
        currencyG = new DTO_Currency(CURRENCY.G, "pathG");
        currencyH = new DTO_Currency(CURRENCY.H, "pathH");
        currencyI = new DTO_Currency(CURRENCY.I, "pathI");

        Map<DTO_Currency, Integer> amountsA = new HashMap<>();
        Map<DTO_Currency, Integer> amountsB = new HashMap<>();

        balanceA = new DTO_Balance(1L, amountsA);
        balanceB = new DTO_Balance(2L, amountsB);
    }

    // Helper method to generate random balances with more currencies
    private DTO_Balance generateRandomBalance() {
        Map<DTO_Currency, Integer> amounts = new HashMap<>();
        amounts.put(currencyA, random.nextInt(200) - 100);
        amounts.put(currencyB, random.nextInt(200) - 100);
        amounts.put(currencyC, random.nextInt(200) - 100);
        amounts.put(currencyD, random.nextInt(200) - 100);
        amounts.put(currencyE, random.nextInt(200) - 100);
        amounts.put(currencyF, random.nextInt(200) - 100);
        amounts.put(currencyG, random.nextInt(200) - 100);
        amounts.put(currencyH, random.nextInt(200) - 100);
        amounts.put(currencyI, random.nextInt(200) - 100);
        return new DTO_Balance(1L, amounts);
    }

    private void executeAdjust(DTO_Currency curr, int sign){
        DTO_Balance result = balanceService.adjust(balanceA, balanceB, sign);
        int expected;
        int lhs = balanceA.getAmounts().getOrDefault(curr, 0);
        int rhs = sign * balanceB.getAmounts().getOrDefault(curr,0);

        if(lhs != 0 && rhs != 0) {
            expected = balanceA.getAmounts().getOrDefault(curr, 0) + sign * balanceB.getAmounts().getOrDefault(curr,0);
            if(expected == 0){
                assertTrue( ! result.getAmounts().containsKey(curr));
            }else{
                assertEquals(expected, result.getAmounts().get(curr));
            }
        }
        else if(lhs != 0){
            expected = lhs;
            assertEquals( expected, result.getAmounts().get(curr));
        } else if(rhs != 0){
            expected = sign * rhs;
            assertEquals( expected, result.getAmounts().get(curr));
        } else {}
    }

    @Test
    void testAdjustBalancesRandomValues() {

        balanceA = generateRandomBalance();
        balanceB = generateRandomBalance();

        for (int i = 0; i < 200; i++) {

            List<DTO_Currency> currencies = List.of(
                    currencyA, currencyB, currencyC, currencyD, currencyE, currencyF, currencyG, currencyH, currencyI);
            currencies.forEach(curr -> {
                executeAdjust(curr, +1);
                executeAdjust(curr, -1);
            });
        }
    }

    @Test
    void testShouldRemoveNullBalance(){
        int sign = 1;
        balanceA.getAmounts().clear();
        balanceB.getAmounts().clear();

        balanceA.getAmounts().put(currencyA, 100);
        balanceA.getAmounts().put(currencyB, 0);
        balanceA.getAmounts().put(currencyD, -10);

        balanceB.getAmounts().put(currencyA, 100);
        balanceB.getAmounts().put(currencyC, 50);
        balanceB.getAmounts().put(currencyD, 10);

        DTO_Balance result = balanceService.adjust(balanceA, balanceB, sign);

        assertTrue( ! result.getAmounts().containsKey(currencyB) );
        assertTrue( ! result.getAmounts().containsKey(currencyD) );
        assertEquals( 200, result.getAmounts().get(currencyA));
        assertEquals( 50, result.getAmounts().get(currencyC));
    }

    @Test
    void testingLimitValues() {
        int sign = 1;
        balanceA.getAmounts().clear();
        balanceB.getAmounts().clear();
        DTO_Balance result = balanceService.adjust(balanceA, balanceB, sign);
        assertTrue( result.getAmounts().isEmpty() );

        balanceA.getAmounts().put(currencyA, 0);
        result = balanceService.adjust(balanceA, balanceB, sign);
        assertTrue( result.getAmounts().isEmpty() );
        balanceA.getAmounts().clear();

        balanceB.getAmounts().put(currencyA, 0);
        result = balanceService.adjust(balanceA, balanceB, sign);
        assertTrue( result.getAmounts().isEmpty() );
        balanceB.getAmounts().clear();

        sign = -1;
        balanceA.getAmounts().put(currencyA, 10);
        balanceB.getAmounts().put(currencyA, 10);
        result = balanceService.adjust(balanceA, balanceB, sign);
        assertTrue( result.getAmounts().isEmpty() );
        balanceA.getAmounts().clear();
        balanceB.getAmounts().clear();

        balanceA.getAmounts().put(currencyC, 10);
        balanceB.getAmounts().put(currencyA, 11);
        balanceB.getAmounts().put(currencyB, 12);
        result = balanceService.adjust(balanceA, balanceB, sign);
        assertEquals( 10, result.getAmounts().get(currencyC));
        assertEquals( 11, result.getAmounts().get(currencyA));
        assertEquals( 12, result.getAmounts().get(currencyB));
        assertEquals( 10, balanceA.getAmounts().get(currencyC));
        assertEquals( 11, balanceB.getAmounts().get(currencyA));
        assertEquals( 12, balanceB.getAmounts().get(currencyB));
        assertEquals( 1,  balanceA.getAmounts().size());
        assertEquals( 2,  balanceB.getAmounts().size());
    }


    @Test
    void testHasPositiveBalance() {
        balanceA.getAmounts().put(currencyA, 100);
        balanceA.getAmounts().put(currencyB, 102330);
        balanceB.getAmounts().put(currencyB, 50);
        balanceB.getAmounts().put(currencyC, -3250);
        balanceB.getAmounts().put(currencyD, 50);
        assertTrue(balanceService.hasPositiveBalance(balanceA));
        assertFalse(balanceService.hasPositiveBalance(balanceB));
    }

    @Test
    void testGetCurrency_Success() {
        Currency currencyEntity = new Currency();
        when(balanceRepository.findCurrencyByName(CURRENCY.A)).thenReturn(Optional.of(currencyEntity));
        when(dtoAdapter.getDTO_Currency(currencyEntity)).thenReturn(currencyA);

        Optional<DTO_Currency> result = balanceService.getCurrency(CURRENCY.A);
        assertTrue(result.isPresent());
        assertEquals(CURRENCY.A, result.get().getCurrency());
    }

    @Test
    void testGetCurrency_NotFound() {
        when(balanceRepository.findCurrencyByName(CURRENCY.A)).thenReturn(Optional.empty());
        Optional<DTO_Currency> result = balanceService.getCurrency(CURRENCY.A);
        assertFalse(result.isPresent());
    }

    @Test
    void testSave_NewBalance() {
        Currency currencyEntity = new Currency();
        when(balanceRepository.findCurrencyByName(CURRENCY.A)).thenReturn(Optional.of(currencyEntity));
        when(balanceRepository.findBalanceById(null)).thenReturn(Optional.empty());
        when(balanceRepository.saveBalance(any())).thenReturn(new Balance(1L, new HashMap<>()))
        ;

        Balance result = balanceService.save(balanceA, null);
        assertNotNull(result);
    }
}
