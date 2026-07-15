package org.fr.controller;

import org.fr.dto.ProfileRequest;
import org.fr.dto.ProfileResponse;
import org.fr.model.Profile;
import org.fr.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    @Test
    void getShouldDelegateToService() {
        ProfileService profileService = mock(ProfileService.class);
        ProfileController controller = new ProfileController(profileService);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");
        when(profileService.getCurrentProfile("fagner")).thenReturn(Profile.builder().role("Role").build());

        ResponseEntity<ProfileResponse> response = controller.get(authentication);

        assertThat(response.getBody().role()).isEqualTo("Role");
    }

    @Test
    void getShouldReturnNoContentWhenProfileMissing() {
        ProfileService profileService = mock(ProfileService.class);
        ProfileController controller = new ProfileController(profileService);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");
        when(profileService.getCurrentProfile("fagner")).thenReturn(null);

        ResponseEntity<ProfileResponse> response = controller.get(authentication);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void updateShouldDelegateToService() {
        ProfileService profileService = mock(ProfileService.class);
        ProfileController controller = new ProfileController(profileService);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("fagner");
        when(profileService.updateCurrentProfile(eq("fagner"), any(ProfileRequest.class))).thenReturn(Profile.builder().role("Role").build());

        ResponseEntity<ProfileResponse> response = controller.update(authentication, new ProfileRequest("Area", "Role", "Junior"));

        assertThat(response.getBody().role()).isEqualTo("Role");
    }
}
