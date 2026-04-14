const API_BASE_URL = "http://localhost:8082";
document.addEventListener("DOMContentLoaded", function () {
  const resumeForm = document.getElementById("resumeForm");
  const resultPage = document.getElementById("resultPage");
  const resumeFileInput = document.getElementById("resumeFile");
  const fileNameText = document.getElementById("fileNameText");

  revealElements();

  if (resumeFileInput && fileNameText) {
    resumeFileInput.addEventListener("change", function () {
      if (resumeFileInput.files.length > 0) {
        fileNameText.textContent = resumeFileInput.files[0].name;
      } else {
        fileNameText.textContent = "No file selected";
      }
    });
  }

  if (resumeForm) {
    resumeForm.addEventListener("submit", async function (event) {
      event.preventDefault();

      const fullName = document.getElementById("fullName").value.trim();
      const role = document.getElementById("role").value.trim();
      const resumeFile = document.getElementById("resumeFile").files[0];
      const jobDescription = document.getElementById("jobDescription").value.trim();
      const errorMsg = document.getElementById("errorMsg");
      const analyzeBtn = document.querySelector(".analyze-btn");
      const btnText = document.querySelector(".btn-text");

      if (errorMsg) errorMsg.textContent = "";

      if (fullName === "") {
        errorMsg.textContent = "Please enter your name.";
        return;
      }

      if (role === "") {
        errorMsg.textContent = "Please select or type your target role.";
        return;
      }

      if (!resumeFile) {
        errorMsg.textContent = "Please upload your resume.";
        return;
      }

      if (jobDescription === "") {
        errorMsg.textContent = "Please paste the job description.";
        return;
      }

      const formData = new FormData();
      formData.append("fullName", fullName);
      formData.append("role", role);
      formData.append("jobDescription", jobDescription);
      formData.append("file", resumeFile);

      try {
        if (analyzeBtn) {
          analyzeBtn.disabled = true;
          analyzeBtn.classList.add("loading");
        }

        if (btnText) {
          btnText.textContent = "Analyzing...";
        }

        const response = await fetch(`${API_BASE_URL}/api/resume/analyze`, {
          method: "POST",
          body: formData
        });

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(errorText || `Server error: ${response.status}`);
        }

        const data = await response.json();

        const resultData = {
          fullName: data.fullName || fullName,
          role: data.role || role,
          fileName: data.fileName || resumeFile.name,
          matchScore: typeof data.matchScore === "number" ? data.matchScore : 0,
          atsScore: typeof data.atsScore === "number" ? data.atsScore : 0,
          foundSkills: Array.isArray(data.foundSkills) ? data.foundSkills : [],
          matchedSkills: Array.isArray(data.matchedSkills) ? data.matchedSkills : [],
          missingSkills: Array.isArray(data.missingSkills) ? data.missingSkills : [],
          rejectionReasons: Array.isArray(data.rejectionReasons) ? data.rejectionReasons : [],
          suggestions: Array.isArray(data.suggestions) ? data.suggestions : [],
          roadmap: Array.isArray(data.roadmap) ? data.roadmap : [],
          extractedText: data.extractedText || "",
          aiAnalysis: data.aiAnalysis || "No AI analysis available."
        };

        localStorage.setItem("resumeAnalysisData", JSON.stringify(resultData));
        window.location.href = "result.html";
      } catch (error) {
        console.error("Fetch error:", error);
        errorMsg.textContent = "Error: " + error.message;
      } finally {
        if (analyzeBtn) {
          analyzeBtn.disabled = false;
          analyzeBtn.classList.remove("loading");
        }

        if (btnText) {
          btnText.textContent = "Analyze Resume";
        }
      }
    });
  }

  if (resultPage) {
    const savedData = localStorage.getItem("resumeAnalysisData");

    if (!savedData) {
      alert("No analysis data found. Please submit the form first.");
      window.location.href = "index.html";
      return;
    }

    const data = JSON.parse(savedData);

    setText("candidateName", data.fullName);
    setText("targetRole", data.role);
    setText("uploadedFile", data.fileName);

    animateScore("matchScore", Number(data.matchScore) || 0);
    animateScore("atsScore", Number(data.atsScore) || 0);

    renderTags("foundSkills", data.foundSkills, "found");
    renderTags("matchedSkills", data.matchedSkills, "matched");
    renderTags("missingSkills", data.missingSkills, "missing");

    renderList("rejectionReasons", data.rejectionReasons);
    renderList("suggestions", data.suggestions);
    renderRoadmap("roadmapBox", data.roadmap);
    renderResumeText("resumeTextBox", data.extractedText);
    renderResumeText("aiAnalysisBox", data.aiAnalysis);

    if (typeof Chart !== "undefined") {
      createDonutChart(
        Array.isArray(data.matchedSkills) ? data.matchedSkills.length : 0,
        Array.isArray(data.missingSkills) ? data.missingSkills.length : 0
      );
    }
  }
});

