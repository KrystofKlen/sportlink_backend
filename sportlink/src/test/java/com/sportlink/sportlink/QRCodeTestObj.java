package com.sportlink.sportlink;

import java.io.Serializable;
import java.util.Objects;

public class QRCodeTestObj implements Serializable {
    private String field1;
    private String field2;
    private Long expiration;
    private long number;

    public QRCodeTestObj(String field1, String field2, Long expiration, long number) {
        this.field1 = field1;
        this.field2 = field2;
        this.expiration = expiration;
        this.number = number;
    }

    // Getters and setters
    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }

    public Long getExpiration() {
        return expiration;
    }

    public long getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QRCodeTestObj that = (QRCodeTestObj) o;
        return number == that.number &&
                Objects.equals(field1, that.field1) &&
                Objects.equals(field2, that.field2) &&
                Objects.equals(expiration, that.expiration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field1, field2, expiration, number);
    }

    @Override
    public String toString() {
        return "ComplexData{" +
                "field1='" + field1 + '\'' +
                ", field2='" + field2 + '\'' +
                ", timestamp=" + expiration +
                ", number=" + number +
                '}';
    }
}
