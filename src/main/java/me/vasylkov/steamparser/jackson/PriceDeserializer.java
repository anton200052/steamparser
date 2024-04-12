package me.vasylkov.steamparser.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class PriceDeserializer extends StdDeserializer<Double>
{

    public PriceDeserializer()
    {
        super(Double.class);
    }

    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        String priceStr = p.getText();
        if (priceStr == null || priceStr.isEmpty())
        {
            return null;
        }

        priceStr = priceStr.replaceAll("[^\\d,\\.]", "");
        priceStr = priceStr.replace(',', '.');

        try
        {
            return Double.parseDouble(priceStr);
        }
        catch (NumberFormatException e)
        {
            throw new IOException("Unable to parse price: " + priceStr, e);
        }
    }
}
