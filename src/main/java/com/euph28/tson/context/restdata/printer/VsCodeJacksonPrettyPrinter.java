package com.euph28.tson.context.restdata.printer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;

/**
 * Custom Pretty Printer that matches VsCode formatting
 */
public class VsCodeJacksonPrettyPrinter extends DefaultPrettyPrinter {

    /* ============================== CONSTRUCTOR ============================== */

    public VsCodeJacksonPrettyPrinter() {
        super();

        // Customize: Spacing = 4 spaces
        DefaultPrettyPrinter.Indenter indentation = new DefaultIndenter("    ", DefaultIndenter.SYS_LF);
        this.indentArraysWith(indentation);
        this.indentObjectsWith(indentation);
    }

    /* ============================== OVERRIDES ============================== */

    @Override
    public DefaultPrettyPrinter createInstance() {
        if (getClass() != VsCodeJacksonPrettyPrinter.class) {
            throw new IllegalStateException("Failed `createInstance()`: " + getClass().getName()
                    + " does not override method; it has to");
        }
        return new VsCodeJacksonPrettyPrinter();
    }

    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(": ");
    }
}