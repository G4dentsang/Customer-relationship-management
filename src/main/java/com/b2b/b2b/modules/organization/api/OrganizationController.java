package com.b2b.b2b.modules.organization.api;

import com.b2b.b2b.modules.auth.payload.AuthResult;
import com.b2b.b2b.modules.auth.service.AuthService;
import com.b2b.b2b.modules.organization.payload.RegisterOrganizationRequestDTO;
import com.b2b.b2b.modules.organization.service.OrgService;
import com.b2b.b2b.shared.response.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("app/v1/organization/")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrgService orgService;
    private final AuthService authService;

    @PostMapping("/register-organization")
    public ResponseEntity<APIResponse> registerOrganization(@Valid @RequestBody RegisterOrganizationRequestDTO request) {
        orgService.registerOrganizationAndAdmin(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new APIResponse("Organization created successfully with you as admin. Please verify email before login.",true));
    }

    @PostMapping("/switch-org/{targetOrgId}")
    public ResponseEntity<?> switchOrg(@PathVariable("targetOrgId") Integer targetOrgId) {
        AuthResult result = authService.switchOrganization(targetOrgId);

        ResponseEntity.BodyBuilder response = ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, result.accessTokenCookie().toString());

        if (result.refreshTokenCookie() != null) {
            response.header(HttpHeaders.SET_COOKIE, result.refreshTokenCookie().toString());
        }
        return response.body(result.responseDTO());
    }
}
