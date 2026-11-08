# City Search & Spell-Check

Dynamic programming and string search in Java:

- Edit Distance in three versions: recursive, full table, two rows (`algorithms.EditDistance`)
- String search: Naive and KMP with a public `KmpSearch.lps` (`algorithms.StringSearch`)
- Counters inside every algorithm, a seeded input generator and an experiment runner
- JUnit 5 tests, `results/results.csv`, five plots and `REPORT.md`

## Requirements

- JDK 17 or newer
- Maven 3.8 or newer
- Python 3 with `matplotlib` (only for redrawing the plots)

## Build and test

```
mvn clean package
```

This compiles the code and runs all JUnit 5 tests. Only the tests:

```
mvn test
```

## Run the experiment

```
mvn -q compile
java -cp target/classes experiment.Experiment
```

The run takes well under a minute and writes `results/results.csv` (one row per algorithm, input and size;
columns: `family,algorithm,input,n,m,runs,time_ms,operations,memory_cells,result,seed`).
A different output path can be given as the first argument.
The generator uses a fixed seed (`Generator.BASE_SEED`), so the counters and results are identical on every run.
Times differ between machines; each time is the median of 5 runs.

## Draw the plots

```
python3 scripts/plots.py
```

Reads `results/results.csv` and writes five PNG files to `results/plots/`.

## Layout

```
src/main/java/algorithms    EditDistance versions, NaiveSearch, KmpSearch
src/main/java/experiment    Generator, Experiment
src/test/java               JUnit 5 tests
scripts/plots.py            plots from results.csv
results/results.csv         all runs of the experiment
results/plots/              PNG charts
REPORT.md                   design card, plots, prediction vs result, short answers
```

## What the counters mean

- Edit Distance `operations`: recursive calls (recursive version) or table cells filled (table and two rows).
- Edit Distance `memory_cells`: number of int cells allocated by the version.
- Search `operations`: comparisons of one text character with one pattern character
  (for KMP this includes building the LPS array).

