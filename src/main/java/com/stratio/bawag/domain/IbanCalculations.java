package com.stratio.bawag.domain;

import java.math.BigInteger;
import java.util.regex.Pattern;

public class IbanCalculations {

    /**
     * IBAN pattern: <code>/[A-Z]{2}\d{2}[A-Z\d]{1,30}/</code>
     */
    private static final Pattern PATTERN =
            Pattern.compile("[A-Z]{2}\\d{2}[A-Za-z\\d]{1,30}");

    /**
     * Pattern of an Austrian IBAN: <code>/AT\d{18}/</code>
     */

    private static final Pattern AUSTRIAN_IBAN_PATTERN =
            Pattern.compile("AT\\d{18}");
    private static final BigInteger INT_97 = BigInteger.valueOf(97);
    private static final BigInteger INT_98 = BigInteger.valueOf(98);
    /**
     * Maps A-Z and a-z to two decimal digits. Used for IBAN check sum calculation. Indexed by char -
     * 'A' (for [A-Z])  or char - 'a' (for [a-z])
     */
    private static final String[] ALPHA_TO_DIGITS;

    static {
        final String[] a = new String[26];
        for (int i = 0; i < a.length; ++i) {
            a[i] = String.format("%02d", i + 10);
        }
        ALPHA_TO_DIGITS = a;
    }

    private static String removeWhitespace(String s) {
        return s == null ? null : s.replaceAll("\\s+", "");
    }

    /**
     * Extracts the country code from the given IBAN. Doesn't do a check sum verification on the given
     * IBAN.
     *
     * <p>Example:
     * <ul>
     * <li>IBAN input: <code>&quot;CH9300762011623852957&quot;</code>
     * <li>Result: <code>&quot;CH&quot;</code>
     * </ul>
     *
     * @param iban the IBAN string (will not be verified!)
     * @return the country code part of the IBAN or <code>null</code> if this is not possible
     * (<code>null</code> input, etc).
     */
    private static String extractCountryCode(String iban) {
        iban = toElectronicFormat(iban);
        if (iban == null) {
            return null;
        }
        return iban.substring(0, 2);
    }

    /**
     * Extracts the check digits from the given IBAN. Doesn't do a check sum verification on the given
     * IBAN.
     *
     * <p>Example:
     * <ul>
     * <li>IBAN input: <code>&quot;CH9300762011623852957&quot;</code>
     * <li>Result: <code>&quot;93&quot;</code>
     * </ul>
     *
     * @param iban the IBAN string (will not be verified!)
     * @return the check digits part of the IBAN or <code>null</code> if this is not possible
     * (<code>null</code> input, etc).
     */
    private static String extractCheckDigits(String iban) {
        iban = toElectronicFormat(iban);
        if (iban == null) {
            return null;
        }
        return iban.substring(2, 4);
    }

    /**
     * Extracts the BBAN (Basic Bank Account Number) from the given IBAN. This is the IBAN with the
     * country code and the check digits stripped off. Doesn't do a check sum verification on the
     * given IBAN.
     *
     * <p>Example:
     * <ul>
     * <li>IBAN input: <code>&quot;CH9300762011623852957&quot;</code>
     * <li>Result: <code>&quot;00762011623852957&quot;</code>
     * </ul>
     *
     * @param iban the IBAN string (will not be verified!)
     * @return the BBAN  part of the IBAN or <code>null</code> if this is not possible
     * (<code>null</code> input, etc).
     */
    private static String extractBBAN(String iban) {
        iban = toElectronicFormat(iban);
        if (iban == null) {
            return null;
        }
        return iban.substring(4);
    }

    /**
     * Extracts the bank code part from an Austrian IBAN. Doesn't do a check sum verification on the
     * given IBAN. If the given IBAN doesn't look like an Austrian IBAN, <code>null</code> will be
     * returned.
     *
     * <p>Example 1:
     * <ul>
     * <li>IBAN input: <code>&quot;AT74 1400 0001 1003 2528&quot;</code>
     * <li>Result: <code>&quot;14000&quot;</code>
     * </ul>
     *
     * <p>Example 2:
     * <ul>
     * <li>IBAN input: <code>&quot;DE86032500000546464646&quot;</code>
     * (German IBAN)
     * <li>Result: <code>null</code>
     * </ul>
     *
     * @param austrianIBAN the Austrian IBAN (will not be verified!)
     * @return the bank code part of the given Austrian IBAN or <code>null</code> if this is not
     * possible (<code>null</code> input, IBAN doesn't start with &quot;AT&quot; etc).
     */
    static String extractAustrianBankCode(String austrianIBAN) {
        austrianIBAN = toElectronicFormat(austrianIBAN);
        if (austrianIBAN == null
                || !AUSTRIAN_IBAN_PATTERN.matcher(austrianIBAN).matches()) {
            return null;
        }
        return austrianIBAN.substring(4, 9);
    }

