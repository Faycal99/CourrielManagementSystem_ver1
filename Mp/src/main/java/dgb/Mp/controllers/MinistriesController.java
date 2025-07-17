package dgb.Mp.controllers;


import dgb.Mp.Utils.AlgerianMinistries;
import dgb.Mp.Utils.EnumOption;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MinistriesController {

    @GetMapping("/ministries")
    public ResponseEntity<List<String>> getAllMinistries() {
        List<String> ministryLabels = Arrays.stream(AlgerianMinistries.values())
                .map(AlgerianMinistries::getDisplayName)
                .toList();

        return ResponseEntity.ok(ministryLabels);
    }
}
