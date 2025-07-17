package dgb.Mp.Couriel;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.core.convert.converter.Converter;
import dgb.Mp.Couriel.enums.Nature;
import org.springframework.stereotype.Component;


public class NatureConverter implements Converter<String, Nature> {
    @Override
    public Nature convert(String s) {
        return switch (s.toLowerCase()) {
            case "Intern" -> Nature.Interne;
            case "Extern" -> Nature.Externe;
            default -> throw new IllegalArgumentException("Invalid Nature: " + s);
        };
    }

}
