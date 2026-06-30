<h1 align="center">Wodel4diac</h1>

<p align="center"><i>A domain-specific language for model mutation — compatible with <a href="https://eclipse.dev/4diac/">Eclipse 4diac</a></i></p>

<p align="center">
  <a href="https://github.com/gomezabajo/Wodel4diac/blob/main/LICENSE"><img src="https://img.shields.io/github/license/gomezabajo/Wodel4diac?style=for-the-badge" alt="License"></a>
  <img src="https://img.shields.io/badge/Java-89.5%25-blue?style=for-the-badge" alt="Java">
  <img src="https://img.shields.io/badge/platform-Eclipse-2c2255?style=for-the-badge" alt="Eclipse">
</p>

<p align="center">
  <a href="https://gomezabajo.github.io/Wodel/">Wodel</a> &middot;
  <a href="https://eclipse.dev/4diac/">Eclipse 4diac</a> &middot;
  <a href="https://github.com/gomezabajo/Wodel4diac/issues">Report a bug</a>
</p>

---

Wodel4diac extends **[Wodel](https://gomezabajo.github.io/Wodel/)** — a domain-independent,
language-independent tool for model mutation — so that it can mutate **IEC 61499** models
authored in **Eclipse 4diac**. This makes it possible to systematically generate variants of
function-block applications for purposes such as mutation testing, fault seeding, robustness
analysis, and the automated generation of learning exercises for industrial-automation
engineering.

## Table of contents

- [Background](#background)
- [What Wodel4diac adds](#what-wodel4diac-adds)
- [Requirements](#requirements)
- [Installation](#installation)
- [Quick start](#quick-start)
- [Project structure](#project-structure)
- [AI assistant (experimental)](#ai-assistant-experimental)
- [Citing](#citing)
- [Acknowledgements and third-party components](#acknowledgements-and-third-party-components)
- [License](#license)
- [Contact](#contact)

## Background

**Wodel** is a tool for mutating models conforming to arbitrary meta-models. A *Wodel program*
specifies, in a high-level and metamodel-agnostic syntax, a set of mutation operators (create,
remove, modify, retype, clone, select, …) to apply to a seed model. Wodel generates
syntactically and (optionally) semantically valid mutants, computes metrics, and post-processes
the results. It is built on the Eclipse Modeling Framework (EMF) and Xtext.

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
- **`wodel.ai.assistant`** — an experimental generative-AI assistant (see
  [below](#ai-assistant-experimental)).


## Requirements

- **Java 21**
- **Eclipse 4diac IDE 3.2.0** — the target platform whose models are mutated
- **Xtext 2.37** and **EMF** — installed automatically as plugin dependencies via the update sites
- Optional, for semantic validation / model finding: a constraint backend used by **eFinder**
  (e.g. USE)

## Installation

### Option A — Install into Eclipse via the update sites (recommended)

1. In Eclipse (with the 4diac IDE features installed), open **Help -> Install New Software...**
2. Add the update sites built from this repository:
   - https://gomezabajo.github.io/Wodel4diac/update-site
3. Select the **Wodel4diac** features and complete the wizard.
4. Restart Eclipse.

### Option B — Build from source

```bash
git clone https://github.com/gomezabajo/Wodel4diac.git
cd Wodel4diac
```

Then import the plugin projects into an Eclipse 4diac 3.2.0 workspace
(*File -> Import -> Existing Projects into Workspace*) and launch a runtime Eclipse instance
with 4diac 3.2.0 installed.

## Quick start

> [`wodel.examples`](https://github.com/gomezabajo/Wodel4diac/tree/main/wodel.examples) /
> [`wodel.project.examples`](https://github.com/gomezabajo/Wodel4diac/tree/main/wodel.project.examples),
> and adjust the syntax to the grammar in
> [`wodel.dsls.wodel`](https://github.com/gomezabajo/Wodel4diac/tree/main/wodel.dsls.wodel).

1. Create (or open) a Wodel project and add a `.wodel` program.
2. Point it at a seed 4diac model and the corresponding meta-model.
3. Run it to generate mutants; the 4diac post-processor writes them out as 4diac projects.

```wodel
// example.wodel — illustrative only
generate 10 mutants in "out/" from "seed.<4diac-extension>"
metamodel "<IEC-61499 / 4diac metamodel URI>"

with commands {
    // e.g. remove a connection between two function blocks
    cmd1 "remove connection" { remove one <Connection> where { ... } }
    // e.g. change the type of a function block instance
    cmd2 "retype function block" { retype one <FB> as <SiblingType> }
}
```

Generated mutants appear in the output folder and can be opened directly in the 4diac IDE.

## Project structure

This is a multi-bundle Eclipse project. The most relevant areas:

| Area | Modules |
|------|---------|
| **4diac integration** | `wodel.fordiac`, `wodel.postprocessor.fordiac` |
| **AI assistant** | `wodel.ai.assistant` |
| Wodel DSL & core | `wodel.core`, `wodel.dsls.wodel*`, `wodel.run`, `wodel.registry*`, `wodel.extension`, `wodel.utils` |
| Post-processing & output | `wodel.postprocessor*`, `wodel.json` |
| Validation & comparison | `wodel.syntactic.*`, `wodel.semantic.*`, `wodel.emf.validation`, `wodel.emf.comparison` |
| Metrics | `wodel.metrics.*`, `wodel.footprints` |
| Synthesis | `wodel.synthesizer`, `wodel.seed.synthesis` |
| ASPLE DSL | `wodel.asple`, `wodel.dsls.asple*` |
| Education layer | `wodel.wodeledu`, `wodeledu.*` (modeldraw, modeltext, mutatext, edutest) |
| Mutation testing | `wodel.wodeltest` |
| Bundled tooling | `anatlyzer.*` (ATL analysis), `efinder.*` (model finding), `tinytools.*` |
| Update sites | `wodel.updatesite`, `efinder.updatesite` |

## AI assistant (experimental)

The [`wodel.ai.assistant`](https://github.com/gomezabajo/Wodel4diac/tree/main/wodel.ai.assistant)
module integrates an experimental **generative-AI assistant** to help users author and refine
Wodel mutation programs. It is under active development and not yet considered stable.

## Citing

If you use Wodel4diac or Wodel in academic work, please cite the relevant Wodel publication and,
where applicable, the Wodel4diac work (WIP):

```bibtex
@inproceedings{GomezAbajo2016Wodel,
  author    = {G\'omez-Abajo, Pablo and Guerra, Esther and de Lara, Juan},
  title     = {Wodel: A Domain-Specific Language for Model Mutation},
  booktitle = {Proceedings of the 31st Annual ACM Symposium on Applied Computing (SAC)},
  pages     = {1968--1973},
  year      = {2016},
  doi       = {10.1145/2851613.2851751}
}

```

## Acknowledgements and third-party components

Wodel4diac builds upon and redistributes components from several projects, including:

- **[Wodel](https://github.com/gomezabajo/Wodel)** and the Wodel ecosystem (Wodel-Edu, Wodel-Test).
- **anATLyzer** — static analysis of ATL transformations (`anatlyzer.*`).
- **eFinder** — model finding / constraint solving (`efinder.*`).
- **tinytools** — supporting EMF utilities (`tinytools.*`).

Developed in the context of work at the Universidad Autónoma de Madrid.

## License

Distributed under the **Eclipse Public License 2.0 (EPL-2.0)**. See
[`LICENSE`](https://github.com/gomezabajo/Wodel4diac/blob/main/LICENSE) for details.
Redistributed third-party components remain under their respective licenses.

## Contact

Maintained by **Pablo Gómez-Abajo** — <Pablo.GomezA@uam.es> ·
[@gomezabajo](https://github.com/gomezabajo).
Issues and contributions are welcome via the
[issue tracker](https://github.com/gomezabajo/Wodel4diac/issues).
