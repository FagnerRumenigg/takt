package org.fr.service;

import org.fr.dto.ProfileRequest;
import org.fr.model.Profile;
import org.fr.model.User;
import org.fr.repository.ProfileRepository;
import org.fr.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ProfileService profileService;

    @Test
    void listShouldReturnAllProfiles() {
        when(profileRepository.findAll()).thenReturn(List.of(Profile.builder().role("Dev").build()));

        var result = profileService.list();

        assertThat(result).hasSize(1);
    }

    @Test
    void createShouldPersistProfile() {
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var created = profileService.create(new ProfileRequest("Area", "Role", "Junior"));

        assertThat(created.getRole()).isEqualTo("Role");
    }

    @Test
    void updateShouldThrowWhenMissingProfile() {
        when(profileRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.update(UUID.randomUUID(), new ProfileRequest("A", "R", "J")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getCurrentProfileShouldReturnLinkedProfile() {
        Profile profile = Profile.builder().role("Role").build();
        User user = User.builder().username("fagner").profile(profile).build();
        when(userRepository.findByUsername("fagner")).thenReturn(Optional.of(user));

        var result = profileService.getCurrentProfile("fagner");

        assertThat(result).isEqualTo(profile);
    }

    @Test
    void updateCurrentProfileShouldCreateWhenAbsent() {
        User user = User.builder().username("fagner").build();
        when(userRepository.findByUsername("fagner")).thenReturn(Optional.of(user));
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = profileService.updateCurrentProfile("fagner", new ProfileRequest("Area", "Role", "Junior"));

        assertThat(result.getRole()).isEqualTo("Role");
        verify(userRepository).save(user);
    }
}
