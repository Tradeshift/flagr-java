# Flagr Java

An easy to use client for the [Flagr](https://checkr.github.io/flagr) feature flag service.

## Usage

````java
flagr = new Flagr(FLAGR_HOST);
context = new EvaluationContext("color_flag");
try {
    EvaluationResponse response = flagr.evaluate(
        new EvaluationContext("color_flag")
    );
    System.out.println(response.getVariantKey()); // example output: "red"
} catch (FlagrException e) {
    e.printStackTrace();
}
````

## Versioning

We use [SemVer](http://semver.org/) for versioning.