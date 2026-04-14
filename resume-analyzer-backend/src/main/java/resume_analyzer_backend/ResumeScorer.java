package resume_analyzer_backend;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeScorer {

    public int calculateMatchScore(List<String> matchedSkills, List<String> jdSkills) {
        if (jdSkills == null || jdSkills.isEmpty()) {
            return 0;
        }

        double percentage = ((double) matchedSkills.size() / jdSkills.size()) * 100;
        return (int) Math.round(percentage);
    }

    public int calculateAtsScore(List<String> foundSkills, String extractedText) {
        int score = 0;

        if (foundSkills != null) {
            score += Math.min(foundSkills.size() * 10, 60);
        }

        if (extractedText != null && !extractedText.trim().isEmpty()) {
            score += 20;
        }

        String lowerText = extractedText == null ? "" : extractedText.toLowerCase();

        if (lowerText.contains("project")) {
            score += 10;
        }

        if (lowerText.contains("skills")) {
            score += 10;
        }

        return Math.min(score, 100);
    }
}