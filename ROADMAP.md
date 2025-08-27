## **Phase 1 — Foundation**

* [x] Scaffold package with **react-native-builder-bob** (Fabric view).
* [ ] Define **FaceAuthView** JS wrapper (`onSuccess`, `onFailure` with `DirectEventHandler`).
* [ ] Setup **native view managers** (`FaceAuthViewManager`) for iOS + Android.
* [ ] Render a **basic camera preview** in the native view.

🎯 Goal: Display live camera feed inside `<FaceAuth />`.

---

## **Phase 2 — Face Detection**

* [ ] Integrate **Vision framework** (iOS) for detecting faces in real-time.
* [ ] Integrate **ML Kit / CameraX** (Android) for face detection.
* [ ] Send detection results to JS via `onSuccess`.
* [ ] Emit `onFailure` when no face is detected.

🎯 Goal: Detect face presence and stream events.

---

## **Phase 3 — Liveness Cues**

* [ ] Define supported cues: **blink, smile, head turn**.
* [ ] Implement **real-time tracking** of landmarks (eyes, mouth, head position).
* [ ] Validate cues against developer-specified challenges (`livenessCues` prop).
* [ ] Emit `onSuccess` only if cues are satisfied.

🎯 Goal: Prevent static photo attacks by requiring user actions.

---

## **Phase 4 — Anti-Spoof Detection**

* [ ] Add **texture checks** (real skin vs flat screen).
* [ ] Add **motion/depth analysis** (small parallax movements).
* [ ] Basic **anti-replay filter** (detect screen flicker / reflections).
* [ ] Optionally support **TensorFlow Lite model** for spoof detection.

🎯 Goal: Block attempts with photos or videos.

---

## **Phase 5 — Developer Experience**

* [ ] Create **typed API + props** (TypeScript).
* [ ] Improve **error handling** (standardized error codes).
* [ ] Write **docs & README** with usage examples.
* [ ] Add **example app flows** (login screen demo).
* [ ] CI/CD: Auto build, lint, release on **npm** + GitHub.

🎯 Goal: Easy adoption by other RN devs.

---

## **Phase 6 — Future Enhancements (v2+)**

* [ ] **Face recognition / matching** (compare against stored embeddings).
* [ ] Support for **multiple faces** (group auth, 2-person login).
* [ ] Configurable **performance modes** (low-power vs high-accuracy).
* [ ] Web (React) support via WebRTC + MediaPipe.

---

### 🚀 Priority Milestones

1. Camera preview working (Phase 1).
2. Face detection integrated (Phase 2).
3. Liveness cues functional (Phase 3).
