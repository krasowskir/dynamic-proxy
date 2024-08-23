package org.richard.home;

import org.richard.home.domain.Address;
import org.richard.home.domain.Country;

public class CsvEntryParser {

    static Address mapFromCsvToAddress(String[] line) {
        return new Address(line[5], line[3].concat(" ").concat(line[2]), line[6], Country.GERMANY);
    }
}
