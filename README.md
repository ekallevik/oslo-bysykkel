// docs:
// - parse, don't validate

# Oslo Bysykler

## Run
The easiest way to run the project is by using the Ktor-plugin in IntelliJ. 
If not, you could use Gradle or Maven directly, as described [here](https://ktor.io/docs/running.html#package)

## 
- I follow the prinicple of "Parse, don't validate"
- GBFS is accepted as is, and then converting to the domain language. 
  - StationID is returned from Oslo Bysykkel as a String and therefore used as a String
  - Docs online are outdated. Some ints are now bools

## Improvements
- Add proper logging, and not using print-statements
- Add caching to reduce load on Oslo Bysykkel and improve performance
- Add a docker image
