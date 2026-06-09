package com.example.TusasProject.service;

import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.service.enums.ScenarioType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromptBuilderService {

    public String buildPrompt(ScenarioType scenarioType, Trend trend, List<Driver> drivers) {
        String driversText = drivers.stream()
                .map(Driver::getDriverName)
                .collect(Collectors.joining("; "));

        return String.format(getTemplate(scenarioType), trend.getTrendName(), driversText);
    }

    private String getTemplate(ScenarioType scenarioType) {
        return switch (scenarioType) {
            case GROWTH -> """
                    Write a forward-looking and coherent narrative scenario titled 'Growth' that envisions a future shaped by positive trends and impactful drivers.
                    Do not use bullet points or lists. Write in a clear and uninterrupted narrative form.

                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;

            case COLLAPSE -> """
                    Write a cautionary and realistic narrative scenario titled 'Collapse' that explores a future where key systems deteriorate due to critical challenges and risks.
                    Focus on negative consequences and potential failures. Do not use bullet points or lists.

                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;

            case DISCIPLINE -> """
                    Write a structured and stable narrative scenario titled 'Discipline' where the future is shaped by strong governance, regulation, and responsible decision-making.
                    Emphasize strategic planning, order, and resilience. Do not use bullet points or lists.

                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;

            case TRANSFORMATIVE -> """
                    Write a bold and visionary narrative scenario titled 'Transformative' that imagines a future defined by radical innovation and disruptive change.
                    Highlight how the following drivers contribute to dramatic transformation. Do not use bullet points or lists.

                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;
        };
    }
}