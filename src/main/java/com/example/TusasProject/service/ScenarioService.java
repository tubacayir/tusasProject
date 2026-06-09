package com.example.TusasProject.service;

import com.example.TusasProject.entity.Comment;
import com.example.TusasProject.entity.Scenario;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.entity.User;
import com.example.TusasProject.repository.CommentRepository;
import com.example.TusasProject.repository.ScenarioRepository;
import com.example.TusasProject.repository.TrendRepository;
import com.example.TusasProject.repository.UserRepository;
import com.example.TusasProject.service.enums.ScenarioType;
import com.example.TusasProject.service.records.ScenarioGenerationResult;
import com.example.TusasProject.service.records.ScenarioViewResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScenarioService {

    private final TrendRepository trendRepository;
    private final ScenarioRepository scenarioRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ScenarioGenerationService scenarioGenerationService;

    public ScenarioGenerationResult generateAndSaveScenarios(Long trendId, String email) {
        Trend trend = trendRepository.getReferenceById(trendId);
        User user = userRepository.findByEmail(email).orElse(null);

        Map<String, String> generatedScenarios = scenarioGenerationService.generateScenarios(trend);

        scenarioRepository.findAllByTrendId(trendId)
                .forEach(scenario -> scenarioRepository.deleteById(scenario.getId()));

        generatedScenarios.forEach((type, text) ->
                saveScenarioEntity(text, trend, user, type)
        );

        return new ScenarioGenerationResult(trend, generatedScenarios);
    }

    public ScenarioViewResult getScenariosByTrend(Long trendId) {
        Trend trend = trendRepository.getReferenceById(trendId);

        Map<String, String> scenarios = scenarioRepository.findAllByTrendId(trendId)
                .stream()
                .collect(Collectors.toMap(
                        Scenario::getScenarioType,
                        Scenario::getScenarioText,
                        (oldValue, newValue) -> newValue,
                        LinkedHashMap::new
                ));

        if (scenarios.isEmpty()) {
            scenarios = createEmptyScenarioMap();
        }

        return new ScenarioViewResult(trend, scenarios);
    }

    public ScenarioViewResult saveScenario(Long trendId, String scenarioText, String type, String email) {

        Trend trend = trendRepository.getReferenceById(trendId);
        User user = userRepository.findByEmail(email).orElse(null);

        Scenario existingScenario = scenarioRepository.findByTrendIdAndScenarioType(trendId, type);

        if (existingScenario != null) {
            scenarioRepository.deleteById(existingScenario.getId());
        }

        saveScenarioEntity(scenarioText, trend, user, type);

        return getScenariosByTrend(trendId);
    }

    public void publishScenario(Long trendId, String type) {
        Scenario scenario = scenarioRepository.findByTrendIdAndScenarioType(trendId, type);

        if (scenario == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Scenario not found for this trend"
            );
        }

        scenario.setIsPublished(true);
        scenario.setUpdatedAt(LocalDateTime.now());

        scenarioRepository.save(scenario);
    }

    public List<Scenario> getPublishedScenarios() {
        return scenarioRepository.findByIsPublishedTrueOrderByUpdatedAtDesc();
    }

    public void addComment(Long scenarioId, String content, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Scenario not found"
                ));

        Comment comment = new Comment();
        comment.setText(content);
        comment.setUser(user);
        comment.setScenario(scenario);
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.save(comment);
    }

    private void saveScenarioEntity(String scenarioText, Trend trend, User user, String type) {
        Scenario scenario = new Scenario();
        scenario.setTrend(trend);
        scenario.setScenarioText(scenarioText);
        scenario.setUser(user);
        scenario.setIsPublished(false);
        scenario.setScenarioType(type);

        scenarioRepository.save(scenario);
    }

    private Map<String, String> createEmptyScenarioMap() {
        Map<String, String> scenarios = new LinkedHashMap<>();

        for (ScenarioType type : ScenarioType.values()) {
            scenarios.put(type.value(), "Scenario not generated yet!");
        }

        return scenarios;
    }
}