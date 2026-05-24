package com.example.AtivHub.AtivHub.service;

import com.example.AtivHub.AtivHub.domain.user.dto.RankingResponseDTO;
import com.example.AtivHub.AtivHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<RankingResponseDTO> getGlobalRanking() {
        // Limita a consulta aos top 10 resultados para evitar sobrecarga
        var topStudentsProjection = userRepository.findTopStudents(PageRequest.of(0, 10));

        List<RankingResponseDTO> ranking = new ArrayList<>();
        int position = 1;

        for (var projection : topStudentsProjection) {
            var user = projection.user();
            long submissionsCount = projection.submissionsCount();

            // Cálculo dinâmico do nível: (XP / 100) + 1
            int level = (user.getXp() / 100) + 1;

            RankingResponseDTO dto = new RankingResponseDTO(
                    position,
                    user.getName(),
                      user.getEmail(),
                      level,
                    user.getXp(),
                    submissionsCount
            );

            ranking.add(dto);
            position++;
        }

        return ranking;
    }
}