    /**
     * Extracts the account number part from an Austrian IBAN. Doesn't do a check sum verification on
     * the given IBAN. If the given IBAN doesn't look like an Austrian IBAN, <code>null</code> will be
     * returned.
     *
     * <p>Example 1:
     * <ul>
     * <li>IBAN input: <code>&quot;AT74 1400 0001 1003 2528&quot;</code>
     * <li>Result: <code>&quot;00110032528&quot;</code>
     * </ul>
     *
     * <p>Example 2:
     * <ul>
     * <li>IBAN input: <code>&quot;DE86032500000546464646&quot;</code>
     * (German IBAN)
     * <li>Result: <code>null</code>
     * </ul>
     *
     * @param austrianIBAN the Austrian IBAN (will not be verified!)
     * @return the account number part of the given Austrian IBAN or <code>null</code> if this is not
     * possible (<code>null</code> input, IBAN doesn't start with &quot;AT&quot; etc).
     */
    public static String extractAustrianAccountNumber(String austrianIBAN) {
        austrianIBAN = toElectronicFormat(austrianIBAN);
        if (austrianIBAN == null
                || !AUSTRIAN_IBAN_PATTERN.matcher(austrianIBAN).matches()) {
            return null;
        }
        return austrianIBAN.substring(9);
    }

    private static int calculateCheckSum(String countryCode, String bban) {
        final String input = bban + countryCode + "00";
        final String digits = toDigits(input);
        final BigInteger i = new BigInteger(digits);
        final BigInteger mod97 = i.mod(INT_97);
        return INT_98.subtract(mod97).intValue();
    }

    private static String toDigits(CharSequence s) {
        final StringBuilder b = new StringBuilder();
        final int len = s.length();
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                b.append(c);
            } else if (c >= 'A' && c <= 'Z') {
                b.append(ALPHA_TO_DIGITS[c - 'A']);
            } else {
                assert c >= 'a' && c <= 'z';
                b.append(ALPHA_TO_DIGITS[c - 'a']);
            }
        }
        return b.toString();
    }

    /**
     * Checks if the given string forms a valid IBAN.
     *
     * @param iban the IBAN to check.
     * @return if the given IBAN is valid.
     */
    static boolean isValidIBAN(String iban) {
        if (iban == null) {
            return false;
        }
        iban = removeWhitespace(iban);
        if (!PATTERN.matcher(iban).matches()) {
            return false;
        }
        final String countryCode = extractCountryCode(iban);
        final String checkDigits = extractCheckDigits(iban);
        final String bban = extractBBAN(iban);
        final int checkSum = calculateCheckSum(countryCode, bban);
        return checkDigits.equals(String.format("%02d", checkSum));
    }

    /**
     * Checks if the given IBAN is a valid Austrian IBAN.
     *
     * @param iban the IBAN to check.
     * @return if the given IBAN is a valid Austrian IBAN.
     */
    public static boolean isValidAustrianIBAN(String iban) {
        if (!isValidIBAN(iban)) {
            return false;
        }
        iban = removeWhitespace(iban);
        return AUSTRIAN_IBAN_PATTERN.matcher(iban).matches();
    }

    /**
     * Checks if the given IBAN is an Austrian IBAN, but doesn't validate the check digits.
     *
     * @param iban the IBAN to check.
     * @return if the given IBAN looks like an Austrian IBAN.
     */
    public static boolean isAustrianIBAN(String iban) {
        if (iban == null) {
            return false;
        }
        iban = removeWhitespace(iban);
        return AUSTRIAN_IBAN_PATTERN.matcher(iban).matches();
    }

    /**
     * Converts the given IBAN to its electronic format (whitespaces removed).
     *
     * <p>Example:
     * <ul>
     * <li>Input: <code>&quot;GR16 0110 1250 0000 0001 2300 695&quot;</code>
     * <li>Result: <code>&quot;GR1601101250000000012300695&quot;</code>
     * </ul>
     *
     * @param iban the IBAN to convert.
     * @return the IBAN in the electronic format or <code>null</code> if the given input string
     * doesn't look like a valid IBAN.
     */
    public static String toElectronicFormat(String iban) {
        if (iban == null) {
            return null;
        }
        iban = removeWhitespace(iban);
        if (!PATTERN.matcher(iban).matches()) {
            return null;
        }
        return iban;
    }
}
