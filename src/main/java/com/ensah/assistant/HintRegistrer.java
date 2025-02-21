package com.ensah.assistant;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.Nullable;

public class HintRegistrer implements RuntimeHintsRegistrar{

    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        hints.resources().registerPattern("*.pdf");
        hints.resources().registerPattern("*.st");
        throw new UnsupportedOperationException("Unimplemented method 'registerHints'");
    }


}
