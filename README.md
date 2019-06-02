# NurtureCloud Backend Challenge

## Scenario

As a real estate tech company, a common use case that our buyers have is wanting to search for properties in and around particular suburbs.  
We start with a list of suburbs and locations and use algorithms to calculate which suburbs should appear in their search.

Your task is to create a search mechanism that efficiently calculates nearby suburbs using provided locations and presents a list of options to a user.

## Requirements

You are provided with a file of Australian suburbs with their locations: ```src/main/resources/aus_suburbs.json```

~~~
...
{
  "Pcode": 2000,
  "Locality": "SYDNEY",
  "State": "NSW",
  "Comments": "",
  "Category": "Delivery Area",
  "Longitude": 151.2099,
  "Latitude": -33.8697
}
...
~~~

Use the suburb location data to calculate nearby suburbs from the list.

Some reading you may find useful on this subject: https://en.wikipedia.org/wiki/Haversine_formula

Requirements:

* The user can search with a Postcode and Locality and promptly get a response.
* The pair of Postcode and Locality is unique, but each field on its own is not.
* Categorise and organise nearby suburbs to close (within 10km) and fringe (within 50km).
* Maximum 15 suburbs returned for each search.
* Should start up and be ready for search quickly.
* Provide an interactive command line interface. Make the input case-insensitive.

~~~
> Please enter a suburb name: Sydney
> Please enter the postcode: 2000

Nearby Suburbs:
    BARANGAROO 2000
    ULTIMO 2000
    THE ROCKS 2000
    ...

Fringe Suburbs
    PYRMONT 2001 
    MOORE PARK 2021
    BALMAIN 2041
    ...

> Please enter a suburb name:
~~~

## What you will be assessed upon

* Reads the provided input and writes correct output.
* Is readable, understandable, and structured in a reasonable way.
* Efficient test coverage and style.
* Is efficient in terms of CPU time and memory usage.
* What trade-offs for memory vs CPU you make to give the optimum solution for the user.
* Handling of edge cases and input validation.
* Choice of libraries.

## Extra points will be given for

* Creative (but readable and reasonable) mechanisms to reduce CPU time and repeat calculations.

## Constraints & Guide

* Please answer in either Java (8) or Kotlin
* This challenge should take an hour or so.

## Submission

* Zip up your file.
* Submit it to https://tinyurl.com/y4x7lm7k
