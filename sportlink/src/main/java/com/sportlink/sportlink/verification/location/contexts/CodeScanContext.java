package com.sportlink.sportlink.verification.location.contexts;

import lombok.Data;

@Data
public class CodeScanContext {
    long entityIdExpected;
    long entityIdScanned;
}
