# NOTES

> Fill this in as you go; it is part of the deliverables.

## How to run & demo

<!-- Exact commands: how to ingest, how to decide claims, how to run tests. -->

## Design decisions

<!-- The 3-5 decisions that shaped your solution, and why. -->

1. Ingestion will throw NullPointer if contract xml can't be parsed if fields Type change due to outstanding correction. If data can't be reliably parsed it's no use and the app should just fail.

## Data findings

<!-- What surprised you in the XML exports and how you handled each case. -->
1. Contract: CANCEL eventhough it ran 36 months
```
2026-07-27 02:14:09.000	ELV-100455	KAU-004521	3188	Tobias Krüger	301	Möbelhaus Schäfer KG	TRK55901	Trek	FX+ 2	2749.00	2024-06-01	2027-05-31	36	4.20	CANCEL?? (eventhough it ran 36 months)
```

## AI usage

<!-- Which tools, for what. At least one place where you rejected or reworked
     the output, and why. -->

## Cut / next steps

<!-- What you consciously skipped and what you'd do with another day. -->

## Time spent

<!-- Honest number. Nobody is graded on this line. -->
