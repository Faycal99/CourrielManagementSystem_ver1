package dgb.Mp.Utils;

import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

@Component
public class AlgerianMinistriesConverter implements Converter<String, AlgerianMinistries> {

    @Override
    public AlgerianMinistries convert(String source) {
        return Arrays.stream(AlgerianMinistries.values())
                .filter(m -> m.getDisplayName().equalsIgnoreCase(source.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid ministry: " + source));
    }
}