package dgb.Mp.Couriel;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.core.convert.converter.Converter;

import dgb.Mp.Couriel.enums.Couriel_Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

public class CourielTypeConverter implements Converter<String, Couriel_Type> {



    @Override
    public Couriel_Type convert(String s) {
        return switch (s.toLowerCase()) {
            case "Entrant" -> Couriel_Type.Arrivé;
            case "Sortant" -> Couriel_Type.Départ;
            default -> throw new IllegalArgumentException("Invalid Couriel_Type: " + s);
        };
    }


}
