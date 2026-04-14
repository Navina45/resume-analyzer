package resume_analyzer_backend;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SuggestionService {

    public List<String> generateSuggestions(List<String> missingSkills, int score) {
        List<String> suggestions = new ArrayList<>();

        if (score < 30) {
            suggestions.add("Your resume is currently weak for this role. Add more role-specific technical skills.");
            suggestions.add("Rewrite your resume summary to match the target job role more clearly.");
        } else if (score < 60) {
            suggestions.add("Your resume has partial match. Improve it by adding stronger technical keywords.");
            suggestions.add("Make your projects more role-focused and outcome-based.");
        } else {
            suggestions.add("Your resume shows a good match for this role.");
            suggestions.add("Improve impact by adding measurable achievements in projects or internship.");
        }

        if (missingSkills != null && !missingSkills.isEmpty()) {
            suggestions.add("Focus on learning these missing skills: " + String.join(", ", missingSkills));
        }

        if (score < 50) {
            suggestions.add("Add more backend/frontend/database related keywords based on the job description.");
        }

        suggestions.add("Use strong action words and clear project outcomes in your resume.");

        return suggestions;
    }
}