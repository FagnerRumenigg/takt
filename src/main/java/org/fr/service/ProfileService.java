package org.fr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.fr.dto.ProfileRequest;
import org.fr.model.Profile;
import org.fr.model.User;
import org.fr.repository.ProfileRepository;
import org.fr.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public List<Profile> list() {
        log.info("Iniciando list");
        return profileRepository.findAll();
    }

    public Profile create(ProfileRequest request) {
        log.info("Iniciando create");
        return profileRepository.save(Profile.builder()
                .areaOfActuation(request.areaOfActuation())
                .role(request.role())
                .jobLevel(request.jobLevel())
                .build());
    }

    public Profile update(UUID id, ProfileRequest request) {
        log.info("Iniciando update");
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile não encontrado"));
        profile.setAreaOfActuation(request.areaOfActuation());
        profile.setRole(request.role());
        profile.setJobLevel(request.jobLevel());
        return profileRepository.save(profile);
    }

    public Profile getCurrentProfile(String username) {
        log.info("Iniciando getCurrentProfile");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        Profile profile = user.getProfile();
        if (profile == null) {
            log.info("Usuário sem profile vinculado");
            return null;
        }
        Hibernate.initialize(profile);
        return profile;
    }

    public Profile updateCurrentProfile(String username, ProfileRequest request) {
        log.info("Iniciando updateCurrentProfile");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        Profile profile = user.getProfile();
        if (profile == null) {
            log.info("Criando profile para usuário");
            profile = profileRepository.save(Profile.builder()
                    .areaOfActuation(request.areaOfActuation())
                    .role(request.role())
                    .jobLevel(request.jobLevel())
                    .build());
            user.setProfile(profile);
            userRepository.save(user);
            Hibernate.initialize(profile);
            return profile;
        }
        profile.setAreaOfActuation(request.areaOfActuation());
        profile.setRole(request.role());
        profile.setJobLevel(request.jobLevel());
        Profile savedProfile = profileRepository.save(profile);
        Hibernate.initialize(savedProfile);
        return savedProfile;
    }
}
