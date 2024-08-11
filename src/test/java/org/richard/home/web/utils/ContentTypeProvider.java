package org.richard.home.web.utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Map;
import java.util.stream.Stream;

import static org.richard.home.web.WebConstants.HEADER_VALUE_APPLICATION_JSON;
import static org.richard.home.web.WebConstants.HEADER_VALUE_FORM_URL_ENCODED;

class ContentTypeProvider implements ArgumentsProvider {


    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Map.of(HEADER_VALUE_APPLICATION_JSON, new Object[]{HEADER_VALUE_APPLICATION_JSON, true},
                        HEADER_VALUE_FORM_URL_ENCODED, new Object[]{HEADER_VALUE_APPLICATION_JSON, false})
                .entrySet().stream()
                .map(Arguments::of);
    }
}
