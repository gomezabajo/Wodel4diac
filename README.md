<p align="center">
  <a href="https://github.com/gomezabajo/Wodel4diac">
    <img src="https://gomezabajo.github.io/Wodel4diac/images/wodel4diac_w_red.png" width="210" height="120" alt="Wodel4diac logo" />
  </a>
</p>

<h1 align="center">Wodel4diac</h1>

<p align="center">
  <strong>Model mutation for IEC 61499 and Eclipse 4diac</strong><br />
  <em>October 2026 update — targeting Eclipse 4diac IDE 3.3.0</em>
</p>

<p align="center">
  <a href="https://github.com/gomezabajo/Wodel4diac/blob/main/LICENSE"><img src="https://img.shields.io/github/license/gomezabajo/Wodel4diac" alt="License" /></a>
  <img src="https://img.shields.io/badge/Java-21-blue" alt="Java 21" />
  <img src="https://img.shields.io/badge/Eclipse%204diac-3.3.0-2c2255" alt="Eclipse 4diac 3.3.0" />
  <img src="https://img.shields.io/badge/status-research%20prototype-orange" alt="Research prototype" />
</p>

<p align="center">
  <a href="https://gomezabajo.github.io/Wodel/">Wodel</a> &middot;
  <a href="https://eclipse.dev/4diac/">Eclipse 4diac</a> &middot;
  <a href="https://gomezabajo.github.io/Wodel4diac/">Project website</a> &middot;
  <a href="https://github.com/gomezabajo/Wodel4diac/issues">Report an issue</a>
</p>

---

