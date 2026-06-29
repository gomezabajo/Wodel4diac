# Wodel4diac

> A domain-specific language for model mutation — compatible with [Eclipse 4diac](https://eclipse.dev/4diac/).

[![License: EPL-2.0](https://img.shields.io/badge/License-EPL%202.0-red.svg)](https://www.eclipse.org/legal/epl-2.0/)

Wodel4diac extends **[Wodel](http://gomezabajo.github.io/Wodel/)**, a domain-independent and
language-independent tool for model mutation, so that it can mutate **IEC 61499** models
authored in **Eclipse 4diac**. This makes it possible to systematically generate variants of
function-block applications for purposes such as mutation testing, fault seeding, robustness
analysis, and the automatic generation of learning exercises for industrial-automation
engineering.

---

## Table of contents

- [Background](#background)
- [What Wodel4diac adds](#what-wodel4diac-adds)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick start](#quick-start)
- [Project structure](#project-structure)
- [AI assistant (experimental)](#ai-assistant-experimental)
- [Related work and citing](#related-work-and-citing)
- [Acknowledgements and third-party components](#acknowledgements-and-third-party-components)
- [License](#license)
- [Contact](#contact)

---

## Background

**Wodel** is a tool for mutating models conforming to arbitrary metamodels. A *Wodel program*
specifies, in a high-level and metamodel-agnostic syntax, a set of mutation operators
(create, remove, modify, retype, clone, select, …) to be applied to a seed model. Wodel takes
care of generating syntactically and (optionally) semantically valid mutants, computing metrics,
and post-processing the results. It is built on the Eclipse Modeling Framework (EMF) and Xtext.

**Eclipse 4diac** is an open-source framework for distributed industrial automation and control
based on the **IEC 61499** standard. It comprises the *4diac IDE* engineering environment and the
*FORTE* runtime, and models control logic as networks of function blocks.

Wodel4diac bridges the two: it lets you express mutation operators over 4diac / IEC 61499 models
and obtain a population of mutated function-block applications.

## What Wodel4diac adds

On top of the standard Wodel ecosystem, this repository contributes:

- **`wodel.fordiac`** — integration enabling Wodel to load, understand, and mutate 4diac /
  IEC 61499 models.
- **`wodel.postprocessor.fordiac`** — a post-processor that serialises generated mutants back
  into the 4diac project format so they can be opened in the 4diac IDE.
- **`wodel.ai.assistant`** — an experimental assistant layer (see
  [below](#ai-assistant-experimental)).

## Requirements

- **Java** `<21>`
- **Eclipse 4diac IDE** `<3.2.0>`
- **Xtext / EMF** `<2.37>` (installed automatically as plugin dependencies if building from the update sites)
- A constraint/model-finder backend for `efinder` (e.g. USE or an SMT/CSP solver), if you use
  semantic validation — `<details>`

## Installation

### Option A — Install into an existing Eclipse via the update site

1. In Eclipse, go to **Help → Install New Software…**
2. Add the update site:
   - `<URL or local path to wodel.updatesite / efinder.updatesite>`
3. Select the **Wodel** and **Wodel4diac** features, then complete the wizard and restart Eclipse.

### Option B — Build from source

```bash
git clone https://github.com/gomezabajo/Wodel4diac.git
cd Wodel4diac
```

Then either:

- **Import into Eclipse:** *File → Import → Existing Projects into Workspace*, select the cloned
  folder, and import all plugin projects; or

## Quick start

> **Note:** the snippet below is **illustrative**. Please replace it with a verified example
> from `wodel.examples` / `wodel.project.examples` and adjust the syntax to the current grammar
> in `wodel.dsls.wodel`.

1. Create (or open) a Wodel project and add a `.wodel` program.
2. Point it at a seed 4diac model and the corresponding metamodel.
3. Run it to generate mutants, which the 4diac post-processor writes out as 4diac projects.

```wodel
// example.wodel  —  illustrative only
generate 10 mutants in "out/" from "seed.<4diac-extension>"
metamodel "<IEC-61499 / 4diac metamodel URI>"

with commands {
    // e.g. remove a connection between two function blocks
    cmd1 "remove connection" {
        remove one <Connection>
    }
    // e.g. swap the type of a function block instance
    cmd2 "retype function block" {
        modify one <FB> ...
    }
}
```

Generated mutants appear in the output folder and can be opened directly in the 4diac IDE.

## Project structure

This is a multi-bundle Eclipse project. The most relevant modules:

| Area | Modules |
|------|---------|
| Wodel DSL & core | `wodel.dsls.wodel*`, `wodel.core`, `wodel.run`, `wodel.registry*` |
| 4diac integration | `wodel.fordiac`, `wodel.postprocessor.fordiac` |
| Post-processing & output | `wodel.postprocessor*`, `wodel.json` |
| Validation & comparison | `wodel.syntactic.*`, `wodel.semantic.*`, `wodel.emf.validation`, `wodel.emf.comparison` |
| Metrics | `wodel.metrics.*`, `wodel.footprints` |
| Synthesis | `wodel.synthesizer`, `wodel.seed.synthesis` |
| Education layer | `wodeledu.*` (modeldraw, modeltext, mutatext, edutest) |
| AI assistant | `wodel.ai.assistant` |
| Bundled tooling | `anatlyzer.*` (ATL analysis), `efinder.*` (model finding), `tinytools.*` |
| Update sites | `wodel.updatesite`, `efinder.updatesite` |


## AI assistant (experimental)

The `wodel.ai.assistant` module provides `<one-line description of what it does — e.g. natural-language
generation of Wodel mutation programs / suggestions of mutation operators>`.



## Acknowledgements and third-party components

Wodel4diac builds upon and redistributes components from several projects, including:

- **Wodel** and the Wodel ecosystem.
- **anATLyzer** — static analysis of ATL transformations.
- **eFinder** — model finding / constraint solving.
- **tinytools** — supporting EMF utilities.


Developed in the context of work at the Universidad Autónoma de Madrid and Johannes Kepler University of Linz.

## License

This project is licensed under the **Eclipse Public License 2.0 (EPL-2.0)**. See the
[`LICENSE`](LICENSE) file for details. Redistributed third-party components remain under their
respective licenses.

## Contact

Maintained by **Pablo Gómez-Abajo** — `<pablo.gomeza@uam.es / https://gomezabajo.github.io>`.
Issues and contributions are welcome via the
[issue tracker](https://github.com/gomezabajo/Wodel4diac/issues).
