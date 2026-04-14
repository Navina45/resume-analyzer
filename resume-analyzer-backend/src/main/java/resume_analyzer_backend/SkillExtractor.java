package resume_analyzer_backend;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SkillExtractor {

  private static final List<String> SKILL_DB = Arrays.asList(
    "java", "spring", "spring boot", "mysql", "html", "css", "javascript",
    "react", "angular", "python", "sql", "hibernate", "jdbc", "git",
    "rest api", "bootstrap", "node.js", "mongodb", "docker", "kubernetes",
    "aws", "azure", "jira", "postman", "microservices", "oauth", "jwt",
    "redis", "linux", "figma", "ui/ux", "testing", "manual testing",
    "automation testing", "selenium", "power bi", "excel"
);
    public List<String> extractSkills(String text) {
        String lowerText = text == null ? "" : text.toLowerCase();
        List<String> matchedSkills = new ArrayList<>();

        for (String skill : SKILL_DB) {
            if (lowerText.contains(skill.toLowerCase())) {
                matchedSkills.add(skill);
            }
        }

        return matchedSkills;
    }

    public List<String> getSkillDatabase() {
        return SKILL_DB;
    }
}