**Wodel4diac** adapts [Wodel](https://github.com/gomezabajo/Wodel), a domain-independent model-mutation language and framework, to models used with [Eclipse 4diac](https://eclipse.dev/4diac/) and the **IEC 61499** industrial-automation ecosystem. Wodel's mutation operators let researchers systematically explore variants of model-based systems; Wodel4diac supplies the additional Eclipse 4diac integration and post-processing required by this application domain.

Wodel4diac is a **research prototype** intended to support work on mutation-based analysis, fault injection, testing, and exploration of IEC 61499 model variants. The availability of a mutation operator or generated variant does **not**, by itself, establish behavioral equivalence, runtime safety, deployability, or IEC 61499 conformance of a resulting application.

## Contents

- [Release overview](#release-overview)
- [Background](#background)
- [Capabilities and components](#capabilities-and-components)
- [Requirements and compatibility](#requirements-and-compatibility)
- [Installation](#installation)
- [Getting started](#getting-started)
- [Project structure](#project-structure)
- [Experimental AI assistant](#experimental-ai-assistant)
- [Wodel ecosystem and recent developments](#wodel-ecosystem-and-recent-developments)
- [Troubleshooting and build notes](#troubleshooting-and-build-notes)
- [Citing](#citing)
- [Acknowledgements](#acknowledgements)
- [License](#license)
- [Contact](#contact)

## Release overview

**Target platform:** Eclipse 4diac IDE **3.3.0** (updating the previous 3.2.0 target). This source snapshot includes a refreshed Wodel plug-in ecosystem and a PDE update-site definition for installation into an appropriately configured Eclipse environment.

Highlights of the supplied October 2026 source snapshot:

- **Eclipse 4diac integration:** Wodel mutation execution can use 4diac-related EMF models, supported by `wodel.run` conversion utilities and `wodel.postprocessor.fordiac`.
- **4diac post-processing:** a registered Wodel post-processor converts model results for the 4diac workflow; the current implementation also handles operational-semantics **`.opsem`** output in an Eclipse workspace.
- **Core Wodel workflow:** mutation programs, model mutation, generation of mutated model variants, registry information, validation, comparison, and extension points.
- **Broader Wodel ecosystem:** the source tree includes Wodel-Edu, Wodel-Test, synthesis, metrics, and supporting tools. These are not all 4diac-specific features.
- **Experimental generative-AI support:** `wodel.ai.assistant` provides AI-related tasks and chat/LLM integration code; it remains experimental.

> **Versioning note:** **3.3.0 is the supported 4diac target, not the OSGi version of every Wodel4diac feature.** Feature descriptors in the supplied source still use versions such as `1.0.0.qualifier`. Avoid treating those values as a Wodel4diac 3.3.0 feature release number unless you update the PDE metadata separately.

> **Release-note scope:** This overview describes capabilities visible in the supplied source snapshot. Without a complete version-control comparison against the July 2026 revision, it does not assert that every listed capability was first introduced after July.

## Background

### Wodel

[Wodel](https://gomezabajo.github.io/Wodel/) is an Eclipse/EMF/Xtext-based domain-specific language (DSL) for the specification and execution of model mutations. A Wodel program declares transformations such as object creation and removal, attribute changes, reference redirection, cloning, and retyping. Its extensibility mechanisms enable model validation, mutation analysis, and domain-specific post-processing.

### Eclipse 4diac and IEC 61499

[Eclipse 4diac](https://eclipse.dev/4diac/) is an open-source environment for distributed industrial automation following **IEC 61499**. Its ecosystem includes the **4diac IDE** engineering tools and the **4diac FORTE** execution environment. The [Eclipse 4diac 3.3 release](https://eclipse.dev/4diac/news/2026/2026/eclipse-4diac-3.3-released/) introduced changes to the IDE and runtime; these are capabilities of 4diac itself, not features implemented by Wodel4diac.

### Wodel4diac

Wodel4diac connects model-mutation specifications with the 4diac modeling workflow. The current adapter uses Eclipse resources and 4diac-related EMF packages, including operational-semantics resources. Actual input and output formats depend on the conversion path selected by the integration; Wodel's generic `.mutator` syntax is reusable, but domain-specific classes and references must match the relevant 4diac metamodel.

## Capabilities and components

| Component | Role |
| --- | --- |
| `wodel.fordiac` | PDE feature defining the 4diac-specific extension. |
| `wodel.postprocessor.fordiac` | Registers a Wodel post-processor and handles 4diac-related output, including `.opsem` resources. |
| `wodel.run` | Mutation-execution integration, including 4diac model conversion helpers. |
| `wodel.core`, `wodel.dsls.wodel*` | Wodel core, DSL, and editor/integration bundles. |
| `wodel.registry*`, `wodel.footprints` | Mutation registry and mutation information. |
| `wodel.syntactic.*`, `wodel.semantic.*` | Model validation and comparison extensions. |
| `wodel.metrics.*`, `wodel.seed.synthesis` | Metrics and seed-related utilities. |
| `wodel.ai.assistant` | Experimental LLM-backed assistant functionality. |
| `wodel.wodeledu`, `wodeledu.*` | Wodel-Edu and educational exercise-generation tools. |
| `wodel.wodeltest`, `wodeltest.*` | Wodel-Test and mutation-testing tooling. |

The 4diac-specific post-processor is registered through Wodel's `wodel.postprocessor.MutPostprocessor` extension point. It is currently workspace-oriented rather than a general-purpose, standalone command-line converter.

## Requirements and compatibility

| Dependency | Target or requirement |
| --- | --- |
| **Java** | **JDK 21** (the included relevant OSGi manifests declare `JavaSE-21`). |
| **Eclipse 4diac IDE** | **3.3.0** (target of this Wodel4diac update). |
| **EMF / Eclipse PDE / Xtext** | Compatible versions required by the plug-in manifests and the target platform. The included AI assistant declares Xtext requirements starting at 2.37; do not assume that the newer standalone Wodel update-site dependencies are identical to this 4diac-based distribution. |
| **4diac interpreter integration** | The post-processor requires `org.eclipse.fordiac.ide.fb.interpreter` with a bundle version in `[3.0.0,4.0.0)`. |
| **Optional validation tooling** | Some advanced semantic-validation/model-finding features use eFinder and an appropriate backend (for example, USE). |
| **Optional AI assistant** | An accessible supported LLM endpoint and user-provided API credentials, where relevant. |

**Compatibility statement:** The repository is aimed at Eclipse 4diac **3.3.0**. Other versions are not automatically supported merely because they satisfy some OSGi version ranges. Resolve dependencies and test the specific feature combination against the actual 4diac installation.

## Installation

### Option A — Install from the published Eclipse update site

When the **3.3.0-targeted update-site artifacts have been published**, open **Help → Install New Software…** in Eclipse 4diac IDE and add:

```text
https://gomezabajo.github.io/Wodel4diac/update-site/
```

Choose the required Wodel features, review the dependency-resolution details, complete installation, and restart the IDE. The update-site feature categories are named **Wodel**, **Wodel-Edu**, and **Wodel-Test**; the 4diac-specific feature is `wodel.fordiac` (label: **Generate mutants in 4diac format**).

> **Important:** Publishing this Git repository is not enough to update the installed plug-ins. The deployed update site must contain its exported feature and plug-in artifacts and the Eclipse repository metadata. The GitHub `raw.githubusercontent.com/.../wodel.updatesite` URL points at repository source files and should **not** be used as an Eclipse p2 update-site URL. Until the new artifacts have been deployed, the public update site may still contain an older release.

### Option B — Build or run from source

```bash
git clone https://github.com/gomezabajo/Wodel4diac.git
cd Wodel4diac
```

1. Install or configure **Eclipse 4diac IDE 3.3.0** with **JDK 21** and the PDE/modeling dependencies required by the projects.
2. Use **File → Import → Existing Projects into Workspace** to import the required Eclipse plug-in and feature projects. The repository contains many companion plug-ins; import the dependency closure needed for your chosen features.
3. Resolve the target-platform dependencies and any API Tools requirements before exporting PDE features.
4. Launch an Eclipse Application runtime configuration with the required Wodel and 4diac bundles enabled.
5. To distribute an update site, build/export the `wodel.updatesite` project and publish the resulting installation repository to the location advertised above.

This is a PDE/OSGi plug-in repository, not a standalone Maven or Gradle application with a single root build command.

## Getting started

1. Launch an Eclipse 4diac environment with the **Wodel core** and **4diac integration** enabled.
2. Prepare or open a supported 4diac model and make its EMF metamodel available to Wodel.
3. Create a Wodel `.mutator` program describing the changes to apply. Operators must refer to types/features in the selected model's metamodel.
4. Execute the Wodel mutation workflow and enable the registered **4diac post-processor** when 4diac output is required.
5. Inspect generated mutant resources in the configured output location, then open/validate supported results in 4diac before attempting any runtime use.

**Current implementation detail:** Some conversion utilities explicitly reference an Eclipse workspace project named `MutationTestingApp`, including `network_traces` for project input and `mutants` for output. Those paths are **implementation-specific**, not a guarantee that every arbitrary 4diac workspace project is automatically supported without configuration or adaptation.

### Example Wodel syntax

Abridged example for **finite automata**, taken from the existing Wodel examples (shown to illustrate the DSL, **not** as a ready-to-run IEC 61499 mutator):

```wodel
generate 10 mutants
in "data/out/"
from "data/model/"
metamodel "/[@**@]/data/model/DFAAutomaton.ecore"

with blocks {
  dtr "Delete transition" {
    remove one Transition
  }
  mfs "Set final state to non-final" {
    modify one State where {isFinal = true} with {reverse(isFinal)}
  }
  rts "Set transition's target to a new final state" {
    s = create State with {name = 'f', isFinal = true}
    modify target tar from one Transition to s
  }
}
```

For an IEC 61499 experiment, replace the automata metamodel, model locations, classes, attributes, and references with those accepted by the 4diac adapter and the selected metamodel. Consult the existing [`wodeltest.extension.examples/mutator`](https://github.com/gomezabajo/Wodel4diac/tree/main/wodeltest.extension.examples/mutator) examples for additional general Wodel programs.

## Project structure

This repository is a multi-project Eclipse workspace. Major areas include:

| Area | Modules |
| --- | --- |
| 4diac integration | `wodel.fordiac`, `wodel.postprocessor.fordiac`, 4diac helpers within `wodel.run` |
| Wodel DSL and runtime | `wodel.core`, `wodel.dsls.wodel*`, `wodel.run`, `wodel.models`, `wodel.utils`, `wodel.extension` |
| Mutation information | `wodel.registry*`, `wodel.footprints`, `wodel.metrics.*` |
| Model validation/comparison | `wodel.syntactic.*`, `wodel.semantic.*`, `wodel.emf.validation`, `wodel.emf.comparison` |
| Output and synthesis | `wodel.postprocessor*`, `wodel.json`, `wodel.synthesizer`, `wodel.seed.synthesis` |
| Experimental AI assistant | `wodel.ai.assistant` |
| Wodel-Edu | `wodel.wodeledu`, `wodeledu.*` |
| Wodel-Test | `wodel.wodeltest`, `wodeltest.*` |
| Other bundled tooling | `anatlyzer.*`, `efinder.*`, `tinytools.*` |
| PDE update sites | `wodel.updatesite`, `wodeledu.updatesite`, `wodeltest.updatesite`, `efinder.updatesite` |

Not every bundle in the source tree is necessarily selected for installation by a given feature or exported update site.

## Experimental AI assistant

The `wodel.ai.assistant` plug-in contains assistant/chat integration, Wodel-oriented AI tasks, and an LLM client. This source snapshot supports user-supplied OpenAI API credentials through the Wodel preferences or the `OPENAI_API_KEY` environment variable; the configured model and API must be compatible with the selected task.

**Status: experimental.** AI-supported model or mutator generation should be reviewed and validated before use. Availability of this bundle does not imply that every AI feature has been tested with Eclipse 4diac 3.3.0 or that every task is exposed through a stable UI. Never commit real API credentials to the repository.

## Wodel ecosystem and recent developments

Wodel4diac is part of a wider family of research tools:

- **[Wodel](https://github.com/gomezabajo/Wodel)** — the mutation DSL and engine. Upstream documentation describes the Wodel 2.3 line, including an updated Eclipse/Xtext toolchain, mutation-console improvements, and ongoing generative-AI work. **Do not infer** that the version identifiers or full dependency stack of the standalone Wodel distribution are identical to the 4diac-specific PDE feature set.
- **[Wodel-Edu](https://gomezabajo.github.io/Wodel/)** — model-mutation-based educational exercise generation, represented by the `wodel.wodeledu` and `wodeledu.*` projects in this source tree.
- **[Wodel-Test](https://github.com/gomezabajo/Wodel-Test)** — supporting language-independent/model-based mutation-testing workflows, represented by `wodel.wodeltest` and `wodeltest.*` projects.
- **WoMoT** — a **separate** research framework combining Wodel-backed mutations with multi-objective search. Its later work on deterministic NSGA-II persistence, resumable search, and verifiable run lineage belongs to **WoMoT**, and should not be interpreted as an integrated Wodel4diac plug-in or an automatically bundled feature of this release.

The 4diac 3.3.0 update therefore focuses on **keeping the Wodel4diac integration aligned with the newer 4diac target and included Wodel code**. Claims about specific changes *since July 2026* should ultimately be verified against the repository's version-control history or a dated changelog.

## Troubleshooting and build notes

### PDE export fails with `apitooling.apigeneration`

If exporting `wodel.updatesite` fails while executing a generated Ant build (for example, in `wodel.semantic.comparison.dfa`) with `failed to create task or type apitooling.apigeneration`, check that the **Eclipse PDE API Tools** component is installed and available to the PDE export process. Restart Eclipse, clean/rebuild the relevant projects, regenerate the PDE build scripts where appropriate, and retry the export.

### Resource paths unexpectedly resolve beneath `bin/`

Compiled plug-ins may run from `bin/` or other build-output directories. A code-source location is not necessarily the Eclipse project root. When resolving Wodel project resources such as `data/config/config.txt`, prefer a validated configuration path or the actual project descriptor over simply accepting the first ancestor with a `data` folder.

### Installed update site still shows an old release

Verify that the **exported feature/plug-in JARs and p2 metadata** have actually been deployed, refresh the Eclipse Available Software Sites cache, and check bundle versions in **Help → About Eclipse IDE → Installation Details**. The source archive supplied for this README contains the `wodel.updatesite/site.xml` definition but does not itself include the generated feature/plugin JARs under `wodel.updatesite/features/` and `wodel.updatesite/plugins/`.

### Model conversion or generated mutants are not found

Check that the correct runtime workspace, `MutationTestingApp` project (where expected), model input and output locations, registered EMF packages, and selected post-processor are available. A generated Wodel mutant is not automatically an executable FORTE application.

## Citing

If you use Wodel4diac or Wodel in academic work, cite the corresponding Wodel paper and any applicable Wodel4diac publication:

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

Please add a Wodel4diac-specific citation when a publication is available; the supplied README does not identify a final, citable Wodel4diac publication.

## Acknowledgements

Wodel4diac builds upon **[Wodel](https://github.com/gomezabajo/Wodel)** and includes source projects associated with **[anATLyzer](https://github.com/anatlyzer/anatlyzer)**, **[eFinder](https://github.com/jesusc/efinder)**, and **[tinytools](https://github.com/jesusc/tiny-tools)**. The research has been developed in the context of work at the **Universidad Autónoma de Madrid** and the **Universidad de Murcia**.

## License

The repository includes the **Eclipse Public License 2.0 (EPL-2.0)**. See [`LICENSE`](LICENSE). Bundled third-party software may have separate notices or licensing requirements; check those components before redistribution.

## Contact

Maintained by **Pablo Gómez-Abajo** — <Pablo.GomezA@uam.es> · [@gomezabajo](https://github.com/gomezabajo). Bug reports and contributions are welcome through the [issue tracker](https://github.com/gomezabajo/Wodel4diac/issues).