function animateScore(id, value) {
  const el = document.getElementById(id);
  if (!el) return;

  let start = 0;
  const finalValue = Math.max(0, Math.min(100, Math.round(value)));
  const duration = 1200;
  const stepTime = Math.max(10, Math.floor(duration / (finalValue || 1)));

  const timer = setInterval(() => {
    start++;
    el.textContent = start + "%";

    if (start >= finalValue) {
      el.textContent = finalValue + "%";
      clearInterval(timer);
    }
  }, stepTime);
}

function setText(id, value) {
  const element = document.getElementById(id);
  if (element) element.textContent = value || "";
}

function renderTags(containerId, items, className) {
  const container = document.getElementById(containerId);
  if (!container) return;

  container.innerHTML = "";

  if (!Array.isArray(items) || items.length === 0) {
    container.innerHTML = "<p class='empty-text'>No data available</p>";
    return;
  }

  items.forEach(function (item) {
    const span = document.createElement("span");
    span.className = "tag " + className;
    span.textContent = item;
    container.appendChild(span);
  });
}

function renderList(containerId, items) {
  const container = document.getElementById(containerId);
  if (!container) return;

  container.innerHTML = "";

  if (!Array.isArray(items) || items.length === 0) {
    const li = document.createElement("li");
    li.textContent = "No data available";
    container.appendChild(li);
    return;
  }

  items.forEach(function (item) {
    const li = document.createElement("li");
    li.textContent = item;
    container.appendChild(li);
  });
}

function renderRoadmap(containerId, items) {
  const container = document.getElementById(containerId);
  if (!container) return;

  container.innerHTML = "";

  if (!Array.isArray(items) || items.length === 0) {
    const div = document.createElement("div");
    div.className = "roadmap-step";
    div.innerHTML = `
      <div class="roadmap-week">Week 1</div>
      <div class="roadmap-text">No roadmap available.</div>
    `;
    container.appendChild(div);
    return;
  }

  items.forEach(function (item, index) {
    const div = document.createElement("div");
    div.className = "roadmap-step";
    div.innerHTML = `
      <div class="roadmap-week">Week ${index + 1}</div>
      <div class="roadmap-text">${item}</div>
    `;
    container.appendChild(div);
  });
}

function renderResumeText(containerId, text) {
  const container = document.getElementById(containerId);
  if (!container) return;

  if (!text || text.trim() === "") {
    container.textContent = "No data available.";
    return;
  }

  container.textContent = text;
}

function createDonutChart(matchedCount, missingCount) {
  const canvas = document.getElementById("skillsDonutChart");
  if (!canvas) return;

  const existingChart = Chart.getChart(canvas);
  if (existingChart) existingChart.destroy();

  new Chart(canvas, {
    type: "doughnut",
    data: {
      labels: ["Matched Skills", "Missing Skills"],
      datasets: [
        {
          data: [matchedCount, missingCount],
          backgroundColor: [
            "rgba(34, 197, 94, 0.95)",
            "rgba(239, 68, 68, 0.95)"
          ],
          borderColor: [
            "rgba(255,255,255,0.12)",
            "rgba(255,255,255,0.12)"
          ],
          borderWidth: 2,
          hoverOffset: 10
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      cutout: "68%",
      plugins: {
        legend: {
          position: "bottom",
          labels: {
            color: "#e2e8f0",
            font: {
              size: 13,
              family: "Poppins"
            },
            padding: 20
          }
        }
      }
    }
  });
}

function revealElements() {
  const elements = document.querySelectorAll(".reveal");
  elements.forEach((element, index) => {
    setTimeout(() => {
      element.classList.add("active");
    }, 120 * (index + 1));
  });
}