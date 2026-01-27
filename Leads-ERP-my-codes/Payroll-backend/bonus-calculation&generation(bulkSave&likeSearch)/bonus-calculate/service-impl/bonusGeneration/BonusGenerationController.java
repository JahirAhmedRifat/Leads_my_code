package com.leads.microcube.process.bonusGeneration;

import com.leads.microcube.infrastructure.anotation.ApiController;
import com.leads.microcube.process.bonusGeneration.command.BonusGenerationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@ApiController
@RequestMapping("BonusProcessing")
public class BonusGenerationController {

    private final BonusGenerationServiceImpl bonusGenerationService;

    public BonusGenerationController(BonusGenerationServiceImpl bonusGenerationService) {
        this.bonusGenerationService = bonusGenerationService;
    }

    @PostMapping("/getBonusInfo")
    ResponseEntity<?> showBonusInformation(@RequestBody BonusGenerationRequest request) {
        return ResponseEntity.ok(bonusGenerationService.showBonusInformation(request));
    }

}
