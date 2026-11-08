package com.nutriplan.backend.plan;

import com.nutriplan.backend.plan.dto.PlanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    /** The "Generate plan" button. */
    @PostMapping("/generate")
    public PlanResponse generate(@AuthenticationPrincipal Jwt jwt) {
        return planService.generate(jwt.getSubject());
    }

    /** This week's plan, or 204 No Content if none was generated yet. */
    @GetMapping("/current")
    public ResponseEntity<PlanResponse> current(@AuthenticationPrincipal Jwt jwt) {
        return planService.current(jwt.getSubject())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
