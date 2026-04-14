package resume_analyzer_backend;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResumeService {

    private final SkillExtractor skillExtractor;
    private final ResumeScorer resumeScorer;
    private final SuggestionService suggestionService;
    private final OpenRouterService openRouterService;

    public ResumeService(SkillExtractor skillExtractor,
                         ResumeScorer resumeScorer,
                         SuggestionService suggestionService,
                         OpenRouterService openRouterService) {
        this.skillExtractor = skillExtractor;
        this.resumeScorer = resumeScorer;
        this.suggestionService = suggestionService;
        this.openRouterService = openRouterService;
    }

    public ResumeAnalysisResponse analyzeResume(MultipartFile file, String fullName, String role, String jobDescription)
            throws IOException {

        String extractedText = PdfUtils.extractText(file);

        List<String> foundSkills = skillExtractor.extractSkills(extractedText);
        List<String> jdSkills = skillExtractor.extractSkills(jobDescription);

        List<String> matchedSkills = new ArrayList<>();
        for (String skill : foundSkills) {
            if (jdSkills.contains(skill)) {
                matchedSkills.add(skill);
            }
        }

        List<String> missingSkills = new ArrayList<>();
        for (String skill : jdSkills) {
            if (!foundSkills.contains(skill)) {
                missingSkills.add(skill);
            }
        }

        int matchScore = resumeScorer.calculateMatchScore(matchedSkills, jdSkills);
        int atsScore = resumeScorer.calculateAtsScore(foundSkills, extractedText);

        List<String> suggestions = suggestionService.generateSuggestions(missingSkills, matchScore);

        List<String> rejectionReasons = new ArrayList<>();
        if (!missingSkills.isEmpty()) {
            rejectionReasons.add("Some required skills from the job description are missing in the resume.");
        }
        if (matchScore < 50) {
            rejectionReasons.add("Resume match score is low for this target role.");
        }
        if (foundSkills.size() < 5) {
            rejectionReasons.add("Resume contains fewer strong technical keywords.");
        }
        if (rejectionReasons.isEmpty()) {
            rejectionReasons.add("No major rejection reasons found.");
        }
List<String> roadmap = new ArrayList<>();

if (!missingSkills.isEmpty()) {
    String skill1 = missingSkills.size() > 0 ? missingSkills.get(0) : "core concepts";
    String skill2 = missingSkills.size() > 1 ? missingSkills.get(1) : "practice";
    String skill3 = missingSkills.size() > 2 ? missingSkills.get(2) : "projects";
    String skill4 = missingSkills.size() > 3 ? missingSkills.get(3) : "revision";

    roadmap.add("Week 1 - Learn fundamentals of " + skill1 + " and understand the basics clearly.");
    roadmap.add("Week 2 - Practice " + skill2 + " with hands-on exercises and small coding tasks.");
    roadmap.add("Week 3 - Build a mini project using " + skill3 + " and improve implementation skills.");
    roadmap.add("Week 4 - Revise all learned topics, solve interview questions, and update your resume.");
} else {
    roadmap.add("Week 1 - Improve your resume summary and technical skills section.");
    roadmap.add("Week 2 - Strengthen your projects with better descriptions and outcomes.");
    roadmap.add("Week 3 - Practice role-based coding and interview questions.");
    roadmap.add("Week 4 - Revise your resume fully and prepare for applications.");
}

        String aiAnalysis = openRouterService.analyzeWithAI(extractedText, role, jobDescription);

        ResumeAnalysisResponse response = new ResumeAnalysisResponse();
        response.setFullName(fullName);
        response.setRole(role);
        response.setFileName(file.getOriginalFilename());
        response.setExtractedText(extractedText);
        response.setFoundSkills(foundSkills);
        response.setMatchedSkills(matchedSkills);
        response.setMissingSkills(missingSkills);
        response.setMatchScore(matchScore);
        response.setAtsScore(atsScore);
        response.setRejectionReasons(rejectionReasons);
        response.setSuggestions(suggestions);
        response.setRoadmap(roadmap);
        response.setAiAnalysis(aiAnalysis);

        return response;
    }
}