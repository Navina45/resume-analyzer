package resume_analyzer_backend;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenRouterService {

    private final String apiKey = System.getenv("OPENROUTER_API_KEY");

    public String analyzeWithAI(String resumeText, String role, String jobDescription) {
        try {
            if (apiKey == null || apiKey.isBlank()) {
                return "OpenRouter API key not found.";
            }

            RestTemplate restTemplate = new RestTemplate();

            String prompt = """
                    You are a strict ATS reviewer and senior software engineer.

                    Analyze the resume based on the target role and job description.
                    Give a response with:
                    1. Professional Summary
                    2. Strengths
                    3. Weaknesses
                    4. Skill Gap Analysis
                    5. Final Hiring Verdict

                    Be specific. Do not give generic answers.

                    Target Role:
                    %s

                    Job Description:
                    %s

                    Resume Text:
                    %s
                    """.formatted(role, jobDescription, resumeText);

            Map<String, Object> message = Map.of(
                    "role", "user",
                    "content", prompt
            );

            Map<String, Object> body = Map.of(
                    "model", "openai/gpt-4o-mini",
                    "messages", List.of(message)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            headers.set("HTTP-Referer", "http://localhost:8082");
            headers.set("X-OpenRouter-Title", "Resume Analyzer");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://openrouter.ai/api/v1/chat/completions",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getBody() == null) {
                return "No AI response received.";
            }

            List choices = (List) response.getBody().get("choices");
            if (choices == null || choices.isEmpty()) {
                return "No AI response received.";
            }

            Map firstChoice = (Map) choices.get(0);
            Map messageMap = (Map) firstChoice.get("message");

            if (messageMap == null || messageMap.get("content") == null) {
                return "AI response content missing.";
            }

            return messageMap.get("content").toString();

        } catch (Exception e) {
            return "AI analysis failed: " + e.getMessage();
        }
    }
}