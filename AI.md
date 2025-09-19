# AI.md

This file documents my use AI tools during the development of Logos, as part of the CS2103T individual project.

---

## 19 Sept 2025

### Increment: A-Personality (Revamping Chatbot UI Color Scheme)
- **AI Tool Used:** ChatGPT (OpenAI GPT-5)
- **Task:** Requested suggestions to refresh the CSS color scheme for our chatbot UI.  
  - Shared current `main.css` and `dialog-box.css` plus a screenshot of the app.  
  - Asked for a more modern, consistent, and accessible design direction.  
- **AI Output:**  
  - Proposed a tokenized color system (`-color-accent`, `-color-surface`, `-color-text`, etc.).  
  - Replaced high-saturation cyan/orange scheme with a calmer teal accent + dark glass surfaces.  
  - Improved readability, hover/pressed states, and semantic status colors.  
  - Suggested CSS replacements for `main.css` and `dialog-box.css` that I could copy and paste in directly.  

### Observations
- **What worked well:** The AI provided a consistent design system with reasoning (contrast, accessibility, maintainability). It also gave ready-to-use CSS code that mapped cleanly to our existing FXML structure.  
- **Time saved:** Significant — drafting a full palette and rewriting the CSS manually could have taken hours; with AI help, I got a solid proposal within minutes and only needed light adjustments.  

gotcha — here’s a short, paste-ready addendum you can drop into **AI.md**:

### AI-caused issues (and quick fixes)

* **Browser CSS leaked into JavaFX CSS.** AI suggested `var(...)`, `%` radii (`50%`), and `-fx-font` shorthands → JavaFX parser warnings and styling glitches.
  **Fix:** Replace with JavaFX-friendly syntax: inline pixel radii (`10px`, `16px`), use explicit font props (`-fx-font-family/size/weight`), and inline `-fx-effect: dropshadow(...)`.

**Outcome:** Warnings cleared, styles render as intended with explicit JavaFX-compatible values.
