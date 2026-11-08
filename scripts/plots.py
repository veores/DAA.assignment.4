import csv
import os
import sys
from collections import defaultdict

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.ticker import NullFormatter

SRC = sys.argv[1] if len(sys.argv) > 1 else "results/results.csv"
OUT = sys.argv[2] if len(sys.argv) > 2 else "results/plots"
os.makedirs(OUT, exist_ok=True)

rows = []
with open(SRC, newline="") as f:
    for r in csv.DictReader(f):
        r["n"] = int(r["n"])
        r["m"] = int(r["m"])
        r["time_ms"] = float(r["time_ms"])
        r["operations"] = int(r["operations"])
        r["memory_cells"] = int(r["memory_cells"])
        rows.append(r)


def series(family, algorithm, inp, x="n"):
    pts = sorted((r[x], r) for r in rows
                 if r["family"] == family and r["algorithm"] == algorithm and r["input"] == inp)
    return [p[0] for p in pts], [p[1] for p in pts]


def finish(fig, name, takeaway):
    for a in fig.axes:
        a.xaxis.set_minor_formatter(NullFormatter())
    fig.text(0.5, 0.01, takeaway, ha="center", va="bottom", fontsize=9, style="italic", wrap=True)
    fig.tight_layout(rect=(0, 0.06, 1, 1))
    fig.savefig(os.path.join(OUT, name), dpi=150)
    plt.close(fig)


fig, ax = plt.subplots(figsize=(7.5, 5))
for inp, style in (("disjoint", "o-"), ("random", "s-"), ("identical", "^-")):
    xs, rs = series("edit_small", "recursive", inp)
    ax.plot(xs, [r["operations"] for r in rs], style, label="recursive, " + inp)
xs, rs = series("edit_small", "table", "random")
ax.plot(xs, [r["operations"] for r in rs], "k--", label="table (cells filled)")
ax.set_yscale("log")
ax.set_xticks([3, 6, 9, 12])
ax.set_xlabel("n (length of each string, characters)")
ax.set_ylabel("operations (recursive calls or table cells), log scale")
ax.set_title("Edit Distance: recursive calls vs table cells")
ax.grid(True, which="both", alpha=0.3)
ax.legend()
finish(fig, "plot1_recursive_calls.png",
       "Recursive calls on disjoint strings grow about 5.6x per extra character; the table grows only as n*n.")

fig, ax = plt.subplots(figsize=(7.5, 5))
colors = {"table": "tab:red", "two_rows": "tab:blue"}
styles = {"random": "-", "identical": ":", "disjoint": "--"}
for alg in ("table", "two_rows"):
    for inp in ("random", "identical", "disjoint"):
        xs, rs = series("edit_large", alg, inp)
        ax.plot(xs, [r["time_ms"] for r in rs], styles[inp], marker="o", color=colors[alg],
                label=alg + ", " + inp)
xs, rs = series("edit_large", "two_rows", "random")
base = rs[2]["time_ms"]
ax.plot(xs, [base * (x / xs[2]) ** 2 for x in xs], color="gray", linestyle="-.", label="reference: time ~ n^2")
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xticks(xs)
ax.set_xticklabels([str(x) for x in xs])
ax.set_xlabel("n (length of each string, characters), log scale")
ax.set_ylabel("median time of 5 runs (ms), log scale")
ax.set_title("Edit Distance: full table vs two rows, time")
ax.grid(True, which="both", alpha=0.3)
ax.legend(fontsize=8)
finish(fig, "plot2_dp_time.png",
       "Both versions follow the n^2 line; only at n = 4000 does the table become slower (84 ms against 54 ms on random).")

fig, ax = plt.subplots(figsize=(7.5, 5))
for alg in ("table", "two_rows"):
    xs, rs = series("edit_large", alg, "random")
    ax.plot(xs, [r["memory_cells"] for r in rs], "o-", color=colors[alg], label=alg)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xticks(xs)
ax.set_xticklabels([str(x) for x in xs])
ax.set_xlabel("n (length of each string, characters), log scale")
ax.set_ylabel("memory allocated (array cells = ints), log scale")
ax.set_title("Edit Distance: memory of table vs two rows")
ax.grid(True, which="both", alpha=0.3)
ax.legend()
finish(fig, "plot3_memory.png",
       "The table needs about n*n cells and the two rows only 2n: at n = 4000 that is 16 million against 8 thousand.")

fig, axes = plt.subplots(1, 2, figsize=(11, 5), sharey=True)
inputs = ("random26", "random_binary", "worst_aab", "all_match")
for ax, alg in zip(axes, ("naive", "kmp")):
    for inp in inputs:
        xs, rs = series("search_text", alg, inp)
        ax.plot(xs, [r["operations"] / r["n"] for r in rs], "o-", label=inp)
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n (text length, characters), log scale")
    ax.set_title(alg + " (pattern length m = 100)")
    ax.grid(True, which="both", alpha=0.3)
    ax.legend(fontsize=8)
axes[0].set_ylabel("character comparisons per text character, log scale")
fig.suptitle("String search: comparisons per text character")
finish(fig, "plot4_search_ratio.png",
       "Naive makes about 100 comparisons per character on worst_aab and all_match (same line, = m); KMP never goes above 2.")

fig, ax = plt.subplots(figsize=(7.5, 5))
for alg, inp, style in (("naive", "worst_aab", "o-"), ("kmp", "worst_aab", "s-"),
                        ("naive", "random26", "o--"), ("kmp", "random26", "s--")):
    xs, rs = series("search_pattern", alg, inp, x="m")
    ax.plot(xs, [r["time_ms"] for r in rs], style, label=alg + ", " + inp)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xticks(xs)
ax.set_xticklabels([str(x) for x in xs])
ax.set_xlabel("m (pattern length, characters), log scale")
ax.set_ylabel("median time of 5 runs (ms), log scale")
ax.set_title("String search: time vs pattern length (text n = 200 000)")
ax.grid(True, which="both", alpha=0.3)
ax.legend()
finish(fig, "plot5_pattern_length.png",
       "Naive on worst_aab gets slower in proportion to m, while KMP and both random26 lines stay almost flat.")
print("plots written to", OUT)
