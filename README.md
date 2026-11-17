# EcoCycle — SDG 12: Responsible Consumption & Production

**Module:** Object Oriented Programming (NCI Year 2)  
**UN SDG:** Goal 12 — Responsible Consumption and Production

## Team Members

- **Section A (Waste & Recycling Tracker):** Jamie Stackpoole
- **Section B (Carbon Footprint Logger):** Valentin Gabriel Luchian
- **Section C (Reuse Marketplace):** Peter Nolan

## Project Structure

```text
EcoCycle-Checkpoint2/
├── src/
│   ├── ecocycle/
│   │   ├── AppTheme.java
│   │   └── MainMenuGUI.java
│   ├── footprint/
│   │   └── FootprintMenuGUI.java
│   ├── marketplace/
│   │   └── MarketplaceMenuGUI.java
│   └── recycling/
│       └── RecycleMenuGUI.java
├── nbproject/              # NetBeans project configuration
├── build.xml               # Apache Ant build entry point
├── LICENSE
└── README.md
```

- **`ecocycle`**: Shared application theme and main menu.
- **`recycling`**: Household waste logging and recycling guidance (Section A).
- **`footprint`**: Transport and energy carbon footprint logger (Section B).
- **`marketplace`**: Reuse marketplace (Section C).

## How to Run

### Requirements

- JDK 11 or later.
- Apache Ant for command-line builds, or NetBeans for IDE builds.

### NetBeans

1. Open the project folder in NetBeans.
2. Select **Run Project** to build and launch the application.

### Command line

From the project root, run:

```sh
ant clean run
```

The project is configured to launch `ecocycle.EcoCycleApp`. That launcher is not yet present in `src/`, and several source files are currently empty, so the project will not build or launch until those classes are implemented.
